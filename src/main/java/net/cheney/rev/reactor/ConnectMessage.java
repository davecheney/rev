package net.cheney.rev.reactor;

import java.net.SocketAddress;

import net.cheney.rev.actor.Message;
import net.cheney.rev.channel.AsyncSocketChannel;

public class ConnectMessage extends Message<Reactor, AsyncSocketChannel> {

	private final SocketAddress addr;

	public ConnectMessage(Reactor sender, SocketAddress addr) {
		super(sender);
		this.addr = addr;
	}

	@Override
	public void accept(AsyncSocketChannel receiver) {
		receiver.recieve(this);
	}

	public SocketAddress addr() {
		return this.addr;
	}

	
}
