package net.cheney.rev.channel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public final class ChannelClosedMessage extends Message<Reactor, AsyncChannel> {

	public ChannelClosedMessage(Reactor sender) {
		super(sender);
	}
	
	@Override
	public void accept(AsyncChannel visitor) {
		visitor.receive(this);
	}

}
