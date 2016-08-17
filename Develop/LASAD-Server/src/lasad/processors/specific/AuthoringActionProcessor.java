package lasad.processors.specific;

import java.io.File;
import java.util.Vector;

import lasad.Server;
import lasad.controller.ManagementController;
import lasad.controller.MapController;
import lasad.controller.xmlController.OntologyParser;
import lasad.entity.Map;
import lasad.entity.Ontology;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * this class handles all actions about Authoring
 * 
 * @author ?
 */
public class AuthoringActionProcessor extends AbstractActionObserver implements ActionObserver {

	public AuthoringActionProcessor() {
		super();
	}

	public ActionPackage processGetTemplates(boolean authoring) {
		ActionPackage ap = ActionPackageFactory.getTemplateListActionPackage(authoring);
		return ap;
	}

	/**
	 * create a Template while it doesn't exist.
	 * 
	 * @param a a specific LASAD action
	 * @author ?
	 */
	public ActionPackage processCreateTemplate(Action a) {
		ActionPackage ap;
		if (Template.getTemplateID(a.getParameterValue(ParameterTypes.TemplateName)) != -1) {
			ap = ActionPackageFactory.authoringFailed("Template already exists, please choose another name.");
		} else {
			// TODO Check if the ontology allows transcript, if transcript is set.
			File f = Template.generateTemplateConfigurationFile(a);

			Template t = Template.parseTemplateFromFile(f);
			t.saveToDatabase();

			ap = ActionPackageFactory.getNewTemplateAction(t);
		}

		return ap;
	}

	/**
	 * del a Template according to user's wish.
	 * 
	 * @param a a specific LASAD action
	 * @author ?
	 */
	public ActionPackage processDeleteTemplate(Action a) {
		ActionPackage ap;
		String templateName = a.getParameterValue(ParameterTypes.Template);

		if (!"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))) {

			Vector<Integer> mapIDs = Map.getIDsOfMapsThatUseTheTemplate(Template.getTemplateID(templateName));
			if (mapIDs.size() > 0) {
				String msg = "There are "
						+ mapIDs.size()
						+ " sessions that use this template. These sessions will no longer be valid and will be deleted. Are you sure, you want to proceed?";
				ap = ActionPackageFactory.getConfirmActionFromExistingAction(a, msg);
				return ap;
			}
		}

		// Remove from client list
		ap = ActionPackageFactory.getRemoveTemplateAction(templateName, Ontology.getOntologyName(Template.getOntologyID(templateName)));
		return ap;
	}

	/**
	 * collect all existing Ontologies and pack them into an ActionPackages
	 * 
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processGetOntologies() {
		Vector<String> ontologyList = Ontology.getAllOntologyNames();
		ActionPackage ap = ActionPackageFactory.getOntologyList(ontologyList);
		return ap;
	}

	/**
	 * collect all details about the Ontolgy that user means and pack them into an pack
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackage
	 * @author ZGE
	 */
	public ActionPackage processGetOntologyDetails(Action a) {
		String ontologyName = a.getParameterValue(ParameterTypes.OntologyName);
		ActionPackage ap = ActionPackageFactory.getOntologyDetails(ontologyName);
		return ap;
	}

	/**
	 * create an Ontology while it doesn't exist.
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processCreateOntology(Action a) {
		ActionPackage ap;

		if (Ontology.getOntologyID(a.getParameterValue(ParameterTypes.OntologyName)) != -1) {
			ap = ActionPackageFactory.authoringFailed("Ontology already exists, please choose another name.");
		} else {
			if (a.getParameterValue(ParameterTypes.Clone) != null) {
				String xml = Ontology.getOntologyXML(Ontology.getOntologyID(a.getParameterValue(ParameterTypes.Clone)));
				xml = xml.replace("type=\"" + a.getParameterValue(ParameterTypes.Clone) + "\"",
						"type=\"" + a.getParameterValue(ParameterTypes.OntologyName) + "\"");

				a.replaceParameter(ParameterTypes.OntologyXML, xml);

				Ontology newOntology = new Ontology(a.getParameterValue(ParameterTypes.OntologyName), xml);
				newOntology.saveToDatabase();

			} else {
				String ontologyXML = a.getParameterValue(ParameterTypes.OntologyXML);
				OntologyParser ontologyParser = new OntologyParser(ontologyXML);
				String ontologyName = ontologyParser.getOntologyName();
				Ontology newOntology = new Ontology(ontologyName, ontologyXML);
				newOntology.saveToDatabase();
			}

			/*File f = */Ontology.generateOntologyConfigurationFile(a);
			ap = processGetOntologies();

