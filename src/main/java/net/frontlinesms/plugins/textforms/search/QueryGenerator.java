package net.frontlinesms.plugins.textforms.search;

import java.util.List;

import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.ui.components.PagedAdvancedTableController;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;

/**
 * A base class for generating HQL search queries
 * Has utilities for paging, query running, and performance monitoring
 * @author Dieterich Lawson
 *
 */
public abstract class QueryGenerator {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(QueryGenerator.class);
	
	protected String previousQuery;
	
	//paging stuff
	/**
	 * The total number of results in the result set
	 * starts counting from 1
	 */
	protected int totalResults;
	/**
	 * The current page in the result set
	 * starts counting from 0
	 */
	protected int currentPage;
	/**
	 * total pages in the result set
	 * starts counting from 1
	 * calculated by taking totalResults/pageSize
	 */
	protected int totalPages;
	/**
	 * the size of the pages
	 */
	protected int pageSize;
	
	//Hibernate session objects
	protected SessionFactory sessionFactory;
	protected Session session;
	
	/**
	 * the table for results
	 */
	protected PagedAdvancedTableController resultsTable;
	
	public abstract void startSearch(String value);
	public abstract void startSearch(String value, String sortColumn, boolean sortAscending);
	
	public QueryGenerator(ApplicationContext appCon, PagedAdvancedTableController resultsTable){
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		this.resultsTable = resultsTable;
		this.pageSize = 30;
		this.totalPages = 0;
		this.totalResults = 0;
		this.currentPage = 0;
		this.previousQuery = "";
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		if(currentPage < this.totalPages){
			this.currentPage = currentPage;
		}
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getTotalResults() {
		return this.totalResults;
	}
	
	public int getTotalPages() {
		return this.totalPages;
	}
	
	public void nextPage(){
		if (this.currentPage < (this.totalPages - 1)){
			this.currentPage++;
			refresh();
		}
	}
	
	public void previousPage(){
		if (this.currentPage > 0){
			this.currentPage--;
			refresh();
		}
	}
	
	public int getFirstResultOnPage(){
		return this.currentPage * this.pageSize + 1;
	}
	
	public int getLastResultOnPage(){
		if (this.currentPage < this.totalPages-1){
			return this.currentPage * this.pageSize + this.pageSize;
		}
		else{
			return this.totalResults;
		}
	}
	
	public boolean hasNextPage(){
		return this.currentPage < (this.totalPages-1);
	}
	
	public boolean hasPreviousPage(){
		return this.currentPage > 0;
	}
	
	public void refresh(){
		runQuery(this.previousQuery);
	}
	
	public void resetPaging(){
		this.currentPage=0;
	}
	
	/**
	 * Takes an already-constructed HQL query and runs it, handling the pagination
	 * and performance measurement
	 * @param query The query to be run
	 */
	@SuppressWarnings("unchecked")
	protected void runQuery(String query) {
		//LOG.debug(query);
		//check if session is active
		if (this.session == null) {
			try {
				this.session = this.sessionFactory.getCurrentSession();
			}
			catch (Throwable t) {			
				this.session = this.sessionFactory.openSession();
			}
		}
		Transaction transaction = this.session.beginTransaction();
		
		//construct the count query
		String querySuffix = query.substring(query.toUpperCase().indexOf("FROM"));
		int orderByIndex = querySuffix.toUpperCase().indexOf("ORDER BY");
		if (orderByIndex > -1) {
			querySuffix = querySuffix.substring(0, orderByIndex);
		}
		String countQuery = "SELECT COUNT(*) " + querySuffix;
		//LOG.debug(countQuery);
		
		//run the count query, obtaining the total number of results
		long countPrevTime = System.nanoTime();
		this.totalResults = ((Long) this.session.createQuery(countQuery).list().get(0)).intValue();
		
		long countElapsedTime = System.nanoTime() - countPrevTime;
		//LOG.debug("Count Time: %s", countElapsedTime/1000000.0);
		
		if (this.totalResults % this.pageSize == 0) {
			this.totalPages = this.totalResults / this.pageSize;
		}
		else {
			this.totalPages = (this.totalResults / this.pageSize) + 1;
		}
		
		//set up the time measurement
		long prevTime = System.nanoTime();
		//run the query
		List results  = session.createQuery(query).setFirstResult(this.currentPage * this.pageSize).setMaxResults(this.pageSize).list();
		this.previousQuery = query;
		
		//output time elapsed
		long elapsedTime = System.nanoTime() - prevTime;
		//LOG.debug("Query Time: %s", elapsedTime/1000000.0);
		
		for (Object result : results){
			this.session.evict(result);
		}
		
		if (transaction != null) {
			transaction.commit();
		}
		
		this.resultsTable.setResults(results);
	}
	
}
