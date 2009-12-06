package net.cheney.rev.reactor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class ListenTestCase {

	private Reactor reactor;

	@Before public void setup() throws IOException {
		this.reactor = Reactor.open();
	}
	
	@Test public void testListen() throws IOException {
		InetSocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(), 6500);
		reactor.listen(addr, null);
		
		Socket s = new Socket();
		s.connect(addr);
		Assert.assertTrue(s.isConnected());
	}
}
