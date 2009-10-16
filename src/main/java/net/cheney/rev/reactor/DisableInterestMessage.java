package net.cheney.rev.reactor;

import net.cheney.rev.channel.AsyncChannel;
import net.cheney.rev.channel.UpdateInterestMessage;

public final class DisableInterestMessage extends UpdateInterestMessage {

	public DisableInterestMessage(AsyncChannel sender, int ops) {
		super(sender, ops);
	}

	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}

}
