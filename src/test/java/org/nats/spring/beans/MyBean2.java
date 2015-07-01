package org.nats.spring.beans;

import org.nats.spring.Key;
import org.nats.spring.Publish;
import org.nats.spring.Request;
import org.nats.spring.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class MyBean2 {
	private String addr;
	
	public MyBean2() {}
	public String getAddr() { return addr; }
	public void setAddr(String addr) { this.addr = addr; }
	
	// Wildcard Subscription
	@Subscribe("woo.*")
	public void receiveByText(String subject, String message) {
		System.out.println("MyBean2 Received (by text=woo.*) : " + message);
	}

	// Subscription by bean attributes
	@Subscribe(attr="addr")
	public void receiveByAttr(String subject, String message) {
		System.out.println("MyBean2 Received (by attribute=" + subject + ") : " + message);
	}
	
	// Subscription expires based on timeout or number of processed messages 
	@Subscribe(value="hoo", timeout=10, limit=5)
	public void receiveAutoUnsub(String subject, String message) {
		System.out.println("MyBean2 Received (by text=" + subject + ") : " + message);
	}
	
	// Publishing a message
	@Publish
	public String publish(@Key String key, int counter) {
		return "Counter : " + Integer.toString(counter);
	}

	// Sending a request to subscribers
	@Request("help")
	public void sendRequest(String response) {
		System.out.println("MyBean2 Got a response for help : " + response);
	}
}
