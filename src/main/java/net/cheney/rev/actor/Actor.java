package net.cheney.rev.actor;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.Nonnull;

public abstract class Actor<RECEIVER> implements Runnable {

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
	
	private final Queue<Message<?, RECEIVER>> mailbox = new LinkedBlockingDeque<Message<?, RECEIVER>>();

	/**
	 * Deliver a message to the {@link Actor}s mailbox
	 * @param message The message to be delivered to this Actors mailbox
	 */
	public final void send(@Nonnull Message<?, RECEIVER> message) {
		mailbox.offer(message);
		schedule();
	}
	
	private void schedule() {
		EXECUTOR.execute(this);
	}
	
	@SuppressWarnings("unchecked")
	public final void run() {
		for(Message<?, RECEIVER> m = mailbox.poll() ; m != null ; m = mailbox.poll()) {
			// cast required because in the context of this compilation unit, this is 
			// the class Actor, not the subtype being executed.
			m.accept((RECEIVER)this);
		}
	}
	
}
