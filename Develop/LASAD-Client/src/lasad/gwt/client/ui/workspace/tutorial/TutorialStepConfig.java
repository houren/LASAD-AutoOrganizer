package lasad.gwt.client.ui.workspace.tutorial;

import lasad.gwt.client.ui.workspace.questionnaire.QuestionnaireStepConfig;

public class TutorialStepConfig {

	private String id;
	private String title, text;

	private QuestionnaireStepConfig questionnaireStepConfig;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public QuestionnaireStepConfig getQuestionnaireStepConfig() {
		return questionnaireStepConfig;
	}

	public void setQuestionnaireStepConfig(QuestionnaireStepConfig questionnaireStepConfig) {
		this.questionnaireStepConfig = questionnaireStepConfig;
	}

}
