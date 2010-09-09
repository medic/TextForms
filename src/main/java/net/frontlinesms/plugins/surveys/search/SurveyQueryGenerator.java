package net.frontlinesms.plugins.surveys.search;

import net.frontlinesms.plugins.surveys.search.QueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class SurveyQueryGenerator extends QueryGenerator {

	public SurveyQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
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
		String query = "SELECT s FROM Survey s";
		query += " WHERE lower(s.name) LIKE lower('%"+text+"%')";
		if (sortColumn != null && sortColumn.length() > 0) {
			if (sortAscending) {
				query += String.format(" ORDER BY s.%s ASC", sortColumn);
			}
			else {
				query += String.format(" ORDER BY s.%s DESC", sortColumn);
			}
		}
		return query;
	}
	
}
