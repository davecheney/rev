package net.cheney.rev.protocol;

import javax.annotation.Nonnull;

import net.cheney.rev.channel.AsyncSocketChannel;

public abstract class ServerProtocolFactory extends ProtocolFactory {

	public abstract void onAccept(@Nonnull AsyncSocketChannel channel);

}
