package org.nats.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.nats.Connection;
import org.nats.MsgHandler;
import org.nats.spring.BeanMsgHandler.Receiver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NatsBeanProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {
	private static final int SUBSCRIBE	= 1;
	private static final int REQUEST	= 2;
	
	private String uri;
	private boolean reconnect;
	private int maxReconnectAttempts;
	private int maxReconnectTimewait;
	private ApplicationContext context;
	private Connection conn;
	private HashMap<String, BeanMsgHandler> subscribeHandlers;
	private Timer beanTimer;
	
	public NatsBeanProcessor(String uri) {this.uri = uri;}
	public void setUri(String uri) {this.uri = uri;}
	public void setReconnect(boolean reconnect) {this.reconnect = reconnect;}
	public void setMaxReconnectAttempts(int maxReconnectAttempts) {this.maxReconnectAttempts = maxReconnectAttempts;}
	public void setMaxReconnectTimewait(int maxReconnectTimewait) {this.maxReconnectTimewait = maxReconnectTimewait;}
	public Connection getDefaultConnection() { return conn; }
	
	@Override
	public void afterPropertiesSet() throws Exception {
		subscribeHandlers = new HashMap<String, BeanMsgHandler>();
		
		Properties popts = new Properties();
		popts.put("uri", uri);
		popts.put("reconnect", Boolean.valueOf(reconnect));
		popts.put("max_reconnect_attempts", maxReconnectAttempts);
		popts.put("max_reconnect_time_wait", maxReconnectTimewait);
			
		try {
			conn = Connection.connect(popts);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}		
		
		beanTimer = new Timer("NATS_Spring_Timer");
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Subscribe sub = null;
		Request req = null;
		boolean isAttr = false;

		try {
			for (Method method : bean.getClass().getMethods()) {
				for(Annotation annon : method.getAnnotations()) {
					// Attribute based subscription has higher priority.
					if (annon.annotationType() == Subscribe.class) {
						sub = (Subscribe)annon;
						isAttr = StringUtils.hasText(sub.attr());
						for(String s : (isAttr) ? sub.attr().split(sub.delimiter()) : sub.value().split(sub.delimiter()))
							subscribe((isAttr ? extractAttr(bean, s) : s), bean, method, sub);
					}
					else if (annon.annotationType() == Request.class) { 
						req = (Request)annon;
						isAttr = StringUtils.hasText(req.attr());
						for(String s : (isAttr) ? req.attr().split(req.delimiter()) : req.value().split(req.delimiter()))
							request((isAttr ? extractAttr(bean, s) : s), bean, method, req);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	private String extractAttr(Object bean, String attr) {
		Method getter;
		try {
			getter = bean.getClass().getMethod("get" + StringUtils.capitalize(attr));
			return (String) getter.invoke(bean, null);		
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void subscribe(String subject, final Object bean, Method method, Subscribe sub) throws IOException {
		BeanMsgHandler handler;
		if (subscribeHandlers.containsKey(subject))
			handler = subscribeHandlers.get(subject);
		else {
			handler = new BeanMsgHandler(this);
			subscribeHandlers.put(subject, handler);
			handler.setSid(conn.subscribe(subject, handler));
		}
		final Receiver receiver = handler.new Receiver();
		receiver.bean = bean;
		receiver.method = method;
		receiver.sub = sub;
		
		handler.addMethod(receiver);
		
		if (receiver.sub.timeout() > 0) {
			beanTimer.schedule(new TimerTask() {
				public void run() {
					try {
						unsubscribe(bean, (StringUtils.hasText(receiver.sub.attr()) ? receiver.sub.attr() : receiver.sub.value()));
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
						e.printStackTrace();
					}
				}
			}, receiver.sub.timeout() * 1000);
		}		
	}

	private void request(String subject, final Object bean, final Method method, Request req) throws IOException {
		conn.request(subject, new MsgHandler() {
			public void execute(String response) {
				try {
					method.invoke(bean, response);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});			
	}
		
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	public void unsubscribe(Object bean, String subText) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		for (Method method : bean.getClass().getMethods()) {
			for(Annotation annon : method.getAnnotations()) {
				if (annon.annotationType() == Subscribe.class) {
					Subscribe sub = (Subscribe)annon;
					if (subText.equals(sub.attr()))
						for(String s : sub.attr().split(sub.delimiter())) {
							subscribeHandlers.get(extractAttr(bean, s)).removeMethod(bean, method);
						}
					if (subText.equals(sub.value()))
						for(String s : sub.value().split(sub.delimiter())) {
							subscribeHandlers.get(s).removeMethod(bean, method);
						}
				}
			}
		}		
	}

}
