package net.frontlinesms.plugins.textforms.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.KeywordDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.hibernate.Pair;

import org.springframework.context.ApplicationContext;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * InfoHandler
 * @author dalezak
 *
 */
public class InfoHandler extends MessageHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(InfoHandler.class);
	
	/**
	 * QuestionDao
	 */
	protected QuestionDao questionDao;
	protected KeywordDao keywordDao;
	
	/**
	 * InfoHandler
	 */
	public InfoHandler() {}
	
	/**
	 * Set ApplicationContext
	 * @param appContext appContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) { 
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		this.keywordDao = (KeywordDao) appContext.getBean("keywordDao", KeywordDao.class);
	}
	
	/**
	 * Get Info keywords
	 */
	@Override
	public List<String> getKeywords() {
		return Arrays.asList(TextFormsProperties.getInfoKeywords());
	}
	
	/**
	 * Handle Info message
	 */
	@Override
	public boolean handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = getWords(message.getTextContent(), 2);
		if (words.length == 2) {// if they are asking for info about a specific field
			Question question = questionDao.getQuestionForKeyword(words[1]);
			if (question != null) {// if the question they are asking about exists
				StringBuilder reply = new StringBuilder(question.getName());
				reply.append(" (");
				reply.append(question.getTypeLabel());
				reply.append(")");
				if (question.getInfoSnippet() != null && question.getInfoSnippet().length() > 0) {
					reply.append(" ");
					reply.append(question.getInfoSnippet());	
				}
				sendReply(message.getSenderMsisdn(), reply.toString(), false);
				return true;
			}
			else {// if the question they are asking about does not exist
				ArrayList<Pair<Float, String>> keywordsAndRating = new ArrayList<Pair<Float,String>>();
				Levenshtein lev = new Levenshtein();
				for(Question q: questionDao.getAllQuestions()){
					keywordsAndRating.add(new Pair(lev.getSimilarity(words[1], q.getKeyword()),q.getKeyword()));
				}
				Collections.sort(keywordsAndRating, new Comparator<Pair<Float,String>>() {
					public int compare(Pair<Float, String> o1, Pair<Float, String> o2) { 
						if(o1.getA() - o2.getA() > 0F){
							return -1;
						}else if(o1.getA() - o2.getA() < 0F){
							return 1;
						}else{
							return 0;
						}
					}
				});
				StringBuilder closestKeywords = new StringBuilder("");
				for(int i = 0; i < 3; i++){
					if(i == 2){
						closestKeywords.append("and ");
					}
					closestKeywords.append(keywordsAndRating.get(i).getB());
					if(i !=2){
						closestKeywords.append(", ");
					}
				}
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeywordExtended(words[1], closestKeywords.toString()), true);
			}		
		}
		else {
			sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerHelp(getAllKeywords()), false);
		}
		return false;
	}
	
	/**
	 * Get all keywords for all Questions
	 * @return
	 */
	private String [] getAllKeywords() {
		List<String> keywords = questionDao.getKeywords();
		return keywords.toArray(new String[keywords.size()]);
	}

}
