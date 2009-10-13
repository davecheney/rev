package net.cheney.rev.actor;

public class Server extends Actor<Server> {

	@Override
	public void run() {
		for(Message<Server> m = pollMailbox() ; m != null ; m = pollMailbox()) {
			m.accept(this);
		}
	}

	public void receive(Ping ping) {
		ping.sender().send(new Ack(this));
	}
	
}