package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	private Deque<ReadRequest> readRequests = new LinkedList<ReadRequest>(); 
	private Deque<WriteRequest> writeRequests = new LinkedList<WriteRequest>();
	
	public AsyncByteChannel(Reactor reactor) {
		super(reactor);
	}

	static abstract class IORequest extends AsyncChannel.IORequest {
		
		public abstract void accept(AsyncByteChannel<?> channel);
		
		public abstract void completed();
		
		public abstract void failed(Throwable t);
		
	}
	
	public static abstract class ReadRequest extends AsyncByteChannel.IORequest {
		
		@Override
		public void accept(AsyncByteChannel<?> channel) {
			channel.receive(this);
		}
		
		public abstract boolean readFrom(ReadableByteChannel channel) throws IOException;
	}
	
	public static abstract class WriteRequest extends AsyncByteChannel.IORequest {
		@Override
		public void accept(AsyncByteChannel<?> channel) {
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
					return;
				}
			}
			disableReadInterest();
		} catch (IOException e) {
			request.failed(e);
		}
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
					return;
				}
			}
			disableWriteInterest();
		} catch (IOException e) {
			request.failed(e);
		}
	}
	
	public void send(@Nonnull AsyncByteChannel.IORequest msg) {
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
