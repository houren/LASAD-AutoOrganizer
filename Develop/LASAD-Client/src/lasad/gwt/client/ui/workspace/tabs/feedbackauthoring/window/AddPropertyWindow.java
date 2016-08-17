package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Map;

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
 * Add property window
 * @author Anahuac
 *
 */
public class AddPropertyWindow {
	
	private final Window window = new Window();
	private ComboBox<ElementModel> optionsCombo;
	private Map<String,String> optionsMap;
	private ElementConstraintsWindow parentRef;
	
	public AddPropertyWindow(Map<String,String> optionsMap, ElementConstraintsWindow parentRef){
		this.optionsMap = optionsMap;
		this.parentRef = parentRef;
		initSelectAgentWindow();
	}
	
	public void show(){
		window.show();
	}

	private void initSelectAgentWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("350px", "200px");
		//window.setMaximizable(true);
		window.setHeading(FeedbackAuthoringStrings.ADD_PROPERTY_LABEL);
		
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
				ElementModel value = (ElementModel) optionsCombo.getValue();
				String optionId = value.getValue(GridElementLabel.ID);
				String optionName = value.getName();
				//FATDebug.print(FATDebug.WAR, "Selected element:" + agentName + " id:" + agentId);
				//Info.display("Selected element", optionName + " id:" + optionId);
				
				doAction(optionId, optionName);
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
	
	public void doAction(String optionId, String optionName){
		parentRef.addParent(optionId, optionName);
	}
	
	private ContentPanel getPanelContent(){
		ContentPanel panel = new ContentPanel();
//		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(400);
//		fPanel.setLabelWidth(100);
		
		Label infoLabel = new Label(FeedbackAuthoringStrings.SELECT_A_PROPERTY_LABEL);
		panel.add(infoLabel);
		
		ListStore<ElementModel> optionsStore = new ListStore<ElementModel>();
		optionsStore.add(Data2ModelConverter.getMapAsModelList(optionsMap));
	  
	    optionsCombo = new ComboBox<ElementModel>();
	    optionsCombo.setFieldLabel(FeedbackAuthoringStrings.PROPERTY_LABEL);
	    optionsCombo.setDisplayField(GridElementLabel.NAME);  
	    optionsCombo.setWidth(150);  
	    optionsCombo.setStore(optionsStore);  
	    optionsCombo.setTypeAhead(true);  
	    optionsCombo.setTriggerAction(TriggerAction.ALL);
	    if(optionsStore.getCount() > 0)
	    	optionsCombo.setValue(optionsStore.getAt(0));
		
	    fPanel.add(optionsCombo);
	    panel.add(fPanel);
	    return panel;
	}

	protected ElementConstraintsWindow getParentRef() {
		return parentRef;
	}

}
