package org.nats.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * When a method annotated with @Subscribe is identified upon bean creation time, Spring Nats subscribes to the subject.
 * The subject can be a plain text or a value of a bean attribute.
 * Timeout or number of method invocation can be specified for expiring subscription.
 * @author Teppei Yagihashi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Subscribe {
	String value() default "";
	String attr() default "";
	String delimiter() default ",";
	int timeout() default 0;
	int limit() default 0;
}
