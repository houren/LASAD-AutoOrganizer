package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PatternsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Add patter window
 * @author Anahuac
 *
 */
public class AddPatternWindow extends Window {
	PatternsGrid patternGrid;
	private List<String> optionList;
	private RadioGroup optionRG;
	private TextField<String> patternNameField;

	public AddPatternWindow(String heading, List<String> optionList, PatternsGrid patternGrid) {
		super();
		this.optionList = optionList;
		initSelectAgentWindow(heading);
		this.patternGrid = patternGrid;
	}

	private void initSelectAgentWindow(String heading){
		this.setPlain(true);
		this.setModal(true);
		this.setBlinkModal(true);
		this.setSize("350px", "200px");
		//window.setMaximizable(true);
		this.setHeading(heading);
		
		this.setLayout(new FitLayout());
		this.add(getPanelContent());
		this.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//TODO check if we need to do something else before closing
			}
		});
		this.layout();
		
		Button okBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				String patternName = patternNameField.getValue();
				
				Radio selectedRadio = optionRG.getValue();
				if(patternName != null && !patternName.equals("")
						&& selectedRadio != null){
					String value = selectedRadio.getBoxLabel();
					//Info.display("Selected element", patternName + "   " + value);
					doAction(patternName, value);
					hide();
				} else{
					Info.display(FeedbackAuthoringStrings.ERROR_LABEL, "Invalid selections");
				}
		    }
		});
		this.addButton(okBtn);
		
		Button cancelBtn = new Button(FeedbackAuthoringStrings.CANCEL_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				hide();  
			}  
		});
		this.addButton(cancelBtn);
	}
	
	public void doAction(String patternName, String option) {
		patternGrid.handleAddPattern(patternName, option);
	}
	
	private ContentPanel getPanelContent(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(400);
		fPanel.setLabelWidth(100);
		
		patternNameField = new TextField<String>();
		patternNameField.setFieldLabel(FeedbackAuthoringStrings.PATTERN_NAME_LABEL);
		patternNameField.setAllowBlank(false);
		fPanel.add(patternNameField);
		
//		Label info = new Label(FeedbackAuthoringStrings.SELECT_TYPE_LABEL + ":");
//		fPanel.add(info);
		
		optionRG = new RadioGroup();
		boolean flag = true;
		for(String opt:optionList){
			Radio radio = new Radio();
			radio.setBoxLabel(opt);
			radio.setStyleAttribute("color", "#000000");
			if(flag){
				flag = false;
				radio.setValue(true);
			}
			optionRG.add(radio);
			optionRG.setSelectionRequired(true);
		}
		optionRG.setOrientation(Orientation.VERTICAL);
//		optionRG.setLabelSeparator("");
		optionRG.setSelectionRequired(true);
		optionRG.setFieldLabel(FeedbackAuthoringStrings.SELECT_TYPE_LABEL);
		
	    fPanel.add(optionRG);
	    panel.add(fPanel);
	    return panel;
	}

}
