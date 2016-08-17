package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;

/**
 * use to keep track of Agents, Ontologies and Sessions.
 * @author anahuacv
 */

public class DatabaseFA {
	
	private HashMap<String, AgentDescriptionFE> agentStore = new HashMap<String, AgentDescriptionFE>();
	private HashMap<String, OntologyFA> ontologyStore = new HashMap<String, OntologyFA>();
	private HashMap<String, SessionFA> sessionStore = new HashMap<String, SessionFA>();
	/*
	 * tracks the pair session name, id <SessionName, SessionId>
	 */
	private HashMap<String, String> name2IdSession = new HashMap<String, String>();
	
	/*
	 * These HashMaps are used to: 
	 */
	// tracks the Agents assigned to an Ontology     <Ontology, HashMap<Agent, 0>
	private HashMap<String, HashMap<String, Integer>> agent2OntologyTracker = new HashMap<String, HashMap<String, Integer>>();
	// tracks the Agents assigned to a session     <Sessions, HashMap<Agent, 0>
	private HashMap<String, HashMap<String, Integer>> agent2SessionTracker = new HashMap<String, HashMap<String, Integer>>();
	// tracks the Ontologies where the agent is assigned <AgentId, HashMap<Ontology, 0>
	private HashMap<String, List<String>> ontology2AgentTracker = new HashMap<String, List<String>>();
	// tracks the Sessions where the agent is assigned <AgentId, HashMap<Sessions, 0>
	private HashMap<String, List<String>> session2AgentTracker = new HashMap<String, List<String>>();
	
	// tracks the sessions with the same ontology <ontologyName, List<sessionId>
	private HashMap<String, List<String>> session2OntoTracker = new HashMap<String, List<String>>();
	
	public boolean addAgent(AgentDescriptionFE agent){
		boolean error = true;
		String agentId = agent.getAgentID();
		if(!agentStore.containsKey(agentId)){
			agentStore.put(agentId, agent);
			error = false;
		}
		return error;
	}
	
	public boolean deleteAgent(String agentId){
		boolean error = true;
		if(agentStore.containsKey(agentId)){
			agentStore.remove(agentId);
			//the agent will be removed from sessions and ontologies 
			//with the AP with Command AgentMappingsDeleted
			error = false;
		}
		return error;
	}
	
	public boolean updateAgent(AgentDescriptionFE agent){
		boolean error = false;
		String agentId = agent.getAgentID();
		agentStore.remove(agentId);
		agentStore.put(agentId, agent);
		return error;
	}
	
	public boolean existAgent(String agentId){
		boolean returnVal = false;
		if(agentStore.containsKey(agentId)){
			returnVal = true;
		}
		return returnVal;
	}
	
	public boolean existAgentByName(String agentName){
		boolean returnVal = false;
		Set<String> setAg = agentStore.keySet();
		for(String agentId:setAg){
			if(agentStore.get(agentId).getDisplayName().equals(agentName)){
				returnVal = true;
				break;
			}
		}
		return returnVal;
	}
	
	public AgentDescriptionFE getAgent(String agentId){
		return agentStore.get(agentId);
	}
	
	public List<String> getAgents(){
		List<String> tmp = new Vector<String>();
		Set<String> setAg = agentStore.keySet();
		tmp.addAll(setAg);
		
		return tmp;
	}
	public Map<String,String> getAgentsIdNameMap(){
		Map<String, String> tmp = new HashMap<String,String>();
		Set<String> setAg = agentStore.keySet();
		for(String agentId:setAg){
			tmp.put(agentId, agentStore.get(agentId).getDisplayName());
		}
		return tmp;
	}
	
	public void resetAgents(){
		agentStore.clear();
	}
	
	public boolean addOntology(OntologyFA ontology){
		boolean error = true;
		String ontologyName = ontology.getName();
		if(!ontologyStore.containsKey(ontologyName)){
			ontologyStore.put(ontologyName, ontology);
			
			agent2OntologyTracker.put(ontologyName, new HashMap<String, Integer>());
			
			error = false;
		}
		return error;
	}
	
	public Set<String> getOntologies(){
		return ontologyStore.keySet();
	}
	
