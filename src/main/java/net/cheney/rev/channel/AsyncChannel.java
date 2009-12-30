package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.DisableInterestRequest;
import net.cheney.rev.reactor.EnableInterestRequest;
import net.cheney.rev.reactor.Reactor;
import net.cheney.rev.util.Worker;

public abstract class AsyncChannel extends Worker {
	
	protected final Reactor reactor;

	public AsyncChannel(Reactor reactor) {
		this.reactor = reactor;
	}
	
	public void send(@Nonnull ReadyOpsNotification msg) {
		deliver(msg);
	}
	
	public abstract SelectableChannel channel();
	
	abstract void deliver(AsyncIORequest msg);

	abstract void receive(ReadyOpsNotification msg);
	
	void enableInterest(final int ops) {
		reactor.send(new EnableInterestRequest() {
			
			@Override
			public AsyncChannel sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public int interestOps() {
				return ops;
			}

		});
	}
	
	void disableInterest(final int ops) {
		reactor.send(new DisableInterestRequest() {
			
			@Override
			public AsyncChannel sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public int interestOps() {
				return ops;
			}
		});
	}

}
