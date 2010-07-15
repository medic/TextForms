package net.frontlinesms.plugins.resourcemapper.search;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class FieldMappingQueryGenerator extends QueryGenerator {

	public FieldMappingQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
		super(appCon, resultsTable);
	}

	@Override
	public void setSort(int column, boolean ascending) { /*do nothing*/}
	
	@Override
	public void startSearch(String text){
		String query = "select f from Field f where lower(f.name) like lower('%"+text+"%') or lower(f.abbreviation) like lower('%"+text+"%')";
		super.runQuery(query);
	}

}
