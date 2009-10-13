package net.cheney.rev.actor;

public class Server extends Actor<Server> {

	public void receive(Ping ping) {
		ping.sender().send(new Ack(this));
	}
	
}