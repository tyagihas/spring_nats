package org.nats.spring.xml;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NatsNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", (BeanDefinitionParser)new NatsConfigBeanDefinitionParser());		
	}

}
