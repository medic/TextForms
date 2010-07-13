package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value = "checklist")
public class ChecklistField extends PlainTextField {
	
	@OneToMany(cascade={},targetEntity=BooleanField.class,mappedBy="list")
	private List<BooleanField> items;

	public ChecklistField(String fullName, String abbreviation, List<BooleanField> items) {
		super(fullName, abbreviation);
		this.setItems(items);
	}

	public ChecklistField(String shortCode, String pathToElement) {
		super(shortCode, pathToElement);
		this.setItems(new ArrayList<BooleanField>());
	}

	public void setItems(List<BooleanField> items) {
		this.items = items;
	}

	public List<BooleanField> getItems() {
		return items;
	}
	
	public void addItem(BooleanField item){
		items.add(item);
	}
	
	public void removceItem(BooleanField item){
		items.remove(item);
	}
	
	public String getType() {
		return "checklist";
	}
	
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_CHECKLIST);
	}
}
