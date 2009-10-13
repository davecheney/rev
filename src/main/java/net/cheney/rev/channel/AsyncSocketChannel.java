package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

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

}
