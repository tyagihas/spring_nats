package org.nats.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Annotation for publishing a message to NATS subscribers. It has to be used in conjunction with @Key annotation.
 * A method call is captured and @Key annotated parameter is treated as a key and returned String from the method
 * as a value.
 *  
 * @author Teppei Yagihashi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Publish {
}
