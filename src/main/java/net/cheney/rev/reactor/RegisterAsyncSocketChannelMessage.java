package net.cheney.rev.reactor;

import net.cheney.rev.channel.AsyncSocketChannel;
import net.cheney.rev.channel.RegisterAsyncChannelMessage;

public class RegisterAsyncSocketChannelMessage extends RegisterAsyncChannelMessage<AsyncSocketChannel> {

	public RegisterAsyncSocketChannelMessage(AsyncSocketChannel sender) {
		super(sender);
	}

	@Override
	public void accept(Reactor receiver) {
		receiver.receive(this);
	}
}
