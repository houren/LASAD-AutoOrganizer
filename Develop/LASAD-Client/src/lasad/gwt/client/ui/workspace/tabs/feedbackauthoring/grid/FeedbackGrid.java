package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation.CheckDependencies;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddFeedbackWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.PriorityDef;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_Highlighting;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_LongText;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_ShortText;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_User;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Creates the feeedbakc messages grid panel displayed in the messages tab.
 * @author Anahuac
 *
 */
public class FeedbackGrid extends CustomizedGrid {
	
	private AgentWindow agentWindow;
	
	public FeedbackGrid(GridConf gridConf, AgentWindow agentWindow) {
		super(gridConf);
		this.agentWindow = agentWindow;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
//	    populateGridForTesting();
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
		FATDebug.print(FATDebug.DEBUG, "[Feedbackgrid][deleteElement] -id:" + id); 
		final String agentId = agentWindow.getAgentInfo().getAgentID();
		ActionType feedback = agentWindow.getAgentInfo().getConfData().getActionType(new ServiceID(agentId, id, ServiceClass.ACTION));
		
		StringBuilder msg = new StringBuilder();
		msg.append(FeedbackAuthoringStrings.DELETE_LABEL + " " + feedback.getName() 
					+ FeedbackAuthoringStrings.QUESTION_MARK);
		String retVal = CheckDependencies.checkFeedbackMessageDependencies(agentWindow.getAgentInfo(), id);
		if(retVal != null && !retVal.equals("")){
			msg.append("<br><br>" + FeedbackAuthoringStrings.THIS_ACTION_WARN_LABEL + "<br>");
			msg.append(retVal);
		}
		
		MessageBox box = new MessageBox();
  	  	box.setButtons(MessageBox.YESNO);
  	  	box.setIcon(MessageBox.QUESTION);
  	  	box.setTitle(FeedbackAuthoringStrings.DELETE_MESSAGE_LABEL);
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
	
	private void doDelete(String agentId, String feedbackId){
		//remove from agent Feedback Messages
		agentWindow.getAgentInfo().getConfData().removeActionType((new ServiceID(agentId, feedbackId, ServiceClass.ACTION)));
		//remove from Strategies
		ServiceID msgServId = new ServiceID(agentWindow.getAgentInfo().getAgentID(), feedbackId, ServiceClass.ACTION);
		Collection<ProvisionType> elemList = agentWindow.getAgentInfo().getConfData().getProvisionTypes();
		for(ProvisionType elem:elemList){
			if(elem.getProvidedActions() != null){
				List<ServiceID> dependencyList = new Vector<ServiceID>();
				for(ServiceID action : elem.getProvidedActions().getServiceIDs()){
					if(action.equals(msgServId)){
						dependencyList.add(action);
						break; //one feedback message can only be 0 or 1 time in a given provision
					}
				}
				elem.getProvidedActions().getServiceIDs().removeAll(dependencyList);
			}
		}
		
		//remove from grid panel
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, feedbackId);
		deleteElementFromStore(m);
		
		agentWindow.resetFeedbackBottomPanel();
	}

	@Override
	void duplicateElement(String id) {
	}

	@Override
	void addElement() {
		List<String> optionList = new Vector<String>();
		optionList.add(FeedbackAuthoringStrings.STD_MSG_LABEL);
		AddFeedbackWindow window = new AddFeedbackWindow(FeedbackAuthoringStrings.ADD_MESSAGE_LABEL, optionList, this);
		window.show();
	}

	@Override
	void populateGridForTesting() {
		addElement2Grid("1", "feedback_1", FeedbackAuthoringStrings.STD_MSG_LABEL, "");
		addElement2Grid("2", "feedback_2", FeedbackAuthoringStrings.STD_MSG_LABEL, "");
		addElement2Grid("3", "feedback_3", FeedbackAuthoringStrings.STD_MSG_LABEL, "");
		
	}

