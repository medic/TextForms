package net.frontlinesms.plugins.resourcemapper.upload;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * Upload Document
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public abstract class UploadDocument implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(UploadDocument.class);
	
	private final List<FieldResponse> fieldResponses = new ArrayList<FieldResponse>();
	
	protected UiGeneratorController ui;
	
	public void setUiGeneratorController(UiGeneratorController ui) {
		this.ui = ui;
	}
	
	public UiGeneratorController getUiGeneratorController() {
		return this.ui;
	}
	
	/**
	 * Add FieldResponse
	 * @param fieldResponse
	 */
	public void addFieldResponse(FieldResponse fieldResponse) {
		this.fieldResponses.add(fieldResponse);
	}
	
	/**
	 * Get FieldResponses
	 * @return list of FieldResponses
	 */
	public List<FieldResponse> getFieldResponses() {
		return this.fieldResponses;
	}
	
	public boolean upload() {
		LOG.debug("upload");
		String document = toString();
		if (ResourceMapperProperties.isDebugMode()) {
			LOG.debug("Document: %s", document);
			return true;
		}
		else if (ResourceMapperProperties.getPublishURL() != null) {
			try {
			    URL url = new URL(ResourceMapperProperties.getPublishURL());
			    URLConnection connection = url.openConnection();
			    connection.setDoInput(true);
			    connection.setDoOutput(true);
			    connection.setUseCaches(false);
			    connection.setDefaultUseCaches(false);
			    connection.setRequestProperty ("Content-Type", "text/xml");
			    Writer writer = new OutputStreamWriter(connection.getOutputStream());
			    try {
			    	 writer.write(document);
			    	 writer.flush(); 	
			    }
			    finally {
			    	writer.close();	
			    }		   
			    Reader reader = new InputStreamReader(connection.getInputStream());
			    try {
			    	StringBuilder response = new StringBuilder();
				    char[] buffer = new char[2048];
				    int num;
				    while (-1 != (num=reader.read(buffer))) {
				    	response.append(buffer, 0, num);
				    }
				    LOG.debug("Response: %s", response.toString());
			    }
			    finally {
			    	reader.close();
			    }
			}
			catch (Throwable t) {
				LOG.error("Error: %s", t);
				return false;
			}
		}
		else {
			LOG.error("No Publish URL Specified");
		}
		return true;
	}
	
	/**
	 * Implementing class needs to define toString() method
	 */
	@Override
	public abstract String toString();
	
	/**
	 * Get title
	 * @return
	 */
	public abstract String getTitle();
	
	/**
	 * The XML file for component options
	 * @return
	 */
	public abstract String getPanelXML();
	
}