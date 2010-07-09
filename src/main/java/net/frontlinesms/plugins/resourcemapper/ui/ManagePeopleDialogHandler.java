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
public class ManagePeopleDialogHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(ManagePeopleDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/resourcemapper/managePeopleDialog.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainDialog;
	private HospitalContact contact;
	
	private Object textName;
	private Object textHospital;
	private Object textPhone;
	
	public ManagePeopleDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textHospital = this.ui.find(this.mainDialog, "textHospital");
		this.textPhone = this.ui.find(this.mainDialog, "textPhone");
	}
	
	public void show(HospitalContact contact) {
		this.contact = contact;
		if (contact != null) {
			this.ui.setText(this.textName, contact.getName());
			this.ui.setText(this.textHospital, contact.getHospitalId());
			this.ui.setText(this.textPhone, contact.getPhoneNumber());
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textHospital, "");
			this.ui.setText(this.textPhone, "");
		}
		this.ui.add(this.mainDialog);
	}
	
	public void savePerson(Object dialog) {
		System.out.println("savePerson");
		this.callback.refreshContact(this.contact);
		this.ui.remove(dialog);
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
	}
}
