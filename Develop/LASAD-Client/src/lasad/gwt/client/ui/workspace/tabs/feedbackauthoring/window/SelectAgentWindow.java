package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Map;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Customizable class to generate AddAgent2[Something] dialogs
 * @author Anahuac
 *
 */
public abstract class SelectAgentWindow {
	
	private final Window window = new Window();
	private ComboBox<ElementModel> comboAgents;
//	private FormData formData = new FormData("-20"); //100%
	private Map<String,String> agentMap;
	private String helperId;
	private String helperName;
	private FeedbackAuthoringTabContent faTabRef;
	
	public SelectAgentWindow(Map<String,String> agentMap, String helperId, String helperName, FeedbackAuthoringTabContent faTabRef){
		this.agentMap = agentMap;
		this.helperId = helperId;
		this.helperName = helperName; 
		this.faTabRef = faTabRef;
		initSelectAgentWindow();
		
	}
	
	public void show(){
		window.show();
	}

	private void initSelectAgentWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("400px", "200px");
		//window.setMaximizable(true);
		window.setHeading(FeedbackAuthoringStrings.SELECT_AGENT_LABEL);
		
		window.setLayout(new FitLayout());
		window.add(getPanelContent());
		window.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//TODO check if we need to do something else before closing
			}
		});
		window.layout();
		
		Button saveBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				ElementModel value = (ElementModel) comboAgents.getValue();
				String agentId = value.getValue(GridElementLabel.ID);
				String agentName = value.getName();
				
				doAction(agentId, agentName, helperId);
				window.hide();
		    }
		});
		window.addButton(saveBtn);
		
		Button cancelBtn = new Button(FeedbackAuthoringStrings.CANCEL_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				window.hide();  
			}  
		});
		window.addButton(cancelBtn);
	}
	
	public abstract void doAction(String agentId, String agentName, String helper);
	
	private ContentPanel getPanelContent(){
		ContentPanel panel = new ContentPanel();
//		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
//		fPanel.setWidth(450);
//		fPanel.setLabelWidth(100);
		
		Label infoLabel = new Label(FeedbackAuthoringStrings.SELECT_AGENT_THAT_LABEL + helperName);
		panel.add(infoLabel);
		
		ListStore<ElementModel> agentsStore = new ListStore<ElementModel>();
//		agentsStore.add(Data2ModelConverter.getStrAsModel(agentList));
		agentsStore.add(Data2ModelConverter.getMapAsModelList(agentMap));
	  
	    comboAgents = new ComboBox<ElementModel>();
	    comboAgents.setFieldLabel(FeedbackAuthoringStrings.AGENT_NAME_LABEL);
	    comboAgents.setDisplayField(GridElementLabel.NAME);  
	    comboAgents.setWidth(150);  
	    comboAgents.setStore(agentsStore);  
	    comboAgents.setTypeAhead(false);
	    comboAgents.setForceSelection(true);
	    comboAgents.setTriggerAction(TriggerAction.ALL);
		
	    fPanel.add(comboAgents);
	    panel.add(fPanel);
	    Label noteLabel = new Label(FeedbackAuthoringStrings.NOTE_SEL_AGENT_WIN_LABEL);
	    panel.add(noteLabel);
	    return panel;
	}

	protected FeedbackAuthoringTabContent getFATabRef() {
		return faTabRef;
	}
}
