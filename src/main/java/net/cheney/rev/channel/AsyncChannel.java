package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

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
	
	abstract void deliver(IOOperation msg);

	abstract void receive(ReadyOpsNotification msg);
	
	void enableInterest(final int ops) {
		
		final class EnableInterestRequest extends net.cheney.rev.reactor.EnableInterestRequest {
			
			@Override
			public AsyncChannel sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public int interestOps() {
				return ops;
			}

		}
		reactor.send(new EnableInterestRequest());
	}
	
	void disableInterest(final int ops) {
		
		final class DisableInterestRequest extends net.cheney.rev.reactor.DisableInterestRequest {
			@Override
			public AsyncChannel sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public int interestOps() {
				return ops;
			}
		}
		
		reactor.send(new DisableInterestRequest());
	}

}
