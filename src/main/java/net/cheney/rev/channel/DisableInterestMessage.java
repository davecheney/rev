package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.reactor.Reactor;

public class DisableInterestMessage extends UpdateInterestMessage {

	public DisableInterestMessage(AsyncChannel<?> sender, SelectableChannel channel, int ops) {
		super(sender, channel, ops);
	}

	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}

}
