package net.frontlinesms.plugins.surveys.upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
import thinlet.ThinletText;

import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * DocumentUploader
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public abstract class DocumentUploader implements ThinletUiEventHandler {

	private static SurveysLogger LOG = SurveysLogger.getLogger(DocumentUploader.class);
	
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
	protected abstract String getPanelXML();
	
	/**
	 * Collection of question responses
	 */
	private final List<Answer> answers = new ArrayList<Answer>();
	
	/**
	 * UiGeneratorController
	 */
	protected UiGeneratorController ui;
	
	/**
	 * ApplicationContext
	 */
	protected ApplicationContext appContext;
	
	/**
	 * Set ApplicationContext
	 * @param appContext ApplicationContext
	 */
	public void setApplicationContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}
	
	/**
	 * Get ApplicationContext
	 * @return ApplicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return this.appContext;
	}
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
	 * Add Answer
	 * @param answer
	 */
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
	}
	
	/**
	 * Get Answers
	 * @return list of Answers
	 */
	public List<Answer> getAnswers() {
		return this.answers;
	}
	
	/**
	 * Upload document
	 * @return
	 */
	public boolean upload() {
		String document = toString();
		if (SurveysProperties.getPublishURL() != null) {
			LOG.debug("url: %s document: %s", SurveysProperties.getPublishURL(), document);
			try {
				URL url = new URL(SurveysProperties.getPublishURL());
			    URLConnection connection = url.openConnection();
			    connection.setDoInput(true);
			    connection.setDoOutput(true);
			    connection.setUseCaches(false);
			    connection.setDefaultUseCaches(false);
			    connection.setRequestProperty ("Content-Type", this.getContentType());
			    Writer writer = new OutputStreamWriter(connection.getOutputStream());
			    try {
			    	 writer.write(document);
			    	 writer.flush(); 	
			    }
			    finally {
			    	writer.close();	
			    }		 
			    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    try {
			    	String inputLine = null;
			    	StringBuilder response = new StringBuilder();
			        while ((inputLine = reader.readLine()) != null) {
			        	LOG.debug(inputLine);
			        	response.append(inputLine);
			        }
				    LOG.debug("Answer: %s", response.toString());
			    }
			    finally {
			    	reader.close();
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
			LOG.debug("No Document Uploader URL Specified");
		}
		return true;
	}
	
	public Object getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = this.ui.loadComponentFromFile(this.getPanelXML(), this);
		}
		return this.mainPanel;
	}private Object mainPanel;
	
	protected final Object createTableCell(Object row, String text) {
		Object cell = Thinlet.create(ThinletText.CELL);
		this.ui.setString(cell, ThinletText.TEXT, text);
		this.ui.add(row, cell);
		return cell;
	}
}