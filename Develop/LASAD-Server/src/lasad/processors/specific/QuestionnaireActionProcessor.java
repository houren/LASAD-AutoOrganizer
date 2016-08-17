package lasad.processors.specific;

import lasad.entity.User;
import lasad.processors.ActionObserver;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.categories.Categories;

public class QuestionnaireActionProcessor extends AbstractActionObserver
		implements ActionObserver {

	public QuestionnaireActionProcessor() {
		super();
	}

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null&&a.getCategory().equals(Categories.Questionnaire)){
			//TODO not implemented yet
			returnValue = true;
		}
		return returnValue;
	}

}
