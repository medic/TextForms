package net.frontlinesms.plugins.surveys.search;

public interface SearchController {
	public void controllerWillAppear();
	public QueryGenerator getQueryGenerator();
}
