package lasad.helper;

import java.util.Vector;

import lasad.Server;
import lasad.State;

import lasad.entity.Map;
import lasad.entity.Ontology;
import lasad.entity.Revision;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.entity.Element;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ActionPackageFactory {

	public static ActionPackage forcedClose() {
		
		Action a = new Action(Commands.ForcedLogout, Categories.Auth);
		//Action a = new Action("FORCED-LOGOUT", "AUTH");
		a.addParameter(ParameterTypes.Status, "OK");

		return createActionPackage(a);
	}

	public static ActionPackage logOut() {

		Action a = new Action(Commands.Logout, Categories.Auth);
//		Action a = new Action("LOGOUT", "AUTH");
		a.addParameter(ParameterTypes.Status, "OK");
		return createActionPackage(a);
	}

	public static ActionPackage leaveMap(int mapID) {

		Action a = new Action(Commands.Leave, Categories.Management);
//		Action a = new Action("LEAVE", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapId, String.valueOf(mapID));

		return createActionPackage(a);
	}

	public static ActionPackage error(String errorMessage) {

		Action a = new Action(Commands.Error, Categories.Error);
//		Action a = new Action("ERROR", "ERROR");
		a.addParameter(ParameterTypes.Message, errorMessage);

		return createActionPackage(a);
	}

	public static ActionPackage confirmLogin(String nickname, String role) {

		Action a = new Action(Commands.Login, Categories.Auth);
//		Action a = new Action("LOGIN", "AUTH");
		a.addParameter(ParameterTypes.Status, "OK");
		a.addParameter(ParameterTypes.UserName, nickname);
		a.addParameter(ParameterTypes.Role, role);
		a.addParameter(ParameterTypes.ServerVersion, Server.getInstance().conf.parameters.get("Server-Version"));
		
		return createActionPackage(a);
	}

	public static ActionPackage failedLogin(String errorMessage) {

		Action a = new Action(Commands.LoginFailed, Categories.Auth);
//		Action a = new Action("LOGIN-FAILED", "AUTH");
		a.addParameter(ParameterTypes.Message, errorMessage);
		return createActionPackage(a);
	}

	public static ActionPackage heartbeatRequest() {

		Action a = new Action(Commands.HeartbeatRequest, Categories.Session);
//		Action a = new Action("HEARTBEATREQUEST", "SESSION");

		return createActionPackage(a);
	}

	public static ActionPackage listMaps(State state, int userID) {
		ActionPackage p = new ActionPackage();

		Vector<Map> maps = Map.getMapList();

		String userRole = User.getRoleByID(userID);

		// Restrictions do not count for teachers and developers
		boolean ignoreRestriction = false;
		if ("Teacher".equals(userRole) || "Developer".equals(userRole)) {
			ignoreRestriction = true;
		}

		for (Map m : maps) {

			if (!ignoreRestriction) {
				
				// Is the current map restricted to specific users?
				if (m.getRestricted_to_user_id().size() > 0) {
					
					boolean userAllowed = false;
					for (int i = 0; i < m.getRestricted_to_user_id().size(); i++) {
						if (m.getRestricted_to_user_id().get(i) == userID) {
							userAllowed = true;
							break;
						}
					}
					if (!userAllowed) {
						continue;
					}
				}
			}

			Template t = Map.getTemplate(m.getId());

			Action action = new Action(Commands.ListMap, Categories.Map);
//			Action action = new Action("LISTMAP", "MAP");
			action.addParameter(ParameterTypes.MapId, m.getId() + "");
			action.addParameter(ParameterTypes.MapName, m.getName());
			action.addParameter(ParameterTypes.CreatorId, m.getCreator_user_id() + "");
			action.addParameter(ParameterTypes.CreatorName, User.getName(m.getCreator_user_id()));
			action.addParameter(ParameterTypes.TemplateTitle, t.getTitle());
			action.addParameter(ParameterTypes.OntologyName, t.getOntologyNameFromDB());
			action.addParameter(ParameterTypes.TemplateName, t.getName());
			action.addParameter(ParameterTypes.Created, Revision.getTimeOfMapRevision(m.getId(), 0) + "");
			action.addParameter(ParameterTypes.Modified, Revision.getTimeOfMapRevision(m.getId(), 1) + "");
			action.addParameter(ParameterTypes.ActiveUsers, state.mapUsers.get(m.getId()).size() + "");

			p.addAction(action);
		}

		if (p.getActions().size() == 0) {
			Action endAction = new Action(Commands.ListMap, Categories.Map);
//			Action endAction = new Action("LISTMAP", "MAP");
			endAction.addParameter(ParameterTypes.Status, "END");
			p.addAction(endAction);
		} else {
			p.getActions().lastElement().addParameter(ParameterTypes.Status, "END");
		}

		return p;
	}

//	public Action deleteReplayElement(int mapElementID, String nickname, long time, int mapID) {
//		// Build delete action for replay-client
//		Action a = new Action("DELETE-ELEMENT", "REPLAY");
//		a.addParameter("ID", mapElementID + "");
//		a.addParameter("REPLAY-TIME", time + "");
//		a.addParameter("USERNAME", nickname);
//		a.addParameter("MAP-ID", mapID + "");
//		return a;
//	}

	public static ActionPackage errorReplay(int mapID, String mapName, int errorIndex) {
		// Create ActionPackage
		Action a;
		if (errorIndex == -1) {
			// No map exists
			a= new Action (Commands.NoMap, Categories.Replay);
//			a = new Action("NO-MAP", "REPLAY");
		} else if (errorIndex == -2) {
			// User is not logged in
			a = new Action(Commands.NoUser, Categories.Replay);
//			a = new Action("NO-USER", "REPLAY");
		} else if (errorIndex == -3) {
			// Map is empty - only one revision (create map) exists
			a = new Action(Commands.EmptyMap, Categories.Replay);
//			a = new Action("EMPTY-MAP", "REPLAY");
		} else {
			// No specific error
			a = new Action(Commands.Error, Categories.Replay);
//			a = new Action("ERROR", "REPLAY");
		}
		// Add Map-Parameters
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.MapName, mapName);
		// Add Action
		return createActionPackage(a);
	}

	public static ActionPackage startInitReplay(int mapID) {
		// Create first action package to initialize the replay-client
		
		Action a = new Action(Commands.InitStart, Categories.Replay);
//		Action a = new Action("INIT-START", "REPLAY");
		// Add Map-Parameters
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.MapName, Map.getMapName(mapID));
		// Add Action
		return createActionPackage(a);
	}

	public static ActionPackage endInitReplay(ActionPackage p, int mapID, long totalSec, State state) {
		Map m = state.maps.get(mapID);
		Template mTemplate = Map.getTemplate(mapID);

		// Create final action to initialize the replay-client
		Action a = new Action(Commands.InitEnd, Categories.Replay);
//		Action a = new Action("INIT-END", "REPLAY");
		// Add Map-Parameters
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.MapName, m.getName());
		a.addParameter(ParameterTypes.OntologyName, mTemplate.getOntologyNameFromDB());
		a.addParameter(ParameterTypes.TemplateName, mTemplate.getName());
		a.addParameter(ParameterTypes.Ontology, Ontology.getOntologyXML(mTemplate.getOntologyID()));
		a.addParameter(ParameterTypes.Template, Map.getTemplate(mapID).getXml());
		a.addParameter(ParameterTypes.TotalSec, totalSec + "");

		// Add Action
		p.addAction(a);
		return p;
	}

