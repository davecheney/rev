package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class RegisterChannelMessage<T extends AsyncChannel<T>> extends Message<T, Reactor> {

	public RegisterChannelMessage(T sender) {
		super(sender);
	}

	public abstract SelectableChannel channel();
	
}
