package net.cheney.rev.reactor;

import javax.annotation.Nonnull;

public abstract class EnableInterestRequest extends UpdateInterestRequest {

	@Override
	public void accept(@Nonnull Reactor reactor) {
		reactor.receive(this);
	}

}