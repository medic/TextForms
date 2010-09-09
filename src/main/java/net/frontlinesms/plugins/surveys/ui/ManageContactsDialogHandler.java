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
package net.frontlinesms.plugins.surveys.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.repository.HospitalContactDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageQuestionsPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageContactsDialogHandler implements ThinletUiEventHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ManageContactsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/surveys/manageContactsDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final SurveysCallback callback;
	
	private final Object mainDialog;
	
	private HospitalContact contact;
	private HospitalContactDao hospitalContactDao;
	
	private final Object textName;
	private final Object textOrganization;
	private final Object textPhone;
	private final Object textEmail;
	
	public ManageContactsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) {
		LOG.debug("ManageContactsDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textOrganization = this.ui.find(this.mainDialog, "textOrganization");
		this.textPhone = this.ui.find(this.mainDialog, "textPhone");
		this.textEmail = this.ui.find(this.mainDialog, "textEmail");
		this.hospitalContactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	public void show(HospitalContact contact) {
		this.contact = contact;
		if (contact != null) {
			this.ui.setText(this.textName, contact.getName());
			this.ui.setText(this.textOrganization, contact.getHospitalId());
			this.ui.setText(this.textPhone, contact.getPhoneNumber());
			this.ui.setText(this.textEmail, contact.getEmailAddress());
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textOrganization, "");
			this.ui.setText(this.textPhone, "");
			this.ui.setText(this.textEmail, "");
		}
		this.ui.add(this.mainDialog);
	}
	
	public void saveContact(Object dialog) throws DuplicateKeyException {
		LOG.debug("saveContact");
		String contactName = this.ui.getText(this.textName);
		String contactOrganization = this.ui.getText(this.textOrganization);
		String contactPhone = this.ui.getText(this.textPhone);
		String contactEmail = this.ui.getText(this.textEmail);
		if (contactOrganization == null || contactOrganization.length() == 0) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_CONTACT_HOSPITAL));
		}
		else if (contactPhone == null || contactPhone.length() == 0) {
			this.ui.alert(getI18NString(SurveysConstants.ALERT_MISSING_CONTACT_PHONE));
		}
		else if (this.contact != null) {
			this.contact.setName(contactName);
			this.contact.setHospitalId(contactOrganization);
			this.contact.setPhoneNumber(contactPhone);
			this.contact.setEmailAddress(contactEmail);
			this.hospitalContactDao.updateHospitalContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
		else {
			this.contact = new HospitalContact(contactName, contactPhone, contactEmail, true, contactOrganization);
			this.hospitalContactDao.saveHospitalContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
}
