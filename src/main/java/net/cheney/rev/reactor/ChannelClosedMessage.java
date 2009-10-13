package net.cheney.rev.reactor;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncChannel;

public final class ChannelClosedMessage<T extends AsyncChannel<?>> extends Message<Reactor, T> {

	public ChannelClosedMessage(Reactor sender) {
		super(sender);
	}
	
	@Override
	public void accept(T visitor) {
		visitor.receive(this);
	}

}
