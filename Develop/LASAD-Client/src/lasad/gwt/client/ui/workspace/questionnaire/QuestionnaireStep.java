package lasad.gwt.client.ui.workspace.questionnaire;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public class QuestionnaireStep extends ContentPanel {

	private QuestionnaireStepConfig stepConfig;

	public QuestionnaireStep(QuestionnaireStepConfig stepConfig) {
		this.stepConfig = stepConfig;
	}

	public QuestionnaireStepConfig getStepConfig() {
		return stepConfig;
	}

	public void setStepConfig(QuestionnaireStepConfig stepConfig) {
		this.stepConfig = stepConfig;
	}
}