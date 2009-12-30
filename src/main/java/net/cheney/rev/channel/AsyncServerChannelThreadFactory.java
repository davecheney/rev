package net.cheney.rev.channel;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class AsyncServerChannelThreadFactory implements
		ThreadFactory {
	AtomicInteger count = new AtomicInteger();

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(String.format("AsyncServerChannel-%d", count.getAndIncrement()));
		t.setDaemon(true);
		return t;
	}
}