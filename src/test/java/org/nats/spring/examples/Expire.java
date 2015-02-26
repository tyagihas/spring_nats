package org.nats.spring.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.nats.spring.NatsBeanProcessor;
import org.nats.spring.beans.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Expire {

	public static void main(String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		ApplicationContext context = new FileSystemXmlApplicationContext("./resources/advanced-context.xml");
		
		MyBean2 bean2 = (MyBean2)context.getBean("myBean2");
		
		NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");
		
		System.out.println("\nWait for 10 seconds to timeout OR press enter to reach the limit(=5).");
		bufferedReader.readLine();

		for(int i = 1; i < 7; i++) {
			System.out.println("Publishing " + i);
			nats.getDefaultConnection().publish("hoo", "Count " + i);
		}
	}
}
