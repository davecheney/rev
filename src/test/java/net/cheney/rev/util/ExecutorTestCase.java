package net.cheney.rev.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class ExecutorTestCase {

	@Test
	public void testExecutor() throws InterruptedException {
		Executor executor = new Executor(1);
		final CountDownLatch latch = new CountDownLatch(5);
		Runnable r = new Runnable() { 
			@Override
			public void run() {
				latch.countDown();
			}
		};
		
		for(int i = 0 ; i < 5 ; ++i) 
			executor.execute(r);
		
		Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
	}
}
