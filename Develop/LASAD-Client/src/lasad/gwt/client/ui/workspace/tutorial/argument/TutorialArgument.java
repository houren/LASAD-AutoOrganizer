package lasad.gwt.client.ui.workspace.tutorial.argument;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorial;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorialStep;
import lasad.gwt.client.ui.workspace.tutorial.TutorialStepConfig;

public class TutorialArgument extends AbstractTutorial {

	public TutorialArgument(AbstractGraphMap map) {
		super(map);
	}

	@Override
	protected AbstractTutorialStep getTutorialStep(AbstractTutorial tutorial,
			TutorialStepConfig stepConfig) {
		return new TutorialStepArgument(tutorial, stepConfig);
	}

}
