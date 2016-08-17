package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.CustomDualListField;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;

/**
 * Add CountPattern Specific Types dialog
 * @author Anahuac
 *
 */
public class AddCountPatternSpecificTypesWindow {
	private final Window window = new Window();
	private CustomDualListField<ElementModel> dualList;
	private Map<String,String> dualListOptions;
	private Map<String,String> selectedDualListOptions;
	private AgentWindow agentWindow;
	private String patternId;
	private String instanceTypeGeneral;
	
	public AddCountPatternSpecificTypesWindow(Map<String, String> dualListOptions, Map<String, String> selectedDualListOptions, String patternId, String instanceTypeGeneral, AgentWindow agentWindow) {
		super();
		this.dualListOptions = dualListOptions;
		this.selectedDualListOptions = selectedDualListOptions;
		this.patternId = patternId;
		this.agentWindow = agentWindow;
		this.instanceTypeGeneral = instanceTypeGeneral;
		initWindow();
	}
	
	public void show(){
		window.show();
	}

	private void initWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("425px", "250px");
		//window.setMaximizable(true);
		window.setHeading(FeedbackAuthoringStrings.HEADING_ADD_SPECIFIC_TYPES);
		
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
				
				ListStore<ElementModel> tmpStore = dualList.getToList().getStore();
				Map<String, String> selectedOptions = new HashMap<String, String>();
				if(tmpStore.getModels() != null){
					for(ElementModel elem:tmpStore.getModels()){
						selectedOptions.put(elem.getValue(GridElementLabel.ID), elem.getName());
					}
					doAction(patternId, instanceTypeGeneral, selectedOptions);
				}
				//Info.display("Title", "message");
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
	
	public void doAction(String paternId, String instanceTypeGeneral, Map<String, String> selectedOptions){
		agentWindow.updateSpecificTypes(paternId, instanceTypeGeneral, selectedOptions);
	}
	
	private ContentPanel getPanelContent(){
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		
		Label availMsgTypesLabel = new Label(FeedbackAuthoringStrings.AVAILABLE_TYPES_LABEL);
		availMsgTypesLabel.setStyleAttribute("color", "#000000");
	    Label selMsgTypesLabel = new Label(FeedbackAuthoringStrings.SELECTED_TYPES_LABEL);
	    selMsgTypesLabel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td.setStyle("style='padding-left: 1px;'");
	    TableData td1 = new TableData();
	    td1.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td1.setStyle("style='padding-left: 120px;'");

	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setAutoWidth(true);
	    hp.setHeight(20);
	    hp.add(availMsgTypesLabel, td);
	    hp.add(new Label(""), td1);
	    hp.add(selMsgTypesLabel, td);
	    panel.add(hp, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
	    Vector<Integer> buttonsToRemoveList = new Vector<Integer>();
	    buttonsToRemoveList.add(CustomDualListField.ALL_LEFT_BUTTON);
	    buttonsToRemoveList.add(CustomDualListField.ALL_RIGHT_BUTTON);
		
		dualList = new CustomDualListField<ElementModel>(buttonsToRemoveList);
		dualList.setMode(CustomDualListField.Mode.INSERT);
		panel.add(dualList, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
		dualList.setWidth(400);
		
		Map<String, String> notSelectedDualListOptions = new HashMap<String, String>();
		
		for(String key:dualListOptions.keySet()){
        	if(!selectedDualListOptions.containsKey(key)){
        		notSelectedDualListOptions.put(key, dualListOptions.get(key));
        	}
        }
		

		ListStore<ElementModel> leftStore = new ListStore<ElementModel>();
		ListStore<ElementModel> rightStore = new ListStore<ElementModel>();

		ListField<ElementModel> leftList = dualList.getFromList();
		leftList.setStore(leftStore);
		leftList.setDisplayField(GridElementLabel.NAME);
		leftList.setWidth(165);
		leftStore.add(Data2ModelConverter.getMapAsModelList(notSelectedDualListOptions));

		ListField<ElementModel> rightList = dualList.getToList();
		rightList.setStore(rightStore);
		rightList.setDisplayField(GridElementLabel.NAME);
		rightList.setWidth(165);
		rightStore.add(Data2ModelConverter.getMapAsModelList(selectedDualListOptions));
		
		return panel;
	}
}
