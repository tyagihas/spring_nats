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
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/basic-context.xml");

		MyBean bean = (MyBean)context.getBean("myBean");
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		nats.getDefaultConnection().publish("Tokyo", "Hello from Tokyo");
		
		System.out.println("\nPress enter to unsubscribe.");
		bufferedReader.readLine();

		// Unsubscribing matched texts
		nats.unsubscribe(bean, "name,addr");
	}
}
