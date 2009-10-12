package net.cheney.rev.actor;

import org.junit.Test;

public class MessageTest {

	public class Ping extends Actor<Pong> {

		@Override
		public void run() {
			for(Message<Pong> m = pollMailbox() ; m != null ; m = pollMailbox()) {
				m.accept(this);
			}
		}

		public void receive(PongMessage pongMessage) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class PingMessage extends Message<Pong> {

		private final Ping sender;

		public PingMessage(Ping sender) {
			this.sender = sender;
		}
		
		@Override
		public void accept(Pong visitor) {
			visitor.receive(this);
		}

		@Override
		public Ping sender() {
			return sender;
		}
		
	}
	
	public class PongMessage extends Message<Ping> {

		private final Pong sender;

		public PongMessage(Pong sender) {
			this.sender = sender;
		}
		
		@Override
		public void accept(Ping visitor) {
			visitor.receive(this);
		}

		@Override
		public Pong sender() {
			return sender;
		}
		
	}
	
	public class Pong extends Actor<Ping> {

		@Override
		public void run() {
			for(Message<Ping> m = pollMailbox() ; m != null ; m = pollMailbox()) {
				m.accept(this);
			}
		}

		public void receive(PingMessage pingMessage) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
	@Test
	public void messageTest() {
		
	}
}
