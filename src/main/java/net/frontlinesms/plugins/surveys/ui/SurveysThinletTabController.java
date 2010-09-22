package net.frontlinesms.plugins.surveys.ui;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysPluginController;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;

import org.springframework.context.ApplicationContext;

/**
 * SurveysThinletTabController
 * @author dalezak
 *
 */
public class SurveysThinletTabController implements ThinletUiEventHandler, SurveysCallback {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveysThinletTabController.class);
	
	private static final String XML_PATH = "/ui/plugins/surveys/mainTab.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainTab;
	private final Object mainPanel;
	private final Object listTasks;
	private final ManageContactsPanelHandler panelManageContacts;
	private final ManageQuestionsPanelHandler panelManagQuestions;
	private final BrowseDataPanelHandler panelBrowseData;
	private final ManageOptionsPanelHandler panelManageOptions;
	private final ManageSurveysPanelHandler panelManageSurveys;
	
	public SurveysThinletTabController(FrontlineSMS frontlineController, UiGeneratorController uiController, ApplicationContext appContext, SurveysPluginController pluginController) {
		LOG.debug("SurveysThinletTabController");
		this.ui = uiController;
		this.appContext = appContext;
		this.mainTab = this.ui.loadComponentFromFile(XML_PATH, this);
		this.mainPanel = this.ui.find(this.mainTab, "panelMainContent");
		this.listTasks = this.ui.find(this.mainTab, "listTasks");
		this.panelManageContacts = new ManageContactsPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelManagQuestions = new ManageQuestionsPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelManageOptions = new ManageOptionsPanelHandler(this.ui, this.appContext, this);
		this.panelManageSurveys = new ManageSurveysPanelHandler(this.ui, this.appContext, this, frontlineController, pluginController);
		
		taskChanged(this.listTasks);
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	public void taskChanged(Object listTasks) {
		Object selectedListItem = this.ui.getSelectedItem(listTasks);
		String selectedProperty = this.ui.getProperty(selectedListItem, "value").toString();
		LOG.debug("taskChanged: " + selectedProperty);
		if ("questions".equalsIgnoreCase(selectedProperty)) {
			showManageQuestionsPanel();
		}
		else if ("people".equalsIgnoreCase(selectedProperty)) {
			showManageContactsPanel();
		}
		else if ("data".equalsIgnoreCase(selectedProperty)) {
			showBrowseDataPanel(null, null);
		}
		else if ("options".equalsIgnoreCase(selectedProperty)) {
			showManageOptionsPanel();
		}
		else if ("surveys".equalsIgnoreCase(selectedProperty)) {
			showManageSurveysPanel();
		}
	}
	
	public void viewAnswers(Contact contact) {
		LOG.debug("viewAnswers by contact");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(contact, null);
	}
	
	public void viewAnswers(Question question) {
		LOG.debug("viewAnswers by question");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(null, question);
	}
	
	public void refreshContact(Contact contact) {
		LOG.debug("refreshContact: %s", contact);
		this.panelManageContacts.refreshContacts(contact);
	}
	
	public void refreshQuestion(Question question) {
		LOG.debug("refreshQuestion: %s", question);
		this.panelManagQuestions.refreshQuestions(question);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshAnswer(Answer answer) {
		LOG.debug("refreshAnswer: %s", answer);
		this.panelBrowseData.refreshAnswers(answer);
	}
	
	public void refreshSurvey(Survey survey) {
		LOG.debug("refreshSurvey: %s", survey);
		this.panelManageSurveys.reloadData();
	}
	
	private void showBrowseDataPanel(Contact contact, Question question) {
		this.ui.removeAll(this.mainPanel);
		this.panelBrowseData.loadContacts();
		this.panelBrowseData.setSelectedContact(contact);
		this.panelBrowseData.setSelectedQuestion(question);
		this.ui.add(this.mainPanel, this.panelBrowseData.getMainPanel());
	}
	
	private void showManageContactsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageContacts.getMainPanel());
		this.panelManageContacts.reloadData();
	}
	
	private void showManageQuestionsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManagQuestions.getMainPanel());
		this.panelManagQuestions.reloadData();
	}
	
	private void showManageSurveysPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageSurveys.getMainPanel());
		this.panelManageSurveys.reloadData();
	}
	
	private void showManageOptionsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageOptions.getMainPanel());
	}
}
