package net.cheney.rev.actor;

public class Ping extends Message<Client, Server> {

	public Ping(Client sender) {
		super(sender);
	}

	@Override
	public void accept(Server visitor) {
		visitor.receive(this);
	}

}