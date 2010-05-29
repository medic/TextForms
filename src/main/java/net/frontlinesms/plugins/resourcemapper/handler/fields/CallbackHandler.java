package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextMapping;

public interface CallbackHandler<M extends PlainTextMapping> extends FieldMessageHandler<M> {

	public void callBackTimedOut(String msisdn);
	
	public boolean shouldHandleCallbackMessage(Message m);
	
	public void handleCallback(Message m);
}
