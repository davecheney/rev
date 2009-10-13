package net.cheney.rev.reactor;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.channel.AsyncChannel;
import net.cheney.rev.channel.UpdateInterestMessage;

public final class DisableInterestMessage extends UpdateInterestMessage {

	public DisableInterestMessage(AsyncChannel<?> sender, SelectableChannel channel, int ops) {
		super(sender, channel, ops);
	}

	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}

}
