package org.nats.spring;

public class UnknownSubscriptionException extends Exception {
	
	public UnknownSubscriptionException(String message) {
		super(message);
	}
}
