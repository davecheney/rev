package net.cheney.rev.actor;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Actor<RECEIVER> implements Runnable {

	private final ExecutorService executor = Executors.newFixedThreadPool(10);
	
	private final Queue<Message<RECEIVER>> mailbox = new LinkedBlockingDeque<Message<RECEIVER>>();

	public void send(Message<RECEIVER> message) {
		mailbox.offer(message);
		schedule();
	}
	
	protected Message<RECEIVER> pollMailbox() {
		return mailbox.poll();
	}

	private void schedule() {
		executor.execute(this);
	}
	
}
