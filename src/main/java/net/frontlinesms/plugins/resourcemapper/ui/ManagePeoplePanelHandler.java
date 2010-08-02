package net.frontlinesms.plugins.resourcemapper.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.search.HospitalContactQueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManagePeoplePanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManagePeoplePanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/managePeoplePanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private ManagePeopleDialogHandler editDialog;
	private ResourceMapperCallback callback;
	
	private Object panelPeople;
	private Object tablePeople;
	
	private Object searchPerson;
	private Object editButton;
	private Object deleteButton;
	private Object viewResponsesButton;
	
	private Object labelNameValue;
	private Object labelHospitalValue;
	private Object labelPhoneValue;
	private Object labelEmailValue;
	private Object labelResponseValue;
	
	private HospitalContactQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	private HospitalContactDao contactDao;
	
	public ManagePeoplePanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		LOG.debug("ManagePeoplePanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.editDialog = new ManagePeopleDialogHandler(this.ui, this.appContext, callback);
		this.tablePeople = this.ui.find(this.mainPanel, "tablePeople");
		this.panelPeople = this.ui.find(this.mainPanel, "panelPeople");
		this.editButton = this.ui.find(this.mainPanel, "buttonEditPerson");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeletePerson");
		this.viewResponsesButton = this.ui.find(this.mainPanel, "buttonViewResponses");
		this.searchPerson = this.ui.find(this.mainPanel, "searchPerson");
		
		this.labelNameValue = this.ui.find(this.mainPanel, "labelNameValue");
		this.labelHospitalValue = this.ui.find(this.mainPanel, "labelHospitalValue");
		this.labelPhoneValue = this.ui.find(this.mainPanel, "labelPhoneValue");
		this.labelEmailValue = this.ui.find(this.mainPanel, "labelEmailValue");
		this.labelResponseValue = this.ui.find(this.mainPanel, "labelResponseValue");
		
		this.contactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tablePeople, this.panelPeople);
		this.tableController.putHeader(HospitalContact.class, 
									   new String[]{getI18NString(ResourceMapperConstants.TABLE_NAME), 
													getI18NString(ResourceMapperConstants.TABLE_PHONE), 
													getI18NString(ResourceMapperConstants.TABLE_ORGANIZATION), 
													getI18NString(ResourceMapperConstants.TABLE_RESPONSE)}, 
									   new String[]{"getName", "getPhoneNumber", "getHospitalId", "getLastResponseText"},
									   new String[]{"/icons/user.png", "/icons/phone_number.png", "/icons/hospital.png", "/icons/date.png"},
									   new String []{"name", "phoneNumber", "hospitalId", "lastResponse"});
		this.queryGenerator = new HospitalContactQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(ResourceMapperConstants.TABLE_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(ResourceMapperConstants.TABLE_TO), 
											  getI18NString(ResourceMapperConstants.TABLE_OF));
		this.queryGenerator.startSearch("");
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void addPerson(Object tablePeople) {
		LOG.debug("addPerson");
		this.editDialog.show(null);
	}
	
	public void editPerson(Object tablePeople) {
		this.editDialog.show(this.getSelectedContact());
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this, ResourceMapperConstants.CONFIRM_DELETE_CONTACT);
	}
	
	public void deletePerson() {
		LOG.debug("deletePerson");
		HospitalContact contact = this.getSelectedContact();
		if (contact != null) {
			this.contactDao.deleteHospitalContact(contact);
		}
		this.ui.removeConfirmationDialog();
		this.refreshContacts(null);
	}
	
	public void refreshContacts(HospitalContact contact) {
		String searchText = this.ui.getText(this.searchPerson);
		this.queryGenerator.startSearch(searchText);
	}
	
	public void searchByPerson(Object searchPerson, Object tablePeople, Object buttonClear) {
		String searchText = this.ui.getText(searchPerson);
		LOG.debug("searchByPerson: %s", searchText);
		this.queryGenerator.startSearch(searchText);
		this.ui.setEnabled(buttonClear, searchText != null && searchText.length() > 0);
	}
	
	public void searchClear(Object searchPerson, Object tablePeople, Object buttonClear) {
		LOG.debug("searchClear");
		this.ui.setText(searchPerson, "");
		this.searchByPerson(searchPerson, tablePeople, buttonClear);
		this.ui.requestFocus(searchPerson);
	}
	
	public void focus(Object component) {
		LOG.debug("focus");
		if (component != null) {
			this.ui.requestFocus(component);
		}
	}
	
	public void viewResponses(Object tablePeople) {
		LOG.debug("viewResponses");
		if (this.callback != null) {
			this.callback.viewResponses(this.getSelectedContact());
		}	
	}
	
	private HospitalContact getSelectedContact() {
		final Object selectedRow = this.ui.getSelectedItem(this.tablePeople);
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
		String searchText = this.ui.getText(this.searchPerson);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
		HospitalContact contact = this.getSelectedContact();
		if (contact != null) {
			LOG.debug("contact: %s" + contact.getName());
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
			this.ui.setEnabled(this.viewResponsesButton, true);
			this.ui.setText(this.labelNameValue, contact.getName());
			this.ui.setText(this.labelHospitalValue, contact.getHospitalId());
			this.ui.setText(this.labelPhoneValue, contact.getPhoneNumber());
			this.ui.setText(this.labelEmailValue, contact.getEmailAddress());
			if (contact.getLastResponse() != null) {
				this.ui.setText(this.labelResponseValue, contact.getLastResponse().toString());
			}
			else {
				this.ui.setText(this.labelResponseValue, "");
			}
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
			this.ui.setEnabled(this.viewResponsesButton, false);
			this.ui.setText(this.labelNameValue, "");
			this.ui.setText(this.labelHospitalValue, "");
			this.ui.setText(this.labelPhoneValue, "");
			this.ui.setText(this.labelEmailValue, "");
			this.ui.setText(this.labelResponseValue, "");
		}
	}
}
