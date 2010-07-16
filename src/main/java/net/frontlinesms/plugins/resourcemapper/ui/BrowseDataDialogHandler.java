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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * BrowseDataDialogHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class BrowseDataDialogHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(ManageFieldsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/resourcemapper/browseDataDialog.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainDialog;
	
	private FieldResponse fieldResponse;
	private FieldResponseDao fieldResponseDao;
	private FieldMappingDao fieldMappingDao;
	private HospitalContactDao hospitalContactDao;
	private MessageDao messageDao;
	
	private Object comboFieldTypes;
	private Object comboSubmitter;
	private Object textResponse;
	private Object textDate;
	private Object textHospital;
	
	public BrowseDataDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) { 
		System.out.println("BrowseDataDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.fieldResponseDao = (FieldResponseDao) appContext.getBean("fieldResponseDao");
		this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
		this.messageDao = (MessageDao)appContext.getBean("messageDao");
		
		this.comboFieldTypes = this.ui.find(this.mainDialog, "comboFieldTypes");
		this.comboSubmitter = this.ui.find(this.mainDialog, "comboSubmitter");
		this.textResponse = this.ui.find(this.mainDialog, "textResponse");
		this.textDate = this.ui.find(this.mainDialog, "textDate");
		this.textHospital = this.ui.find(this.mainDialog, "textHospital");
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
	
	public void loadFieldMappings() {
		this.ui.removeAll(this.comboFieldTypes);
		this.ui.add(this.comboFieldTypes, this.ui.createComboboxChoice("", null));
		for (Field fieldClass : this.fieldMappingDao.getAllFieldMappings()) {
			String fieldDisplayName = String.format("%s : %s (%s)", fieldClass.getName(), fieldClass.getAbbreviation(), fieldClass.getTypeLabel());
			Object comboBoxChoice = this.ui.createComboboxChoice(fieldDisplayName, fieldClass);
			this.ui.setIcon(comboBoxChoice, "/icons/keyword.png");
			this.ui.add(this.comboFieldTypes, comboBoxChoice);
		}
	}
	
	public void show(FieldResponse fieldResponse) {
		this.fieldResponse = fieldResponse;
		this.ui.setSelectedIndex(this.comboSubmitter, 0);
		this.ui.setSelectedIndex(this.comboFieldTypes, 0);
		if (fieldResponse != null) {
			this.ui.setText(this.textDate, fieldResponse.getDateSubmittedText());
			this.ui.setText(this.textResponse, fieldResponse.getMessageText());
			this.setSelectedContact(fieldResponse.getSubmitter());
			this.setSelectedField(fieldResponse.getMapping());
			submitterChanged(this.comboSubmitter, this.textHospital);
			for (int index = 0; index < this.ui.getCount(this.comboFieldTypes); index++) {
				Object comboTypeItem = this.ui.getItem(this.comboFieldTypes, index);
				Field field = (Field)this.ui.getAttachedObject(comboTypeItem);
				if (field != null) {
					if (fieldResponse.getMappingType().equalsIgnoreCase(field.getType())) {
						this.ui.setSelectedIndex(this.comboFieldTypes, index);
						break;
					}			
				}		
			}
			if (fieldResponse.getMessage() != null) {
				this.ui.setEnabled(this.textResponse, false);
				this.ui.setEditable(this.textResponse, false);
			}
			else {
				this.ui.setEnabled(this.textResponse, true);
				this.ui.setEditable(this.textResponse, true);
			}
		}
		else {
			this.ui.setText(this.textDate, "");
			this.ui.setText(this.textResponse, "");
			this.ui.setEnabled(this.textResponse, true);
			this.ui.setEditable(this.textResponse, true);
			this.ui.setSelectedIndex(this.comboSubmitter, 0);
			this.ui.setSelectedIndex(this.comboFieldTypes, 0);
		}
		this.ui.add(this.mainDialog);
	}
	
	private void setSelectedContact(HospitalContact contact) {
		System.out.println("setSelectedContact: "+ contact);
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
	
	private void setSelectedField(Field field) {
		for (int index = 0; index < this.ui.getCount(this.comboFieldTypes); index++) {
			Object comboTypeItem = this.ui.getItem(this.comboFieldTypes, index);
			Object attachedObject = this.ui.getAttachedObject(comboTypeItem);
			if (attachedObject != null) {
				if (fieldResponse.getMapping().getType().equalsIgnoreCase(attachedObject.toString())) {
					this.ui.setSelectedIndex(this.comboFieldTypes, index);
					break;
				}			
			}		
		}
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void showDateSelecter(Object textField) {
		System.out.println("showDateSelecter");
		this.ui.showDateSelecter(textField);
	}
	
	public void saveFieldResponse(Object dialog) throws DuplicateKeyException {
		System.out.println("saveFieldResponse");
		Date dateSubmitted = this.getDateSubmitted();
		String response = this.ui.getText(this.textResponse);
		String hospitalId = this.ui.getText(this.textHospital);
		HospitalContact submitter = this.getSubmitter();
		Field field = this.getField();
		if (this.fieldResponse != null) {
			this.fieldResponse.setDateSubmitted(dateSubmitted);
			this.fieldResponse.setSubmitter(submitter);
			this.fieldResponse.setHospitalId(hospitalId);
			if (this.fieldResponse.getMessage() == null) {
				FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateSubmitted.getTime(), Long.toString(submitter.getId()), null, response);
				this.messageDao.saveMessage(frontlineMessage);
				this.fieldResponse.setMessage(frontlineMessage);
				System.out.println("FrontlineMessage Created!");
			}
			this.fieldResponseDao.updateFieldResponse(this.fieldResponse);
			this.callback.refreshFieldResponse(this.fieldResponse);
			System.out.println("FieldResponse Updated!");
		}
		else {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateSubmitted.getTime(), Long.toString(submitter.getId()), null, response);
			this.messageDao.saveMessage(frontlineMessage);
			System.out.println("FrontlineMessage Created!");
			
			FieldResponse newFieldResponse = FieldResponseFactory.createFieldResponse(frontlineMessage, submitter, dateSubmitted, hospitalId, field);
			if (newFieldResponse != null) {
				System.out.println("FieldResponse Created!");
				this.fieldResponseDao.saveFieldResponse(newFieldResponse);
			}
			else {
				this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_ERROR_CREATE_RESPONSE));
			}
			this.callback.refreshFieldResponse(newFieldResponse);
		}
		this.ui.remove(dialog);
	}
	
	private Field getField() {
		Object fieldItem = this.ui.getSelectedItem(this.comboFieldTypes);
		if (fieldItem != null) {
			return (Field)this.ui.getAttachedObject(fieldItem);
		}
		return null;
	}
	
	private HospitalContact getSubmitter() {
		Object submitterItem = this.ui.getSelectedItem(this.comboSubmitter);
		if (submitterItem != null) {
			return (HospitalContact)this.ui.getAttachedObject(submitterItem);
		}
		return null;
	}
	
	private Date getDateSubmitted() {
		try {
			String dateString = this.ui.getText(this.textDate);
			if (dateString != null && dateString.length() > 0) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				return dateFormat.parse(dateString);	
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void submitterChanged(Object comboSubmitter, Object textHospital) {
		System.out.println("submitterChanged");
		Object submitterItem = this.ui.getSelectedItem(this.comboSubmitter);
		if (submitterItem != null) {
			HospitalContact submitter = (HospitalContact)this.ui.getAttachedObject(submitterItem);
			this.ui.setText(this.textHospital, submitter.getHospitalId());
		}
		else {
			this.ui.setText(this.textHospital, "");
		}
	}
	
}
