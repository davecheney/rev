package net.cheney.rev.reactor;

import javax.annotation.Nonnull;

public abstract class DisableInterestRequest extends UpdateInterestRequest {

	@Override
	public void accept(@Nonnull Reactor reactor) {
		reactor.receive(this);
	}
}