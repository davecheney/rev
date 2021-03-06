package net.cheney.rev.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.reactor.ChannelRegistrationRequest;
import net.cheney.rev.reactor.Reactor;

public class AsyncServerChannel extends AsyncChannel implements Runnable {
	
	private final Queue<IOOperation> mailbox = new ConcurrentLinkedQueue<IOOperation>();

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, new AsyncServerChannelThreadFactory()); 

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
	
	@Override
	protected final ExecutorService executor() {
		return EXECUTOR;
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
	void deliver(IOOperation msg) {
		mailbox.add(msg);
		schedule();
	}

	@Override
	public void run() {
		for(IOOperation msg = mailbox.poll() ; msg != null ; msg = mailbox.poll()) {
			msg.accept(this);
		}
	}

	@Override
	public void receive(@Nonnull ReadyOpsNotification msg) {
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
				reactor.send(new ChannelRegistrationRequest() {
					
					@Override
					public AsyncSocketChannel sender() {
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
