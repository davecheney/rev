package net.cheney.rev.channel;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Message;
import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.reactor.Reactor;

public final class AsyncServerChannel extends AsyncChannel {

	private final ServerProtocolFactory factory;
	private final ServerSocketChannel channel;

	public AsyncServerChannel(ServerProtocolFactory factory) throws IOException {
		this.channel = SelectorProvider.provider().openServerSocketChannel();
		this.channel.configureBlocking(false);
		this.factory = factory;
	}
	
	public ServerProtocolFactory factory() {
		return factory;
	}
	
	@Override
	protected ServerSocketChannel channel() {
		return channel;
	}

	void receive(@Nonnull BindMessage msg) {
		try {
			ServerSocket socket = channel().socket();
			socket.bind(msg.addr());
			msg.sender().send(new RegisterAsyncChannelMessage(this));
		} catch (IOException e) {
			factory.send(new UnableToBindMessage(this));
		}
	}

	@Override
	void receive(@Nonnull ChannelRegistrationCompleteMessage msg) {
		msg.sender().send(enableAcceptInterest());
	}

	private Message<?, Reactor> enableAcceptInterest() {
		return enableInterest(SelectionKey.OP_ACCEPT);
	}

}
