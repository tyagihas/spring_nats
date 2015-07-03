package org.nats.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * When a method annotated with @Request is identified upon bean creation time, Spring Nats sends a request to subscribers
 * and invokes the method when it receives a returned message.
 * The subject can be a plain text or a value of a bean attribute.
 * @author Teppei Yagihashi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Request {
	String value() default "";
	String attr() default "";
	String delimiter() default ",";
}
