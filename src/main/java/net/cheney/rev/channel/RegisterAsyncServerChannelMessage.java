package net.cheney.rev.channel;

import java.nio.channels.ServerSocketChannel;

import net.cheney.rev.reactor.Reactor;

public class RegisterAsyncServerChannelMessage extends RegisterChannelMessage<AsyncServerChannel> {

	private final ServerSocketChannel channel;

	public RegisterAsyncServerChannelMessage(AsyncServerChannel sender, ServerSocketChannel channel) {
		super(sender);
		this.channel = channel;
	}

	@Override
	public ServerSocketChannel channel() {
		return channel;
	}
	
	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}


}
