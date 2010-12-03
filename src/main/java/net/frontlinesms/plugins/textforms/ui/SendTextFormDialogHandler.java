package net.frontlinesms.plugins.textforms.ui;

import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.textforms.TextFormsListener;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsPluginController;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.TextFormResponse;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.TextFormResponseDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * SendTextFormDialogHandler
 * @author dalezak
 *
 */
@SuppressWarnings("unused")
public class SendTextFormDialogHandler extends ExtendedThinlet implements ThinletUiEventHandler {

	private static final long serialVersionUID = 1L;
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(SendTextFormDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/textforms/sendTextFormDialog.xml";
	
	private final FrontlineSMS frontline;
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsPluginController pluginController;
	
	private TextForm textform;
	private final ContactDao contactDao;
	private final TextFormResponseDao textformResponseDao;
	
	private final Object mainDialog;
	private final Object tblContacts;
	private final Object btnSend;
	
	public SendTextFormDialogHandler(FrontlineSMS frontlineController, UiGeneratorController ui, ApplicationContext appContext, TextFormsPluginController pluginController) { 
		LOG.debug("SendTextFormDialogHandler");
		this.frontline = frontlineController;
		this.ui = ui;
		this.appContext = appContext;
		this.pluginController = pluginController;
		
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.contactDao = frontlineController.getContactDao();
		this.textformResponseDao = pluginController.getTextFormResponseDao();
		
		this.tblContacts = this.ui.find(this.mainDialog, "tblContacts");
		this.btnSend = this.ui.find(this.mainDialog, "btnSend");
	}
	
	public void show(TextForm textform) {
		LOG.debug("show");
		this.textform = textform;
		ui.removeAll(tblContacts);
		for (Contact contact : contactDao.getAllContacts()) {
			ui.add(tblContacts, getRow(contact));
		}
		ui.setEnabled(btnSend, false);
		ui.add(mainDialog);
	}
	
	public void sendTextForm(Object tblContacts) {
		LOG.debug("sendTextForm");
		for(Object selectedItem : ui.getSelectedItems(tblContacts)) {
			Contact contact = ui.getAttachedObject(selectedItem, Contact.class);
			TextFormResponse textformResponse = new TextFormResponse();
			textformResponse.setStarted(new Date());
			textformResponse.setContact(contact);
			textformResponse.setTextForm(textform);
			try {
				textformResponseDao.saveTextForm(textformResponse);
				LOG.debug("TextFormResponse Created: %s", textformResponse.getTextFormName());
				Question question = textformResponse.getNextQuestion();
				if (question != null) {
					TextFormsListener.registerTextForm(contact.getPhoneNumber(), textformResponse, question);
					frontline.sendTextMessage(contact.getPhoneNumber(), question.toString(true));
					LOG.debug("%s", question.toString(true));
				}
				else {
					LOG.error("Questions is NULL");
				}
			} 
			catch (DuplicateKeyException ex) {
				LOG.error("DuplicateKeyException %s", ex);
			}
		}
		this.ui.remove(mainDialog);
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void contactsChanged(Object tblContacts) {
		ui.setEnabled(btnSend, ui.getSelectedItems(tblContacts).length > 0);
	}
	
	public Object getRow(Contact contact){
		Object row = this.ui.createTableRow(contact);
		this.createTableCell(row, contact.getName());
		this.createTableCell(row, contact.getPhoneNumber());
		return row;
	}
}