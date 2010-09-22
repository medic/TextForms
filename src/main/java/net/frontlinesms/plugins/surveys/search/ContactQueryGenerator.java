package net.frontlinesms.plugins.surveys.search;

import net.frontlinesms.plugins.surveys.search.QueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

/**
 * ContactQueryGenerator
 * @author dalezak
 *
 */
public class ContactQueryGenerator extends QueryGenerator {

	public ContactQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
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
		String query = "SELECT c FROM Contact c";
		query += " WHERE lower(c) LIKE lower('%"+text+"%')";
		query += " OR lower(c) LIKE lower('%"+text+"%')";
		query += " OR lower(c) LIKE lower('%"+text+"%')";
		if (sortColumn != null && sortColumn.length() > 0) {
			if (sortAscending) {
				query += String.format(" ORDER BY c.%s ASC", sortColumn);
			}
			else {
				query += String.format(" ORDER BY c.%s DESC", sortColumn);
			}
		}
		return query;
	}
	
}
