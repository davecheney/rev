package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public class AsyncSocketChannel extends AsyncChannel implements Runnable, Closeable {
	
	private final Deque<AsyncChannel.IORequest> mailbox = new LinkedBlockingDeque<AsyncChannel.IORequest>();
	
	private Deque<ReadRequest> readRequests = new LinkedList<ReadRequest>(); 
	private Deque<WriteRequest> writeRequests = new LinkedList<WriteRequest>();

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, new ThreadFactory() {
		
		AtomicInteger count = new AtomicInteger();
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(String.format("AsyncSocketChannel-%d", count.getAndIncrement()));
			t.setDaemon(true);
			return t;
		}
	});  
	
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
	public SocketChannel channel() {
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
		switch(msg.readyOps()) {
		case SelectionKey.OP_READ:
			doRead(); return;
			
		case SelectionKey.OP_WRITE:
			doWrite(); return;
			
		case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
			doRead(); doWrite() ; return;
			
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void close() throws IOException {
		sc.close();
	}
	
	static abstract class IORequest extends AsyncChannel.IORequest {
		
		public abstract void accept(AsyncSocketChannel channel);
		
		public abstract void completed();
		
		public void failed(Throwable t) {
			t.printStackTrace();
		}
		
		@Override
		public void accept(AsyncServerChannel channel) {
			throw new IllegalArgumentException();
		}
		
	}
	
	public static abstract class ReadRequest extends IORequest {
		
//		@Override
		public void accept(AsyncSocketChannel channel) {
			channel.receive(this);
		}
		
		public abstract boolean readFrom(ReadableByteChannel channel) throws IOException;
	}
	
	public static abstract class WriteRequest extends IORequest {
		
//		@Override
		public void accept(AsyncSocketChannel channel) {
			channel.receive(this);
		}
		
		public abstract boolean writeTo(WritableByteChannel channel) throws IOException;
	}

	
	void doRead() {
		ReadRequest request = null;
		try {
			for(Iterator<ReadRequest> i = readRequests.iterator() ; i.hasNext() ; ) {
				request = i.next();
				if(request.readFrom(channel())) {
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
			for(Iterator<WriteRequest> i = writeRequests.iterator() ; i.hasNext() ; ) {
				request = i.next();
				if(request.writeTo(channel())) {
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

	public void send(@Nonnull AsyncSocketChannel.IORequest msg) {
		deliver(msg);
	}

	void disableReadInterest() {
		disableInterest(SelectionKey.OP_READ);
	}
	
	void disableWriteInterest() {
		disableInterest(SelectionKey.OP_WRITE);
	}

	void receive(WriteRequest writeRequest) {
		writeRequests.addLast(writeRequest);
		enableInterest(SelectionKey.OP_WRITE);
	}


	void receive(ReadRequest readRequest) {
		readRequests.addLast(readRequest);
		enableInterest(SelectionKey.OP_READ);
	}

}
