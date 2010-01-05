package net.cheney.rev.channel;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

public abstract class Operation {
	
	private static final Logger LOG = Logger.getLogger(Operation.class);

	public void completed() {
		// yay
	}

	public void failed(@Nonnull Throwable t) {
		LOG.fatal(t);
	}
}
