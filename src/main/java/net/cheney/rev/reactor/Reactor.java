package net.cheney.rev.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import net.cheney.rev.channel.AsyncChannel;
import net.cheney.rev.channel.AsyncServerChannel;
import net.cheney.rev.protocol.ServerProtocolFactory;

public class Reactor implements Runnable {

	private final Deque<Reactor.IORequest> mailbox = new LinkedBlockingDeque<Reactor.IORequest>();

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
		
		AtomicInteger count = new AtomicInteger();
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(String.format("Reactor-%d", count.getAndIncrement()));
			t.setDaemon(false);
			return t;
		}
	});

	private Selector selector;

	public abstract static class IORequest {

		public abstract void accept(Reactor reactor);
		
		public void completed() {
			// yay
		}

		public void failed(Throwable t) {
			t.printStackTrace();
		}

	}

	public abstract static class ChannelRegistrationRequest extends Reactor.IORequest {

		@Override
		public void accept(Reactor reactor) {
			reactor.receive(this);
		}

		public SelectableChannel channel() {
			return sender().channel();
		}

		public abstract AsyncChannel sender();
		
		public abstract int interestOps();

	}

	abstract static class UpdateInterestRequest extends Reactor.IORequest {

		public abstract int ops();

		public abstract SelectableChannel channel();

	}

	public static abstract class EnableInterestRequest extends UpdateInterestRequest {

		@Override
		public void accept(Reactor reactor) {
			reactor.receive(this);
		}

	}

	public static abstract class DisableInterestRequest extends UpdateInterestRequest {

		@Override
		public void accept(Reactor reactor) {
			reactor.receive(this);
		}
	}

	private Reactor() throws IOException {
		this.selector = Selector.open();
	}

	public static Reactor open() throws IOException {
		Reactor r = new Reactor();
		r.schedule();
		return r;
	}

	void receive(ChannelRegistrationRequest msg) {
		try {
			msg.channel().register(selector, msg.interestOps(), msg.sender());
			msg.completed();
		} catch (IOException e) {
			msg.failed(e);
		}
	}

	void receive(EnableInterestRequest msg) {
		enableInterest(msg.channel(), msg.ops());
	}

	private void enableInterest(SelectableChannel channel, int ops) {
		enableInterest(channel.keyFor(selector), ops);
	}

	private void enableInterest(SelectionKey sk, int ops) {
		sk.interestOps(sk.interestOps() | ops);
	}

	void receive(DisableInterestRequest msg) {
		disableInterest(msg.channel(), msg.ops());
	}

	private void disableInterest(SelectableChannel channel, int ops) {
		disableInterest(channel.keyFor(selector), ops);
	}

	private void disableInterest(SelectionKey sk, int ops) {
		sk.interestOps(sk.interestOps() & ~ops);
	}

	@Override
	public void run() {
		try {
			for (Reactor.IORequest msg = mailbox.pollFirst(); msg != null; msg = mailbox.pollFirst()) {
				msg.accept(this);
			}
			doSelect();
			schedule();
		} catch (IOException e) {
			// log and close
		}
	}

	private void doSelect() throws IOException {
		Set<SelectionKey> keys = selectNow();
		for (SelectionKey key : keys) {
			handleSelectionKey(key);
		}
		// indicate that all keys have been processed
		keys.clear();
	}

	private void handleSelectionKey(SelectionKey key) {
		final int ops = key.readyOps();
		disableInterest(key, ops);
		channelFromKey(key).send(new AsyncChannel.ReadyOpsNotification() {
			
			@Override
			public int readyOps() {
				return ops;
			}
		});
	}

	private static AsyncChannel channelFromKey(SelectionKey key) {
		return (AsyncChannel) key.attachment();
	}

	private Set<SelectionKey> selectNow() throws IOException {
		selector.select();
		return selector.selectedKeys();
	}

	private void schedule() {
		EXECUTOR.execute(this);
	}

	private void wakeup() {
		selector.wakeup();
	}

	public void send(UpdateInterestRequest msg) {
		deliver(msg);
	}

	public void send(ChannelRegistrationRequest msg) {
		deliver(msg);
	}
	
	void deliver(Reactor.IORequest msg) {
		mailbox.addLast(msg);
		wakeup();
	}

	public void listen(SocketAddress addr, ServerProtocolFactory factory) throws IOException {
		final AsyncServerChannel channel = new AsyncServerChannel(this, addr, factory);
		send(new ChannelRegistrationRequest() {

			@Override
			public AsyncChannel sender() {
				return channel;
			}

			@Override
			public int interestOps() {
				return SelectionKey.OP_ACCEPT;
			}
		});
	}
}
