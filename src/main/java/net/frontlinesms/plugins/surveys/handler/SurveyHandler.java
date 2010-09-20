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
	private SurveyDao surveyDao;
	private SurveyResponseDao surveyResponseDao;
	private ContactDao contactDao;
	
	@Override
	public Collection<String> getKeywords() {
		return surveyDao.getKeywords();
	}

	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = toWords(message.getTextContent(), 2);
		if (words.length > 0) {
			Survey survey = surveyDao.getSurveyByKeyword(words[0]);
			Contact contact = getContact(message);
			if (survey != null && contact != null) {
				SurveyResponse surveyResponse = new SurveyResponse();
				surveyResponse.setContact(contact);
				surveyResponse.setSurvey(survey);
				surveyResponse.setStarted(new Date());
				try {
					surveyResponseDao.saveSurvey(surveyResponse);
					LOG.debug("SurveyResponse Created: %s", surveyResponse.getSurveyName());
					
					Question question = survey.getQuestions().get(0);
					SurveysListener.registerSurvey(message.getSenderMsisdn(), surveyResponse, question);
					sendReply(message.getSenderMsisdn(), getQuestionText(question, true), false);
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException %s", ex);
				}
			}
		}
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
		return contactDao.getFromMsisdn(message.getSenderMsisdn());
	}
}