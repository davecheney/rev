package net.cheney.rev.actor;

import org.junit.Test;

public class MessageTest {

	public class Ping extends Actor<Pong> {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class Pong extends Actor<Ping> {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Test
	public void messageTest() {
		
	}
}
