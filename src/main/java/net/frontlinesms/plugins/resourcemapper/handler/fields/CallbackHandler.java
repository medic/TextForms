package net.frontlinesms.plugins.resourcemapper.handler.fields;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

public abstract class CallbackHandler<M extends Field> extends FieldMessageHandler<M> {

	public CallbackHandler() {
		super(null, null);
	}
	
	public CallbackHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public abstract void callBackTimedOut(String msisdn);
	
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	public abstract void handleCallback(FrontlineMessage m);
}
