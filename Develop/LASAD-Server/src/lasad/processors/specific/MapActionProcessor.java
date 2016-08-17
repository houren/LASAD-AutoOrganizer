package lasad.processors.specific;

// import lasad.processors.specific.organization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Calendar;

import lasad.Config;
import lasad.State;
import lasad.database.DatabaseConnectionHandler;
import lasad.controller.ManagementController;
import lasad.entity.ActionParameter;
import lasad.entity.Element;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.OliDatabaseLogger;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.element.DatasetElement;
import edu.cmu.pslc.logging.element.LevelElement;
import edu.cmu.pslc.logging.element.MetaElement;
import edu.cmu.pslc.logging.element.ProblemElement;


// import lasad.gwt.client.LASAD_Client;
// import lasad.gwt.client.communication.LASADActionSender;
// import lasad.gwt.client.communication.helper.ActionFactory;
// import lasad.gwt.client.logger.Logger;
// import lasad.gwt.client.model.ElementInfo;
// import lasad.gwt.client.model.argument.MVController;
// import lasad.gwt.client.ui.box.AbstractBox;
// import lasad.gwt.client.ui.common.AbstractExtendedElement;
// import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;
// import lasad.gwt.client.ui.link.AbstractLinkPanel;
// import lasad.gwt.client.ui.workspace.LASADInfo;
// import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

/**
 * this class handles all actions about map
 * 
 * @author ?
 */
public class MapActionProcessor extends AbstractActionObserver implements ActionObserver {

	private boolean DS_LOGGING_IS_ON;

	private OliDatabaseLogger dsLogger;

	// user, sessionID
	private java.util.Map<String, Set<String>> loggedSessions;

	// session + user + mapID --> contextMSG
	private java.util.Map<String, ContextMessage> loggedContexts;

	private String DATASET;
	private String className;
	private String school;
	private String period;
	private String instructor;

	public MapActionProcessor()
	{
		super();

		// These settings correspond to whether or not PSLC DataShop Logging will be used
        String settingsFileName = "./ds_settings.txt";

        try
        {
            FileReader fr = new FileReader(settingsFileName);
            BufferedReader reader = new BufferedReader(fr);

            // first line of file is whether to do DS Logging
            DS_LOGGING_IS_ON = Boolean.parseBoolean(reader.readLine().replaceAll("\\s",""));
            if (DS_LOGGING_IS_ON)
            {
            	String url = reader.readLine();
            	dsLogger = OliDatabaseLogger.create(url, "UTF-8");
            	// second line of file is dataset
            	DATASET = reader.readLine();
            	className = reader.readLine();
            	school = reader.readLine();
            	period = reader.readLine();
            	instructor = reader.readLine(); 

				loggedSessions = new ConcurrentHashMap<String, Set<String>>();
				loggedContexts = new ConcurrentHashMap<String, ContextMessage>();
            }
            reader.close();         
        }
        catch(Exception ex) {
        	DS_LOGGING_IS_ON = false;
        	DATASET = "garbage";
        	className = "garbage";
        	school = "garbage";
        	period = "garbage";
        	instructor = "garbage";
            Logger.debugLog("ERROR: can't read DS settings, DS Logging deactivated.");
        	StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			Logger.debugLog(sw.toString());              
        }
	}

	/**
     * Logs user map actions to PSLC datashop
     */
    private void logToDataShop(Action a, String userName, String sessionID)
    {
    	try
    	{
			//String userName = u.getNickname();
	        //String sessionID = u.getSessionID();
	        Integer mapID = ActionProcessor.getMapIDFromAction(a);

	        final String CONTEXT_REF = sessionID + userName + String.valueOf(mapID);

	        if (loggedSessions.get(userName) == null)
	        {
	        	dsLogger.logSession(userName, sessionID);
	        	Set<String> sessIDs = new HashSet<String>();
	        	sessIDs.add(sessionID);
	        	loggedSessions.put(userName, sessIDs);
	        }
	        else if (!loggedSessions.get(userName).contains(sessionID))
	        {
	        	dsLogger.logSession(userName, sessionID);
	        	loggedSessions.get(userName).add(sessionID);
	        }

	        boolean shouldLogContext = false;
	        ProblemElement problem = new ProblemElement(Map.getMapName(mapID));

	        String timeString = Long.toString(a.getTimeStamp());
			String timeZone = Calendar.getInstance().getTimeZone().getID();
		        
			ContextMessage contextMsg = loggedContexts.get(CONTEXT_REF);
			if (contextMsg == null)
			{
			    MetaElement metaElement = new MetaElement(userName, sessionID, timeString, timeZone);
				shouldLogContext = true;
				contextMsg = ContextMessage.createStartProblem(metaElement);

				LevelElement sectionLevel;
	        	sectionLevel = new LevelElement("Section", "01", problem);

		        contextMsg.setClassName(className);
		        contextMsg.setSchool(school);
		        contextMsg.setPeriod(period);
		        contextMsg.addInstructor(instructor);
		        contextMsg.setDataset(new DatasetElement(DATASET, sectionLevel));	        
			}	

	        ToolMessage toolMsg = ToolMessage.create(contextMsg);
	        toolMsg.setTimeString(timeString);

	        String selection;
	        if (a.getParameterValue(ParameterTypes.Id) == null)
	        {
	        	selection = "";
	        }
	        else
	        {
	        	selection = a.getParameterValue(ParameterTypes.Id);
	        }

	        final String input;
	        final String action;
	        String eltType = a.getParameterValue(ParameterTypes.Type);

	        switch (a.getCmd())
	        {
	        	case CreateElement:
		        	input = "";
		        	if (eltType == null)
		        	{
		        		return;
		        	}
		        	else if (eltType.equals("box"))
		        	{
		        		if (a.getParameterValue(ParameterTypes.AutoGenerated) == null)
		        		{ 
		        			action = "Create Box";
		        		}
		        		else
		        		{
		        			action = "Auto Create Box";
		        		}
		        		toolMsg.setAsAttempt("");
		        	}
		        	else if (eltType.equals("relation"))
		        	{
		        		if (a.getParameterValue(ParameterTypes.AutoGenerated) == null)
		        		{
		        			action = "Create Relation";
		        		}
		        		else
		        		{
		        			action = "Auto Create Relation";
		        		}
		        		toolMsg.setAsAttempt("");
		        	}
		        	else
		        	{
		        		return;
		        	}
	        		break;
	        	/*case AutoResizeTextBox:
	        		input = "Width: " + a.getParameterValue(ParameterTypes.Width) + "; Height: " + a.getParameterValue(ParameterTypes.Height);
			        action = "Auto Resize Text Box";
			        toolMsg.setAsAttempt("");
	        		break; */
	        	case UpdateElement:
	        		String text = a.getParameterValue(ParameterTypes.Text);
			        if (text == null)
			        {
			        	String posX = a.getParameterValue(ParameterTypes.PosX);
			        	if (posX == null)
			        	{
			        		String width = a.getParameterValue(ParameterTypes.Width);
			        		if (width == null)
			        		{
			        			return;
			        		}
			        		else
			        		{
			        			action = "Resize Box";
			        			input = "Width: " + width + "; Height: " + a.getParameterValue(ParameterTypes.Height);
			        			toolMsg.setAsAttempt("");
			        		}
			        	}
			        	else
			        	{	
			        		action = "Reposition Box";
			        		input = "PosX: " + posX + "; PosY: " + a.getParameterValue(ParameterTypes.PosY);
			        		toolMsg.setAsAttempt("");
			        	}
			        }
			        else
			        {
			        	action = "Modify Text";

			        	// hack, box ID is always 1 less than text box ID
			        	selection = String.valueOf(Integer.parseInt(selection) - 1);

			        	toolMsg.setAsAttempt("");
			        	input = text;
			        }
	        		break;
	        	case DeleteElement:
		        	if (eltType == null)
		        	{
		        		return;
		        	}
		        	else if (eltType.equals("box"))
		        	{
		        		if (a.getParameterValue(ParameterTypes.AutoGenerated) == null)
		        		{
		        			action = "Delete Box";
		        		}
		        		else
		        		{
		        			action = "Auto Delete Box";
		        		}
		        		toolMsg.setAsAttempt("");
	        		}
	        		else if (eltType.equals("relation"))
	        		{
	        			if (a.getParameterValue(ParameterTypes.AutoGenerated) == null)
		        		{
		        			action = "Delete Relation";
		        		}
		        		else
		        		{
		        			action = "Auto Delete Relation";
		        		}
	        			toolMsg.setAsAttempt("");
	        		}
		        	else
		        	{
		        		return;
		        	}
		        	input = "";
	        		break;
	        	case AutoOrganize:
	        		toolMsg.setAsAttempt("");
		        	String orientationBool = a.getParameterValue(ParameterTypes.OrganizerOrientation);
		        	String orient;
		        	if (Boolean.parseBoolean(orientationBool))
		        	{
		        		orient = "upward";
		        	}
		        	else
		        	{
		        		orient = "downward";
		        	}
		        	String width = a.getParameterValue(ParameterTypes.OrganizerBoxWidth);
		        	String height = a.getParameterValue(ParameterTypes.OrganizerBoxHeight);
		        	input = "Orientation: " + orient + "; Width: " + width + "; Height: " + height;
		        	action = "Auto Organize";
	        		break;
	        	case ChangeFontSize:
	        		toolMsg.setAsAttempt("");
		        	input = "Font Size: " + a.getParameterValue(ParameterTypes.FontSize);
		        	action = "Change Font Size";
	        		break;
	        	default:
	        		return;
	        }

	        if (selection != null && action != null && input != null)
	        {
	        	toolMsg.addSai(selection, action, input);
	        }
	        else
	        {
	        	Logger.debugLog("ERROR: cannot log becase sai is null for following action!");
	        	Logger.debugLog(a.toString());
	        	return;
	        }

	        if (shouldLogContext)
	        {
	        	if (dsLogger.log(contextMsg))
		        {
		        	loggedContexts.put(CONTEXT_REF, contextMsg);
		        }
		        else
		        {
		        	Logger.debugLog("ERROR: context Message log failed for following action!");
		        	Logger.debugLog(a.toString());
		        	return;
		        }
	        }

	        if (!dsLogger.log(toolMsg))
	        {
	        	Logger.debugLog("ERROR: tool Message log failed for following action!");
	        	Logger.debugLog(a.toString());
	        	return;
	        }
		}	    	
        catch(Exception e)
        {
        	Logger.debugLog("ERROR: Exception thrown for following action!");
        	Logger.debugLog(a.toString());
        	Logger.debugLog("EXCEPTION INFO...");
        	StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Logger.debugLog(sw.toString());
        } 	
    }

