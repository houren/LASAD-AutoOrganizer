package lasad.gwt.client.ui.workspace.questionnaire;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.tutorial.AbstractTutorialStep;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public abstract class AbstractQuestionnaireWindow extends Window {

	private AbstractGraphMap map;
	private AbstractTutorialStep tutorialStep;

	private FormData formData;

	private Map<String, String> oldAnswers = new HashMap<String, String>();

	private Map<QuestionConfig, BoxComponent> answerAreas = new LinkedHashMap<QuestionConfig, BoxComponent>();

//	private final LASADActionSender communicator = LASADActionSender.getInstance();

	public AbstractQuestionnaireWindow(AbstractGraphMap map, AbstractTutorialStep tutorialStep) {
		this.map = map;
		this.tutorialStep = tutorialStep;
		setWidth(500);
		setPlain(true);
		setModal(true);
		setBlinkModal(true);
		setHeading("Questionnaire: " + tutorialStep.getConfig().getTitle());

		// Register Window
		LASAD_Client.getMapTab(map.getID()).getMyMapSpace().getTutorial().getQuestionnaireHandler().registerQuestionWindow(this, tutorialStep.getConfig().getQuestionnaireStepConfig());

		// if(tutorialStep.getConfig().getQuestionnaireStepConfig().getQuestionnaireQuestions().size()
		// > 4){
		setHeight(500);
		setLayout(new FitLayout());
		// }
		// else{
		// setLayout(new FitLayout());
		// setAutoHeight(true);
		// }

		// load Settings
		loadSettings();

		// buildQuestions();

	}

	private Status statusIcon = new Status();

	private void loadSettings() {
		ContentPanel loadPanel = new ContentPanel(new CenterLayout());
		loadPanel.setHeaderVisible(false);
		loadPanel.setBodyBorder(false);
		loadPanel.setBorders(false);
		loadPanel.setHeight(400);

		statusIcon.setAutoHeight(true);
		statusIcon.setWidth(200);
		statusIcon.setBusy("loading questionnaire settings...");
		loadPanel.add(statusIcon);

		add(loadPanel);

		loadPanel.layout();

		// Send Request to server
		ActionPackage p = ActionFactory.getInstance().getQuestionnaireAnswers(map.getID(), tutorialStep.getConfig().getQuestionnaireStepConfig().getQuestionnaireQuestions());
//		communicator.sendActionPackage(p);
		sendActionPackageToServer(p);

	}
	
	protected abstract void sendActionPackageToServer(ActionPackage p);

	private void buildQuestions() {
		this.removeAll();

		formData = new FormData("-20");
		FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOY);

		int questionNumber = 0;
		for (QuestionConfig questionConfig : tutorialStep.getConfig().getQuestionnaireStepConfig().getQuestionnaireQuestions()) {
			questionNumber++;
			// Question
			TextArea question = new TextArea();
			// question.setPreventScrollbars(true);
			question.setFieldLabel("Question " + questionNumber);
			question.setReadOnly(true);
			// question.setAutoHeight(true);
			question.setValue(questionConfig.getQuestion());
			formPanel.add(question, formData);
			// Answer
			BoxComponent answer = null;
			if (questionConfig.getType().equals("text")) {
				answer = new TextArea();
				// answer.setPreventScrollbars(true);
				((TextArea) answer).setFieldLabel("Your answer");
				((TextArea) answer).setValue(oldAnswers.get(questionConfig.getId()));
				formPanel.add(answer, formData);
			} else if (questionConfig.getType().equals("score")) {
				answer = new Slider();

				((Slider) answer).setIncrement(1);
				((Slider) answer).setMinValue(Integer.valueOf(questionConfig.getMinScore()));
				((Slider) answer).setMaxValue(Integer.valueOf(questionConfig.getMaxScore()));
				if (oldAnswers.get(questionConfig.getId()) == null) {
					((Slider) answer).setValue(Integer.valueOf(questionConfig.getMinScore()), true);
				} else {
					((Slider) answer).setValue(Integer.valueOf(oldAnswers.get(questionConfig.getId())), true);
				}
				((Slider) answer).setMessage("{0}");
				SliderField sf = new SliderField((Slider) answer);
				sf.setFieldLabel("Your answer");
				formPanel.add(sf, formData);
			} else if (questionConfig.getType().equals("radioscore")) {

				answer = new RadioGroup();
				((RadioGroup) answer).setFieldLabel("Your answer");

				for (int i = questionConfig.getMinScore(); i <= questionConfig.getMaxScore(); i++) {
					Radio tempRadio = new Radio();
					if (oldAnswers.get(questionConfig.getId()) != null && oldAnswers.get(questionConfig.getId()).equals(i + "")) {
						tempRadio.setValue(true);
					}
					if (i == questionConfig.getMinScore()) {
						tempRadio.setBoxLabel(i + " " + questionConfig.getMinScoreLabel());
					} else if (i == questionConfig.getMaxScore()) {
						tempRadio.setBoxLabel(i + " " + questionConfig.getMaxScoreLabel());
					} else {
						tempRadio.setBoxLabel(i + "");
					}

					tempRadio.setData("questionvalue", i);
					tempRadio.setStyleAttribute("color", "#000000");

					((RadioGroup) answer).add(tempRadio);
				}
				formPanel.add(((RadioGroup) answer), formData);
			}

			answerAreas.put(questionConfig, answer);

		}

		// Build Send Button

		Button submit = new Button("Submit") {
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				// Send Data to Server
				// ActionSet actionSet = new
				// ActionSet(QuestionnaireWindow.this.map.getID());

				ActionPackage p = new ActionPackage();

				for (QuestionConfig config : answerAreas.keySet()) {
					String answer = null;
					if (answerAreas.get(config) instanceof TextArea) {
						answer = ((TextArea) answerAreas.get(config)).getValue();
					} else if (answerAreas.get(config) instanceof Slider) {
						answer = "" + ((Slider) answerAreas.get(config)).getValue();
					} else if (answerAreas.get(config) instanceof RadioGroup) {
						if (((RadioGroup) answerAreas.get(config)).getValue() != null) answer = "" + ((RadioGroup) answerAreas.get(config)).getValue().getData("questionvalue");
					}

					p.addAction(ActionFactory.getInstance().addQuestionnaireAnswer(map.getID(), config.getId(), answer));
				}

//				communicator.sendActionPackage(p);
				sendActionPackageToServer(p);
				// Hide this window
				AbstractQuestionnaireWindow.this.hide();
			}
		};

		formPanel.addButton(submit);

		Button cancel = new Button("Cancel") {
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				// Remove entries form the QuestionnaireHandler
				LASAD_Client.getMapTab(map.getID()).getMyMapSpace().getTutorial().getQuestionnaireHandler().removeQuestionWindow(AbstractQuestionnaireWindow.this);

				AbstractQuestionnaireWindow.this.hide();
			}
		};

		formPanel.addButton(cancel);

		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(formPanel);
		binding.addButton(submit);

		add(formPanel);
		layout();
	}

	public void addQuestionAnswer(String id, String answer) {
		oldAnswers.put(id, answer);

		if (oldAnswers.size() == tutorialStep.getConfig().getQuestionnaireStepConfig().getQuestionnaireQuestions().size()) {
			// Draw Questionnaire
			buildQuestions();
		} else {
			statusIcon.setBusy("loaded " + oldAnswers.size() + " / " + tutorialStep.getConfig().getQuestionnaireStepConfig().getQuestionnaireQuestions().size() + " questions...");

		}

	}

}
