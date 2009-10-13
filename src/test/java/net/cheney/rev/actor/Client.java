package net.cheney.rev.actor;

import java.util.concurrent.CountDownLatch;

public class Client extends Actor<Client> {

	private CountDownLatch latch = new CountDownLatch(1);
	
	@Override
	public void run() {
		for(Message<Client> m = pollMailbox() ; m != null ; m = pollMailbox()) {
			m.accept(this);
		}
	}
	
	public CountDownLatch ping(Server server) {
		server.send(new Ping(this));
		return latch;
	}

	public void receive(Ack ack) {
		latch.countDown();
	}

}