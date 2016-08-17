package lasad.controller;

import java.util.Vector;

import lasad.Server;
import lasad.controller.xmlController.ChatParser;
import lasad.controller.xmlController.MapParser;
import lasad.controller.xmlController.OntologyParser;
import lasad.controller.xmlController.TemplateParser;
import lasad.entity.ActionParameter;
import lasad.entity.Element;
import lasad.entity.Map;
import lasad.entity.Ontology;
import lasad.entity.Revision;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class MapController {
	/**
	 * Create a new Map and join it (wrong name? where does loading the file happen? )
	 * 
	 * @param a a special lasad action
	 * @param u an user
	 * @author ?
	 */
	public static void loadMapFromFile(Action a, User u, boolean userJoin) {
		String sessionName = a.getParameterValue(ParameterTypes.Session);
		if (Map.getMapID(sessionName) != -1) {
			ErrorMessageHandler.createErrorMessageForUser("Session does already exist. Please choose a different name...", u);
			return;
		}

		// Create ontology if not existent
		String ontologyXML = a.getParameterValue(ParameterTypes.OntologyXML);

		// TODO Sabine: Hier mehr als nur Pruefung gegen null erforderlich?
		if (ontologyXML != null) {
			createOntology(ontologyXML);

			// Create template if not existent
			String templateName = createNewTemplate(a);

			// Create map / session
			String username = a.getParameterValue(ParameterTypes.RestrictedTo);

			ActionPackage createMapAp = processCreateMap(ActionPackageFactory.getCreateMapAction(sessionName, templateName, username), u,
					false);
			Logger.doCFLogging(createMapAp);
			ManagementController.addToUsersActionQueue(createMapAp, u.getSessionID());
			int mapID = Integer.parseInt(createMapAp.getActions().get(0).getParameterValue(ParameterTypes.MapId));

			if(a.getParameterValue(ParameterTypes.BackgroundImageURL)!= null )
			{
				Map.setBackgroundImage(mapID, a.getParameterValue(ParameterTypes.BackgroundImageURL));
			}
			// Parse content
			MapParser mapParser = new MapParser(a.getParameterValue(ParameterTypes.ContentXML), ActionProcessor.getInstance());
			mapParser.parseContentFromXML(mapID, u.getUserID());
			// processParseContentFromXML(mapID, a.getParameterValue(ParameterTypes.ContentXML), u.getUserID());

			// Parse chat log
			processParseChatLogFromXML(mapID, a.getParameterValue(ParameterTypes.ChatLog), u.getUserID());

			// Join map
			if (userJoin) {
				UserManagementController.processJoin(ActionPackageFactory.joinMap(mapID), u);
			}

			// Send end action to close any loading dialogues
			ActionPackage ap = ActionPackageFactory.loadFromFileFinished("LASAD");
			Logger.doCFLogging(ap);
			ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		} else {
			// TODO Sabine: Hier evtl. mehr als nur eine solche kurze Fehlermeldung?
			ErrorMessageHandler.createErrorMessageForUser("Upload failed...", u);
			return;
		}
	}

	// create a new ontology
	private static void createOntology(String ontologyXML) {
		OntologyParser ontologyParser = new OntologyParser(ontologyXML);
		if (!Ontology.isExisting(ontologyParser.getOntologyName())) {
			Ontology newOntology = new Ontology(ontologyParser.getOntologyName(), ontologyXML);
			newOntology.saveToDatabase();
		} else {
			Logger.log("Load from file: Ontology found.");
		}
	}

	// create a new template
	private static String createNewTemplate(Action a) {
		// Create template if not existent
		String templateXML = a.getParameterValue(ParameterTypes.TemplateXML);
		TemplateParser templateParser = new TemplateParser(templateXML);
		String templateName = templateParser.getTemplateName();
		int templateID = Template.getTemplateID(templateName);
		int ontologyID = Ontology.getOntologyID(templateParser.getOntologyName());

		if (templateID == -1) {
			Template newTemplate = new Template(templateName, templateXML, ontologyID);
			newTemplate.saveToDatabase();
		} else {
			Logger.log("Load from file: Template found.");
		}
		return templateName;
	}

	// TODO Merge with ChatParser
	private static void processParseChatLogFromXML(int mapID, String chatLogXML, int userID) {
		ChatParser chatParser = new ChatParser(chatLogXML);
		Vector<String[]> chatMessages = chatParser.GetChatMessages();

		for (String[] chatMessage : chatMessages) {
			Revision r = new Revision(mapID, "Chat message", userID);
			r.saveToDatabase();

			Action chatAction = new Action(Commands.ChatMsg, Categories.Communication);
			chatAction.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
			chatAction.addParameter(ParameterTypes.UserName, chatMessage[0]);
			chatAction.addParameter(ParameterTypes.Time, chatMessage[1]);
			chatAction.addParameter(ParameterTypes.Message, chatMessage[2]);
			chatAction.addParameter(ParameterTypes.Opener, chatMessage[3]);
			chatAction.addParameter(ParameterTypes.TextColor, chatMessage[4]);

			ActionParameter.saveParametersForRevision(r.getId(), chatAction);
		}
	}

	/**
	 * create a Map if it doesn't exist
	 * 
	 * @param a a specific LASAD action
	 * @param u a user whos want to create a new map
	 * @param enableCheck enable the check to check if this map already exists or not
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public static ActionPackage processCreateMap(Action a, User u, boolean enableCheck) {
		ActionPackage ap;
		int mapId = Map.getMapID(a.getParameterValue(ParameterTypes.MapName));
		if (mapId != -1 && enableCheck) {
			//ap = ActionPackageFactory.authoringFailed("Session already exists, please choose a different name.");
			ap = ActionPackageFactory.authoringFailed("Session " +  Integer.toString(mapId) + " already exists, please choose a different name.", Integer.toString(mapId), "ALREADY_EXISTS");
			//TODO: could check to ensure that the map that exists is the same as the template sent in, send warning otherwise
			return ap;
		} else {
			Vector<Integer> userList = new Vector<Integer>();

			// TODO Zhenyu
			addUserIntoListForCreatingMap(userList, a);

			String templateName = a.getParameterValue(ParameterTypes.Template);
			int templateId = Template.getTemplateID(a.getParameterValue(ParameterTypes.Template));
			if (templateId != -1) {
				Map m = new Map(a.getParameterValue(ParameterTypes.MapName), templateId, u.getUserID(), userList);
				boolean saveSuccess = m.saveToDatabase(ActionProcessor.getInstance());

				if (saveSuccess) {
					if(a.getParameterValue(ParameterTypes.BackgroundImageURL)!= null )
					{
						Map.setBackgroundImage(m.getId(), a.getParameterValue(ParameterTypes.BackgroundImageURL));
					}
					Server.getInstance().currentState.maps.put(m.getId(), m);
					Server.getInstance().currentState.mapUsers.put(m.getId(), new Vector<User>());

					if ("TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Persistent))) {
						m.generateMapConfigurationFile();
					}

					ap = ActionPackageFactory.getNewMapAction(m);
					return ap;
				} else {
					return null;
				}
			} else {
				System.out.println("In ActionProcessor.processAuthoringCreateSession: template not found for name - " + templateName);
				return null;
			}

		}
	}

	/**
	 * find out all maps which are restricted to the user
	 * 
	 * @param userList the list of mapIds which are restricted to the user
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	private static void addUserIntoListForCreatingMap(Vector<Integer> userList, Action a)// TODO this functions name isn't right
	{
		if (a.getParameterValues(ParameterTypes.RestrictedTo) != null) {
			if (a.getParameterValues(ParameterTypes.RestrictedTo).size() > 0) {
				for (int i = 0; i < a.getParameterValues(ParameterTypes.RestrictedTo).size(); i++) {
					if (a.getParameterValues(ParameterTypes.RestrictedTo).get(i) != null
							&& !a.getParameterValues(ParameterTypes.RestrictedTo).get(i).equals("")) {
						System.out.println("User: \n" + a.getParameterValues(ParameterTypes.RestrictedTo).get(i) + "\"");
						userList.add(User.getUserID(a.getParameterValues(ParameterTypes.RestrictedTo).get(i)));
					}
				}
			}
		}
	}

	/**
	 * Processes an UpdateAction of a certain element
	 * 
	 * @param a The UpdateAction
	 * @param u The user sending this action
	 * @author FL
	 */
	public static void processUpdateElement(Action a, User u) {
		a.addParameter(ParameterTypes.Received, System.currentTimeMillis() + "");

		int mapID = ActionProcessor.getMapIDFromAction(a);
		int elementID = Integer.parseInt(a.getParameterValue(ParameterTypes.Id));

		synchronized (ActionProcessor.DeleteUpdateJoinActionLock) {
			// Action is already obsolete
			if (Element.getLastModificationTime(elementID) > Long.parseLong(a.getParameterValue(ParameterTypes.Received))) {
				return;
			}

			if (!Element.isElementActive(elementID)) {
				Logger.log("Element " + elementID + " is no longer active. Update failed.");
				ActionPackage ap = ActionPackageFactory.error("Element is no longer present on map. Update failed");
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return;
			}

			// Create new revision of the map
			Revision r = new Revision(mapID, u.getUserID());
			r.saveToDatabase();

			// If it is a lock / unlock update of an element, check if element is already locked or unlocked
			if (a.getParameterValue(ParameterTypes.Status) != null) {
				String lockStatus = Element.getLastValueOfElementParameter(elementID, "STATUS");
				if (a.getParameterValue(ParameterTypes.Status).equalsIgnoreCase(lockStatus)) {
					return;
				}
			}

			// Update elements last update time
			Element.updateModificationTime(elementID, Long.parseLong(a.getParameterValue(ParameterTypes.Received)));

			addAndSaveParameters(a, u.getNickname(), r, elementID);

			// Add to users' action queues
			if (a.getCmd().equals(Commands.UpdateCursorPosition)) {
				ActionPackage ap = ActionPackage.wrapAction(a);
				Logger.doCFLogging(ap);
				ManagementController.addToAllUsersButMySelfOnMapActionQueue(ap, mapID, u);
			} else {
				ActionPackage ap = ActionPackage.wrapAction(a);
				Logger.doCFLogging(ap);
				ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
			}
		}
	}

	/**
	 * @param a
	 * @param mapID
	 * @param userID
	 * @return
	 * @author FL
	 */
	public static int processCreatePreDefinedElement(Action a, int mapID, int userID) {

		Revision r = null;

		if (a.getParameterValue(ParameterTypes.Time) != null && !a.getParameterValue(ParameterTypes.Time).equalsIgnoreCase("CURRENT-TIME")) {
			r = new Revision(mapID, userID, Long.parseLong(a.getParameterValue(ParameterTypes.Time)));
		} else {
			// Create new revision of the map
			r = new Revision(mapID, userID);
		}

		r.saveToDatabase();

		// Create an element in the database with type information; replace
		// parents (LAST-ID) with correct top level element's ID
		Element e = new Element(mapID, a, r.getId(), Server.getInstance().currentState);

		addAndSaveParameters(a, User.getName(userID), r, e.getId());

		return e.getId();
	}

	/**
	 * Processes an UpdateAction which is not distributed to other users.
	 * 
	 * @param a The UpdateAction
	 * @param userID The user this action came from
	 * @author FL
	 */
	public static void processUpdateElementLocal(Action a, int userID) {
		int mapID = ActionProcessor.getMapIDFromAction(a);

		int elementID = Integer.parseInt(a.getParameterValue(ParameterTypes.Id));

		// Create new revision of the map
		Revision r = new Revision(mapID, userID);
		r.saveToDatabase();

		addAndSaveParameters(a, User.getName(userID), r, elementID);
	}

	/**
	 * adds parameters to an Action and saves parameters for revision
	 * 
	 * @param a
	 * @param userID
	 * @param r
	 * @param elementID
	 * @author SN
	 */
	private static void addAndSaveParameters(Action a, String userNickname, Revision r, int elementID) {
		// Replace TIME, ROOTELEMENTID, PARENT with actual values instead of
		// place holders
		a = replacePlaceHoldersAddIDsAddMetadata(a, elementID, userNickname);

		// Save action parameters for the revision
		ActionParameter.saveParametersForRevision(r.getId(), elementID, a);
	}

	/**
	 * Replaces TIME, ROOTELEMENTID, PARENT with actual values instead of place holders of a certain action
	 * 
	 * @param a The action whose place holders are to be replaced
	 * @return The action with replaced place holders
	 * @author FL
	 */
	public static Action replacePlaceHoldersAddIDsAddMetadata(Action a, int ID, String username) {
		Server myServer = Server.getInstance();
		int mapID = ActionProcessor.getMapIDFromAction(a);

		boolean usernamePresent = false, idPresent = false;
		boolean relation = false;
		boolean addRootElementID = false;

		for (Parameter p : a.getParameters()) {
			switch (p.getType()) {
			case UserName:
				usernamePresent = true;
				break;
			case Type:
				if (p.getValue().equalsIgnoreCase("relation") || p.getValue().equalsIgnoreCase("emptyrelation")) {
					addRootElementID = true;
					relation = true;
				} else if (p.getValue().equalsIgnoreCase("box") || p.getValue().equalsIgnoreCase("emptybox")) {
					addRootElementID = true;
				}
				break;
			case Id:
				idPresent = true;
				break;
			default:
				break;
			}

			// Replace LAST-ID of parent parameters
			// The secondLastTopLevelElementID is required if the user creates a
			// box and a relation in the same step to avoid the relation to use itself as parent
			if ("LAST-ID".equalsIgnoreCase(p.getValue())) {
				if (!relation) {
					p.setValue(myServer.currentState.lastTopLevelElementID + "");
				} else {
					p.setValue(myServer.currentState.secondLastTopLevelElementID + "");
				}
			}

			// Replace CURRENT-TIME of time parameters
			else if ("CURRENT-TIME".equalsIgnoreCase(p.getValue())) {
				p.setValue(System.currentTimeMillis() + "");
			}
		}// end for

		if (!idPresent) {
			a.addParameter(ParameterTypes.Id, ID + "");
		}

		if (!usernamePresent) {
			a.addParameter(ParameterTypes.UserName, username);
		}

		if (addRootElementID) {
			if (a.getParameterValue(ParameterTypes.RootElementId) == null) {
				a.addParameter(ParameterTypes.RootElementId, Map.getNewRootElementID(mapID) + "");
			}
		}
		return a;
	}
}
