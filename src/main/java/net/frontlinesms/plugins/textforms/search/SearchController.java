package net.frontlinesms.plugins.textforms.search;

public interface SearchController {
	public void controllerWillAppear();
	public QueryGenerator getQueryGenerator();
}
