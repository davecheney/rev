package net.cheney.rev.actor;

public class Ack extends Message<Server, Client> {

	public Ack(Server sender) {
		super(sender);
	}

	@Override
	public void accept(Client visitor) {
		visitor.receive(this);
	}

}