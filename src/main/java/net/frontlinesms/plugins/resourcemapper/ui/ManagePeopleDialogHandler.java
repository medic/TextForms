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

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManagePeopleDialogHandler implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManagePeopleDialogHandler.class);
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
		LOG.debug("ManagePeopleDialogHandler");
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
		LOG.debug("savePerson");
		String contactName = this.ui.getText(this.textName);
		String contactHospital = this.ui.getText(this.textHospital);
		String contactPhone = this.ui.getText(this.textPhone);
		String contactEmail = this.ui.getText(this.textEmail);
		if (contactName == null || contactName.length() == 0) {
			this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_CONTACT_NAME));
		}
		else if (contactHospital == null || contactHospital.length() == 0) {
			this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_CONTACT_HOSPITAL));
		}
		else if (contactPhone == null || contactPhone.length() == 0) {
			this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_CONTACT_PHONE));
		}
		else if (contactEmail == null || contactEmail.length() == 0) {
			this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_CONTACT_EMAIL));
		}
		else if (this.contact != null) {
			this.contact.setName(contactName);
			this.contact.setHospitalId(contactHospital);
			this.contact.setPhoneNumber(contactPhone);
			this.contact.setEmailAddress(contactEmail);
			this.contactDao.updateHospitalContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
		else {
			this.contact = new HospitalContact(contactName, contactPhone, contactEmail, true, contactHospital);
			this.contactDao.saveHospitalContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
}
