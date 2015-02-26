package org.nats.spring.beans;

import org.nats.spring.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class MyBean {
	private String name;
	private String addr; 
	
	public MyBean() {}
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getAddr() { return addr; }
	public void setAddr(String addr) { this.addr = addr; }
	
	// Subscription by text
	@Subscribe("foo,woo")
	public void handleMessageByText(String subject, String message) {
		System.out.println("MyBean1 Received (by text=" + subject + ") : " + message);
	}
	
	// Subscription by bean attributes
	@Subscribe(attr="name,addr")
	public void handleMessageByAttr(String subject, String message) {
		System.out.println("MyBean1 Received (by attribute=" + subject + ") : " + message);
	}
}
