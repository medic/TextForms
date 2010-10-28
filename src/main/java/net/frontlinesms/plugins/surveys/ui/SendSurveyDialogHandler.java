package net.frontlinesms.plugins.surveys.ui;

import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.surveys.SurveysListener;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysPluginController;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.repository.SurveyResponseDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * SendSurveyDialogHandler
 * @author dalezak
 *
 */
@SuppressWarnings("unused")
public class SendSurveyDialogHandler extends ExtendedThinlet implements ThinletUiEventHandler {

	private static final long serialVersionUID = 1L;
	private static final SurveysLogger LOG = SurveysLogger.getLogger(SendSurveyDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/surveys/sendSurveyDialog.xml";
	
	private final FrontlineSMS frontline;
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final SurveysPluginController pluginController;
	
	private Survey survey;
	private final ContactDao contactDao;
	private final SurveyResponseDao surveyResponseDao;
	
	private final Object mainDialog;
	private final Object tblContacts;
	private final Object btnSend;
	
	public SendSurveyDialogHandler(FrontlineSMS frontlineController, UiGeneratorController ui, ApplicationContext appContext, SurveysPluginController pluginController) { 
		LOG.debug("SendSurveyDialogHandler");
		this.frontline = frontlineController;
		this.ui = ui;
		this.appContext = appContext;
		this.pluginController = pluginController;
		
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.contactDao = frontlineController.getContactDao();
		this.surveyResponseDao = pluginController.getSurveyResponseDao();
		
		this.tblContacts = this.ui.find(this.mainDialog, "tblContacts");
		this.btnSend = this.ui.find(this.mainDialog, "btnSend");
	}
	
	public void show(Survey survey) {
		LOG.debug("show");
		this.survey = survey;
		ui.removeAll(tblContacts);
		for (Contact contact : contactDao.getAllContacts()) {
			ui.add(tblContacts, getRow(contact));
		}
		ui.setEnabled(btnSend, false);
		ui.add(mainDialog);
	}
	
	public void sendSurvey(Object tblContacts) {
		LOG.debug("sendSurvey");
		for(Object selectedItem : ui.getSelectedItems(tblContacts)) {
			Contact contact = ui.getAttachedObject(selectedItem, Contact.class);
			SurveyResponse surveyResponse = new SurveyResponse();
			surveyResponse.setStarted(new Date());
			surveyResponse.setContact(contact);
			surveyResponse.setSurvey(survey);
			try {
				surveyResponseDao.saveSurvey(surveyResponse);
				LOG.debug("SurveyResponse Created: %s", surveyResponse.getSurveyName());
				Question question = surveyResponse.getNextQuestion();
				if (question != null) {
					SurveysListener.registerSurvey(contact.getPhoneNumber(), surveyResponse, question);
					frontline.sendTextMessage(contact.getPhoneNumber(), question.toString(true));
					LOG.out("%s", question.toString(true));
				}
				else {
					LOG.error("Questions is NULL");
				}
			} 
			catch (DuplicateKeyException ex) {
				LOG.error("DuplicateKeyException %s", ex);
			}
		}
		this.ui.remove(mainDialog);
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void contactsChanged(Object tblContacts) {
		ui.setEnabled(btnSend, ui.getSelectedItems(tblContacts).length > 0);
	}
	
	public Object getRow(Contact contact){
		Object row = this.ui.createTableRow(contact);
		this.createTableCell(row, contact.getName());
		this.createTableCell(row, contact.getPhoneNumber());
		return row;
	}
}