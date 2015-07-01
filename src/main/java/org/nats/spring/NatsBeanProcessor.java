package org.nats.spring;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nats.Connection;
import org.nats.MsgHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Aspect
public class NatsBeanProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {
	private static final String VERSION = "0.2.1";
	
	private String uri;
	private boolean reconnect;
	private int maxReconnectAttempts;
	private int maxReconnectTimewait;
	private Connection conn;
	private HashMap<String, BeanMsgHandler> handlers;
	private Timer beanTimer;
	
	public NatsBeanProcessor(String uri) {this.uri = uri;}
	public void setUri(String uri) {this.uri = uri;}
	public void setReconnect(boolean reconnect) {this.reconnect = reconnect;}
	public void setMaxReconnectAttempts(int maxReconnectAttempts) {this.maxReconnectAttempts = maxReconnectAttempts;}
	public void setMaxReconnectTimewait(int maxReconnectTimewait) {this.maxReconnectTimewait = maxReconnectTimewait;}
	public Connection getDefaultConnection() { return conn; }
	public String getVersion() { return VERSION; }
	
	@Override
	public void afterPropertiesSet() throws Exception {
		handlers = new HashMap<String, BeanMsgHandler>();
		
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
	}
	
	@Around("execution(@org.nats.spring.Publish String *(.., @org.nats.spring.Key (*), ..))")
	public Object publish(ProceedingJoinPoint pjp) throws Throwable {
		Object result = pjp.proceed();

		MethodSignature sig = (MethodSignature) pjp.getSignature();
		Class<?> cls = sig.getDeclaringType();
		Method method = cls.getDeclaredMethod(sig.getName(), sig.getParameterTypes());
		Parameter[] params = method.getParameters();
		for(int i = 0; i < params.length; i++) {
			if (params[i].getAnnotation(Key.class) != null) {
				conn.publish((String)pjp.getArgs()[i], (String)result);
				break;
			}
		}        
		
		return result;	
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		try {
			boolean isAttr = false;
			for (Method method : bean.getClass().getMethods()) {
				Subscribe sub = AnnotationUtils.findAnnotation(method, Subscribe.class);
				if (sub != null) {
					isAttr = StringUtils.hasText(sub.attr());
					for(String s : (isAttr) ? sub.attr().split(sub.delimiter()) : sub.value().split(sub.delimiter()))
						subscribe((isAttr ? extractAttr(bean, s) : s), bean, method, sub);	
					continue;
				}
				Request req = AnnotationUtils.findAnnotation(method, Request.class);
				if (req != null) {
					isAttr = StringUtils.hasText(req.attr());
					for(String s : (isAttr) ? req.attr().split(req.delimiter()) : req.value().split(req.delimiter()))
						request((isAttr ? extractAttr(bean, s) : s), bean, method, req);					
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
			return (String) getter.invoke(bean);		
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void subscribe(String subject, final Object bean, Method method, Subscribe sub) throws IOException {
		final BeanMsgHandler handler = new BeanMsgHandler();
		Properties props = null;
		if (sub.limit() != 0) {
			props = new Properties();
			props.setProperty("max", Integer.toString(sub.limit()));
		}
		handler.setSid(conn.subscribe(subject, props, handler));
		handler.setMethod(method);
		handler.setBean(bean);
		handler.setSub(sub);
		handler.setSubject(subject);
		final String key = bean.toString() + "_" + subject;
		handlers.put(key, handler);
			
		if (handler.getSub().timeout() > 0) {
			beanTimer.schedule(new TimerTask() {
				public void run() {
					try {
						unsubscribe(handler.getSid());
						handlers.remove(key);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, handler.getSub().timeout() * 1000);
		}		
	}

	private void request(String subject, final Object bean, final Method method, Request req) throws IOException {
		conn.request(subject, new MsgHandler() {
			public void execute(String response) {
				try {
					method.invoke(bean, response);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});			
	}
		
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	public void unsubscribe(Object bean, String subject) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, UnknownSubscriptionException {
		String key = bean.toString() + "_" + subject;
		BeanMsgHandler handler = handlers.get(key);
		if (handler == null) {
			key = bean.toString() + "_" + extractAttr(bean, subject);
			handler = handlers.get(key);
			if (handler == null)
				throw new UnknownSubscriptionException("Unknown subject : " + subject);
		}
		unsubscribe(handler.getSid());
		handlers.remove(key);
	}

	public void unsubscribe(Integer sid) throws IOException {
		conn.unsubscribe(sid);
	}
}
