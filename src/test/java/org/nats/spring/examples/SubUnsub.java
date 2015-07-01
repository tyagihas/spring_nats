package org.nats.spring.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.MyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SubUnsub {

	public static void main(String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/nats-context.xml");

		MyBean bean = (MyBean)context.getBean("myBean");
		bean.publishWithAnnotation("Tokyo");
		
		System.out.println("\nPress enter to unsubscribe.");
		bufferedReader.readLine();

		// Unsubscribing matched texts or attributes
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		nats.unsubscribe(bean, "name");
		nats.unsubscribe(bean, "addr");
	}
}
