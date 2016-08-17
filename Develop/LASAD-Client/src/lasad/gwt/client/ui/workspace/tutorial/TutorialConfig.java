package lasad.gwt.client.ui.workspace.tutorial;

import java.util.Vector;

public class TutorialConfig {

	Vector<TutorialStepConfig> tutorialSteps = new Vector<TutorialStepConfig>();

	public Vector<TutorialStepConfig> getTutorialSteps() {
		return tutorialSteps;
	}

	public void setTutorialSteps(Vector<TutorialStepConfig> tutorialSteps) {
		this.tutorialSteps = tutorialSteps;
	}
}