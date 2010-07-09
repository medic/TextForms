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
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManagePeoplePanelHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/managePeoplePanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private ManagePeopleDialogHandler editDialog;
	private ResourceMapperCallback callback;
	private Object editButton;
	private Object deleteButton;
	private Object viewResponsesButton;
	
	public ManagePeoplePanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManagePeoplePanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.editDialog = new ManagePeopleDialogHandler(this.ui, this.appContext, callback);
		this.editButton = this.ui.find(this.mainPanel, "buttonEditPerson");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeletePerson");
		this.viewResponsesButton = this.ui.find(this.mainPanel, "buttonViewResponses");
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void addPerson(Object tablePeople) {
		System.out.println("addPerson");
		this.editDialog.show(null);
	}
	
	public void editPerson(Object panelPerson) {
		HospitalContact contact = (HospitalContact)this.ui.getAttachedObject(panelPerson);
		this.editDialog.show(contact);
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void deletePerson() {
		System.out.println("deletePerson");
	}
	
	public void searchByPerson(Object searchPerson, Object tablePeople, Object buttonClear) {
		String searchText = this.ui.getText(searchPerson);
		System.out.println("searchByPerson: " + searchText);
		this.ui.setEnabled(buttonClear, searchText != null && searchText.length() > 0);
	}
	
	public void searchClear(Object searchPerson, Object tablePeople, Object buttonClear) {
		System.out.println("searchClear");
		this.ui.setText(searchPerson, "");
		this.searchByPerson(searchPerson, tablePeople, buttonClear);
		this.ui.requestFocus(searchPerson);
	}
	
	public void viewResponses(Object panelPerson) {
		System.out.println("viewResponses");
		if (this.callback != null) {
			HospitalContact contact = (HospitalContact)this.ui.getAttachedObject(panelPerson);
			this.callback.viewResponses(contact);
		}	
	}
	
	public void personSelectionChanged(Object tablePeople, Object panelPerson) {
		System.out.println("personSelectionChanged");
		Object selectedPerson = this.ui.getSelectedItem(tablePeople);
		if (selectedPerson != null) {
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
			this.ui.setEnabled(this.viewResponsesButton, true);
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
			this.ui.setEnabled(this.viewResponsesButton, true);
		}
	}
}
