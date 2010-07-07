package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.PolymorphismType;

@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
public class BooleanField extends CodedField {
	
	@ManyToOne(cascade={},targetEntity=ChecklistField.class)
	private ChecklistField parentList;
	
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
		this.parentList = list;
	}

	public ChecklistField getList() {
		return parentList;
	}
	
	public boolean isOnChecklist(){
		return parentList !=null;
	}
}
