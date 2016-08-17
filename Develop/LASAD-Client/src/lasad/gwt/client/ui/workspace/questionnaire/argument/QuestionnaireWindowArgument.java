package lasad.gwt.client.ui.workspace.questionnaire.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.questionnaire.AbstractQuestionnaireWindow;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorialStep;
import lasad.shared.communication.objects.ActionPackage;

public class QuestionnaireWindowArgument extends AbstractQuestionnaireWindow {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();

	public QuestionnaireWindowArgument(AbstractGraphMap map,
			AbstractTutorialStep tutorialStep) {
		super(map, tutorialStep);
	}

	@Override
	protected void sendActionPackageToServer(ActionPackage p) {
		communicator.sendActionPackage(p);
	}

}
