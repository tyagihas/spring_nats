# Spring_Nats
Spring implementation for [NATS messaging system](http://nats.io). Annotated Java Bean instances can communicate each other based on robust NATS messaging system.  
  
Following method annotations are available in Spring_Nats :  
* @Publish : When a bean method is invoked, publish a message with a @Key annotated parameter as a key and returned String as a value. The annotation is evaluated upon every method invocation.
* @Subscribe : Subscribe to a subject which can be specified as a plain text or a bean attribute. This annotation is only evaluated during bean creation time.
* @Request : Send a request to subscribers for a response. Request string can be specified as a plain text or a bean attribute. This annotation is only evaluated during bean creation time.

## Getting Started

Adding dependency to Maven pom.xml

```xml
<dependency>
	<groupId>com.github.tyagihas</groupId>
	<artifactId>spring_nats</artifactId>
	<version>0.2.1</version>
</dependency>
```

### Configuration 

application-context.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns=...>
	
	<aop:aspectj-autoproxy proxy-target-class="true" />
	<nats:config uri="nats://server1:4222" />
	    
	<bean id="myBean" class="org.nats.spring.beans.MyBean">
		<property name="name" value="Teppei" />
		<property name="addr" value="Tokyo" />
	</bean>
</beans>
```

## Basic Usage

```java
public class MyBean {
	private String name;
	private String addr; 
	
	...

	// Publishing a message with the parameter with @Key annotation as key and returned String as value
	@Publish
	public String publishWithAnnotation(@Key String key) {
		return "Japan";
	}
	
	// Subscription by multiple static text values
	@Subscribe("foo,woo.hoo")
	public void receiveByText(String subject, String message) {
		System.out.println("MyBean Received (by text=" + subject + ") : " + message);
	}
	
	// Subscription by bean attributes
	@Subscribe(attr="name,addr")
	public void receiveByAttr(String subject, String message) {
		System.out.println("MyBean Received (by attribute=" + subject + ") : " + message);
	}
}

// Unsubscribing
MyBean bean = (MyBean)context.getBean("myBean");
NatsBeanProcessor nats = (NatsBeanProcessor) context.getBean("nats");

nats.unsubscribe(bean, "name");
nats.unsubscribe(bean, "addr");
```

### Wildcard Subscriptions
```java
public class MyBean2 {
	...
	
	// Wildcard Subscription
	@Subscribe("foo.*.baz")
	public void receiveByText1(String subject, String message) {
		System.out.println("MyBean2 Received (by text=" + subject + ") : " + message);
	}

	@Subscribe("foo.bar.*")
	public void receiveByText2(String subject, String message) {
		System.out.println("MyBean2 Received (by text=" + subject + ") : " + message);
	}

	@Subscribe("*.bar.*")
	public void receiveByText3(String subject, String message) {
		System.out.println("MyBean2 Received (by text=" + subject + ") : " + message);
	}

	// ">" matches any length of the tail of a subject, and can only be the last token
	// E.g. 'foo.>' will match 'foo.bar', 'foo.bar.baz', 'foo.foo.bar.bax.22'
	@Subscribe("foo.>")
	public void receiveByText4(String subject, String message) {
		System.out.println("MyBean2 Received (by text=" + subject + ") : " + message);
	}
}
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
// Subscription expires based on timeout OR number of processed messages 
public class MyBean2 {
	...

	@Subscribe(value="hoo", timeout=10, limit=5)
	public void receiveAutoUnsub(String subject, String message) {
		System.out.println("MyBean2 Received : " + message);
	}
}	

// Sending a request to a helper bean and receiving a response
public class MyBean2 {
	...

	// Sending a request to subscribers
	@Request("help")
	public void sendRequest(String response) {
		System.out.println("MyBean2 Got a response for help : " + response);
	}
}

// Helper bean
@Component
public class Helper {
	private NatsBeanProcessor nats;
	
	@Autowired
	public Helper(NatsBeanProcessor nats) {this.nats = nats;}
	
	// Listening for help request
	@Subscribe("help")
	public void replyToHelp(String subject, String reply, String message) throws IOException {
		nats.getDefaultConnection().publish(reply, "I can help you!");
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
