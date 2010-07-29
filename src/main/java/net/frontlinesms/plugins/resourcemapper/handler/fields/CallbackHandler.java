package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

public abstract class CallbackHandler<M extends Field> extends FieldMessageHandler<M> {

	/**
	 * CallbackHandler
	 */
	public CallbackHandler() {} 
	
	public abstract void callBackTimedOut(String msisdn);
	
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	public abstract void handleCallback(FrontlineMessage m);
}
