package org.nats.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @Key annotation has to be used in conjunction with @Publish annotation. A method parameter with @Key annotation is
 * treated as a key to publish a message.
 * 
 * @author Teppei Yagihashi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Key {
	String value() default "";
}
