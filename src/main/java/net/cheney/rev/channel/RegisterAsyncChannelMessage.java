package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public class RegisterAsyncChannelMessage extends Message<AsyncChannel, Reactor> {

	protected RegisterAsyncChannelMessage(AsyncChannel sender) {
		super(sender);
	}

	public SelectableChannel channel() {
		return sender().channel();
	}

	@Override
	public void accept(Reactor receiver) {
		receiver.receive(this);
	}

}
