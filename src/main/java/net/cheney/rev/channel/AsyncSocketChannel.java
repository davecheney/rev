package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public class AsyncSocketChannel extends AsyncChannel implements Runnable, Closeable {

	private final Queue<AsyncIORequest> mailbox = new ConcurrentLinkedQueue<AsyncIORequest>();

	private Queue<ReadRequest> readRequests = new LinkedList<ReadRequest>();
	private Queue<WriteRequest> writeRequests = new LinkedList<WriteRequest>();

	private Lock guard = new ReentrantLock();

	private static final ExecutorService EXECUTOR = Executors
			.newFixedThreadPool(4, new AsyncSocketChannelThreadFactory());

	private final SocketChannel sc;

	public AsyncSocketChannel(@Nonnull Reactor reactor) throws IOException {
		super(reactor);
		this.sc = configureSocketChannel(createSocketChannel());
	}

	public AsyncSocketChannel(@Nonnull Reactor reactor, SocketChannel sc) throws IOException {
		super(reactor);
		this.sc = configureSocketChannel(sc);
	}

	private static SocketChannel configureSocketChannel(@Nonnull SocketChannel sc) throws IOException {
		sc.configureBlocking(false);
		return sc;
	}

	private static SocketChannel createSocketChannel() throws IOException {
		return SelectorProvider.provider().openSocketChannel();
	}

	@Override
	public SocketChannel channel() {
		return sc;
	}

	@Override
	void deliver(@Nonnull AsyncIORequest msg) {
		mailbox.add(msg);
		schedule();
	}

	@Override
	public void run() {
		if (guard.tryLock()) {
			try {
				for (AsyncIORequest msg = mailbox.poll(); msg != null; msg = mailbox.poll()) {
					msg.accept(this);
				}
			} finally {
				guard.unlock();
			}
		} else {
			System.out.println("Spurious wakeup");
		}

	}
	
	@Override
	protected final ExecutorService executor() {
		return EXECUTOR;
	}

	@Override
	public void receive(@Nonnull ReadyOpsNotification msg) {
		switch (msg.readyOps()) {
		case SelectionKey.OP_READ:
			doRead();
			return;

		case SelectionKey.OP_WRITE:
			doWrite();
			return;

		case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
			doRead();
			doWrite();
			return;

		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void close() throws IOException {
		sc.close();
	}

	public static abstract class ReadRequest extends net.cheney.rev.channel.IORequest<SocketChannel> {

		// @Override
		public void accept(@Nonnull AsyncSocketChannel channel) {
			channel.receive(this);
		}

	}
		
	public static abstract class WriteRequest extends net.cheney.rev.channel.IORequest<SocketChannel> {

		// @Override
		public void accept(@Nonnull AsyncSocketChannel channel) {
			channel.receive(this);
		}

	}

	void doRead() {
		ReadRequest request = null;
		try {
			for (Iterator<ReadRequest> i = readRequests.iterator(); i.hasNext();) {
				request = i.next();
				if (request.accept(channel())) {
					i.remove();
					request.completed();
				} else {
					enableReadInterest();
					return;
				}
			}
		} catch (IOException e) {
			request.failed(e);
		}
	}

	void enableReadInterest() {
		enableInterest(SelectionKey.OP_READ);
	}

	void doWrite() {
		WriteRequest request = null;
		try {
			for (Iterator<WriteRequest> i = writeRequests.iterator(); i.hasNext();) {
				request = i.next();
				if (request.accept(channel())) {
					i.remove();
					request.completed();
				} else {
					enableWriteInterest();
					return;
				}
			}
		} catch (IOException e) {
			request.failed(e);
		}
	}

	void enableWriteInterest() {
		enableInterest(SelectionKey.OP_WRITE);
	}

	public void send(@Nonnull net.cheney.rev.channel.IORequest<?> msg) {
		deliver(msg);
	}

	void disableReadInterest() {
		disableInterest(SelectionKey.OP_READ);
	}

	void disableWriteInterest() {
		disableInterest(SelectionKey.OP_WRITE);
	}

	void receive(@Nonnull WriteRequest writeRequest) {
		writeRequests.add(writeRequest);
		enableInterest(SelectionKey.OP_WRITE);
	}

	void receive(@Nonnull ReadRequest readRequest) {
		readRequests.add(readRequest);
		enableInterest(SelectionKey.OP_READ);
	}

	public void shutdownOutput() {
		try {
			channel().socket().shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
