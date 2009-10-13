package net.cheney.rev.reactor;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncChannel;

public final class ChannelRegistrationCompleteMessage<RECEIVER extends AsyncChannel<RECEIVER>> extends Message<Reactor, RECEIVER> {

	public ChannelRegistrationCompleteMessage(Reactor sender) {
		super(sender);
	}

	@Override
	public void accept(RECEIVER visitor) {
		visitor.receive(this);
	}

}
