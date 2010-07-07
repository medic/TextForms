package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Message;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.repository.BooleanMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.CodedMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.PlainTextMappingDao;
import net.frontlinesms.plugins.resourcemapper.handler.InfoHandler;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.BooleanHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.CallbackHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.CodedHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.PlainTextHandler;
import net.frontlinesms.plugins.resourcemapper.ui.ResourceMapperThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name="Resource Mapper", iconPath="/icons/small_rmapper.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper.hibernate.cfg.xml")
public class ResourceMapperPluginController extends BasePluginController implements IncomingMessageListener{

	private FrontlineSMS frontlineController;
	
	/** The Application Context for fetching daos and other Spring stuff */
	private ApplicationContext appCon;
	
	HospitalContactDao contactDao;
	
	private Object mainTab;
	
	private List<MessageHandler> listeners;
	
	private static ArrayList<CallbackInfo> callbacks;
	
	private ResourceMapperThinletTabController tabController;
	
	public String getName() {
		return getI18NString("resourcemapper.tab.title");
	}

	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		tabController = new ResourceMapperThinletTabController(uiController,appCon);
		return tabController.getTab();
	}

	public Object getTab(){
		return mainTab;
	}
	
	public void deinit() {
		
	}
	
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext)	throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		frontlineController.addIncomingMessageListener(this);
		this.appCon = applicationContext;
		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
		createDummyData();
		initListeners();
		try{
		if(ResourceMapperProperties.getInstance().isInDebugMode()){
			startDebugTerminal();
		}
		}catch(Throwable t){
			System.out.println("Error in the debug terminal");
			t.printStackTrace();
		}
	}
	
	private void startDebugTerminal() {
		boolean cont = true;
		System.out.println("Enter a message for the system");
		Scanner s = new Scanner(System.in);
		int i = getRandomContactNumber();
		int j = getRandomContactNumber();
		int k = getRandomContactNumber();
		int[] numbers = new int[]{i,j,k};
		int pointer = 0;
		while(cont){
			String message = s.nextLine();
			if(message.equalsIgnoreCase("exit") ||message.equalsIgnoreCase("quit") ||message.equalsIgnoreCase("q") ||message.equalsIgnoreCase("x")){
				cont=false;
			}else if(message.contains("change number")){
				pointer++;
				pointer %=numbers.length;
				System.out.println("Number changed to "+contactDao.getAllHospitalContacts().get(numbers[pointer]).getPhoneNumber());
			}else{
				try{
					Message m = Message.createIncomingMessage(new Date().getTime(), contactDao.getAllHospitalContacts().get(numbers[pointer]).getPhoneNumber(), "1234567891", message);
					this.incomingMessageEvent(m);
				}catch(Throwable t){
					System.out.println("Error handling the message");
					t.printStackTrace();
				}
			}
		}
	}
	
	private int getRandomContactNumber(){
		return rand.nextInt(contactDao.getAllHospitalContacts().size());
	}

	private void initListeners(){
		listeners = new ArrayList<MessageHandler>();
		listeners.add(new PlainTextHandler(frontlineController,appCon));
		listeners.add(new BooleanHandler(frontlineController,appCon));
		listeners.add(new InfoHandler(frontlineController,appCon));
		listeners.add(new CodedHandler(frontlineController,appCon));
	}
		
	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}
	
	/** @return {@link #appCon} */
	public ApplicationContext getApplicationContext() {
		return this.appCon;
	}
	
	private void createDummyData(){
		try{
		if(contactDao.getAllHospitalContacts().size() == 0){
			System.out.println("Creating dummy data");
			for(int i = 0; i < 43; i++){
				String name = getRandomName();
				try {
					contactDao.saveHospitalContact(new HospitalContact(rand.nextBoolean()?name:"",getRandomPhoneNumber(),rand.nextBoolean()?getEmail(name):"",true,getRandomHospitalId()));
				} catch (DuplicateKeyException e) {
					System.out.println("Duplicate phone numbers generated, unable to save contact");
				}
				if(i % 5 == 0){
					System.out.println(i + " contacts created");
				}
			}
			//Plain text mappings
			PlainTextMappingDao mappingDao = (PlainTextMappingDao) appCon.getBean("plainTextMappingDao");
			PlainTextField mapping = new PlainTextField("organization.name","have:Hospital/have:Organization/have:OrganizationInformation/have:OrganisationName");
			//bed totals
			PlainTextField mapping2 = new PlainTextField("hospital.surgical.bed.total","have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:Capacity");
			mapping2.addInstruction("have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:BedType=MedicalSurgical");
			PlainTextField mapping3 = new PlainTextField("hospital.non.surgical.bed.total","have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:Capacity");
			mapping3.addInstruction("have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:BedType=NonSurgical");
			//bed availables
			PlainTextField mapping4 = new PlainTextField("hospital.surgical.bed.available","have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:Capacity/have:AvailableCount");
			mapping4.addInstruction("have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:BedType=MedicalSurgical");
			PlainTextField mapping5 = new PlainTextField("hospital.non.surgical.bed.available","have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:Capacity/have:AvailableCount");
			mapping5.addInstruction("have:Hospital/have:HospitalBedCapacityStatus/have:BedCapacity/have:BedType=NonSurgical");
			//geolocation
			PlainTextField mapping6 = new PlainTextField("organization.geolocation","have:Hospital/have:Organization/have:OrganizationGeoLocation");
			
			BooleanField bmapping = new BooleanField("organization.is.damaged","have:Hospital/have:Organization/have:OrganizationInformation/have:BuildingDamage");
			bmapping.addInstruction("have:Hospital/have:Organization/have:OrganizationInformation/have:BuildingDamage/have:DamageType=water");
			
			
			CodedField cmapping = new CodedField("organization.type","have:Hospital/have:Organization/have:OrganizationTypeText");
			cmapping.setPossibleResponses(new String[]{"Public Hospital","Government Hospital","University Hospital", "Private Hospital","Health Center", "Clinic", "Dispensary", "Temporary Healthcare Facility"});
			cmapping.addInstruction("have:Hospital/have:Organization/have:OrganizationInformation/have:BuildingDamage/have:DamageType=water");
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping);
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping2);
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping3);
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping4);
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping5);
			mappingDao.savePlainTextMappingWithoutDuplicateHandling(mapping6);
			
			BooleanMappingDao bMappingDao = (BooleanMappingDao) appCon.getBean("booleanMappingDao" ); 
			bMappingDao.saveBooleanMappingWithoutDuplicateHandling(bmapping);
			
			CodedMappingDao cMappingDao = (CodedMappingDao) appCon.getBean("codedMappingDao" ); 
			cMappingDao.saveCodedMappingWithoutDuplicateHandling(cmapping);
		}
		}catch(Throwable t){
			System.out.println("Error creating dummy data");
			t.printStackTrace();
		}
	}
	
	public String getRandomName(){
		return firsts[rand.nextInt(firsts.length)] + " "+ lasts[rand.nextInt(firsts.length)];
	}
	
	private String getRandomPhoneNumber() {
		String result = "";
		for (int i = 0; i < 10; i++) {
			result += rand.nextInt(10);
		}
		return result;
	}
	
	public String getRandomHospitalId(){
		String result = "";
		for (int i = 0; i < 5; i++) {
			result += rand.nextInt(10);
		}
		return result + ".org";
	}
	
	public String getEmail(String name){
		String email = name.substring(0, 1).toLowerCase();
		email += name.substring(name.indexOf(" ")+1);
		email += "@google.com";
		return email;
	}
	
	private static final Random rand = new Random();
	private static final String[] firsts = { "Dieterich", "Dolores", "Freddy", "Alex",
			"Charlie", "Lindsay", "Winnie", "Terrence", "Wilson", "Jenny",
			"Meghan", "Katherine", "Poe", "Phillip", "Andrew", "Elizabeth",
			"Whitney", "Frank", "Jared", "Pope", "Wylie","Theodore", "Margot",
			"Forscythe","Lars","Sarah","Teddy","Fitz","Humphrey","James","Mark","Jesse" };
	// list of last names
	private static final  String[] lasts = { "Lawson", "Threadbare", "Evermore", "Brown",
			"Tender", "Taraban", "Polombo", "Trought",
			"Finkley", "Coriander", "Groesbeck", "Trounce", "Longbottom",
			"Yip", "Fiars", "Trunch", "Whelp", "Schy", "Munificent",
			"Coyote","Brown","Black","Ames","Chavez","Richards","Swanson","Ballard"
			,"Roosevelt","Jackson","Trueblood","Wachowsky","Corleogne" };

	public void incomingMessageEvent(Message message) {
		if(callbacks == null){
			callbacks = new ArrayList<CallbackInfo>();
		}
		//first, remove all callbacks that have timed out
		ArrayList<CallbackInfo> toRemove = new ArrayList<CallbackInfo>();
		for(CallbackInfo info:callbacks){
			if(info.hasTimedOut()){
				info.getHandler().callBackTimedOut(info.getPhoneNumber());
				toRemove.add(info);
			}
		}
		callbacks.removeAll(toRemove);
		
		//see if there is a handler that wants to handle the message
		MessageHandler handler = null;
		String keyword = message.getTextContent().split(" ")[0];
		for(MessageHandler m: listeners){
			Collection<String> keywords = m.getKeywords();
			if(m.getKeywords().contains(keyword)){
				handler = m;
			}
		}
		
		//now, see if there is a callback out on that message
		CallbackHandler callbackHandler = null;
		for(CallbackInfo info: callbacks){
			if(info.getPhoneNumber().equalsIgnoreCase(message.getSenderMsisdn())){
				callbackHandler=info.getHandler();
			}
		}
		
		//if there is a keyword in the message and the callback handler doesn't seem
		//to know what to do with it, give the message to the keyword handler as opposed
		//to the callback handler and remove the callback.
		if(handler !=null && callbackHandler != null && callbackHandler.shouldHandleCallbackMessage(message) == false){
			handler.handleMessage(message);
			unregisterCallback(message.getSenderMsisdn());
		//otherwise, if there is a callback out on the message, pass the message to the callback handler
		}else if(callbackHandler != null){
			callbackHandler.handleCallback(message);
		//if there is no callback out on the message, give it to a keyword handler
		}else if(handler != null){
			handler.handleMessage(message);
		}

	}
	
	public static void registerCallback(String msisdn, CallbackHandler handler){
		if(callbacks == null){
			callbacks = new ArrayList<CallbackInfo>();
		}
		//if there is already a callback out on that phone number, do nothing
		for(CallbackInfo info:callbacks){
			if(info.getPhoneNumber().equalsIgnoreCase(msisdn)){
				return;
			}
		}
		callbacks.add(new CallbackInfo(msisdn,handler));
		
	}
	
	public static void unregisterCallback(String msisdn){
		ArrayList<CallbackInfo> toRemove = new ArrayList<CallbackInfo>();
		for(CallbackInfo info:callbacks){
			if(info.getPhoneNumber().equalsIgnoreCase(msisdn)){
				info.getHandler().callBackTimedOut(msisdn);
				toRemove.add(info);
			}
		}
		callbacks.removeAll(toRemove);
	}
	
	public static void unregisterCallback(CallbackInfo info){
		unregisterCallback(info.getPhoneNumber());
	}

}
