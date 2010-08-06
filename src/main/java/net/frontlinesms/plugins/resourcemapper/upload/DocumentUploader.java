package net.frontlinesms.plugins.resourcemapper.upload;

import java.io.BufferedReader;
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
 * DocumentUploader
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public abstract class DocumentUploader implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(DocumentUploader.class);
	
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
	 * Get ContentType
	 * @return content type
	 */
	public abstract String getContentType();
	
	/**
	 * The XML file for component options
	 * @return
	 */
	public abstract String getPanelXML();
	
	/**
	 * Collection of field responses
	 */
	private final List<FieldResponse> fieldResponses = new ArrayList<FieldResponse>();
	
	/**
	 * UiGeneratorController
	 */
	protected UiGeneratorController ui;
	
	/**
	 * Set UiGeneratorController
	 * @param ui UiGeneratorController
	 */
	public void setUiGeneratorController(UiGeneratorController ui) {
		this.ui = ui;
	}
	
	/**
	 * Get UiGeneratorController
	 * @return UiGeneratorController
	 */
	public UiGeneratorController getUiGeneratorController() {
		return this.ui;
	}
	
	/**
	 * Phone Number
	 */
	protected String phoneNumber;
	
	/**
	 * Set Phone Number
	 * @param phoneNumber Phone Number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * Get Phone Number
	 * @returnPhone Number
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	/**
	 * Hospital ID
	 */
	protected String hospitalId; 

	/**
	 * Set Hospital ID
	 * @param hospitalId Hospital ID
	 */
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	
	/**
	 * Get Hospital ID
	 * @return Hospital ID
	 */
	public String getHospitalId() {
		return this.hospitalId;
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
	
	/**
	 * Upload document
	 * @return
	 */
	public boolean upload() {
		String document = toString();
		LOG.debug("document: %s", document);
		if (ResourceMapperProperties.getPublishURL() != null) {
			try {
				LOG.debug("url: %s", ResourceMapperProperties.getPublishURL());
			    URL url = new URL(ResourceMapperProperties.getPublishURL());
			    URLConnection connection = url.openConnection();
			    LOG.debug("Connection opened...");
			    connection.setDoInput(true);
			    connection.setDoOutput(true);
			    connection.setUseCaches(false);
			    connection.setDefaultUseCaches(false);
			    connection.setRequestProperty ("Content-Type", this.getContentType());
			    LOG.debug("Writer opened...");
			    Writer writer = new OutputStreamWriter(connection.getOutputStream());
			    try {
			    	 writer.write(document);
			    	 writer.flush(); 	
			    }
			    finally {
			    	writer.close();	
			    	LOG.debug("...Writer closed.");
			    }		 
			    LOG.debug("Reader opened...");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    try {
			    	String inputLine = null;
			    	StringBuilder response = new StringBuilder();
			        while ((inputLine = reader.readLine()) != null) {
			        	LOG.debug(inputLine);
			        	response.append(inputLine);
			        }
				    LOG.debug("Response: %s", response.toString());
			    }
			    finally {
			    	reader.close();
			    	LOG.debug("...Reader closed.");
			    }
			    return true;
			}
			catch (Exception ex) {
				LOG.error("UploadException");
				ex.printStackTrace();
				return false;
			}
		}
		else {
			LOG.debug("No Publish URL Specified");
		}
		return true;
	}
	
}