//	public Action createReplayElement(int mapElementID, String type, String nickname, long time, int mapID) {
//		// Build create action for replay-client
//		Action a = new Action("CREATE-ELEMENT", "REPLAY");
//		a.addParameter("ID", mapElementID + "");
//		a.addParameter("TYPE", type);
//		a.addParameter("REPLAY-TIME", time + "");
//		a.addParameter("USERNAME", nickname);
//		a.addParameter("MAP-ID", mapID + "");
//		return a;
//	}

//	public Action updateReplayElement(int mapElementID, String nickname, long time, int mapID) {
//		// Build update action for replay-client
//		Action a = new Action("UPDATE-ELEMENT", "REPLAY");
//		a.addParameter("ID", mapElementID + "");
//		a.addParameter("REPLAY-TIME", time + "");
//		a.addParameter("USERNAME", nickname);
//		a.addParameter("MAP-ID", mapID + "");
//		return a;
//	}

	public static ActionPackage joinMap(int mapID, State state) {

		Map m = state.maps.get(mapID);
		Template mTemplate = Map.getTemplate(mapID);
		
		Action a = new Action(Commands.Join, Categories.Map);
//		Action a = new Action("JOIN", "MAP");

		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.MapName, m.getName());
		a.addParameter(ParameterTypes.OntologyName, mTemplate.getOntologyNameFromDB());
		a.addParameter(ParameterTypes.TemplateName, mTemplate.getName());
		a.addParameter(ParameterTypes.Ontology, Ontology.getOntologyXML(mTemplate.getOntologyID()));
		a.addParameter(ParameterTypes.Template, Map.getTemplate(mapID).getXml());
		
		String imageUrl = Map.getImageUrl(mapID);
		if(imageUrl != null)
		{
			a.addParameter(ParameterTypes.BackgroundImageURL,imageUrl);
		}

		return createActionPackage(a);
	}

	/**
	 * Notification that a new user joined the map
	 * 
	 * @param mapID
	 * @param nickname
	 * @return
	 */
	public static ActionPackage userEventUserJoin(int mapID, String nickname) {

		Action a = new Action(Commands.UserJoin, Categories.UserEvent);
//		Action a = new Action("USERJOIN", "USEREVENT");
		a.addParameter(ParameterTypes.UserName, nickname);
		a.addParameter(ParameterTypes.MapId, mapID + "");

		return createActionPackage(a);
	}

	/**
	 * Notification that a user has left the map
	 * 
	 * @param mapID
	 * @param nickname
	 * @return
	 */
	public static ActionPackage userEventUserLeave(int mapID, String nickname) {

		Action a = new Action(Commands.UserLeave, Categories.UserEvent);
//		Action a = new Action("USERLEAVE", "USEREVENT");
		a.addParameter(ParameterTypes.UserName, nickname);
		a.addParameter(ParameterTypes.MapId, mapID + "");

		return createActionPackage(a);
	}

	/**
	 * Creates a list that contains all active users on the map
	 * 
	 * @param mapID
	 * @param state
	 * @return
	 */
	public static ActionPackage userEventUserList(int mapID, State state) {

		Action a = new Action(Commands.UserList, Categories.UserEvent);
//		Action a = new Action("USERLIST", "USEREVENT");

		synchronized (ActionProcessor.mapUsersLock) {
			for (User u : state.mapUsers.get(mapID)) {
				a.addParameter(ParameterTypes.UserName, u.getNickname()+" ("+u.getRole()+")");
			}
		}

		a.addParameter(ParameterTypes.MapId, mapID + "");
		return createActionPackage(a);
	}

	public static ActionPackage getMapDetails(int mapID, State state) {
		Map m = state.maps.get(mapID);
		Template mTemplate = Map.getTemplate(mapID);

		Action action = new Action(Commands.MapDetails, Categories.Map);
//		Action action = new Action("MAPDETAILS", "MAP");
		action.addParameter(ParameterTypes.Status, "OK");
		action.addParameter(ParameterTypes.MapId, mapID + "");
		action.addParameter(ParameterTypes.MapName, m.getName());
		action.addParameter(ParameterTypes.CreatorName, User.getName(m.getCreator_user_id()));
		action.addParameter(ParameterTypes.CreatorId, m.getCreator_user_id() + "");
		action.addParameter(ParameterTypes.OntologyName, mTemplate.getOntologyNameFromDB());
		action.addParameter(ParameterTypes.TemplateName, mTemplate.getName());
		action.addParameter(ParameterTypes.TemplateTitle, mTemplate.getTitle());
		action.addParameter(ParameterTypes.TemplateXML, mTemplate.getXml());
		action.addParameter(ParameterTypes.ActiveUsers, state.mapUsers.get(mapID).size() + "");
		action.addParameter(ParameterTypes.TemplateMaxUsers, Template.getTemplateMaxUsers(mTemplate.getXml()) + "");

		return createActionPackage(action);
	}

	public static ActionPackage getOntology(int mapID) {

		Template mTemplate = Map.getTemplate(mapID);

		Action action = new Action(Commands.Ontology, Categories.Map);
//		Action action = new Action("ONTOLOGY", "MAP");
		action.addParameter(ParameterTypes.MapId, mapID + "");
		action.addParameter(ParameterTypes.Ontology, Ontology.getOntologyXML(mTemplate.getOntologyID()));

		return createActionPackage(action);
	}

	public static ActionPackage deleteElement(int mapID, int elementID, String username) {

		Action a = new Action(Commands.DeleteElement, Categories.Map);
//		Action a = new Action("DELETE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.Id, elementID + "");
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.Type, Element.getElementType(elementID, mapID));
		return createActionPackage(a);
	}

	public static ActionPackage getAllOntologiesAndTemplates() {
		ActionPackage p = new ActionPackage();

		for (Ontology o : Ontology.getOntologyList()) {
			for (Template t : Template.getAllTemplatesWithOntology(o.getId())) {
				Action a = new Action(Commands.TemplateListElement, Categories.Map);
//				Action a = new Action("TEMPLATELISTELEMENT", "MAP");
				a.addParameter(ParameterTypes.OntologyName, o.getName());
				a.addParameter(ParameterTypes.TemplateName, t.getName());
				a.addParameter(ParameterTypes.TemplateTitle, t.getTitle());
				p.addAction(a);
			}
		}
		return p;
	}

	public static ActionPackage getMapsAndTemplates() {
		ActionPackage p = new ActionPackage();

		// TODO Optimize so that templates with a map will not be send to client
		// empty in the first step

		// Add templates without map first to catch all templates, even those
		// which do not have any map instance
		Vector<Template> templates = Template.getAllTemplates();
		for (int i = 0; i < templates.size(); i++) {
			Template t = templates.get(i);
			
			Action a = new Action(Commands.AddMapToList, Categories.Authoring);
//			Action a = new Action("ADD-MAP-TO-LIST", "AUTHORING");
			a.addParameter(ParameterTypes.Template, t.getName());
			a.addParameter(ParameterTypes.MapId, i + ""); // In this case, it is NOT the
			// internal map id!
			a.addParameter(ParameterTypes.MapMaxId, templates.size() - 1 + "");
			p.addAction(a);
		}

		// Add all concrete maps
		Vector<Map> mapList = Map.getMapList();
		for (int i = 0; i < mapList.size(); i++) {
			Map m = mapList.get(i);

			Action a = new Action(Commands.AddMapToList, Categories.Authoring);
//			Action a = new Action("ADD-MAP-TO-LIST", "AUTHORING");
			a.addParameter(ParameterTypes.MapName, m.getName());
			a.addParameter(ParameterTypes.Template, Template.getTemplateName(m.getTemplate_id()));
			a.addParameter(ParameterTypes.MapId, i + ""); // In this case, it is NOT the
			// internal map id!
			a.addParameter(ParameterTypes.MapMaxId, mapList.size() - 1 + "");
			p.addAction(a);
		}

		return p;
	}

	public static Action joinMap(int mapID) {
		Action a = new Action(Commands.Join, Categories.Management);
//		Action a = new Action("JOIN", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapId, mapID + "");

		return a;
	}

	public static Action getLeaveMapAction(int mapID) {

		Action a = new Action(Commands.Leave, Categories.Management);
//		Action a = new Action("LEAVE", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapId, mapID + "");

		return a;
	}

	public static Action getUnlockElementAction(User u, int mapID, int lockedElementID) {

		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.UserName, u.getNickname());
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.Id, lockedElementID + "");
		a.addParameter(ParameterTypes.Status, "UNLOCK");

		return a;
	}

	public static ActionPackage getLogOutActionPackage(String sessionID, String username) {
		ActionPackage p = new ActionPackage();
		p.addParameter(ParameterTypes.SessionId, sessionID);

		Action a = new Action(Commands.Logout, Categories.Management);
//		Action a = new Action("LOGOUT", "MANAGEMENT");
		a.addParameter(ParameterTypes.UserName, username);

		p.addAction(a);
		return p;
	}

	public static ActionPackage getTemplateListActionPackage(boolean authoring) {
		Vector<String> o = Ontology.getAllOntologyNames();

		ActionPackage p = new ActionPackage();

		if (authoring) {
			for (int i = 0; i < o.size(); i++) {
				String ontology = o.get(i);

				// Create ontology folders without templates
				Action a = new Action(Commands.AddTemplateToList, Categories.Authoring);
//				a = new Action("ADD-TEMPLATE-TO-LIST", "AUTHORING");
				a.addParameter(ParameterTypes.Ontology, ontology);
				a.addParameter(ParameterTypes.TemplateId, i + "");
				a.addParameter(ParameterTypes.TemplateMaxId, o.size() - 1 + "");
				p.addAction(a);
			}
		}

		// Create templates to fill the ontology folders
		Vector<Template> t = Template.getAllTemplates();
		for (int i = 0; i < t.size(); i++) {
			Template temp = t.get(i);

			Action a = null;
			if (authoring) {
				a = new Action(Commands.AddTemplateToList, Categories.Authoring);
//				a = new Action("ADD-TEMPLATE-TO-LIST", "AUTHORING");
			} else {
				a = new Action(Commands.AddTemplateToList, Categories.Management);
//				a = new Action("ADD-TEMPLATE-TO-LIST", "MANAGEMENT");
			}
			a.addParameter(ParameterTypes.Ontology, temp.getOntologyName());
			a.addParameter(ParameterTypes.Template, temp.getName());
			a.addParameter(ParameterTypes.TemplateId, i + "");
			a.addParameter(ParameterTypes.TemplateMaxId, t.size() - 1 + "");

			p.addAction(a);
		}

		return p;
	}

	public static ActionPackage getNewTemplateAction(Template t) {

		Action a = new Action(Commands.AddTemplateToList, Categories.Authoring);
//		Action a = new Action("ADD-TEMPLATE-TO-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.Ontology, t.getOntologyNameFromDB());
		a.addParameter(ParameterTypes.Template, t.getName());

		String templateID = t.getId() + "";
		a.addParameter(ParameterTypes.TemplateId, templateID);
		a.addParameter(ParameterTypes.TemplateMaxId, templateID);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.TemplateCreated, Categories.Notify);
