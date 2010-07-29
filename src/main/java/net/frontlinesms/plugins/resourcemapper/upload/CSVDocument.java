package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.Map;
import java.util.HashMap;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload JSON Document
 * @author dalezak
 *
 */
public class CSVDocument extends UploadDocument {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(JSONDocument.class);
	private Map<String, Object> items = new HashMap<String, Object>();
	
	/**
	 * CSVDocument
	 */
	public CSVDocument() {
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
		for (String key : this.items.keySet()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(key);
			sb.append(",");
			sb.append(this.items.get(key));
		}
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(schema);
					sb.append(",");
					sb.append(responseValue);
				}
				else {
					LOG.error("ResponseValue is NULL for '%s'", schema);
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_CSV);
	}
	
	@Override
	public String getPanelXML() {
		return "/ui/plugins/resourcemapper/upload/uploadCSVDocument.xml";
	}

}