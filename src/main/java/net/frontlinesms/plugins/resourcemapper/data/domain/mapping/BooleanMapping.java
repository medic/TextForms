package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.PolymorphismType;

@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
public class BooleanMapping extends CodedMapping {
	
	@ManyToOne(cascade={},targetEntity=ChecklistMapping.class)
	private ChecklistMapping list;
	
	public BooleanMapping(String shortCode, String pathToElement) {
		super(shortCode, pathToElement, new String[]{"responses.yes","responses.no"});
	}
	
	public BooleanMapping(){super();}

	public void setList(ChecklistMapping list) {
		this.list = list;
	}

	public ChecklistMapping getList() {
		return list;
	}
	
	public boolean isOnChecklist(){
		return list !=null;
	}
}
