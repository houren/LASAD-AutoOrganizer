package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;

/**
 * Class to check dependencies between patterns, messages and provisions
 * @author Anahuac
 *
 */
public class CheckDependencies {
	
	/**
	 * This function validates dependencies that one or more Feedback Messages can have with this pattern,
	 * and the dependencies of one or more of this Feedback Messages with one or more Strategies (ControlThread).
	 * @returns an empty String if there are no dependencies, otherwise a string like the following:
	 * - message x
	 * -- strategy y
	 * ...
	 * @param agent
	 * @param patternId
	 * @return
	 */
	public static String checkPatternDependencies(AgentDescriptionFE agent, String patternId){
		StringBuilder retVal = new StringBuilder();
		
		List<FeedbackActionType> msgDependencyList = getAgentWithFeedbackDependencies(agent, patternId);
		if(msgDependencyList.size() > 0){
//			retVal.append(FeedbackAuthoringStrings.AGENT_LABEL + " " + agent.getDisplayName()
//					+ " " + "has the following dependencies:" + "\n");
			for(FeedbackActionType msg: msgDependencyList){
				retVal.append("-" + msg.getName() + " (" + FeedbackAuthoringStrings.MESSAGE_LABEL +")" + "<br>");
				List<ProvisionType> provDependencyList = getProvisionDependencies(agent, msg.getServiceID().getTypeID());
				if(provDependencyList != null && provDependencyList.size() > 0){
					for(ProvisionType prov : provDependencyList){
						retVal.append("--" + prov.getName() + " (" + FeedbackAuthoringStrings.STRATEGY_LABEL +")" + "<br>");
					}
				}
			}
		}
		return retVal.toString();
	}
	
	/**
	 * * This function validates dependencies that one or more Strategies can have with this message,
	 * @returns an empty String if there are no dependencies, otherwise a string like the following:
	 * - strategy x
	 * ...
	 * @param agent
	 * @param feedbackId
	 * @return
	 */
	public static String checkFeedbackMessageDependencies(AgentDescriptionFE agent, String feedbackId){
		StringBuilder retVal = new StringBuilder();
		
		//ActionType feedback = agent.getConfData().getActionType(new ServiceID(agent.getAgentID(), feedbackId, ServiceClass.ACTION));
		
		List<ProvisionType> provDependencyList = getProvisionDependencies(agent, feedbackId);
		if(provDependencyList != null && provDependencyList.size() > 0){
			for(ProvisionType prov : provDependencyList){
				retVal.append("-" + prov.getName() + " (" + FeedbackAuthoringStrings.STRATEGY_LABEL +")" + "<br>");
			}
		}
		return retVal.toString();
	}
	
	public static List<FeedbackActionType>  getAgentWithFeedbackDependencies(AgentDescriptionFE agent, String patternId){
		List<FeedbackActionType> dependencyList = new Vector<FeedbackActionType>();
		ServiceID servPatternId = new ServiceID(agent.getAgentID(), patternId, ServiceClass.ANALYSIS);
		
		Collection<ActionType> fbList = agent.getConfData().getActionTypes();
		for(ActionType fb:fbList){
			FeedbackActionType newfb = (FeedbackActionType)fb;
			if(newfb.getTriggerID() != null && newfb.getTriggerID().equals(servPatternId)){
				dependencyList.add(newfb);
			}
		}
		return dependencyList;
	}
	
	public static List<ProvisionType> getProvisionDependencies(AgentDescriptionFE agent, String feedbackId){
		List<ProvisionType> dependencyList = new Vector<ProvisionType>();
		
		ServiceID msgServId = new ServiceID(agent.getAgentID(), feedbackId, ServiceClass.ACTION);
		
		Collection<ProvisionType> elemList = agent.getConfData().getProvisionTypes();
		for(ProvisionType elem:elemList){
			if(elem.getProvidedActions() != null){
				for(ServiceID action : elem.getProvidedActions().getServiceIDs()){
					if(action.equals(msgServId)){
						dependencyList.add(elem);
					}
				}
			}
		}
		return dependencyList;
	}

}
