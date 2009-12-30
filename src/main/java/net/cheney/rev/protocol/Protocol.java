package net.cheney.rev.protocol;

import java.io.IOException;

import javax.annotation.Nonnull;

import net.cheney.rev.channel.AsyncSocketChannel;

public abstract class Protocol {

	private final AsyncSocketChannel channel;
	
	protected Protocol(@Nonnull AsyncSocketChannel channel) {
		this.channel = channel;
	}
	
	protected final AsyncSocketChannel channel() {
		return channel;
	}
	
	/**
	 * Called by the transport once the underlying transport is connected. 
	 * @throws IOException 
	 *
	 */
	public abstract void onConnect() throws IOException;

	/**
	 * Called by the transport when the underlying transport is disconnected
	 * @throws IOException 
	 *
	 */
	public abstract void onDisconnect() throws IOException;
}
