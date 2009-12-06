package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

public abstract class AsyncChannel<T extends SelectableChannel> {

	public static abstract class IORequest {

		public abstract void accept(AsyncChannel<?> channel);
		
	}
	
	abstract T channel();
	
	abstract void deliver(AsyncChannel.IORequest msg);

}
