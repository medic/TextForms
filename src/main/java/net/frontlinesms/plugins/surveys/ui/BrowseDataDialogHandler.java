package net.frontlinesms.plugins.surveys.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;
import net.frontlinesms.plugins.surveys.data.repository.AnswerFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * BrowseDataDialogHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class BrowseDataDialogHandler implements ThinletUiEventHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(BrowseDataDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/surveys/browseDataDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final SurveysCallback callback;
	
	private final Object mainDialog;
	
	@SuppressWarnings("unchecked")
	private Answer answer;
	private final AnswerDao answerDao;
	private final QuestionDao questionDao;
	private final ContactDao contactDao;
	private final MessageDao messageDao;
	
	private final Object comboQuestionTypes;
	private final Object comboContact;
	private final Object textAnswer;
	private final Object textDate;
	private final Object textHospital;
	
	public BrowseDataDialogHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) { 
		LOG.debug("BrowseDataDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.answerDao = (AnswerDao) appContext.getBean("answerDao", AnswerDao.class);
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		this.contactDao = (ContactDao)appContext.getBean("contactDao", ContactDao.class);
		this.messageDao = (MessageDao)appContext.getBean("messageDao", MessageDao.class);
		
		this.comboQuestionTypes = this.ui.find(this.mainDialog, "comboQuestionTypes");
		this.comboContact = this.ui.find(this.mainDialog, "comboContact");
		this.textAnswer = this.ui.find(this.mainDialog, "textAnswer");
		this.textDate = this.ui.find(this.mainDialog, "textDate");
		this.textHospital = this.ui.find(this.mainDialog, "textHospital");
	}
	
	public void loadContacts() {
		this.ui.removeAll(this.comboContact);
		this.ui.add(this.comboContact, this.ui.createComboboxChoice("", null));
		for (Contact contact : this.contactDao.getAllContacts()) {
			Object comboboxChoice = this.ui.createComboboxChoice(contact.getName(), contact);
			this.ui.setIcon(comboboxChoice, "/icons/user.png");
			this.ui.add(this.comboContact, comboboxChoice);
		}
	}
	
	public void loadQuestions() {
		this.ui.removeAll(this.comboQuestionTypes);
		this.ui.add(this.comboQuestionTypes, this.ui.createComboboxChoice("", null));
		for (Question questionClass : this.questionDao.getAllQuestions()) {
			String questionDisplayName = String.format("%s : %s (%s)", questionClass.getName(), questionClass.getKeyword(), questionClass.getTypeLabel());
			Object comboBoxChoice = this.ui.createComboboxChoice(questionDisplayName, questionClass);
			this.ui.setIcon(comboBoxChoice, "/icons/keyword.png");
			this.ui.add(this.comboQuestionTypes, comboBoxChoice);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void show(Answer answer) {
		this.answer = answer;
		this.ui.setSelectedIndex(this.comboContact, 0);
		this.ui.setSelectedIndex(this.comboQuestionTypes, 0);
		if (answer != null) {
			this.ui.setText(this.textDate, answer.getDateSubmittedText());
			this.ui.setText(this.textAnswer, answer.getMessageText());
			this.setSelectedContact(answer.getContact());
			this.setSelectedQuestion(answer.getQuestion());
			contactChanged(this.comboContact, this.textHospital);
			for (int index = 0; index < this.ui.getCount(this.comboQuestionTypes); index++) {
				Object comboTypeItem = this.ui.getItem(this.comboQuestionTypes, index);
				Question question = (Question)this.ui.getAttachedObject(comboTypeItem);
				if (question != null) {
					if (answer.getQuestionType().equalsIgnoreCase(question.getType())) {
						this.ui.setSelectedIndex(this.comboQuestionTypes, index);
						break;
					}			
				}		
			}
			if (answer.getMessage() != null) {
				this.ui.setEnabled(this.textAnswer, false);
				this.ui.setEditable(this.textAnswer, false);
			}
			else {
				this.ui.setEnabled(this.textAnswer, true);
				this.ui.setEditable(this.textAnswer, true);
			}
		}
		else {
			this.ui.setText(this.textDate, "");
			this.ui.setText(this.textAnswer, "");
			this.ui.setEnabled(this.textAnswer, true);
			this.ui.setEditable(this.textAnswer, true);
			this.ui.setSelectedIndex(this.comboContact, 0);
			this.ui.setSelectedIndex(this.comboQuestionTypes, 0);
		}
		this.ui.add(this.mainDialog);
	}
	
	private void setSelectedContact(Contact contact) {
		LOG.debug("setSelectedContact: %s", contact);
		if (contact != null) {
			int index = 0;
			for (Object comboboxChoice : this.ui.getItems(this.comboContact)) {
				Object attachedObject = this.ui.getAttachedObject(comboboxChoice);
				if (attachedObject != null) {
					Contact contactItem = (Contact)attachedObject;
					if (contact.equals(contactItem)) {
						this.ui.setSelectedIndex(this.comboContact, index);
						LOG.debug("Selecting Contact: %s", contact.getName());
						break;
					}
				}
				index++;
			}
		}
		else {
			this.ui.setSelectedIndex(this.comboContact, 0);
		}
	}
	
	private void setSelectedQuestion(Question question) {
		for (int index = 0; index < this.ui.getCount(this.comboQuestionTypes); index++) {
			Object comboTypeItem = this.ui.getItem(this.comboQuestionTypes, index);
			Object attachedObject = this.ui.getAttachedObject(comboTypeItem);
			if (attachedObject != null) {
				if (answer.getQuestion().getType().equalsIgnoreCase(attachedObject.toString())) {
					this.ui.setSelectedIndex(this.comboQuestionTypes, index);
					break;
				}			
			}		
		}
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void showDateSelecter(Object textQuestion) {
		LOG.debug("showDateSelecter");
		this.ui.showDateSelecter(textQuestion);
	}
	
	@SuppressWarnings("unchecked")
	public void saveAnswer(Object dialog) throws DuplicateKeyException {
		LOG.debug("saveAnswer");
		Date dateSubmitted = this.getDateSubmitted();
		String response = this.ui.getText(this.textAnswer);
		String organizationId = this.ui.getText(this.textHospital);
		Contact contact = this.getContact();
		Question question = this.getQuestion();
		if (contact == null) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_RESPONSE_SUBMITTER));
		}
		else if (question == null) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_RESPONSE_FIELD));
		}
		else if (dateSubmitted == null) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_RESPONSE_DATE));
		}
		else if (response == null || response.length() == 0) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_RESPONSE_TEXT));
		}
		else if (this.answer != null) {
			this.answer.setDateSubmitted(dateSubmitted);
			this.answer.setContact(contact);
			this.answer.setOrganizationId(organizationId);
			if (this.answer.getMessage() == null) {
				FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateSubmitted.getTime(), contact.getPhoneNumber(), null, response);
				this.messageDao.saveMessage(frontlineMessage);
				this.answer.setMessage(frontlineMessage);
				LOG.debug("FrontlineMessage Created!");
			}
			this.answerDao.updateAnswer(this.answer);
			this.callback.refreshAnswer(this.answer);
			this.ui.remove(dialog);
			LOG.debug("Answer Updated!");
		}
		else {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateSubmitted.getTime(), Long.toString(contact.getId()), null, response);
			this.messageDao.saveMessage(frontlineMessage);
			LOG.debug("FrontlineMessage Created!");
			
			Answer newAnswer = AnswerFactory.createAnswer(frontlineMessage, contact, dateSubmitted, organizationId, question);
			if (newAnswer != null) {
				LOG.debug("Answer Created!");
				this.answerDao.saveAnswer(newAnswer);
			}
			else {
				this.ui.alert(getI18NString(SurveysConstants.ALERT_ERROR_CREATE_RESPONSE));
			}
			this.callback.refreshAnswer(newAnswer);
			this.ui.remove(dialog);
		}
	}
	
	private Question getQuestion() {
		Object questionItem = this.ui.getSelectedItem(this.comboQuestionTypes);
		if (questionItem != null) {
			return (Question)this.ui.getAttachedObject(questionItem);
		}
		return null;
	}
	
	private Contact getContact() {
		Object contactItem = this.ui.getSelectedItem(this.comboContact);
		if (contactItem != null) {
			return (Contact)this.ui.getAttachedObject(contactItem);
		}
		return null;
	}
	
	private Date getDateSubmitted() {
		try {
			String dateString = this.ui.getText(this.textDate);
			if (dateString != null && dateString.length() > 0) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				return dateFormat.parse(dateString);	
			}
		} 
		catch (ParseException e) {
			//do nothing
		}
		return null;
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void contactChanged(Object comboContact, Object textHospital) {
		LOG.debug("contactChanged");
		Object contactItem = this.ui.getSelectedItem(this.comboContact);
		if (contactItem != null) {
			Contact contact = this.ui.getAttachedObject(contactItem, Contact.class);
			OrganizationDetails organizationsDetails = contact.getDetails(OrganizationDetails.class);
			if (organizationsDetails != null) {
				this.ui.setText(this.textHospital, organizationsDetails.getOrganizationId());
			}
			else {
				this.ui.setText(this.textHospital, "");
			}
		}
		else {
			this.ui.setText(this.textHospital, "");
		}
	}
	
}
