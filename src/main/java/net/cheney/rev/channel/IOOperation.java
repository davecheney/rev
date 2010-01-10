package net.cheney.rev.channel;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

public abstract class IOOperation extends Operation {

	public abstract void accept(@Nonnull AsyncSocketChannel channel);
	
	public abstract void accept(@Nonnull AsyncServerChannel channel);
	
	protected void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException ignored) {
		}
	}

}