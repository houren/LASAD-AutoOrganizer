package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.FeedbackGrid;
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
 * Add feedback message window
 * @author Anahuac
 *
 */
public class AddFeedbackWindow extends Window{
	private List<String> optionList;
	private RadioGroup optionRG;
	private FeedbackGrid feedbackGrid;
	private TextField<String> feedbackNameField;
	
	public AddFeedbackWindow(String heading, List<String> optionList, FeedbackGrid feedbackGrid) {
		this.optionList = optionList;
		initSelectAgentWindow(heading);
		this.feedbackGrid = feedbackGrid; 
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
				String feedbackName = feedbackNameField.getValue();
				
				Radio selectedRadio = optionRG.getValue();
				String value = selectedRadio.getBoxLabel();
				Info.display("Selected element", feedbackName + "   " + value);
				doAction(feedbackName, value);
				hide();
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
	
	public void doAction(String feedbackName, String option) {
		feedbackGrid.handleAddFeedback(feedbackName, option);
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
		
		feedbackNameField = new TextField<String>();
		feedbackNameField.setFieldLabel(FeedbackAuthoringStrings.MESSAGE_NAME_LABEL);
		feedbackNameField.setAllowBlank(false);
		fPanel.add(feedbackNameField);
		
		optionRG = new RadioGroup();
		boolean first = true;
		for(String opt:optionList){
			Radio radio = new Radio();
			radio.setBoxLabel(opt);
			if(first){
				first = false;
				radio.setValue(true);
			}
			radio.setStyleAttribute("color", "#000000");
			optionRG.add(radio);
		}
		optionRG.setOrientation(Orientation.VERTICAL);
		//optionRG.setLabelSeparator("");
		optionRG.setSelectionRequired(true);
		optionRG.setFieldLabel(FeedbackAuthoringStrings.SELECT_TYPE_LABEL);
		
	    fPanel.add(optionRG);
	    panel.add(fPanel);
	    return panel;
	}
	
	
	
	
}