	public OntologyFA getOntology(String ontologyName){
		OntologyFA ontology = null;
		if(ontologyStore.containsKey(ontologyName)){
			ontology = ontologyStore.get(ontologyName);
		}
		return ontology;
	}
	public boolean existOntology(String ontologyName){
		boolean returnVal = false;
		if(ontologyStore.containsKey(ontologyName)){
			returnVal = true;
		}
		return returnVal;
	}
	public boolean updateOntologyXML(String ontologyName, String ontologyXML){
		boolean returnVal = false;
		if(ontologyStore.containsKey(ontologyName)){
			returnVal = true;
			OntologyFA ontology = ontologyStore.get(ontologyName);
			ontology.setXml(ontologyXML);
			ontology.setOntology(null);
		}
		return returnVal;
	}
	
	public void resetOntologies(){
		ontologyStore.clear();
	}
	
	public boolean addSession(SessionFA session){
		boolean error = true;
		String sessionId = session.getId();
		if(!sessionStore.containsKey(sessionId)){
			sessionStore.put(sessionId, session);
			addSes2OntoTracker(sessionId, session.getOntology());
			name2IdSession.put(session.getName(), sessionId);
			agent2SessionTracker.put(sessionId, new HashMap<String, Integer>());
			
			error = false;
		}
		return error;
	}
	public Set<String> getSessions(){
		return sessionStore.keySet();
	}
	
	public void resetSessions(){
		sessionStore.clear();
	}
	
	public boolean existSession(String sessionId){
		boolean returnVal = false;
		if(sessionStore.containsKey(sessionId)){
			returnVal = true;
		}
		return returnVal;
	}
	
	public SessionFA getSession(String sessionId){
		return sessionStore.get(sessionId);
	}
	
	public List<String> getSessionsWithOntology(String ontology){
		List<String> tmp = new Vector<String>();
		if(session2OntoTracker.containsKey(ontology)){
			tmp.addAll(session2OntoTracker.get(ontology));
		}
		return tmp;
	}
	
	public String getSessionId(String sessionName){
		return name2IdSession.get(sessionName);
	}
	
	public String getSessionName(String sessionId){
		SessionFA session = sessionStore.get(sessionId);
		if(session == null){
			FATDebug.print(FATDebug.ERROR, "[DatabaseFA][getSessionName]sessionId " + sessionId + " not found in sessionStore");
			return null;
		} else{
			return session.getName();
		}
	}
	
	/**
     * Adds an agent to an Ontology
     *
     * @param  agentId agent id
     * @param  ontologyName ontology's name
     * @throws NullPointerException if any of agentId and ontologyName are null .
     */
	public boolean addAgent2Ontology(String agentId, String ontologyName) throws NullPointerException{
		boolean error = true;
		
		if (agentId == null) throw new NullPointerException("agentId is null");
		if (ontologyName == null) throw new NullPointerException("ontology is null");
		
		if(existOntology(ontologyName)){ //check if the ontology is in the ontology store
			if(agent2OntologyTracker.containsKey(ontologyName)){
				HashMap<String, Integer> ht = agent2OntologyTracker.get(ontologyName);
				if(!ht.containsKey(agentId)){ //check if the agent is already there
					ht.put(agentId, 0);
					addOnt2Ont2AgTracker(agentId, ontologyName);
					//logger.debug("Agent -" + agentId + "- assigned to ontology -"+ ontology + "' ontology");
				}
				error = false;
			}
		}
		return error;
	}
	
	/**
     * Adds an ontology to an agent
     *
     * @param  agent agent's name
     * @param  ontology ontology's name
     * @throws NullPointerException if any of agent and ontology are null .
     */
	private void addOnt2Ont2AgTracker(String agentId, String ontology){
		if(ontology2AgentTracker.containsKey(agentId)){
			List<String> listAgOnto = ontology2AgentTracker.get(agentId);
			listAgOnto.add(ontology);
		}else{
			List<String> listAgOnto = new Vector<String>();
			listAgOnto.add(ontology);
			ontology2AgentTracker.put(agentId, listAgOnto);
		}
	}
	
	/**
	 * get agents in ontology
     * gets a list of agents assigned to the ontology
     * @param  ontology ontology's name
     * @return List<Integer> the list of agents
     */
	public List<String> getAgInOnt(String ontology){
		List<String> list;
		if(agent2OntologyTracker.containsKey(ontology)){
			HashMap<String, Integer> tmp = agent2OntologyTracker.get(ontology);
			list = new Vector<String>(tmp.keySet());
		}else{
			list = new Vector<String>();
		}
		return list;
	}
	
