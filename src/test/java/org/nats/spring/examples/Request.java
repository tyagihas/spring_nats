package org.nats.spring.examples;

import java.io.IOException;

import org.nats.Connection;
import org.nats.MsgHandler;
import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.MyBean2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Request {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/advanced-context.xml");
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		
		final Connection conn = nats.getDefaultConnection();
		conn.subscribe("help", new MsgHandler() {
			public void execute(String request, String replyTo) {
				try {
					conn.publish(replyTo, "I can help!");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		});		

		MyBean2 bean = (MyBean2)context.getBean("myBean2");
	}
}
