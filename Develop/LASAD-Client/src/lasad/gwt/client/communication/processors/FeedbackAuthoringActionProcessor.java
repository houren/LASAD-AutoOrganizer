package lasad.gwt.client.communication.processors;

import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.Vector;

import lasad.gwt.client.ui.workspace.loaddialogues.LoadingInfoDialogue;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionListFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2OntologiesFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2SessionsFE;
import lasad.shared.dfki.authoring.frontenddata.OntologiesFE;
import lasad.shared.dfki.authoring.frontenddata.SessionStatusMapFE;
import lasad.shared.dfki.authoring.frontenddata.SessionsFE;
import lasad.shared.dfki.meta.ServiceStatus;

public class FeedbackAuthoringActionProcessor {

	public static void translate(Action a) {
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][translate]");
		
		if(a.getCmd().equals(Commands.GetOntologyDetails)){
			FATDebug.print(FATDebug.DEBUG, "GetOntologyDetails for " + a.getParameterValue(ParameterTypes.OntologyName));
		}else{
			FATDebug.print(FATDebug.DEBUG, a.toString());
		}
		
        if(FeedbackAuthoringTabContent.getInstance() == null){
            return;
        }
        
        switch (a.getCmd()) {
			case ListOntologies:// Check
				processListOntologies(a);
				break;
			case GetOntologyDetails:// Check
				processGetOntologyDetails(a);
				break;
			case ListMap:// Check
				processListMap(a);
				break;
			case MapDetails:// Check
				processMapDetails(a);
				break;
			case ListAgentsInfo:// Check
				processListAgentsInfo(a);
				break;
			case ListAgentsToOntologies:// Check
				processListAgentsToOntologies(a);
				break;
			case ListAgentsToSessions:// Check
				processListAgentsToSessions(a);
				break;
			case ListSessionStatus:// Check
				processListSessionStatus(a);
				break;
			case AddOrUpdateAgent:
				processAddOrUpdateAgent(a);
				break;
			case DeleteAgent:
				processDeleteAgent(a);
				break;
			case AddAgentToOntology:
				processAddAgentToOntology(a);
				break;
			case AddAgentToSession:
				processAddAgentToSession(a);
				break;
			case RemoveAgentFromOntology:
				processRemoveAgentFromOntology(a);
				break;
			case RemoveAgentFromSession:
				processRemoveAgentFromSession(a);
				break;
			case CompileAgent:
				processCompileAgent(a);
				break;
			case AgentMappingsDeleted:
				processAgentMappingsDeleted(a);
				break;
			case ComponentRuntimeStatusChanged:
				processComponentRuntimeStatusChanged(a);
				break;
			case SessionRuntimeStatusChanged:
				processSessionRuntimeStatusChanged(a);
				break;
			case GetFreshAgentId:
				processGetFreshAgentId(a);
				break;
			case GetFreshPatternId:
				processGetFreshPatternId(a);
				break;
			case Info:
				processInfoMsg(a);
				break;
			default:
				FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][translate] Error: Unknown action category!");
				break;
		}
	}
	
	private static void processListOntologies(Action a){
		
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListOntologies]");

        if(a.getObjectFE() == null){
        	return;
        }
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
        	if(!hasCommandArrived(Commands.ListOntologies.toString())){
        		addMsgFlag(Commands.ListOntologies.toString(), true);
        		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
         			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
         			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
         		}
        	}
		}
        
        OntologiesFE ontFE = (OntologiesFE)a.getObjectFE();
        List<String> ontologyList = ontFE.getElements();
        for(String ont:ontologyList){
        	FATDebug.print(FATDebug.DEBUG, ont);        	
        }
        FeedbackAuthoringTabContent.getInstance().handleListOntologies(ontologyList);
        processInitMsgsQueue();
	}
	
	private static boolean hasCommandArrived(String command){
		boolean returnVal = false;
		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueFlags()) {
			returnVal = FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueFlags().containsKey(command);
		}
		return returnVal;
	}
	
	private static void addMsgFlag(String key, boolean val){
		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueFlags()) {
			FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueFlags().put(key, val);
		}
	}
	
	private static boolean isCommandAtStart(String command){
		boolean retVal = false;
		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
			if(FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().size() >= 1){
				retVal = FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().get(0).equals(command);
			}
 		}
		return retVal;
	}
	
	private static void processInitMsgsQueue(){
		FATDebug.print(FATDebug.DEBUG,"[FAActionProcessor][processInitMsgsQueue]");
		if(FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
			return;
		}
		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
			FATDebug.print(FATDebug.DEBUG,"[FAActionProcessor][processInitMsgsQueue] InitMsgsTracker:");
			for(String cmd:FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()){
				FATDebug.print(FATDebug.DEBUG,"cmd:" + cmd);
			}
			
			//Vector<String> tmp = new Vector<String>(FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker());
			while(FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().size() > 0){
				String cmd = FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().get(0);
				
				FATDebug.print(FATDebug.DEBUG,"[FAActionProcessor][processInitMsgsQueue] trying cmd:" + cmd);
				
				if(!isCommandAtStart(cmd) || !hasCommandArrived(cmd)){
					break;
				}
				Action action = null;
				synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
					action = FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().remove(cmd);
				}
				FATDebug.print(FATDebug.DEBUG,"[FAActionProcessor][processInitMsgsQueue] about to process cmd:" + cmd);
				if(cmd.equals(Commands.ListAgentsInfo.toString())){
					processListAgentsInfo(action);
				} else if(cmd.equals(Commands.ListMap.toString())){
					processListMap(action);
				} else if(cmd.equals(Commands.ListAgentsToSessions.toString())){
					processListAgentsToSessions(action);
				} else if(cmd.equals(Commands.ListAgentsToOntologies.toString())){
					processListAgentsToOntologies(action);
				} else if(cmd.equals(Commands.ListSessionStatus.toString())){
					processListSessionStatus(action);
				} else if(cmd.equals(Commands.GetOntologyDetails.toString())){
					processGetOntologyDetails(action);
				}
			}
		}
		
	}
	
	private static void processGetOntologyDetails(Action a){
		FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processGetOntologyDetails]");
        String ontology = a.getParameterValue(ParameterTypes.OntologyName);
        String ontologyXML = a.getParameterValue(ParameterTypes.Ontology);
        FATDebug.print(FATDebug.DEBUG,"Ontology name:" + ontology);
        
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
        	if(ontology.equals(FATConstants.FA_ONTOLOGY_NAME)){
        		if(!hasCommandArrived(Commands.GetOntologyDetails.toString())){
             		addMsgFlag(Commands.GetOntologyDetails.toString(), true);
             	}
         		if(!isCommandAtStart(Commands.GetOntologyDetails.toString())){
         			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
         				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.GetOntologyDetails.toString(), a);
         			}
         			processInitMsgsQueue();
         			return;
     			}
         		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
         			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
         			FeedbackAuthoringTabContent.getInstance().cancelInitTimer();
         		}
         		FeedbackAuthoringTabContent.getInstance().setInitMsgs(true);
         		LoadingInfoDialogue.getInstance().closeLoadingScreen();
        	}
 		}
        
        
        //FATDebug.print(FATDebug.DEBUG,ontologyXML);
        FeedbackAuthoringTabContent.getInstance().handleGetOntologyDetails(ontology, ontologyXML);
	}
	private static void processListMap(Action a){
		 FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListMap]");
         if(a.getObjectFE() == null){
         	return;
         }
         
         if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
         	if(!hasCommandArrived(Commands.ListMap.toString())){
         		addMsgFlag(Commands.ListMap.toString(), true);
         	}
     		if(!isCommandAtStart(Commands.ListMap.toString())){
     			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
     				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.ListMap.toString(), a);
     			}
     			processInitMsgsQueue();
     			return;
 			}
     		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
     			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
     			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
     		}
 		}
         
         SessionsFE sesFE = (SessionsFE)a.getObjectFE();
         Map<String, String> id2OntMap = sesFE.getId2OntMap();
         Map<String, String> id2NameMap = sesFE.getId2NameMap();
         Set<String> ids = id2OntMap.keySet();
         for(String sessionId: ids){
        	 String sessionOnt = id2OntMap.get(sessionId);
 			 String sessionName = id2NameMap.get(sessionId);
 			FATDebug.print(FATDebug.DEBUG, "Session -id:" + sessionId + " -name:" + sessionName + " -Ont:" + sessionOnt);
 		 }
         FeedbackAuthoringTabContent.getInstance().handleListMap(id2OntMap, id2NameMap);
         processInitMsgsQueue();
	}
	private static void processMapDetails(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processMapDetails]");
        if(a.getObjectFE() == null){
        	return;
        }
        FeedbackAuthoringTabContent.getInstance().handleMapdetails();
	}
	private static void processListAgentsInfo(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListAgentsInfo]");
        if(a.getObjectFE() == null){
        	FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListAgentsInfo] is null");
        	return;
        }
        
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
        	if(!hasCommandArrived(Commands.ListAgentsInfo.toString())){
        		addMsgFlag(Commands.ListAgentsInfo.toString(), true);
        	}
    		if(!isCommandAtStart(Commands.ListAgentsInfo.toString())){
    			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
    				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.ListAgentsInfo.toString(), a);
    			}
    			processInitMsgsQueue();
    			return;
			}
    		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
     			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
     			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
     		}
		}
        
        AgentDescriptionListFE value = (AgentDescriptionListFE)a.getObjectFE();
        for(AgentDescriptionFE agentFE: value.getAgentDescriptions()){
        	FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListAgentsInfo] Agent -id:" + agentFE.getAgentID() + " -name:" + agentFE.getDisplayName()
        					+ " -configCompleted:" + agentFE.isConfigCompleted()
        					+ " -confReadable:" + agentFE.isConfReadable()
        					+ " -confWritable:" + agentFE.isConfWritable());
        }
        FeedbackAuthoringTabContent.getInstance().handleListAgentsInfo(value.getAgentDescriptions());
        processInitMsgsQueue();
	}
	private static void processListAgentsToOntologies(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListAgentsToOntologies]");
        if(a.getObjectFE() == null){
        	return;
        }
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
         	if(!hasCommandArrived(Commands.ListAgentsToOntologies.toString())){
         		addMsgFlag(Commands.ListAgentsToOntologies.toString(), true);
         	}
     		if(!isCommandAtStart(Commands.ListAgentsToOntologies.toString())){
     			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
     				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.ListAgentsToOntologies.toString(), a);
     			}
     			processInitMsgsQueue();
     			return;
 			}
     		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
     			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
     			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
     		}
 		}
        
        //TODO should we get a fresh version of the Ontologies???
        Agents2OntologiesFE value = (Agents2OntologiesFE)a.getObjectFE();
        Map<String, List<String>> map = value.getMappings();
        
        Set<String> ontlist = map.keySet();
        for(String ontology: ontlist){
        	List<String> agentList = map.get(ontology);
        	FATDebug.print(FATDebug.DEBUG, "Ontology:" + ontology);
        	for(String agentId:agentList){
        		FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId);
        	}
        }
        FeedbackAuthoringTabContent.getInstance().handleListAgentsToOntologies(map);
        processInitMsgsQueue();
	}
	private static void processListAgentsToSessions(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListAgentsToSessions]");
        if(a.getObjectFE() == null){
        	return;
        }
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
         	if(!hasCommandArrived(Commands.ListAgentsToSessions.toString())){
         		addMsgFlag(Commands.ListAgentsToSessions.toString(), true);
         	}
     		if(!isCommandAtStart(Commands.ListAgentsToSessions.toString())){
     			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
     				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.ListAgentsToSessions.toString(), a);
     			}
     			processInitMsgsQueue();
     			return;
 			}
     		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
     			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
     			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
     		}
 		}
        
        Agents2SessionsFE value = (Agents2SessionsFE)a.getObjectFE();
        Map<String, List<String>> map = value.getMappings();
        
        Set<String> seslist = map.keySet();
        for(String sessionId: seslist){
	    	FATDebug.print(FATDebug.DEBUG, "Session id:" + sessionId);
	    	List<String> agentList = map.get(sessionId);
	    	for(String agentId:agentList){
	    		FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId);
	    	}
        }
        
        FeedbackAuthoringTabContent.getInstance().handleListAgentsToSessions(map);
        processInitMsgsQueue();
	}
	private static void processListSessionStatus(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processListSessionStatus]");
        if(a.getObjectFE() == null){
        	return;
        }
        if(!FeedbackAuthoringTabContent.getInstance().isInitMsgs()){
         	if(!hasCommandArrived(Commands.ListSessionStatus.toString())){
         		addMsgFlag(Commands.ListSessionStatus.toString(), true);
         	}
     		if(!isCommandAtStart(Commands.ListSessionStatus.toString())){
     			synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal()) {
     				FeedbackAuthoringTabContent.getInstance().getInitMsgsQueueVal().put(Commands.ListSessionStatus.toString(), a);
     			}
     			processInitMsgsQueue();
     			return;
 			}
     		synchronized (FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker()) {
     			FeedbackAuthoringTabContent.getInstance().getInitMsgsTracker().remove(0);
     			FeedbackAuthoringTabContent.getInstance().resetInitTimer();
     		}
 		}
        
        SessionStatusMapFE value = (SessionStatusMapFE)a.getObjectFE();
        Map<String, ServiceStatus> sessionID2Status = value.getSessionID2Status();
        Map<String, Map<String, ServiceStatus>> sessionID2agent2Status = value.getSessionID2agent2Status();
        
        Set<String> seslist = sessionID2Status.keySet();
        for(String sessionId: seslist){
        	ServiceStatus sessionStatus = sessionID2Status.get(sessionId);
        	FATDebug.print(FATDebug.DEBUG, "Session id:" + sessionId + " "+ sessionStatus);
        	
        	Map<String, ServiceStatus> agent2StatusMap = sessionID2agent2Status.get(sessionId);
        	if(agent2StatusMap != null){
        		Set<String> agentList = agent2StatusMap.keySet();
            	for(String agentId : agentList){
            		ServiceStatus agentStatus = agent2StatusMap.get(agentId);
            		FATDebug.print(FATDebug.DEBUG, "Agent id:" + agentId + " "+ agentStatus);
            	}
        	}
        	
        }
        
        FeedbackAuthoringTabContent.getInstance().handleListSessionStatus(sessionID2Status, sessionID2agent2Status);
        processInitMsgsQueue();
	}
	private static void processAddOrUpdateAgent(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddOrUpdateAgent]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			AgentDescriptionFE agentDescriptionFE = (AgentDescriptionFE)a.getObjectFE();
			FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddOrUpdateAgent] Changed Agent -id:" + agentDescriptionFE.getAgentID() + " -name:" + agentDescriptionFE.getDisplayName());
			FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddOrUpdateAgent] Changed Agent -id:" + agentDescriptionFE.getAgentID() + " -name:" + agentDescriptionFE.getDisplayName()
					+ " -configCompleted:" + agentDescriptionFE.isConfigCompleted()
					+ " -confReadable:" + agentDescriptionFE.isConfReadable()
					+ " -confWritable:" + agentDescriptionFE.isConfWritable());
			
	        FeedbackAuthoringTabContent.getInstance().handleAddOrUpdateAgent(agentDescriptionFE);
		} else{
			AgentDescriptionFE agentDescriptionFE = (AgentDescriptionFE)a.getObjectFE();
			FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddOrUpdateAgent] Not Changed Agent -id:" + agentDescriptionFE.getAgentID() + " -name:" + agentDescriptionFE.getDisplayName());
		}
	}
	private static void processDeleteAgent(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processDeleteAgent]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId);
			
			FeedbackAuthoringTabContent.getInstance().handleDeleteAgent(agentId);
		}
	}
	private static void processAddAgentToOntology(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddAgentToOntology]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String ontology = a.getParameterValue(ParameterTypes.Ontology);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId + " -ontology:" + ontology);
			
			FeedbackAuthoringTabContent.getInstance().handleAddAgentToOntology(agentId, ontology);
		}
	}
	private static void processRemoveAgentFromOntology(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processRemoveAgentFromOntology]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String ontology = a.getParameterValue(ParameterTypes.Ontology);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId + " -ontology:");
			
			FeedbackAuthoringTabContent.getInstance().handleRemoveAgentFromOntology(agentId, ontology);
		}
	}
	private static void processAddAgentToSession(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAddAgentToSession]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String sessionId = a.getParameterValue(ParameterTypes.SessionId);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId + " -sessionId:" + sessionId);
			
			FeedbackAuthoringTabContent.getInstance().handleAddAgentToSession(agentId, sessionId);
		}
	}
	private static void processRemoveAgentFromSession(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processRemoveAgentFromSession]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String sessionId = a.getParameterValue(ParameterTypes.SessionId);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId + " -sessionId:" + sessionId);
			
			FeedbackAuthoringTabContent.getInstance().handleRemoveAgentFromSession(agentId, sessionId);
		}
	}
	private static void processCompileAgent(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processCompileAgent]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		String successful = a.getParameterValue(ParameterTypes.Successful);
		if (changedStr != null && Boolean.parseBoolean(changedStr) && successful != null){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId);
			
			FeedbackAuthoringTabContent.getInstance().handleCompileAgent(agentId);
		}
	}
	private static void processAgentMappingsDeleted(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processAgentMappingDeleted]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			List<String> ontologyList = a.getParameterValues(ParameterTypes.Ontology); 
			List<String> sessionIdList = a.getParameterValues(ParameterTypes.SessionId);
			
			FATDebug.print(FATDebug.DEBUG, "Agent -id:" + agentId);
			if(ontologyList != null){
				for(String ontology:ontologyList){
					FATDebug.print(FATDebug.DEBUG, "Ontology:" + ontology);
				}
			}else{
				FATDebug.print(FATDebug.DEBUG, "Ontologies are not affected");
			}
			if(sessionIdList != null){
				for(String sessionId:sessionIdList){
					FATDebug.print(FATDebug.DEBUG, "Session:" + sessionId);
				}
			}else{
				FATDebug.print(FATDebug.DEBUG, "Sessions are not affected");
			}
			
			FeedbackAuthoringTabContent.getInstance().handleAgentMappingsDeleted(agentId, ontologyList, sessionIdList);
		}
	}
	private static void processComponentRuntimeStatusChanged(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processComponentRuntimeStatusChanged]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String componentId = a.getParameterValue(ParameterTypes.ChangedComponentID);
			String oldStatus = a.getParameterValue(ParameterTypes.OldStatus);
			String newStatus = a.getParameterValue(ParameterTypes.NewStatus);
			
			FATDebug.print(FATDebug.DEBUG, "componentId:" + componentId + " -oldStatus:" + oldStatus + " -newStatus:" + newStatus);
			
			FeedbackAuthoringTabContent.getInstance().handleComponentRuntimeStatusChanged(componentId, ServiceStatus.valueOf(oldStatus) , ServiceStatus.valueOf(newStatus));
		}
	}
	private static void processSessionRuntimeStatusChanged(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processSessionRuntimeStatusChanged]");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr)){
			String sessionId = a.getParameterValue(ParameterTypes.SessionId);
			String oldStatus = a.getParameterValue(ParameterTypes.OldStatus);
			String newStatus = a.getParameterValue(ParameterTypes.NewStatus);
			
			FATDebug.print(FATDebug.DEBUG, "sessionId:" + sessionId + " -oldStatus:" + oldStatus + " -newStatus:" + newStatus);
			
			FeedbackAuthoringTabContent.getInstance().handleSessionRuntimeStatusChanged(sessionId, ServiceStatus.valueOf(oldStatus), ServiceStatus.valueOf(newStatus));
		}
	}
	
	private static void processGetFreshAgentId(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processGetFreshAgentId]");
		String successfuldStr = a.getParameterValue(ParameterTypes.Successful);
		if (successfuldStr != null && Boolean.parseBoolean(successfuldStr)){
			String requestId = a.getParameterValue(ParameterTypes.RequestId);
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			
			FATDebug.print(FATDebug.DEBUG, "requestId:" + requestId + " -agentId:" + agentId);
			
			FeedbackAuthoringTabContent.getInstance().handleGetFreshAgentId(requestId, agentId);
		}
	}
	private static void processGetFreshPatternId(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processGetFreshPatternId]");
		String successfuldStr = a.getParameterValue(ParameterTypes.Successful);
		if (successfuldStr != null && Boolean.parseBoolean(successfuldStr)){
			String requestId = a.getParameterValue(ParameterTypes.RequestId);
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String patternId = a.getParameterValue(ParameterTypes.PatternId);
			String serviceClass = a.getParameterValue(ParameterTypes.ServiceClass);
			
			FATDebug.print(FATDebug.DEBUG, "requestId:" + requestId + " -agentId:" + agentId + " -patternId:" + patternId + " -serviceClass:" + serviceClass);
			
			FeedbackAuthoringTabContent.getInstance().handleGetFreshPatternId(requestId, agentId, patternId, serviceClass);
		}
	}
	private static void processInfoMsg(Action a){
		FATDebug.print(FATDebug.DEBUG, "[lasad.gwt.client.communication.FeedbackAuthoringActionProcessor][processInfo]");
		String successfuldStr = a.getParameterValue(ParameterTypes.Successful);
		if (successfuldStr != null && Boolean.parseBoolean(successfuldStr)){
			String text = a.getParameterValue(ParameterTypes.Text);
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			String patternId = a.getParameterValue(ParameterTypes.PatternId);
			
			FATDebug.print(FATDebug.DEBUG, "text:" + text + " -agentId:" + agentId + " -patternId:" + patternId);
			
			FeedbackAuthoringTabContent.getInstance().handleInfoMsg(text, agentId, patternId);
		}
	}
}
