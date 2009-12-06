package net.cheney.rev.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import net.cheney.rev.reactor.Reactor;
import net.cheney.rev.reactor.Reactor.ReadyOpsNotification;

public class AsyncServerChannel extends AsyncChannel<ServerSocketChannel> implements Runnable {
	
	private final Deque<AsyncChannel.IORequest> mailbox = new LinkedBlockingDeque<AsyncChannel.IORequest>();

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4); 

	private final ServerSocketChannel ssc;

	public AsyncServerChannel(Reactor reactor, InetSocketAddress addr) throws IOException {
		super(reactor);
		this.ssc = configureServerSocketChannel(createServerSocketChannel());
		ssc.socket().bind(addr);
	}
	
	private static ServerSocketChannel configureServerSocketChannel(ServerSocketChannel sc) throws IOException {
		sc.configureBlocking(false);
		return sc;
	}

	private static ServerSocketChannel createServerSocketChannel() throws IOException {
		return SelectorProvider.provider().openServerSocketChannel();
	}

	@Override
	public
	ServerSocketChannel channel() {
		return ssc;
	}

	@Override
	void deliver(AsyncChannel.IORequest msg) {
		mailbox.addLast(msg);
		schedule();
	}

	private void schedule() {
		EXECUTOR.execute(this);
	}
	
	@Override
	public void run() {
		for(AsyncChannel.IORequest msg = mailbox.pollFirst() ; msg != null ; msg = mailbox.pollFirst()) {
			msg.accept(this);
		}
	}

	@Override
	public void receive(ReadyOpsNotification msg) {
		switch(msg.readyOps()) {
		case SelectionKey.OP_ACCEPT:
			doAccept();
			break;
			
		default:
			throw new IllegalArgumentException();
		}
	}

	private void doAccept() {
		try {
			SocketChannel sc = ssc.accept();
			if(sc != null) {
				final AsyncSocketChannel channel = new AsyncSocketChannel(this.reactor, sc);
				reactor.send(new Reactor.ChannelRegistrationRequest() {
					
					@Override
					public AsyncChannel<?> sender() {
						return channel;
					}
					
					@Override
					public SelectableChannel channel() {
						return channel.channel();
					}
				});
			}
		} catch (IOException e) {
			// oh poo
		}
	}
	
}
