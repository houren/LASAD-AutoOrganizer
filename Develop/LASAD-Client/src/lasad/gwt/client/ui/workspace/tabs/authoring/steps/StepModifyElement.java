package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;



public class StepModifyElement extends ContentPanel {

	private LASADActionSender communicator = LASADActionSender.getInstance();
	private ActionFactory builder = ActionFactory.getInstance();

	private Button previewButton, saveButton;
	private String selection,s1="0",s2="0",s3="0";

	private TextField<String> elementID,URLFieldLabel,textFieldLabel,radioButtonFieldLabel,radioButtonOption1,radioButtonOption2,radioButtonOption3, scoreFieldLabel, dropDownFieldLabel;
	private TextArea dropDownElements;
	private ArrayList<String> radiobuttonIDs;
	
	private Slider uframeminheigth,uframeminwidth,minScore, maxScore, startScore,windowheigth,windowwidth, textFieldMinHeight, textFieldMaxHeight;
	private SliderField suframeminheigth,suframeminwidth,sfStartScore,sfwindowheigth,sfwindowwidth,sfMinScore, sfMaxScore, tfMinHeight, tfMaxHeight;
	private FieldSet useLabelForTextField, useMultiLine, elementOptions;
	private ElementInfo parent;
	private String fontStyleStart = "<span style=\"color: #000000;\">";
	private String fontStyleEnd = "</span>";
	private Radio radioEditable, radiononEditable,r1,r2,r3;;
	
	private boolean isradiobuttons=false;