    // To avoid duplicate links being created
    private boolean linkExistsBetweenParents(Vector<String> parents, int mapID)
    {
    	if (parents != null && parents.size() == 2)
    	{
    		Vector<Integer> origParents = new Vector<Integer>();
	    	for (String parentID : parents)
	    	{
	    		if (parentID.equalsIgnoreCase("LAST-ID"))
	    		{
					origParents.add(myServer.currentState.lastTopLevelElementID);
				}
				else
				{
					try
					{
						origParents.add(Integer.parseInt(parentID));
					}
					catch(Exception e)
					{
						return false;
					}
				}
	    	}

    		Vector<Integer> relations = Element.getActiveRelationIDs(mapID);
    		for (int relationID : relations)
    		{
    			Vector<Integer> otherParents = Element.getParentElementIDs(relationID);
    			if (otherParents.containsAll(origParents))
    			{
    				return true;
    			}
    		}
    		return false;
    	}
    	else
    	{
    		return false;
    	}
    }

    /**
	 * create an Element in a map,for Example Box, Link etc. save it in the database
	 * 
	 * @param a a specific LASAD action
	 * @param u the User,who owns this map
	 * @author ZGE
	 * Return true if successfully created
	 */
	public boolean processCreateElement(Action a, User u) {
		int mapID = ActionProcessor.getMapIDFromAction(a);

		Vector<String> parents = a.getParameterValues(ParameterTypes.Parent);
		if (parents != null) {
			if (!parentsDoExist(a.getParameterValues(ParameterTypes.Parent))) {
				Logger.log("One of the parents is no longer active. Create element failed.");
				ActionPackage ap = ActionPackageFactory.error("One of the parents is no longer present on map. Create element failed");
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return false;
			}
			else if (linkExistsBetweenParents(parents, mapID))
			{
				Logger.log("Link already exists between parents. Create element failed.");
				return false;
			}
		}

		if (u.getSessionID().equals("DFKI") && Map.getMapName(mapID) == null) {
			Logger.log("[ActionProcessor.processCreateElement] ERROR: No LASAD map for ID submitted from xmpp - " + mapID
					+ " - Ignoring create action - \n" + a);
			return false;
		}
		// Create new revision of the map
		Revision r = createNewRevision(mapID, u, a);

		r.saveToDatabase();

		// Create an element in the database with type information; replace
		// parents (LAST-ID) with correct top level element's ID
		Element e = new Element(mapID, a, r.getId(), myServer.currentState);

		// Replace TIME, ROOTELEMENTID, PARENT with actual values instead of
		// place holders
		a = replacePlaceHoldersAddIDsAddMetadata(a, e.getId(), u.getNickname());

		// Save action parameters for the revision
		ActionParameter.saveParametersForRevision(r.getId(), e.getId(), a);

		// Add to users' action queues
		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
		return true;

	}

