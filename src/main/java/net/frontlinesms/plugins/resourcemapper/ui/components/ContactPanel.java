//package net.frontlinesms.plugins.resourcemapper.ui.components;
//
//import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import net.frontlinesms.data.DuplicateKeyException;
//import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
//import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
//import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalReportDao;
//import net.frontlinesms.ui.UiGeneratorController;
//
//import org.springframework.context.ApplicationContext;
//public class ContactPanel {
//	protected UiGeneratorController uiController;
//	protected Object mainPanel;
//	protected HospitalContact person;
//	protected boolean inEditingMode;
//	protected boolean isNewPersonPanel;
//	protected ApplicationContext appCon;
//	protected HospitalContactDao contactDao;
//	protected boolean isNewPersonPanel;
//	protected boolean isNewPersonPanel;

//	protected HospitalReportDao reportDao;
//	
//	
//	private static final String DEFAULT_TITLE="contactpanel.default.title";
//	private static final String EDITING_TITLE="contactpanel.editing.title";
//	private static final String ADDING_TITLE="contactpanel.adding.title";
//	
//	private static final String NAME_LABEL="contactpanel.name.label";
//	private static final String EMAIL_LABEL="contactpanel.email.label";
//	private static final String PHONE_NUMBER_LABEL="contactpanel.phone.number.label";
//	private static final String HOSPITAL_ID_LABEL="contactpanel.hospital.id.label";
//	private static final String LAST_REPORT_LABEL="contactpanel.last.report.label";
//	
//	/**
//	 * The general constructor that creates a panel for person p. If person p is null,
//	 * it creates an 'add person' panel
//	 * @param uiController
//	 * @param p
//	 */
//	public ContactPanel(UiGeneratorController uiController, ApplicationContext appCon,HospitalContact p){
//		this.uiController= uiController;
//		this.appCon = appCon;
//		this.mainPanel = uiController.create("panel");
//		uiController.setInteger(mainPanel, "weightx", 1);
//		if(p != null){
//			isNewPersonPanel=false;
//			inEditingMode=false;
//			this.person = p;
//			addNonEditableFields();
//		}else{
//			isNewPersonPanel=true;
//			inEditingMode=true;
//			addEditableFields();
//		}
//		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
//		reportDao = (HospitalReportDao) appCon.getBean("hospitalReportDao");
//	}
//	
//	/**
//	 * A constructor for creating person panels that are meant to add new people to the system
//	 * @param uiController the UI controller
//	 */
//	public ContactPanel(UiGeneratorController uiController, ApplicationContext appCon){
//		this.uiController= uiController;
//		this.appCon = appCon;
//		this.mainPanel = uiController.create("panel");
//		isNewPersonPanel=true;
//		inEditingMode=true;
//		uiController.setInteger(mainPanel, "weightx", 1);
//		addEditableFields();
//		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
//		reportDao = (HospitalReportDao) appCon.getBean("hospitalReportDao");
//	}
//	
//	/**
//	 * Should perform a save operation on whatever person type the implementing class is for
//	 */
//	protected void savePerson(){
//		try {
//			contactDao.saveHospitalContact(person);
//		} catch (DuplicateKeyException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Should perform an update operation on whatever person type the implementing class is for
//	 */
//	protected void updatePerson(){
//		try {
//			contactDao.updateHospitalContact(person);
//		} catch (DuplicateKeyException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Should create a person of whichever person type the implementing class is for.
//	 * Should do this by instantiating the "person" field
//	 */
//	protected void createPerson(){
//		person = new HospitalContact();
//	}
//	
//	/**
//	 * creates a new panel with non-editable fields for the person that this class was initialized with
//	 * This method adds the first four fields: name, id, gender, and age, and then calls the
//	 * abstract method addAdditionalFields, which should be implemented to do additional
//	 * displaying of information in subclasses
//	 */
//	private void addNonEditableFields(){
//		uiController.removeAll(mainPanel);
//		inEditingMode = false;
//		//add the core fields
//		addLabelToLabelPanel(person.getDisplayName());
//		addLabelToLabelPanel(getI18NString(EMAIL_LABEL) + ": "+ person.getEmailAddress());
//		if(!(person.getName()==null) && !person.getName().equals("")){
//			addLabelToLabelPanel(getI18NString(PHONE_NUMBER_LABEL) + ": "+ person.getPhoneNumber());
//		}
//		addLabelToLabelPanel(getI18NString(HOSPITAL_ID_LABEL) + ": "+ person.getHospitalId());
//		addLabelToLabelPanel(getI18NString(LAST_REPORT_LABEL) + ": "+ reportDao.getLastReportDateForContact(person));
//		uiController.setText(mainPanel, getI18NString(DEFAULT_TITLE));
//	}
//	
//	/**
//	 * Replaces all labels in the person panel with editable controls for modifying the person's data
//	 */
//	private void addEditableFields() {
//		uiController.removeAll(mainPanelContainer);
//		mainPanel = uiController.loadComponentFromFile(PERSON_PANEL_XML,this);
//		uiController.setAction(uiController.find(mainPanel,"imagePanel"),"imageClicked()", null, this);
//		//set the edit image button
//		uiController.setIcon(uiController.find(mainPanel,"imagePanel"), "/icons/patientview/blank_person_edit.png");
//		//get the panel with all the labels in it and remove everything
//		Object labelPanel = getLabelPanel();
//		uiController.removeAll(labelPanel);
//		//create and add the thinlet form fields
//		//the name field
//		NameField name = new NameField(uiController,isNewPersonPanel?"":person.getName());
//		uiController.setInteger(name.getThinletPanel(),"colspan",1);
//		uiController.add(labelPanel,name.getThinletPanel());
//		//the gender field
//		GenderComboBox gender = new GenderComboBox(uiController,isNewPersonPanel?null:person.getGender());
//		uiController.add(labelPanel,gender.getThinletPanel());
//		//the birthdate field
//		BirthdateField bday = new BirthdateField(uiController,isNewPersonPanel?new Date():person.getBirthdate());
//		uiController.add(labelPanel,bday.getThinletPanel());
//		addAdditionalEditableFields();
//		inEditingMode = true;
//		if(isNewPersonPanel){
//			uiController.setText(mainPanel, getAddingTitle());
//		}else{
//			uiController.setText(mainPanel, getEditingTitle());			
//		}
//		uiController.add(getLabelPanel(),getSaveCancelButtons());
//		uiController.add(mainPanelContainer,mainPanel);
//	}
//	
//	/**
//	 * Updates the person to reflect all the responses to the fields
//	 * and then writes the changes to the database
//	 */
//	private boolean validateAndSaveFieldResponses(){
//		if(isNewPersonPanel && person ==null){
//			createPerson();
//		}
//		//get all the fields
//		boolean isValid = true;
//		ArrayList<PersonalFormField> fields = getFieldsInLabelPanel();
//		for(PersonalFormField pff: fields){
//			//if the field is valid, and has changed, then set the value
//			if(pff.isValid() && pff.hasChanged()){
//				pff.setFieldForPerson(person);
//			}else if(!pff.isValid()){
//				uiController.alert(getI18NString("personpanel.edit.details.error.prefix")+" \""+pff.getLabel()+"\" "+getI18NString("personpanel.edit.details.error.suffix"));
//				
//				isValid=false;
//				break;
//			}
//		}
//		if(isValid){
//			//now, save the fields
//			if(isNewPersonPanel){
//				savePerson();
//				isNewPersonPanel = false;
//				if(delegate !=null){
//					delegate.didCreatePerson();
//				}
//			}else{
//				updatePerson();
//			}
//		}
//		return isValid;
//	}
//	
//	/**
//	 * @return a Thinlet panel with 2 buttons inside of it for saving and cancelling
//	 */
//	private Object getSaveCancelButtons(){
//		Object saveCancelPanel = uiController.create("panel");
//		uiController.setInteger(saveCancelPanel, "columns", 2);
//		uiController.setInteger(saveCancelPanel, "gap", 10);
//		uiController.setInteger(saveCancelPanel, "right", 10);
//		uiController.setChoice(saveCancelPanel, "halign", "fill");
//		uiController.setInteger(saveCancelPanel, "weightx", 1);
//		Object saveButton = uiController.createButton(getI18NString("detailview.buttons.save"));
//		uiController.setAction(saveButton, "stopEditingWithSave()", null, this);
//		uiController.setChoice(saveButton, "halign", "left");
//		Object cancelButton = uiController.createButton(getI18NString("detailview.buttons.cancel"));
//		uiController.setAction(cancelButton, "stopEditingWithoutSave()", null, this);
//		uiController.setChoice(cancelButton, "halign", "right");
//		uiController.add(saveCancelPanel, saveButton);
//		uiController.add(saveCancelPanel, cancelButton);
//		return saveCancelPanel;
//	}
//	
//	public Object getLabelPanel(){
//		return uiController.find(mainPanel, "labelPanel");
//	}
//	
//	/**
//	 * All the panels in the label panel should have ThinletFormFields attached to them.
//	 * All of these thinlet form fields should be PersonalFormFields.
//	 * This method goes through each panel in the label panel, gets it's attached object,
//	 * and adds it to an arraylist if it conforms to expectations
//	 * @return the arraylist of PersonalFormFields in the label panel
//	 */
//	public ArrayList<PersonalFormField> getFieldsInLabelPanel(){
//		if(inEditingMode){
//			ArrayList<PersonalFormField> fields = new ArrayList<PersonalFormField>();
//			Object [] arrayFields = uiController.getItems(getLabelPanel());
//			for(Object o: arrayFields){
//				Object f = uiController.getAttachedObject(o);
//				if(f instanceof PersonalFormField){
//					fields.add((PersonalFormField) f);
//				}
//			}
//			return fields;
//		}else{
//			return null;
//		}
//	}
//	
//	/**
//	 * Switches the mode to editing mode, where the user can edit the person's core info
//	 */
//	public void switchToEditingPanel(){
//		addEditableFields();
//	}
//	
//	/**
//	 * Switches from editing mode back to normal mode, saving any changes that have occurred
//	 */
//	public void stopEditingWithSave(){
//		if(validateAndSaveFieldResponses()){
//			addNonEditableFields();
//		}
//	}
//	
//	/**
//	 * Switches from editing mode back to normal mode, without saving any changes
//	 */
//	public void stopEditingWithoutSave(){
//		if(isNewPersonPanel){
//			uiController.removeAll(mainPanel);
//			return;
//		}
//		addNonEditableFields();
//	}
//	
//	public Object getMainPanel(){
//		return mainPanel;
//	}
//	
//	/**
//	 * Used to add descriptive labels to the space next to the picture.
//	 * Used to add name, gender, phone number, etc..
//	 * @param text The text for the label
//	 */
//	protected void addLabelToLabelPanel(String text){
//		Object label = uiController.createLabel(text);
//		uiController.setInteger(label, "weightx", 1);
//		uiController.setInteger(label, "weighty", 1);
//		uiController.add(getLabelPanel(),label);
//	}
//	
//	protected void setNameLabel(String name){
//		uiController.setText(uiController.find(mainPanel,"nameLabel"), name);
//	}
//	
//	public void setPanelTitle(String title){
//		uiController.setText(mainPanel,title);
//	}
//	
//	
//
//}
