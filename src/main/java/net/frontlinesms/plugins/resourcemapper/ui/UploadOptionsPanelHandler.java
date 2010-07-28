package net.frontlinesms.plugins.resourcemapper.ui;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocument;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocumentFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

public class UploadOptionsPanelHandler implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManagePeoplePanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/uploadOptionsPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainPanel;
	private Object comboUploadDocuments;
	
	public UploadOptionsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		LOG.debug("UploadOptionsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.comboUploadDocuments = this.ui.find(this.mainPanel, "comboUploadDocuments");	
		loadUploadDocumentHandlers();
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	private void loadUploadDocumentHandlers() {
		this.ui.add(this.comboUploadDocuments, this.ui.createComboboxChoice("", null));
		for (UploadDocument uploadDocument : UploadDocumentFactory.getHandlerClasses()) {
			Object comboBoxChoice = this.ui.createComboboxChoice(uploadDocument.getTitle(), uploadDocument);
			this.ui.add(this.comboUploadDocuments, comboBoxChoice);
		}
	}
	
	public void uploadDocumentChanged(Object comboUploadDocuments) {
		Object selectedItem = this.ui.getSelectedItem(comboUploadDocuments);
		UploadDocument uploadDocument = selectedItem != null ? (UploadDocument)this.ui.getAttachedObject(selectedItem) : null; 
		if (uploadDocument != null) {
			LOG.debug("uploadDocumentChanged: %s", uploadDocument.getTitle());
		}
		else {
			LOG.debug("uploadDocumentChanged: NULL");
		}
	}
}