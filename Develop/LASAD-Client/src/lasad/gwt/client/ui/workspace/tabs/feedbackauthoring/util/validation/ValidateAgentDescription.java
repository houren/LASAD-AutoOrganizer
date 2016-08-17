package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.PriorityDef;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_LongText;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_ShortText;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterDef;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Object;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Pattern;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseDef;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseModelerDef;
import lasad.shared.dfki.meta.agents.analysis.phases.PhasesDef;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.common.ActionListDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_OnRequest;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;
import lasad.shared.dfki.meta.agents.provision.priority.PriorityProvisionType;

/**
 * Uses to validate the configuration of an AgentDescriptionFE.
 * @author Anahuac
 *
 */
public class ValidateAgentDescription {
	public static final String BREAK_LINE = "\n"; //<br>
	public static final String BREAK_LINE_HTML = "<br>";
	static final String IS_NOT_SPECIFIED = "is not specificed";
	static final String ARE_NOT_SPECIFIED = "are not specificed";
	static final String ALREADY_EXISTS = "already exists";
	static final String NO_PATTERNS_DEFINED = "No patterns defined";
	static final String NO_MESSAGES_DEFINED = "No messages defined";
	static final String NO_PRIORITIES_DEFINED = "No priorities defined";
	static final String REFERS_2_NOT_EXISTING_PAT = "refers to a not existing Pattern";
	static final String REFERS_2_NOT_EXISTING_MSG = "refers to a not existing Message";
	static final String REFERS_2_NOT_EXISTING_PHASE = "refers to a not existing Phase";
	static final String AT_LEAST_ONE_IS_REQUIRED = "at least one is required";
	
	/**
	 * * This function validates that the provided AgentDescriptionFE configuration is correct,
	 * that is, it checks that all the required fields are present.
	 * @returns an empty String if everything is correct, otherwise a string listing the errors:
	 * - error x
	 * - error y
	 * ...
	 * @param agent
	 * @return
	 */
	public static String validateConf(AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		if(agent != null){
			if(agent.getDisplayName() == null || agent.getDisplayName().equals("")){
				retVal.append(FeedbackAuthoringStrings.AGENT_NAME_LABEL + " " + IS_NOT_SPECIFIED + BREAK_LINE);
				//error = true;
			}
			if (agent.getSupportedOntology() == null || agent.getSupportedOntology().getSupportedOntologies() == null
					|| agent.getSupportedOntology().getSupportedOntologies().size() < 1){
				retVal.append(FeedbackAuthoringStrings.ONTOLOGY_LABEL + " " + IS_NOT_SPECIFIED  + BREAK_LINE);
				//error = true;
	    	}
			retVal.append(checkPhases(agent));
			retVal.append(checkAnalysisTypeList(agent));
			retVal.append(checkActionTypes(agent));
			retVal.append(checkProvisionTypeList(agent));
		}
		return retVal.toString();
	}
	
	private static String checkProvisionTypeList(AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		List<ProvisionType> provList = new Vector<ProvisionType>(agent.getConfData().getProvisionTypes());
		
		if(provList.size() == 0){
			retVal.append(NO_PRIORITIES_DEFINED + ", " + AT_LEAST_ONE_IS_REQUIRED + BREAK_LINE);
			//error = true;
		}
		
		Map<ServiceID, String> actMap = new HashMap<ServiceID, String>();
		Collection<ActionType> actList = agent.getConfData().getActionTypes();
		for(ActionType act: actList){
			actMap.put(act.getServiceID(), act.getName());
		}
		
		HashMap<String, String> provTimeNameMap = new HashMap<String, String>();
		for(ProvisionType provision : provList){
			if(provision instanceof PriorityProvisionType){
				PriorityProvisionType prov = (PriorityProvisionType)provision;
				//basic
				if(prov.getName() == null || prov.getName().equals("")){
					retVal.append(FeedbackAuthoringStrings.STRATEGY_NAME_LABEL + " " + IS_NOT_SPECIFIED  + BREAK_LINE);
					//error = true;
				}
				
				if(prov.getProvisionTime() instanceof ProvisionTimeDef_OnRequest){
					ProvisionTimeDef_OnRequest provTime = (ProvisionTimeDef_OnRequest) prov.getProvisionTime();
					if(provTimeNameMap.containsKey(provTime.getDisplayName())){
						retVal.append(FeedbackAuthoringStrings.STRATEGY_LABEL + " " + prov.getName() + ", " 
								+ FeedbackAuthoringStrings.PROVISION_TIME_LABEL + ", " 
								+ FeedbackAuthoringStrings.DISPLAY_NAME_LABEL + " " 
								+ provTime.getDisplayName() + " " + ALREADY_EXISTS + BREAK_LINE);
						//error = true;
					} else{
						provTimeNameMap.put(provTime.getDisplayName(), provTime.getDisplayName());
					}
				}
				
				ActionListDef actionList = prov.getProvidedActions();
				if(actionList == null || actionList.getServiceIDs() == null || actionList.getServiceIDs().size() == 0){
					retVal.append(FeedbackAuthoringStrings.STRATEGY_LABEL + " " + prov.getName() + ", " 
									+ FeedbackAuthoringStrings.MESSAGE_LABEL + "s" + " " + ARE_NOT_SPECIFIED + BREAK_LINE);
					//error = true;
				} else{
					//validate that referred message exist
					for(ServiceID actId : actionList.getServiceIDs()){
						if(!actMap.containsKey(actId)){
							retVal.append(FeedbackAuthoringStrings.STRATEGY_LABEL + " " + prov.getName() + ", " 
									+ REFERS_2_NOT_EXISTING_MSG + BREAK_LINE);
							//error = true;
						}
					}
				}
				
			} else{
				FATDebug.print(FATDebug.ERROR, "[CheckAgentDescriptionFE][checkProvisionTypeList] unknown type for provision:" + provision + " " + provision.getName());
				retVal.append("[CheckAgentDescriptionFE][checkProvisionTypeList] unknown type for provision:" + provision + " " + provision.getName());
			}
		}
		
		return retVal.toString();
	}
	
