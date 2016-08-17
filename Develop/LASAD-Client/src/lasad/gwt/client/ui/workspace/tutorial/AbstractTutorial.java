package lasad.gwt.client.ui.workspace.tutorial;

import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMap;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.questionnaire.QuestionnaireHandler;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;

public abstract class AbstractTutorial extends ContentPanel {

	private AbstractGraphMap myMap = null;
	private TutorialConfig tutorialConfig = null;

	public AbstractTutorial(AbstractGraphMap map) {
		this.myMap = map;

		this.setLayout(new AccordionLayout());
		this.setHeading("Tutorial");
		this.setBodyBorder(true);
		this.setBorders(false);

		this.setHeight("100%");

		// Get TutorialConfig
		MVCViewSession viewSession = (MVCViewSession) myMap.getMyViewSession();
		this.setTutorialConfig(viewSession.getController().getMapInfo().getTutorialConfig());
//		this.setTutorialConfig(myMap.getMyViewSession().getController().getMapInfo().getTutorialConfig());
	}

	public void setTutorialConfig(TutorialConfig config) {
		if (config == null) return;

		this.tutorialConfig = config;

		// Remove all further elements
		this.removeAll();

		// Load new Elements
		loadConfig();
	}

	private void loadConfig() {
		for (TutorialStepConfig stepConfig : tutorialConfig.getTutorialSteps()) {
			// Add Step to Panel
//			this.add(new TutorialStep(this, stepConfig));
			this.add(getTutorialStep(this, stepConfig));
		}
	}
	
	protected abstract AbstractTutorialStep getTutorialStep(AbstractTutorial tutorial, TutorialStepConfig stepConfig);

	public AbstractGraphMap getMyMap() {
		return myMap;
	}

	public void setMyMap(ArgumentMap myMap) {
		this.myMap = myMap;
	}

	private QuestionnaireHandler questionnaireHandler = new QuestionnaireHandler();

	public QuestionnaireHandler getQuestionnaireHandler() {
		return questionnaireHandler;
	}
}