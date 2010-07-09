package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

/**
 * Inteface for Field classes
 * @author Dale Zak
 *
 */
public interface Field {
	public long getMid();
	
	public String getFullName();
	public void setFullName(String fullName);
	
	public String getAbbreviation();
	public void setAbbreviation(String abbreviation);
	
	public String getInfoSnippet();
	
	public void setInfoSnippet(String infoSnippet);
	
	public String getSchemaName();
	public void setSchemaName(String schemaName);
	
	public String getPathToElement();

	public String [] getAdditionalInstructions();

	public void addInstruction(String string);
}