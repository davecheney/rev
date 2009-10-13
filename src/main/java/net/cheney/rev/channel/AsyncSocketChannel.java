package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import net.cheney.rev.reactor.EnableInterestMessage;


public final class AsyncSocketChannel extends AsyncByteChannel<AsyncSocketChannel> {

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
	void receive(ChannelRegistrationCompleteMessage<AsyncSocketChannel> msg) {
		msg.sender().send(new EnableInterestMessage(this, channel(), SelectionKey.OP_CONNECT));
	}

}
