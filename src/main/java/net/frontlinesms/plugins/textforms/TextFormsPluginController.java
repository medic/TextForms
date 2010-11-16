package net.frontlinesms.plugins.textforms;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.AnswerDao;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.plugins.textforms.data.repository.TextFormResponseDao;
import net.frontlinesms.plugins.textforms.ui.TextFormsThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * TextFormsPluginController
 * @author dalezak
 *
 */
@PluginControllerProperties(name="TextForms (Beta)", i18nKey = "plugins.textforms", iconPath="/icons/textforms.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/textforms/textforms-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/textforms/textforms.hibernate.cfg.xml")
public class TextFormsPluginController extends BasePluginController {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormsPluginController.class);
	
	/**
	 * FrontlineSMS
	 */
	private FrontlineSMS frontlineController;
	
	/**
	 * ApplicationContext
	 */
	private ApplicationContext appContext;
	
	/**
	 * TextFormsListener
	 */
	@SuppressWarnings("unused")
	private TextFormsListener listener;
	
	/**
	 * TextFormsThinletTabController
	 */
	private TextFormsThinletTabController tabController;
	
	/**
	 * QuestionDao
	 */
	private QuestionDao questionDao;
	
	/**
	 * AnswerDao
	 */
	private AnswerDao answerDao;
	
	/**
	 * TextFormDao
	 */
	private TextFormDao textformDao;
	
	/**
	 * TextFormResponseDao
	 */
	private TextFormResponseDao textformResponseDao;
	
	/**
	 * The main tab panel
	 */
	private Object mainTab;
	
	public String getName() {
		return getI18NString("textforms.tab.title");
	}

	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		this.tabController = new TextFormsThinletTabController(frontlineController, uiController, appContext, this);
		return this.tabController.getTab();
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	/**
	 * De-initialize TextFormsPluginController
	 */
	public void deinit() {
		LOG.debug("deinit");
	}
	
	/**
	 * Initialize TextFormsPluginController
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext appContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.appContext = appContext;
		
		this.questionDao = (QuestionDao)appContext.getBean("questionDao", QuestionDao.class);
		this.answerDao = (AnswerDao)appContext.getBean("answerDao", AnswerDao.class);
		this.textformDao = (TextFormDao)appContext.getBean("textformDao", TextFormDao.class);
		this.textformResponseDao = (TextFormResponseDao)appContext.getBean("textformResponseDao", TextFormResponseDao.class);
		
		this.listener = new TextFormsListener(frontlineController, appContext, this);
		
		if (TextFormsProperties.isDebugMode()) {
            LOG.debug("Running TextFormsDebug...");
            TextFormsDebug resourceMapperDebug = new TextFormsDebug(this.appContext);
            resourceMapperDebug.startDebugTerminal();
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

	public QuestionDao getQuestionDao() {
		return this.questionDao;
	}
	
	public AnswerDao getAnswerDao() {
		return this.answerDao;
	}
	
	public TextFormDao getTextFormDao() {
		return this.textformDao;
	}
	
	public TextFormResponseDao getTextFormResponseDao() {
		return this.textformResponseDao;
	}
}
