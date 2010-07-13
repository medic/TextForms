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
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
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
	private HospitalContactDao contactDao;
	
	private Object textName;
	private Object textHospital;
	private Object textPhone;
	private Object textEmail;
	
	public ManagePeopleDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textHospital = this.ui.find(this.mainDialog, "textHospital");
		this.textPhone = this.ui.find(this.mainDialog, "textPhone");
		this.textEmail = this.ui.find(this.mainDialog, "textEmail");
		this.contactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	public void show(HospitalContact contact) {
		this.contact = contact;
		if (contact != null) {
			this.ui.setText(this.textName, contact.getName());
			this.ui.setText(this.textHospital, contact.getHospitalId());
			this.ui.setText(this.textPhone, contact.getPhoneNumber());
			this.ui.setText(this.textEmail, contact.getEmailAddress());
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textHospital, "");
			this.ui.setText(this.textPhone, "");
			this.ui.setText(this.textEmail, "");
		}
		this.ui.add(this.mainDialog);
	}
	
	public void savePerson(Object dialog) throws DuplicateKeyException {
		System.out.println("savePerson");
		String contactName = this.ui.getText(this.textName);
		String contactHospital = this.ui.getText(this.textHospital);
		String contactPhone = this.ui.getText(this.textPhone);
		String contactEmail = this.ui.getText(this.textEmail);
		if (this.contact != null) {
			this.contact.setName(contactName);
			this.contact.setHospitalId(contactHospital);
			this.contact.setPhoneNumber(contactPhone);
			this.contact.setEmailAddress(contactEmail);
			this.contactDao.updateHospitalContact(this.contact);
		}
		else {
			this.contact = new HospitalContact(contactName, contactPhone, contactEmail, true, contactHospital);
			this.contactDao.saveHospitalContact(this.contact);
		}
		this.callback.refreshContact(this.contact);
		this.ui.remove(dialog);
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
	}
}
