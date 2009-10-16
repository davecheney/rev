package net.cheney.rev.channel;

import java.net.SocketAddress;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public final class BindMessage extends Message<Reactor, AsyncChannel> {

	private final SocketAddress addr;

	public BindMessage(Reactor sender, SocketAddress addr) {
		super(sender);
		this.addr = addr;
	}

	public SocketAddress addr() {
		return addr;
	}
	
	@Override
	public void accept(AsyncChannel visitor) {
		visitor.receive(this);	
	}

}
