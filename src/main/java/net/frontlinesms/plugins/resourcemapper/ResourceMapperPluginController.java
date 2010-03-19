package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Random;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.ui.ResourceMapperThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name="Resource Mapper", iconPath="/icons/small_rmapper.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper.hibernate.cfg.xml")
public class ResourceMapperPluginController extends BasePluginController{

	private FrontlineSMS frontlineController;
	
	/** The Application Context for fetching daos and other Spring stuff */
	private ApplicationContext appCon;
	
	private Object mainTab;
	
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
		this.appCon = applicationContext;
		createDummyData();
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
		HospitalContactDao contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
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

}
