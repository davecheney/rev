package net.cheney.rev.actor;

public class Ack extends Message<Client> {

	private final Server sender;

	public Ack(Server sender) {
		this.sender = sender;
	}
	
	@Override
	public void accept(Client visitor) {
		visitor.receive(this);
	}

	@Override
	public Server sender() {
		return sender;
	}
	
}