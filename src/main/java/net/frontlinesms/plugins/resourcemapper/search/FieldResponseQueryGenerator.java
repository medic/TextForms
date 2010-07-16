package net.frontlinesms.plugins.resourcemapper.search;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

import org.springframework.context.ApplicationContext;

import com.ibm.icu.util.Calendar;

public class FieldResponseQueryGenerator extends QueryGenerator {

	public FieldResponseQueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable) {
		super(appCon, resultsTable);
	}

	@Override
	public void startSearch(String text) {
		super.runQuery(getSearchQuery(text, null, false, null, null));
	}
	
	@Override
	public void startSearch(String text, String sortColumn, boolean sortAscending) {
		super.runQuery(getSearchQuery(text, sortColumn, sortAscending, null, null));
	}

	public void startSearch(String text, String sortColumn, boolean sortAscending, String date, String contact) {
		super.runQuery(getSearchQuery(text, sortColumn, sortAscending,  date, contact));
	}
	
	private String getSearchQuery(String text, String sortColumn, boolean sortAscending, String dateString, String contact) {
		System.out.println(String.format("text=%s date=%S contact=%s", text, dateString, contact));
		String query = "SELECT fr FROM FieldResponse fr";
		query += " WHERE (lower(fr.hospitalId) LIKE lower('%"+text+"%')";
		query += " OR lower(fr.mapping.name) LIKE lower('%"+text+"%')";
		query += " OR lower(fr.mapping.abbreviation) LIKE lower('%"+text+"%'))";
		if (contact != null && contact.length() > 0) {
			query += " AND lower(fr.submitter.name) LIKE lower('%"+contact+"%')";
		}
		if (dateString != null && dateString.length() > 0) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date date = dateFormat.parse(dateString);
				Calendar startDate = Calendar.getInstance();
				startDate.setTime(date);
				startDate.set(Calendar.HOUR_OF_DAY, 0);
				query += String.format(" AND fr.dateSubmitted >= %d ", startDate.getTimeInMillis());
				
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(date);
				endDate.set(Calendar.HOUR_OF_DAY, 23);
				endDate.set(Calendar.MINUTE, 59);
				System.out.println(String.format("Start: %s", startDate.getTime()));
				System.out.println(String.format("End: %s", endDate.getTime()));
				query += String.format(" AND fr.dateSubmitted <= %d ", endDate.getTimeInMillis());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sortColumn != null && sortColumn.length() > 0) {
			if (sortAscending) {
				query += String.format(" ORDER BY fr.%s ASC", sortColumn);
			}
			else {
				query += String.format(" ORDER BY fr.%s DESC", sortColumn);
			}
		}
		return query;
	}

}
