package net.cheney.rev.channel;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EnableInterestTest {
	
	private ServerSocketChannel sc;

	@Before public void setup() throws IOException {
		sc = SelectorProvider.provider().openServerSocketChannel();
	}

	@Test public void channelBindTest() throws IOException {
		
	}
	
	@After public void cleanup() throws IOException {
		sc.close();
	}
}
