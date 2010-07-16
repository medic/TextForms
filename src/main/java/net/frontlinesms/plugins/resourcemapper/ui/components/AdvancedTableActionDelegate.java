package net.frontlinesms.plugins.resourcemapper.ui.components;


public interface AdvancedTableActionDelegate {
	public void selectionChanged(Object selectedObject);
	public void doubleClickAction(Object selectedObject);
	public void resultsChanged();
	public void sortChanged(String column, boolean ascending);
}
