package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import net.cheney.rev.actor.Message;

public class AsyncSocketChannel extends AsyncByteChannel<AsyncSocketChannel> {

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
	public void run() {
		for (Message<AsyncSocketChannel> m = pollMailbox(); m != null; m = pollMailbox()) {
			m.accept(this);
		}
	}

}
