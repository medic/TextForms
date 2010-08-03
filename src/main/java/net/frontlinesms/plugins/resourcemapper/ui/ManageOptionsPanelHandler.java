package net.frontlinesms.plugins.resourcemapper.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.upload.DocumentUploader;
import net.frontlinesms.plugins.resourcemapper.upload.DocumentUploaderFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

public class ManageOptionsPanelHandler implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManagePeoplePanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/manageOptionsPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainPanel;
	private Object comboUploadDocuments;
	private Object listBooleanTrue;
	private Object listBooleanFalse;
	private Object listInfo;
	private Object listRegister;
	
	private Object textUploadURL;
	private Object checkboxDebugYes;
	private Object checkboxDebugNo;
	private Object panelUploadOptions;
	
	public ManageOptionsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		LOG.debug("ManageOptionsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.comboUploadDocuments = this.ui.find(this.mainPanel, "comboUploadDocuments");	
		this.listBooleanTrue = this.ui.find(this.mainPanel, "listBooleanTrue");	
		this.listBooleanFalse = this.ui.find(this.mainPanel, "listBooleanFalse");	
		this.listInfo = this.ui.find(this.mainPanel, "listInfo");	
		this.listRegister = this.ui.find(this.mainPanel, "listRegister");	
		this.textUploadURL = this.ui.find(this.mainPanel, "textUploadURL");	
		this.checkboxDebugYes = this.ui.find(this.mainPanel, "checkboxDebugYes");	
		this.checkboxDebugNo = this.ui.find(this.mainPanel, "checkboxDebugNo");	
		this.panelUploadOptions = this.ui.find(this.mainPanel, "panelUploadOptions");	
		
		loadDebugMode();
		loadUploadURL();
		loadUploadDocuments();
		loadBooleanValues();
		loadInfoValues();
		loadRegisterValues();
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	private void loadUploadURL() {
		this.ui.setText(this.textUploadURL, ResourceMapperProperties.getPublishURL());
	}
	
	private void loadDebugMode() {
		if (ResourceMapperProperties.isDebugMode()) {
			this.ui.setSelected(this.checkboxDebugYes, true);
			this.ui.setSelected(this.checkboxDebugNo, false);
		}
		else {
			this.ui.setSelected(this.checkboxDebugYes, false);
			this.ui.setSelected(this.checkboxDebugNo, true);
		}
	}
	
	private void loadUploadDocuments() {
		this.ui.add(this.comboUploadDocuments, this.ui.createComboboxChoice("", null));
		DocumentUploader selectedDocumentUploader = ResourceMapperProperties.getDocumentUploader();
		int index = 1;
		for (DocumentUploader documentUploader : DocumentUploaderFactory.getDocumentUploaders()) {
			documentUploader.setUiGeneratorController(this.ui);
			Object comboBoxChoice = this.ui.createComboboxChoice(documentUploader.getTitle(), documentUploader);
			this.ui.add(this.comboUploadDocuments, comboBoxChoice);
			if (documentUploader == selectedDocumentUploader) {
				this.ui.setSelectedIndex(this.comboUploadDocuments, index);
			}
			index++;
		}
		uploadDocumentChanged(this.comboUploadDocuments);
	}
	
	private void loadBooleanValues() {
		loadListOptions(this.listBooleanTrue, ResourceMapperProperties.getBooleanTrueValues());
		loadListOptions(this.listBooleanFalse, ResourceMapperProperties.getBooleanFalseValues());
	}
	
	private void loadInfoValues() {
		loadListOptions(this.listInfo, ResourceMapperProperties.getInfoKeywords());
	}
	
	private void loadRegisterValues() {
		loadListOptions(this.listRegister, ResourceMapperProperties.getRegisterKeywords());
	}
	
	public void uploadDocumentChanged(Object comboUploadDocuments) {
		Object selectedItem = this.ui.getSelectedItem(comboUploadDocuments);
		DocumentUploader documentUploader = selectedItem != null ? (DocumentUploader)this.ui.getAttachedObject(selectedItem) : null; 
		this.ui.removeAll(this.panelUploadOptions);
		if (documentUploader != null) {
			LOG.debug("uploadDocumentChanged: %s", documentUploader.getTitle());
			try {
				Object panel = this.ui.loadComponentFromFile(documentUploader.getPanelXML(), documentUploader);
				this.ui.add(this.panelUploadOptions, panel);
			}
			catch (RuntimeException ex) {
				LOG.error("RuntimeException: %s", ex);
			}
		}
		else {
			LOG.debug("uploadDocumentChanged: NULL");
		}
		ResourceMapperProperties.setDocumentUploader(documentUploader);
	}
	
	public void uploadUrlChanged(Object textUploadURL) {
		String url = this.ui.getText(textUploadURL);
		LOG.debug("uploadUrlChanged: %s", url);
		ResourceMapperProperties.setPublishURL(url);
	}
	
	//################ DEBUG ################
	
	public void debugChanged(Object checkboxDebug) {
		if (checkboxDebug == this.checkboxDebugYes) {
			ResourceMapperProperties.setDebugMode(true);
		}
		else if (checkboxDebug == this.checkboxDebugNo) {
			ResourceMapperProperties.setDebugMode(false);
		}
	}
	
	//################ COMMON ################
	
	public void textOptionChanged(Object textField, Object list, Object button) {
		if (this.ui.getText(textField).length() > 0) {
			this.ui.setEnabled(button, true);
		}
		else {
			this.ui.setEnabled(button, false);
		}
	}
	
	public void optionChanged(Object list, Object button) {
		if (this.ui.getSelectedItem(list) != null) {
			this.ui.setEnabled(button, true);
		}
		else {
			this.ui.setEnabled(button, false);
		}
	}
	
	public void addOption(Object textField, Object list, Object button) {
		String text = this.ui.getText(textField);
		if (text != null && text.length() > 0) {
			this.ui.add(list, this.ui.createListItem(text, text));
			this.ui.setText(textField, "");
			this.ui.setEnabled(button, false);
		}
		saveOptions(list);
	}
	
	public void deleteOption(Object textField, Object list, Object button) {
		Object valueToDelete = this.ui.getSelectedItem(list);
		String valueText = this.ui.getText(valueToDelete);
		LOG.debug("Deleted: %s", valueText);
		this.ui.remove(valueToDelete);
		saveOptions(list);
		this.ui.setEnabled(button, false);
		if (this.ui.getItems(list).length == 0) {
			this.ui.alert(ResourceMapperMessages.getMessageChoiceRequired());
			this.ui.setFocus(textField);
		}
	}
	
	private void saveOptions(Object list) {
		List<String> options = new ArrayList<String>();
		for (Object item : this.ui.getItems(list)) {
			options.add((String)this.ui.getAttachedObject(item));
		}
		String [] values = options.toArray(new String[options.size()]);
		if (list == this.listBooleanTrue) {
			ResourceMapperProperties.setBooleanTrueValues(values);
		}
		else if (list == this.listBooleanFalse) {
			ResourceMapperProperties.setBooleanFalseValues(values);
		}
		else if (list == this.listInfo) {
			ResourceMapperProperties.setInfoKeywords(values);
		}
		else if (list == this.listRegister) {
			ResourceMapperProperties.setRegisterKeywords(values);
		}
	}
	
	private void loadListOptions(Object list, String [] options) {
		for (String option : options) {
			this.ui.add(list, this.ui.createListItem(option, option));
		}
	}
}