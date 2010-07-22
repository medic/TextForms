package net.frontlinesms.plugins.resourcemapper.search;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class FieldMappingQueryGenerator extends QueryGenerator {

	public FieldMappingQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
		super(appCon, resultsTable);
	}

	@Override
	public void startSearch(String text) {
		super.runQuery(getSearchQuery(text, null, false));
	}
	
	@Override
	public void startSearch(String text, String sortColumn, boolean sortAscending) {
		super.runQuery(getSearchQuery(text, sortColumn, sortAscending));
	}
	
	private String getSearchQuery(String text, String sortColumn, boolean sortAscending) {
		String query = "SELECT f FROM Field f";
		query += " WHERE lower(f.name) LIKE lower('%"+text+"%')";
		query += " OR lower(f.keyword) LIKE lower('%"+text+"%')";
		if (sortColumn != null && sortColumn.length() > 0) {
			if (sortAscending) {
				query += String.format(" ORDER BY f.%s ASC", sortColumn);
			}
			else {
				query += String.format(" ORDER BY f.%s DESC", sortColumn);
			}
		}
		return query;
	}
	
}
