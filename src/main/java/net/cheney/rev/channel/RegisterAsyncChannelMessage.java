package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public abstract class RegisterAsyncChannelMessage<T extends AsyncChannel<T>> extends Message<AsyncChannel<T>, Reactor> {

	public RegisterAsyncChannelMessage(T sender) {
		super(sender);
	}

	public SelectableChannel channel() {
		return sender().channel();
	}

	@Override
	public abstract void accept(Reactor receiver);

}
