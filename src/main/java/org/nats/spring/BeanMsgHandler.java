package org.nats.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nats.MsgHandler;

public class BeanMsgHandler extends MsgHandler {
			
	private Integer sid;
	private Object bean;
	private Method method;
	private Subscribe sub;
	private String subject;
	private boolean isReply;
	
	public BeanMsgHandler() {sid = null;}
	
	public void setSid(Integer sid) {this.sid = sid;}
	public Integer getSid() {return sid;}
	public void setBean(Object bean) {this.bean = bean;}
	public Object getBean() {return bean;}
	public void setMethod(Method method) {
		this.method = method;
		if (method.getParameterCount() == 3)
			isReply = true;
		else
			isReply = false;
	}	
	public Method getMethod() {return method;}
	public void setSub(Subscribe sub) {this.sub = sub;}
	public Subscribe getSub() {return sub;}
	public void setSubject(String subject) {this.subject = subject;}
	public String getSubject() {return subject;}
	public void setIsReply(boolean isReply) {this.isReply = isReply;}
	public boolean getIsReply() {return isReply;}
	
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
