package net.frontlinesms.plugins.resourcemapper.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.search.contact.HospitalContactQueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
public class ContactSearchArea implements AdvancedTableActionDelegate {

	private Object mainPanel;
	private Object detailPanel;
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private HospitalContactQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	public ContactSearchArea(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
	}
	
	private void init(){
		mainPanel= uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setGap(mainPanel, 5);
		uiController.setColumns(mainPanel, 1);
		Object searchPanel = uiController.create("panel");
		uiController.setWeight(searchPanel, 1, 0);
		uiController.setGap(searchPanel, 6);
		uiController.add(searchPanel,uiController.createLabel(getI18NString("resourcemapper.search")));
		Object searchBox = uiController.createTextfield("", "");
		uiController.setWeight(searchBox,1,0);
		uiController.setAction(searchBox, "search(this.text)", null, this);
		uiController.add(searchPanel,searchBox);
		uiController.add(mainPanel,searchPanel);
		tableController = new PagedAdvancedTableController(this, appCon, uiController, null);
		queryGenerator = new HospitalContactQueryGenerator(appCon,tableController);
		tableController.putHeader(HospitalContact.class, new String[]{"name","email address","phone number", "hospital id"}, new String[]{"getName","getEmailAddress","getPhoneNumber", "getHospitalId"});
		tableController.setQueryGenerator(queryGenerator);
		uiController.add(mainPanel,tableController.getMainPanel());
		search("");
	}
	
	public void search(String text){
		queryGenerator.startSearch(text);
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}

	public void doubleClickAction(Object selectedObject) {
		
	}

	public void resultsChanged() {
		
	}

	public void selectionChanged(Object selectedObject) {
		
	}
}
