package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class UpdateInterestMessage extends Message<AsyncChannel<?>, Reactor> {

	private final SelectableChannel channel;
	private final int ops;

	public UpdateInterestMessage(AsyncChannel<?> sender, SelectableChannel channel, int ops) {
		super(sender);
		this.channel = channel;
		this.ops = ops;
	}
	
	public final SelectableChannel channel() {
		return channel;
	}
	
	public final int ops() {
		return ops;
	}
	
}
