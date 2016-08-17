package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Customized window that shows radio buttons based on the List<String> provided
 * @author Anahuac
 *
 */
public abstract class RadioGroupWindow extends Window{
	
	private List<String> optionList;
	private RadioGroup optionRG;
	
	public RadioGroupWindow(String heading, List<String> optionList) {
		super();
		this.optionList = optionList;
		initSelectAgentWindow(heading);
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
				Radio selectedRadio = optionRG.getValue();
				String value = selectedRadio.getBoxLabel();
				doAction(value);
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
	
	public abstract void doAction(String option);
	
	private ContentPanel getPanelContent(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(350);
		
		Label info = new Label(FeedbackAuthoringStrings.SELECT_TYPE_LABEL + ":");
		fPanel.add(info);
		
		optionRG = new RadioGroup();
		for(String opt:optionList){
			Radio radio = new Radio();
			radio.setBoxLabel(opt);
			optionRG.add(radio);
		}
		optionRG.setOrientation(Orientation.VERTICAL);
		optionRG.setLabelSeparator("");
		optionRG.setSelectionRequired(true);
		
	    fPanel.add(optionRG);
	    panel.add(fPanel);
	    return panel;
	}

}
