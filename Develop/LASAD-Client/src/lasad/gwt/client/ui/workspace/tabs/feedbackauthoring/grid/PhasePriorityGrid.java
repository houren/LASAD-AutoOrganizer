package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddEditPhasePriorityWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseDef;

/**
 * Creates the phase priority panel grid panel displayed in the messages tab.
 * @author Anahuac
 *
 */
public class PhasePriorityGrid extends CustomizedGrid  {

	private AgentWindow agentWindow;
	private ServiceID feedbackServiceID;
	
	public PhasePriorityGrid(GridConf gridConf, AgentWindow agentWindow, ServiceID serviceID) {
		super(gridConf);
		this.agentWindow = agentWindow;
		this.feedbackServiceID = serviceID;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	    populateGrid();
//	    populateGridForTesting();
	}
	
	@Override
	void markReadyElement(String id) {
	}

	@Override
	void viewElement(String id) {
	}
	@Override
	void duplicateElement(String id) {
	}
	@Override
	void selectedRowEvent(ElementModel selectedRow) {	
	}

	@Override
	void deleteElement(String id, String name) {
		FeedbackActionType fa = (FeedbackActionType) agentWindow.getAgentInfo().getConfData().getActionType(feedbackServiceID);
		fa.getPriorityDef().removePhasePriority(id);

		//remove from panel
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, id);
		deleteElementFromStore(m);
	}
	
	@Override
	void editElement(String id) {
		HashMap<String, String> optionList = new HashMap<String, String>();
		
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		if(phases != null){
			for(PhaseDef phase: phases){
				optionList.put(phase.getID(), phase.getName());
			}
		}
		String currentIdValue = id;
		String currentSliderValue = optionList.get(currentIdValue);
		AddEditPhasePriorityWindow window = new AddEditPhasePriorityWindow(optionList, currentIdValue, currentSliderValue , this);
		window.show();
	}

	@Override
	void addElement() {
		
		FeedbackActionType fa = (FeedbackActionType) agentWindow.getAgentInfo().getConfData().getActionType(feedbackServiceID);
		Map<String, Integer> phase2PriorityMap = fa.getPriorityDef().getPhase2Priority();
		
		HashMap<String, String> optionList = new HashMap<String, String>();
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		if(phases != null){
			for(PhaseDef phase: phases){
				if(phase2PriorityMap != null && phase2PriorityMap.containsKey(phase.getID())){
					//this is done to skip the phases that have already a priority defined. 
					continue;
				}
				optionList.put(phase.getID(), phase.getName());
			}
		}
		AddEditPhasePriorityWindow window = new AddEditPhasePriorityWindow(optionList, this);
		window.show();
	}
	
	
	/*
	 * elementId = phaseId
	 * elementName = phaseName
	 */
	public void handleEditPhasePriority(String elementId, String elementName, int sliderValue){
		ElementModel elementStub = new ElementModel();
		elementStub.set(GridElementLabel.ID, elementId);
		elementStub.set(GridElementLabel.NAME, elementName);
		elementStub.set(GridElementLabel.PRIORITY, sliderValue);
		updateElementFromStore(elementStub);
		
		FeedbackActionType fa = (FeedbackActionType) agentWindow.getAgentInfo().getConfData().getActionType(feedbackServiceID);
		fa.getPriorityDef().editPhasePriority(elementId, sliderValue);
	}
	
	public void handleAddPhasePriority(String elementId, String elementName, int sliderValue){
		addPhasePriority2Grid(elementId, elementName, sliderValue);
		
		FeedbackActionType fa = (FeedbackActionType) agentWindow.getAgentInfo().getConfData().getActionType(feedbackServiceID);
		fa.getPriorityDef().addPhasePriority(elementId, sliderValue);
		
	}
	
	public void addPhasePriority2Grid(String elementId, String elementName, int sliderValue){
		ElementModel elementStub = new ElementModel();
		elementStub.set(GridElementLabel.ID, elementId);
		elementStub.set(GridElementLabel.NAME, elementName);
		elementStub.set(GridElementLabel.PRIORITY, String.valueOf(sliderValue));
		addElement2GridStore(elementStub);
	}
	

	@Override
	void populateGridForTesting() {
		addPhasePriority2Grid("egal1", "egal1", 1);
		addPhasePriority2Grid("egal2", "egal2", 2);
	}

	@Override
	void populateGrid() {
		FeedbackActionType fa = (FeedbackActionType) agentWindow.getAgentInfo().getConfData().getActionType(feedbackServiceID);
		Map<String, Integer> phase2PriorityMap = fa.getPriorityDef().getPhase2Priority();
		Set<String> phasesIdList = phase2PriorityMap.keySet(); //phase-priority mapping
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases(); //phases list
		if(phases != null){
			for(String id: phasesIdList){
				PhaseDef tmp = new PhaseDef();
				tmp.setID(id);
				int index = phases.indexOf(tmp);
				if(index >= 0){
					PhaseDef phaseDef = phases.get(index);
					addPhasePriority2Grid(id, phaseDef.getName(), phase2PriorityMap.get(id));
				}
			}
		}
	}

}
