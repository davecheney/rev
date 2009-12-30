package net.cheney.rev.channel;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

final class AsyncSocketChannelThreadFactory implements
		ThreadFactory {
	AtomicInteger count = new AtomicInteger();

	@Override
	public Thread newThread(@Nonnull Runnable r) {
		Thread t = new Thread(r);
		t.setName(String.format("AsyncSocketChannel-%d", count
				.getAndIncrement()));
		t.setDaemon(true);
		return t;
	}
}