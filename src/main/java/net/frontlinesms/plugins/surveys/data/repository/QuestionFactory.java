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

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

/*
 * QuestionFactory
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public final class QuestionFactory {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(QuestionFactory.class);
	
	/**
	 * Get list of Question classes
	 * (To add a new Question classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.surveys.data.domain.questions.Question
	 * with the full package and class name of the new implementing Question class)
	 */
	public static List<Question> getQuestionClasses() {
		if (questionClasses == null) {
			questionClasses = new ArrayList<Question>();
			for (Question question : ServiceLoader.load(Question.class)) {
				LOG.debug("Question Discovered: %s", question.getClass().getSimpleName());
				questionClasses.add(question);
		    }
		}
		return questionClasses;
	}private static List<Question> questionClasses = null;
	
	/**
	 * Create instance of Question for associated type
	 * @param name Question name
	 * @param keyword Question keyword
	 * @param infoSnippet Question info snippet
	 * @param type Question type
	 * @param choices Question choices
	 * @return Question
	 */
	public static Question createQuestion(String name, String keyword, String infoSnippet, String type, String schemaName, List<String> choices) {
		for (Question questionClass : getQuestionClasses()) {
			if (questionClass.getType().equalsIgnoreCase(type)) {
				try {
					Question question = questionClass.getClass().newInstance();
					question.setName(name);
					question.setKeyword(keyword);
					question.setInfoSnippet(infoSnippet);
					question.setSchemaName(schemaName);
					question.setChoices(choices);
					LOG.debug("Question Created: %s", question.getClass().getSimpleName());
					return question;
				} 
				catch (InstantiationException ex) {
					LOG.error("InstantiationException: %s", ex);
				} 
				catch (IllegalAccessException ex) {
					LOG.error("InstantiationException: %s", ex);
				}
			}
		}
		LOG.error("Unable to find class for question: %s", type);
		return null;
	}
	
}