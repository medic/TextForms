/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.frontlinesms.plugins.resourcemapper.ui;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageFieldsPanelHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(ManageFieldsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/manageFieldsPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private ManageFieldsDialogHandler editDialog;
	private ResourceMapperCallback callback;
	private Object editButton;
	private Object deleteButton;
	private Object viewResponsesButton;
	
	public ManageFieldsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManageFieldsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.editDialog = new ManageFieldsDialogHandler(this.ui, this.appContext, callback);
		this.editButton = this.ui.find(this.mainPanel, "buttonEditField");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeleteField");
		this.viewResponsesButton = this.ui.find(this.mainPanel, "buttonViewResponses");
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void addField(Object tableField) {
		System.out.println("addField");
		this.editDialog.show(null);
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void deleteField() {
		System.out.println("deleteField");
	}
	
	public void editField(Object panelField) {
		Field field = (Field)this.ui.getAttachedObject(panelField);
		this.editDialog.show(field);
	}
	
	public void searchByField(Object searchField, Object tableField, Object buttonClear) {
		String searchText = this.ui.getText(searchField);
		System.out.println("searchByField: " + searchText);
		this.ui.setEnabled(buttonClear, searchText != null && searchText.length() > 0);
	}
	
	public void searchClear(Object searchField, Object tableField, Object buttonClear) {
		System.out.println("searchClear");
		this.ui.setText(searchField, "");
		this.searchByField(searchField, tableField, buttonClear);
		this.ui.requestFocus(searchField);
	}
	
	public void viewResponses(Object panelField) {
		System.out.println("viewResponses");
		if (this.callback != null) {
			Field field = (Field)this.ui.getAttachedObject(panelField);
			this.callback.viewResponses(field);
		}	
	}
	
	public void typeChanged(Object comboTypes, Object listChoices) {
		System.out.println("typeChanged");
		Object selectedType = this.ui.getSelectedItem(comboTypes);
		String selectedProperty = this.ui.getProperty(selectedType, "value").toString();
		if ("plaintext".equalsIgnoreCase(selectedProperty)) {
			this.ui.setVisible(listChoices, false);
		}
		else if ("boolean".equalsIgnoreCase(selectedProperty)) {
			this.ui.setVisible(listChoices, false);
		}
		else if ("checklist".equalsIgnoreCase(selectedProperty)) {
			this.ui.setVisible(listChoices, true);
		}
		else if ("multiplechoice".equalsIgnoreCase(selectedProperty)) {
			this.ui.setVisible(listChoices, true);
		}
	}
	
	public void fieldSelectionChanged(Object tableField, Object panelField) {
		System.out.println("fieldSelectionChanged");
		Object selectedField = this.ui.getSelectedItem(tableField);
		if (selectedField != null) {
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
			this.ui.setEnabled(this.viewResponsesButton, true);
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
			this.ui.setEnabled(this.viewResponsesButton, false);
		}
	}
}
