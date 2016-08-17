package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation.CheckDependencies;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddPatternWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTime;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTimeSetting;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_User;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_UserSetting;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterCriterionDef;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterDef;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeGeneral;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific;
import lasad.shared.dfki.meta.agents.analysis.counter.UserRoleSetting;
import lasad.shared.dfki.meta.agents.analysis.counter.UserSelectionSetting;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Creates the patterns grid panel displayed in the patterns tab.
 * @author Anahuac
 *
 */
public class PatternsGrid extends CustomizedGrid {

	private AgentWindow agentWindow;
	
	
	public PatternsGrid(GridConf gridConf, AgentWindow agentWindow) {
		super(gridConf);
		this.agentWindow = agentWindow;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	    //populateGridForTesting();
	    populateGrid();
	}

	@Override
	void viewElement(String id) {
		// TODO Auto-generated method stub
	}

	@Override
	void editElement(String id) {
		// TODO Auto-generated method stub
	}
	
	public void update(String id, String name, String type, String info){

		ElementModel element = new ElementModel();
		element.set(GridElementLabel.ID, id);
		element.set(GridElementLabel.NAME, name);
		element.set(GridElementLabel.TYPE, type);
		element.set(GridElementLabel.INFO, info);
		updateElementFromStore(element);
		
	}

	@Override
	void deleteElement(final String id, String name) {
		final String agentId = agentWindow.getAgentInfo().getAgentID();
		ServiceID servPatternId = new ServiceID(agentId, id, ServiceClass.ANALYSIS);
		final AnalysisType pattern = agentWindow.getAgentInfo().getConfData().getAnalysisType(servPatternId);
		
		StringBuilder msg = new StringBuilder();
		msg.append(FeedbackAuthoringStrings.DELETE_LABEL + " " + pattern.getName() 
					+ FeedbackAuthoringStrings.QUESTION_MARK);
		String retVal = CheckDependencies.checkPatternDependencies(agentWindow.getAgentInfo(), id);
		if(retVal != null && !retVal.equals("")){
			msg.append("<br><br>" + FeedbackAuthoringStrings.THIS_ACTION_WARN_LABEL + "<br>");
			msg.append(retVal);
		}
		
		MessageBox box = new MessageBox();
  	  	box.setButtons(MessageBox.YESNO);
  	  	box.setIcon(MessageBox.QUESTION);
  	  	box.setTitle(FeedbackAuthoringStrings.DELETE_PATTERN_LABEL);
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
	
	private void doDelete(String agentId, String patternId){
		//remove from server manager. 
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().deletePattern(agentId, patternId);
		//remove pattern manipulator
		ServiceID servPatternId = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
		final AnalysisType pattern = agentWindow.getAgentInfo().getConfData().getAnalysisType(servPatternId);
		if(pattern instanceof StructureAnalysisType){
			//remove from StructuralAnalysisTypeManipulator
			FeedbackAuthoringTabContent.getInstance().deletePattternManipulator(servPatternId);
		} else if(pattern instanceof CounterAnalysisType){
			//do nothing
		}
		//remove from agent structure
		agentWindow.getAgentInfo().getConfData().removeAnalysisType(servPatternId);
		//remove from feedback messages
		//List<ServiceID> dependencyList = new Vector<ServiceID>();
		Collection<ActionType> fbList = agentWindow.getAgentInfo().getConfData().getActionTypes();
		for(ActionType fb:fbList){
			FeedbackActionType newfb = (FeedbackActionType)fb;
			if(newfb.getTriggerID() != null && newfb.getTriggerID().equals(servPatternId)){
				//dependencyList.add(newfb.getServiceID());
				newfb.setTriggerID(null);
				//remove message linked to patternId from strategies
				Collection<ProvisionType> elemList = agentWindow.getAgentInfo().getConfData().getProvisionTypes();
				for(ProvisionType elem:elemList){
					if(elem.getProvidedActions() != null){
						elem.getProvidedActions().getServiceIDs().remove(newfb.getServiceID());
					}
				}
				
			}
		}
//		//remove from feedback messages
//		for(ServiceID fmId : dependencyList){
//			agentWindow.getAgentInfo().getConfData().removeActionType(fmId);
//		}
		
		//remove from grid panel
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, patternId);
		deleteElementFromStore(m);
		agentWindow.resetPatternBottomPanel();
	}

