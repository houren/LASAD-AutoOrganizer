package lasad.gwt.client.ui.workspace.tutorial.argument;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.questionnaire.AbstractQuestionnaireWindow;
import lasad.gwt.client.ui.workspace.questionnaire.argument.QuestionnaireWindowArgument;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorial;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorialStep;
import lasad.gwt.client.ui.workspace.tutorial.TutorialStepConfig;

public class TutorialStepArgument extends AbstractTutorialStep {

	public TutorialStepArgument(AbstractTutorial parent, TutorialStepConfig config) {
		super(parent, config);
	}

	@Override
	protected AbstractQuestionnaireWindow getQuestionnaireWindow(
			AbstractGraphMap map, AbstractTutorialStep tutorialStep) {
		return new QuestionnaireWindowArgument(map, tutorialStep);
	}

}
