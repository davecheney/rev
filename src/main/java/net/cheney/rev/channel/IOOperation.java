package net.cheney.rev.channel;

import javax.annotation.Nonnull;

public abstract class IOOperation extends Operation {

	public abstract void accept(@Nonnull AsyncSocketChannel channel);
	
	public abstract void accept(@Nonnull AsyncServerChannel channel);

}