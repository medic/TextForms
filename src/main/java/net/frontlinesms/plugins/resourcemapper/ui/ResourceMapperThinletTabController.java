package net.frontlinesms.plugins.resourcemapper.ui;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class ResourceMapperThinletTabController implements ThinletUiEventHandler{
	
	private UiGeneratorController uiController;
	
	private Object mainTab;
	private Object mainPanel;
	
	private ApplicationContext appCon;
	private static final String TAB_PATH = "/ui/plugins/resourcemapper/mainTab.xml";
	
	public ResourceMapperThinletTabController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		initTab();
	}

	private void initTab() {
		this.mainTab = uiController.loadComponentFromFile(TAB_PATH, this);
		this.mainPanel = uiController.find(mainTab, "resourcemapper_main_panel");
		uiController.add(mainPanel,new ContactSearchArea(uiController,appCon).getMainPanel());
	}
	
	public Object getTab(){
		return mainTab;
	}
	
}
