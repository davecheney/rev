package net.cheney.rev.channel;

import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Deque;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	private Deque<ReadRequest> readRequests = new LinkedList<ReadRequest>(); 
	private Deque<WriteRequest> writeRequests = new LinkedList<WriteRequest>();
	
	public AsyncByteChannel(Reactor reactor) {
		super(reactor);
	}


	public static abstract class IORequest extends AsyncChannel.IORequest {
		
		public abstract void accept(AsyncByteChannel<?> channel);
		
		public abstract void completed();
		
		public abstract void failed(Throwable t);
		
	}
	
	public static abstract class ReadRequest extends AsyncByteChannel.IORequest {
		
		@Override
		public void accept(AsyncByteChannel<?> channel) {
			channel.receive(this);
		}
	}
	
	public static abstract class WriteRequest extends AsyncByteChannel.IORequest {
		@Override
		public void accept(AsyncByteChannel<?> channel) {
			channel.receive(this);
		}
	}

	
	public void send(@Nonnull AsyncByteChannel.IORequest msg) {
		deliver(msg);
	}


	public void receive(WriteRequest writeRequest) {
		writeRequests.addLast(writeRequest);
		enableInterest(SelectionKey.OP_WRITE);
	}


	public void receive(ReadRequest readRequest) {
		readRequests.addLast(readRequest);
		enableInterest(SelectionKey.OP_READ);
	}

}
