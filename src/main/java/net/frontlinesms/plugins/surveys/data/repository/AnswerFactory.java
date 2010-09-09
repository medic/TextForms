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
package net.frontlinesms.plugins.surveys.data.repository;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

/*
 * AnswerFactory
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public final class AnswerFactory {

	private static SurveysLogger LOG = SurveysLogger.getLogger(AnswerFactory.class);
	
	/**
	 * Get list of Answer classes
	 * (To add a new Reminder classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.surveys.data.domain.answers.Answer
	 * with the full package and class name of the new implementing Reminder class)
	 */
	public static List<Answer> getAnswerClasses() {
		if (answerClasses == null) {
			answerClasses = new ArrayList<Answer>();
			for (Answer answer : ServiceLoader.load(Answer.class)) {
				LOG.debug("Answer Discovered: %s", answer.getClass().getSimpleName());
				answerClasses.add(answer);
		    }
		}
		return answerClasses;
	}private static List<Answer> answerClasses = null;
	
	/**
	 * Create instance of QuestionRespone for associated Question question
	 * @param message FrontlineMessage
	 * @param submitter HospitalContact
	 * @param dateSubmitted date submitted
	 * @param hospitalId Hospital ID
	 * @param question Question
	 * @return Answer
	 */
	public static Answer createAnswer(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, Question question) {
		for (Answer answerClass : getAnswerClasses()) {
			if (answerClass.isAnswerFor(question)) {
				try {
					Answer answer = answerClass.getClass().newInstance();
					answer.setMessage(message);
					answer.setSubmitter(submitter);
					answer.setDateSubmitted(dateSubmitted);
					answer.setHospitalId(hospitalId);
					answer.setQuestion(question);
					LOG.debug("Answer Created: %s", answer.getClass().getSimpleName());
					return answer;
				}
				catch (InstantiationException ex) {
					LOG.error("InstantiationException: %s", ex);
				} 
				catch (IllegalAccessException ex) {
					LOG.error("InstantiationException: %s", ex);
				}
			}
		}
		LOG.error("Unable to find class for question: %s", question.getType());
		return null;
	}
	
}