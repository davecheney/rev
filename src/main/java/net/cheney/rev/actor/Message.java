package net.cheney.rev.actor;

import java.io.IOException;

public abstract class Message<RECEIVER> {

	public abstract Object sender();

	public abstract void accept(RECEIVER visitor) throws IOException;
}
