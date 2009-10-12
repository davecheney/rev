package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class RegisterChannelMessage<T extends AsyncChannel<T>> extends Message<Reactor> {

	private final T sender;

	public RegisterChannelMessage(T sender) {
		this.sender = sender;
	}

	public abstract SelectableChannel channel();
	
	@Override
	public T sender() {
		return sender;
	}

}
