package org.nats.spring;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;

import org.nats.MsgHandler;

public class BeanMsgHandler extends MsgHandler {
	public class Receiver {
		public Object bean;
		public Method method;
		public Subscribe sub;
		public int counter = 0;
		public boolean isLimit() {return sub.limit() > 0;}
	}
	
	private NatsBeanProcessor nats;
	private ArrayList<Receiver> receivers;
	private Integer sid;
	private Timer timer;
	
	public BeanMsgHandler(NatsBeanProcessor nats) {
		this.nats = nats;
		receivers = new ArrayList<Receiver>();
	}
	
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	
	public Integer getSid() {
		return sid;
	}
	
	public void addMethod(Receiver receiver) {
		receivers.add(receiver);
	}
	
	public void removeMethod(Object bean, Method method) throws IOException {
		for(int i = 0; i < receivers.size(); i++) {
			Receiver receiver = receivers.get(i);
			if ((receiver.bean == bean) && (receiver.method.getName().equals(method.getName()))) 
				receivers.remove(i);
		}
		
		// If no receiver is available, unsubscribe the key.
		if (receivers.size() == 0) nats.getDefaultConnection().unsubscribe(sid);
	}
	
	@Override
	public void execute(String msg, String reply, String subject) {
		for(int i = 0; i < receivers.size(); i++) {
			Receiver receiver = receivers.get(i);
			receiver.counter++;
			try {
				if (receiver.bean == null)
					receivers.remove(i);
				else {
					receiver.method.invoke(receiver.bean, subject, msg);
					if ((receiver.isLimit()) && (receiver.counter >= receiver.sub.limit()))
						removeMethod(receiver.bean, receiver.method);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
