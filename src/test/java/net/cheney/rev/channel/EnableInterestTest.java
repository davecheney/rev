package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import net.cheney.rev.reactor.Reactor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EnableInterestTest {
	
	private ServerSocketChannel sc;

	@Before public void setup() throws IOException {
		sc = SelectorProvider.provider().openServerSocketChannel();
	}

	@Test public void channelBindTest() throws IOException {
		Reactor reactor = new Reactor();
		MockAsyncChannel channel = new MockAsyncChannel(sc);
		reactor.send(new RegisterAsyncChannelMessage<MockAsyncChannel>(channel) {

			@Override
			public void accept(Reactor receiver) {
				receiver.recieve(this);
			}
		});
	}
	
	@After public void cleanup() throws IOException {
		sc.close();
	}
}
