package net.cheney.rev.channel;

import javax.annotation.Nonnull;

public abstract class A {

	public void completed() {
		// yay
	}

	public void failed(@Nonnull Throwable t) {
		t.printStackTrace();
	}
}
