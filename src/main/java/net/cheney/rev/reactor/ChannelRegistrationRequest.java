package net.cheney.rev.reactor;

public abstract class ChannelRegistrationRequest extends IOOperation {

	@Override
	public void accept(Reactor reactor) {
		reactor.receive(this);
	}

}