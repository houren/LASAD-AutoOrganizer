package lasad.gwt.client.ui.workspace.questionnaire;

import java.util.Vector;

public class QuestionnaireStepConfig {

	private Vector<QuestionConfig> questionnaireQuestions = new Vector<QuestionConfig>();

	public Vector<QuestionConfig> getQuestionnaireQuestions() {
		return questionnaireQuestions;
	}

	public void setQuestionnaireQuestions(Vector<QuestionConfig> questionnaireQuestions) {
		this.questionnaireQuestions = questionnaireQuestions;
	}
}
