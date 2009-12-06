package net.cheney.rev.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import net.cheney.rev.channel.AsyncChannel;

public class Reactor implements Runnable {
	
	private final Deque<AsyncChannel.IORequest> mailbox = new LinkedBlockingDeque<AsyncChannel.IORequest>();
	
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(); 

	private Selector selector;
	
	public static class ReadyOpsNotification extends AsyncChannel.IORequest {

		private final int readyOps;

		public ReadyOpsNotification(int readyOps) {
			this.readyOps = readyOps;
		}

		@Override
		public void accept(AsyncChannel<?> channel) {
			channel.receive(this);
		}

		@Override
		public void accept(Reactor reactor) {
			throw new IllegalStateException();
		}

	}

	public Reactor() throws IOException {
		this.selector = Selector.open();
	}

	@Override
	public void run() {
		try {
			for(AsyncChannel.IORequest msg = mailbox.pollFirst() ; msg != null ; msg = mailbox.pollFirst()) {
				msg.accept(this);
			}
			doSelect();
			schedule();
		} catch(IOException e) {
			// log and close
		}
	}
	
	private void doSelect() throws IOException {
		for(SelectionKey key : selectNow()) {
			handleSelectionKey(key);
		}
	}

	private void handleSelectionKey(SelectionKey key) {
		channelFromKey(key).send(new ReadyOpsNotification(key.readyOps()));
	}

	private static AsyncChannel<?> channelFromKey(SelectionKey key) {
		return (AsyncChannel<?>) key.attachment();
	}

	private Set<SelectionKey> selectNow() throws IOException {
		return selector.selectNow() > 0 ? selector.selectedKeys() : emptySet(); 
	}

	private Set<SelectionKey> emptySet() {
		return Collections.emptySet();
	}

	private void schedule() {
		EXECUTOR.execute(this);
	}
	
	private void wakeup() {
		selector.wakeup();
	}
}
