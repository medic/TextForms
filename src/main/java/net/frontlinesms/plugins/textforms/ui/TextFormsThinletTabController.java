package net.frontlinesms.plugins.textforms.ui;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsPluginController;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;

import org.springframework.context.ApplicationContext;

/**
 * TextFormsThinletTabController
 * @author dalezak
 *
 */
public class TextFormsThinletTabController implements ThinletUiEventHandler, TextFormsCallback {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormsThinletTabController.class);
	
	private static final String XML_PATH = "/ui/plugins/textforms/mainTab.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainTab;
	private final Object mainPanel;
	private final Object listTasks;
	private final ManageContactsPanelHandler panelManageContacts;
	private final ManageQuestionsPanelHandler panelManagQuestions;
	private final BrowseDataPanelHandler panelBrowseData;
	private final ManageOptionsPanelHandler panelManageOptions;
	private final ManageTextFormsPanelHandler panelManageTextForms;
	
	public TextFormsThinletTabController(FrontlineSMS frontlineController, UiGeneratorController uiController, ApplicationContext appContext, TextFormsPluginController pluginController) {
		LOG.debug("TextFormsThinletTabController");
		this.ui = uiController;
		this.appContext = appContext;
		this.mainTab = this.ui.loadComponentFromFile(XML_PATH, this);
		this.mainPanel = this.ui.find(this.mainTab, "panelMainContent");
		this.listTasks = this.ui.find(this.mainTab, "listTasks");
		this.panelManageContacts = new ManageContactsPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelManagQuestions = new ManageQuestionsPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this, frontlineController);
		this.panelManageOptions = new ManageOptionsPanelHandler(this.ui, this.appContext, this);
		this.panelManageTextForms = new ManageTextFormsPanelHandler(this.ui, this.appContext, this, frontlineController, pluginController);
		
		taskChanged(this.listTasks);
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	public void showHelpPage(String page) {
		ui.showHelpPage(page);
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
		else if ("textforms".equalsIgnoreCase(selectedProperty)) {
			showManageTextFormsPanel();
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
	
	public void refreshTextForm(TextForm textform) {
		LOG.debug("refreshTextForm: %s", textform);
		this.panelManageTextForms.reloadData();
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
	
	private void showManageTextFormsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageTextForms.getMainPanel());
		this.panelManageTextForms.reloadData();
	}
	
	private void showManageOptionsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageOptions.getMainPanel());
	}
}
