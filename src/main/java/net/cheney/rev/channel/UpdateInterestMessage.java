package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class UpdateInterestMessage extends Message<Reactor> {

	private final AsyncChannel<?> sender;
	private final SelectableChannel channel;
	private final int ops;

	public UpdateInterestMessage(AsyncChannel<?> sender, SelectableChannel channel, int ops) {
		this.sender = sender;
		this.channel = channel;
		this.ops = ops;
	}
	
	public SelectableChannel channel() {
		return channel;
	}
	
	public int ops() {
		return ops;
	}
	
	@Override
	public AsyncChannel<?> sender() {
		return sender;
	}
}
