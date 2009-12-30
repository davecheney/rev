package net.cheney.rev.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Executor extends Scheduler<Runnable> implements java.util.concurrent.Executor {

	private static final AtomicInteger executorCount = new AtomicInteger(-1);
	private final AtomicInteger workerCount = new AtomicInteger();
	
	public Executor(int maxSize) {
		super(maxSize);
		executorCount.incrementAndGet();
	}

	@Override
	public void accept(Runnable task) {
		task.run();
	}
	
	@Override
	protected Thread createWorkerThread(Runnable worker) {
		Thread t = new Thread(worker, String.format("Executor-%d-%d", executorCount.intValue(), workerCount.incrementAndGet()));
		t.setDaemon(false);
		return t;
	}
	
}
