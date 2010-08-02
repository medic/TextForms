package net.frontlinesms.plugins.resourcemapper.ui;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

import org.springframework.context.ApplicationContext;

/**
 * ResourceMapperThinletTabController
 * @author dalezak
 *
 */
public class ResourceMapperThinletTabController implements ThinletUiEventHandler, ResourceMapperCallback {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperThinletTabController.class);
	
	private static final String XML_PATH = "/ui/plugins/resourcemapper/mainTab.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainTab;
	private Object mainPanel;
	private Object listTasks;
	private ManagePeoplePanelHandler panelManagePeople;
	private ManageFieldsPanelHandler panelManageFields;
	private BrowseDataPanelHandler panelBrowseData;
	private ManageOptionsPanelHandler panelManageOptions;
	
	public ResourceMapperThinletTabController(FrontlineSMS frontlineController, UiGeneratorController uiController, ApplicationContext appContext) {
		LOG.debug("ResourceMapperThinletTabController");
		this.ui = uiController;
		this.appContext = appContext;
		this.mainTab = this.ui.loadComponentFromFile(XML_PATH, this);
		this.mainPanel = this.ui.find(this.mainTab, "panelMainContent");
		this.listTasks = this.ui.find(this.mainTab, "listTasks");
		this.panelManagePeople = new ManagePeoplePanelHandler(this.ui, this.appContext, this);
		this.panelManageFields = new ManageFieldsPanelHandler(this.ui, this.appContext, this);
		this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this);
		this.panelManageOptions = new ManageOptionsPanelHandler(this.ui, this.appContext, this);
		
		taskChanged(this.listTasks);
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	public void taskChanged(Object listTasks) {
		Object selectedListItem = this.ui.getSelectedItem(listTasks);
		String selectedProperty = this.ui.getProperty(selectedListItem, "value").toString();
		LOG.debug("taskChanged: " + selectedProperty);
		if ("fields".equalsIgnoreCase(selectedProperty)) {
			showManageFieldsPanel();
		}
		else if ("people".equalsIgnoreCase(selectedProperty)) {
			showManagePeoplePanel();
		}
		else if ("data".equalsIgnoreCase(selectedProperty)) {
			showBrowseDataPanel(null, null);
		}
		else if ("options".equalsIgnoreCase(selectedProperty)) {
			showManageOptionsPanel();
		}
	}
	
	public void viewResponses(HospitalContact contact) {
		LOG.debug("viewResponses by contact");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(contact, null);
	}
	
	public void viewResponses(Field field) {
		LOG.debug("viewResponses by field");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(null, field);
	}
	
	public void refreshContact(HospitalContact contact) {
		LOG.debug("refreshContact: %s", contact);
		this.panelManagePeople.refreshContacts(contact);
	}
	
	public void refreshField(Field field) {
		LOG.debug("refreshField: %s", field);
		this.panelManageFields.refreshFields(field);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshFieldResponse(FieldResponse fieldResponse) {
		LOG.debug("refreshFieldResponse: %s", fieldResponse);
		this.panelBrowseData.refreshFieldResponses(fieldResponse);
	}
	
	private void showBrowseDataPanel(HospitalContact contact, Field field) {
		this.ui.removeAll(this.mainPanel);
		this.panelBrowseData.loadHospitalContacts();
		this.panelBrowseData.setSelectedContact(contact);
		this.panelBrowseData.setSelectedField(field);
		this.ui.add(this.mainPanel, this.panelBrowseData.getMainPanel());
	}
	
	private void showManagePeoplePanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManagePeople.getMainPanel());
	}
	
	private void showManageFieldsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageFields.getMainPanel());
	}
	
	private void showManageOptionsPanel() {
		this.ui.removeAll(this.mainPanel);
		this.ui.add(this.mainPanel, this.panelManageOptions.getMainPanel());
	}
}
