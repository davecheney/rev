package net.cheney.rev.channel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public class ChannelRegistrationCompleteMessage extends Message<Reactor, AsyncChannel> {

	public ChannelRegistrationCompleteMessage(Reactor sender) {
		super(sender);
	}

	@Override
	public void accept(AsyncChannel receiver) {
		receiver.receive(this);
	}

}
