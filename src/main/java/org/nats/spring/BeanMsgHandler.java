package org.nats.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nats.MsgHandler;

public class BeanMsgHandler extends MsgHandler {
			
	public Integer sid;
	public Object bean;
	private Method method;
	public Subscribe sub;
	public String subject;
	private boolean isReply;
	
	public BeanMsgHandler() {
		sid = null;
	}
	
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	
	public Integer getSid() {
		return sid;
	}
	
	public void setMethod(Method method) {
		this.method = method;
		if (method.getParameterCount() == 3)
			isReply = true;
		else
			isReply = false;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public boolean isSubscribed() {
		return sid != null;
	}
	
	@Override
	public void execute(String msg, String reply, String subject) {
		try {
			if (isReply)
				method.invoke(bean, subject, reply, msg);
			else
				method.invoke(bean, subject, msg);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
