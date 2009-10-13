package net.cheney.rev.actor;

import org.junit.Test;

public class MessageTest {

	@Test(timeout=1000L)
	public void messageTest() throws InterruptedException {
		Client client = new Client();
		Server server = new Server();
		
		client.ping(server).await();
		
	}
	
}
