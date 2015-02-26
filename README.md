# Spring_Nats
Spring implementation for [NATS messaging system](http://nats.io). Java Bean instances can communicate each other locally or remotely based on roboust messaging system.

## Getting Started

Adding dependency to Maven pom.xml

```xml
<dependency>
	<groupId>com.github.tyagihas</groupId>
	<artifactId>spring_nats</artifactId>
	<version>0.1</version>
</dependency>
```

### Configuration

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns=...>
	
	<nats:config uri="nats://server1:4222" />
	    
	<bean id="myBean" class="org.nats.spring.beans.MyBean">
		<property name="name" value="Teppei" />
		<property name="addr" value="Tokyo" />
	</bean>
</beans>
```

## Basic Usage

```java
// Subscribing
public class MyBean {
	private String name;
	private String addr; 
	
	...
	
	// Subscription by text
	@Subscribe("foo,woo")
	public void handleMessageByText(String subject, String message) {
		System.out.println("MyBean1 Received (by text=" + subject + ") : " + message);
	}
	
	// Subscription by bean attributes
	@Subscribe(attr="name,addr")
	public void handleMessageByAttr(String subject, String message) {
		System.out.println("MyBean1 Received (by attribute=" + subject + ") : " + message);
	}
}

// Unsubscribing
MyBean bean = (MyBean)context.getBean("myBean");
NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");

nats.unsubscribe(bean, "name,addr");
```

### Clustered Usage
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns=...>
	
	<nats:config uri="nats://user1:pass1@server1:4242,nats://user1:pass1@server2:4243" />
	    
</beans>
```

## Advanced Usage

```java
// Broadcasting a single message to multiple Bean instances (subscribers)  
public class MyBean {
	...

	// Subscription by text
   	@Subscribe("foo")
   	public void handleMessageByText(String subject, String message) {
		System.out.println("Received : " + message);
	}	
}

public class MyBean2 {
	...

	// Subscription by text
   	@Subscribe("foo")
   	public void handleMessageByText(String subject, String message) {
		System.out.println("Received : " + message);
	}	
}

// Auto-expiring subscriptions and sending requests
public class MyBean2 {
	private String addr;
	
	public MyBean2() {}
	public String getAddr() { return addr; }
	public void setAddr(String addr) { this.addr = addr; }
	
	// Subscription expires based on timeout or number of processed messages 
	@Subscribe(value="hoo", timeout=10, limit=5)
	public void handleMessageAutoUnsub(String subject, String message) {
		System.out.println("MyBean2 Received : " + message);
	}
	
	// Sending a request and receiving a response from a subscriber
	@Request("help")
	public void handleRequest(String response) {
		System.out.println("MyBean2 Got a response for help : " + response);
	}
} 
```

## License

(The MIT License)

Copyright (c) 2015 Teppei Yagihashi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to
deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.