//		a = new Action("TEMPLATE-CREATED", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "Template (" + t.getName() + ") successfully created.");

		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getRemoveTemplateAction(String templateName, String ontologyName) {

		Action a = new Action(Commands.DeleteTemplateFromList, Categories.Authoring);
//		Action a = new Action("DELETE-TEMPLATE-FROM-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.Ontology, ontologyName);
		a.addParameter(ParameterTypes.Template, templateName);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.Info, Categories.Notify);
//		a = new Action("INFO", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "Template (" + templateName + ") successfully removed.");
		p.addAction(confirmationAction);

		return p;
	}
	
	public static ActionPackage getKickOutAction() {

		Action a = new Action(Commands.ErrorInfo, Categories.Notify);
//		Action a = new Action("ERROR-INFO", "NOTIFY");
		a.addParameter(ParameterTypes.UserKickOut, "true");
		a.addParameter(ParameterTypes.Message, "Another user has logged in using your account," 
						+ " thus your session has been closed.");
		return createActionPackage(a);
	}

	public static ActionPackage getInfoAction(String info) {

		Action a = new Action(Commands.OntologyCreated, Categories.Notify);
//		Action a = new Action("ONTOLOGY-CREATED", "NOTIFY");
		a.addParameter(ParameterTypes.Message, info);
		return createActionPackage(a);
	}

	public static ActionPackage getUserListActionPackage(Vector<User> u) {
		ActionPackage p = new ActionPackage();

		for (int i = 0; i < u.size(); i++) {
			User user = u.get(i);

			Action a = new Action(Commands.AddUserToList, Categories.Authoring);
//			Action a = new Action("ADD-USER-TO-LIST", "AUTHORING");
			a.addParameter(ParameterTypes.UserName, user.getNickname());
			a.addParameter(ParameterTypes.Role, user.getRole());
			a.addParameter(ParameterTypes.UserId, i + "");
			a.addParameter(ParameterTypes.UserMaxId, u.size() - 1 + "");

			p.addAction(a);
		}

		return p;
	}

	public static ActionPackage getNewUserAction(User user) {

		Action a = new Action(Commands.AddUserToList, Categories.Authoring);
//		Action a = new Action("ADD-USER-TO-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.UserName, user.getNickname());
		a.addParameter(ParameterTypes.Role, user.getRole());

		String userID = user.getUserID() + "";
		a.addParameter(ParameterTypes.UserId, userID);
		a.addParameter(ParameterTypes.UserMaxId, userID);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.UserCreated, Categories.Notify);
