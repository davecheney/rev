package net.cheney.rev.channel;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import javax.annotation.Nonnull;

import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.reactor.BindMessage;

public final class AsyncServerChannel extends AsyncChannel<AsyncServerChannel> {

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

	public void receive(@Nonnull BindMessage msg) {
		try {
			ServerSocket socket = channel().socket();
			socket.bind(msg.addr());
			msg.sender().send(new RegisterAsyncServerChannelMessage(this, channel()));
		} catch (IOException e) {
			factory.send(new UnableToBindMessage(this));
		}
	}
}
