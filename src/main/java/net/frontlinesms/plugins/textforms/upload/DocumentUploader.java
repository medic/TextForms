package net.frontlinesms.plugins.textforms.upload;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
import thinlet.ThinletText;

import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * DocumentUploader
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public abstract class DocumentUploader implements ThinletUiEventHandler {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(DocumentUploader.class);
	
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
	protected String organizationId; 

	/**
	 * Set Hospital ID
	 * @param organizationId Hospital ID
	 */
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	/**
	 * Get Hospital ID
	 * @return Hospital ID
	 */
	public String getOrganizationId() {
		return this.organizationId;
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
	 * @return true if successful
	 */
	public boolean upload() {
		String document = toString();
		if (TextFormsProperties.getPublishURL() != null) {
			LOG.debug("url: %s document: %s", TextFormsProperties.getPublishURL(), document);
			try {
				new ThreadedUploader(TextFormsProperties.getPublishURL(), this.getContentType(), document).run();
				return true;
			} 
			catch (MalformedURLException e) {
				LOG.error("MalformedURLException: %s", e);
			}
		}
		else {
			LOG.debug("No Document Uploader URL Specified");
		}
		return false;
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