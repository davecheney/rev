package net.cheney.rev.util;

import java.util.concurrent.ExecutorService;

public abstract class Worker implements Runnable {

	protected final void schedule() {
		executor().execute(this);
	}

	protected abstract ExecutorService executor();
}
