package net.frontlinesms.plugins.textforms.handler;

import java.util.Collection;
import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.textforms.TextFormsListener;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.TextFormResponse;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.plugins.textforms.data.repository.TextFormResponseDao;

/**
 * TextFormHandler
 * @author dalezak
 *
 */
public class TextFormHandler extends MessageHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormHandler.class);
	private ContactDao contactDao;
	private TextFormDao textformDao;
	private TextFormResponseDao textformResponseDao;
	
	@Override
	public Collection<String> getKeywords() {
		return textformDao.getKeywords();
	}

	@Override
	public boolean handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = getWords(message.getTextContent(), 2);
		if (words.length > 0) {
			TextForm textform = textformDao.getTextFormByKeyword(words[0]);
			LOG.debug("TextForm: %s", textform != null ? textform.getName() : "NULL");
			Contact contact = getContact(message);
			LOG.debug("Contact: %s", contact != null ? contact.getName() : "NULL");
			if (textform != null && contact != null) {
				TextFormResponse textformResponse = new TextFormResponse();
				textformResponse.setStarted(new Date());
				textformResponse.setContact(contact);
				textformResponse.setTextForm(textform);
				try {
					textformResponseDao.saveTextForm(textformResponse);
					LOG.debug("TextFormResponse Created: %s", textformResponse.getTextFormName());
					Question question = textformResponse.getNextQuestion();
					if (question != null) {
						TextFormsListener.registerTextForm(message.getSenderMsisdn(), textformResponse, question);
						sendReply(message.getSenderMsisdn(), question.toString(true), false);
						LOG.out("%s", question.toString(true));
						return true;	
					}
					else {
						LOG.error("Questions is NULL");
					}
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException %s", ex);
				}
			}
		}
		return false;
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext) {
		textformDao = (TextFormDao)appContext.getBean("textformDao", TextFormDao.class);	
		textformResponseDao = (TextFormResponseDao)appContext.getBean("textformResponseDao", TextFormResponseDao.class);
	}
	
	@Override
	public void setFrontline(FrontlineSMS frontline) {
		super.setFrontline(frontline);
		contactDao = frontline.getContactDao();
	}
	
	private Contact getContact(FrontlineMessage message) {
		return message != null ? contactDao.getFromMsisdn(message.getSenderMsisdn()) : null;
	}
}