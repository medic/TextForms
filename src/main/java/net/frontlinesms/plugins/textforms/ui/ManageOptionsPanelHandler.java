package net.frontlinesms.plugins.textforms.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.upload.DocumentUploader;
import net.frontlinesms.plugins.textforms.upload.DocumentUploaderFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * ManageOptionsPanelHandler
 * @author dalezak
 *
 */
@SuppressWarnings("unused")
public class ManageOptionsPanelHandler implements ThinletUiEventHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageContactsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/textforms/manageOptionsPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsCallback callback;
	
	private final Object mainPanel;
	private final Object comboUploadDocuments;
	private final Object listBooleanTrue;
	private final Object listBooleanFalse;
	private final Object listInfo;
	private final Object listRegister;
	
	private final Object textUploadURL;
	private final Object checkboxDebugYes;
	private final Object checkboxDebugNo;
	private final Object panelUploadOptions;
	
	public ManageOptionsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback) {
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
		this.ui.setText(this.textUploadURL, TextFormsProperties.getPublishURL());
	}
	
	private void loadDebugMode() {
		if (TextFormsProperties.isDebugMode()) {
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
		DocumentUploader selectedDocumentUploader = TextFormsProperties.getDocumentUploader();
		int index = 1;
		for (DocumentUploader documentUploader : DocumentUploaderFactory.getDocumentUploaders()) {
			documentUploader.setUiGeneratorController(this.ui);
			documentUploader.setApplicationContext(this.appContext);
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
		loadListOptions(this.listBooleanTrue, TextFormsProperties.getBooleanTrueValues());
		loadListOptions(this.listBooleanFalse, TextFormsProperties.getBooleanFalseValues());
	}
	
	private void loadInfoValues() {
		loadListOptions(this.listInfo, TextFormsProperties.getInfoKeywords());
	}
	
	private void loadRegisterValues() {
		loadListOptions(this.listRegister, TextFormsProperties.getRegisterKeywords());
	}
	
	public void uploadDocumentChanged(Object comboUploadDocuments) {
		Object selectedItem = this.ui.getSelectedItem(comboUploadDocuments);
		DocumentUploader documentUploader = selectedItem != null ? (DocumentUploader)this.ui.getAttachedObject(selectedItem) : null; 
		this.ui.removeAll(this.panelUploadOptions);
		if (documentUploader != null) {
			LOG.debug("uploadDocumentChanged: %s", documentUploader.getTitle());
			this.ui.add(this.panelUploadOptions, documentUploader.getMainPanel());
		}
		else {
			LOG.debug("uploadDocumentChanged: NULL");
		}
		TextFormsProperties.setDocumentUploader(documentUploader);
	}
	
	public void uploadUrlChanged(Object textUploadURL) {
		String url = this.ui.getText(textUploadURL);
		LOG.debug("uploadUrlChanged: %s", url);
		TextFormsProperties.setPublishURL(url);
	}
	
	//################ DEBUG ################
	
	public void debugChanged(Object checkboxDebug) {
		if (checkboxDebug == this.checkboxDebugYes) {
			TextFormsProperties.setDebugMode(true);
		}
		else if (checkboxDebug == this.checkboxDebugNo) {
			TextFormsProperties.setDebugMode(false);
		}
	}
	
	//################ COMMON ################
	
	public void textOptionChanged(Object textQuestion, Object list, Object button) {
		if (this.ui.getText(textQuestion).length() > 0) {
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
	
	public void addOption(Object textQuestion, Object list, Object button) {
		String text = this.ui.getText(textQuestion);
		if (text != null && text.length() > 0) {
			this.ui.add(list, this.ui.createListItem(text, text));
			this.ui.setText(textQuestion, "");
			this.ui.setEnabled(button, false);
		}
		saveOptions(list);
	}
	
	public void deleteOption(Object textQuestion, Object list, Object button) {
		Object valueToDelete = this.ui.getSelectedItem(list);
		String valueText = this.ui.getText(valueToDelete);
		LOG.debug("Deleted: %s", valueText);
		this.ui.remove(valueToDelete);
		saveOptions(list);
		this.ui.setEnabled(button, false);
		if (this.ui.getItems(list).length == 0) {
			this.ui.alert(TextFormsMessages.getMessageChoiceRequired());
			this.ui.setFocus(textQuestion);
		}
	}
	
	private void saveOptions(Object list) {
		List<String> options = new ArrayList<String>();
		for (Object item : this.ui.getItems(list)) {
			options.add((String)this.ui.getAttachedObject(item));
		}
		String [] values = options.toArray(new String[options.size()]);
		if (list == this.listBooleanTrue) {
			TextFormsProperties.setBooleanTrueValues(values);
		}
		else if (list == this.listBooleanFalse) {
			TextFormsProperties.setBooleanFalseValues(values);
		}
		else if (list == this.listInfo) {
			TextFormsProperties.setInfoKeywords(values);
		}
		else if (list == this.listRegister) {
			TextFormsProperties.setRegisterKeywords(values);
		}
	}
	
	private void loadListOptions(Object list, String [] options) {
		for (String option : options) {
			this.ui.add(list, this.ui.createListItem(option, option));
		}
	}
}