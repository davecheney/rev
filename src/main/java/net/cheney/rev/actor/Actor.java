package net.cheney.rev.actor;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.Nonnull;

public abstract class Actor<RECEIVER> implements Runnable {

	private final ExecutorService executor = Executors.newFixedThreadPool(10);
	
	private final Queue<Message<?, RECEIVER>> mailbox = new LinkedBlockingDeque<Message<?, RECEIVER>>();

	public final void send(@Nonnull Message<?, RECEIVER> message) {
		mailbox.offer(message);
		schedule();
	}
	
	@Nonnull
	protected final Message<?, RECEIVER> pollMailbox() {
		return mailbox.poll();
	}

	private void schedule() {
		executor.execute(this);
	}
	
	@SuppressWarnings("unchecked")
	public final void run() {
		for(Message<?, RECEIVER> m = pollMailbox() ; m != null ; m = pollMailbox()) {
			m.accept((RECEIVER)this);
		}
	}
	
}
