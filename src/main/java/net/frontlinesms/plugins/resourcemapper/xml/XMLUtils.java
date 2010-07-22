package net.frontlinesms.plugins.resourcemapper.xml;

import java.util.GregorianCalendar;

import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

import org.apache.abdera.model.AtomDate;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.tree.DefaultDocument;

public class XMLUtils {
	
	@SuppressWarnings("unchecked")
	public static Document getInitializedDocument(FieldResponse response){
		DocumentFactory factory = DocumentFactory.getInstance();
		Document doc = factory.createDocument();
		doc.addElement("atom:entry")
				.addNamespace("atom", "http://www.w3.org/2005/Atom")
				.addNamespace("have", "urn:oasis:names:tc:emergency:EDXL:HAVE:1.0");
		handlePath("atom:author/atom:email=" + response.getSubmitter().getPhoneNumber(), doc);
		//add the date updated element
		handlePath("atom:update="+new AtomDate(new GregorianCalendar()).getValue(),doc);
		//add the hospital id
		handlePath("have:Hospital/have:Organization/have:OrganizationInformation/have:OrganizationID="+response.getHospitalId(),doc);
		return doc;
	}
	
	public static Document handlePath(String path, Document doc) {
		if(path.startsWith("/")){
			path = path.split("/", 2)[1];
		}
		if(path.endsWith("/")){
			path = path.substring(0, path.lastIndexOf("/"));
		}
		if(path.contains("/=")){
			path=path.replace("/=", "=");
		}
		if (path.contains("@")) {
			if (path.contains("=")) {
				setAttributeValue(path, doc);
			} else {
				createAttributeIfNotExists(path, doc);
			}
		} else {
			if (path.contains("=")) {
				setElementValue(path, doc);
			} else {
				createPathIfNotExists(path, doc);
			}
		}
		return doc;
	}

	public static void setElementValue(String path, Document doc) {
		String[] parts = path.split("=",2);
		createPathIfNotExists(parts[0], doc);
		doc.getRootElement().selectSingleNode(parts[0]).setText(parts[1]);
	}

	public static Element setAttributeValue(String path, Document doc) {
		String elementPath= path.split("@",2)[0];
		
		String[] attribute = path.split("@",2)[1].split("=",2);
		createPathIfNotExists(elementPath, doc);
		return ((Element) doc.getRootElement().selectSingleNode(elementPath)).addAttribute(attribute[0], attribute[1]);
	}

	public static Element createAttributeIfNotExists(String path, Document doc) {
		String[] parts = path.split("@");
		return createPathIfNotExists(parts[0], doc).addAttribute(parts[1],"");
	}
	
	public static Element createPathIfNotExists(String path, Document document){
		if(path.contains("atom:entry")){
			path = path.substring(path.indexOf("atom:entry")+"atom:entry".length());
		}
		return createPathIfNotExists(path, document.getRootElement());
	}
	
	public static Element createPathIfNotExists(String path, Element element){
		if(path.equals("")){
			return element;
		}
		String[] pathParts = path.split("/",2);
		if(!(element.selectSingleNode(pathParts[0]) == null)){
			return createPathIfNotExists(pathParts[1], (Element) element.selectSingleNode(pathParts[0]));
		}else{
			String[] allPathParts = path.split("/");
			Element currentElement = element;
			for(String s:allPathParts){
				currentElement = currentElement.addElement(s);
			}
			return currentElement;
		}
	}
	
	public static void main(String[] args) {
		Document doc= new DefaultDocument();
		doc.addElement("atom:entry");
		doc.getRootElement().addNamespace("have", "urn:oasis:names:tc:emergency:EDXL:HAVE:1.0");
		handlePath("have:HospitalBedCapacityStatus/have:BedCapacity/have:BedType=MedicalSurgical",doc);
		System.out.println(doc.asXML());
	}
}