	/**
	 * tests if an ontology has assigned an agent
     * 
     * @param  ontology ontology's name
     * @param  agent agent's name
     * @return true if agent is already assigned in ontology, false otherwise
     */
	public boolean isAgentInOntology(String ontology, String agentId){
		boolean retVal = false;
		if(agent2OntologyTracker.containsKey(ontology)){
			HashMap<String, Integer> tmp = agent2OntologyTracker.get(ontology);
			if (tmp.containsKey(agentId)){
				retVal = true;
			}
		}
		return retVal;
	}
	
	
	/**
	 * get ontologies for agent
     * gets a list of ontologies where agent is assigned to
     * @param  agent agent name
     * @return List<String> the list of ontologies
     */
	public List<String> getOnt4Ag(String agentId){
		List<String> list;
		if(ontology2AgentTracker.containsKey(agentId)){
			list = ontology2AgentTracker.get(agentId);
		}else{
			list = new Vector<String>();
		}
		return list;
	}
	
	/**
     * deletes all the agent from an ontology
     *
     * @param  ontology name
     * @throws NullPointerException if ontology is null .
     */
	public boolean deleteAllAgentsFromOntology(String ontology) throws NullPointerException{
		boolean error = true;
		
		if (ontology == null) throw new NullPointerException("ontology is null");
		
		if(existOntology(ontology)){ //check if the ontology is in the ontology store
			if(agent2OntologyTracker.containsKey(ontology)){ //check if the ontology is in the agent2Ontology tracker
				HashMap<String, Integer> agentsHM = agent2OntologyTracker.get(ontology);
				Set<String> agentsList = agentsHM.keySet();
				
				for(String agentId : agentsList){
					deleteOntFromOnt2AgTracker(agentId, ontology);
				}
				agentsHM.clear();
			}
		}
		return error;
	}
	
	/**
     * deletes an agent from an ontology
     *
     * @param  agentId agent id
     * @param  ontology ontology's name
     * @throws NullPointerException if any of agent and ontology are null .
     */
	public boolean deleteAgentFromOntology(String agentId, String ontology) throws NullPointerException{
		boolean error = true;
		
		if (ontology == null) throw new NullPointerException("ontology is null");
		
		if(existOntology(ontology)){ //check if the ontology is in the ontology store
			if(agent2OntologyTracker.containsKey(ontology)){ //check if the ontology is in the agent2Ontology tracker
				HashMap<String, Integer> ht = agent2OntologyTracker.get(ontology);
				if(ht.containsKey(agentId)){ //check if the agent is there
					ht.remove(agentId);
					deleteOntFromOnt2AgTracker(agentId, ontology);
					error = false;
					//logger.warn("Agent -" + agent + "- deleted from ontology");
				}
			}
		}
		return error;
	}
	
	/**
     * deletes an ontology from an agent
     *
     * @param  agent agent's name
     * @param  ontology ontology's name
     * @throws NullPointerException if any of agent and ontology are null .
     */
	private void deleteOntFromOnt2AgTracker(String agentId, String ontology){
		
		if(ontology2AgentTracker.containsKey(agentId)){
			List<String> listAgOnto = ontology2AgentTracker.get(agentId);
			listAgOnto.remove(ontology);
		}
	}
	
	/**
     * Adds an agent to a session
     *
     * @param  agentid
     * @param  sessionId sessionId id	
     * @throws NullPointerException if any of agent and session are null .
     */
	public boolean addAgent2Session(String agentId, String sessionId) throws NullPointerException{
		boolean error = true;
		
		if (sessionId == null) throw new NullPointerException("session is null");
		
		if(existSession(sessionId)){ //check if the session is in the session store
			if(agent2SessionTracker.containsKey(sessionId)){ //check if the session is in the agent2Session tracker
				HashMap<String, Integer> ht = agent2SessionTracker.get(sessionId);
				if(!ht.containsKey(agentId)){ //check if the agent is there
					ht.put(agentId, 0);
					addSes2AgSesTracker(agentId, sessionId);
					//logger.warn("Agent -" + agent + "- is already assigned to session -"+ session + "'");
				}
				error = false;
			}
		}
		return error;
	}
	
