package net.frontlinesms.plugins.resourcemapper.search;

public interface SearchController {
	public void controllerWillAppear();
	public QueryGenerator getQueryGenerator();
}
