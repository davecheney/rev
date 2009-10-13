package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Actor;

public abstract class AsyncChannel<T extends AsyncChannel<T>> extends Actor<T> implements Closeable {

	protected abstract SelectableChannel channel();
	
	@Override
	public final void close() {
		try {
			this.channel().close();
		} catch (IOException e) {
			// ignore
		}
	}
	
	final void receive(@Nonnull ChannelClosedMessage<T> msg) {
		close();
	}

	abstract void receive(@Nonnull ChannelRegistrationCompleteMessage<T> channelRegistrationCompleteMessage);
	
}
