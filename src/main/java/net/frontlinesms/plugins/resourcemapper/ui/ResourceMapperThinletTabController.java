package net.frontlinesms.plugins.resourcemapper.ui;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

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
		taskChanged(this.ui.find(this.mainTab, "listTasks"));
	}

	public Object getTab(){
		return mainTab;
	}
	
	public void taskChanged(Object listTasks) {
		Object selectedListItem = this.ui.getSelectedItem(listTasks);
		String selectedProperty = this.ui.getProperty(selectedListItem, "value").toString();
		System.out.println("taskChanged: " + selectedProperty);
		this.ui.removeAll(this.mainPanel);
		if ("fields".equalsIgnoreCase(selectedProperty)) {
			if (this.panelManageFields == null) {
				this.panelManageFields = new ManageFieldsPanelHandler(this.ui, this.appContext, this);
			}
			this.ui.add(this.mainPanel, this.panelManageFields.getMainPanel());
		}
		else if ("people".equalsIgnoreCase(selectedProperty)) {
			if (this.panelManagePeople == null) {
				this.panelManagePeople = new ManagePeoplePanelHandler(this.ui, this.appContext, this);
			}
			this.ui.add(this.mainPanel, this.panelManagePeople.getMainPanel());
		}
		else if ("data".equalsIgnoreCase(selectedProperty)) {
			if (this.panelBrowseData == null) {
				this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this);
			}
			this.panelBrowseData.setSelectedContact(null);
			this.panelBrowseData.setSelectedField(null);
			this.ui.add(this.mainPanel, this.panelBrowseData.getMainPanel());
		}
	}
	
	public void viewResponses(HospitalContact contact) {
		System.out.println("viewResponses by contact");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		this.ui.removeAll(this.mainPanel);
		if (this.panelBrowseData == null) {
			this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this);
		}
		this.panelBrowseData.setSelectedContact(contact);
		this.panelBrowseData.setSelectedField(null);
		this.ui.add(this.mainPanel, this.panelBrowseData.getMainPanel());
	}
	
	public void viewResponses(Field field) {
		System.out.println("viewResponses by field");
		Object taskList = this.ui.find(this.mainTab, "listTasks");
		for (Object item : this.ui.getItems(taskList)) {
			String itemProperty = this.ui.getProperty(item, "value").toString();
			this.ui.setSelected(item, "data".equalsIgnoreCase(itemProperty));
		} 
		this.ui.removeAll(this.mainPanel);
		if (this.panelBrowseData == null) {
			this.panelBrowseData = new BrowseDataPanelHandler(this.ui, this.appContext, this);
		}
		this.panelBrowseData.setSelectedContact(null);
		this.panelBrowseData.setSelectedField(field);
		this.ui.add(this.mainPanel, this.panelBrowseData.getMainPanel());
	}
	
	public void refreshContact(HospitalContact contact) {
		System.out.println("refreshContact: " + contact);
		if (this.panelManagePeople == null) {
			this.panelManagePeople = new ManagePeoplePanelHandler(this.ui, this.appContext, this);
		}
		this.panelManagePeople.refreshContacts();
	}
	
	public void refreshField(Field field) {
		System.out.println("refreshField: " + field);
		if (this.panelManageFields == null) {
			this.panelManageFields = new ManageFieldsPanelHandler(this.ui, this.appContext, this);
		}
		this.panelManageFields.refreshFields();
	}
}
