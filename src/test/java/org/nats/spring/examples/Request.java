package org.nats.spring.examples;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.Helper;
import org.nats.spring.beans.MyBean2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Request {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("./nats-context.xml");

		Helper helper = (Helper)context.getBean("helper");
		MyBean2 bean2 = (MyBean2)context.getBean("myBean2");
	}
}
