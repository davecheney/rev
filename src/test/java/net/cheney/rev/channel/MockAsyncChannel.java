package net.cheney.rev.channel;

import java.nio.channels.SelectableChannel;

public class MockAsyncChannel extends AsyncChannel<MockAsyncChannel> {

	private SelectableChannel channel;

	public MockAsyncChannel(SelectableChannel channel) {
		this.channel = channel();
	}

	@Override
	protected SelectableChannel channel() {
		return channel; 
	}

	@Override
	void receive(ChannelRegistrationCompleteMessage<MockAsyncChannel> channelRegistrationCompleteMessage) {
		// TODO Auto-generated method stub
		
	}
}