	@Override
	void selectedRowEvent(ElementModel selectedRow) {
		String elemId = selectedRow.get(GridElementLabel.ID);
		FATDebug.print(FATDebug.DEBUG, "[Feedbackgrid][selectedRowEvent] -id:" + elemId);
		agentWindow.updateFeedbackBottomPanel(elemId);
	}
	
	public void handleAddFeedback(String feedbackName, String feedbackType){
		String id = IdGenerator.getNewFeedbackId();
		//add element to grid
		addElement2Grid(id, feedbackName, FeedbackAuthoringStrings.STD_MSG_LABEL, "");
		String agentId = agentWindow.getAgentInfo().getAgentID();
		//add element to structure
		addElement(agentId, id, feedbackName, FeedbackAuthoringStrings.STD_MSG_LABEL);
		
		//select and display the pattern that has being created
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, id);
		selectElementRow(m);
	}
	
	public void update(String id, String name, String type, String info){

		ElementModel element = new ElementModel();
		element.set(GridElementLabel.ID, id);
		element.set(GridElementLabel.NAME, name);
		element.set(GridElementLabel.TYPE, type);
		element.set(GridElementLabel.INFO, info);
		updateElementFromStore(element);
		
	}
	
	public void addElement2Grid(String id, String name, String type, String userSpecific){
		ElementModel patternStub = new ElementModel();
		patternStub.set(GridElementLabel.ID, id);
		patternStub.set(GridElementLabel.NAME, name);
		patternStub.set(GridElementLabel.TYPE, type);
		patternStub.set(GridElementLabel.INFO, userSpecific);
		addElement2GridStore(patternStub);
	}
	
	private void addElement(String agentId, String elemID, String elemName, String elemType){
//		Collection<ActionType> fbList = agentWindow.getAgentInfo().getConfData().getActionTypes();
		
		ServiceID fbID = new ServiceID(agentId, elemID, ServiceClass.ACTION);
		FeedbackActionType feedback = new FeedbackActionType(fbID, null);
		//basic
		feedback.setName(elemName);
		//Content
		//Short msg
		feedback.getMsgCompDefs().add(new MsgCompDef_ShortText(""));
		//Long msg
		feedback.getMsgCompDefs().add(new MsgCompDef_LongText(""));
		//Highlighting
		feedback.getMsgCompDefs().add(new MsgCompDef_Highlighting());
		
		//Priority
		feedback.setPriorityDef(new PriorityDef());
		
		List<ActionType> list = new Vector<ActionType>();
		list.add(feedback);
		agentWindow.getAgentInfo().getConfData().addActionTypes(list);
	}

	@Override
	void populateGrid() {
		Collection<ActionType> fbList = agentWindow.getAgentInfo().getConfData().getActionTypes();
		for(ActionType fb:fbList){
			FeedbackActionType newfb = (FeedbackActionType)fb;
			
//			String agentId = agentWindow.getAgentInfo().getAgentID();
//			new ServiceID(agentId, newfb.getTriggerID(), ServiceClass.ANALYSIS);
			
			addElement2Grid(newfb.getServiceID().getTypeID(), newfb.getName(), FeedbackAuthoringStrings.STD_MSG_LABEL, isUserSpecific(newfb)? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "");
		}
	}
	
	private boolean isUserSpecific(FeedbackActionType fb){
		AnalysisType pattern = agentWindow.getAgentInfo().getConfData().getAnalysisType(fb.getTriggerID());
		//String userSpecific = "";
		if(pattern instanceof StructureAnalysisType){
			for(PatternFilterDef patDef : ((StructureAnalysisType)pattern).getFilterDefs()){
				if(patDef instanceof PatternFilterDef_User){
					//userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
					return ((PatternFilterDef_User)patDef).isUserSpecific();
				}
			}
		} else if(pattern instanceof CounterAnalysisType){
			for(PatternFilterDef patDef : ((CounterAnalysisType)pattern).getFilterDefs()){
				if(patDef instanceof PatternFilterDef_User){
//					userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
					return ((PatternFilterDef_User)patDef).isUserSpecific();
				}
			}
		}
		return false;
	}
	
}
