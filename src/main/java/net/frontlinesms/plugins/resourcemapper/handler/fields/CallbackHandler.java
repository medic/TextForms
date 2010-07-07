package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

public interface CallbackHandler<M extends PlainTextField> extends FieldMessageHandler<M> {

	public void callBackTimedOut(String msisdn);
	
	public boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	public void handleCallback(FrontlineMessage m);
}
