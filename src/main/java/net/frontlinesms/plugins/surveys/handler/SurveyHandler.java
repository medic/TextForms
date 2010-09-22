package net.frontlinesms.plugins.surveys.handler;

import java.util.Collection;
import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.surveys.SurveysListener;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.repository.SurveyDao;
import net.frontlinesms.plugins.surveys.data.repository.SurveyResponseDao;

/**
 * SurveyHandler
 * @author dalezak
 *
 */
public class SurveyHandler extends MessageHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveyHandler.class);
	private ContactDao contactDao;
	private SurveyDao surveyDao;
	private SurveyResponseDao surveyResponseDao;
	
	@Override
	public Collection<String> getKeywords() {
		return surveyDao.getKeywords();
	}

	@Override
	public boolean handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = getWords(message.getTextContent(), 2);
		if (words.length > 0) {
			Survey survey = surveyDao.getSurveyByKeyword(words[0]);
			LOG.debug("Survey: %s", survey != null ? survey.getName() : "NULL");
			Contact contact = getContact(message);
			LOG.debug("Contact: %s", contact != null ? contact.getName() : "NULL");
			if (survey != null && contact != null) {
				SurveyResponse surveyResponse = new SurveyResponse();
				surveyResponse.setStarted(new Date());
				surveyResponse.setContact(contact);
				surveyResponse.setSurvey(survey);
				try {
					surveyResponseDao.saveSurvey(surveyResponse);
					LOG.debug("SurveyResponse Created: %s", surveyResponse.getSurveyName());
					Question question = surveyResponse.getNextQuestion();
					if (question != null) {
						SurveysListener.registerSurvey(message.getSenderMsisdn(), surveyResponse, question);
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
		surveyDao = (SurveyDao)appContext.getBean("surveyDao", SurveyDao.class);	
		surveyResponseDao = (SurveyResponseDao)appContext.getBean("surveyResponseDao", SurveyResponseDao.class);
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