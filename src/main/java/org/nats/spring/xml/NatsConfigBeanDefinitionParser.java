package org.nats.spring.xml;

import org.nats.Connection;
import org.nats.spring.NatsBeanProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class NatsConfigBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override 
	protected Class getBeanClass(Element element) {
		return NatsBeanProcessor.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		// Mandatory attribute
		bean.addConstructorArgValue(element.getAttribute("uri"));

		// Optional attributes, set default values if not specified.
		bean.addPropertyValue("reconnect", (element.getAttribute("reconnect").equals("") ? false : Boolean.valueOf(element.getAttribute("reconnect"))));
		bean.addPropertyValue("maxReconnectAttempts", (element.getAttribute("max_reconnect_attempts").equals("") ? Integer.valueOf(Connection.DEFAULT_MAX_RECONNECT_ATTEMPTS) : Integer.valueOf(element.getAttribute("max_reconnect_attempts"))));
		bean.addPropertyValue("maxReconnectTimewait", (element.getAttribute("max_reconnect_time_wait").equals("") ? Integer.valueOf(Connection.DEFAULT_RECONNECT_TIME_WAIT) : Integer.valueOf(element.getAttribute("reconnect_time_wait"))));
	}
	
}
