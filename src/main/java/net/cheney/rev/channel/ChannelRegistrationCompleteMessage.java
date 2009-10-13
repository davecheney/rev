package net.cheney.rev.channel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public class ChannelRegistrationCompleteMessage<T extends AsyncChannel<T>> extends Message<Reactor, T> {

	public ChannelRegistrationCompleteMessage(Reactor sender) {
		super(sender);
	}

	@Override
	public void accept(T receiver) {
		receiver.receive(this);
	}

}
