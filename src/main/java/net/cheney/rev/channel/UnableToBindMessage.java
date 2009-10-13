package net.cheney.rev.channel;

import net.cheney.rev.actor.Message;
import net.cheney.rev.protocol.ServerProtocolFactory;

public class UnableToBindMessage extends Message<AsyncServerChannel, ServerProtocolFactory> {

	public UnableToBindMessage(AsyncServerChannel sender) {
		super(sender);
	}

	@Override
	public void accept(ServerProtocolFactory receiver) {
		receiver.receive(this);
	}

}
