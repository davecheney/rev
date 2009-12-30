package net.cheney.rev.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import net.cheney.rev.channel.AsyncChannel;
import net.cheney.rev.channel.AsyncServerChannel;
import net.cheney.rev.channel.ReadyOpsNotification;
import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.util.Worker;

public class Reactor extends Worker {

	private final Queue<IOOperation> mailbox = new ConcurrentLinkedQueue<IOOperation>();

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ReactorThreadFactory());

	private Selector selector;

	private Reactor() throws IOException {
		this.selector = Selector.open();
	}

	public static Reactor open() throws IOException {
		Reactor r = new Reactor();
		r.schedule();
		return r;
	}

	void receive(@Nonnull ChannelRegistrationRequest msg) {
		try {
			msg.channel().register(selector, msg.interestOps(), msg.sender());
			msg.completed();
		} catch (IOException e) {
			msg.failed(e);
		}
	}

	void receive(@Nonnull EnableInterestRequest msg) {
		enableInterest(msg.channel(), msg.interestOps());
	}

	private void enableInterest(@Nonnull SelectableChannel channel, int ops) {
		enableInterest(channel.keyFor(selector), ops);
	}

	private void enableInterest(@Nonnull SelectionKey sk, int ops) {
		sk.interestOps(sk.interestOps() | ops);
	}

	void receive(@Nonnull DisableInterestRequest msg) {
		disableInterest(msg.channel(), msg.interestOps());
	}

	private void disableInterest(@Nonnull SelectableChannel channel, int ops) {
		disableInterest(channel.keyFor(selector), ops);
	}

	private void disableInterest(@Nonnull SelectionKey sk, int ops) {
		sk.interestOps(sk.interestOps() & ~ops);
	}

	@Override
	public void run() {
		try {
			for (IOOperation msg = mailbox.poll(); msg != null; msg = mailbox.poll()) {
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

	private void handleSelectionKey(@Nonnull SelectionKey key) {
		final int ops = key.readyOps();
		disableInterest(key, ops);
		channelFromKey(key).send(new ReadyOpsNotification() {
			
			@Override
			public int readyOps() {
				return ops;
			}
		});
	}

	private static AsyncChannel channelFromKey(@Nonnull SelectionKey key) {
		return (AsyncChannel) key.attachment();
	}

	private Set<SelectionKey> selectNow() throws IOException {
		selector.select();
		return selector.selectedKeys();
	}

	private void wakeup() {
		selector.wakeup();
	}
	
	@Override
	protected final ExecutorService executor() {
		return EXECUTOR;
	}

	public void send(@Nonnull UpdateInterestRequest msg) {
		deliver(msg);
	}

	public void send(@Nonnull ChannelRegistrationRequest msg) {
		deliver(msg);
	}
	
	void deliver(IOOperation msg) {
		mailbox.add(msg);
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
