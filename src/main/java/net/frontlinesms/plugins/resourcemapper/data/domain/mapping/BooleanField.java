package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value = "boolean")
public class BooleanField extends CodedField {
	
	@ManyToOne(cascade={},targetEntity=ChecklistField.class)
	private ChecklistField list;
	
	public BooleanField(String fullName, String abbreviation) {
		super(fullName, abbreviation, null);
		Set<String> yesAndNo = new HashSet<String>();
		//the only possibilities are 0 and 1, since this is a boolean field
		yesAndNo.add("0");
		yesAndNo.add("1");
		super.setPossibleResponses(yesAndNo);
	}
	
	public BooleanField(){super();}

	public void setList(ChecklistField list) {
		this.list = list;
	}

	public ChecklistField getList() {
		return list;
	}
	
	public boolean isOnChecklist(){
		return list !=null;
	}
	
	public String getType() {
		return "boolean";
	}
	
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_BOOLEAN);
	}
}
