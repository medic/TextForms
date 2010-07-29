package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.Map;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocument;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload XML Document
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class XMLDocument extends UploadDocument {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(XMLDocument.class);
	
	/**
	 * Map of Namespaces
	 */
	private final Map<String, Namespace> namespaces = new HashMap<String, Namespace>();
	
	/**
	 * XML Document
	 */
	private Document document;
	
	public XMLDocument() {
		
	}
	
	/**
	 * XMLDocument
	 * @param rootElementName root element name
	 */
	public XMLDocument(String rootElementName) {
		DocumentFactory factory = DocumentFactory.getInstance();
		this.document = factory.createDocument();
		if (rootElementName != null) {
			this.document.addElement(rootElementName);
		}
	}
	
	/**
	 * Generate XMlL document for uploading
	 */
	@Override
	public String toString() {
		Element rootElement = this.document.getRootElement();
		//add namespaces
		for (Namespace namespace : this.namespaces.values()) {
			rootElement.addNamespace(namespace.getPrefix(), namespace.getURI());
		}
		//add responses
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					Element entry = rootElement.addElement(schema);
					entry.setText(responseValue);
				}
				else {
					LOG.error("ResponseValue is NULL for %s", schema);
				}
			}
		}
		return this.document.asXML();
	}
	
	/**
	 * Add XML namespace
	 * @param prefix prefix
	 * @param uri uri
	 */
	public void addNamespace(String prefix, String uri) {
		this.namespaces.put(prefix, new Namespace(prefix, uri));
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @return Element
	 */
	public Element addElement(String name) {
		return addElement(null, name, null, null);
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @param value value
	 * @return Element
	 */
	public Element addElement(String name, String value) {
		return addElement(null, name, value, null);
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @param value value
	 * @param namespacePrefix XML namespace prefix
	 * @return Element
	 */
	public Element addElement(String name, String value, String namespacePrefix) {
		return addElement(null, name, value, namespacePrefix);
	}
	
	/**
	 * Add XML Element
	 * @param parent XML parent
	 * @param name name
	 * @param value value
	 * @return Element
	 */
	public Element addElement(Element parent, String name, String value) {
		return addElement(parent, name, value, null);
	}
	
	/**
	 * Add XML Element
	 * @param parent Element parent
	 * @param name element name
	 * @param value element value
	 * @param namespace XML namespace prefix
	 * @return Element
	 */
	public Element addElement(Element parent, String name, String value, String namespacePrefix) {
		if (parent == null) {
			parent = this.document.getRootElement();
		}
		Element element = (namespacePrefix != null && this.namespaces.containsKey(namespacePrefix))
			? parent.addElement(new QName(name, this.namespaces.get(namespacePrefix)))
			: parent.addElement(name);
		if (value != null) {
			element.setText(value);
		}
		return element;
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_XML);
	}
	
	@Override
	public String getPanelXML() {
		return "/ui/plugins/resourcemapper/upload/uploadXMLDocument.xml";
	}
	
	/**
	 * Handle changing of root element name from UI
	 * @param textRootElement component
	 */
	public void rootElementChanged(Object textRootElement) {
		String rootElementName = this.ui.getText(textRootElement);
		LOG.debug("rootElementChanged: %s", rootElementName);
		if (rootElementName != null) {
			if (this.document.getRootElement() != null) {
				this.document.remove(this.document.getRootElement());
			}
			this.document.addElement(rootElementName);
		}
	}
}