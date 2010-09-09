package net.frontlinesms.plugins.surveys.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Color;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.surveys.search.HospitalContactQueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageContactsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageContactsPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ManageContactsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/surveys/manageContactsPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainPanel;
	private ManageContactsDialogHandler editDialog;
	private SurveysCallback callback;
	
	private final Object panelContacts;
	private final Object tableContacts;
	
	private final Object searchContact;
	private final Object buttonEditContact;
	private final Object buttonDeleteContact;
	private final Object buttonViewAnswers;
	
	private final Object labelNameValue;
	private final Object labelHospitalValue;
	private final Object labelPhoneValue;
	private final Object labelEmailValue;
	private final Object labelAnswerValue;
	
	private final HospitalContactQueryGenerator queryGenerator;
	private final PagedAdvancedTableController tableController;
	
	private final HospitalContactDao hospitalContactDao;

	public ManageContactsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) {
		LOG.debug("ManageContactsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.editDialog = new ManageContactsDialogHandler(this.ui, this.appContext, callback);
		this.tableContacts = this.ui.find(this.mainPanel, "tableContacts");
		this.panelContacts = this.ui.find(this.mainPanel, "panelContacts");
		this.buttonEditContact = this.ui.find(this.mainPanel, "buttonEditContact");
		this.buttonDeleteContact = this.ui.find(this.mainPanel, "buttonDeleteContact");
		this.buttonViewAnswers = this.ui.find(this.mainPanel, "buttonViewAnswers");
		this.searchContact = this.ui.find(this.mainPanel, "searchContact");
		
		this.labelNameValue = this.ui.find(this.mainPanel, "labelNameValue");
		this.labelHospitalValue = this.ui.find(this.mainPanel, "labelHospitalValue");
		this.labelPhoneValue = this.ui.find(this.mainPanel, "labelPhoneValue");
		this.labelEmailValue = this.ui.find(this.mainPanel, "labelEmailValue");
		this.labelAnswerValue = this.ui.find(this.mainPanel, "labelAnswerValue");
		
		this.hospitalContactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableContacts, this.panelContacts);
		this.tableController.putHeader(HospitalContact.class, 
									   new String[]{getI18NString(SurveysConstants.TABLE_NAME), 
													getI18NString(SurveysConstants.TABLE_ORGANIZATION), 										
													getI18NString(SurveysConstants.TABLE_PHONE), 
													getI18NString(SurveysConstants.TABLE_ANSWER)}, 
									   new String[]{"getName", "getHospitalId", "getPhoneNumber", "getLastAnswerText"},
									   new String[]{"/icons/user.png", "/icons/organization.png", "/icons/phone_number.png", "/icons/date.png"},
									   new String []{"name", "hospitalId", "phoneNumber", "lastAnswer"});
		this.queryGenerator = new HospitalContactQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(SurveysConstants.TABLE_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(SurveysConstants.TABLE_TO), 
											  getI18NString(SurveysConstants.TABLE_OF));
		this.queryGenerator.startSearch("");
		focusLost(this.searchContact);
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void reloadData() {
		this.queryGenerator.refresh();
	}
	public void addContact(Object tableContacts) {
		LOG.debug("addContact");
		this.editDialog.show(null);
	}
	
	public void editContact(Object tableContacts) {
		this.editDialog.show(this.getSelectedContact());
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this, SurveysConstants.CONFIRM_DELETE_CONTACT);
	}
	
	public void deleteContact() {
		LOG.debug("deleteContact");
		HospitalContact contact = this.getSelectedContact();
		if (contact != null) {
			this.hospitalContactDao.deleteHospitalContact(contact);
		}
		this.ui.removeConfirmationDialog();
		this.refreshContacts(null);
	}
	
	public void refreshContacts(HospitalContact contact) {
		String searchText = this.ui.getText(this.searchContact);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchContacts())) {
			this.queryGenerator.startSearch("");
		}
		else {
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	public void searchByContact(Object searchContact, Object tableContacts) {
		String searchText = this.ui.getText(searchContact);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchContacts())) {
			this.queryGenerator.startSearch("");
		}
		else {
			LOG.debug("searchByContact: %s", searchText);
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	public void focus(Object component) {
		LOG.debug("focus");
		if (component != null) {
			this.ui.requestFocus(component);
		}
	}
	
	public void viewAnswers(Object tableContacts) {
		LOG.debug("viewAnswers");
		if (this.callback != null) {
			this.callback.viewAnswers(this.getSelectedContact());
		}	
	}
	
	private HospitalContact getSelectedContact() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableContacts);
		if (selectedRow != null) {
			return (HospitalContact)this.ui.getAttachedObject(selectedRow, HospitalContact.class);
		}
		return null;
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		this.editDialog.show(this.getSelectedContact());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
		selectionChanged(null);
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		String searchText = this.ui.getText(this.searchContact);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
		HospitalContact contact = this.getSelectedContact();
		if (contact != null) {
			LOG.debug("contact: %s" + contact.getName());
			this.ui.setEnabled(this.buttonEditContact, true);
			this.ui.setEnabled(this.buttonDeleteContact, true);
			this.ui.setEnabled(this.buttonViewAnswers, true);
			this.ui.setText(this.labelNameValue, contact.getName());
			this.ui.setText(this.labelHospitalValue, contact.getHospitalId());
			this.ui.setText(this.labelPhoneValue, contact.getPhoneNumber());
			this.ui.setText(this.labelEmailValue, contact.getEmailAddress());
			if (contact.getLastAnswer() != null) {
				this.ui.setText(this.labelAnswerValue, contact.getLastAnswer().toString());
			}
			else {
				this.ui.setText(this.labelAnswerValue, "");
			}
		}
		else {
			this.ui.setEnabled(this.buttonEditContact, false);
			this.ui.setEnabled(this.buttonDeleteContact, false);
			this.ui.setEnabled(this.buttonViewAnswers, false);
			this.ui.setText(this.labelNameValue, "");
			this.ui.setText(this.labelHospitalValue, "");
			this.ui.setText(this.labelPhoneValue, "");
			this.ui.setText(this.labelEmailValue, "");
			this.ui.setText(this.labelAnswerValue, "");
		}
	}
	
	public void focusGained(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchContacts())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void focusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, SurveysMessages.getMessageSearchContacts());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}
}
