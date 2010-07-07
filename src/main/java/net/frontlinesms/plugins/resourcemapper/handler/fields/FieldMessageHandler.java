package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;

public interface FieldMessageHandler<M extends PlainTextField> extends MessageHandler{

	public void generateAndPublishXML(FieldResponse<M> response);
}
