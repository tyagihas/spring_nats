package org.nats.spring.examples;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.MyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Sub {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/basic-context.xml");
		
		MyBean bean = (MyBean)context.getBean("myBean");
		
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		nats.getDefaultConnection().publish("Tokyo", "Hello from Tokyo");
	}
}
