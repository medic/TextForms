package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.PolymorphismType;

@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
public class ChecklistMapping extends PlainTextMapping {
	
	@OneToMany(cascade={},targetEntity=BooleanMapping.class,mappedBy="list")
	private List<BooleanMapping> items;

	public ChecklistMapping(String shortCode, String pathToElement, ArrayList<BooleanMapping> items) {
		super(shortCode, pathToElement);
		this.setItems(items);
	}

	public ChecklistMapping(String shortCode, String pathToElement) {
		super(shortCode, pathToElement);
		this.setItems(new ArrayList<BooleanMapping>());
	}

	public void setItems(ArrayList<BooleanMapping> items) {
		this.items = items;
	}

	public List<BooleanMapping> getItems() {
		return items;
	}
	
	public void addItem(BooleanMapping item){
		items.add(item);
	}
	
	public void removceItem(BooleanMapping item){
		items.remove(item);
	}
}
