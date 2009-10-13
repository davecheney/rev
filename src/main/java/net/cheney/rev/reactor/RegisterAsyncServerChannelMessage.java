package net.cheney.rev.reactor;

import net.cheney.rev.channel.AsyncServerChannel;
import net.cheney.rev.channel.RegisterAsyncChannelMessage;

public class RegisterAsyncServerChannelMessage extends RegisterAsyncChannelMessage<AsyncServerChannel> {

	public RegisterAsyncServerChannelMessage(AsyncServerChannel sender) {
		super(sender);
	}

	@Override
	public void accept(Reactor receiver) {
		receiver.receive(this);
	}
}
