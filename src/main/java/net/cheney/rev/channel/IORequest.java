package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnull;

public abstract class IORequest<T extends SocketChannel> extends AsyncIORequest {

	public abstract void accept(@Nonnull AsyncSocketChannel channel);

	@Override
	public void accept(@Nonnull AsyncServerChannel channel) {
		throw new IllegalArgumentException();
	}
	
	public abstract boolean accept(@Nonnull T channel)
	throws IOException;

}