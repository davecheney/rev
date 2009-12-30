package net.cheney.rev.reactor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class ReactorThreadFactory implements ThreadFactory {
	AtomicInteger count = new AtomicInteger();

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(String.format("Reactor-%d", count.getAndIncrement()));
		t.setDaemon(false);
		return t;
	}
}