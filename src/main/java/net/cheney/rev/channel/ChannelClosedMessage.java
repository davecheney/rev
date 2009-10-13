package net.cheney.rev.channel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public final class ChannelClosedMessage<T extends AsyncChannel<T>> extends Message<Reactor, T> {

	public ChannelClosedMessage(Reactor sender) {
		super(sender);
	}
	
	@Override
	public void accept(T visitor) {
		visitor.receive(this);
	}

}
