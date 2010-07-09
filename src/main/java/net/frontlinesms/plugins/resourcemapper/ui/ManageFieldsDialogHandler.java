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
 * ManageFieldsDialogHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageFieldsDialogHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(ManageFieldsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/resourcemapper/manageFieldsDialog.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainDialog;
	private Field field;
	private Object textName;
	private Object textAbbreviation;
	private Object textInfo;
	private Object comboTypes;
	private Object listChoices; 
	
	public ManageFieldsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textAbbreviation = this.ui.find(this.mainDialog, "textAbbreviation");
		this.textInfo = this.ui.find(this.mainDialog, "textInfo");
		this.comboTypes = this.ui.find(this.mainDialog, "comboTypes");
		this.listChoices = this.ui.find(this.mainDialog, "listChoices");
	}
	
	public void show(Field field) {
		this.field = field;
		if (field != null) {
			this.ui.setText(this.textName, field.getFullName());
			this.ui.setText(this.textAbbreviation, field.getAbbreviation());
			this.ui.setText(this.textInfo, field.getInfoSnippet());
			//TODO set comboTypes & listChoices
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textAbbreviation, "");
			this.ui.setText(this.textInfo, "");
			this.ui.setSelectedIndex(this.comboTypes, -1);
			this.ui.setVisible(this.listChoices, false);
			this.ui.removeAll(this.listChoices);
		}
		this.ui.add(this.mainDialog);
	}
	
	public void saveField(Object dialog) {
		System.out.println("saveField");
		this.callback.refreshField(this.field);
		this.ui.remove(dialog);
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
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
}