			// TODO: Zhenyu
			ap.addActionPackage(ActionPackageFactory.getInfoAction("Ontology successfully created."));
			// ActionPackage ap = ActionPackageFactory.getInfoAction("Ontology successfully created.");
		}
		return ap;
	}

	/**
	 * del an Ontology according to users wish
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processDeleteOntology(Action a) {
		ActionPackage ap;

		String ontologyName = a.getParameterValue(ParameterTypes.OntologyName);
		int ontologyID = Ontology.getOntologyID(ontologyName);

		// !"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))
		// The expression above is the original statement
		// TODO: Does it make any sense?
		if (!"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))) {

			Vector<Integer> templateIDs = Template.getIDsOfTemplatesThatUseTheOntology(ontologyID);
			if (templateIDs.size() > 0) {
				String msg = "The ontology is used by "
						+ templateIDs.size()
						+ " templates. These templates and all sessions that are created based on these templates will be deleted. Are you sure, you want to proceed?";

				ap = ActionPackageFactory.getConfirmActionFromExistingAction(a, msg);
				return ap;
			}
		}

		// Remove from client list
		ap = ActionPackageFactory.getRemoveOntologyAction(ontologyName);
		return ap;
	}

	/**
	 * update an Ontology and save it in database and xml file
	 * 
	 * @param a a specific LASAD action
	 * @param Server a Server object ?(this object seems not to be used in the end or only to be logging?)
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processUpdateOntology(Action a, Server myServer) {
		ActionPackage ap;

		String ontologyName = a.getParameterValue(ParameterTypes.OntologyName);
		int ontologyID = Ontology.getOntologyID(ontologyName);

		// Check if there are already sessions that are using this template. If that is the case
		// the sessions must be deleted to avoid any errors caused by the modifications of the ontology.

		// !"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))
		// The expression above is the original statement
		// TODO: Does it make any sense?
		if (!"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))) {

			Vector<Integer> templateIDs = Template.getIDsOfTemplatesThatUseTheOntology(ontologyID);

			int numberOfSessions = 0;

			for (Integer templateID : templateIDs) {
				Vector<Integer> sessionIDs = Map.getIDsOfMapsThatUseTheTemplate(templateID);
				numberOfSessions += sessionIDs.size();
			}

			if (templateIDs.size() > 0) {
				String msg = "";

				if (numberOfSessions > 0) {
					msg = "The ontology is used by "
							+ templateIDs.size()
							+ " template(s) and "
							+ numberOfSessions
							+ " session(s). These template(s) and all session(s) (including their content) that are created based on these template(s) will be deleted. Are you sure, you want to update the ontology?";
				} else {
					msg = "The ontology is used by " + templateIDs.size()
							+ " template(s). These template(s) will be deleted. Are you sure, you want to update the ontology?";
				}

				ap = ActionPackageFactory.getConfirmActionFromExistingAction(a, msg);
				return ap;
			}

		}

		// Delete templates & sessions (recursive) that use the ontology
		Vector<Integer> templateIDs = Template.getIDsOfTemplatesThatUseTheOntology(ontologyID);
		for (int i : templateIDs) {
			Template.delete(i);
		}

		// Update ontology saved in database with new ontology
		String xml = a.getParameterValue(ParameterTypes.OntologyXML);
		Ontology.updateOntologyInDB(ontologyName, xml);

		// Update local ontology xml file
		Ontology.updateOntologyInFileSystem(ontologyName, xml);

		// Update client view
		ap = processGetOntologyDetails(a);

		ap.addActionPackage(ActionPackageFactory.getInfoAction("Ontology successfully updated."));
		return ap;
	}

	/**
	 * get the list of all Users from database and pack them into the ActionPackage
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processGetUsers(Action a) {
		ActionPackage ap;
		if ("TRUE".equals(a.getParameterValue(ParameterTypes.GetRoles))) {
			Vector<User> t = User.getAllUsers();

			ap = ActionPackageFactory.getUserListActionPackage(t);
		} else {
			Vector<String> usernames = User.getUserListWithoutRoles();

			ap = ActionPackageFactory.getUserListWithoutRolesActionPackage(usernames);
		}
		return ap;
	}

	/**
	 * del a User according to users wish if the User that is going to be deleted is an author of one or more maps,show a warning
	 * to the user to confirm if not,del it directly.
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processDeleteUser(Action a) {
		ActionPackage ap;
		String username = a.getParameterValue(ParameterTypes.UserName);
		int userID = User.getUserID(username);

		if (!"TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Confirmed))) {

			String msg = "";

			Vector<Integer> authorOfMapIDs = Map.getIDsOfMapsThatAreCreatedByUser(userID);
			if (authorOfMapIDs.size() > 0) {
				msg += "The user is author of " + authorOfMapIDs.size() + " sessions. ";
			}

			int sessionIDsWhereUserDidParticipate = Map.getNumberOfMapRevisionsCreatedByUser(userID);
			if (sessionIDsWhereUserDidParticipate > 0) {
				msg += "The user provided " + sessionIDsWhereUserDidParticipate + " revisions in one or multiple sessions. ";
			}

			if (!msg.equals("")) {
				msg += "To keep the internal validity, the user will be replaced by \"Unknown\". Are you sure, you want to proceed?";
				ap = ActionPackageFactory.getConfirmActionFromExistingAction(a, msg);
				return ap;
			}
		}

		// Remove from client list
		ap = ActionPackageFactory.getRemoveUserAction(username, User.getRole(username));
		return ap;
	}

	/**
	 * create a User if it doesn't exist
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */
	public ActionPackage processCreateUser(Action a) {
		ActionPackage ap;
		int userID = User.getUserID(a.getParameterValue(ParameterTypes.UserName));
		if (userID != -1) {
			ap = ActionPackageFactory.authoringFailed("Username already exists, please choose another one.");
		} else {
			// TODO Marcel: Extract XML stuff off User
			User.generateUserConfigurationFile(a);

			boolean isPasswordEncrypted = ActionProcessor.isPasswordEncrypted(a);
			User user = new User(a.getParameterValue(ParameterTypes.UserName), a.getParameterValue(ParameterTypes.Password),
					a.getParameterValue(ParameterTypes.Role), isPasswordEncrypted);
			user.saveToDatabase();

			ap = ActionPackageFactory.getNewUserAction(user);
		}
		return ap;
	}

	/**
	 * Call to get an ActionPackage retrieving all maps and templates in one
	 * 
	 * @return the ActionPackage
	 * @author MB
	 */
	public ActionPackage processGetMapsAndTemplates() {
		ActionPackage ap = ActionPackageFactory.getMapsAndTemplates();
		return ap;
	}

	/**
	 * del a Map according to users wish
	 * 
	 * @param a a specific LASAD action
	 * @return an ActionPackages
	 * @author ZGE
	 */

	public ActionPackage processDeleteMap(Action a) {
		String mapName = a.getParameterValue(ParameterTypes.MapName);

		// Remove from client list
		ActionPackage ap = ActionPackageFactory.getRemoveMapAction(mapName, Map.getTemplate(Map.getMapID(mapName)).getName());

		// Delete map from file system and database
		Map.delete(Map.getMapID(mapName));

		return ap;
	}

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null && a.getCategory().equals(Categories.Authoring)) {
			ActionPackage ap = null;
			switch (a.getCmd()) {
			case GetTemplates:
				ap = processGetTemplates(true);
				returnValue = true;
				break;
			case CreateTemplate:
				ap = processCreateTemplate(a);
				returnValue = true;
				break;
			case DeleteTemplate:
				ap = processDeleteTemplate(a);
				String templateName = a.getParameterValue(ParameterTypes.Template);
				Template.delete(Template.getTemplateID(templateName));
				returnValue = true;
				break;
			case GetOntologies:
				ap = processGetOntologies();
				returnValue = true;
				break;
			case GetOntologyDetails:
				ap = processGetOntologyDetails(a);
				returnValue = true;
				break;
			case CreateOntology:
				ap = processCreateOntology(a);
				returnValue = true;
				break;
			case DeleteOntology:
				ap = processDeleteOntology(a);
				String ontologyName = a.getParameterValue(ParameterTypes.OntologyName);
				// Delete ontology from file system and database
				Ontology.delete(Ontology.getOntologyID(ontologyName));
				returnValue = true;
				break;
			case UpdateOntology:
				ap = processUpdateOntology(a, myServer);
				returnValue = true;
				break;
			case GetMapsAndTemplates:
				ap = processGetMapsAndTemplates();
				returnValue = true;
				break;
			case CreateMap:
				ap = MapController.processCreateMap(a, u, true);
				returnValue = true;
				break;
			case DeleteMap:
				ap = processDeleteMap(a);
				returnValue = true;
				break;
			case GetUsers:
				ap = processGetUsers(a);
				returnValue = true;
				break;
			case CreateUser:
				ap = processCreateUser(a);
				returnValue = true;
				break;
			case DeleteUser:
				ap = processDeleteUser(a);
				String username = a.getParameterValue(ParameterTypes.UserName);
				User.delete(username, User.getUserID(username), User.getUserID("Unknown"));
				returnValue = true;
				break;
			default:
				break;
			}

			if (ap != null) {
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
			}
		}
		return returnValue;
	}

}