	private static String checkActionTypes(AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		
		Map<ServiceID, String> patternMap = new HashMap<ServiceID, String>();
		Collection<AnalysisType> patternList = agent.getConfData().getAnalysisTypes();
		for(AnalysisType pat: patternList){
			patternMap.put(pat.getServiceID(), pat.getName());
		}
		
		List<ActionType> actionList = new Vector<ActionType>(agent.getConfData().getActionTypes());
		if(actionList.size() == 0){
			retVal.append(NO_MESSAGES_DEFINED + ", " + AT_LEAST_ONE_IS_REQUIRED + BREAK_LINE);
			//error = true;
		}
		for(ActionType actionType : actionList){
			if(actionType instanceof FeedbackActionType){
				FeedbackActionType at = (FeedbackActionType)actionType;
				if(at.getName() == null || at.getName().equals("")){
					retVal.append(FeedbackAuthoringStrings.MESSAGE_NAME_LABEL + " " + IS_NOT_SPECIFIED  + BREAK_LINE);
					//error = true;
				}
				ServiceID trigger = at.getTriggerID();
				if(trigger == null){
					retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " + "trigger" + " " + IS_NOT_SPECIFIED + BREAK_LINE);
					//error = true;
				} else if(!patternMap.containsKey(trigger)){
						retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " + "trigger" + " " + REFERS_2_NOT_EXISTING_PAT + BREAK_LINE);
					//error = true;
				}
				//Validate short and long message
				if(at.getMsgCompDefs() == null){
					retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " + FeedbackAuthoringStrings.SHORT_MESSAGE_LABEL +  " " 
									+ "&" + " " + FeedbackAuthoringStrings.LONG_MESSAGE_LABEL + ARE_NOT_SPECIFIED + BREAK_LINE);
					//error = true;
				} else{
					boolean shortMsg = false;
					boolean longMsg = false;
					for(MsgCompDef msg : at.getMsgCompDefs()){
						if(msg instanceof MsgCompDef_ShortText){
							if(((MsgCompDef_ShortText) msg).getText() == null || ((MsgCompDef_ShortText) msg).getText().equals("")){
								shortMsg = true;
							}
						} else if(msg instanceof MsgCompDef_LongText){
							if(((MsgCompDef_LongText) msg).getText() == null || ((MsgCompDef_LongText) msg).getText().equals("")){
								longMsg = true;
							}
						}
					}
					if(shortMsg){
						retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " 
									+ FeedbackAuthoringStrings.SHORT_MESSAGE_LABEL +  " " + IS_NOT_SPECIFIED + BREAK_LINE);
						//error = true;
					}
					if(longMsg){
						retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " 
									+ FeedbackAuthoringStrings.LONG_MESSAGE_LABEL +  " " + IS_NOT_SPECIFIED + BREAK_LINE);
						//error = true;
					}
				}
				PriorityDef priorityDef = at.getPriorityDef();
				if(priorityDef.getPhase2Priority() != null && priorityDef.getPhase2Priority().size() > 0){
					//There are phase priorities defined, check that the phase exists
					HashMap<String, String> phasesMap = new HashMap<String, String>();
					List<PhaseDef> phases = agent.getConfData().getPhaseModelerDef().getPhaseDef().getPhases();
					if(phases != null){
						for(PhaseDef phase: phases){
							phasesMap.put(phase.getID(), phase.getName());
						}
					}
					for(String phaseId : priorityDef.getPhase2Priority().keySet()){
						if(!phasesMap.containsKey(phaseId)){
							retVal.append(FeedbackAuthoringStrings.MESSAGE_LABEL + " " + at.getName() + ", " + "trigger" + " " + REFERS_2_NOT_EXISTING_PHASE + BREAK_LINE);
							//error = true;
						}
					}
				}
			} else{
				FATDebug.print(FATDebug.ERROR, "[CheckAgentDescription][checkActionTypeList] unknown type for actionType:" + actionType);
				retVal.append("[CheckAgentDescription][checkActionTypeList] unknown type for actionType " + actionType.getName());
			}
		}
		
		
		return retVal.toString();
	}
	
	private static String checkAnalysisTypeList(AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		List<AnalysisType> oldList = new Vector<AnalysisType>(agent.getConfData().getAnalysisTypes());
		if(oldList != null){
			if(oldList.size() == 0){
				retVal.append(NO_PATTERNS_DEFINED + ", " + AT_LEAST_ONE_IS_REQUIRED + BREAK_LINE);
				//error = true;
			}
			for(AnalysisType an : oldList){
				retVal.append(checkAnalysisType(an, agent));
			}
		}
		
		return retVal.toString();
	}
	
	private static String checkAnalysisType(AnalysisType analysisType, AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		
		if(analysisType.getName() == null || analysisType.getName().equals("")){
			retVal.append(FeedbackAuthoringStrings.PATTERN_NAME_LABEL + " " + IS_NOT_SPECIFIED  + BREAK_LINE);
			//error = true;
		}
		
		if(analysisType instanceof CounterAnalysisType){
			CounterDef counterDef = ((CounterAnalysisType)analysisType).getCounterDefinition();
			 if(counterDef.getInstanceTypesSpecific() != null &&
					 counterDef.getInstanceTypesSpecific().size() > 0){
				 
				 Map<String, String> patternMap = new HashMap<String, String>();
				 Collection<AnalysisType> patternList = agent.getConfData().getAnalysisTypes();
				 for(AnalysisType pat: patternList){
					 if(pat.getResultDatatype() == AnalysisResultDatatype.object_binary_result){//object_binary_result only applies to structural patterns
						 patternMap.put(pat.getServiceID().getTypeID(), pat.getName());
					 }
				 }
				 for(InstanceTypeSpecific type: ((CounterAnalysisType)analysisType).getCounterDefinition().getInstanceTypesSpecific()){
					if(type instanceof InstanceTypeSpecific_Object){
						//String id = ((InstanceTypeSpecific_Object)type).getTypeID();
					} else if(type instanceof InstanceTypeSpecific_Pattern){
						String id = ((InstanceTypeSpecific_Pattern)type).getTypeID();
						if(!patternMap.containsKey(id)){
							retVal.append(FeedbackAuthoringStrings.PATTERN_LABEL + " " + analysisType.getName() + " " + REFERS_2_NOT_EXISTING_PAT + " " + BREAK_LINE);
							//error = true;
						}
					}
				}
				 
			 }
			
			if(((CounterAnalysisType) analysisType).getCounterCriteria().size() < 1){
				retVal.append(FeedbackAuthoringStrings.PATTERN_LABEL + " " + analysisType.getName() + ", " + FeedbackAuthoringStrings.COUNT_CONDITION + " " + IS_NOT_SPECIFIED + BREAK_LINE);
				//error = true;
			}
			
		} else if(analysisType instanceof StructureAnalysisType){
			List<NodeVariable> nodeVars = ((StructureAnalysisType)analysisType).getPattern().getNodeVars();
			List<LinkVariable> linkVars = ((StructureAnalysisType)analysisType).getPattern().getLinkVars();
			Collection<StructuralPattern> notPatterns = ((StructureAnalysisType)analysisType).getPattern().getNotPatterns();
			if(nodeVars.size() == 0 && linkVars.size() == 0 && notPatterns.size() == 0){
				retVal.append(FeedbackAuthoringStrings.PATTERN_LABEL + " " + analysisType.getName() + " " + "does not have elements defined" + BREAK_LINE);
				//error = true;
			} else{
				//TODO is it requited to add same validation as in PatternServerManager.isValidRelation?
			}
			
			
		}else{
			retVal.append("[ValidateAgentDescription][checkAnalysisType] unknown type for analysisType:" + analysisType);
			FATDebug.print(FATDebug.ERROR, "[ValidateAgentDescription][checkAnalysisType] unknown type for analysisType " + analysisType.getName());
		}
		
		return retVal.toString();
	}
	
	private static String checkPhases(AgentDescriptionFE agent){
		//boolean error = false;
		StringBuilder retVal = new StringBuilder();
		
		PhaseModelerDef newPhaseMod = agent.getConfData().getPhaseModelerDef();
		PhasesDef phasesDef = newPhaseMod.getPhaseDef();
		if(newPhaseMod != null && phasesDef != null){
			HashMap<String, String> map = new HashMap<String, String>();
			for(PhaseDef phase : phasesDef.getPhases()){
				if(map.containsKey(phase.getName())){
					retVal.append(FeedbackAuthoringStrings.PHASE_NAME_LABEL + ":" + phase.getName() + " " + ALREADY_EXISTS + BREAK_LINE);
					//error = true;
				} else{
					map.put(phase.getName(), phase.getName());
				}
			}
		}
		return retVal.toString();
	}

}
