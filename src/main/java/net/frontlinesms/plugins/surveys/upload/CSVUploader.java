package net.frontlinesms.plugins.surveys.upload;

import java.util.Map;
import java.util.HashMap;

import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload CSV Document
 * @author dalezak
 *
 */
public class CSVUploader extends DocumentUploader {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(CSVUploader.class);
	
	/**
	 * Collection of additional items
	 */
	private Map<String, Object> items = new HashMap<String, Object>();
	
	/**
	 * CSVUploader
	 */
	public CSVUploader() {
	}

	public void addItem(String key, Object value) {
		this.items.put(key, value);
	}
	
	public Map<String, Object> getItems() {
		return this.items;
	}
	
	/**
	 * Generate CSV string for uploading
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//author
		if (this.phoneNumber != null) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append("author,");
			sb.append(this.phoneNumber);
		}
		//hospital id
		if (this.hospitalId != null) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append("hospital,");
			sb.append(this.hospitalId);
		}
		//items
		for (String key : this.items.keySet()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(key);
			sb.append(",");
			sb.append(this.items.get(key));
		}
		//responses
		for (Answer answer : this.getAnswers()) {
			String schema = answer.getQuestionSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = answer.getAnswerValue();
				if (responseValue != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(schema);
					sb.append(",");
					sb.append(responseValue);
				}
				else {
					LOG.error("AnswerValue is NULL for '%s'", schema);
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(SurveysConstants.DOCUMENT_UPLOAD_CSV);
	}
	
	@Override
	protected String getPanelXML() {
		return "/ui/plugins/surveys/upload/CSVUploader.xml";
	}
	
	/**
	 * Get ContentType
	 */
	@Override
	public String getContentType() {
		return "text/csv";
	}

}