package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.OntologyGenerator;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.RadioButtonParameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;




public class StepCreateAndDeleteElement extends ContentPanel {

	private LASADActionSender communicator = LASADActionSender.getInstance();
	private ActionFactory builder = ActionFactory.getInstance();

	private Button previewButton, saveButton;
	private String elementType, selection, internalElementType,s1="1",s2="0",s3="0";

	private TextField<String> elementID, textFieldLabel, scoreFieldLabel,URLFieldLabel,radioButtonFieldLabel,radioButtonOption1,radioButtonOption2,radioButtonOption3, dropDownFieldLabel;
	private TextArea dropDownElements;
	private Slider uframeminheigth,uframeminwidth, minScore,windowheigth,windowwidth, maxScore, startScore, textFieldMinHeight, textFieldMaxHeight;
	private SliderField suframeminheigth,suframeminwidth,sfStartScore,sfwindowheigth,sfwindowwidth,sfMinScore, sfMaxScore, tfMinHeight, tfMaxHeight;
	private SimpleComboBox<String> elements = new SimpleComboBox<String>();
	private SimpleComboBox<String> type = new SimpleComboBox<String>();
	private FieldSet useLabelForTextField, useMultiLine, elementOptions;
	private ElementInfo parent;
	private FormPanel delElementForm;
	
	private String fontStyleStart = "<span style=\"color: #000000;\">";
	private String fontStyleEnd = "</span>";
	private boolean isradiobuttons=false;
	
private Radio radioEditable, radiononEditable,r1,r2,r3;

//private String viewMode="editable";

