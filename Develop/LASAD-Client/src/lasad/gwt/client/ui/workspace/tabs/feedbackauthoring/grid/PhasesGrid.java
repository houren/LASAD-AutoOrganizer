package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddPhaseWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseDef;

/**
 * Creates the pahses grid panel displayed in the general->phases tab.
 * @author Anahuac
 *
 */
public class PhasesGrid extends CustomizedGrid {
	
private AgentWindow agentWindow;
	
	
	public PhasesGrid(GridConf gridConf, AgentWindow agentWindow) {
		super(gridConf);
		this.agentWindow = agentWindow;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	    populateGrid();
	    //populateGridForTesting();
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
	void editElement(String id) {
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		PhaseDef phaseDef = new PhaseDef();
		phaseDef.setID(id);
		int index = phases.indexOf(phaseDef);
		phaseDef = phases.get(index);
		AddPhaseWindow win = new AddPhaseWindow(this, phaseDef.getID(), phaseDef.getName(), phaseDef.getDescription());
		win.show();
	}

	@Override
	void deleteElement(String id, String name) {
		PhaseDef phase = new PhaseDef();
		phase.setID(id);
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		phases.remove(phase);

		//remove from panel
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, id);
		deleteElementFromStore(m);
	}

	@Override
	void addElement() {
		AddPhaseWindow win = new AddPhaseWindow(this);
		win.show();
	}
	
	public void updateElement(String phaseId, String phaseName, String phaseDesc) {
		ElementModel element = new ElementModel();
		element.set(GridElementLabel.ID, phaseId);
		element.set(GridElementLabel.NAME, phaseName);
		element.set(GridElementLabel.DESC, phaseDesc);
		updateElementFromStore(element);
	}
	
	public void saveElement(String phaseName, String phaseDesc) {
		String phaseId = IdGenerator.getNewPhaseId();
		ElementModel element = new ElementModel();
		element.set(GridElementLabel.ID, phaseId);
		element.set(GridElementLabel.NAME, phaseName);
		element.set(GridElementLabel.DESC, phaseDesc);
		addElement2GridStore(element);
		
		PhaseDef phase = new PhaseDef();
		phase.setID(phaseId);
		phase.setName(phaseName);
		phase.setDescription(phaseDesc);
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		phases.add(phase);
		
	}
	
	/*
	 * populates the grid with the current phases
	 */
	void populateGrid() {
		List<PhaseDef> phases = agentWindow.getAgentInfo().getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
		if(phases != null){
			for(PhaseDef phase: phases){
				ElementModel element = new ElementModel();
				element.set(GridElementLabel.ID, phase.getID());
				element.set(GridElementLabel.NAME, phase.getName());
				element.set(GridElementLabel.DESC, phase.getDescription());
				addElement2GridStore(element);
			}
		}
	}

	@Override
	void populateGridForTesting() {
		// TODO Auto-generated method stub

	}

	@Override
	void selectedRowEvent(ElementModel selectedRow) {
	}

}
