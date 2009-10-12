package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Actor;
import net.cheney.rev.reactor.ChannelRegistrationCompleteMessage;

public abstract class AsyncChannel<T extends AsyncChannel<T>> extends Actor<T> implements Closeable {

	protected abstract SelectableChannel channel();
	
	@Override
	public void close() throws IOException {
		this.channel().close();
	}
	
	public void receive(@Nonnull ChannelRegistrationCompleteMessage<T> msg) {
		msg.sender().send(new EnableInterestMessage(this, channel(), SelectionKey.OP_ACCEPT));
	}

}
