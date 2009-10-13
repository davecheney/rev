package net.cheney.rev.actor;

import java.util.concurrent.CountDownLatch;

public class Client extends Actor<Client> {

	private CountDownLatch latch = new CountDownLatch(1);
	
	public CountDownLatch ping(Server server) {
		server.send(new Ping(this));
		return latch;
	}

	public void receive(Ack ack) {
		latch.countDown();
	}

}