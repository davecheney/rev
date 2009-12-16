package net.cheney.rev.protocol;

import net.cheney.rev.channel.AsyncSocketChannel;

public abstract class ServerProtocolFactory extends ProtocolFactory {

	public abstract void onAccept(AsyncSocketChannel channel);

}
