package net.frontlinesms.plugins.surveys;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;
import net.frontlinesms.plugins.surveys.data.repository.SurveyDao;
import net.frontlinesms.plugins.surveys.ui.SurveysThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * SurveysPluginController
 * @author dalezak
 *
 */
@PluginControllerProperties(name="Surveys (Beta)", i18nKey = "plugins.surveys", iconPath="/icons/surveys.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/surveys/surveys-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/surveys/surveys.hibernate.cfg.xml")
public class SurveysPluginController extends BasePluginController {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveysPluginController.class);
	
	/**
	 * FrontlineSMS
	 */
	private FrontlineSMS frontlineController;
	
	/**
	 * ApplicationContext
	 */
	private ApplicationContext appContext;
	
	/**
	 * SurveysListener
	 */
	private SurveysListener listener;
	
	/**
	 * SurveysThinletTabController
	 */
	private SurveysThinletTabController tabController;
	
	/**
	 * QuestionDao
	 */
	private QuestionDao questionDao;
	
	/**
	 * AnswerDao
	 */
	private AnswerDao answerDao;
	
	/**
	 * SurveyDao
	 */
	private SurveyDao surveyDao;
	
	/**
	 * The main tab panel
	 */
	private Object mainTab;
	
	public String getName() {
		return getI18NString("surveys.tab.title");
	}

	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		this.tabController = new SurveysThinletTabController(this.frontlineController, uiController, this.appContext);
		return this.tabController.getTab();
	}

	public Object getTab(){
		return this.mainTab;
	}
	
	/**
	 * De-initialize SurveysPluginController
	 */
	public void deinit() {
		LOG.debug("deinit");
		if (this.listener != null) {
			this.listener.setListening(false);
		}
	}
	
	/**
	 * Initialize SurveysPluginController
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext appContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.appContext = appContext;
		this.listener = new SurveysListener(frontlineController, appContext);
		
		this.questionDao = (QuestionDao)appContext.getBean("questionDao", QuestionDao.class);
		this.answerDao = (AnswerDao)appContext.getBean("answerDao", AnswerDao.class);
		this.surveyDao = (SurveyDao)appContext.getBean("surveyDao", SurveyDao.class);
		
	    if (SurveysProperties.isDebugMode()) {
            LOG.debug("Running SurveysDebug...");
            SurveysDebug resourceMapperDebug = new SurveysDebug(this.appContext);
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
	
	public SurveyDao getSurveyDao() {
		return this.surveyDao;
	}
}
