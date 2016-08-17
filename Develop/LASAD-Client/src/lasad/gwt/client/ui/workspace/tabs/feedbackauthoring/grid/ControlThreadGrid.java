package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddControlThreadWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.common.ActionListDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_OnRequest;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Group;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Individuals;
import lasad.shared.dfki.meta.agents.provision.priority.PriorityProvisionType;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Creates the strategies grid panel displayed in the strategies tab.
 * @author Anahuac
 *
 */
public class ControlThreadGrid extends CustomizedGrid {
	private AgentWindow agentWindow;
	
	
	public ControlThreadGrid(GridConf gridConf, AgentWindow agentWindow) {
		super(gridConf);
		this.agentWindow = agentWindow;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	    //populateGridForTesting();
	    populateGrid();
	}
	
	@Override
	void markReadyElement(String id) {
	}

	@Override
	void viewElement(String id) {
	}

	@Override
	void editElement(String id) {
	}

	@Override
	void deleteElement(final String id, String name) {
		final String agentId = agentWindow.getAgentInfo().getAgentID();
		ProvisionType strategy = agentWindow.getAgentInfo().getConfData().getProvisionTypes((new ServiceID(agentId, id, ServiceClass.PROVISION)));
		
		StringBuilder msg = new StringBuilder();
		msg.append(FeedbackAuthoringStrings.DELETE_LABEL + " " + strategy.getName() 
					+ FeedbackAuthoringStrings.QUESTION_MARK);
		
		MessageBox box = new MessageBox();
  	  	box.setButtons(MessageBox.YESNO);
  	  	box.setIcon(MessageBox.QUESTION);
  	  	box.setTitle(FeedbackAuthoringStrings.DELETE_PRIORITY_LABEL);
  	  	box.setMessage(msg.toString());
  	  	box.addCallback(new Listener<MessageBoxEvent>() {
  	  		public void handleEvent(MessageBoxEvent be) {
  	  			if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
  	  				doDelete(agentId, id);
  	  			}
  	  		}
  	    });
  	    box.show();
	}
	
	private void doDelete(String agentId, String strategyId){
		//remove strategy from the agent
		agentWindow.getAgentInfo().getConfData().removeProvisionType((new ServiceID(agentId, strategyId, ServiceClass.PROVISION)));
		//remove from panel
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, strategyId);
		ElementModel m = new ElementModel(map);
		deleteElementFromStore(m);
		agentWindow.resetStrategyBottomPanel();
	}

	@Override
	void duplicateElement(String id) {

	}

	@Override
	void addElement() {
		List<String> optionList = new Vector<String>();
		optionList.add(FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL);
		optionList.add(FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL);
		AddControlThreadWindow window = new AddControlThreadWindow(FeedbackAuthoringStrings.ADD_STRATEGY_LABEL, optionList, this);
		window.show();
	}

	@Override
	void populateGridForTesting() {
		addElement2Grid("1", "test_provision1",FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL, FeedbackAuthoringStrings.ON_REQUEST_LABEL);
		addElement2Grid("2", "test_provision2",FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL, FeedbackAuthoringStrings.PERIODICALLY_LABEL);
		addElement2Grid("3", "test_provision3",FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL, FeedbackAuthoringStrings.ON_REQUEST_LABEL);
	}

	@Override
	void selectedRowEvent(ElementModel selectedRow) {
		String elemId = selectedRow.get(GridElementLabel.ID);
		agentWindow.updateStrategyBottomPanel(elemId);
		
	}
	
	public void handleAddElement(String elemName, String elemType){
		String elemId = IdGenerator.getNewProvisionId();
		//add element to grid
		addElement2Grid(elemId, elemName, elemType, FeedbackAuthoringStrings.ON_REQUEST_LABEL); //by default on request.
		String agentId = agentWindow.getAgentInfo().getAgentID();
		//add element to structure
		addElement(agentId, elemId, elemName, elemType);
		
		//select and display the pattern that has being created
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, elemId);
		selectElementRow(m);
	}
	
	/*
	 * Adds the element to the grid on the GUI
	 */
	public void addElement2Grid(String elemId, String elemName, String elemType, String provTime){
		ElementModel patternStub = new ElementModel();
		patternStub.set(GridElementLabel.ID, elemId);
		patternStub.set(GridElementLabel.NAME, elemName);
		patternStub.set(GridElementLabel.TYPE, elemType);
		patternStub.set(GridElementLabel.INFO, provTime);
		addElement2GridStore(patternStub);
	}
	
	/*
	 * Adds the element to the internal data-structure
	 */
	private void addElement(String agentId, String elemID, String elemName, String elemType){
		
		ServiceID provId = new ServiceID(agentId, elemID, ServiceClass.PROVISION);
		PriorityProvisionType provision = new PriorityProvisionType(provId);
		provision.setName(elemName);
		provision.setDescription("");
		provision.setProvisionTime(new ProvisionTimeDef_OnRequest(FeedbackAuthoringStrings.GET_HINT_LABEL)); //by default on request.
		if(FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL.equals(elemType)){
			provision.setRecipient(new RecipientDef_Group());
		} else if(FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL.equals(elemType)){
			provision.setRecipient(new RecipientDef_Individuals());
		}
		provision.setProvidedActions(new ActionListDef(new Vector<ServiceID>()));

		List<ProvisionType> list = new Vector<ProvisionType>();
		list.add(provision);
		agentWindow.getAgentInfo().getConfData().addProvisionTypes(list);
	}
	
	public void update(String id, String name, String type, String provTime){

		ElementModel element = new ElementModel();
		element.set(GridElementLabel.ID, id);
		element.set(GridElementLabel.NAME, name);
		element.set(GridElementLabel.TYPE, type);
		element.set(GridElementLabel.INFO, provTime);
		updateElementFromStore(element);
		
	}

	@Override
	void populateGrid() {
		Collection<ProvisionType> elemList = agentWindow.getAgentInfo().getConfData().getProvisionTypes();
		for(ProvisionType elem:elemList){
			String elemType = elem.getRecipient() instanceof RecipientDef_Group ? FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL : FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL;
			String provTime = elem.getProvisionTime() instanceof ProvisionTimeDef_OnRequest ? FeedbackAuthoringStrings.ON_REQUEST_LABEL : FeedbackAuthoringStrings.PERIODICALLY_LABEL;
			addElement2Grid(elem.getServiceID().getTypeID(), elem.getName(), elemType, provTime);
		}
	}

}
