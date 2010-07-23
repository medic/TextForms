package net.frontlinesms.plugins.resourcemapper.ui.components;

import java.util.List;

import net.frontlinesms.plugins.resourcemapper.search.QueryGenerator;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PagedAdvancedTableController extends AdvancedTableController {

	private QueryGenerator queryGenerator;
	protected Object pagingControls;
	protected Object mainPanel;
	private final static String PAGING_CONTROLS_XML = "/ui/plugins/resourcemapper/pagingControls.xml";

	protected String pagingToText;
	protected String pagingOfText;
	
	@SuppressWarnings("static-access")
	public PagedAdvancedTableController(AdvancedTableActionDelegate delegate, ApplicationContext appcon, UiGeneratorController uiController, Object table, Object panel) {
			super(delegate, appcon, uiController, table);
			if (panel == null) {
				this.mainPanel = uiController.create("panel");
			}
			else {
				this.mainPanel = panel;
			}
			uiController.setWeight(mainPanel, 1, 1);
			uiController.setColumns(mainPanel, 1);
			uiController.setGap(mainPanel, 6);
			uiController.add(mainPanel, super.getTable());
			pagingControls = uiController.loadComponentFromFile(PAGING_CONTROLS_XML, this);
			uiController.add(mainPanel, pagingControls);			
	}

	public void setPagingPhrases(String pagingTo, String pagingOf) {
		this.pagingToText = pagingTo;
		this.pagingOfText = pagingOf;
	}
	
	/**
	 * action method for left page button
	 */
	public void pageLeft(){
		this.getQueryGenerator().previousPage();
		updatePagingControls();
	}
	
	/**
	 * action method for right page button
	 */
	public void pageRight(){
		this.getQueryGenerator().nextPage();
		updatePagingControls();
	}
	
	private void updatePagingControls(){
		if (getQueryGenerator().getTotalResults() == 0) {
			uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"), false);
			uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"), false);
			uiController.setText(uiController.find(pagingControls, "resultsLabel"), this.noResultsText);
			return;
		}
		
		//set the paging buttons
		uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"), getQueryGenerator().hasPreviousPage());
		uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"), getQueryGenerator().hasNextPage());
		String pagingLabel = this.resultsText + " " + 
							 getQueryGenerator().getFirstResultOnPage() + " " + 
							 this.pagingToText + " " +
							 getQueryGenerator().getLastResultOnPage() + " " + 
							 this.pagingOfText + " " + 
							 getQueryGenerator().getTotalResults();
		uiController.setText(uiController.find(pagingControls, "resultsLabel"), pagingLabel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setResults(List results){
		super.setResults(results);
		updatePagingControls();
	}

	public void setQueryGenerator(QueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}

	public QueryGenerator getQueryGenerator() {
		return queryGenerator;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}

}
