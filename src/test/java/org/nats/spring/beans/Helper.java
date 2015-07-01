package org.nats.spring.beans;

import java.io.IOException;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Helper {
	private NatsBeanProcessor nats;
	
	@Autowired
	public Helper(NatsBeanProcessor nats) {this.nats = nats;}
	
	// Listening for help request
	@Subscribe("help")
	public void replyToHelp(String subject, String reply, String message) throws IOException {
		nats.getDefaultConnection().publish(reply, "I can help you!");
	}
}
