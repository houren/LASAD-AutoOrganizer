package lasad.processors.specific;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.logging.Logger;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionListFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2OntologiesFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2SessionsFE;
import lasad.shared.dfki.authoring.frontenddata.SessionStatusMapFE;
import lasad.shared.dfki.authoring.frontenddata.SessionsFE;
import lasad.shared.dfki.meta.ServiceStatus;


public class FeedbackAuthoringProcessorStatus {

//	private Map<String, AgentDescriptionFE> mapAgentConfig = new HashMap<String, AgentDescriptionFE>(10);
	private AgentDescriptionListFE agentDescriptionList;
	private Agents2OntologiesFE agentsToOntologies;
	private Agents2SessionsFE agentsToSessions;
	private SessionStatusMapFE listSessionStatus;
	private SessionsFE sessionsFE;

//	public void addAgentConfig2Map(String agentId, AgentDescriptionFE agentConfig) {
//		mapAgentConfig.put(agentId, agentConfig);
//	}
//	
//	public void updateAgentConfigFromMap(String agentId, AgentDescriptionFE agentConfig) {
//		mapAgentConfig.remove(agentId);
//		mapAgentConfig.put(agentId, agentConfig);
//	}
//	
//	public void removeAgentConfigFromMap(String agentId, AgentDescriptionFE agentConfig) {
//		mapAgentConfig.remove(agentId);
//	}
//	
//	public AgentDescriptionFE getAgentConfigFromMap(String agentId) {
//		if(mapAgentConfig.containsKey(agentId)){
//			return mapAgentConfig.get(agentId); 
//		}
//		return null;
//	}
//	
//	public void resetMapAgentConfig(){
//		mapAgentConfig.clear();
//	}
//	
//	public Map<String, AgentDescriptionFE> getMapAgentConfig() {
//		return mapAgentConfig;
//	}
	
	public AgentDescriptionListFE getAgentDescriptionList() {
		return agentDescriptionList;
	}

	public void setAgentDescriptionList(AgentDescriptionListFE agentDescriptionList) {
		this.agentDescriptionList = agentDescriptionList;
	}
	
	public void addUpdateAgentFromAgentDescriptionList(AgentDescriptionFE agentFE){
		if (agentDescriptionList != null){
			if (agentDescriptionList.getAgentDescriptions().contains(agentFE)){
				deleteAgentFromAgentDescriptionList(agentFE.getAgentID());
			}
			agentDescriptionList.getAgentDescriptions().add(agentFE);
		}
	}
	
	public void deleteAgentFromAgentDescriptionList(String agentId){
		if (agentDescriptionList != null){
			AgentDescriptionFE tmp = new AgentDescriptionFE();
			tmp.setAgentID(agentId);
			agentDescriptionList.getAgentDescriptions().remove(tmp);
		}
	}
	
	public void compileAgentFromAgentDescriptionList(String agentId){
		if (agentDescriptionList != null){
			AgentDescriptionFE tmp = new AgentDescriptionFE();
			tmp.setAgentID(agentId);
			//int index = agentDescriptionList.getAgentDescriptions().indexOf(tmp);
			//TODO Incomplete
		}
	}

	public Agents2OntologiesFE getAgentsToOntologies() {
		return agentsToOntologies;
	}

	public void setAgentsToOntologies(Agents2OntologiesFE agentsToOntologies) {
		this.agentsToOntologies = agentsToOntologies;
	}
	public void addAgent2AgentsToOntologiesFE(String agentId, String ontology){
		agentsToOntologies.addMapping(agentId, ontology);
	}
	public void removeAgent2AgentsToOntologiesFE(String agentId, String ontology){
		if(agentsToOntologies != null){
			Map<String, List<String>> map = agentsToOntologies.getMappings();
			List<String> agentList = map.get(ontology);
			agentList.remove(agentId);
		}
	}
	
	public Agents2SessionsFE getAgentsToSessions() {
		return agentsToSessions;
	}
	public void setAgentsToSessions(Agents2SessionsFE agentsToSessions) {
		this.agentsToSessions = agentsToSessions;
	}
	public void addAgent2AgentsToSessionsFE(String agentId, String sessionId){
		agentsToSessions.addMapping(agentId, sessionId);
	}
	public void removeAgent2AgentsToSessionsFE(String agentId, String sessionId){
		if(agentsToSessions != null){
			Map<String, List<String>> map = agentsToSessions.getMappings();
			List<String> listOnto = map.get(sessionId);
			listOnto.remove(agentId);
		}
	}

	public SessionStatusMapFE getListSessionStatus() {
		return listSessionStatus;
	}

	public void setListSessionStatus(SessionStatusMapFE listSessionStatus) {
		this.listSessionStatus = listSessionStatus;
	}
	public void updateSessionRuntimeStatus(String sessionID, String newStatus){
		Map<String, ServiceStatus> map =  listSessionStatus.getSessionID2Status();
		if(map.containsKey(sessionID)){
			map.put(sessionID, ServiceStatus.valueOf(newStatus));
		}
	}
	public void addNewAgentToSessionRuntimeStatus(String agentId, String sessionId){
		Map<String,Map<String,ServiceStatus>> map =  listSessionStatus.getSessionID2agent2Status();
		if(map.containsKey(sessionId)){
			Map<String,ServiceStatus> mapAgents = map.get(sessionId);
			if(!mapAgents.containsKey(agentId)){
				mapAgents.put(agentId, ServiceStatus.READY_TO_START);
			}
		}
	}
	public void removeAgentFromSessionRuntimeStatus(String agentId, String sessionId){
		Map<String,Map<String,ServiceStatus>> map =  listSessionStatus.getSessionID2agent2Status();
		if(map.containsKey(sessionId)){
			//check that the agent is not assigned through the ontology panel as well
			Map<String, String> id2OntMap = sessionsFE.getId2OntMap();
			String sesOnt = id2OntMap.get(sessionId);  //get session's ontology
			Map<String,List<String>>mapAg2OnMap = agentsToOntologies.getMappings();
			List<String> agentList = mapAg2OnMap.get(sesOnt); //get all agents assigned to the ontology
			if(agentList == null || (agentList != null && !agentList.contains(agentId))){ //if(!agList.contains(agentId)){
				Map<String,ServiceStatus> mapAgents = map.get(sessionId);
				mapAgents.remove(agentId);
			}
		}
	}
	
