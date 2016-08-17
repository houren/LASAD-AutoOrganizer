package lasad.gwt.client.ui.workspace.questionnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class QuestionnaireHandler {

	private Map<String, AbstractQuestionnaireWindow> question2Window = new HashMap<String, AbstractQuestionnaireWindow>();
	private Map<AbstractQuestionnaireWindow, Vector<String>> window2Question = new HashMap<AbstractQuestionnaireWindow, Vector<String>>();

	public void registerQuestionWindow(AbstractQuestionnaireWindow window, QuestionnaireStepConfig stepConfig) {
		window2Question.put(window, new Vector<String>());
		for (QuestionConfig question : stepConfig.getQuestionnaireQuestions()) {
			question2Window.put(question.getId(), window);
			window2Question.get(window).add(question.getId());
		}

	}

	public void removeQuestionWindow(AbstractQuestionnaireWindow window) {
		for (String id : window2Question.get(window)) {
			question2Window.remove(id);
		}
		window2Question.remove(window);
	}

	public void publishQuestionnaireAnswer(String id, String answer) {
		question2Window.get(id).addQuestionAnswer(id, answer);
	}

}
