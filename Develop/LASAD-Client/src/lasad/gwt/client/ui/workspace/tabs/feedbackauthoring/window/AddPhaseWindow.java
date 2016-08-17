package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PhasesGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

/**
 * Add phase window
 * @author Anahuac
 *
 */
public class AddPhaseWindow extends Window {
	
	private PhasesGrid phasesGrid;
	private TextField<String> phaseNameField;
	private TextArea phaseDescField;
	
	String phaseId;
	String phaseName;
	String phaseDesc;
	
	public AddPhaseWindow(PhasesGrid phasesGrid) {
		super();
		this.phasesGrid = phasesGrid;
		initSelectAgentWindow();
	}
	
	public AddPhaseWindow(PhasesGrid phasesGrid, String phaseId, String phaseName, String phaseDesc) {
		super();
		this.phasesGrid = phasesGrid;
		this.phaseId = phaseId;
		this.phaseName = phaseName;
		this.phaseDesc = phaseDesc;
		initSelectAgentWindow();
	}
	
	private void initSelectAgentWindow(){
		this.setPlain(true);
		this.setModal(true);
		this.setBlinkModal(true);
		this.setSize("350px", "200px");
		//window.setMaximizable(true);
		this.setHeading(FeedbackAuthoringStrings.ADD_UPC_LABEL + " " + FeedbackAuthoringStrings.PHASE_LABEL);
		
		this.setLayout(new FitLayout());
		this.add(getPanelContent());
		this.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//Do nothing
			}
		});
		this.layout();
		
		Button okBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				String phaseName = phaseNameField.getValue();
				String phaseDesc = phaseDescField.getValue();
				
				if(phaseId == null){ //add mode
					phasesGrid.saveElement(phaseName, phaseDesc);
				} else{ //update mode
					phasesGrid.updateElement(phaseId, phaseName, phaseDesc);
				}
				
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
	
	private Widget getPanelContent(){
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(350);
		
		phaseNameField = new TextField<String>();
		phaseNameField.setFieldLabel(FeedbackAuthoringStrings.PHASE_NAME_LABEL);
		phaseNameField.setAllowBlank(false);
		if(phaseName != null){
			phaseNameField.setValue(phaseName);
		}
	    formPanel.add(phaseNameField);
	    
	    phaseDescField = new TextArea();
	    phaseDescField.setFieldLabel(FeedbackAuthoringStrings.DESCRIPTION_LABEL);
	    phaseDescField.setAllowBlank(false);
	    if(phaseDesc != null){
			phaseDescField.setValue(phaseDesc);
		}
	    formPanel.add(phaseDescField);
		
		return formPanel;
	}

}
