package net.cheney.rev.actor;

import javax.annotation.Nonnull;

public abstract class Message<RECEIVER> {

	public abstract Object sender();

	public abstract void accept(@Nonnull RECEIVER visitor);
}
