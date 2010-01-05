package net.cheney.rev.reactor;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.channel.Operation;
import net.cheney.rev.channel.AsyncChannel;

public abstract class IOOperation extends Operation {

	public abstract void accept(Reactor reactor);
	
	public abstract int interestOps();
	
	public SelectableChannel channel() {
		return sender().channel();
	}
	
	public abstract AsyncChannel sender();

}