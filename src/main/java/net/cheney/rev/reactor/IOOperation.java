package net.cheney.rev.reactor;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.channel.A;
import net.cheney.rev.channel.AsyncChannel;

public abstract class IOOperation extends A {

	public abstract void accept(Reactor reactor);
	
	public abstract int interestOps();
	
	public SelectableChannel channel() {
		return sender().channel();
	}
	
	public abstract AsyncChannel sender();

}