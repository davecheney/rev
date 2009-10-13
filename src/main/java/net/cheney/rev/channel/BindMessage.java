package net.cheney.rev.channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.cheney.rev.actor.Message;
import net.cheney.rev.reactor.Reactor;

public final class BindMessage extends Message<Reactor, AsyncServerChannel> {

	private final InetSocketAddress addr;

	public BindMessage(Reactor sender, InetSocketAddress addr) {
		super(sender);
		this.addr = addr;
	}

	public SocketAddress addr() {
		return addr;
	}
	
	@Override
	public void accept(AsyncServerChannel visitor) {
		visitor.receive(this);	
	}

}
