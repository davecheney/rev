package net.cheney.rev.reactor;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncChannel;

public final class ChannelRegistrationCompleteMessage<RECEIVER extends AsyncChannel<RECEIVER>> extends Message<RECEIVER> {

	private final Reactor sender;

	public ChannelRegistrationCompleteMessage(Reactor sender) {
		this.sender = sender;
	}

	@Override
	public Reactor sender() {
		return this.sender;
	}
	
	@Override
	public void accept(RECEIVER visitor) {
		visitor.receive(this);
	}

}
