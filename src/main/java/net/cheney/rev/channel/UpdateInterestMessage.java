package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class UpdateInterestMessage extends Message<AsyncChannel<?>, Reactor> {

	private final int ops;

	public UpdateInterestMessage(AsyncChannel<?> sender, int ops) {
		super(sender);
		this.ops = ops;
	}
	
	public final SelectableChannel channel() {
		return this.sender().channel();
	}
	
	public final int ops() {
		return ops;
	}
	
}
