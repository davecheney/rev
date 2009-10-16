package net.cheney.rev.channel;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.ConnectMessage;
import net.cheney.rev.reactor.Reactor;

public final class AsyncSocketChannel extends AsyncByteChannel {

	private final SocketChannel channel;

	public AsyncSocketChannel() throws IOException {
		this.channel = SelectorProvider.provider().openSocketChannel();
		this.channel.configureBlocking(false);
	}

	@Override
	protected SocketChannel channel() {
		return this.channel;
	}

	@Override
	void receive(@Nonnull ChannelRegistrationCompleteMessage msg) {
		msg.sender().send(enableConnectInterest());
	}

	private Message<?, Reactor> enableConnectInterest() {
		return enableInterest(SelectionKey.OP_CONNECT);
	}

	public void recieve(ConnectMessage msg) {
		try {
			Socket socket = channel().socket();
			socket.connect(msg.addr());
			msg.sender().send(new RegisterAsyncChannelMessage(this));
		} catch (IOException e) {
//			factory.send(new UnableToBindMessage(this));
		}
	}

	@Override
	void receive(BindMessage msg) {
		throw new IllegalStateException();
	}

}
