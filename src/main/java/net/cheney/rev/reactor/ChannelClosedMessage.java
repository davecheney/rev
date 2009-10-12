package net.cheney.rev.reactor;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncChannel;

public final class ChannelClosedMessage<T extends AsyncChannel<?>> extends Message<T> {

	private final Reactor sender;

	public ChannelClosedMessage(Reactor sender) {
		this.sender = sender;
	}
	
	@Override
	public void accept(T visitor) {
		visitor.receive(this);
	}

	@Override
	public Reactor sender() {
		return sender;
	}

}
