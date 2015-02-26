package org.nats.spring.examples;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class MultiPub {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/advanced-context.xml");
		
		MyBean bean = (MyBean)context.getBean("myBean");
		MyBean2 bean2 = (MyBean2)context.getBean("myBean2");
		
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		// This single message propagates to multiple beans.
		nats.getDefaultConnection().publish("Tokyo", "Hello from Tokyo");		
	}
}
