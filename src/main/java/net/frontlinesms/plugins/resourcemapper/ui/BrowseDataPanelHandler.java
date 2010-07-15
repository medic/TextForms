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
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;

/*
 * BrowseDataPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class BrowseDataPanelHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/browseDataPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private ResourceMapperCallback callback;
	
	private HospitalContact selectedContact;
	private HospitalContactDao hospitalContactDao;
	
	private Field selectedField;
	private Object comboSubmitter;
	
	public BrowseDataPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("BrowseDataPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.comboSubmitter = this.ui.find(this.mainPanel, "comboSubmitter");
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}

	public void loadHospitalContacts() {
		this.ui.removeAll(this.comboSubmitter);
		this.ui.add(this.comboSubmitter, this.ui.createComboboxChoice("", null));
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			Object comboboxChoice = this.ui.createComboboxChoice(contact.getName(), contact);
			this.ui.setIcon(comboboxChoice, "/icons/user.png");
			this.ui.add(this.comboSubmitter, comboboxChoice);
		}
	}
	
	public void showDateSelecter(Object textField) {
		System.out.println("showDateSelecter");
		this.ui.showDateSelecter(textField);
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
	
	public void submitterChanged(Object comboSubmitter) {
		Object selectedItem = this.ui.getSelectedItem(comboSubmitter);
		if (selectedItem != null) {
			HospitalContact submitter = (HospitalContact)this.ui.getAttachedObject(selectedItem, HospitalContact.class);
			if (submitter != null) {
				System.out.println("submitterChanged: " + submitter.getName());
			}
			else {
				System.out.println("submitterChanged: null");
			}	
		}
	}
	
	public void setSelectedField(Field field) {
		System.out.println("setSelectedField: "+ field);
		this.selectedField = field;
		Object searchField = this.ui.find(this.mainPanel, "searchField");
		if (field != null) {
			this.ui.setText(searchField, field.getName());
		}
		else {
			this.ui.setText(searchField, "");
		}
	}
	
	public void setSelectedContact(HospitalContact contact) {
		System.out.println("setSelectedContact: "+ contact);
		this.selectedContact = contact;
		if (contact != null) {
			int index = 0;
			for (Object comboboxChoice : this.ui.getItems(this.comboSubmitter)) {
				Object attachedObject = this.ui.getAttachedObject(comboboxChoice);
				if (attachedObject != null) {
					HospitalContact contactItem = (HospitalContact)attachedObject;
					if (contact.equals(contactItem)) {
						this.ui.setSelectedIndex(this.comboSubmitter, index);
						System.out.println("Selecting Contact: " + contact.getName());
						break;
					}
				}
				index++;
			}
		}
		else {
			this.ui.setSelectedIndex(this.comboSubmitter, 0);
		}
	}
}
