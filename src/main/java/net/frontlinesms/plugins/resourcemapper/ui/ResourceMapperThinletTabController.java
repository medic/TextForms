package net.frontlinesms.plugins.resourcemapper.ui;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class ResourceMapperThinletTabController implements ThinletUiEventHandler, ResourceMapperCallback {
	
	private static Logger LOG = FrontlineUtils.getLogger(ResourceMapperThinletTabController.class);
	
	private static final String XML_PATH = "/ui/plugins/resourcemapper/mainTab.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainTab;
	private Object mainPanel;
	private ManagePeoplePanelHandler panelManagePeople;
	private ManageFieldsPanelHandler panelManageFields;
	private BrowseDataPanelHandler panelBrowseData;
	
	public ResourceMapperThinletTabController(UiGeneratorController uiController, ApplicationContext appContext){
		this.ui = uiController;
		this.appContext = appContext;
		this.mainTab = this.ui.loadComponentFromFile(XML_PATH, this);
		this.mainPanel = this.ui.find(this.mainTab, "panelMainContent");
		this.panelManagePeople = new ManagePeoplePanelHandler(this.ui, this.appContext, this);
		this.panelManageFields = new ManageFieldsPanelHandler(this.ui, this.appContext, this);
		this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this);
		taskChanged(this.ui.find(this.mainTab, "listTasks"));
	}

	public Object getTab(){
		return mainTab;
	}
	
	public void taskChanged(Object listTasks) {
		Object selectedListItem = this.ui.getSelectedItem(listTasks);
		String selectedProperty = this.ui.getProperty(selectedListItem, "value").toString();
		System.out.println("taskChanged: " + selectedProperty);
		if ("fields".equalsIgnoreCase(selectedProperty)) {
			showManageFieldsPanel();
		}
		else if ("people".equalsIgnoreCase(selectedProperty)) {
			showManagePeoplePanel();
		}
		else if ("data".equalsIgnoreCase(selectedProperty)) {
			showBrowseDataPanel(null, null);
		}
	}
	
	public void viewResponses(HospitalContact contact) {
		System.out.println("viewResponses by contact");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(contact, null);
	}
	
	public void viewResponses(Field field) {
		System.out.println("viewResponses by field");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		showBrowseDataPanel(null, field);
	}
	
	public void refreshContact(HospitalContact contact) {
		System.out.println("refreshContact: " + contact);
		this.panelManagePeople.refreshContacts(contact);
	}
	
	public void refreshField(Field field) {
		System.out.println("refreshField: " + field);
		this.panelManageFields.refreshFields(field);
	}
	
	public void refreshFieldResponse(FieldResponse fieldResponse) {
		System.out.println("refreshFieldResponse: " + fieldResponse);
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
}
