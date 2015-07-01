package org.nats.spring.beans;

import org.nats.spring.Key;
import org.nats.spring.Publish;
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

	// Publishing a message with @Key parameter as key and returned String as value
	@Publish
	public String publishWithAnnotation(@Key String key) {
		return "Japan";
	}
	
	// Subscription by multiple static text values
	@Subscribe("foo,woo.hoo")
	public void receiveByText(String subject, String message) {
		System.out.println("MyBean Received (by text=" + subject + ") : " + message);
	}
	
	// Subscription by multiple bean attributes
	@Subscribe(attr="name,addr")
	public void receiveByAttr(String subject, String message) {
		System.out.println("MyBean Received (by attribute=" + subject + ") : " + message);
	}	
}
