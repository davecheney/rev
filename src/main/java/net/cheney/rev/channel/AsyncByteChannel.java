package net.cheney.rev.channel;

import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	public AsyncByteChannel(Reactor reactor) {
		super(reactor);
	}


	public static abstract class IORequest extends AsyncChannel.IORequest {
		
		public abstract void accept(AsyncByteChannel<?> channel);
		
		public abstract void completed();
		
		public abstract void failed(Throwable t);
		
	}
	
	public static abstract class ReadRequest extends AsyncByteChannel.IORequest {
		
	}
	
	public static abstract class WriteRequest extends AsyncByteChannel.IORequest {
		
	}

	
	public void send(@Nonnull AsyncByteChannel.IORequest msg) {
		deliver(msg);
	}

}
