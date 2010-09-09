package net.frontlinesms.plugins.surveys.search;

import net.frontlinesms.plugins.surveys.search.QueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

public class HospitalContactQueryGenerator extends QueryGenerator {

	public HospitalContactQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
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
		String query = "SELECT hos FROM HospitalContact hos";
		query += " WHERE lower(hos.name) LIKE lower('%"+text+"%')";
		query += " OR lower(hos.emailAddress) LIKE lower('%"+text+"%')";
		query += " OR lower(hos.phoneNumber) LIKE lower('%"+text+"%')";
		query += " OR lower(hos.hospitalId) LIKE lower('%"+text+"%')";
		if (sortColumn != null && sortColumn.length() > 0) {
			if (sortAscending) {
				query += String.format(" ORDER BY hos.%s ASC", sortColumn);
			}
			else {
				query += String.format(" ORDER BY hos.%s DESC", sortColumn);
			}
		}
		return query;
	}
	
}