	private void switchToElementDetails(String value) {
		selection = value;

		// Remove previous options
		elementOptions.setVisible(false);
		elementOptions.removeAll();

		elementID.setVisible(true);
		if (value.equals("Text Field")) {
			internalElementType = "text";
			showTextFieldOptions();
		} else if (value.equals("Internal Link")) {
			internalElementType = "transcript-link";
			showTranscriptLinkOptions();
		} else if (value.equals("Awareness")) {
			internalElementType = "awareness";
			showAwarenessOptions();
		} else if (value.equals("Score")) {
			internalElementType = "rating";
			showScoreOptions();
		} else if (value.equals("Dropdown")) {
			internalElementType = "dropdown";
			showDropdownOptions();
		} else if (value.equals("URL")) {
			internalElementType = "url";
			showURLOptions();
		}
		
		else if (value.equals("URL Display")) {
			internalElementType = "frame";
			
			showUframeOptions();
			//showURLOptions();
		}
		
		else if (value.equals("Radio Buttons")) {
			internalElementType = "radiobtn";
			isradiobuttons=true;
			showRadioButtonOptions();
			//showURLOptions();
		}

		elementOptions.layout();
		layout();
	}

	
	void showRadioButtonOptions(){
		
		radioButtonFieldLabel = new TextField<String>();
		radioButtonFieldLabel.setFieldLabel(fontStyleStart+"Label");
		radioButtonFieldLabel.setAllowBlank(true);
		radioButtonFieldLabel.setEmptyText("Enter a label for Radio Buttons");
		radioButtonOption1 = new TextField<String>();
		radioButtonOption1.setFieldLabel(fontStyleStart+"Option1"+fontStyleEnd);
		radioButtonOption1.setAllowBlank(false);
		radioButtonOption1.setEmptyText("Enter first option");
		
		radioButtonOption2 = new TextField<String>();
		radioButtonOption2.setFieldLabel(fontStyleStart+"Option2"+fontStyleEnd);
		radioButtonOption2.setAllowBlank(false);
		radioButtonOption2.setEmptyText("Enter second option");
		
		radioButtonOption3 = new TextField<String>();
		radioButtonOption3.setFieldLabel(fontStyleStart+"Option3"+fontStyleEnd);
		radioButtonOption3.setAllowBlank(true);
		radioButtonOption3.setEmptyText("Enter third option");
		
		
		elementOptions.add(radioButtonFieldLabel);
		elementOptions.add(radioButtonOption1);
		elementOptions.add(radioButtonOption2);
		elementOptions.add(radioButtonOption3);
		
		r1 = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				s1 = "1";
				s2="0";
				s3="0";
			}
		};
		
		r1.setBoxLabel(fontStyleStart+"First"+fontStyleEnd);
		r1.setValue(true);
		
		r2 = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				s1="0";
				s2 = "1";
				s3="0";
			}
		};
		
		r2.setBoxLabel(fontStyleStart+"Second"+fontStyleEnd);
		
		r3 = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				s1="0";
				s2="0";
				s3 = "1";
			}
		};
		
		r3.setBoxLabel(fontStyleStart+"Third"+fontStyleEnd);
		
		
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Default Selection");
		radioGroup.add(r1);
		radioGroup.add(r2);
		radioGroup.add(r3);
		
		elementOptions.add(radioGroup);
		
		elementOptions.setVisible(true);
		
		
	}
	
	private void showURLOptions() {
		// Currently, nothing to add
		elementID.setVisible(false);
	}

	private void showDropdownOptions() {

		dropDownFieldLabel = new TextField<String>();
		dropDownFieldLabel.setFieldLabel("Label");
		dropDownFieldLabel.setAllowBlank(false);
		dropDownFieldLabel.setEmptyText("Enter a label for the drop down list");

		elementOptions.add(dropDownFieldLabel);

		dropDownElements = new TextArea();
		dropDownElements.setFieldLabel("Options (one per line)");
		elementOptions.add(dropDownElements);

		elementOptions.setVisible(true);
	}

	
	private void showUframeOptions() {

		
		radioEditable = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				//viewMode = "editable";
			}
		};
		radioEditable.setBoxLabel(fontStyleStart+"Editable"+fontStyleEnd);
		radioEditable.setValue(true);

		radiononEditable = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				//viewMode = "noneditable";
			}
		};
		radiononEditable.setBoxLabel(fontStyleStart+"Non-Editable"+fontStyleEnd);

		
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Display Mode");
		radioGroup.add(radioEditable);
		radioGroup.add(radiononEditable);
		
		
		URLFieldLabel = new TextField<String>();
		URLFieldLabel.setFieldLabel("Default URL");
		URLFieldLabel.setAllowBlank(true);
		URLFieldLabel.setEmptyText("Enter a default URL");

		
		
		
		
		
		windowheigth = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfwindowheigth.setFieldLabel("Window Height [" + value + "]");
			}

		};
		windowheigth.setMinValue(150);
		windowheigth.setMaxValue(600);
		windowheigth.setValue(400);
		windowheigth.setIncrement(25);
		windowheigth.setMessage("Minimal score: {0}");

		sfwindowheigth= new SliderField(windowheigth);
		sfwindowheigth.setFieldLabel("Min. Score [0]");
		
		windowwidth = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfwindowwidth .setFieldLabel("Window Width [" + value + "]");
			}

		};
		windowwidth.setMinValue(150);
		windowwidth.setMaxValue(600);
		windowwidth.setValue(400);
		windowwidth.setIncrement(25);
		windowwidth.setMessage("Minimal width: {0}");

		sfwindowwidth= new SliderField(windowwidth);
		sfwindowwidth.setFieldLabel("Min. Width [0]");
		
		
	
		uframeminheigth = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				suframeminheigth .setFieldLabel("Min. Height [" + value + "]");
			}

		};
		uframeminheigth.setMinValue(50);
		uframeminheigth.setMaxValue(600);
		uframeminheigth.setValue(100);
		uframeminheigth.setIncrement(25);
		uframeminheigth.setMessage("Min. height: {0}");

	
		suframeminheigth= new SliderField(uframeminheigth);
		suframeminheigth.setFieldLabel("Min. Height [0]");
		
		uframeminwidth = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				suframeminwidth.setFieldLabel("Min. Width [" + value + "]");
			}

		};
		uframeminwidth.setMinValue(50);
		uframeminwidth.setMaxValue(600);
		uframeminwidth.setValue(100);
		uframeminwidth.setIncrement(25);
		uframeminwidth.setMessage("Min. Width: {0}");

		suframeminwidth= new SliderField(uframeminwidth);
		suframeminwidth.setFieldLabel("Min. Width [0]");
		
		
		
		
		
		
		elementOptions.add(radioGroup);
		elementOptions.add(URLFieldLabel);
		elementOptions.add(sfwindowheigth);
		elementOptions.add(sfwindowwidth);
		
		elementOptions.add(suframeminheigth);
		elementOptions.add(suframeminwidth);

		elementOptions.setVisible(true);
	}

	private void showScoreOptions() {

		scoreFieldLabel = new TextField<String>();
		scoreFieldLabel.setFieldLabel("Label");
		scoreFieldLabel.setAllowBlank(false);
		scoreFieldLabel.setEmptyText("Enter a label for the score field");

		elementOptions.add(scoreFieldLabel);

		minScore = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfMinScore.setFieldLabel("Min. Score [" + value + "]");
				startScore.setMinValue(value);

				if (startScore.getValue() < value) {
					startScore.setValue(value);
				}
			}

		};
		minScore.setMinValue(-10);
		minScore.setMaxValue(0);
		minScore.setValue(0);
		minScore.setIncrement(1);
		minScore.setMessage("Minimal score: {0}");

		sfMinScore = new SliderField(minScore);
		sfMinScore.setFieldLabel("Min. Score [0]");

		elementOptions.add(sfMinScore);

		maxScore = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfMaxScore.setFieldLabel("Max. Score [" + value + "]");
				startScore.setMaxValue(value);

				if (startScore.getValue() > value) {
					startScore.setValue(value);
				}
			}

		};
		maxScore.setMinValue(1);
		maxScore.setMaxValue(10);
		maxScore.setValue(1);
		maxScore.setIncrement(1);
		maxScore.setMessage("Maximal score: {0}");

		sfMaxScore = new SliderField(maxScore);
		sfMaxScore.setFieldLabel("Max. Score [1]");

		elementOptions.add(sfMaxScore);

		startScore = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfStartScore.setFieldLabel("Default Score [" + value + "]");

			}

		};
		startScore.setMinValue(minScore.getValue());
		startScore.setMaxValue(maxScore.getValue());
		startScore.setValue(0);
		startScore.setIncrement(1);
		startScore.setMessage("Default score: {0}");

		sfStartScore = new SliderField(startScore);
		sfStartScore.setFieldLabel("Default Score [0]");

		elementOptions.add(sfStartScore);
		elementOptions.setVisible(true);
	}

	private void showAwarenessOptions() {
		// Currently, nothing to add
		elementID.setVisible(false);
	}

	private void showTranscriptLinkOptions() {
		elementID.setVisible(false);
	}

	private void showTextFieldOptions() {
		// Use Label
		useLabelForTextField = new FieldSet();
		FormLayout useLabelForTextFieldLayout = new FormLayout();
		useLabelForTextFieldLayout.setLabelWidth(150);
		useLabelForTextField.setLayout(useLabelForTextFieldLayout);
		useLabelForTextField.setHeading("Use label for the text field");
		useLabelForTextField.setCheckboxToggle(true);
		useLabelForTextField.setExpanded(false);

		textFieldLabel = new TextField<String>();
		textFieldLabel.setFieldLabel("Label");
		textFieldLabel.setAllowBlank(false);
		textFieldLabel.setEmptyText("Enter a label for the text field");

		useLabelForTextField.add(textFieldLabel);

		elementOptions.add(useLabelForTextField);

		// Single / Multiline
		// The number of lines must be multiplied by 14 to get the height of the
		// text field
		useMultiLine = new FieldSet();
		FormLayout useMultiLineLayout = new FormLayout();
		useMultiLineLayout.setLabelWidth(150);
		useMultiLine.setLayout(useMultiLineLayout);
		useMultiLine.setHeading("Use multiple lines");
		useMultiLine.setCheckboxToggle(true);
		useMultiLine.setExpanded(false);

		textFieldMinHeight = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				tfMinHeight.setFieldLabel("Min. no. of visible lines [" + value + "]");
			}

		};
		textFieldMinHeight.setMinValue(2);
		textFieldMinHeight.setMaxValue(10);
		textFieldMinHeight.setValue(2);
		textFieldMinHeight.setIncrement(1);
		textFieldMinHeight.setMessage("Min. no. of visible visible lines: {0}");

		tfMinHeight = new SliderField(textFieldMinHeight);
		tfMinHeight.setFieldLabel("Min. no. of visible lines [2]");

		useMultiLine.add(tfMinHeight);

		textFieldMaxHeight = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				tfMaxHeight.setFieldLabel("Max. no. of visible lines [" + value + "]");
			}

		};
		textFieldMaxHeight.setMinValue(2);
		textFieldMaxHeight.setMaxValue(10);
		textFieldMaxHeight.setValue(2);
		textFieldMaxHeight.setIncrement(1);
		textFieldMaxHeight.setMessage("Max. no. of visible lines: {0}");

		tfMaxHeight = new SliderField(textFieldMaxHeight);
		tfMaxHeight.setFieldLabel("Max. no. visible of lines [2]");

		useMultiLine.add(tfMinHeight);
		useMultiLine.add(tfMaxHeight);

		elementOptions.add(useLabelForTextField);
		elementOptions.add(useMultiLine);

		elementOptions.setVisible(true);
	}

	public void resetElementList() {
		elements.removeAll();
	}

	public void addElementToList(String name) {
		elements.add(name);
	}

	private void clearAddElementPanel() {
		elementID.setAllowBlank(true);
		elementID.reset();
		elementID.setAllowBlank(false);

		type.setForceSelection(false);
		type.reset();
		type.setForceSelection(true);

		if (dropDownElements != null) {
			dropDownElements.setAllowBlank(true);
			dropDownElements.reset();
			dropDownElements.setAllowBlank(false);
		}

		if (dropDownFieldLabel != null) {
			dropDownFieldLabel.setAllowBlank(true);
			dropDownFieldLabel.reset();
			dropDownFieldLabel.setAllowBlank(false);
		}

		if (sfMaxScore != null) {
			sfMaxScore.reset();
		}

		if (sfMinScore != null) {
			sfMinScore.reset();
		}

		if (sfStartScore != null) {
			sfStartScore.reset();
		}

		if (tfMaxHeight != null) {
			tfMaxHeight.reset();
		}

		if (tfMinHeight != null) {
			tfMinHeight.reset();
		}

		if (textFieldLabel != null) {
			textFieldLabel.setAllowBlank(true);
			textFieldLabel.reset();
			textFieldLabel.setAllowBlank(false);
		}

		if (useLabelForTextField != null) {
			useLabelForTextField.setExpanded(false);
		}

		if (useMultiLine != null) {
			useMultiLine.setExpanded(false);
		}

		elementOptions.setVisible(false);
	}

	private void clearDelElementPanel() {
		elements.setForceSelection(false);
		elements.reset();
		elements.setForceSelection(true);

		if (parent.getChildElements().keySet().size() == 0) {
			delElementForm.setEnabled(false);
		} else {
			delElementForm.setEnabled(true);
		}
	}

	public StepCreateAndDeleteElement(ElementInfo topLevelElementInfo) {
		setLayout(new AccordionLayout());

		setHeaderVisible(false);
		setBorders(false);

		initAddElementPanel(topLevelElementInfo.getElementType());
		initDelElementPanel();

		if (topLevelElementInfo.getElementType().equalsIgnoreCase("box")) {
			initModifyNodePanel(topLevelElementInfo);
		} else {
			initModifyRelationPanel(topLevelElementInfo);
		}

		layout();

		parent = topLevelElementInfo;

		clearAddElementPanel();
		clearDelElementPanel();

	}

	private void initModifyRelationPanel(ElementInfo elInfo) {
		add(new StepCreateModifyAndDeleteRelation().initAddOrModifyRelationPanel(elInfo));
	}

	private void initModifyNodePanel(ElementInfo elInfo) {
		add(new StepCreateModifyAndDeleteNode().initAddOrModifyNodePanel(elInfo));
	}

	private void initAddElementPanel(String topLevelElementType) {

		FormPanel addTopLevelElementForm = new FormPanel();
		addTopLevelElementForm.setWidth(350);
		addTopLevelElementForm.setScrollMode(Scroll.AUTOY);
		addTopLevelElementForm.setButtonAlign(HorizontalAlignment.LEFT);
		addTopLevelElementForm.setHeading("Add a new element...");

		FieldSet typeField = new FieldSet();
		FormLayout typeFieldLayout = new FormLayout();
		typeFieldLayout.setLabelWidth(160);
		typeField.setLayout(typeFieldLayout);
		typeField.setHeading("General element settings");

		type.setFieldLabel("Type");

		if (!topLevelElementType.equalsIgnoreCase("relation")) {
			type.add("Internal Link");
		}

		type.add("Text Field");
		type.add("Awareness");
		type.add("Score");
		type.add("Dropdown");
		type.add("URL");
		type.add("URL Display");
		type.add("Radio Buttons");
		type.setEditable(false);
		type.setTriggerAction(TriggerAction.ALL);
		type.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				if (se.getSelectedItem() != null) {
					String selection = se.getSelectedItem().getValue();

					if (!selection.equals(elementType)) {
						elementType = selection;
						switchToElementDetails(selection);
					}

					elementType = null;
				}
			}
		});

		typeField.add(type);

		elementID = new TextField<String>();
		elementID.setFieldLabel("Unique ID");
		elementID.setEmptyText("Enter a unique identifier for the element");
		elementID.setAllowBlank(false);
		elementID.setMinLength(1);

		typeField.add(elementID);

		elementOptions = new FieldSet();
		elementOptions.setHeading("Additional options for this element");
		FormLayout elementOptionsLayout = new FormLayout();
		elementOptionsLayout.setLabelWidth(160);
		elementOptions.setLayout(elementOptionsLayout);
		elementOptions.setVisible(false);

		addTopLevelElementForm.add(typeField);
		addTopLevelElementForm.add(elementOptions);

		previewButton = new Button("Preview") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields()) {
					Vector<Parameter> subElOptions = getElOptionsVector(selection);
					Vector<Parameter> subUiOptions = getUiOptionsVector(selection);

					String id = elementID.getValue();
					ElementInfo newElement=null;
					if(isradiobuttons){
					newElement= OntologyGenerator.createElementInfo(id, internalElementType, 1, 1, 1, subElOptions, subUiOptions,getRadioButtonsVector());	
					}
					else{
					newElement= OntologyGenerator.createElementInfo(id, internalElementType, 1, 1, 1, subElOptions, subUiOptions);
					}
					parent.getChildElements().put(id, newElement);

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(parent);
					parent.getChildElements().remove(id);
				}
			}
		};

		saveButton = new Button("Save") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields()) {
					// Add default text field
					Vector<Parameter> subElOptions = getElOptionsVector(selection);
					Vector<Parameter> subUiOptions = getUiOptionsVector(selection);

					String id = elementID.getValue();

					ElementInfo newElement = null;
					if (internalElementType.equals("transcript-link")) {
						
						if(isradiobuttons){
			
					    newElement = OntologyGenerator.createElementInfo("transcriptlink", internalElementType, 0, 1, 0, subElOptions, subUiOptions,getRadioButtonsVector());		
						}else
						{
						newElement = OntologyGenerator.createElementInfo("transcriptlink", internalElementType, 0, 1, 0, subElOptions, subUiOptions);
						}	
						} else {
							
							if(isradiobuttons){
								newElement = OntologyGenerator.createElementInfo(id, internalElementType, 1, 1, 1, subElOptions, subUiOptions,getRadioButtonsVector());
									
								
							}else{
						newElement = OntologyGenerator.createElementInfo(id, internalElementType, 1, 1, 1, subElOptions, subUiOptions);
					}
							}

					parent.getChildElements().put(id, newElement);

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(parent);

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology, CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					CreateModifyAndDeleteOntology.clearElementActions();
				}
			}
		};

		addTopLevelElementForm.setBodyBorder(false);
		addTopLevelElementForm.addButton(previewButton);
		addTopLevelElementForm.addButton(saveButton);

		addTopLevelElementForm.layout();

		add(addTopLevelElementForm);
	}

	protected Vector<Parameter> getUiOptionsVector(String sel) {
		Vector<Parameter> uiOptions = new Vector<Parameter>();

		if (sel.equals("Text Field")) {
			// Multiline
			if (useMultiLine.isExpanded()) {
				uiOptions.add(new Parameter(ParameterTypes.MinHeight, (16 * textFieldMinHeight.getValue()) + ""));
				uiOptions.add(new Parameter(ParameterTypes.MaxHeight, (16 * textFieldMaxHeight.getValue()) + ""));
			}
			// Single-line
			else {
				uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
				uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));
			}
		} else if (sel.equals("Internal Link")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));

		} else if (sel.equals("Awareness")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));

		} else if (sel.equals("Score")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));
		} else if (sel.equals("Dropdown")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "22"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "22"));
		} else if (sel.equals("URL")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));
		}

		else if (sel.equals("URL Display")) {
			
			String url = "";
			if (URLFieldLabel.getValue() != null) {
				url=URLFieldLabel.getValue();
			}
			
			if (!url.equals("")) {
				if(!url.startsWith("http://") && !url.startsWith("HTTP://") ){
					url="http://"+url;	
				}
			}
			
//			if (url != null && !url.equals("")) {
//				if(!url.startsWith("http://") && !url.startsWith("HTTP://") ){
//					url="http://"+url;	
//				}
//			}
		
			
			uiOptions.add(new Parameter(ParameterTypes.MinWidth, uframeminwidth.getValue()+""));
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, uframeminheigth.getValue()+""));
			uiOptions.add(new Parameter(ParameterTypes.WindowHeight, windowheigth.getValue() + ""));
			uiOptions.add(new Parameter(ParameterTypes.WindowWidth, windowwidth.getValue() + ""));
			uiOptions.add(new Parameter(ParameterTypes.DefaultURL,url));
			uiOptions.add(new Parameter(ParameterTypes.Editable, isEditable()));
		
						
		}
		else if (sel.equals("Radio Buttons")) {
			
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "30"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "30"));
			uiOptions.add(new Parameter(ParameterTypes.Width, "212"));
			
		}
		
		
		return uiOptions;
	}

	
	
	
	 public static String getRandomString() {
	    	
		    double a=	Math.random();
		    	String ak=Double.toString(a).substring(3, 11);
		    	
		        return ak;
		        
		    }
	
	protected RadioButtonParameter getRadioButtonsVector() {
	
		RadioButtonParameter radiobuttons=new RadioButtonParameter();
		Map<String, String> radioButton1=new HashMap<String, String>();
		Map<String, String> radioButton2=new HashMap<String, String>();
		Map<String, String> radioButton3=new HashMap<String, String>();
		
	//String _name=	radioButtonFieldLabel.getValue();
	//radiobuttons.setName(_name);
String rb1L=radioButtonOption1.getValue();
String rb1C=s1;
String rb1ID=getRandomString()+"1";


String rb2L=radioButtonOption2.getValue();
String rb2C=s2;
String rb2ID=getRandomString()+"2";

String rb3L=radioButtonOption3.getValue();
String rb3C=s3;
String rb3ID=getRandomString()+"3";;

	radioButton1.put("id", rb1ID);
	radioButton1.put("checked",rb1C);
	radioButton1.put("label",rb1L);
	

	radioButton2.put("id", rb2ID);
	radioButton2.put("checked",rb2C);
	radioButton2.put("label",rb2L);	
	
	
	radioButton3.put("id", rb3ID);
	radioButton3.put("checked", rb3C);
	radioButton3.put("label",rb3L);
	
	
radiobuttons.addValue(radioButton1);
radiobuttons.addValue(radioButton2);
if(rb3L!="" && rb3L!=null)
radiobuttons.addValue(radioButton3);
return radiobuttons;
	}
	
	
	
	
	
	
	
	
	
	protected String isEditable() {
	String mode = "";

		if (radiononEditable.getValue()) {
			
			mode="false";
			
		} else if (radioEditable.getValue()) {
			mode="true";
		}
		else{
			
			mode="true";
		}

		return mode;
	}
	
	
	protected Vector<Parameter> getElOptionsVector(String sel) {
		Vector<Parameter> elOptions = new Vector<Parameter>();

		if (sel.equals("Text Field")) {
			// Multiline
			if (useMultiLine.isExpanded()) {
				elOptions.add(new Parameter(ParameterTypes.TextType, "textarea"));
			}
			// Single-line
			else {
				elOptions.add(new Parameter(ParameterTypes.TextType, "textfield"));
			}

			if (useLabelForTextField.isExpanded()) {
				elOptions.add(new Parameter(ParameterTypes.Label, textFieldLabel.getValue()));
			}
		} else if (sel.equals("Internal Link")) {
			elOptions.add(new Parameter(ParameterTypes.LongLabel, "Internal-Link"));
			elOptions.add(new Parameter(ParameterTypes.ManualAdd, "false"));
			// TODO: Startrow, Endrow, Startpoint und Endpoint waren hier klein geschrieben, sind jetzt aber auf die gro�-geschriebene Variante abgebildet. Pr�fen ob das ok ist oder Probleme verursacht.
			elOptions.add(new Parameter(ParameterTypes.StartRow, ""));
			elOptions.add(new Parameter(ParameterTypes.EndRow, ""));
			elOptions.add(new Parameter(ParameterTypes.StartPoint, ""));
			elOptions.add(new Parameter(ParameterTypes.EndPoint, ""));
		} else if (sel.equals("Awareness")) {
			// Currently nothing to add
		} else if (sel.equals("Score")) {
			// TODO: Score war hier klein geschrieben, ist jetzt aber auf die gro�-geschriebene Variante abgebildet. Pr�fen ob das ok ist oder Probleme verursacht.
			elOptions.add(new Parameter(ParameterTypes.Score, startScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.MinScore, minScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.MaxScore, maxScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.Label, scoreFieldLabel.getValue()));
		} else if (sel.equals("Dropdown")) {

			elOptions.add(new Parameter(ParameterTypes.Label, dropDownFieldLabel.getValue()));

			String[] ddOptions = dropDownElements.getValue().split("\n");

			//String startSelection = "";
			String ddOptionsString = "";
			for (int i = 0; i < ddOptions.length; i++) {
				if (i != 0) {
					ddOptionsString += ",";
				} else {
					/*startSelection =*/ ddOptions[i].trim();
				}
				ddOptionsString += ddOptions[i].trim();
			}

			elOptions.add(new Parameter(ParameterTypes.Options, ddOptionsString));

			// TODO Check if we want the user to provide a start selection, if
			// it should be the first entry or - as it is now - there should be
			// the empty one selected on startup
			elOptions.add(new Parameter(ParameterTypes.SelectedOption, ""));

		} else if (sel.equals("URL")) {
			// Currently nothing to add
		}

		
		else if (sel.equals("URL Display")) {
		
		}

		
		else if (sel.equals("Radio Buttons")) {
		String _label=radioButtonFieldLabel.getValue();
		
			elOptions.add(new Parameter(ParameterTypes.Label,_label));
			
		}

		
		return elOptions;
	}

	private void initDelElementPanel() {
		delElementForm = new FormPanel();
		delElementForm.setButtonAlign(HorizontalAlignment.LEFT);
		delElementForm.setHeading("Delete an existing element...");

		elements = new SimpleComboBox<String>();
		elements.setFieldLabel("Element");
		elements.setEmptyText("Choose an element");
		elements.setForceSelection(true);
		elements.setAllowBlank(false);
		elements.setTriggerAction(TriggerAction.ALL);

		Button deleteButton = new Button("Delete") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (elements.validate()) {

					// Get the concrete element ID without the element type,
					// which is always added in parentheses
					String id = elements.getSimpleValue();
					int index = id.indexOf("("); // Get the opening parenthesis

					// Get substring before the parenthesis. The -- removes the
					// whitespace.
					String internalID = id.substring(0, --index);

					parent.getChildElements().remove(internalID);

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology, CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					LASADInfo.display("Info", "Updating ontology on server...");

					CreateModifyAndDeleteOntology.clearElementActions();

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(parent);

				} else {
					LASADInfo.display("Error", "Please select an element");
				}
			}
		};

		delElementForm.setBodyBorder(false);
		delElementForm.add(elements);
		delElementForm.addButton(deleteButton);

		add(delElementForm);
	}

	private boolean validateFields() {
		boolean ok = true;

		if(selection == null) {
			ok = false;
		}
		else {
			if (selection.equals("Text Field")) {
				if (!validateTextFieldOptions()) {
					ok = false;
				}
			} else if (selection.equals("Awareness")) {
	
				elementID.setValue("author");
				// if (!validateStandardOptions()) {
				// ok = false;
				// }
			} else if (selection.equals("Score")) {
				if (!validateScoreOptions()) {
					ok = false;
				}
			} else if (selection.equals("Dropdown")) {
				if (!validateDropdownOptions()) {
					ok = false;
				}
			} else if (selection.equals("URL")) {
				elementID.setValue("url");
				// if (!validateStandardOptions()) {
				// ok = false;
				// }
			}
			
			else if (selection.equals("URL Display")) {
				//elementID.setValue("frame");
				 if (!validateStandardOptions()) {
				 ok = false;
				 }
			}
			
			
			
			else if (selection.equals("Radio Buttons")) {
				//elementID.setValue("radiobtn");
				 if (!validateRadioButtonOptions()) {
				 ok = false;
				 }
			}
		}
		return ok;
	}

	private boolean validateTextFieldOptions() {
		boolean ok = validateStandardOptions();

		if (useLabelForTextField.isExpanded()) {
			if (!textFieldLabel.validate()) {
				ok = false;
				LASADInfo.display("Error", "Label must not be empty");
			}
		}

		if(useMultiLine.isExpanded()) {
			if (textFieldMinHeight.getValue() > textFieldMaxHeight.getValue()) {
				ok = false;
				LASADInfo.display("Error", "Minimal number of lines must not be bigger than maximal number of lines");
			}
		}

		return ok;
	}
	
	private boolean validateScoreOptions() {
		boolean ok = validateStandardOptions();

		if (!scoreFieldLabel.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the score field");
		}

		if (minScore.getValue() >= maxScore.getValue()) {
			ok = false;
			LASADInfo.display("Error", "The minimal score must not be bigger than the maximal score");
		}

		if (startScore.getValue() > maxScore.getValue() || startScore.getValue() < minScore.getValue()) {
			ok = false;
			LASADInfo.display("Error", "The start score must be between minimal and maximal score");
		}

		return ok;
	}

	private boolean validateDropdownOptions() {
		boolean ok = validateStandardOptions();

		if (!dropDownFieldLabel.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the drop down list");
		}

		if (dropDownElements.getValue() == null || dropDownElements.getValue().trim().length() == 0) {
			ok = false;
			LASADInfo.display("Error", "Dropdown elements must at least contain one item");
		}

		return ok;
	}

	
	private boolean validateRadioButtonOptions() {
		boolean ok = validateStandardOptions();
		
		if (!radioButtonFieldLabel.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the radio button list");
		}

/*		if (radioButtonFieldLabel.getValue() == null || radioButtonFieldLabel.getValue().trim().length() == 0) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the radio button list");
		}
	*/	
		
		
		
		if (!radioButtonOption1.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the first radio button");
		}

		if (radioButtonOption1.getValue() == null || radioButtonOption1.getValue().trim().length() == 0) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the first radio button");
		}
		
		
		if (!radioButtonOption2.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the second radio button");
		}

		if (radioButtonOption2.getValue() == null || radioButtonOption2.getValue().trim().length() == 0) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the second radio button");
		}
		
		

		return ok;
	}
	
	private boolean validateStandardOptions() {
		if (elementID.validate()) {
			return true;
		} else {
			LASADInfo.display("Error", "Element ID must not be empty");
			return false;
		}
	}
}