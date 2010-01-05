package net.cheney.rev.channel;

public abstract class ReadyOpsNotification extends IOOperation {

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