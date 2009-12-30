package net.cheney.rev.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Scheduler<T> {

	private final BlockingQueue<T> tasks = new LinkedBlockingDeque<T>();
	private final AtomicInteger workerCount = new AtomicInteger();
	private final int maxSize;
	
	public Scheduler(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void execute(T task) {
		increaseWorkerCountIfNeeded();
		tasks.offer(task);
	}
	
	private void increaseWorkerCountIfNeeded() {
		int currentWorkerCount = workerCount.get();
		if(currentWorkerCount < maxSize) {
			workerCount.incrementAndGet();
			createWorkerThread(new Worker()).start();
		}
	}


	protected Thread createWorkerThread(Runnable worker) {
		Thread t = new Thread(worker);
		t.setDaemon(false);
		return t;
	}


	class Worker implements Runnable {

		@Override
		public void run() {
			for(;;) {
				try {
					T task = tasks.poll(10, TimeUnit.SECONDS);
					if(task != null) 
						accept(task);
					else 
						return;
				} catch (InterruptedException e) { }
			}
		}
	}
		
	
	public abstract void accept(T task);
}
