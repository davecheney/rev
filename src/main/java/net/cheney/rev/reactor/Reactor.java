package net.cheney.rev.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import net.cheney.rev.actor.Actor;
import net.cheney.rev.channel.AsyncServerChannel;
import net.cheney.rev.channel.AsyncSocketChannel;
import net.cheney.rev.channel.BindMessage;
import net.cheney.rev.channel.ChannelClosedMessage;
import net.cheney.rev.channel.ChannelRegistrationCompleteMessage;
import net.cheney.rev.protocol.ServerProtocolFactory;

public final class Reactor extends Actor<Reactor> {

	private Selector selector;

	public Reactor() throws IOException {
		this.selector = SelectorProvider.provider().openSelector();
	}

	public void listen(@Nonnull InetSocketAddress addr, @Nonnull ServerProtocolFactory factory) throws IOException {
		AsyncServerChannel sc = new AsyncServerChannel(factory);
		sc.send(new BindMessage(this, addr));
	}
	
//	public Future<AsyncSocketChannel> connect(@Nonnull SocketAddress addr) throws IOException {
//		AsyncSocketChannel sc = new AsyncSocketChannel();
//		sc.send(new ConnectMessage(this, addr));
//	}
	
	void receive(@Nonnull RegisterAsyncServerChannelMessage msg) {
		try {
			msg.channel().register(this.selector, 0, msg.sender());
			msg.sender().send(new ChannelRegistrationCompleteMessage<AsyncServerChannel>(this));
		} catch (ClosedChannelException e) {
			msg.sender().send(new ChannelClosedMessage<AsyncServerChannel>(this));
		}
	}
	
	void receive(@Nonnull RegisterAsyncSocketChannelMessage msg) {
		try {
			msg.channel().register(this.selector, 0, msg.sender());
			msg.sender().send(new ChannelRegistrationCompleteMessage<AsyncSocketChannel>(this));
		} catch (ClosedChannelException e) {
			msg.sender().send(new ChannelClosedMessage<AsyncSocketChannel>(this));
		}
	}

	void receive(@Nonnull EnableInterestMessage msg) {
		SelectionKey sk = msg.channel().keyFor(this.selector);
		sk.interestOps(sk.interestOps() | msg.ops());
	}
	
	void receive(@Nonnull DisableInterestMessage msg) {
		SelectionKey sk = msg.channel().keyFor(this.selector);
		sk.interestOps(sk.interestOps() & ~msg.ops());
	}

}
