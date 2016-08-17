package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.AgentsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

/**
 * Add agent dialog
 * @author Anahuac
 *
 */
public class AddAgentWindow extends Window {
	
	private AgentsGrid agentsGrid;
	private List<String> ontologyList;
	private TextField<String> agentNameField;
	private ComboBox<ElementModel> supportedOntCombo;
	
	public AddAgentWindow(AgentsGrid agentsGrid, List<String> ontologyList) {
		super();
		this.agentsGrid = agentsGrid;
		this.ontologyList = ontologyList;
		initSelectAgentWindow();
	}
	
	private void initSelectAgentWindow(){
		this.setPlain(true);
		this.setModal(true);
		this.setBlinkModal(true);
		this.setSize("400px", "200px");
		//window.setMaximizable(true);
		this.setHeading(FeedbackAuthoringStrings.ADD_UPC_LABEL + " " + FeedbackAuthoringStrings.AGENT_LABEL);
		
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
				String agentName = agentNameField.getValue();
				
				ElementModel value = (ElementModel) supportedOntCombo.getValue();
				String ontology = (String)value.get(GridElementLabel.NAME); 
				
				agentsGrid.saveAgent(agentName, ontology);
				
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
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(350);
		
		agentNameField = new TextField<String>();
		agentNameField.setFieldLabel(FeedbackAuthoringStrings.AGENT_NAME_LABEL);
		agentNameField.setAllowBlank(false);
	    formPanel.add(agentNameField);
	    
	    ListStore<ElementModel> ontologies = new ListStore<ElementModel>();
//	    ElementModel anyOnt = new ElementModel(FeedbackAuthoringStrings.ANY_LABEL, null);
//	    ontologies.add(anyOnt);
	    ontologies.add(Data2ModelConverter.getOntListAsModelList(ontologyList));  
	  
	    supportedOntCombo = new ComboBox<ElementModel>();
	    supportedOntCombo.setFieldLabel(FeedbackAuthoringStrings.SUPPORTED_ONTOLOGY_LABEL);
	    //comboSupportedOnto.setEmptyText("...");
	    supportedOntCombo.setDisplayField("name");  
	    supportedOntCombo.setWidth(150);  
	    supportedOntCombo.setStore(ontologies);  
	    supportedOntCombo.setTypeAhead(true);  
	    supportedOntCombo.setTriggerAction(TriggerAction.ALL);
	    //comboSupportedOnto.select(0); //selecting any by default
	    if(ontologies.getCount() > 0)
	    	supportedOntCombo.setValue(ontologies.getAt(0));
	    
	    formPanel.add(supportedOntCombo);
	    panel.add(formPanel);
		
		return panel;
	}

}
