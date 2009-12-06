package net.cheney.rev.channel;

import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	public static abstract class IORequest extends AsyncChannel.IORequest {
		
		public abstract void accept(AsyncByteChannel<?> channel);
		
	}
	
	public void send(@Nonnull AsyncByteChannel.IORequest msg) {
		deliver(msg);
	}

}
