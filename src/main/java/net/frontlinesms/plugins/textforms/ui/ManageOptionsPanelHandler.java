package net.frontlinesms.plugins.textforms.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.QuestionFactory;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

/**
 * ManageOptionsPanelHandler
 * @author dalezak
 *
 */
@SuppressWarnings("unused")
public class ManageOptionsPanelHandler implements ThinletUiEventHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageContactsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/textforms/manageOptionsPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsCallback callback;
	
	private final Object mainPanel;
	private final Object comboUploadDocuments;
	private final Object listBooleanTrue;
	private final Object listBooleanFalse;
	private final Object listInfo;
	private final Object listRegister;
	
	private final Object textUploadURL;
	private final Object checkboxDebugYes;
	private final Object checkboxDebugNo;
	private final Object panelUploadOptions;
	private Object createFieldsButton;
	private Object creationSuccessfulLabel;
	
	/**
	 * QuestionDao
	 */
	private QuestionDao questionDao;
	
	private QuestionDao getQuestionDao() {
		if (this.questionDao == null) {
			this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		}
		return this.questionDao;
	}
	
	/**
	 * TextFormDao
	 */
	private TextFormDao textformDao;
	
	private TextFormDao getTextFormDao() {
		if (this.textformDao == null) {
			this.textformDao = (TextFormDao) appContext.getBean("textformDao", TextFormDao.class);
		}
		return this.textformDao;
	}
	
	public ManageOptionsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback) {
		LOG.debug("ManageOptionsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.comboUploadDocuments = this.ui.find(this.mainPanel, "comboUploadDocuments");	
		this.listBooleanTrue = this.ui.find(this.mainPanel, "listBooleanTrue");	
		this.listBooleanFalse = this.ui.find(this.mainPanel, "listBooleanFalse");	
		this.listInfo = this.ui.find(this.mainPanel, "listInfo");	
		this.listRegister = this.ui.find(this.mainPanel, "listRegister");	
		this.textUploadURL = this.ui.find(this.mainPanel, "textUploadURL");	
		this.checkboxDebugYes = this.ui.find(this.mainPanel, "checkboxDebugYes");	
		this.checkboxDebugNo = this.ui.find(this.mainPanel, "checkboxDebugNo");	
		this.panelUploadOptions = this.ui.find(this.mainPanel, "panelUploadOptions");	
		this.createFieldsButton = this.ui.find(this.mainPanel,"createFieldsButton");
		this.creationSuccessfulLabel = this.ui.find(this.mainPanel,"fieldCreationSuccessLabel");
		if(TextFormsProperties.areResourceFinderQuestionsGenerated()){
			ui.setVisible(createFieldsButton, false);
		}
		loadDebugMode();
		loadUploadURL();
		loadBooleanValues();
		loadInfoValues();
		loadRegisterValues();
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	private void loadUploadURL() {
		this.ui.setText(this.textUploadURL, TextFormsProperties.getPublishURL());
	}
	
	private void loadDebugMode() {
		if (TextFormsProperties.isDebugMode()) {
			this.ui.setSelected(this.checkboxDebugYes, true);
			this.ui.setSelected(this.checkboxDebugNo, false);
		}
		else {
			this.ui.setSelected(this.checkboxDebugYes, false);
			this.ui.setSelected(this.checkboxDebugNo, true);
		}
	}
	
	private void loadBooleanValues() {
		loadListOptions(this.listBooleanTrue, TextFormsProperties.getBooleanTrueValues());
		loadListOptions(this.listBooleanFalse, TextFormsProperties.getBooleanFalseValues());
	}
	
	private void loadInfoValues() {
		loadListOptions(this.listInfo, TextFormsProperties.getInfoKeywords());
	}
	
	private void loadRegisterValues() {
		loadListOptions(this.listRegister, TextFormsProperties.getRegisterKeywords());
	}
	
	public void uploadUrlChanged(Object textUploadURL) {
		String url = this.ui.getText(textUploadURL);
		LOG.debug("uploadUrlChanged: %s", url);
		TextFormsProperties.setPublishURL(url);
	}
	
	public void createFieldsClicked(){
		
	}
	
	//################ DEBUG ################
	
	public void debugChanged(Object checkboxDebug) {
		if (checkboxDebug == this.checkboxDebugYes) {
			TextFormsProperties.setDebugMode(true);
		}
		else if (checkboxDebug == this.checkboxDebugNo) {
			TextFormsProperties.setDebugMode(false);
		}
	}
	
	//################ COMMON ################
	
	public void textOptionChanged(Object textQuestion, Object list, Object button) {
		if (this.ui.getText(textQuestion).length() > 0) {
			this.ui.setEnabled(button, true);
		}
		else {
			this.ui.setEnabled(button, false);
		}
	}
	
	public void optionChanged(Object list, Object button) {
		if (this.ui.getSelectedItem(list) != null) {
			this.ui.setEnabled(button, true);
		}
		else {
			this.ui.setEnabled(button, false);
		}
	}
	
	public void addOption(Object textQuestion, Object list, Object button) {
		String text = this.ui.getText(textQuestion);
		if (text != null && text.length() > 0) {
			this.ui.add(list, this.ui.createListItem(text, text));
			this.ui.setText(textQuestion, "");
			this.ui.setEnabled(button, false);
		}
		saveOptions(list);
	}
	
	public void deleteOption(Object textQuestion, Object list, Object button) {
		Object valueToDelete = this.ui.getSelectedItem(list);
		String valueText = this.ui.getText(valueToDelete);
		LOG.debug("Deleted: %s", valueText);
		this.ui.remove(valueToDelete);
		saveOptions(list);
		this.ui.setEnabled(button, false);
		if (this.ui.getItems(list).length == 0) {
			this.ui.alert(TextFormsMessages.getMessageChoiceRequired());
			this.ui.setFocus(textQuestion);
		}
	}
	
	private void saveOptions(Object list) {
		List<String> options = new ArrayList<String>();
		for (Object item : this.ui.getItems(list)) {
			options.add((String)this.ui.getAttachedObject(item));
		}
		String [] values = options.toArray(new String[options.size()]);
		if (list == this.listBooleanTrue) {
			TextFormsProperties.setBooleanTrueValues(values);
		}
		else if (list == this.listBooleanFalse) {
			TextFormsProperties.setBooleanFalseValues(values);
		}
		else if (list == this.listInfo) {
			TextFormsProperties.setInfoKeywords(values);
		}
		else if (list == this.listRegister) {
			TextFormsProperties.setRegisterKeywords(values);
		}
	}
	
	private void loadListOptions(Object list, String [] options) {
		for (String option : options) {
			this.ui.add(list, this.ui.createListItem(option, option));
		}
	}
	
	public boolean createResourceFinderQuestions() {
		try {
			//PLAIN TEXT
			Question title = createPlainTextQuestion("Hospital Title", "title", "What is the hospital's title?", "title");
			Question alt = createPlainTextQuestion("Hospital Alternative Title", "alt", "What is the hospital's alternative title?", "alt_title");
			Question contact = createPlainTextQuestion("Hospital Contact Name", "contact", "What is hospital contact name?", "contact_name");
			Question phone = createPlainTextQuestion("Hospital Phone", "phone", "What is hospital phone number?", "phone");
			Question email = createPlainTextQuestion("Hospital Email", "email", "What is hospital email address?", "email");
			Question department = createPlainTextQuestion("Hospital Department", "department", "What is the hospital department?", "department");
			Question district = createPlainTextQuestion("Hospital District", "district", "What is the hospital district?", "district");
			Question commune = createPlainTextQuestion("Hospital Commune", "commune", "What is the hospital commune?", "commune");
			Question address = createPlainTextQuestion("Hospital Address", "address", "What is hospital address?", "address");
			Question location = createPlainTextQuestion("Hospital Location", "location", "What is hospital location (latitude,longitude)?", "location");
			Question accuracy = createPlainTextQuestion("Hospital Location Accuracy", "accuracy", "What is hospital location accuracy?", "accuracy");
			Question damage = createPlainTextQuestion("Hospital Damage", "damage", "What is the hospital damage?", "damage");
			Question comments = createPlainTextQuestion("Additional Comments", "comments", "Additional comments?", "comments");
			//INTEGER
			Question available = createIntegerQuestion("Available Beds", "available", "How many beds does the hospital have available?", "available_beds");
			Question beds = createIntegerQuestion("Total Beds", "beds", "The total number of hospital beds?", "total_beds");
			//BOOLEAN
			Question reachable = createBooleanQuestion("Hospital Reachable By Road", "reachable", "Is the hospital reachable by road?", "reachable_by_road");
			Question pickup = createBooleanQuestion("Hospital Can Pick Up Patients", "pickup", "Can the hospital pick up patients?", "can_pick_up_patients");
			//MULTICHOICE
			Question type = createMultiChoiceQuestion("Hospital Type", "type", "What is the hospital type?", "organization_type",
										new String [] {"PUBLIC", "FOR_PROFIT", "UNIVERSITY", "COMMUNITY", "NGO", "FAITH_BASED", "MILITARY", "MIXED"});
			Question category = createMultiChoiceQuestion("Hospital Category", "category", "What is the hospital category?", "category",
										new String [] {"HOSPITAL", "CLINIC", "MOBILE_CLINIC", "DISPENSARY"});
			Question construction = createMultiChoiceQuestion("Hospital Construction", "construction", "What is the hospital construction?", "construction",
										new String [] {"REINFORCED_CONCRETE", "UNREINFORCED_MASONRY", "WOOD_FRAME", "ADOBE"});
			Question status = createMultiChoiceQuestion("Hospital Operational Status", "status", "What is the hospital operational status?", "operational_status",
										new String [] {"OPERATIONAL", "NO_SURGICAL_CAPACITY", "FIELD_HOSPITAL", "FIELD_WITH_HOSPITAL", "CLOSED_OR_CLOSING"});
			//CHECKLIST
			Question services = createChecklistQuestion("Hospital Services", "services", "What services does the hospital offer?", "services", 
									new String [] {"GENERAL_SURGERY", "ORTHOPEDICS", "NEUROSURGERY", "VASCULAR_SURGERY", 
												   "INTERNAL_MEDICINE", "CARDIOLOGY", "INFECTIOUS_DISEASE", "PEDIATRICS", 
												   "POSTOPERATIVE_CARE", "REHABILITATION", "OBSTETRICS_GYNECOLOGY", "MENTAL_HEALTH",
												   "DIALYSIS", "LAB", "X_RAY", "CT_SCAN", "BLOOD_BANK", "MORTUARY_SERVICES"});
			//TEXTFORM
			TextForm contactForm = new TextForm("Hospital Contact", "hosp_contact", Arrays.asList(title, contact, phone, email));
			this.getTextFormDao().saveTextForm(contactForm);
			
			TextForm locationForm = new TextForm("Hospital Location", "hosp_location", Arrays.asList(address, district, location, accuracy));
			this.getTextFormDao().saveTextForm(locationForm);
			
			TextForm statusForm = new TextForm("Hospital Status", "hosp_status", Arrays.asList(status, reachable, damage, pickup, beds, available, comments));
			this.getTextFormDao().saveTextForm(statusForm);
			
			TextForm typeForm = new TextForm("Hospital Type", "hosp_type", Arrays.asList(type, category, construction, services));
			this.getTextFormDao().saveTextForm(typeForm);
			TextFormsProperties.setResourceFinderQuestionsGenerated(true);
			ui.setVisible(createFieldsButton, false);
			ui.setVisible(creationSuccessfulLabel, true);
			return true;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	protected Question createPlainTextQuestion(String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.PLAINTEXT, schema, null);
	}
	
	protected Question createDateQuestion(String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.DATE, schema, null);
	}
	
	protected Question createIntegerQuestion(String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.INTEGER, schema, null);
	}
	
	protected Question createBooleanQuestion(String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.BOOLEAN, schema, null);
	}
	
	protected Question createMultiChoiceQuestion(String name, String keyword, String infoSnippet, String schema, String [] choices) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.MULTICHOICE, schema, choices);
	}
	
	protected Question createChecklistQuestion(String name, String keyword, String infoSnippet, String schema, String [] choices) {
		return createQuestion(name, keyword, infoSnippet, QuestionType.CHECKLIST, schema, choices);
	}
	
	protected Question createQuestion(String name, String keyword, String infoSnippet, String type, String schema, String [] choices) {
		try {
			List<String> choiceList = choices != null ? Arrays.asList(choices) : null;
			Question question = QuestionFactory.createQuestion(name, keyword, infoSnippet, type, schema, choiceList);
			this.getQuestionDao().saveQuestion(question);
			LOG.debug("Question Created [%s, %s, %s, %s]", question.getName(), question.getKeyword(), question.getType(), question.getSchemaName());
			return question;
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Question Exists [%s, %s, %s, %s]", name, keyword, type, schema);
			Question question = this.getQuestionDao().getQuestionForKeyword(keyword);
			return question;
		}
	}
	
}