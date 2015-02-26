package org.nats.spring.beans;

import org.nats.spring.Request;
import org.nats.spring.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class MyBean2 {
	private String addr;
	
	public MyBean2() {}
	public String getAddr() { return addr; }
	public void setAddr(String addr) { this.addr = addr; }
	
	// Subscription by bean attributes
	@Subscribe(attr="addr")
	public void handleMessageByAttr(String subject, String message) {
		System.out.println("MyBean2 Received (by attribute=" + subject + ") : " + message);
	}
	
	// Subscription expires based on timeout or number of processed messages 
	@Subscribe(value="hoo", timeout=10, limit=5)
	public void handleMessageAutoUnsub(String subject, String message) {
		System.out.println("MyBean2 Received : " + message);
	}
	
	// Sending a request to subscribers
	@Request("help")
	public void handleRequest(String response) {
		System.out.println("MyBean2 Got a response for help : " + response);
	}
}