	/**
     * Adds a session to an agent
     *
     * @param  agent agent's name
     * @param  sessionId session Id
     */
	private void addSes2AgSesTracker(String agentId, String sessionId){
		
		if(session2AgentTracker.containsKey(agentId)){
			List<String> listAgOnto = session2AgentTracker.get(agentId);
			listAgOnto.add(sessionId);
		}else{
			List<String> listAgOnto = new Vector<String>();
			listAgOnto.add(sessionId);
			session2AgentTracker.put(agentId, listAgOnto);
		}
	}
	
	/**
	 * tests if a session has assigned an agent
     * 
     * @param  session sessionId 
     * @param  agent agent's name
     * @return true if agent is already assigned in session, false otherwise
     */
	public boolean isAgentInSession(String sessionId, String agentId){
		boolean retVal = false;
		if(agent2SessionTracker.containsKey(sessionId)){
			HashMap<String, Integer> tmp = agent2SessionTracker.get(sessionId);
			if (tmp.containsKey(agentId)){
				retVal = true;
			}
		}
		return retVal;
	}
	
	/**
	 * get agents in session
     * gets a list of agents assigned to the session
     * @param  sessionId 
     * @return List<Integer> the list of agents
     */
	public List<String> getAgInSes(String sessionId){
		List<String> list;
		if(agent2SessionTracker.containsKey(sessionId)){
			HashMap<String, Integer> tmp = agent2SessionTracker.get(sessionId);
			list = new Vector<String>(tmp.keySet());
		}else{
			list = new Vector<String>();
		}
		return list;
	}
	
	/**
	 * get sessions for agent
     * gets a list of sessions where agent is assigned to
     * @param  agent agent name
     * @return List<String> the list of sessions
     */
	public List<String> getSes4Ag(String agentId){
		List<String> list;
		if(session2AgentTracker.containsKey(agentId)){
			list = session2AgentTracker.get(agentId);
		}else{
			list = new Vector<String>();
		}
		return list;
	}
	
	/**
     * deletes all the agents from an session
     *
     * @param  sessionId
     * @throws NullPointerException if session is null .
     */
	public boolean deleteAllAgentsFromSession(String sessionId) throws NullPointerException{
		boolean error = true;
		
		if (sessionId == null) throw new NullPointerException("session is null");
		
		if(existSession(sessionId)){ //check if the session is in the session store
			if(agent2SessionTracker.containsKey(sessionId)){ //check if the session is in the agent2Session tracker
				HashMap<String, Integer> agentsHM = agent2SessionTracker.get(sessionId);
				Set<String> agentsList = agentsHM.keySet();
				
				for(String agentId : agentsList){
					deleteSesFromSes2AgTracker(agentId, sessionId);
				}
				agentsHM.clear();
			}
		}
		return error;
	}
	
	
	
	/**
     * deletes an agent from a session
     *
     * @param  agent agent's name
     * @param  sessionId 
     * @throws NullPointerException if session is null .
     */
	public boolean deleteAgentFromSession(String agentId, String sessionId) throws NullPointerException{
		boolean error = true;
		
		if (sessionId == null) throw new NullPointerException("session is null");
		
		if(existSession(sessionId)){ //check if the session is in the session store
			if(agent2SessionTracker.containsKey(sessionId)){ //check if the session is in the agent2Session tracker
				HashMap<String, Integer> ht = agent2SessionTracker.get(sessionId);
				if(ht.containsKey(agentId)){ //check if the agent is there
					ht.remove(agentId);
					error = false;
					deleteSesFromSes2AgTracker(agentId, sessionId);
					//logger.warn("Agent -" + agent + "- deleted from session");
				}
			}
		}
		
		return error;
	}
	
	/**
     * deletes a session from an agent
     *
     * @param  agent agent's name
     * @param  sessionId 
     * @throws NullPointerException if any of agent and ontology are null .
     */
	private void deleteSesFromSes2AgTracker(String agentId, String sessionId){
		
		if(session2AgentTracker.containsKey(agentId)){
			List<String> listAgOnto = session2AgentTracker.get(agentId);
			listAgOnto.remove(sessionId);
		}
	}
	
	/**
     * Adds a session to an ontology
     *
     * @param  sessionId 
     * @param  ontology ontology's name
     */
	private void addSes2OntoTracker(String sessionId, String ontology){
		
		if(session2OntoTracker.containsKey(ontology)){
			List<String> listOnto = session2OntoTracker.get(ontology);
			listOnto.add(sessionId);
		}else{
			List<String> listOnto = new Vector<String>();
			listOnto.add(sessionId);
			session2OntoTracker.put(ontology, listOnto);
		}
	}
}
