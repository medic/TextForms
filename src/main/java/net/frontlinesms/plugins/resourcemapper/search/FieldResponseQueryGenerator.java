package net.frontlinesms.plugins.resourcemapper.search;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class FieldResponseQueryGenerator extends QueryGenerator {

	public FieldResponseQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
		super(appCon, resultsTable);
	}

	@Override
	public void setSort(int column, boolean ascending) { /*do nothing*/}
	
	@Override
	public void startSearch(String text){
		String query = "select fr from FieldResponse fr where lower(fr.hospitalId) like lower('%"+text+"%')";
		super.runQuery(query);
	}

}
