package net.cheney.rev.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.reactor.Reactor;

public class AsyncServerChannel extends AsyncChannel implements Runnable {
	
	private final Deque<AsyncChannel.IORequest> mailbox = new LinkedBlockingDeque<AsyncChannel.IORequest>();

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4); 

	private final ServerSocketChannel ssc;

	private final ServerProtocolFactory factory;

	public AsyncServerChannel(Reactor reactor, SocketAddress addr, ServerProtocolFactory factory) throws IOException {
		super(reactor);
		this.factory = factory;
		this.ssc = configureServerSocketChannel(createServerSocketChannel());
		ssc.socket().bind(addr);
	}
	
	protected ServerProtocolFactory factory() {
		return factory;
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
		for(AsyncChannel.IORequest msg = mailbox.poll() ; msg != null ; msg = mailbox.poll()) {
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
					public AsyncChannel sender() {
						return channel;
					}
					
					@Override
					public void completed() {
						// warning, calls protocol factory, which calls protocol in the context of the reactor, not the AsyncServerChannel or AsyncSocketChannel
						factory().onAccept(channel);
					}
					
					@Override
					public int interestOps() {
						return SelectionKey.OP_READ;
					}
				});
			}
			enableAcceptInterest();
		} catch (IOException e) {
			// oh poo
		}
	}

	private void enableAcceptInterest() {
		enableInterest(SelectionKey.OP_ACCEPT);
	}
}
