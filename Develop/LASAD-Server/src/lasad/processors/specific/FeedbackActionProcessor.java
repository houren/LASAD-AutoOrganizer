package lasad.processors.specific;

import java.util.Vector;

import lasad.controller.ManagementController;
import lasad.controller.MapController;
import lasad.entity.ActionParameter;
import lasad.entity.Element;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.processors.ActionProcessor;

/**
 * This class handles all feedback related actions distributed by the ActionProcessor
 * 
 * @author MB
 */
public class FeedbackActionProcessor extends AbstractActionObserver implements ActionObserver {

	public FeedbackActionProcessor() {
		super();
	}

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null && a.getCategory().equals(Categories.Feedback)) {
			if (a.getCmd().equals(Commands.Request)) {
				distributeToAllUsersWithoutSaving(a, u);
				returnValue = true;
			} else if (a.getCmd().equals(Commands.CreateElement) || a.getCmd().equals(Commands.Highlight)) {
				processCreateElement(a, u);
				returnValue = true;
			}
		}

		return returnValue;
	}

	/**
	 * Creates an ActionPackage and distributes it to all users on the current map. No new revision is created therefore though
	 * and thus there's no database entry for this action
	 * 
	 * @param a The action to be distributed
	 * @param u The user that this action @{a} came from
	 * @author FL
	 */
	private void distributeToAllUsersWithoutSaving(Action a, User u) {
		int mapID = ActionProcessor.getMapIDFromAction(a);

		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
	}

	/**
	 * Processes a CreateElementAction. Therefore a new map revision is created and thus a database entry for that.
	 * 
	 * @param a The action to create a new element
	 * @param u The user that created that new element
	 * @author FL
	 */
	private void processCreateElement(Action a, User u) {
		int mapID = ActionProcessor.getMapIDFromAction(a);

		Vector<String> parents = a.getParameterValues(ParameterTypes.Parent);
		if (parents != null) {
			if (!parentsDoExist(a.getParameterValues(ParameterTypes.Parent), a.getParameterValue(ParameterTypes.Type))) {
				Logger.log("One of the parents is no longer active. Create element failed.");
				ActionPackage ap = ActionPackageFactory.error("One of the parents is no longer present on map. Create element failed");
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return;
			}
		}

		// TODO Zhenyu
		if (u.getSessionID().equals("DFKI") && Map.getMapName(mapID) == null) {
			Logger.log("[ActionProcessor.processCreateElement] ERROR: No LASAD map for ID submitted from xmpp - " + mapID
					+ " - Ignoring create action - \n" + a);
			return;
		}
		// Create new revision of the map
		Revision r = createNewRevision(mapID, u, a);

		r.saveToDatabase();

		// Create an element in the database with type information; replace
		// parents (LAST-ID) with correct top level element's ID
		Element e = new Element(mapID, a, r.getId(), myServer.currentState);

		// Replace TIME, ROOTELEMENTID, PARENT with actual values instead of
		// place holders
		a = MapController.replacePlaceHoldersAddIDsAddMetadata(a, e.getId(), u.getNickname());

		// Save action parameters for the revision
		ActionParameter.saveParametersForRevision(r.getId(), e.getId(), a);

		// Add to users' action queues
		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
	}

	/**
	 * Checks whether parent element of another element is active, thus not deleted by anyone.
	 * 
	 * @param parameterVector
	 * @param type
	 * @return
	 * @author FL
	 */
	// TODO Check what else this method is doing, 'specially first part
	private boolean parentsDoExist(Vector<String> parameterVector, String type) {
		for (String p : parameterVector) {
			int elementID = -1;
			// TODO Find out why this is not similar
			if (p.equalsIgnoreCase("LAST-ID")) {
				if ("LAST-ID".equalsIgnoreCase(p)) {
					elementID = myServer.currentState.lastTopLevelElementID;
				}
			} else {
				elementID = Integer.parseInt(p);
			}

			if (!Element.isElementActive(elementID)) {
				Logger.debugLog("ERROR: Element " + elementID + " is no longer active!");
				return false;
			} else {
				Logger.log("Element " + elementID + " is still active.");
			}
		}
		return true;
	}

	/**
	 * Creates a new revision after applying an action from a certain user on the map he's currently working on
	 * 
	 * @param mapID The map the user's working on
	 * @param u The user
	 * @param a The user's action on this map
	 * @return A new revision of this map
	 * @author FL
	 */
	private Revision createNewRevision(int mapID, User u, Action a) {
		// Create new revision of the map
		Revision r;
		// special case for agent creating nodes for others
		if (u.getSessionID().equals("DFKI")) {

			String username = a.getParameterValue(ParameterTypes.UserName);
			if (username != null) {
				int userId = User.getId(username);
				if (userId != -1) {
					r = new Revision(mapID, userId);
				} else {
					r = new Revision(mapID, u.getUserID());
					Logger.log("[ActionProcessor.processCreateElement] ERROR: Non-LASAD username submitted from xmpp - " + username
							+ ", using username but default will appear upon relogin");
				}
			} else {
				r = new Revision(mapID, u.getUserID());
			}
		} else {
			r = new Revision(mapID, u.getUserID());
		}

		return r;
	}

}