	public SessionsFE getSessionsFE() {
		return sessionsFE;
	}
	public void setSessionsFE(SessionsFE sessionsFE) {
		this.sessionsFE = sessionsFE;
	}
	
	public void addNewAgentToOntologyRuntimeStatus(String agentId, String ontology){
		List<String> sessionList = getSessionsWithOntology(ontology);
		for(String ses:sessionList){
			addNewAgentToSessionRuntimeStatus(agentId, ses);
		}
	}
	public void removeAgentFromOntologyRuntimeStatus(String agentId, String ontology){
		List<String> sessionList = getSessionsWithOntology(ontology);
		for(String ses:sessionList){
			//check that the agent is not assigned through the session panel as well
			Map<String, List<String>> map = agentsToSessions.getMappings();
			List<String>agentList = map.get(ses); 
			if(agentList == null || (agentList != null && !agentList.contains(agentId))){
				Map<String,Map<String,ServiceStatus>> map2 =  listSessionStatus.getSessionID2agent2Status();
				if(map2.containsKey(ses)){
					Map<String,ServiceStatus> mapAgents = map2.get(ses);
					mapAgents.remove(agentId);
				}
			}
		}
	}
	
	private List<String> getSessionsWithOntology(String ontology){
		
		Map<String, String> id2OntMap = sessionsFE.getId2OntMap();
		Set<String> sessionList = id2OntMap.keySet();
		
		HashMap<String, List<String>> session2OntologyMapping = new HashMap<String, List<String>>();
		
		for(String ses:sessionList){
			String ont = id2OntMap.get(ses);
			List<String> ontSesList = session2OntologyMapping.get(ont);
			if(ontSesList == null){
				ontSesList = new Vector<String>();
				session2OntologyMapping.put(ont, ontSesList);
			}
			ontSesList.add(ses);
		}
		
		return session2OntologyMapping.get(ontology);
	}
	
//	public void printMapAgentConfig(){
//		Set<String> agentSet = mapAgentConfig.keySet();
//		for(String agentId: agentSet){
//			AgentDescriptionFE agentFE = mapAgentConfig.get(agentId);
//			Logger.debugLog("Agent -id:" + agentFE.getAgentID() + " -name:" + agentFE.getDisplayName());
//        }
//	}
	public void printAgentDescriptionListFE(){
		for(AgentDescriptionFE agentFE: agentDescriptionList.getAgentDescriptions()){
			Logger.debugLog("Agent -id:" + agentFE.getAgentID() + " -name:" + agentFE.getDisplayName());
        }
	}
	public void printAgents2OntologiesFE(){
		Map<String, List<String>> map = agentsToOntologies.getMappings();
        
        Set<String> ontlist = map.keySet();
        for(String ontology: ontlist){
        	List<String> agentList = map.get(ontology);
        	Logger.debugLog("Ontology:" + ontology);
        	for(String agentId:agentList){
        		Logger.debugLog("Agent -id:" + agentId);
        	}
        }
	}
	public void printAgents2SessionsFE(){
        Map<String, List<String>> map = agentsToSessions.getMappings();
        
        Set<String> seslist = map.keySet();
        for(String sessionId: seslist){
	    	Logger.debugLog("Session id:" + sessionId);
	    	List<String> agentList = map.get(sessionId);
	    	for(String agentId:agentList){
	    		Logger.debugLog("Agent -id:" + agentId);
	    	}
        }
	}
	public void printSessionStatusMapFE(){
		Map<String, ServiceStatus> sessionID2Status = listSessionStatus.getSessionID2Status();
        Map<String, Map<String, ServiceStatus>> sessionID2agent2Status = listSessionStatus.getSessionID2agent2Status();
        
        Set<String> seslist = sessionID2Status.keySet();
        for(String sessionId: seslist){
        	ServiceStatus sessionStatus = sessionID2Status.get(sessionId);
        	Logger.debugLog("Session id:" + sessionId + " "+ sessionStatus);
        	
        	Map<String, ServiceStatus> agent2StatusMap = sessionID2agent2Status.get(sessionId);
        	if(agent2StatusMap != null){
        		Set<String> agentList = agent2StatusMap.keySet();
            	for(String agentId : agentList){
            		ServiceStatus agentStatus = agent2StatusMap.get(agentId);
            		Logger.debugLog("Agent id:" + agentId + " "+ agentStatus);
            	}
        	}
        	
        }
	}
	public void printSessionsFE(){
		Map<String, String> id2OntMap = sessionsFE.getId2OntMap();
        Map<String, String> id2NameMap = sessionsFE.getId2NameMap();
        Set<String> ids = id2OntMap.keySet();
        for(String sessionId: ids){
       	 String sessionOnt = id2OntMap.get(sessionId);
			 String sessionName = id2NameMap.get(sessionId);
			Logger.debugLog("Session -id:" + sessionId + " -name:" + sessionName + " -Ont:" + sessionOnt);
		 }
	}
	
}
