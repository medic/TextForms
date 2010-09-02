package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.ui.ResourceMapperThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * ResourceMapperPluginController
 * @author dalezak
 *
 */
@PluginControllerProperties(name="Operator (Beta)", i18nKey = "plugins.resourcemapper", iconPath="/icons/operator.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper.hibernate.cfg.xml")
public class ResourceMapperPluginController extends BasePluginController {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperPluginController.class);
	
	/**
	 * FrontlineSMS
	 */
	private FrontlineSMS frontlineController;
	
	/**
	 * ApplicationContext
	 */
	private ApplicationContext appContext;
	
	/**
	 * ResourceMapperListener
	 */
	private ResourceMapperListener listener;
	
	/**
	 * ResourceMapperThinletTabController
	 */
	private ResourceMapperThinletTabController tabController;
	
	/**
	 * FieldMappingDao
	 */
	private FieldMappingDao fieldMappingDao;
	
	/**
	 * FieldResponseDao
	 */
	private FieldResponseDao fieldResponseDao;
	
	/**
	 * The main tab panel
	 */
	private Object mainTab;
	
	public String getName() {
		return getI18NString("resourcemapper.tab.title");
	}

	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		this.tabController = new ResourceMapperThinletTabController(this.frontlineController, uiController, this.appContext);
		return this.tabController.getTab();
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	/**
	 * De-initialize ResourceMapperPluginController
	 */
	public void deinit() {
		LOG.debug("deinit");
		if (this.listener != null) {
			this.listener.setListening(false);
		}
	}
	
	/**
	 * Initialize ResourceMapperPluginController
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext appContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.appContext = appContext;
		this.listener = new ResourceMapperListener(frontlineController, appContext);
		
		this.fieldMappingDao = (FieldMappingDao)appContext.getBean("fieldMappingDao");
		this.fieldResponseDao = (FieldResponseDao)appContext.getBean("fieldResponseDao");
		
	    if (ResourceMapperProperties.isDebugMode()) {
            LOG.debug("Running ResourceMapperDebug...");
            ResourceMapperDebug resourceMapperDebug = new ResourceMapperDebug(this.appContext);
            resourceMapperDebug.startDebugTerminal();
//            resourceMapperDebug.createDebugFields();
//          resourceMapperDebug.createDebugContacts();
//          resourceMapperDebug.createDebugResponses();
//          resourceMapperDebug.createResponseOutputs();
//          resourceMapperDebug.createUploadXMLDocument();
//          resourceMapperDebug.createUploadJSONDocument();
//          resourceMapperDebug.createUploadCSVDocument();
//          resourceMapperDebug.createUploadGoogleDocument();
        }

	}
	
	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}
	
	/** @return {@link #appContext} */ 
	public ApplicationContext getApplicationContext() {
		return this.appContext;
	}

	public FieldMappingDao getFieldMappingDao() {
		return this.fieldMappingDao;
	}
	
	public FieldResponseDao getFieldResponseDao() {
		return this.fieldResponseDao;
	}
}
