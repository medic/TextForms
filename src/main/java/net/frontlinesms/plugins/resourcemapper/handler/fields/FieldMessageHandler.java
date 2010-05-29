package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextMapping;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;

public interface FieldMessageHandler<M extends PlainTextMapping> extends MessageHandler{

	public void generateAndPublishXML(FieldResponse<M> response);
}
