package net.cheney.rev.reactor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncServerChannel;

public final class BindMessage extends Message<AsyncServerChannel> {

	private final InetSocketAddress addr;
	private Reactor sender;

	public BindMessage(Reactor sender, InetSocketAddress addr) {
		this.sender = sender;
		this.addr = addr;
	}

	public SocketAddress addr() {
		return addr;
	}
	
	@Override
	public Reactor sender() {
		return sender;
	}

	@Override
	public void accept(AsyncServerChannel visitor) {
		visitor.receive(this);	
	}

}
