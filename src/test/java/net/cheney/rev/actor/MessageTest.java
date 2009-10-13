package net.cheney.rev.actor;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class MessageTest {

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
	
	
	@Test()
	public void messageTest() throws InterruptedException {
		Client client = new Client();
		Server server = new Server();
		
		client.ping(server).await();
		
	}
}
