package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;

public abstract class AsyncChannel {
	
	protected final Reactor reactor;

	public AsyncChannel(Reactor reactor) {
		this.reactor = reactor;
	}
	
	public static abstract class IORequest {

		public abstract void accept(AsyncSocketChannel channel);
		
		public abstract void accept(AsyncServerChannel channel);

	}
	
	public abstract static class ReadyOpsNotification extends AsyncChannel.IORequest {

		@Override
		public void accept(AsyncServerChannel channel) {
			channel.receive(this);
		}

		@Override
		public void accept(AsyncSocketChannel channel) {
			channel.receive(this);
		}

		public abstract int readyOps();

	}
	
	public void send(@Nonnull ReadyOpsNotification msg) {
		deliver(msg);
	}
	
	public abstract SelectableChannel channel();
	
	abstract void deliver(AsyncChannel.IORequest msg);

	abstract void receive(ReadyOpsNotification msg);
	
	void enableInterest(final int ops) {
		reactor.send(new Reactor.EnableInterestRequest() {
			
			@Override
			public SelectableChannel channel() {
				return AsyncChannel.this.channel();
			}
			
			@Override
			public int ops() {
				return ops;
			}

		});
	}
	
	void disableInterest(final int ops) {
		reactor.send(new Reactor.DisableInterestRequest() {
			
			@Override
			public SelectableChannel channel() {
				return AsyncChannel.this.channel();
			}
			
			@Override
			public int ops() {
				return ops;
			}
		});
	}
	
	void registerChannel() {
		reactor.send(new Reactor.ChannelRegistrationRequest() {
			
			@Override
			public AsyncChannel sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public int interestOps() {
				return 0;
			}
		});
	}

}
