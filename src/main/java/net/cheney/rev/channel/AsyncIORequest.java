package net.cheney.rev.channel;

import javax.annotation.Nonnull;

public abstract class AsyncIORequest extends Operation {

	public abstract void accept(@Nonnull AsyncSocketChannel channel);
	
	public abstract void accept(@Nonnull AsyncServerChannel channel);

}