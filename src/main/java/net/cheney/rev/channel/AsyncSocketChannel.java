package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import net.cheney.rev.reactor.Reactor;
import net.cheney.rev.reactor.Reactor.ReadyOpsNotification;

public class AsyncSocketChannel extends AsyncByteChannel<SocketChannel> implements Runnable {
	
	private final Deque<AsyncChannel.IORequest> mailbox = new LinkedBlockingDeque<AsyncChannel.IORequest>();

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4); 
	
	private final SocketChannel sc;

	public AsyncSocketChannel(Reactor reactor) throws IOException {
		super(reactor);
		this.sc = configureSocketChannel(createSocketChannel());
	}
	
	public AsyncSocketChannel(Reactor reactor, SocketChannel sc) throws IOException {
		super(reactor);
		this.sc = configureSocketChannel(sc);
	}

	private static SocketChannel configureSocketChannel(SocketChannel sc) throws IOException {
		sc.configureBlocking(false);
		return sc;
	}

	private static SocketChannel createSocketChannel() throws IOException {
		return SelectorProvider.provider().openSocketChannel();
	}

	@Override
	SocketChannel channel() {
		return sc;
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
		// TODO Auto-generated method stub
		
	}
	
}
