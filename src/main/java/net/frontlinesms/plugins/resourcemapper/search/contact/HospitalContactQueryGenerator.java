package net.frontlinesms.plugins.resourcemapper.search.contact;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class HospitalContactQueryGenerator extends QueryGenerator{

	public HospitalContactQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
		super(appCon, resultsTable);
	}

	@Override
	public void setSort(int column, boolean ascending) { /*do nothing*/}
	
	@Override
	public void startSearch(String name){
		String query = "select hos from HospitalContact hos where lower(hos.name) like lower('%"+name+"%') or lower(hos.emailAddress) like lower('%"+name+"%') or lower(hos.phoneNumber) like lower('%"+name+"%') or lower(hos.hospitalId) like lower('%"+name+"%')";
		super.runQuery(query);
	}

}
