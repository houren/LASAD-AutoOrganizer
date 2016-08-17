package lasad.processors.specific;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import lasad.controller.ManagementController;
import lasad.entity.Map;
import lasad.entity.Ontology;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionListFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2OntologiesFE;
import lasad.shared.dfki.authoring.frontenddata.Agents2SessionsFE;
import lasad.shared.dfki.authoring.frontenddata.OntologiesFE;
import lasad.shared.dfki.authoring.frontenddata.SessionStatusMapFE;
import lasad.shared.dfki.authoring.frontenddata.SessionsFE;
import lasad.shared.dfki.meta.util.ConstantsFE;


/**
 * This class handles all Feedback Authoring related actions distributed by the ActionProcessor
 * 
 * @author Anahuac Valero
 */
public class FeedbackAuthoringActionProcessor extends AbstractActionObserver implements ActionObserver {
	
	private FeedbackAuthoringProcessorStatus faStatusKeeper;
	
	public FeedbackAuthoringActionProcessor(){
		super();
		faStatusKeeper = new FeedbackAuthoringProcessorStatus();
	}
	
	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (a.getCategory().equals(Categories.FeedbackAuthoring)) {
			if (u != null) {
				switch (a.getCmd()) {
					case ListOntologies:// Check
						processListOntologies(a, u);
						returnValue = true;
						break;
					case GetOntologyDetails:// Check
						processGetOntologyDetails(a, u);
						returnValue = true;
						break;
					case ListMap:// Check
						processListMap(a, u);
						returnValue = true;
						break;
					case MapDetails:// Check
						processMapDetails(a, u);
						returnValue = true;
						break;
					case ListAgentsInfo:// Check
						processListAgentsInfo(a, u);
						returnValue = true;
						break;
					case ListAgentsToOntologies:// Check
						processListAgentsToOntologies(a, u);
						returnValue = true;
						break;
					case ListAgentsToSessions:// Check
						processListAgentsToSessions(a, u);
						returnValue = true;
						break;
					case ListSessionStatus:// Check
						processListSessionStatus(a, u);
						returnValue = true;
						break;
					case AddOrUpdateAgent:
						processAddOrUpdateAgent(a,u);
						returnValue = true;
						break;
					case DeleteAgent:
						processDeleteAgent(a,u);
						returnValue = true;
						break;
					case AddAgentToOntology:
						processAddAgentToOntology(a,u);
						returnValue = true;
						break;
					case AddAgentToSession:
						processAddAgentToSession(a,u);
						returnValue = true;
						break;
					case RemoveAgentFromOntology:
						processRemoveAgentFromOntology(a,u);
						returnValue = true;
						break;
					case RemoveAgentFromSession:
						processRemoveAgentFromSession(a,u);
						returnValue = true;
						break;
					case StartSession:
						processStartSession(a,u);
						returnValue = true;
						break;
					case StopSession:
						processStopSession(a,u);
						returnValue = true;
						break;
					case CompileAgent:
						processCompileAgent(a,u);
						returnValue = true;
						break;
					case AgentMappingsDeleted:
						processAgentMappingsDeleted(a,u);
						returnValue = true;
						break;
					case ComponentRuntimeStatusChanged:
						processComponentRuntimeStatusChanged(a,u);
						returnValue = true;
						break;
					case SessionRuntimeStatusChanged:
						processSessionRuntimeStatusChanged(a,u);
						returnValue = true;
						break;
					case GetFreshAgentId:
						processGetFreshAgentId(a,u);
						returnValue = true;
						break;
					case GetFreshPatternId:
						processGetFreshPatternId(a,u);
						returnValue = true;
						break;
					default:
						Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] processAction: Invalid Command: " + a.getCmd());
						break;
				}
			} else {
				Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] processAction: User is null!!! Action ignored: " + a.toString());
			}
		}
		return returnValue;
	}
	
	/**
	 * Creates an ActionPackage and distributes it to all users that need to be informed, currently Developer,
	 * No new revision is created, thus there's no database entry for this action
	 * 
	 * @param a The action to be distributed
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void distributeToUsersWithoutSaving(Action a, User u) {
		
		ActionPackage ap = ActionPackage.wrapAction(a);
		synchronized (ActionProcessor.sessionToUserLock) {
			Logger.debugLog("Enable lock sessionToUserLock from FeedbackAuthoringActionProcessor.distributeToUsersWithoutSaving");
			Set<String> sessionSet = myServer.currentState.sessionToUser.keySet();
			for(String userSession : sessionSet){
				User user = myServer.currentState.sessionToUser.get(userSession);
				if (user != null){
					String userRole = user.getRole();
					for(String validRole : ConstantsFE.getFeedbackAuthoringValidRoles()){
						if (validRole.equalsIgnoreCase(userRole)){    // validation also used in LASAD_Client.java L455
							Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] distribute to -userNickname:" + user.getNickname() + " -sessionID:" + user.getSessionID());
							ManagementController.addToUsersActionQueue(ap, user.getSessionID());
							break;
						}
					}
				}
			}
			Logger.debugLog("Disable lock sessionToUserLock from FeedbackAuthoringActionProcessor.distributeToUsersWithoutSaving");
		}
	}
	
	/**
	 * Processes a ListOntologies action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListOntologies(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: ListOntologies");
		Vector<String> ontologyList = Ontology.getAllOntologyNames();
		ActionPackage ap = new ActionPackage();
		
		Action action = new Action(Commands.ListOntologies, Categories.FeedbackAuthoring);
		OntologiesFE ontFE = new OntologiesFE();
		if(ontologyList != null){
			for(String ont: ontologyList){
				ontFE.add(ont);
			}
		}
//		for (int i = 0; i < ontologyList.size(); i++) {
//			
//			action.addParameter(ParameterTypes.OntologyName, ontologyList.get(i));
//		}
		action.setObjectFE(ontFE);
		ap.addAction(action);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}
	
	/**
	 * Processes a GetOntologyDetails action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processGetOntologyDetails(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Get Ontology Details");
		
		String ontologyName = a.getParameterValue(ParameterTypes.OntologyName);
		int ontologyID = Ontology.getOntologyID(ontologyName);
		String ontologyXML = Ontology.getOntologyXML(ontologyID);
		
		ActionPackage ap = new ActionPackage();		
		Action action = new Action(Commands.GetOntologyDetails, Categories.FeedbackAuthoring);
		action.addParameter(ParameterTypes.OntologyName, ontologyName);
		action.addParameter(ParameterTypes.Ontology, ontologyXML);
		ap.addAction(action);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		//distributeToUsersWithoutSaving(action, u);
	}
	
	/**
	 * Processes a ListMap action
	 * 		Gets the id, name and ontology for each map.
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListMap(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: List Map");
		ActionPackage ap = new ActionPackage();

		Vector<Map> maps = Map.getMapList();

		Action action = new Action(Commands.ListMap, Categories.FeedbackAuthoring);
		SessionsFE sesFE = new SessionsFE();
		for (Map m : maps) {
			Template t = Map.getTemplate(m.getId());
			sesFE.add(m.getId()+"", m.getName(), t.getOntologyNameFromDB());
		}
		action.setObjectFE(sesFE);
		faStatusKeeper.setSessionsFE(sesFE);
		ap.addAction(action);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}
	
	/**
	 * Processes a MapDetails action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processMapDetails(Action a, User u) {
		//TODO Incomplete
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Map Details");
	}
	
	/**
	 * Processes a ListAgentsInfo action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListAgentsInfo(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: List Agents Info");
		
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				AgentDescriptionListFE value = (AgentDescriptionListFE)a.getObjectFE();
				faStatusKeeper.setAgentDescriptionList(value);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//the LASAD-client is requesting the data, send current copy. 
			ActionPackage ap = new ActionPackage();
			Action action = new Action(Commands.ListAgentsInfo, Categories.FeedbackAuthoring);
			AgentDescriptionListFE value = faStatusKeeper.getAgentDescriptionList();
			action.setObjectFE(value);
			ap.addAction(action);
			ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		}

	}
	
	/**
	 * Processes a ListAgentsToOntologies action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListAgentsToOntologies(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: List Agents To Ontologies");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				Agents2OntologiesFE value = (Agents2OntologiesFE)a.getObjectFE();
				faStatusKeeper.setAgentsToOntologies(value);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//the LASAD-client is requesting the data, send current copy.
			ActionPackage ap = new ActionPackage();
			Action action = new Action(Commands.ListAgentsToOntologies, Categories.FeedbackAuthoring);
			Agents2OntologiesFE value = faStatusKeeper.getAgentsToOntologies();
			action.setObjectFE(value);
			ap.addAction(action);
	        ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		}
	}
	
	/**
	 * Processes a ListAgentsToSessions action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListAgentsToSessions(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: List Agents To Sessions");
		//The existence of the "changed" parameter is used to determine the origin of the Action 
		// if exist : Action comes from the Feedback Engine, it�s an update
		// otherwise : comes from the LASAD-client, it�s a request
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				Agents2SessionsFE value = (Agents2SessionsFE)a.getObjectFE();
				faStatusKeeper.setAgentsToSessions(value);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//the LASAD-client is requesting the data, send current copy.
			ActionPackage ap = new ActionPackage();
			Action action = new Action(Commands.ListAgentsToSessions, Categories.FeedbackAuthoring);
			Agents2SessionsFE value = faStatusKeeper.getAgentsToSessions();
			action.setObjectFE(value);
			ap.addAction(action);
	        ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		}
	}
	
	/**
	 * Processes a ListSessionStatus action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processListSessionStatus(Action a, User u) {
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: List Sessions Status");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				SessionStatusMapFE value = (SessionStatusMapFE)a.getObjectFE();
				faStatusKeeper.setListSessionStatus(value);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//the LASAD-client is requesting the data, send current copy.
			ActionPackage ap = new ActionPackage();
			Action action = new Action(Commands.ListSessionStatus, Categories.FeedbackAuthoring);
			SessionStatusMapFE value = faStatusKeeper.getListSessionStatus();
			action.setObjectFE(value);
			ap.addAction(action);
	        ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		}
	}

	/**
	 * Processes a AddOrUpdateAgent action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processAddOrUpdateAgent(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Add or update Agent");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				AgentDescriptionFE agentDescriptionFE = (AgentDescriptionFE)a.getObjectFE();
				faStatusKeeper.addUpdateAgentFromAgentDescriptionList(agentDescriptionFE);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a DeleteAgent action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processDeleteAgent(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Delete Agent");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				//the mappings in ontologies and sessions is removed with the AgentMappingDeleted action
				faStatusKeeper.deleteAgentFromAgentDescriptionList(agentId);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
		
	}
	/**
	 * Processes a AddAgentToOntology action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	public void processAddAgentToOntology(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Add Agent to Ontology");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				String ontology = a.getParameterValue(ParameterTypes.Ontology);
				
				Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] AgentId:" + agentId + " ontology:" + ontology);
				
				faStatusKeeper.addAgent2AgentsToOntologiesFE(agentId, ontology);
				faStatusKeeper.addNewAgentToOntologyRuntimeStatus(agentId, ontology);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a AddAgentToSession action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	public void processAddAgentToSession(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Add Agent to Session");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				String sessionId = a.getParameterValue(ParameterTypes.SessionId);
				
				Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] AgentId:" + agentId + " sessionId:" + sessionId);
				
				faStatusKeeper.addAgent2AgentsToSessionsFE(agentId, sessionId);
				faStatusKeeper.addNewAgentToSessionRuntimeStatus(agentId, sessionId);
				
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a RemoveAgentFromOntology action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processRemoveAgentFromOntology(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Remove Agent from Ontology");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				String ontology = a.getParameterValue(ParameterTypes.Ontology);
				
				Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] AgentId:" + agentId + " ontology:" + ontology);
				
				faStatusKeeper.removeAgent2AgentsToOntologiesFE(agentId, ontology);
				faStatusKeeper.removeAgentFromOntologyRuntimeStatus(agentId, ontology);
				
//				faStatusKeeper.printAgents2OntologiesFE();
//				faStatusKeeper.printSessionStatusMapFE();
				
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a RemoveAgentFromSession action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processRemoveAgentFromSession(Action a, User u){
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			boolean changed = Boolean.parseBoolean(changedStr);
			if(changed){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				String sessionId = a.getParameterValue(ParameterTypes.SessionId);
				
				Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] AgentId:" + agentId + " sessionId:" + sessionId);
				
				faStatusKeeper.removeAgent2AgentsToSessionsFE(agentId, sessionId);
				faStatusKeeper.removeAgentFromSessionRuntimeStatus(agentId, sessionId);
				
//				faStatusKeeper.printAgents2SessionsFE();
//				faStatusKeeper.printSessionStatusMapFE();
				
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a StartSession action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processStartSession(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Start Session");
		ActionPackage ap = new ActionPackage();
		ap.addAction(a);
		User dfkiUser = getDFKIUser();
		//fwd request to Feedback Engine.
		ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
	}
	/**
	 * Processes a StopSession action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processStopSession(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Stop Session");
		ActionPackage ap = new ActionPackage();
		ap.addAction(a);
		User dfkiUser = getDFKIUser();
		//fwd request to Feedback Engine.
		ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
	}
	/**
	 * Processes a CompileAgent action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processCompileAgent(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Compile Agent");
		//The existence of the "changed" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null){
			if(Boolean.parseBoolean(changedStr) && a.getParameterValue(ParameterTypes.Successful) != null){
				//update local version and forward the info to the LASAD-client
				String agentId = a.getParameterValue(ParameterTypes.AgentId);
				//TODO method is not working
				faStatusKeeper.compileAgentFromAgentDescriptionList(agentId);
				distributeToUsersWithoutSaving(a, u);
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	/**
	 * Processes a AgentMappingDeleted action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processAgentMappingsDeleted(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Agent Mapping Deleted");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr) == true){
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			//User dfkiUser = getDFKIUser();
			String agentId = a.getParameterValue(ParameterTypes.AgentId);
			List<String> ontologyList = a.getParameterValues(ParameterTypes.Ontology); 
			List<String> sessionIdList = a.getParameterValues(ParameterTypes.SessionId);
			if(ontologyList != null){
				for(String ont:ontologyList){
					faStatusKeeper.removeAgent2AgentsToOntologiesFE(agentId, ont);
				}
			}
			if(sessionIdList != null){
				for(String ses:sessionIdList){
					faStatusKeeper.removeAgent2AgentsToSessionsFE(agentId, ses);
				}
			}
			
			distributeToUsersWithoutSaving(a, u);
		}
	}
	/**
	 * Processes a ComponentRuntimeStatusChanged action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processComponentRuntimeStatusChanged(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Component Runtime Status Changed");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr) == true){
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			//User dfkiUser = getDFKIUser();
			//String componentId = a.getParameterValue(ParameterTypes.ChangedComponentID);
			//String oldStatus = a.getParameterValue(ParameterTypes.OldStatus);
			//String newStatus = a.getParameterValue(ParameterTypes.NewStatus);
			//TODO update faStatusKeeper
			//faStatusKeeper.setListSessionStatus(value);
			distributeToUsersWithoutSaving(a, u);
		}
	}
	/**
	 * Processes a SessionRuntimeStatusChanged action
	 * 
	 * @param a The request action
	 * @param u The user who triggered action @{a}
	 * @author Anahuac Valero
	 */
	private void processSessionRuntimeStatusChanged(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Session Runtime Status Changed");
		String changedStr = a.getParameterValue(ParameterTypes.Changed);
		if (changedStr != null && Boolean.parseBoolean(changedStr) == true){
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			String sessionId = a.getParameterValue(ParameterTypes.SessionId);
			//String oldStatus = a.getParameterValue(ParameterTypes.OldStatus);
			String newStatus = a.getParameterValue(ParameterTypes.NewStatus);
			faStatusKeeper.updateSessionRuntimeStatus(sessionId, newStatus);
			
			distributeToUsersWithoutSaving(a, u);
		}
	}
	
	private void processGetFreshAgentId(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Get Fresh Agent Id");
		//The existence of the "Successful" parameter is used to determine the sender of the Action 
		// if exist : Action comes from the Feedback Engine
		// otherwise : comes from the LASAD-client
		String successfulStr = a.getParameterValue(ParameterTypes.Successful);
		if (successfulStr != null){
			boolean successful = Boolean.parseBoolean(successfulStr);
			if(successful){
				ActionPackage ap = new ActionPackage();
				Action action = new Action(Commands.GetFreshAgentId, Categories.FeedbackAuthoring);
				ap.addAction(action);
				distributeToUsersWithoutSaving(a, u);
				//ManagementController.addToUsersActionQueue(ap, u.getSessionID());
			}
		}
		else{
			//fwd request to Feedback Engine.
			ActionPackage ap = new ActionPackage();
			ap.addAction(a);
			User dfkiUser = getDFKIUser();
			ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
		}
	}
	
	private void processGetFreshPatternId(Action a, User u){
		Logger.debugLog("[lasad.processors.specific][FeedbackAuthoringActionProcessor] Processing request: Get Fresh Pattern Id");
		//The existence of the "Successful" parameter is used to determine the sender of the Action 
				// if exist : Action comes from the Feedback Engine
				// otherwise : comes from the LASAD-client
				String successfulStr = a.getParameterValue(ParameterTypes.Successful);
				if (successfulStr != null){
					boolean successful = Boolean.parseBoolean(successfulStr);
					if(successful){
						ActionPackage ap = new ActionPackage();
						Action action = new Action(Commands.GetFreshPatternId, Categories.FeedbackAuthoring);
						ap.addAction(action);
						distributeToUsersWithoutSaving(a, u);
						//ManagementController.addToUsersActionQueue(ap, u.getSessionID());
					}
				}
				else{
					//fwd request to Feedback Engine.
					ActionPackage ap = new ActionPackage();
					ap.addAction(a);
					User dfkiUser = getDFKIUser();
					ManagementController.addToUsersActionQueue(ap, dfkiUser.getSessionID());
				}
	}
	
	
	private User getDFKIUser(){
		String sessionID = myServer.currentState.userIdToSession.get(ConstantsFE.FEEDBACK_ENGINE_USER);
		User dfkiUser = myServer.currentState.sessionToUser.get(sessionID);
		return dfkiUser;
	}
}
