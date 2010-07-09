package net.frontlinesms.plugins.resourcemapper.ui;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class ResourceMapperThinletTabController implements ThinletUiEventHandler{
	
	private static Logger LOG = FrontlineUtils.getLogger(ResourceMapperThinletTabController.class);
	
	private static final String XML_PATH = "/ui/plugins/resourcemapper/mainTab.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainTab;
	private Object mainPanel;
	private Object panelManagePeople;
	private Object panelManageFields;
	private Object panelBrowseData;
	
	public ResourceMapperThinletTabController(UiGeneratorController uiController, ApplicationContext appContext){
		this.ui = uiController;
		this.appContext = appContext;
		initTab();
	}

	private void initTab() {
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
		LOG.debug("taskChanged: " + selectedProperty);
		this.ui.removeAll(this.mainPanel);
		if ("fields".equalsIgnoreCase(selectedProperty)) {
			if (this.panelManageFields == null) {
				this.panelManageFields = new ManageFieldsPanelHandler(ui, appContext).getMainPanel();
			}
			this.ui.add(this.mainPanel, this.panelManageFields);
		}
		else if ("people".equalsIgnoreCase(selectedProperty)) {
			if (this.panelManagePeople == null) {
				this.panelManagePeople = new ManagePeoplePanelHandler(ui, appContext).getMainPanel();
			}
			this.ui.add(this.mainPanel, this.panelManagePeople);
		}
		else if ("data".equalsIgnoreCase(selectedProperty)) {
			if (this.panelBrowseData == null) {
				this.panelBrowseData = new BrowseDataPanelHandler(ui, appContext).getMainPanel();
			}
			this.ui.add(this.mainPanel, this.panelBrowseData);
		}
	}
}
