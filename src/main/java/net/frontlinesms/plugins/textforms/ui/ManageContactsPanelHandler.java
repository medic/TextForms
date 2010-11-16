package net.frontlinesms.plugins.textforms.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Color;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.textforms.search.ContactQueryGenerator;
import net.frontlinesms.plugins.textforms.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.textforms.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageContactsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageContactsPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageContactsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/textforms/manageContactsPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainPanel;
	private ManageContactsDialogHandler editDialog;
	private TextFormsCallback callback;
	
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
	
	private final ContactQueryGenerator queryGenerator;
	private final PagedAdvancedTableController tableController;
	
	private final ContactDao contactDao;

	public ManageContactsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback, FrontlineSMS frontlineController) {
		LOG.debug("ManageContactsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		frontlineController.getEventBus().registerObserver(this);
		
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
		
		this.contactDao = (ContactDao) appContext.getBean("contactDao", ContactDao.class);
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableContacts, this.panelContacts);
		this.tableController.putHeader(Contact.class, 
									   new String[]{getI18NString(TextFormsConstants.TABLE_NAME), 										
													getI18NString(TextFormsConstants.TABLE_PHONE),
													getI18NString(TextFormsConstants.TABLE_EMAIL)}, 
									   new String[]{"getName", "getPhoneNumber", "getEmailAddress"},
									   new String[]{"/icons/user.png", "/icons/phone_number.png", "/icons/email.png"},
									   new String []{"name", "phoneNumber", "emailAddress"});
		this.queryGenerator = new ContactQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(TextFormsConstants.TABLE_RESULTS), 
											   getI18NString(TextFormsConstants.TABLE_NO_RESULTS), 
											   getI18NString(TextFormsConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(TextFormsConstants.TABLE_TO), 
											  getI18NString(TextFormsConstants.TABLE_OF));
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
		this.ui.showConfirmationDialog(methodToBeCalled, this, TextFormsConstants.CONFIRM_DELETE_CONTACT);
	}
	
	public void deleteContact() {
		LOG.debug("deleteContact");
		Contact contact = this.getSelectedContact();
		if (contact != null) {
			this.contactDao.deleteContact(contact);
		}
		this.ui.removeConfirmationDialog();
		this.refreshContacts(null);
	}
	
	public void refreshContacts(Contact contact) {
		String searchText = this.ui.getText(this.searchContact);
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchContacts())) {
			this.queryGenerator.startSearch("");
		}
		else {
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	public void searchByContact(Object searchContact, Object tableContacts) {
		String searchText = this.ui.getText(searchContact);
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchContacts())) {
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
	
	private Contact getSelectedContact() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableContacts);
		if (selectedRow != null) {
			return (Contact)this.ui.getAttachedObject(selectedRow, Contact.class);
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
		Contact contact = this.getSelectedContact();
		if (contact != null) {
			LOG.debug("contact: %s" + contact.getName());
			this.ui.setEnabled(this.buttonEditContact, true);
			this.ui.setEnabled(this.buttonDeleteContact, true);
			this.ui.setEnabled(this.buttonViewAnswers, true);
			this.ui.setText(this.labelNameValue, contact.getName());
			this.ui.setText(this.labelPhoneValue, contact.getPhoneNumber());
			this.ui.setText(this.labelEmailValue, contact.getEmailAddress());
			OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
			if(details != null) {
				this.ui.setText(this.labelHospitalValue, details.getOrganizationId());
				this.ui.setText(this.labelAnswerValue, details.getLastAnswerText());
			}
			else {
				this.ui.setText(this.labelHospitalValue, "");
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
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchContacts())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void focusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, TextFormsMessages.getMessageSearchContacts());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}

	public void notify(FrontlineEventNotification notification) {
		if (notification instanceof DatabaseEntityNotification<?>) {
			DatabaseEntityNotification<?> databaseEntityNotification = (DatabaseEntityNotification<?>)notification;
			if (databaseEntityNotification.getDatabaseEntity() instanceof Contact) {
				this.queryGenerator.refresh();
			}
		}
	}
}
