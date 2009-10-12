package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.reactor.Reactor;

public class EnableInterestMessage extends UpdateInterestMessage {

	public EnableInterestMessage(AsyncChannel<?> sender, SelectableChannel channel, int ops) {
		super(sender, channel, ops);
	}

	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}

}
