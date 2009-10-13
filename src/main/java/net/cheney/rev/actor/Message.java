package net.cheney.rev.actor;

import javax.annotation.Nonnull;

public abstract class Message<SENDER, RECEIVER> {

	private final SENDER sender;

	public Message(SENDER sender) {
		this.sender = sender;
	}
	
	public final SENDER sender() {
		return sender;
	}

	public abstract void accept(@Nonnull RECEIVER receiver);
}
