package net.cheney.rev.actor;

public class Ping extends Message<Server> {

	private final Client sender;

	public Ping(Client sender) {
		this.sender = sender;
	}
	
	@Override
	public void accept(Server visitor) {
		visitor.receive(this);
	}

	@Override
	public Client sender() {
		return sender;
	}
	
}