	/**
	 * check if the elements parents exist
	 * 
	 * @param parameterVector a set of parameters describing the parents
	 * @return a boolean value , true the parents exist or false the parents don't exist
	 * @author ZGE
	 */
	private boolean parentsDoExist(Vector<String> parameterVector) {
		for (String p : parameterVector) {
			int elementID = -1;
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
	 * create a new revision of map and save it in the database
	 * 
	 * @param mapID the ID of this map
	 * @param u the owner of this map
	 * @param a the current action to create a new Revision
	 * @return the instance of the revision
	 * @author ZGE
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

	/**
	 * Replaces TIME, ROOTELEMENTID, PARENT with actual values instead of place holders
	 * 
	 * @param a
	 * @return
	 */
	private Action replacePlaceHoldersAddIDsAddMetadata(Action a, int ID, String username) {

		int mapID = ActionProcessor.getMapIDFromAction(a);

		boolean usernamePresent = false, idPresent = false;
		boolean relation = false;
		boolean addRootElementID = false;

		for (Parameter p : a.getParameters()) {

			// To decide which type of this action is
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
			// box and a relation in the same step to avoid the relation to use
			// itself as parent
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

	/**
	 * after changing the status of the element in the map the relevant values in database are changed too and the relevant users
	 * are informed.
	 * 
	 * @param a a special lasad action
	 * @param u the current user
	 * @author ZGE
	 */
	public boolean processUpdateElement(Action a, User u) {

		a.addParameter(ParameterTypes.Received, System.currentTimeMillis() + "");

		int mapID = ActionProcessor.getMapIDFromAction(a);
		int elementID = Integer.parseInt(a.getParameterValue(ParameterTypes.Id));

		synchronized (ActionProcessor.DeleteUpdateJoinActionLock) {

			// Action is already obsolete
			if (Element.getLastModificationTime(elementID) > Long.parseLong(a.getParameterValue(ParameterTypes.Received))) {
				return false;
			}

			if (!Element.isElementActive(elementID)) {
				Logger.log("Element " + elementID + " is no longer active. Update failed.");
				ActionPackage ap = ActionPackageFactory.error("Element is no longer present on map. Update failed");
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return false;
			}

			// Create new revision of the map
			Revision r = new Revision(mapID, u.getUserID());
			r.saveToDatabase();

			// If it is a lock / unlock update of an element, check if element is already locked or unlocked
			if (a.getParameterValue(ParameterTypes.Status) != null) {
				String lockStatus = Element.getLastValueOfElementParameter(elementID, "STATUS");
				if (a.getParameterValue(ParameterTypes.Status).equalsIgnoreCase(lockStatus)) {
					return false;
				}
			}

			// Update elements last update time
			Element.updateModificationTime(elementID, Long.parseLong(a.getParameterValue(ParameterTypes.Received)));

			// Add the username, etc.
			a = replacePlaceHoldersAddIDsAddMetadata(a, elementID, u.getNickname());

			// Save action parameters for the revision
			ActionParameter.saveParametersForRevision(r.getId(), elementID, a);

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

		return true;
	}

	/**
	 * update the Cursor
	 * 
	 * @param a a special lasad action
	 * @param u the current user
	 * @author ZGE
	 */
	public void processCursorUpdate(Action a, User u) {
		if ("TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Persistent))) {
			processUpdateElement(a, u);
		} else {
			distributeToAllUsersButMeWithoutSaving(a, u);
		}
	}

	/**
	 * 
	 * @param a, u
	 * @author ZGE
	 */
	private void distributeToAllUsersButMeWithoutSaving(Action a, User u) {
		int mapID = ActionProcessor.getMapIDFromAction(a);

		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);
		ManagementController.addToAllUsersButMySelfOnMapActionQueue(ap, mapID, u);
	}

	/**
	 * Delete the element and all of his relevant elements like his children etc.
	 * 
	 * @param a a special lasad action
	 * @param u the current user
	 * @author ZGE
	 */
	public void processDeleteElement(Action a, User u) {

		int mapID = ActionProcessor.getMapIDFromAction(a);
		int elementID = Integer.parseInt(a.getParameterValue(ParameterTypes.Id));

		synchronized (ActionProcessor.DeleteUpdateJoinActionLock) {

			if (!Element.isElementActive(elementID)) {
				Logger.log("Element " + elementID + " is no longer active. Delete failed.");
				// Not necessary -> ActionPackage ap = ActionPackageFactory.error("Element is already deleted. Delete failed");
				// Logger.doCFLogging(ap);
				// ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return;
			}

			// Create new revision of the map
			Revision r = new Revision(mapID, u.getUserID());
			r.saveToDatabase();

			ActionParameter.saveParametersForRevision(r.getId(), elementID, a);

			deleteElementAndChildrenStub(elementID, r.getId(), mapID, u.getNickname(), u.getSessionID());
		}
	}

	/**
	 * collect all informations of the element's children to prepare to delete
	 * 
	 * @param elementID the Id of element
	 * @param revisionID the ID of revision
	 * @param mapID the ID of the Map
	 * @param username the name of User
	 * @param sessionID the ID of the Session
	 * @author ZGE
	 */

	private void deleteElementAndChildrenStub(int elementID, int revisionID, int mapID, String username, String sessionID)
	{
		Element.updateEndRevisionID(elementID, revisionID);

		ActionPackage deleteBoxActionPackage = ActionPackageFactory.deleteElement(mapID, elementID, username);

		// need one action package with all delete actions for logging
		ActionPackage deleteAllActionPackage = new ActionPackage();
		deleteAllActionPackage.addAction(deleteBoxActionPackage.getActions().get(0));
		// get all childElement IDs of this element
		Vector<Integer> childElements = Element.getChildElementIDs(elementID);
		for (Integer i : childElements) {
			deleteElementAndChildrenRecursive(i, revisionID, mapID, username, sessionID, deleteAllActionPackage);
		}

		// log only the one large action package
		removeMetaInformation(deleteAllActionPackage);
		this.aproc.addMetaInformation(deleteAllActionPackage, sessionID);
		Logger.doCFLogging(deleteAllActionPackage);



		removeMetaInformation(deleteBoxActionPackage);

		// send out delete of only top-level box, other deletes sent separately
		ManagementController.addToAllUsersOnMapActionQueue(deleteBoxActionPackage, mapID);

		if (DS_LOGGING_IS_ON)
		{
			logToDataShop(deleteBoxActionPackage.getActions().get(0), username, sessionID);
		}
	}

	/**
	 * collect all informations of the children of an element's children to prepare to delete
	 * 
	 * @param elementID the Id of element
	 * @param revisionID the ID of revision
	 * @param mapID the ID of the Map
	 * @param username the name of User
	 * @param sessionID the ID of the Session
	 * @param deleteAllActionPackage an ActionPackage including actions for deleting an element and his chirldren
	 * @author ZGE
	 */
	private void deleteElementAndChildrenRecursive(int elementID, int revisionID, int mapID, String username, String sessionID, ActionPackage deleteAllActionPackage)
	{
		ActionPackage ap = ActionPackageFactory.deleteElement(mapID, elementID, username);
		Action a = ap.getActions().get(0);
		if (DS_LOGGING_IS_ON && Element.isElementActive(elementID))
		{
			logToDataShop(a, username, sessionID);
		}

		Element.updateEndRevisionID(elementID, revisionID);

		Vector<Integer> childElements = Element.getChildElementIDs(elementID);

		for (Integer i : childElements) {
			deleteElementAndChildrenRecursive(i, revisionID, mapID, username, sessionID, deleteAllActionPackage);
		}
		deleteAllActionPackage.addAction(ap.getActions().get(0));
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
	}

	// "Delete" actions end up with bad meta-information, so must be cleared for logging
	private void removeMetaInformation(ActionPackage p) {

		for (Action a : p.getActions()) {
			a.removeParameter(ParameterTypes.UserActionId);
			a.removeParameter(ParameterTypes.NumActions);
		}
	}

	// // Send actionPackage containing updated size & position of boxes back to all users    
 //    public ActionPackage updateBoxInfo(ActionPackage p, String mapID, int boxID, int width, int height) {
	// 	Action returnAction = new Action(Commands.UpdateElement, Categories.Map);
	// 	returnAction.addParameter(ParameterTypes.MapId, mapID);
	// 	returnAction.addParameter(ParameterTypes.Id, String.valueOf(boxID));
	// 	// returnAction.addParameter(ParameterTypes.PosX, String.valueOf(x));
	// 	// returnAction.addParameter(ParameterTypes.PosY, String.valueOf(y));
	// 	returnAction.addParameter(ParameterTypes.Width, String.valueOf(width));
	// 	returnAction.addParameter(ParameterTypes.Height, String.valueOf(height));
	// 	p.addAction(returnAction);
	// 	return p;
	// }
/*
	private void updateBoxPositions(Set<LinkedBox> boxes)
	{ 
		for (LinkedBox box : boxes)
		{
			int intX = (int) Math.round(box.getXLeft());
			int intY = (int) Math.round(box.getYTop());
			if (this.controller.getElement(box.getBoxID()) != null)
			{
				communicator.sendActionPackage(actionBuilder.updateBoxPosition(map.getID(), box.getBoxID(), intX, intY));
			}
			else
			{
				Logger.log("ERROR: Tried to update box position of nonexisting element.", Logger.DEBUG);
				this.argModel.removeBoxByBoxID(box.getBoxID());
			}
		}
	}
	*/

	// ******** COPIED FROM AUTOORGANIZER ********

	// The width that all boxes will take upon organization
	private int boxWidth = 200;

	// The minimum height boxes will take upon organization (if the box needs more height to fit text, it will expand)
	private int minBoxHeight = 100;

	// The default minimum space between rows of boxes when organized
	private final double DEFAULT_MIN_VERT_SPACE = 50.0;

	private final boolean DEBUG = false;

	// The maximum number of siblings (grouped boxes) a box can have
	private final int MAX_SIBLINGS = 2;

	// Perfectly centered box location
	private final double CENTER_X = 2400.0;
	private final double CENTER_Y = CENTER_X;

	// The orientation of the maps links (true for down, false for up)
	private boolean downward = false;

	private final String BOX = "box";
	private final String RELATION = "relation";
	private final int DEFAULT_HEIGHT = 107;

	// The map that this instance of AutoOrganizer corresponds to
	// ******************************************************************************
	// ********************** NOT SURE WHAT THIS IS USED FOR ************************
	// ******************************************************************************
	// private AbstractGraphMap map;

	// // For sending map updates to the server
	// private LASADActionSender communicator = LASADActionSender.getInstance();
	// private ActionFactory actionBuilder = ActionFactory.getInstance();
	// private MVController controller;

	// The organization model that we will update (the actual model the server updates is contained within the controller)
	private ArgumentModel argModel;

	public ArgumentModel getArgModelFromDatabase(int mapID) {

		String mapIDString = String.valueOf(mapID);
		ArgumentModel argModel = new ArgumentModel(mapIDString);

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement getArgModel = null;
		
		try {

			con = DatabaseConnectionHandler.getConnection(MapActionProcessor.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getArgModel = con.prepareStatement("SELECT * FROM "+Config.dbName+".argment_model WHERE map_id = ? AND end_revision_id = ? ;");
			getArgModel.setInt(1, mapID);
			getArgModel.setString(2, "null");

			rs = getArgModel.executeQuery();
			
			if(rs.next()) {

				// Add the element to argument model.
				// value = rs.getString("value");

				String elementType = rs.getString("element_type");
				int elementID = rs.getInt("element_id");

				// Since we're creating a new element, we need to add it to the autoOrganize model and update the siblingLinks on the map
				String elementSubType = String.valueOf(elementID); // a.getParameterValue(ParameterTypes.ElementId);
				int rootID = rs.getInt("root_id");

				// In order to keep the variable available for database connection later.
				double xLeft = 0;
				double yTop = 0;
				int width = 0;
				int height = 0;
				Boolean canBeGrouped = false;
				Boolean connectsGroup = false;
				int startBoxID = -1;
				int endBoxID = -1;

				// If it's a box, add it to the model
				if (elementType.equalsIgnoreCase(BOX))
				{

					xLeft = rs.getInt("x_left");
					yTop = rs.getInt("y_top");

					// This should also be originally stored in SQL tables, but now we just get rid of it to test the code.
					// ElementInfo newBoxInfo = controller.getMapInfo().getElementsByType(BOX).get(elementModel.getValue(ParameterTypes.ElementId));
					// if (newBoxInfo == null)
					// {
					// 	 Logger.log("newBoxDimenions are null", Logger.DEBUG);
					// }

					width = rs.getInt("width");
					height = rs.getInt("height");
					canBeGrouped = Boolean.parseBoolean(rs.getString("can_be_grouped"));

					argModel.addArgThread(new ArgumentThread(new LinkedBox(elementID, rootID, elementSubType, xLeft, yTop, width, height, canBeGrouped)));
							
					//Added by DSF, run setFontSize so new boxes get the right font size
					argModel.setFontSize(argModel.getFontSize(), false);

				}

				// If it's a relation, adda it to the model
				else if (elementType.equalsIgnoreCase(RELATION))
				{	
					startBoxID = rs.getInt("start_box_id");
					endBoxID = rs.getInt("end_box_id");

					LinkedBox startBox = argModel.getBoxByBoxID(startBoxID);
					LinkedBox endBox = argModel.getBoxByBoxID(endBoxID);

					if (startBox != null && endBox != null) 
					{

						connectsGroup = Boolean.parseBoolean(rs.getString("connects_group"));

						OrganizerLink link = new OrganizerLink(elementID, startBox, endBox, elementSubType, connectsGroup);
						if (link.getConnectsGroup())
						{
							startBox.addSiblingLink(link);
							endBox.addSiblingLink(link);
						}
						else
						{
							startBox.addChildLink(link);
							endBox.addParentLink(link);
						}

						ArgumentThread startBoxThread = argModel.getBoxThread(startBox);

						ArgumentThread endBoxThread = argModel.getBoxThread(endBox);

						if (!startBoxThread.equals(endBoxThread))
						{
							startBoxThread.addBoxes(endBoxThread.getBoxes());
							argModel.removeArgThread(endBoxThread);
						}

						// Not sure if this is needed or not, since it calls AutoOrganizer.
						// String siblingsAlreadyUpdated = a.getParameterValue(ParameterTypes.SiblingsAlreadyUpdated);

						// if (siblingsAlreadyUpdated != null)
						// {
						// 	if (!Boolean.parseBoolean(siblingsAlreadyUpdated))
						// 	{
						// 		autoOrganizer.updateSiblingLinks(link);
						// 	}
						// }

					}
				}

			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { rs.close(); } catch (Exception e) { /* ignored */ }
    		try { getArgModel.close(); } catch (Exception e) { /* ignored */ }
			if(con != null) {
				//close Connection
				DatabaseConnectionHandler.closeConnection(MapActionProcessor.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}

		// *************************************************************************************
		// ******** copied from LASADActionReceiver, try to store ArgModel in SQL table ********
		// *************************************************************************************
		
		// String elementType = this.typeValue; // a.getParameterValue(ParameterTypes.Type);
		// String elementIDString = a.getParameterValue(ParameterTypes.Id);
		// // DON'T KNOW WHY THESE DON'T WORK.
		// if (elementIDString == null) {
		// 	elementIDString = a.getParameterValue(ParameterTypes.ElementId);
		// }
		// int elementID = -1;
		// // if (elementIDString != null) {
		// // 	Integer.parseInt(elementIDString);
		// // }


		// // String username = a.getParameterValue(ParameterTypes.UserName);

		// // Try creating a new argModel, but should be retreived from the SQL table in the end.
		// // ArgumentModel argModel = LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getMyMap().getArgModel();
		// ArgumentModel argModel = new ArgumentModel(String.valueOf(mapID));

		// // Begin Kevin Loughlin code

		// // Since we're creating a new element, we need to add it to the autoOrganize model and update the siblingLinks on the map
		// String elementSubType = elementIDString; // a.getParameterValue(ParameterTypes.ElementId);

		// // String rootIDString = a.getParameterValue(ParameterTypes.RootElementId);

		// int rootID = -1;

		// // In order to keep the variable available for database connection later.
		// double xLeft = 0;
		// double yTop = 0;
		// int width = 0;
		// Boolean canBeGrouped = false;
		// Boolean connectsGroup = false;
		// int startBoxID = -1;
		// int endBoxID = -1;

		// // if (rootIDString != null)
		// // {
		// // 	rootID = Integer.parseInt(rootIDString);
		// // }

		// // If it's a box, add it to the model
		// if (elementType.equalsIgnoreCase(BOX))
		// {
		// 	String xLeftString = a.getParameterValue(ParameterTypes.PosX);
		// 	xLeft = 2400; // double xLeft;
		// 	if (xLeftString != null)
		// 	{
		// 		xLeft = Double.parseDouble(xLeftString);
		// 	}
		// 	else
		// 	{
		// 		// Logger.log("Null xLeftString", Logger.DEBUG);
		// 		return;
		// 	}

		// 	String yTopString = a.getParameterValue(ParameterTypes.PosY);
		// 	yTop = 2400; // double yTop;
		// 	if (yTopString != null)
		// 	{
		// 		yTop = Double.parseDouble(yTopString);
		// 	}
		// 	else
		// 	{
		// 		// Logger.log("Null yTopString", Logger.DEBUG);
		// 		return;
		// 	}

		// 	// This should also be originally stored in SQL tables, but now we just get rid of it to test the code.
		// 	// ElementInfo newBoxInfo = controller.getMapInfo().getElementsByType(BOX).get(elementModel.getValue(ParameterTypes.ElementId));
		// 	// if (newBoxInfo == null)
		// 	// {
		// 	// 	 Logger.log("newBoxDimenions are null", Logger.DEBUG);
		// 	// }

		// 	// String widthString = newBoxInfo.getUiOption(ParameterTypes.Width);

		// 	// // The default height is set incorrectly, don't know how, but it's always 107 yet comes out as 200.
		// 	// //String heightString = newBoxInfo.getUiOption(ParameterTypes.Height);
		// 	// String canBeGroupedString = newBoxInfo.getElementOption(ParameterTypes.CanBeGrouped);
		// 	// boolean canBeGrouped;
		// 	// if (canBeGroupedString == null)
		// 	// {
		// 	// 	canBeGrouped = false;
		// 	// }
		// 	// else
		// 	// {
		// 	// 	canBeGrouped = Boolean.parseBoolean(canBeGroupedString);
		// 	// }

		// 	// if (widthString == null)
		// 	// {
		// 	// 	Logger.log("width string is null", Logger.DEBUG);
		// 	// }

		// 	// Try this.
		// 	// int width = Integer.parseInt(widthString);

		// 	// Added merely to test.
		// 	canBeGrouped = false;
		// 	// Boolean canBeGrouped = false;
		// 	// String widthString = a.getParameterValue(ParameterTypes.Width);
		// 	// int width = Integer.parseInt(widthString);

		// 	argModel.addArgThread(new ArgumentThread(new LinkedBox(elementID, rootID, elementSubType, xLeft, yTop, width, DEFAULT_HEIGHT, canBeGrouped)));
					
		// 	//Added by DSF, run setFontSize so new boxes get the right font size
		// 	argModel.setFontSize(argModel.getFontSize(), false);
		// }

		// // If it's a relation, add it to the model
		// else if (elementType.equalsIgnoreCase(RELATION))
		// {	
		// 	String startBoxStringID = a.getParameterValues(ParameterTypes.Parent).get(0);
		// 	/* int */ startBoxID = Integer.parseInt(startBoxStringID);

		// 	// For some reason, parent here gives box ID, not root ID, so plan accordingly
		// 	String endBoxStringID = a.getParameterValues(ParameterTypes.Parent).get(1);
		// 	/* int */ endBoxID = Integer.parseInt(endBoxStringID);

		// 	LinkedBox startBox = argModel.getBoxByBoxID(startBoxID);
		// 	LinkedBox endBox = argModel.getBoxByBoxID(endBoxID);

		// 	if (startBox == null || endBox == null)
		// 	{
		// 		// Logger.log("Error: sibling links could not be updated: null box", Logger.DEBUG);
		// 		return;
		// 	}

		// 	// // Also should be retrieved from SQL
		// 	// ElementInfo newLinkInfo = controller.getMapInfo().getElementsByType(RELATION).get(elementModel.getValue(ParameterTypes.ElementId));
		// 	// String connectsGroupString = newLinkInfo.getElementOption(ParameterTypes.ConnectsGroup);
		// 	// boolean connectsGroup;
		// 	// if (connectsGroupString == null)
		// 	// {
		// 	// 	connectsGroup = false;
		// 	// }
		// 	// else
		// 	// {
		// 	// 	connectsGroup = Boolean.parseBoolean(connectsGroupString);
		// 	// }	

		// 	// Added to test code
		// 	/* Boolean */ connectsGroup = false;

		// 	OrganizerLink link = new OrganizerLink(elementID, startBox, endBox, elementSubType, connectsGroup);
		// 	if (link.getConnectsGroup())
		// 	{
		// 		startBox.addSiblingLink(link);
		// 		endBox.addSiblingLink(link);
		// 	}
		// 	else
		// 	{
		// 		startBox.addChildLink(link);
		// 		endBox.addParentLink(link);
		// 	}

		// 	ArgumentThread startBoxThread = argModel.getBoxThread(startBox);

		// 	ArgumentThread endBoxThread = argModel.getBoxThread(endBox);

		// 	if (!startBoxThread.equals(endBoxThread))
		// 	{
		// 		startBoxThread.addBoxes(endBoxThread.getBoxes());
		// 		argModel.removeArgThread(endBoxThread);
		// 	}

		// 	// Not sure if this is needed or not, since it calls AutoOrganizer.
		// 	// String siblingsAlreadyUpdated = a.getParameterValue(ParameterTypes.SiblingsAlreadyUpdated);

		// 	// if (siblingsAlreadyUpdated != null)
		// 	// {
		// 	// 	if (!Boolean.parseBoolean(siblingsAlreadyUpdated))
		// 	// 	{
		// 	// 		autoOrganizer.updateSiblingLinks(link);
		// 	// 	}
		// 	// }
		// }
		// // End Kevin Loughlin

		// *************************************************************************************
		// ******** copied from LASADActionReceiver, try to store ArgModel in SQL table ********
		// *************************************************************************************

		return argModel;

	}

	public boolean getOrientation()
	{
		return downward;
	}

	public void setOrientation(boolean downward)
	{
		this.downward = downward;
	}

	public void setBoxWidth(int width)
	{
		this.boxWidth = width;
	}

	public void setMinBoxHeight(int minBoxHeight)
	{
		this.minBoxHeight = minBoxHeight;
	}

	public int getBoxWidth()
	{
		return boxWidth;
	}

	public int getMinBoxHeight()
	{
		return minBoxHeight;
	}


	// TODO Judy.  Here is where you can process how to handle auto organization.  My suggestion is you do a sequence of box repositioning commands that you send back to the client.
	private void processAutoOrganize(Action a, User u)
	{

		// Begin as default, but update later if more space is needed (ex. maybe a link has a large panel)
		double minVertSpace = DEFAULT_MIN_VERT_SPACE;

		// Get orientation of auto-organization from action sent by the client
		final boolean orientation = Boolean.valueOf(a.getParameterValue(ParameterTypes.OrganizerOrientation));

		// The orientation of the maps links (true for down, false for up)
		boolean downward = !orientation;
		final boolean DOWNWARD = downward;

		int mapID = ActionProcessor.getMapIDFromAction(a);

		// Get argument model from database;
		argModel = getArgModelFromDatabase(mapID);

		// Get the parameters from Action a
		boxWidth = Integer.parseInt(a.getParameterValue(ParameterTypes.OrganizerBoxWidth));
		minBoxHeight = Integer.parseInt(a.getParameterValue(ParameterTypes.OrganizerBoxHeight));

		// Update boxsizes
		Connection con = null;
		PreparedStatement getBoxesStmt = null;
		PreparedStatement updateBoxSizeToDatabase = null;
		ResultSet rs = null;

		try {

			con = DatabaseConnectionHandler.getConnection(MapActionProcessor.class);

			// updateBoxSizeToDatabase = con.prepareStatement("UPDATE lasad.argument_model SET width = 200, height = 107 WHERE map_id = 1 AND element_type = \"box\" ;");
			updateBoxSizeToDatabase = con.prepareStatement("UPDATE lasad.elements SET map_id = 1 ;");
			updateBoxSizeToDatabase.executeQuery();

			getBoxesStmt = con.prepareStatement("SELECT id FROM "+Config.dbName+".elements WHERE type = \"box\" AND map_id = ?");
			getBoxesStmt.setInt(1, mapID);
			rs = getBoxesStmt.executeQuery();
			while (rs.next()) {

		  		int boxID = rs.getInt("id");

				Action returnAction = new Action(Commands.UpdateElement, Categories.Map);
				returnAction.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
				returnAction.addParameter(ParameterTypes.Id, String.valueOf(boxID));
				returnAction.addParameter(ParameterTypes.Width, String.valueOf(boxWidth));
				returnAction.addParameter(ParameterTypes.Height, String.valueOf(minBoxHeight));
				processUpdateElement(returnAction, u);

			}

			// updateBoxSizeToDatabase = con.prepareStatement("UPDATE "+Config.dbName+".argument_model SET width = ? , height = ? WHERE map_id = ? AND element_type = \"box\" ;");
			// updateBoxSizeToDatabase.setInt(1, boxWidth);
			// updateBoxSizeToDatabase.setInt(2, minBoxHeight);
			// updateBoxSizeToDatabase.setInt(3, mapID);
			// updateBoxSizeToDatabase.executeQuery();

		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{getBoxesStmt.close();}catch(Exception e){}
			try{updateBoxSizeToDatabase.close();}catch(Exception e){}
			try{rs.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(MapActionProcessor.class, con);
			}
		}

		// This part now deals with update of box positions
		// Try catch just because organizeMap is complicated and if there's an error we don't want a crash
		try
		{
			Set<LinkedBox> boxesToSendToServer = new HashSet<LinkedBox>();

			double columnXcoord = CENTER_X;

			// Organize the grid by height and width "levels" (think chess board)
			for (ArgumentThread argThread : argModel.getArgThreads())
			{
				// organizes the grid, and we will then base coordinate positions from this grid organization
				argThread.organizeGrid(DOWNWARD);
				ArgumentGrid grid = argThread.getGrid();
				if (grid.getBoxes().size() == 0)
				{
					continue;
				}

				// // Take into account the possibility of large elements on the link panel when determining vertical spacing between rows
				// List<Component> mapComponents = map.getItems();
				// for (Component mapComponent : mapComponents)
				// {
				// 	if(mapComponent instanceof AbstractLinkPanel)
				// 	{
				// 		AbstractLinkPanel linkPanel = (AbstractLinkPanel) mapComponent;
				// 		if (linkPanel.getMyLink().getExtendedElements().size() != 0)
				// 		{
				// 			minVertSpace = (int) Math.max(minVertSpace, linkPanel.getSize().height + DEFAULT_MIN_VERT_SPACE);
				// 		}
				// 	}
				// }

				// // Important to make a copy so that we don't modify the actual mapComponents when we remove nonbox elements for below loop speed
				// List<Component> mapComponentsCopy = new ArrayList<Component>(mapComponents);
				// ArrayList<Component> toRemove = new ArrayList<Component>();

				/*	We need to call setSize from abstractBox, so we temporarily shift from LinkedBoxes to AbstractBoxes
					I'm aware it's annoying that we have two types of boxes representing the same thing, but it made life a lot easier
					for auto organization to have almost everything I needed in one place (LinkedBox) */
				// for (LinkedBox box : grid.getBoxes())
				// {
				// 	for (Component mapComponent : mapComponentsCopy)
				// 	{
				// 		if (mapComponent instanceof AbstractBox)
				// 		{
				// 			AbstractBox myBox = (AbstractBox) mapComponent;
				// 			if (myBox.getConnectedModel().getId() == box.getBoxID())
				// 			{
				// 				/*	The setSize method will update the textBox appropriately.  We want the box height to be just enough to fit the text
				// 					By shrinking it first, we allow it to be the minimum height and width possible */
				// 				toRemove.add(mapComponent);
				// 				myBox.setSize(boxWidth, minBoxHeight);
				// 				box.setSize(boxWidth, minBoxHeight);

				// 				for (AbstractExtendedElement childElement : myBox.getExtendedElements())
				// 				{
				// 					if (childElement instanceof AbstractExtendedTextElement)
				// 					{
				// 						// Updates the LinkedBox instance to correspond with the AbstractBox
				// 						AbstractExtendedTextElement textElt = (AbstractExtendedTextElement) childElement;
				// 						myBox.textAreaCallNewHeightgrow(textElt.determineBoxHeightChange());
				// 						box.setHeight(myBox.getHeight());
				// 						break;
				// 					}
				// 				}
				// 				updateBoxSize(u, box, mapID, boxWidth, minBoxHeight);
				// 				break;
				// 			}
				// 		}
				// 		else
				// 		{
				// 			toRemove.add(mapComponent);
				// 		}
				// 	}
				// 	// mapComponentsCopy.removeAll(toRemove);
				// }
				
				IntPair minMaxColumn = ArgumentGrid.determineMinMaxWidthLevels(grid.getBoxes());
				final int MIN_WIDTH_LEVEL = minMaxColumn.getMin();
				final int MAX_WIDTH_LEVEL = minMaxColumn.getMax();

				IntPair minMaxRow = ArgumentGrid.determineMinMaxHeightLevels(grid.getBoxes());
				final int MIN_HEIGHT_LEVEL = minMaxRow.getMin();
				final int MAX_HEIGHT_LEVEL = minMaxRow.getMax();

				// Sets the y coord either top to bottom or bottom to top, providing space between each row. Each thread should start at the same y-coord
				if (DOWNWARD)
				{
					double rowYcoord = CENTER_Y;
					for (int rowCount = MAX_HEIGHT_LEVEL; rowCount >= MIN_HEIGHT_LEVEL; rowCount--)
					{
						ArrayList<LinkedBox> row = grid.getBoxesAtHeightLevel(rowCount);

						// Make sure there's room at each row to accomodate the tallest box
						int tallestHeightAtRow = Integer.MIN_VALUE;
						for (LinkedBox box : row)
						{
							box.setYTop(rowYcoord);
							
							final int BOX_HEIGHT = box.getHeight();
							if (BOX_HEIGHT > tallestHeightAtRow)
							{
								tallestHeightAtRow = BOX_HEIGHT;
							}				
						}

						// Add space between rows; a value of MIN_VALUE would indicate there were no boxes at the row, which should be impossible
						if (tallestHeightAtRow != Integer.MIN_VALUE)
						{
							rowYcoord += tallestHeightAtRow + minVertSpace;
						}
						else
						{
							rowYcoord += minVertSpace;
						}
					}
				}
				else
				{
					double nextRowYcoord = CENTER_Y;
					for (int rowCount = MIN_HEIGHT_LEVEL; rowCount <= MAX_HEIGHT_LEVEL; rowCount++)
					{
						ArrayList<LinkedBox> row = grid.getBoxesAtHeightLevel(rowCount);

						for (LinkedBox box : row)
						{
							box.setYTop(nextRowYcoord);
						}

						ArrayList<LinkedBox> nextRow = grid.getBoxesAtHeightLevel(rowCount + 1);
						if (nextRow.size() > 0)
						{
							int tallestHeightAtNextRow = Integer.MIN_VALUE;
							for (LinkedBox box : nextRow)
							{
								final int BOX_HEIGHT = box.getHeight();
								if (BOX_HEIGHT > tallestHeightAtNextRow)
								{
									tallestHeightAtNextRow = BOX_HEIGHT;
								}
							}

							nextRowYcoord = nextRowYcoord - minVertSpace - tallestHeightAtNextRow;
						}
					}
				}

				// Sets the x coord left to right
				for (int columnNumber = MIN_WIDTH_LEVEL; columnNumber <= MAX_WIDTH_LEVEL; columnNumber++)
				{
					Set<LinkedBox> column = grid.getBoxesAtWidthLevel(columnNumber);
					for (LinkedBox box : column)
					{
						box.setXLeft(columnXcoord);
						box.setYTop(2400);
						boxesToSendToServer.add(box);
					}
					// Space between boxes
					columnXcoord += (boxWidth * 3) / 4;
				}

				// Give an extra space between threads
				columnXcoord += boxWidth;

				// if (DEBUG)
				// {
				// 	Logger.log(grid.toString(), Logger.DEBUG);
				// 	Logger.log(argThread.toString(), Logger.DEBUG);
				// }
			}

			// Send the new positions to the server
			updateBoxPositions(u, mapID, boxesToSendToServer);

		}
		// Just in case
		catch (Exception e)
		{
			// LASADInfo.display("Error", "An unknown error has occurred - current arrangement of map cannot be auto organized.");
			e.printStackTrace();
			// Logger.log(e.toString(), Logger.DEBUG);
			// Logger.log(e.getMessage(), Logger.DEBUG);
			// Logger.log(e.getStackTrace().toString(), Logger.DEBUG);
			// Logger.log(e.getClass().toString(), Logger.DEBUG);
		}
		finally
		{
			// // Position the cursor of the map
			// final double[] SCROLL_EDGE = determineScrollEdge(DOWNWARD);

			// // Free some memory for speed (garbage collector will take the nullified values)
			// for (ArgumentThread argThread : argModel.getArgThreads())
			// {
			// 	argThread.getGrid().empty();
			// }

			// // Close Connection
			// if(con != null) {
			// 	DatabaseConnectionHandler.closeConnection(MapActionProcessor.class, con);
			// }

		}

	}

	// ******************************************************************
	// ******** HELPER FUNCTIONS COPIED FROM ORGANIZATION FOLDER ********
	// *************************** DOWN *********************************

	/**
	 * Updates the sibling boxes (i.e. grouped boxes) related to the creation of a new link.
	 * For example, if box A is attached to box B via a group link, and box B gets a new child, then box A should also
	 * have a relation pointing to that child.  This method uses the private helper method "updateRecursive" to do its dirty work.
	 * @param link - The new, user-drawn link from which we must search for possibly necessary additional new links
	 */
	public void updateSiblingLinks(OrganizerLink link)
	{
		Set<OrganizerLink> linksToCreate = new HashSet<OrganizerLink>();

		// The original link data
		LinkedBox origStartBox = link.getStartBox();
		LinkedBox origEndBox = link.getEndBox();
		//String linkType = link.getType();

		// If the newly created link connects a group, make sure all the gorup members point to the same children
		if (link.getConnectsGroup())
		{
			Set<OrganizerLink> origStartChildLinks = origStartBox.getChildLinks();
			Set<LinkedBox> origStartChildBoxes = origStartBox.getChildBoxes();

			Set<OrganizerLink> origEndChildLinks = origEndBox.getChildLinks();
			Set<LinkedBox> origEndChildBoxes = origEndBox.getChildBoxes();

			for (OrganizerLink origStartChildLink : origStartChildLinks)
			{
				LinkedBox newChildBox = origStartChildLink.getEndBox();
				if (!origEndChildBoxes.contains(newChildBox))
				{
					OrganizerLink newLink = new OrganizerLink(origEndBox, newChildBox, origStartChildLink.getType(), origStartChildLink.getConnectsGroup());
					linksToCreate.add(newLink);
				}
			}

			for (OrganizerLink origEndChildLink : origEndChildLinks)
			{
				LinkedBox newChildBox = origEndChildLink.getEndBox();
				if (!origStartChildBoxes.contains(newChildBox))
				{
					OrganizerLink newLink = new OrganizerLink(origStartBox, newChildBox, origEndChildLink.getType(), origEndChildLink.getConnectsGroup());
					linksToCreate.add(newLink);
				}
			}
		}
		// else make sure all the grouped parents of the link's child point to the child
		else
		{
			Set<LinkedBox> origStartSiblingBoxes = origStartBox.getSiblingBoxes();

			// We only need the first one, hence why I break, but I use this for loop in case origStartSiblingBoxes is empty so that it will skip
			for (LinkedBox origStartSiblingBox : origStartSiblingBoxes)
			{
				linksToCreate = updateRecursive(origStartSiblingBox, origEndBox, link, new VisitedAndLinksHolder()).getLinks();
				break;
			}
		}

		// ****************** NOT SURE WHAT THIS IS FOR *********************
		// addLinksToVisual(linksToCreate);
	}

	/**
	 *	Holds the visited boxes and links accumulated in a recursive method, with the benefit of one data structure
	 *	For use with updateSiblingLinks and updateRecursive
	 */
	class VisitedAndLinksHolder
	{
		private Set<LinkedBox> visited;
		private Set<OrganizerLink> links;

		public VisitedAndLinksHolder()
		{
			visited = new HashSet<LinkedBox>();
			links = new HashSet<OrganizerLink>();
		}

		public void addVisited(LinkedBox box)
		{
			visited.add(box);
		}

		public void addLink(OrganizerLink link)
		{
			links.add(link);
		}

		public Set<LinkedBox> getVisited()
		{
			return visited;
		}

		public Set<OrganizerLink> getLinks()
		{
			return links;
		}
	}

	/*	
	 *	Recursively checks the siblings of a given start box to see if they need to be updated with a new relation.
	 *	Keeps track of boxes visited so that the method will eventually end.
	 *	@param startBox - The box from which we might make a link
	 *	@param END_BOX - The constant box to which we will be connecting
	 *	@param LINK_TYPE - The constant type of connection we will make if necessary
	 *  @param holder - The visited boxes and the links that need to be created, should be initialized as empty
	*/
	private VisitedAndLinksHolder updateRecursive(LinkedBox startBox, final LinkedBox END_BOX, final OrganizerLink LINK_DATA, VisitedAndLinksHolder holder)
	{
		if (!holder.getVisited().contains(startBox))
		{
			holder.addVisited(startBox);
			if (!startBox.getChildBoxes().contains(END_BOX))
			{
				holder.addLink(new OrganizerLink(startBox, END_BOX, LINK_DATA.getType(), LINK_DATA.getConnectsGroup()));
			}
			for (LinkedBox siblingBox : startBox.getSiblingBoxes())
			{
				holder = updateRecursive(siblingBox, END_BOX, LINK_DATA, holder);
			}
		}
		return holder;
	}

	/**
	 *	Determines whether or not the passed group OrganizerLink can be created on the map, which depends on invariants such as
	 *	the maximum number of permitted siblings per box, both boxes being the same type, groupable, and no conflicting links between siblings.
	 *	@param link - The link to check for valid creation
	 *	@return Success/error integer code: 0 for success, greater than 0 for error code
	 */
	public int groupedBoxesCanBeCreated(OrganizerLink link)
	{
		LinkedBox startBox = link.getStartBox();
		LinkedBox endBox = link.getEndBox();

		// Boxes shouldn't be null
		if (startBox == null || endBox == null)
		{
			return GroupedBoxesStatusCodes.NULL_BOX;
		}

		// Can't create a link to self
		if (startBox.equals(endBox))
		{
			return GroupedBoxesStatusCodes.SAME_BOX;
		}

		// Checks if they are of groupable type
		if (startBox.getCanBeGrouped())
		{
			// Checks that both boxes are of same type, else return error code
			if (startBox.getType().equalsIgnoreCase(endBox.getType()))
			{
				// Checks that they both have fewer than 2 siblings, else return error code
				if (startBox.getNumSiblings() < MAX_SIBLINGS && endBox.getNumSiblings() < MAX_SIBLINGS)
				{
					// See this.isCompatible for what the method within this if statement checks, else return error code
					if (this.isCompatible(startBox, endBox))
					{
						return GroupedBoxesStatusCodes.SUCCESS;
					}
					else
					{
						return GroupedBoxesStatusCodes.TWO_WAY_LINK;
					}
				}
				else
				{
					return GroupedBoxesStatusCodes.TOO_MANY_SIBS;
				}
			}
			else
			{
				return GroupedBoxesStatusCodes.NOT_SAME_TYPE;
			}
		}
		else
		{
			return GroupedBoxesStatusCodes.CANT_BE_GROUPED;
		}
	}

	/*	
	 *	Checks that there aren't existing invalid connections between the startBoxAndExtSibs group and the end Box children.
	 *	For example, a group link can't be created between box A and B if box A has a child box (C) that is also a parent of Box B.
	 *	This is because B would then also have to point to C (creating a 2 way link) to maintain that grouped boxes point to the same children.
	 *	@param startBoxAndExtSibs - The startBox for the new link and its extended siblings
	 *	@param endBox - The end box for the new link
	 *	@return true if there are no conflicts, false if the grouped boxes should not be created
	 */
	private boolean isCompatible(LinkedBox startBox, LinkedBox endBox)
	{
		Set<LinkedBox> startChildBoxes = startBox.getChildBoxes();
		Set<LinkedBox> endChildBoxes = endBox.getChildBoxes();
		
		for (LinkedBox startChildBox : startChildBoxes)
		{
			if (endBox.hasNonChildLinkWith(startChildBox))
			{
				return false;
			}
		}

		for (LinkedBox endChildBox : endChildBoxes)
		{
			if (startBox.hasNonChildLinkWith(endChildBox))
			{
				return false;
			}
		}

		return true;
	}

	/*
	 *	Updates a box's visual position on the map
	 *	@param box - The box whose position we will update with its new coordinates
	 */
	// Modified by Judy Kong
	private void updateBoxPositions(User u, int mapID, Set<LinkedBox> boxes)
	{

		// ActionPackage p = new ActionPackage();
		Connection con = null;
		PreparedStatement updateBoxPositionToDatabase = null;
		
		try {

			con = DatabaseConnectionHandler.getConnection(MapActionProcessor.class);

			for (LinkedBox box : boxes)
			{
				double xLeft = box.getXLeft();
				double yTop = box.getYTop();
				int intX = (int) Math.round(xLeft);
				int intY = (int) Math.round(yTop);
				int boxID = box.getBoxID();

				Action returnAction = new Action(Commands.UpdateElement, Categories.Map);
				returnAction.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
				returnAction.addParameter(ParameterTypes.Id, String.valueOf(boxID));
				returnAction.addParameter(ParameterTypes.PosX, String.valueOf(intX));
				returnAction.addParameter(ParameterTypes.PosY, String.valueOf(intY));
				// p.addAction(returnAction);

				updateBoxPositionToDatabase = con.prepareStatement("UPDATE "+Config.dbName+".argument_model SET x_left = ? AND y_top = ? WHERE map_id = ? AND element_id = ? ;");
				updateBoxPositionToDatabase.setDouble(1, xLeft);
				updateBoxPositionToDatabase.setDouble(2, yTop);
				updateBoxPositionToDatabase.setInt(3, mapID);
				updateBoxPositionToDatabase.setInt(4, boxID);
				updateBoxPositionToDatabase.executeQuery();

				processUpdateElement(returnAction, u); 

			}

		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{updateBoxPositionToDatabase.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(MapActionProcessor.class, con);
			}
		}


		// Logger.doCFLogging(p);	
		// ManagementController.addToAllUsersOnMapActionQueue(p, mapID);

	}

	/*
	 *	Shockingly, this updates a box's visual size on the map
	 *	@param box - The box whose size we will send to the server for an update
	 */
	// Modified by Judy Kong
	private void updateBoxSize(User u, LinkedBox box, int mapID, int boxWidth, int minBoxHeight)
	{

		// ActionPackage p = new ActionPackage();
		int boxID = box.getBoxID();
		Action returnAction = new Action(Commands.UpdateElement, Categories.Map);
		returnAction.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
		returnAction.addParameter(ParameterTypes.Id, String.valueOf(boxID));
		returnAction.addParameter(ParameterTypes.OrganizerBoxWidth, String.valueOf(boxWidth));
		returnAction.addParameter(ParameterTypes.OrganizerBoxHeight, String.valueOf(minBoxHeight));
		// p.addAction(returnAction);

		processUpdateElement(returnAction, u); 
		// Logger.doCFLogging(p);	
		// ManagementController.addToAllUsersOnMapActionQueue(p, mapID);

	}

	// /*
	//  *	Positions the map cursor either with the top most box(es) at the top of the map or bottom-most at the bottom
	//  *	@param DOWNWARD - if true, put the bottom boxes at the bottom of the screen, false do other option
	//  *	The "edge" is the bottom of the bottom row of boxes in the case of true; top of the top row in case of false
	//  */
	// private double[] determineScrollEdge(final boolean DOWNWARD)
	// {
	// 	// // Where end level is either top or bottom depending on DOWNWARD
	// 	// ArrayList<LinkedBox> boxesAtEndLevel = new ArrayList<LinkedBox>();

	// 	// // The sum of x-coord positions at the edge row
	// 	// double edgeSum = 0.0;

	// 	// int numEdgeBoxes = 0;
	// 	// double edgeCoordY;

	// 	// if (DOWNWARD)
	// 	// {
	// 	// 	// Find min grid height
	// 	// 	int minGridHeight = Integer.MAX_VALUE;
	// 	// 	for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
	// 	// 	{
	// 	// 		final int GRID_MIN = ArgumentGrid.determineMinMaxHeightLevels(ARG_THREAD.getGrid().getBoxes()).getMin();
	// 	// 		if (GRID_MIN < minGridHeight)
	// 	// 		{
	// 	// 			minGridHeight = GRID_MIN;
	// 	// 		}
	// 	// 	}

	// 	// 	// Find lowest bottom and lowest grid height level, accumulate boxes at end level
	// 	// 	double currentBottom = Double.MIN_VALUE;
	// 	// 	for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
	// 	// 	{
	// 	// 		final ArgumentGrid GRID = ARG_THREAD.getGrid();
			
	// 	// 		ArrayList<LinkedBox> boxesAtMinGridHeight = GRID.getBoxesAtHeightLevel(minGridHeight);
	// 	// 		for (LinkedBox box : boxesAtMinGridHeight)
	// 	// 		{
	// 	// 			boxesAtEndLevel.add(box);
	// 	// 			final double BOTTOM_EDGE = box.getYTop() + box.getHeight();
	// 	// 			if (BOTTOM_EDGE > currentBottom)
	// 	// 			{
	// 	// 				currentBottom = BOTTOM_EDGE;
	// 	// 			}
	// 	// 		}
	// 	// 	}
	// 	// 	edgeCoordY = currentBottom;
	// 	// }
	// 	// else
	// 	// {
	// 	// 	int maxGridHeight = Integer.MIN_VALUE;
	// 	// 	for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
	// 	// 	{
	// 	// 		final int GRID_MAX = ArgumentGrid.determineMinMaxHeightLevels(ARG_THREAD.getGrid().getBoxes()).getMax();
	// 	// 		if (GRID_MAX > maxGridHeight)
	// 	// 		{
	// 	// 			maxGridHeight = GRID_MAX;
	// 	// 		}
	// 	// 	}

	// 	// 	double currentTop = Double.MAX_VALUE;
	// 	// 	for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
	// 	// 	{
	// 	// 		final ArgumentGrid GRID = ARG_THREAD.getGrid();
			
	// 	// 		ArrayList<LinkedBox> boxesAtMaxGridHeight = GRID.getBoxesAtHeightLevel(maxGridHeight);
	// 	// 		for (LinkedBox box : boxesAtMaxGridHeight)
	// 	// 		{
	// 	// 			boxesAtEndLevel.add(box);
	// 	// 			final double TOP_EDGE = box.getYTop();
	// 	// 			if (TOP_EDGE < currentTop)
	// 	// 			{
	// 	// 				currentTop = TOP_EDGE;
	// 	// 			}
	// 	// 		}
	// 	// 	}
	// 	// 	edgeCoordY = currentTop;
	// 	// }

	// 	// for (LinkedBox box : boxesAtEndLevel)
	// 	// {
	// 	// 	edgeSum += box.getXCenter();
	// 	// 	numEdgeBoxes++;
	// 	// }

	// 	// // Take the average x at the edge level and put the scroller there
	// 	// if (numEdgeBoxes > 0)
	// 	// {
	// 	// 	return new double[]{edgeSum / numEdgeBoxes, edgeCoordY};
	// 	// }
	// 	// // If empty, put it at the center
	// 	// else
	// 	// {
	// 		return new double[]{CENTER_X, CENTER_Y};
	// 	// }
	// }

	/*
	 *	Wraps each new link to be created as an actionPackage, which is sent to server to be added to the model and map.
	 */
	// private void addLinksToVisual(Set<OrganizerLink> linksToCreate)
	// {
	// 	// String elementType = "relation";

	// 	// //MVController controller = LASAD_Client.getMVCController(map.getID());

	// 	// ElementInfo linkInfo = new ElementInfo();
	// 	// linkInfo.setElementType(elementType);

	// 	// for (OrganizerLink link : linksToCreate)
	// 	// {
	// 	// 	// a better name for element ID here would be subtype, as in, what kind of relation.  Alas, I didn't name it.
	// 	// 	linkInfo.setElementID(link.getType());

	// 	// 	String startBoxStringID = Integer.toString(link.getStartBox().getBoxID());
	// 	// 	String endBoxStringID = Integer.toString(link.getEndBox().getBoxID());

	// 	// 	ActionPackage myPackage = actionBuilder.autoOrganizerCreateLinkWithElements(linkInfo, map.getID(), startBoxStringID, endBoxStringID);
	// 	// 	communicator.sendActionPackage(myPackage);
	// 	// }
	// }

	/**
	 *	Determines if supplemental links need to be removed after the passed link is removed.  This might be necessary, for example,
	 *	if the removal of solely the passed link would result in a violation of the grouped boxes invariants (i.e. each sibling box must
	 *	link to each other sibling's children).
	 *	@param removedLink - The link already removed from the model, that provides an easy access point for other nearby links to remove
	 */
	// public void determineLinksToRemove(OrganizerLink removedLink)
	// {
	// 	// Set<OrganizerLink> linksToRemove = new HashSet<OrganizerLink>();

	// 	// if (!removedLink.getConnectsGroup())
	// 	// {
	// 	// 	LinkedBox startBox = removedLink.getStartBox();

	// 	// 	if (startBox != null)
	// 	// 	{
	// 	// 		Set<OrganizerLink> siblingLinks = startBox.getSiblingLinks();
	// 	// 		for (OrganizerLink link : siblingLinks)
	// 	// 		{
	// 	// 			linksToRemove.add(link);
	// 	// 		}

	// 	// 		removeLinksFromVisual(linksToRemove);
	// 	// 	}
	// 		// else
	// 		// {
	// 		// 	Logger.log("No necesito hacer nada, links boxes have already been removed", Logger.DEBUG);
	// 		// }
	// 	}
	// }

	/*
	 *	Helper method called by determineLinksToRemove that actually sends the actionPackage to the server, telling the server to
	 *	remove each necessary link from the map.
	 */
	// private void removeLinksFromVisual(Set<OrganizerLink> linksToRemove)
	// {
	// 	// for (OrganizerLink link : linksToRemove)
	// 	// {
	// 	// 	ActionPackage myPackage = actionBuilder.autoOrganizerRemoveElement(map.getID(), link.getLinkID());
	// 	// 	if (this.controller.getElement(link.getLinkID()) != null)
	// 	// 	{
	// 	// 		communicator.sendActionPackage(myPackage);
	// 	// 	}
	// 	// 	else
	// 	// 	{
	// 	// 		LinkedBox startBox = link.getStartBox();
	// 	// 		LinkedBox endBox = link.getEndBox();

	// 	// 		if (startBox != null)
	// 	// 		{
	// 	// 			startBox.removeSiblingLink(link);
	// 	// 		}
	// 	// 		if (endBox != null)
	// 	// 		{
	// 	// 			endBox.removeSiblingLink(link);
	// 	// 		}

	// 	// 		// Logger.log("ERROR: Tried to remove a null link: " + link.toString(), Logger.DEBUG);
	// 	// 	}
	// 	// }
	// }

	// *************************** UP ***********************************
	// ******** HELPER FUNCTIONS COPIED FROM ORGANIZATION FOLDER ********
	// ******************************************************************

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null && a.getCategory().equals(Categories.Map)) {

			boolean actionIsLoggable;

			switch (a.getCmd()) {
			case ChangeFontSize:
				actionIsLoggable = processChangeFontSize(a, u);
				returnValue = true;
				break;
			case CreateElement:// Check
				actionIsLoggable = processCreateElement(a, u);
				returnValue = true;
				break;
			case UpdateElement:
			case AutoResizeTextBox:
				actionIsLoggable = processUpdateElement(a, u);
				returnValue = true;
				break;
			case UpdateCursorPosition:
				processCursorUpdate(a, u);
				actionIsLoggable = false;
				returnValue = true;
				break;
			case DeleteElement:
				processDeleteElement(a, u);
				returnValue = true;

				// handled separately
				actionIsLoggable = false;
				break;
			//TODO Zhenyu
			case BackgroundImage:
				processBackgroudImage(a, u);
				returnValue = true;
				actionIsLoggable = false;
				break;
			case AutoOrganize:
				// TODO Judy: see processAutoOrganize.  When done, returnValue should be true
				actionIsLoggable = true;
				processAutoOrganize(a, u);
				returnValue = true;
				break;
			default:
				actionIsLoggable = false;
				break;
			}

			if (DS_LOGGING_IS_ON && actionIsLoggable)
			{
				logToDataShop(a, u.getNickname(), u.getSessionID());
			}
		}

		return returnValue;
	}

	private boolean processChangeFontSize(Action a, User u){
		int mapID = ActionProcessor.getMapIDFromAction(a);
		
		Revision r = createNewRevision(mapID, u, a);
		r.setDescription("Changing the font size");
		r.saveToDatabase();
		
		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);	
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
		return true;
	}
	
	//TODO Zhenyu
	private void processBackgroudImage(Action a,User u)
	{
		int mapID = ActionProcessor.getMapIDFromAction(a);
		
		//Save the url in the database
		Map.setBackgroundImage(mapID, a.getParameterValue(ParameterTypes.BackgroundImageURL));
		
		// Create new revision of the map
		Revision r = createNewRevision(mapID, u, a);
		r.setDescription("Adding a Background Image");
		r.saveToDatabase();
		
		
		ActionPackage ap = ActionPackage.wrapAction(a);
		Logger.doCFLogging(ap);	
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
	}
}