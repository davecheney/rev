package net.cheney.rev.channel;

import java.nio.channels.SocketChannel;

import net.cheney.rev.reactor.Reactor;

public final class RegisterAsyncSocketChannelMessage extends RegisterChannelMessage<AsyncSocketChannel> {

	private SocketChannel channel;

	public RegisterAsyncSocketChannelMessage(AsyncSocketChannel sender, SocketChannel channel) {
		super(sender);
		this.channel = channel;
	}

	@Override
	public SocketChannel channel() {
		return channel;
	}
		
	@Override
	public void accept(Reactor visitor) {
		visitor.receive(this);
	}
}