	private void showDropdownOptions(ElementInfo currentChildInfo) {

		dropDownFieldLabel = new TextField<String>();
		dropDownFieldLabel.setFieldLabel("Label");
		dropDownFieldLabel.setAllowBlank(false);
		dropDownFieldLabel.setEmptyText("Enter a label for the drop down list");

		dropDownElements = new TextArea();
		dropDownElements.setFieldLabel("Options (one per line)");

		if(currentChildInfo.getElementOption(ParameterTypes.Label) != null) {
			dropDownFieldLabel.setValue(currentChildInfo.getElementOption(ParameterTypes.Label));
		}
		
		if(currentChildInfo.getElementOption(ParameterTypes.Options) != null) {
			String options = currentChildInfo.getElementOption(ParameterTypes.Options);
			options = options.replace(",", "\n");
			
			dropDownElements.setValue(options);
		}
		
		elementOptions.add(dropDownFieldLabel);
		elementOptions.add(dropDownElements);
		
		elementOptions.setVisible(true);
	}
	
void showRadioButtonOptions(ElementInfo currentChildInfo){
		radiobuttonIDs=new ArrayList<String>();
		radioButtonFieldLabel = new TextField<String>();
		radioButtonFieldLabel.setFieldLabel(fontStyleStart+"Label");
		radioButtonFieldLabel.setAllowBlank(true);
		radioButtonFieldLabel.setEmptyText("Enter a label for Radio Buttons");
		radioButtonFieldLabel.setValue(currentChildInfo.getElementOption(ParameterTypes.Label));
		
		radioButtonOption1 = new TextField<String>();
		radioButtonOption1.setFieldLabel(fontStyleStart+"Option1"+fontStyleEnd);
		radioButtonOption1.setAllowBlank(false);
		radioButtonOption1.setEmptyText("Enter first option");
		
		radioButtonOption1.setValue(currentChildInfo.getRadioButtonElements().get(0).get("label"));
		radiobuttonIDs.add(currentChildInfo.getRadioButtonElements().get(0).get("id"));
		
		
		
		radioButtonOption2 = new TextField<String>();
		radioButtonOption2.setFieldLabel(fontStyleStart+"Option2"+fontStyleEnd);
		radioButtonOption2.setAllowBlank(false);
		radioButtonOption2.setEmptyText("Enter second option");
		radioButtonOption2.setValue(currentChildInfo.getRadioButtonElements().get(1).get("label"));
		radiobuttonIDs.add(currentChildInfo.getRadioButtonElements().get(1).get("id"));
		
		radioButtonOption3 = new TextField<String>();
		radioButtonOption3.setFieldLabel(fontStyleStart+"Option3"+fontStyleEnd);
		radioButtonOption3.setAllowBlank(true);
		radioButtonOption3.setEmptyText("Enter third option");
	
		
		
		
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
		//r1.setValue(true);
		
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
		
		
		String checked=currentChildInfo.getRadioButtonElements().get(0).get("checked");
		if(checked.equals("1") ||  checked.equals("true")){	
			r1.setValue(true);
			s1="1";
			s2 = "0";
			s3="0";
		}
		 checked=currentChildInfo.getRadioButtonElements().get(1).get("checked");
		if(checked.equals("1") ||  checked.equals("true")){	
			r2.setValue(true);
			s1="0";
			s2 = "1";
			s3="0";
		}
		
		
		
		
		if(currentChildInfo.getRadioButtonElements().size()>=3){
			radioButtonOption3.setValue(currentChildInfo.getRadioButtonElements().get(2).get("label"));
			radiobuttonIDs.add(currentChildInfo.getRadioButtonElements().get(2).get("id"));
			
		   checked=currentChildInfo.getRadioButtonElements().get(2).get("checked");
			if(checked.equals("1") ||  checked.equals("true")){	
				r3.setValue(true);
				s1="0";
				s2 = "0";
				s3="1";
			}
			}
		
		
		
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Default Selection");
		radioGroup.add(r1);
		radioGroup.add(r2);
		radioGroup.add(r3);
		
		elementOptions.add(radioButtonFieldLabel);
		elementOptions.add(radioButtonOption1);
		elementOptions.add(radioButtonOption2);
		elementOptions.add(radioButtonOption3);
		elementOptions.add(radioGroup);
		
		elementOptions.setVisible(true);
		isradiobuttons=true;
		
		
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
String rb1ID=radiobuttonIDs.get(0);


String rb2L=radioButtonOption2.getValue();
String rb2C=s2;
String rb2ID=radiobuttonIDs.get(1);

String rb3L=radioButtonOption3.getValue();
String rb3C=s3;
String rb3ID="";
if(radiobuttonIDs.size()>=3)
rb3ID=radiobuttonIDs.get(2);

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
	
	private void showScoreOptions(ElementInfo currentChildInfo) {

		scoreFieldLabel = new TextField<String>();
		scoreFieldLabel.setFieldLabel("Label");
		scoreFieldLabel.setAllowBlank(false);
		scoreFieldLabel.setEmptyText("Enter a label for the score field");

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

		if(currentChildInfo.getElementOption(ParameterTypes.Label) != null) {
			scoreFieldLabel.setValue(currentChildInfo.getElementOption(ParameterTypes.Label));
		}
		
		if(currentChildInfo.getElementOption(ParameterTypes.MinScore) != null) {
			sfMinScore.setValue(Integer.parseInt(currentChildInfo.getElementOption(ParameterTypes.MinScore)));
		}
		
		if(currentChildInfo.getElementOption(ParameterTypes.MaxScore) != null) {
			sfMaxScore.setValue(Integer.parseInt(currentChildInfo.getElementOption(ParameterTypes.MaxScore)));
		}
		
		if(currentChildInfo.getElementOption(ParameterTypes.Score) != null) {
			sfStartScore.setValue(Integer.parseInt(currentChildInfo.getElementOption(ParameterTypes.Score)));
		}
		
		elementOptions.add(scoreFieldLabel);
		
		elementOptions.add(sfMinScore);
		elementOptions.add(sfMaxScore);
		
		elementOptions.add(sfStartScore);
		elementOptions.setVisible(true);
	}

	private void showTextFieldOptions(ElementInfo currentChildInfo) {
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

		// Restore current settings of the element
		if(currentChildInfo.getElementOption(ParameterTypes.Label) != null) {
			useLabelForTextField.setExpanded(true);
			textFieldLabel.setValue(currentChildInfo.getElementOption(ParameterTypes.Label));
		}
		
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

		if(!"textfield".equals(currentChildInfo.getElementOption(ParameterTypes.TextType))) {
			useMultiLine.setExpanded(true);
			
			int minLineCount = Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.MinHeight)) / 16;
			
			int maxLineCount = minLineCount;
			if(currentChildInfo.getUiOption(ParameterTypes.MaxHeight) != null) {
				maxLineCount = Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.MaxHeight)) / 16;	
			}
			
			textFieldMinHeight.setValue(minLineCount);
			textFieldMaxHeight.setValue(maxLineCount);
		}
		
		elementOptions.add(useLabelForTextField);
		elementOptions.add(useMultiLine);

		elementOptions.setVisible(true);
	}

	
	private void showUframeOptions(ElementInfo currentChildInfo) {

		
		radioEditable = new Radio();
//		{
//			@Override
//			protected void onClick(ComponentEvent be) {
//				super.onClick(be);
//				viewMode = "editable";
//			}
//		};
		radioEditable.setBoxLabel(fontStyleStart+"Editable"+fontStyleEnd);
		radioEditable.setValue(true);

		radiononEditable = new Radio();
//		{
//			@Override
//			protected void onClick(ComponentEvent be) {
//				super.onClick(be);
//				viewMode = "noneditable";
//			}
//		};
		radiononEditable.setBoxLabel(fontStyleStart+"Non-Editable"+fontStyleEnd);

		
		
		if(currentChildInfo.getUiOption(ParameterTypes.Editable) != null && !currentChildInfo.getUiOption(ParameterTypes.Editable).isEmpty()) {
			boolean mode=Boolean.parseBoolean(currentChildInfo.getUiOption(ParameterTypes.Editable));
			if(mode){
				radioEditable.setValue(true);
				radiononEditable.setValue(false);
				
			}
			else{
				
				radioEditable.setValue(false);
				radiononEditable.setValue(true);
				
			}
		}
		else{
			
			radioEditable.setValue(true);
			radiononEditable.setValue(false);
		}
		
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Display Mode");
		radioGroup.add(radioEditable);
		radioGroup.add(radiononEditable);
		
		
		URLFieldLabel = new TextField<String>();
		URLFieldLabel.setFieldLabel("Default URL");
		URLFieldLabel.setAllowBlank(true);
		URLFieldLabel.setEmptyText("Enter a default URL");
		
		
		

		if(currentChildInfo.getUiOption(ParameterTypes.DefaultURL) != null && !currentChildInfo.getUiOption(ParameterTypes.DefaultURL).isEmpty()) {
			URLFieldLabel.setValue(currentChildInfo.getUiOption(ParameterTypes.DefaultURL));
		}
		else{
			
			URLFieldLabel.setValue("");
			
		}
		
		
		
		windowheigth = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfwindowheigth.setFieldLabel("Window Height [" + value + "]");
			}

		};
		windowheigth.setMinValue(150);
		windowheigth.setMaxValue(600);
		
		
		int windheigth=400;
		if(currentChildInfo.getUiOption(ParameterTypes.WindowHeight) != null && !currentChildInfo.getUiOption(ParameterTypes.WindowHeight).isEmpty()) {
			windheigth=Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.WindowHeight));
		}
		windowheigth.setValue(windheigth);
		
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
		
		
		int windwidth=400;
		if(currentChildInfo.getUiOption(ParameterTypes.WindowWidth) != null && !currentChildInfo.getUiOption(ParameterTypes.WindowWidth).isEmpty()) {
			windwidth=Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.WindowWidth));
		}
		windowwidth.setMinValue(150);
		windowwidth.setMaxValue(600);
		windowwidth.setValue(windwidth);
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
		
		
		int minheight=100;
		if(currentChildInfo.getUiOption(ParameterTypes.MinHeight) != null && !currentChildInfo.getUiOption(ParameterTypes.MinHeight).isEmpty()) {
			minheight=Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.MinHeight));
		}
		
		uframeminheigth.setValue(minheight);
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
		
		
		int minwidth=100;
		if(currentChildInfo.getUiOption(ParameterTypes.MinWidth) != null && !currentChildInfo.getUiOption(ParameterTypes.MinWidth).isEmpty()) {
			minwidth=Integer.parseInt(currentChildInfo.getUiOption(ParameterTypes.MinWidth));
		}
		uframeminwidth.setValue(minwidth);
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

	
	public StepModifyElement(ElementInfo topLevelElementInfo, ElementInfo currentChildInfo) {
		setLayout(new AccordionLayout());

		setHeaderVisible(false);
		setBorders(false);

		initModifyElementPanel(topLevelElementInfo, currentChildInfo);

		layout();

		parent = topLevelElementInfo;
	}

	private void initModifyElementPanel(ElementInfo topLevelElementInfo, final ElementInfo currentChildInfo) {

		if(currentChildInfo.getElementType().equals("awareness") || currentChildInfo.getElementType().equals("url") || currentChildInfo.getElementType().equals("transcript-link")) {
			// Nothing to edit
		} else {
			
			FormPanel modifyElementForm = new FormPanel();
			modifyElementForm.setWidth(350);
			modifyElementForm.setScrollMode(Scroll.AUTOY);
			modifyElementForm.setButtonAlign(HorizontalAlignment.LEFT);
			modifyElementForm.setHeading("Modify this element...");
			
			FieldSet typeField = new FieldSet();
			FormLayout typeFieldLayout = new FormLayout();
			typeFieldLayout.setLabelWidth(160);
			typeField.setLayout(typeFieldLayout);
			typeField.setHeading("General element settings");
			
			elementID = new TextField<String>();
			elementID.setFieldLabel("Unique ID");
			elementID.setEmptyText("Enter a unique identifier for the element");
			elementID.setAllowBlank(false);
			elementID.setMinLength(1);

			elementID.setValue(currentChildInfo.getElementID());
			
			typeField.add(elementID);
			
			elementOptions = new FieldSet();
			elementOptions.setHeading("Additional options for this element");
			FormLayout elementOptionsLayout = new FormLayout();
			elementOptionsLayout.setLabelWidth(160);
			elementOptions.setLayout(elementOptionsLayout);
			elementOptions.setVisible(false);

			selection = currentChildInfo.getElementType();

			if (selection.equals("text")) {
				showTextFieldOptions(currentChildInfo);
			} else if (selection.equals("rating")) {
				showScoreOptions(currentChildInfo);
			} else if (selection.equals("dropdown")) {
				showDropdownOptions(currentChildInfo);
			}
			else if (selection.equals("frame")) {
				showUframeOptions(currentChildInfo);
			} 
			else if(selection.equals("radiobtn")){
				
				showRadioButtonOptions(currentChildInfo);
				
				
			}
		
			previewButton = new Button("Preview") {

				@Override
				protected void onClick(ComponentEvent ce) {
					super.onClick(ce);

					if (validateFields()) {
						
						Vector<Parameter> subElOptions = getElOptionsVector(selection);
						Vector<Parameter> subUiOptions = getUiOptionsVector(selection);

						String id = elementID.getValue();

						ElementInfo newElement = null;
						if (selection.equals("transcript-link")) {
							if(isradiobuttons){
								newElement = OntologyGenerator.createElementInfo("transcriptlink", selection, 0, 1, 0, subElOptions, subUiOptions,getRadioButtonsVector());
									
							}
							else{
							
							newElement = OntologyGenerator.createElementInfo("transcriptlink", selection, 0, 1, 0, subElOptions, subUiOptions);
							}
							} else {
								
								if(isradiobuttons){
									
								newElement = OntologyGenerator.createElementInfo(id, selection, 1, 1, 1, subElOptions, subUiOptions,getRadioButtonsVector());
									
								}else{
							newElement = OntologyGenerator.createElementInfo(id, selection, 1, 1, 1, subElOptions, subUiOptions);
								}}
						
						LinkedHashMap<String, ElementInfo> newOrder = new LinkedHashMap<String, ElementInfo>();
						
						// Replace the old element OR remove it and add the new one
						
						Iterator<String> it = parent.getChildElements().keySet().iterator();
						while(it.hasNext()) {
							String s = it.next();
							if(s.equals(currentChildInfo.getElementID())) {
								newOrder.put(id, newElement);	
							}
							else {
								newOrder.put(s, parent.getChildElements().get(s));
							}
						}	

						parent.setChildElements(newOrder);
						
						CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(parent);
						
						
						LinkedHashMap<String, ElementInfo> oldOrder = new LinkedHashMap<String, ElementInfo>();
						// Revert the changes
						Iterator<String> it2 = parent.getChildElements().keySet().iterator();
						while(it2.hasNext()) {
							String s = it2.next();
							if(s.equals(id)) {
								oldOrder.put(currentChildInfo.getElementID(), currentChildInfo);	
							}
							else {
								oldOrder.put(s, parent.getChildElements().get(s));
							}
						}
						
						parent.setChildElements(oldOrder);
					}
				}
			};

			saveButton = new Button("Save") {

				@Override
				protected void onClick(ComponentEvent ce) {
					super.onClick(ce);

					if (validateFields()) {
						Vector<Parameter> subElOptions = getElOptionsVector(selection);
						Vector<Parameter> subUiOptions = getUiOptionsVector(selection);

						String id = elementID.getValue();

						ElementInfo newElement = null;
						if (selection.equals("transcript-link")) {
							if(isradiobuttons){
								
								newElement = OntologyGenerator.createElementInfo("transcriptlink", selection, 0, 1, 0, subElOptions, subUiOptions,getRadioButtonsVector());
								
							}
							else{
							newElement = OntologyGenerator.createElementInfo("transcriptlink", selection, 0, 1, 0, subElOptions, subUiOptions);
							}
							} else {
								
								if(isradiobuttons){
									newElement = OntologyGenerator.createElementInfo(id, selection, 1, 1, 1, subElOptions, subUiOptions,getRadioButtonsVector());
		
								}
								else{
							newElement = OntologyGenerator.createElementInfo(id, selection, 1, 1, 1, subElOptions, subUiOptions);
						}}

						
						LinkedHashMap<String, ElementInfo> newOrder = new LinkedHashMap<String, ElementInfo>();					
						// Replace the old element OR remove it and add the new one						
						Iterator<String> it = parent.getChildElements().keySet().iterator();
						while(it.hasNext()) {
							String s = it.next();
							if(s.equals(currentChildInfo.getElementID())) {
								newOrder.put(id, newElement);	
							}
							else {
								newOrder.put(s, parent.getChildElements().get(s));
							}
						}	
						parent.setChildElements(newOrder);
						
						
//						if(!currentChildInfo.getElementType().equals(id)) {
//							parent.getChildElements().remove(currentChildInfo.getElementType());
//						}
//						parent.getChildElements().put(id, newElement);
							

						CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(parent);

						String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology, CreateModifyAndDeleteOntology.contributionToElement.keySet());
						communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

						CreateModifyAndDeleteOntology.clearElementActions();
					}
				}
			};

			elementOptions.layout();
			
			modifyElementForm.setBodyBorder(false);
			
			modifyElementForm.add(typeField);
			modifyElementForm.add(elementOptions);

			modifyElementForm.addButton(previewButton);
			modifyElementForm.addButton(saveButton);

			modifyElementForm.layout();
			add(modifyElementForm);
			
			
			layout();
		}		
	}

	protected Vector<Parameter> getUiOptionsVector(String type) {
		Vector<Parameter> uiOptions = new Vector<Parameter>();

		if (type.equals("text")) {
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
		} else if (type.equals("rating")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));
		} else if (type.equals("dropdown")) {
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "22"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "22"));
		}
		
		
		else if (type.equals("frame")) {
			String url=URLFieldLabel.getValue();
			
			if(url==null){
				url="";
				
			}
			
				if (!url.equals("")) {
					if(!url.startsWith("http://") && !url.startsWith("HTTP://") )
						{
						
						url="http://"+url;	
							}
				}
			
			
			uiOptions.add(new Parameter(ParameterTypes.MinHeight,uframeminheigth.getValue()+""));
			uiOptions.add(new Parameter(ParameterTypes.MinWidth,uframeminwidth.getValue()+""));
			uiOptions.add(new Parameter(ParameterTypes.WindowHeight, windowheigth.getValue() + ""));
			uiOptions.add(new Parameter(ParameterTypes.WindowWidth, windowwidth.getValue() + ""));
			uiOptions.add(new Parameter(ParameterTypes.DefaultURL,url));
			uiOptions.add(new Parameter(ParameterTypes.Editable, isEditable()));
		}
		
	else if (type.equals("radiobtn")) {
			
			uiOptions.add(new Parameter(ParameterTypes.MinHeight, "30"));
			uiOptions.add(new Parameter(ParameterTypes.MaxHeight, "30"));
			uiOptions.add(new Parameter(ParameterTypes.Width, "212"));
			
		}
		

		return uiOptions;
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
		

	protected Vector<Parameter> getElOptionsVector(String type) {
		Vector<Parameter> elOptions = new Vector<Parameter>();

		if (type.equals("text")) {
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
		} else if (type.equals("rating")) {
			// TODO: Score war hier kleingeschrieben, ist jetzt aber auf gro�-geschrieben abgebildet. Pr�fen ob das funktioniert.
			elOptions.add(new Parameter(ParameterTypes.Score, startScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.MinScore, minScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.MaxScore, maxScore.getValue() + ""));
			elOptions.add(new Parameter(ParameterTypes.Label, scoreFieldLabel.getValue()));
		} else if (type.equals("dropdown")) {

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

		}
		else if (type.equals("radiobtn")) {
			String _label=radioButtonFieldLabel.getValue();
				elOptions.add(new Parameter(ParameterTypes.Label,_label));
			}
		

		return elOptions;
	}

	private boolean validateFields() {
		boolean ok = true;

		if (selection.equals("text")) {
			if (!validateTextFieldOptions()) {
				ok = false;
			}
		} else if (selection.equals("rating")) {
			if (!validateScoreOptions()) {
				ok = false;
			}
		} else if (selection.equals("dropdown")) {
			if (!validateDropdownOptions()) {
				ok = false;
			}
		}
		 else if (selection.equals("radiobtn")) {
				if (!validateRadioButtonOptions()) {
					ok = false;
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

		if (radioButtonFieldLabel.getValue() == null || radioButtonFieldLabel.getValue().trim().length() == 0) {
			ok = false;
			LASADInfo.display("Error", "Please enter a label for the radio button list");
		}
		
		
		
		
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