package org.nats.spring.examples;

import org.nats.spring.beans.MyBean;
import org.nats.spring.beans.MyBean2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Sub {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("./nats-context.xml");
		
		MyBean bean = (MyBean)context.getBean("myBean");	
		MyBean2 bean2 = (MyBean2)context.getBean("myBean2");	
		
		bean.publishWithAnnotation("woo.hoo");
	}
}
