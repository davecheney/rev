package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Actor;
import net.cheney.rev.reactor.ChannelClosedMessage;
import net.cheney.rev.reactor.ChannelRegistrationCompleteMessage;

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
	
	public final void receive(@Nonnull ChannelRegistrationCompleteMessage<T> msg) {
		msg.sender().send(new EnableInterestMessage(this, channel(), SelectionKey.OP_ACCEPT));
	}
	
	public final void receive(@Nonnull ChannelClosedMessage msg) {
		close();
	}

}