	@Override
	void duplicateElement(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void addElement() {
		List<String> optionList = new Vector<String>();
		optionList.add(FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL);
		optionList.add(FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL);
		AddPatternWindow window = new AddPatternWindow(FeedbackAuthoringStrings.ADD_PATTERN_LABEL, optionList, this);
		window.show();
	}

	@Override
	void markReadyElement(String name) {
	}
	
	@Override
	void populateGridForTesting() {
	}
	
	public void addPattern2Grid(String patternId, String patternName, String patternType, String patternInfo){
		ElementModel patternStub = new ElementModel();
		patternStub.set(GridElementLabel.ID, patternId);
		patternStub.set(GridElementLabel.NAME, patternName);
		patternStub.set(GridElementLabel.TYPE, patternType);
		patternStub.set(GridElementLabel.INFO, patternInfo);
		addElement2GridStore(patternStub);
	}
	
	public void addPattern(String agentId, String patternId, String patternName, String patternType){
		//add pattern to Patterns grid
		addPattern2Grid(patternId, patternName, patternType, "");//PatternFilterDef_UserSetting.NONE.toString()
		//add pattern to PatternServerManager
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(agentId, patternId);
		if(FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL.equals(patternType)){
			ServiceID id = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
			StructureAnalysisType pattern = new StructureAnalysisType(id, new StructuralPattern(patternId));
			pattern.setName(patternName);
			
			AgentDescriptionFE agent = agentWindow.getAgentInfo(); 
			SupportedOntologiesDef supOnt = agent.getSupportedOntology();
			List<String> ontList = supOnt.getSupportedOntologies();
			if(ontList != null && ontList.size()>0){
				pattern.setOntologyID(ontList.get(0));
			}
			//save pattern in agent
			agent.getConfData().addAnalysisType(pattern);
			
			//Setting default user restriction
			PatternFilterDef_User userRest = new PatternFilterDef_User(PatternFilterDef_UserSetting.NONE);
			pattern.getFilterDefs().add(userRest);
			//Setting default Time restriction
			PatternFilterDef_LastModTime timeRest = new PatternFilterDef_LastModTime(PatternFilterDef_LastModTimeSetting.NONE, 0);
			pattern.getFilterDefs().add(timeRest);
			
			//create & store the StructuralAnalysisTypeManipulator
			FeedbackAuthoringTabContent.getInstance().addPatternManipulator(pattern);
			
		} else if(FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL.equals(patternType)){
			ServiceID id = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
			//TODO use a constructor that receives the pattern name only
			CounterDef counterDef = new CounterDef(UserSelectionSetting.ONE, "", UserRoleSetting.NONE, InstanceTypeGeneral.NODE, new Vector<InstanceTypeSpecific>(), Integer.valueOf(0), Integer.valueOf(0));
//			CounterDef counterDef = new CounterDef(InstanceTypeGeneral.NODE, null); // null == ANY type of nodes
			CounterAnalysisType pattern = new CounterAnalysisType(id, counterDef, new Vector<CounterCriterionDef>());
			pattern.setName(patternName);
			
			AgentDescriptionFE agent = agentWindow.getAgentInfo();
			//save pattern in agent
			agent.getConfData().addAnalysisType(pattern);
			
			//Setting default user restriction
			PatternFilterDef_User userRest = new PatternFilterDef_User(PatternFilterDef_UserSetting.NONE);
			pattern.getFilterDefs().add(userRest);
			//Setting default Time restriction
			PatternFilterDef_LastModTime timeRest = new PatternFilterDef_LastModTime(PatternFilterDef_LastModTimeSetting.NONE, 0);
			pattern.getFilterDefs().add(timeRest);
		}
		
		//select and display the pattern that has being created
		ElementModel m = new ElementModel();
		m.set(GridElementLabel.ID, patternId);
		selectElementRow(m);
	}
	
	public void handleAddPattern(String patternName, String patternType){
		FeedbackAuthoringTabContent.getInstance().doGetFreshPatternId(patternName, patternType, agentWindow.getAgentInfo().getAgentID(), this);
	}

	@Override
	void selectedRowEvent(ElementModel selectedRow) {
		String patternId = selectedRow.get(GridElementLabel.ID);
		agentWindow.updatePatternBottomPanel(patternId);
	}

	@Override
	void populateGrid() {
		Collection<AnalysisType> patternList = agentWindow.getAgentInfo().getConfData().getAnalysisTypes();
		for(AnalysisType pattern:patternList){
			if(pattern instanceof StructureAnalysisType){
				String userSpecific = "";
				for(PatternFilterDef patDef : pattern.getFilterDefs()){
					if(patDef instanceof PatternFilterDef_User){
						userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
						break;
					}
				}
				addPattern2Grid(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL, userSpecific);
				FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(agentWindow.getAgentInfo().getAgentID(), pattern.getServiceID().getTypeID());
			} else if(pattern instanceof CounterAnalysisType){
				//patternsGrid.addPattern(pattern.getServiceID().getAgentID(), pattern.getServiceID().getTypeID(), FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL);
				String userSpecific = "";
				for(PatternFilterDef patDef : pattern.getFilterDefs()){
					if(patDef instanceof PatternFilterDef_User){
						userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
						break;
					}
				}
				addPattern2Grid(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL, userSpecific);
				FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(agentWindow.getAgentInfo().getAgentID(), pattern.getServiceID().getTypeID());
			}  
		}
	}

}