package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnull;

import net.cheney.rev.reactor.Reactor;
import net.cheney.rev.reactor.Reactor.ReadyOpsNotification;

public abstract class AsyncChannel<T extends SelectableChannel> {
	
	private final Reactor reactor;

	public AsyncChannel(Reactor reactor) {
		this.reactor = reactor;
	}
	
	public static abstract class IORequest {

		public abstract void accept(AsyncChannel<?> channel);

		public abstract void accept(Reactor reactor);

	}
	
	public void send(@Nonnull AsyncChannel.IORequest msg) {
		deliver(msg);
	}
	
	abstract T channel();
	
	abstract void deliver(AsyncChannel.IORequest msg);

	public abstract void receive(ReadyOpsNotification msg);
	
	void enableInterest(final int ops) {
		reactor.send(reactor.new EnableInterestRequest() {
			
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
		reactor.send(reactor.new DisableInterestRequest() {
			
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
			public AsyncChannel<?> sender() {
				return AsyncChannel.this;
			}
			
			@Override
			public void failed(Throwable t) {
				// fuk
			}
			
			@Override
			public void completed() {
				// yay
			}
			
			@Override
			public SelectableChannel channel() {
				return sender().channel();
			}
		});
	}

}
