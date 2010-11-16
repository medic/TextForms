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
package net.frontlinesms.plugins.textforms.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.OrganizationDetails;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageQuestionsPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageContactsDialogHandler implements ThinletUiEventHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageContactsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/textforms/manageContactsDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsCallback callback;
	
	private final Object mainDialog;
	
	private Contact contact;
	private ContactDao contactDao;
	
	private final Object textName;
	private final Object textOrganization;
	private final Object textPhone;
	private final Object textEmail;
	
	public ManageContactsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback) {
		LOG.debug("ManageContactsDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textOrganization = this.ui.find(this.mainDialog, "textOrganization");
		this.textPhone = this.ui.find(this.mainDialog, "textPhone");
		this.textEmail = this.ui.find(this.mainDialog, "textEmail");
		this.contactDao = (ContactDao) appContext.getBean("contactDao", ContactDao.class);
	}
	
	public void show(Contact contact) {
		this.contact = contact;
		if (contact != null) {
			this.ui.setText(this.textName, contact.getName());
			OrganizationDetails organizationDetails = contact.getDetails(OrganizationDetails.class);
			if (organizationDetails != null) {
				this.ui.setText(this.textOrganization, organizationDetails.getOrganizationId());
			}
			else {
				this.ui.setText(this.textOrganization, "");	
			}
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
			this.ui.alert(getI18NString(TextFormsConstants.ALERT_MISSING_CONTACT_HOSPITAL));
		}
		else if (contactPhone == null || contactPhone.length() == 0) {
			this.ui.alert(getI18NString(TextFormsConstants.ALERT_MISSING_CONTACT_PHONE));
		}
		else if (this.contact != null) {
			this.contact.setName(contactName);
			OrganizationDetails details = this.contact.getDetails(OrganizationDetails.class);
			if(details != null) {
				details.setOrganizationId(contactOrganization);
			}
			else {
				this.contact.addDetails(new OrganizationDetails(contactOrganization));
			}
			this.contact.setPhoneNumber(contactPhone);
			this.contact.setEmailAddress(contactEmail);
			this.contactDao.updateContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
		else {
			this.contact = new Contact(contactName, contactPhone, null, contactEmail, null, true);
			this.contact.addDetails(new OrganizationDetails(contactOrganization));
			this.contactDao.saveContact(this.contact);
			this.callback.refreshContact(this.contact);
			this.ui.remove(dialog);
		}
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
}
