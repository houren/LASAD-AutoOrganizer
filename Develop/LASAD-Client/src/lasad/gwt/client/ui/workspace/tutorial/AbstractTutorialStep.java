package lasad.gwt.client.ui.workspace.tutorial;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.questionnaire.AbstractQuestionnaireWindow;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public abstract class AbstractTutorialStep extends ContentPanel {

	private AbstractTutorial tutorial = null;
	private TutorialStepConfig config = null;

	private Button questionnaireButton;

	private Html htmlDiv = new Html();

	public AbstractTutorialStep(AbstractTutorial parent, TutorialStepConfig config) {
		this.tutorial = parent;
		this.config = config;

		// Set Heading
		this.setScrollMode(Scroll.AUTOY);
		this.setLayout(new FlowLayout());
		this.setHeading(config.getTitle());
		this.setBorders(false);
		this.setBodyBorder(false);
		this.getHeader().setBorders(false);

		this.add(htmlDiv);

		// Questionnaire Button
		if (config.getQuestionnaireStepConfig() != null && config.getQuestionnaireStepConfig().getQuestionnaireQuestions().size() > 0) {
			questionnaireButton = new Button("Open Questionnaire") {
				protected void onClick(ComponentEvent ce) {
					// Shows up the step questions
//					QuestionnaireWindow window = new QuestionnaireWindow(getTutorial().getMyMap(), AbstractTutorialStep.this);
					AbstractQuestionnaireWindow window = getQuestionnaireWindow(getTutorial().getMyMap(), AbstractTutorialStep.this);
					window.show();
				}
			};
			questionnaireButton.setWidth("100%");
			// questionnaireButton.setIconAlign(IconAlign.RIGHT);
			this.add(questionnaireButton);
			Html tempDiv = new Html();
			tempDiv.setStyleName("tutorialStepTemp-div");
			add(tempDiv);
		}
	}
	
	protected abstract AbstractQuestionnaireWindow getQuestionnaireWindow(AbstractGraphMap map, AbstractTutorialStep tutorialStep);

	protected void afterRender() {
		super.afterRender();
		setHTMLText(config.getText());

		layout();
	}

	private void setHTMLText(String text) {
		htmlDiv.setStyleName("tutorialStep-div");
		htmlDiv.setHtml(text);
	}

	public AbstractTutorial getTutorial() {
		return tutorial;
	}

	public TutorialStepConfig getConfig() {
		return config;
	}
}