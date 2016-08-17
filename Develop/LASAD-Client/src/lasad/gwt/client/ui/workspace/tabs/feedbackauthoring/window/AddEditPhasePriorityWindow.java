package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PhasePriorityGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Add Edit Phase Priority window
 * @author Anahuac
 *
 */
public class AddEditPhasePriorityWindow {

	
	private final Window window = new Window();
	private ComboBox<ElementModel> combo;
	private Map<String,String> comboOptions;
	private SliderField slider = null;
	private PhasePriorityGrid phasePriorityGridRef;
	
	private String currentIdValue;
	private String currentSliderValue;
	private int mode = FATConstants.ADD_TYPE;
	
	public AddEditPhasePriorityWindow(Map<String,String> comboOptions, PhasePriorityGrid phasePriorityGridRef){
		mode = FATConstants.ADD_TYPE;
		this.comboOptions = comboOptions;
		this.phasePriorityGridRef = phasePriorityGridRef;
		initSelectAgentWindow();
	}
	
	public AddEditPhasePriorityWindow(Map<String,String> comboOptions, String currentIdValue, String currentSliderValue, PhasePriorityGrid phasePriorityGridRef){
		mode = FATConstants.EDIT_TYPE;
		this.comboOptions = comboOptions;
		this.currentIdValue = currentIdValue;
		this.currentSliderValue = currentSliderValue;
		this.phasePriorityGridRef = phasePriorityGridRef;
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
		window.setHeading(FeedbackAuthoringStrings.HEADING_ADD_PHASE_PRIORITY);
		
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
				ElementModel value = (ElementModel) combo.getValue();
				if(value != null){
					String elementId = value.getValue(GridElementLabel.ID);
					String elementName = value.getName();
					Object sliderValue = slider.getValue();
					int val = 0;
					if(sliderValue instanceof String)
							val = Integer.valueOf((String)sliderValue);
					
					doAction(elementId, elementName, val);
					window.hide();
				}
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
	
	private void doAction(String elementId, String elementName, int sliderValue){
		if(mode == FATConstants.ADD_TYPE){
			phasePriorityGridRef.handleAddPhasePriority(elementId, elementName, sliderValue);
		} else{
			phasePriorityGridRef.handleEditPhasePriority(elementId, elementName, sliderValue);
		}
	}
	
	private ContentPanel getPanelContent(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(350);
		
		ListStore<ElementModel> comboStore = new ListStore<ElementModel>();
		comboStore.add(Data2ModelConverter.getMapAsModelList(comboOptions));
	  
	    combo = new ComboBox<ElementModel>();
	    combo.setFieldLabel(FeedbackAuthoringStrings.PHASE_LABEL);
	    combo.setDisplayField(GridElementLabel.NAME);  
	    combo.setWidth(150);  
	    combo.setStore(comboStore);
	    combo.setForceSelection(true);
	    combo.setTriggerAction(TriggerAction.ALL);
	    fPanel.add(combo);
	    
	    //set default value
	    if(currentIdValue != null){
		    Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, currentIdValue);
	    	ElementModel value = new ElementModel(map);
	    	combo.setValue(value);
	    } else if(comboStore.getCount() > 0){
	    	combo.setValue(comboStore.getAt(0));
	    }
	    
	    Slider phasePrioritySlider = new Slider();
		phasePrioritySlider.setMinValue(FATConstants.SLIDER_MIN_VAL);
		phasePrioritySlider.setMaxValue(FATConstants.SLIDER_MAX_VAL);
		phasePrioritySlider.setValue(FATConstants.SLIDER_MIN_VAL);
		phasePrioritySlider.setIncrement(FATConstants.SLIDER_INC_VAL);
		phasePrioritySlider.setMessage(FeedbackAuthoringStrings.PRIORITY_LABEL + " {0}");
	    
		slider = new SliderField(phasePrioritySlider);
		slider.setFieldLabel(FeedbackAuthoringStrings.PRIORITY_LABEL);
		slider.setValue(0);
		
		if(currentSliderValue != null){
	    	slider.setValue(Integer.valueOf(currentSliderValue));
	    }
		
		fPanel.add(slider);
		if(comboStore.getCount() == 0){
			Label msg = new Label();
			msg.setText(FeedbackAuthoringStrings.NO_PHASEs_DEF_FOR_THIS_AGENT);
			msg.setStyleAttribute("color", "#FF0000");
			fPanel.add(msg);
		}
	    
	    panel.add(fPanel);
	    return panel;
	}

	protected PhasePriorityGrid getPhasePriorityGridRef() {
		return phasePriorityGridRef;
	}
}