//		a = new Action("USER-CREATED", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "User (" + user.getNickname() + ") successfully created.");

		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getRemoveUserAction(String username, String role) {

		Action a = new Action(Commands.DeleteUserFromList, Categories.Authoring);
//		Action a = new Action("DELETE-USER-FROM-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.Role, role);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.Info, Categories.Notify);
//		a = new Action("INFO", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "User (" + username + ") successfully removed.");
		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getUserListWithoutRolesActionPackage(Vector<String> u) {

		Action a = new Action(Commands.ListUsers, Categories.Authoring);
//		Action a = new Action("LIST-USERS", "AUTHORING");

		for (int i = 0; i < u.size(); i++) {
			String user = u.get(i);
			a.addParameter(ParameterTypes.UserName, user);
		}

		return createActionPackage(a);
	}

	public static ActionPackage getNewMapAction(Map m) {

		Action a = new Action(Commands.AddMapToList, Categories.Authoring);
//		Action a = new Action("ADD-MAP-TO-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.MapName, m.getName());
		a.addParameter(ParameterTypes.Template, Template.getTemplateName(m.getTemplate_id()));
		a.addParameter(ParameterTypes.MapId, m.getId() + "");
		a.addParameter(ParameterTypes.MapMaxId, m.getId() + "");

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.SessionCreated, Categories.Notify);
//		a = new Action("SESSION-CREATED", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "A new session (" + m.getName() + " ID:" + m.getId()+ ") has been successfully created.");

		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getRemoveMapAction(String mapName, String templateName) {

		Action a = new Action(Commands.DeleteMapFromList, Categories.Authoring);
//		Action a = new Action("DELETE-MAP-FROM-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.MapName, mapName);
		a.addParameter(ParameterTypes.Template, templateName);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.Info, Categories.Notify);
//		a = new Action("INFO", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "Map (" + mapName + ") successfully removed.");
		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getRemoveOntologyAction(String ontologyName) {

		Action a = new Action(Commands.DeleteOntologyFromList, Categories.Authoring);
//		Action a = new Action("DELETE-ONTOLOGY-FROM-LIST", "AUTHORING");
		a.addParameter(ParameterTypes.OntologyName, ontologyName);

		ActionPackage p = createActionPackage(a);

		Action confirmationAction = new Action(Commands.Info, Categories.Notify);
//		a = new Action("INFO", "NOTIFY");
		confirmationAction.addParameter(ParameterTypes.Message, "Ontology (" + ontologyName + ") successfully removed.");
		p.addAction(confirmationAction);

		return p;
	}

	public static ActionPackage getOntologyList(Vector<String> ontologyList) {

		Action a = new Action(Commands.ListOntologies, Categories.Authoring);
//		Action a = new Action("LIST-ONTOLOGIES", "AUTHORING");

		for (int i = 0; i < ontologyList.size(); i++) {
			a.addParameter(ParameterTypes.OntologyName, ontologyList.get(i));
		}

		return createActionPackage(a);
	}

	public static ActionPackage getOntologyDetails(String ontologyName) {

		Action a = new Action(Commands.OntologyDetails, Categories.Authoring);
//		Action a = new Action("ONTOLOGY-DETAILS", "AUTHORING");
		a.addParameter(ParameterTypes.Ontology, Ontology.getOntologyXML(Ontology.getOntologyID(ontologyName)));
		return createActionPackage(a);
	}

	public static ActionPackage getTemplateDetails(String tName) {

		Template mTemplate = Template.getTemplate(tName);

		Action a = new Action(Commands.TemplateDetails, Categories.Map);
//		Action action = new Action("TEMPLATEDETAILS", "MAP");
		a.addParameter(ParameterTypes.Status, "OK");
		a.addParameter(ParameterTypes.Ontology, mTemplate.getOntologyNameFromDB());
		a.addParameter(ParameterTypes.Template, mTemplate.getName());
		a.addParameter(ParameterTypes.TemplateTitle, mTemplate.getTitle());
		a.addParameter(ParameterTypes.TemplateMaxUsers, Template.getTemplateMaxUsers(mTemplate.getXml()) + "");

		return createActionPackage(a);
	}

	public static Action getCreateMapAction(String sessionName, String templateName, String username) {
		Action a = new Action();
		a.addParameter(ParameterTypes.MapName, sessionName);
		a.addParameter(ParameterTypes.Template, templateName);
		a.addParameter(ParameterTypes.RestrictedTo, username);
		return a;
	}

	public static ActionPackage loadFromFileFinished(String importType) {
		Action a = new Action(Commands.Ready, Categories.Info);
//		Action a = new Action("READY", "INFO");
		a.addParameter(ParameterTypes.ImportType, importType);
		return createActionPackage(a);
	}

	public static ActionPackage getConfirmActionFromExistingAction(Action a, String msg) {

		Action action = new Action(Commands.ConfirmationRequest, Categories.Authoring);
//		Action action = new Action("CONFIRMATION-REQUEST", "AUTHORING");
		action.getParameters().addAll(a.getParameters());
		action.addParameter(ParameterTypes.OriginalCommand, a.getCmd().toString());
		action.addParameter(ParameterTypes.Message, msg);

		return createActionPackage(action);
	}

	public static ActionPackage authoringFailed(String msg, String mapId, String reason) {

		Action a = new Action(Commands.AuthoringFailed, Categories.Info);
		a.addParameter(ParameterTypes.Message, msg);
		a.addParameter(ParameterTypes.SessionId, mapId);
		a.addParameter(ParameterTypes.Reason, reason);
		return createActionPackage(a);
	}
	
	public static ActionPackage authoringFailed(String msg) {

		Action a = new Action(Commands.AuthoringFailed, Categories.Info);
//		Action a = new Action("AUTHORING-FAILED", "INFO");
		a.addParameter(ParameterTypes.Message, msg);

		return createActionPackage(a);
	}

	public static ActionPackage createActionPackage(Action a){	
		ActionPackage p = new ActionPackage();
		p.addAction(a);
		return p;
	}

}


