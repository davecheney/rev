package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

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
	void receive(@Nonnull ChannelRegistrationCompleteMessage<AsyncSocketChannel> msg) {
		msg.sender().send(enableConnectInterest());
	}

	private Message<?, Reactor> enableConnectInterest() {
		return enableInterest(SelectionKey.OP_CONNECT);
	}

}
