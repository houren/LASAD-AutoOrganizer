package lasad.gwt.client.communication.helper;

import java.util.Collection;
import java.util.Vector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.organization.ArgumentModel;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.questionnaire.QuestionConfig;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;

import com.google.gwt.core.client.GWT;

/**
 * Creates ActionPackages and Actions
 * 
 * @author Frank Loll
 * 
 */
public class ActionFactory {

	private ActionFactory() {
	}

	public static ActionFactory getInstance() {
		if (instance == null) {
			instance = new ActionFactory();
		}
		return instance;
	}

	private static ActionFactory instance = null;

	public ActionPackage leaveMap(String mapID) {
		ActionPackage p = new ActionPackage();

		if (GraphMap.mapIDtoCursorID.containsKey(mapID)) {
			p.addAction(removeElementAction(mapID, GraphMap.mapIDtoCursorID.get(mapID)));
		}

		Action a = new Action(Commands.Leave, Categories.Management);
		a.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
		p.addAction(a);

		return p;
	}

	public ActionPackage joinMap(String mapID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Join, Categories.Management);
//		Action a = new Action("JOIN", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Summary, "TRUE");
		p.addAction(a);
		return p;
	}

	public ActionPackage getMaps() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.List, Categories.Management);
//		Action a = new Action("LIST", "MANAGEMENT");
		p.addAction(a);
		return p;
	}

	// David Drexler Edit-BEGIN
	public ActionPackage startReplay(String mapID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Start, Categories.Replay);
//		Action a = new Action("START", "REPLAY");
		a.addParameter(ParameterTypes.MapId, mapID);
		p.addAction(a);
		return p;
	}

	// David Drexler Edit-END

	public ActionPackage saveMapToXMLFile(String mapID, String xmlString) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.SaveLocal, Categories.Map);
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.XMLSaveFile, xmlString);
		p.addAction(a);
		return p;
	}

	public ActionPackage updateBoxPosition(String mapID, int boxID, int x, int y) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(boxID));
		a.addParameter(ParameterTypes.PosX, String.valueOf(x));
		a.addParameter(ParameterTypes.PosY, String.valueOf(y));
		p.addAction(a);
		return p;
	}

	// // Created by Judy Kong
 //    public ActionPackage updateBoxInfo(ActionPackage p, String mapID, int boxID, int x, int y, int width, int height) {
	// 	Action returnAction = new Action(Commands.UpdateElement, Categories.Map);
	// 	returnAction.addParameter(ParameterTypes.MapId, mapID);
	// 	returnAction.addParameter(ParameterTypes.Id, String.valueOf(boxID));
	// 	returnAction.addParameter(ParameterTypes.PosX, String.valueOf(x));
	// 	returnAction.addParameter(ParameterTypes.PosY, String.valueOf(y));
	// 	returnAction.addParameter(ParameterTypes.Width, String.valueOf(width));
	// 	returnAction.addParameter(ParameterTypes.Height, String.valueOf(height));
	// 	p.addAction(returnAction);
	// 	return p;
	// }

	// Created by Darlan Santana Farias
	public ActionPackage changeFontSize(String mapID, int fontSize) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ChangeFontSize, Categories.Map);
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.FontSize, Integer.toString(fontSize));

		p.addAction(a);
		return p;
	}

	// private static Vector<PoolConnection> connectionPool = new Vector<PoolConnection>();
	// private static String dbName = Config.dbName;

	// /**
	//  * @author SN return an unused connection from the connectionPool vector (a connection Pool), creates a new one if there is no
	//  *         unused connection. synchronized, to ensure no two clients get the same connection.
	//  * @param Object owner -> who wants to get the connection
	//  * @return unused connection
	//  */
	// public static synchronized Connection getConnection(Object owner) {
	// 	// Date timestamp = new Date();
	// 	// System.out.println("--> Connection angefordert von: " +owner.toString()+" "
	// 	// +timestamp.getHours()+":"+timestamp.getMinutes()+":"+timestamp.getSeconds());

	// 	LinkedList<PoolConnection> toRemove = new LinkedList<PoolConnection>();
	// 	for (PoolConnection poolCon : connectionPool) {
	// 		if (!poolCon.isInUse()) { // if there is any free Connection
	// 			try {
	// 				if (poolCon.getConnection().isValid(0)) {
	// 					poolCon.setInUse(true); // set it inUse = true
	// 					poolCon.setOwner(owner);
	// 					// System.out.println("------connection reuse: Connection at Pool Position: " +
	// 					// connectionPool.indexOf(poolCon));
	// 					return poolCon.getConnection(); // break
	// 				}
	// 				// System.out.println("Removed connection from the connection pool since it has been closed");
	// 				toRemove.add(poolCon);
	// 			} catch (SQLException e) {
	// 				// TODO Auto-generated catch block
	// 				e.printStackTrace();
	// 			}
	// 		}
	// 	}
	// 	if (!toRemove.isEmpty()) {
	// 		connectionPool.removeAll(toRemove);
	// 	}

	// 	// no free Connection
	// 	Connection con;
	// 	try {
	// 		con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword); // set up a new connection
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 		return null;
	// 	}

	// 	connectionPool.addElement(new PoolConnection(con, true, owner));
	// 	Logger.debugLog("new Connection in Connection Pool");
	// 	return con;
	// }

	// /**
	//  * @author SN gives the connection back to the pool => set connection as unused
	//  * @param owner -> who wants to close
	//  * @param con -> connection to be closed
	//  */
	// public static synchronized void closeConnection(Object owner, Connection con) {

	// 	for (PoolConnection poolCon : connectionPool) {
	// 		if (poolCon.getConnection().equals(con)) {
	// 			// System.out.println("------give back Connection: Connection at Pool Position: "+connectionPool.indexOf(poolCon));

	// 			// check that only the owner of a connection can close it
	// 			boolean ownercheck = false;
	// 			if (poolCon.getOwner().equals(owner)) {
	// 				ownercheck = true;
	// 			}
	// 			assert (ownercheck);

	// 			// System.out.println("Besitzer passt"+ownercheck);
	// 			// System.out.println("Besitzer der Connection: "+poolCon.getOwner().toString()+"; Dieser hier will schliessen: "+owner.toString());
	// 			poolCon.setInUse(false);
	// 			break;
	// 		}
	// 	}
	// }

	// orientation is either true (upward) or false (downward)
	public ActionPackage autoOrganize(final String mapID, final boolean orientation, final int boxWidth, final int minBoxHeight)
	{
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.AutoOrganize, Categories.Map);
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.OrganizerOrientation, String.valueOf(orientation));
		a.addParameter(ParameterTypes.OrganizerBoxWidth, String.valueOf(boxWidth));
		a.addParameter(ParameterTypes.OrganizerBoxHeight, String.valueOf(minBoxHeight));

		// // Store argModel to SQLDatabase and get the ID for the argModel
		// Connection con = null;
		// ResultSet rs = null;
		// PreparedStatement insertArgModel = null;
		// PreparedStatement getArgmodelID = null;
		// int argModelID;

		// con = getConnection(ActionFactory.class);

		// insertArgModel = con.prepareStatement("INSERT INTO "+Config.dbName+".argmodel (id, argumentmodel) VALUES (NULL, ?);");
		// insertArgModel.setInt(1, 1);
		// insertArgModel.executeUpdate();			
		// try{insertArgModel.close();}catch(Exception e){}

		// getArgmodelID = con.prepareStatement("SELECT LAST_INSERT_ID()");
		// rs = getArgmodelID.executeQuery();
		// rs.next();
		// argModelID = rs.getInt(1);
		// try{getArgmodelID.close();}catch(Exception e){}

		// closeConnection(ActionFactory.class, con);

		// a.addParameter(ParameterTypes.ArgModel, String.valueOf(argModelID));

		p.addAction(a);
		return p;
	}
	
	public ActionPackage updateLinkPosition(String mapID, int linkID, float per) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(linkID));
		a.addParameter(ParameterTypes.Percent, String.valueOf(per));
		p.addAction(a);
		return p;
	}

	public ActionPackage updateBoxSize(String mapID, int boxID, int width, int height) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(boxID));
		a.addParameter(ParameterTypes.Width, String.valueOf(width));
		a.addParameter(ParameterTypes.Height, String.valueOf(height));
		p.addAction(a);
		return p;
	}

	public ActionPackage autoResizeTextBox(String mapID, int boxID, int width, int height) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.AutoResizeTextBox, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(boxID));
		a.addParameter(ParameterTypes.Width, String.valueOf(width));
		a.addParameter(ParameterTypes.Height, String.valueOf(height));
		p.addAction(a);
		return p;
	}

	/**
	 * 
	 * @param mapID
	 *            Map ID
	 * @param elementID
	 *            Element ID
	 * @param attribute
	 *            The attribute to change
	 * @param value
	 *            The new value of that attribute
	 * @return action package
	 */
	public ActionPackage editAttribute(String mapID, String elementID, String attribute, String value) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, elementID);
		a.addParameter(ParameterTypes.fromOldString(attribute), value);
		p.addAction(a);
		return p;
	}

	public ActionPackage lockElement(String mapID, int elementID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(elementID));
		// Will be created by Server ActionFactory 20.07.2010
		// a.addParameter("USERNAME", LASAD_Client.getInstance().getUsername());
		a.addParameter(ParameterTypes.Status, "LOCK");
		p.addAction(a);
		return p;
	}

	public ActionPackage unlockElement(String mapID, int elementID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(elementID));
		// Will be created by Server ActionFactory 20.07.2010
		// a.addParameter("USERNAME", LASAD_Client.getInstance().getUsername());
		a.addParameter(ParameterTypes.Status, "UNLOCK");
		p.addAction(a);
		return p;
	}

	public ActionPackage updateLinkSize(String mapID, int linkID, int height) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(linkID));
		a.addParameter(ParameterTypes.Height, String.valueOf(height));
		p.addAction(a);
		return p;
	}

	public ActionPackage switchLinkDirection(String mapID, int linkID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(linkID));
		a.addParameter(ParameterTypes.Direction, "changed");
		p.addAction(a);
		return p;
	}
	
	public ActionPackage setLinkDirection(String mapID, int linkID, String startParentID, String endParentID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(linkID));
		a.addParameter(ParameterTypes.Direction, startParentID + "," + endParentID);
		p.addAction(a);
		return p;
	}

	public Action createTranscriptLink(String mapID, String boxID, TranscriptLinkData tData) {
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.BoxId, boxID);
		a.addParameter(ParameterTypes.Type, "transcript-link");

		if (tData.getElementID() == 0) {
			// No Parent is set, so use the last added element as parent
			a.addParameter(ParameterTypes.Parent, "LAST-ID");
		} else {
			a.addParameter(ParameterTypes.Parent, String.valueOf(tData.getElementID()));
		}

		a.addParameter(ParameterTypes.ElementId, "transcriptlink");
		a.addParameter(ParameterTypes.StartRow, tData.getStartRow().getLineNumber() + "");
		a.addParameter(ParameterTypes.StartPoint, tData.getStartRow().getTextPassages().indexOf(tData.getStartPassage()) + "");
		a.addParameter(ParameterTypes.EndRow, tData.getEndRow().getLineNumber() + "");
		a.addParameter(ParameterTypes.EndPoint, tData.getEndRow().getTextPassages().indexOf(tData.getEndPassage()) + "");

		return a;
	}

	public ActionPackage updateTranscriptLink(String mapID, int elementID, TranscriptLinkData tData) {
		AbstractUnspecifiedElementModel tModel = tData.getModel();

		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(tModel.getId()));

		if (!String.valueOf(tData.getStartRow().getLineNumber()).equals(tModel.getValue(ParameterTypes.StartRow))) {
			a.addParameter(ParameterTypes.StartRow, String.valueOf(tData.getStartRow().getLineNumber()));
		}
		if (!String.valueOf(tData.getStartRow().getTextPassages().indexOf(tData.getStartRow().getSelectionStartTextPassage())).equals(tModel.getValue(ParameterTypes.StartPoint))) {
			a.addParameter(ParameterTypes.StartPoint, tData.getStartRow().getTextPassages().indexOf(tData.getStartPassage()) + "");
		}
		if (!String.valueOf(tData.getEndRow().getLineNumber()).equals(tModel.getValue(ParameterTypes.EndRow))) {
			a.addParameter(ParameterTypes.EndRow, String.valueOf(tData.getEndRow().getLineNumber()));
		}
		if (!String.valueOf(tData.getEndRow().getTextPassages().indexOf(tData.getEndRow().getSelectionEndTextPassage())).equals(tModel.getValue(ParameterTypes.EndPoint))) {
			a.addParameter(ParameterTypes.EndPoint, tData.getEndRow().getTextPassages().indexOf(tData.getEndPassage()) + "");
		}

		p.addAction(a);
		return p;
	}

	public ActionPackage updateElementWithMultipleValues(String mapID, int id, Vector<Object[]> values) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateElement, Categories.Map);
//		Action a = new Action("UPDATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(id));
		for (int i = 0; i < values.size(); i++) {
			a.addParameter((ParameterTypes)values.get(i)[0], (String)values.get(i)[1]);
		}
		p.addAction(a);
		return p;
	}

	public ActionPackage addElement(String mapID, int boxID, String elementType, String elementID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Type, elementType);
		a.addParameter(ParameterTypes.Parent, String.valueOf(boxID));
		a.addParameter(ParameterTypes.ElementId, elementID);
		// a.addParameter("TIME", "CURRENT-TIME");

		p.addAction(a);
		return p;
	}
	
	//###################################################
	//##	3 Procedures to load boxes/links from xml-file   ##
	//###################################################

	public ActionPackage createBoxWithElementsAndContent(String ontologyId, int rootElementId, String mapID, int posX, int posY, int width, int height, Vector<String[]> subElements) {
	    ActionPackage p = new ActionPackage();
	    Action a = createEmptyBox(ontologyId, rootElementId, mapID, posX, posY, width, height);
	    p.addAction(a);
	    
	    //actions erstellen f�r alle subElemente
	    for(String[] subElement : subElements) {
		p.addAction(addElementWithContent(mapID, subElement[1], subElement[0], subElement[2]));
	    }
	    return p;
	}
	
	public ActionPackage createLinkWithElementsAndContent(String ontologyId, int rootElementId, String mapID, int startBoxId, int endBoxId, Vector<String[]> subElements) {
	    ActionPackage p = new ActionPackage();
	    Action a = createEmptyLink(ontologyId, rootElementId, mapID, startBoxId, endBoxId);
	    p.addAction(a);

	    //actions erstellen f�r alle subElemente
	    for(String[] subElement : subElements) {
		p.addAction(addElementWithContent(mapID, subElement[1], subElement[0], subElement[2]));
	    }
	    return p;
	}

	private Action createEmptyBox(String ontologyId, int rootElementId, String mapID, int posX, int posY, int width, int height) {
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
	    a.addParameter(ParameterTypes.Type, "emptybox");
	    //Used for identification
	    a.addParameter(ParameterTypes.ObjectId, String.valueOf(rootElementId));
	    a.addParameter(ParameterTypes.MapId, mapID);
	    a.addParameter(ParameterTypes.ElementId, ontologyId);
	    a.addParameter(ParameterTypes.PosX, String.valueOf(posX));
	    a.addParameter(ParameterTypes.PosY, String.valueOf(posY));
	    a.addParameter(ParameterTypes.Width, String.valueOf(width));
	    a.addParameter(ParameterTypes.Height, String.valueOf(height));

	    return a;
	}
	
	private Action createEmptyLink(String ontologyId, int rootElementId, String mapID, int startBoxId, int endBoxId) {
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.Type, "emptyrelation");
		a.addParameter(ParameterTypes.ObjectId, String.valueOf(rootElementId));
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, String.valueOf(ontologyId));
		a.addParameter(ParameterTypes.Parent, String.valueOf(startBoxId));
		a.addParameter(ParameterTypes.Parent, String.valueOf(endBoxId));
		
		return a;
	}

	private Action addElementWithContent(String mapID, String elementType, String elementID, String elementValue) {
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
	    a.addParameter(ParameterTypes.MapId, mapID);
	    a.addParameter(ParameterTypes.Type, elementType);
	    a.addParameter(ParameterTypes.Parent, "LAST-ID");
	    a.addParameter(ParameterTypes.ElementId, elementID);
	    a.addParameter(ParameterTypes.Text, elementValue);

	    return a;
	}

	private Action removeElementAction(String mapID, int id) {
		Action a = new Action(Commands.DeleteElement, Categories.Map);
//		Action a = new Action("DELETE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(id));
		a.addParameter(ParameterTypes.Type, LASAD_Client.getMVCController(mapID).getElement(id).getType());
		a.addParameter(ParameterTypes.LinksAlreadyRemoved, "false");
		return a;
	}

	public ActionPackage removeElement(String mapID, int id) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteElement, Categories.Map);
//		Action a = new Action("DELETE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Type, LASAD_Client.getMVCController(mapID).getElement(id).getType());
		a.addParameter(ParameterTypes.Id, String.valueOf(id));
		a.addParameter(ParameterTypes.LinksAlreadyRemoved, "false");

		p.addAction(a);
		return p;
	}

	public Action createBox(ElementInfo config, String mapID, int posX, int posY) {
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.Type, "box");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, config.getElementID());
		a.addParameter(ParameterTypes.PosX, String.valueOf(posX));
		a.addParameter(ParameterTypes.PosY, String.valueOf(posY));

		return a;
	}

	private Vector<Action> createBoxElementsAction(ElementInfo currentElement, String mapID) {

		Vector<Action> allActions = new Vector<Action>();

		for (ElementInfo childElement : currentElement.getChildElements().values()) {
			Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG_DETAILS);
			for (int i = 0; i < childElement.getQuantity(); i++) {
				Action a = new Action(Commands.CreateElement, Categories.Map);
//				Action a = new Action("CREATE-ELEMENT", "MAP");
				a.addParameter(ParameterTypes.Type, childElement.getElementType());
				a.addParameter(ParameterTypes.MapId, mapID);
				a.addParameter(ParameterTypes.Parent, "LAST-ID");
				a.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
				a.addParameter(ParameterTypes.UserName, LASAD_Client.getInstance().getUsername());

				if (childElement.getElementType().equals("rating")) {
					a.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
				} else if (childElement.getElementType().equals("awareness")) {
					a.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will be

					// filled in by the
					// server
				}

				if (a != null) {
					allActions.add(a);
				}
			}
		}
		return allActions;
	}

	//TODO wie sinnvoll ist das hier?
	public ActionPackage createBoxWithElements(ElementInfo currentElement, String mapID, int posX, int posY) {
		ActionPackage p = new ActionPackage();
		Action action = createBox(currentElement, mapID, posX, posY);
		p.addAction(action);

		Vector<Action> vAction = createBoxElementsAction(currentElement, mapID);
		if (vAction.size() > 0) {
			for (Action a : vAction) {
				p.addAction(a);
			}
		}
		return p;
	}

	public ActionPackage undo(String mapID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Undo, Categories.Map);
//		Action a = new Action("UNDO", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);

		p.addAction(a);
		return p;
	}

	public ActionPackage createBoxAndLink(ElementInfo boxInfo, ElementInfo linkInfo, String mapID, int x, int y, String start, String end) {
		ActionPackage p = new ActionPackage();
		Action a = createBox(boxInfo, mapID, x, y);
		p.addAction(a);

		Vector<Action> b = createBoxElementsAction(boxInfo, mapID);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}

		Vector<Action> c = createLinkWithElementsAction(linkInfo, mapID, start, end, null);
		if (c.size() > 0) {
			for (Action d : c) {
				p.addAction(d);
			}
		}

		return p;
	}

	public ActionPackage sendChatMessage(String mapID, String msg) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ChatMsg, Categories.Communication);
//		Action a = new Action("CHAT-MSG", "COMMUNICATION");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Message, msg);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage sendChatMessage(String mapID, String msg, String sentenceOpener, String textColor) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ChatMsg, Categories.Communication);
//		Action a = new Action("CHAT-MSG", "COMMUNICATION");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Message, msg);
		a.addParameter(ParameterTypes.Opener, sentenceOpener);
		a.addParameter(ParameterTypes.TextColor, textColor);
		p.addAction(a);
		return p;
	}

	public ActionPackage createLinkWithElements(ElementInfo info, String mapID, String startElementID, String endElementID) {
		ActionPackage p = new ActionPackage();

		Vector<Action> b = createLinkWithElementsAction(info, mapID, startElementID, endElementID, null);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}
		return p;
	}

	public ActionPackage createLinkWithElements(ElementInfo info, String mapID, String startElementID, String endElementID, Vector<AbstractExtendedElement> existingChildElements) {
		ActionPackage p = new ActionPackage();

		Vector<Action> b = createLinkWithElementsAction(info, mapID, startElementID, endElementID, existingChildElements);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}
		return p;
	}

	private Vector<Action> createLinkWithElementsAction(ElementInfo info, String mapID, String startID, String endID, Vector<AbstractExtendedElement> existingChildElements) {

		Vector<Action> resultingActions = new Vector<Action>();

		Action a = new Action(Commands.CreateElement, Categories.Map);
		a.addParameter(ParameterTypes.Type, info.getElementType());
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, String.valueOf(info.getElementID()));
		a.addParameter(ParameterTypes.Parent, startID);
		a.addParameter(ParameterTypes.Parent, endID);
		a.addParameter(ParameterTypes.SiblingsAlreadyUpdated, "false");

		resultingActions.add(a);

		if (existingChildElements == null) {
			Collection<ElementInfo> childElements = info.getChildElements().values();
			for (ElementInfo childElement : childElements) {
				Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG);

				// Initializing loop variable and changing it in case there're
				// no existing child elements
				int quantity = childElement.getQuantity();
				int i = 0;
				while (i < quantity) {
					Action b = new Action(Commands.CreateElement, Categories.Map);
//					Action b = new Action("CREATE-ELEMENT", "MAP");
					b.addParameter(ParameterTypes.Type, childElement.getElementType());
					b.addParameter(ParameterTypes.MapId, mapID);
					b.addParameter(ParameterTypes.Parent, "LAST-ID");
					b.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));

					if (childElement.getElementType().equals("rating")) {
						b.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
					} else if (childElement.getElementType().equals("awareness")) {
						b.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will
						// be filled in
						// by the server
					}
					if (b != null) {
						resultingActions.add(b);
					}

					i++;
				}
			}
		} else {
			for (AbstractExtendedElement element : existingChildElements) {
				Logger.log("Creating action for ChildElement: " + element.getConfig().getElementType(), Logger.DEBUG);
				Action b = new Action(Commands.CreateElement, Categories.Map);
//				Action b = new Action("CREATE-ELEMENT", "MAP");
				b.addParameter(ParameterTypes.Type, element.getConfig().getElementType());
				b.addParameter(ParameterTypes.MapId, mapID);
				b.addParameter(ParameterTypes.Parent, "LAST-ID");
				b.addParameter(ParameterTypes.ElementId, String.valueOf(element.getConfig().getElementID()));

				if (element.getConfig().getElementType().equals("rating")) {
					b.addParameter(ParameterTypes.Score, element.getConfig().getElementOption(ParameterTypes.Score));
				} else if (element.getConfig().getElementType().equals("text")) {
					String text;
					if (element.getConnectedModel().getValue(ParameterTypes.Text) == null) {
						text = "";
					} else {
						text = element.getConnectedModel().getValue(ParameterTypes.Text);
					}
					b.addParameter(ParameterTypes.Text, text);
				} else if (element.getConfig().getElementType().equals("awareness")) {
					b.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will be
					// filled in by the
					// server
				}
				if (b != null) {
					resultingActions.add(b);
				}
			}
		}
		return resultingActions;
	}
	
	public ActionPackage createLogin(String username, String pw, boolean pwencrypted) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Login, Categories.Management);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.Password, pw);
		a.addParameter(ParameterTypes.passwordEncrypted, Boolean.toString(pwencrypted));
		p.addAction(a);
		return p;
	}
	
	public ActionPackage createLogin(String username, String pw) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Login, Categories.Management);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.Password, pw);
		p.addAction(a);
		return p;
	}

	public ActionPackage logout(String username) {
		ActionPackage p = new ActionPackage();

		Action a = new Action(Commands.Logout, Categories.Management);
		a.addParameter(ParameterTypes.UserName, username);
		p.addAction(a);

		Logger.log(a.toString(), Logger.DEBUG);
		return p;
	}

	public ActionPackage sendHeartbeat() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Heartbeat, Categories.Session);
//		Action a = new Action("HEARTBEAT", "SESSION");
		p.addAction(a);
		return p;
	}

	public ActionPackage getMapDetails(String mapID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.MapDetails, Categories.Management);
//		Action a = new Action("MAPDETAILS", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapId, mapID);
		p.addAction(a);
		return p;
	}

	public ActionPackage createAndJoinMap(String mapName, String selectedOntology, String selectedTemplate,String backgroundImage) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateAndJoin, Categories.Management);
//		Action a = new Action("CREATEANDJOIN", "MANAGEMENT");
		a.addParameter(ParameterTypes.MapName, mapName);
		a.addParameter(ParameterTypes.Ontology, selectedOntology);
		a.addParameter(ParameterTypes.Template, selectedTemplate);
		if(backgroundImage != "")
		{
			a.addParameter(ParameterTypes.BackgroundImageURL, backgroundImage);
		}
		p.addAction(a);
		return p;
	}
	
	/**
	 * Basically this method works like createAndJoinMap but transfers an xml-text to be loaded after joining the map
	 * @param mapName
	 * @param selectedOntology
	 * @param selectedTemplate
	 * @param xmlText The xml-file to be imported
	 * @param chatlog
	 * @return
	 */
	public ActionPackage importAndJoinMap(String mapName, String selectedOntology, String selectedTemplate, String xmlText) {
		ActionPackage p = createAndJoinMap(mapName, selectedOntology, selectedTemplate,null);
		p.getActions().get(0).addParameter(ParameterTypes.XMLText, xmlText);
		return p;
	}

	public ActionPackage getAllOntologgiesAndTemplates() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetAllOntologiesAndTemplates, Categories.Management);
//		Action a = new Action("GETALLONTOLOGIESANDTEMPLATES", "MANAGEMENT");
		p.addAction(a);
		return p;
	}

	public ActionPackage getMapsAndTemplates() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetMapsAndTemplates, Categories.Authoring);
//		Action a = new Action("GET-MAPS-AND-TEMPLATES", "AUTHORING");
		p.addAction(a);
		return p;
	}

	public ActionPackage getQuestionnaireAnswers(String mapID, Vector<QuestionConfig> questions) {
		ActionPackage p = new ActionPackage();
		for (QuestionConfig qc : questions) {
			Action a = new Action(Commands.GetAnswer, Categories.Questionnaire);
//			Action a = new Action("GETANSWER", "QUESTIONNAIRE");
			a.addParameter(ParameterTypes.MapId, mapID);
			a.addParameter(ParameterTypes.QuestionId, qc.getId());
			p.addAction(a);
		}

		return p;
	}

	public Action addQuestionnaireAnswer(String mapID, String questionID, String answer) {
		Action a = new Action(Commands.AddAnswer, Categories.Questionnaire);
//		Action a = new Action("ADDANSWER", "QUESTIONNAIRE");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.QuestionId, questionID);
		a.addParameter(ParameterTypes.QuestionAnswer, answer);

		return a;
	}

	public ActionPackage updateMyCursorPositionNonPersistent(String mapID, int cursorID, String username, int x, int y) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateCursorPosition, Categories.Map);
//		Action a = new Action("UPDATE-CURSOR-POSITION", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(cursorID));
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.PosX, x + "");
		a.addParameter(ParameterTypes.PosY, y + "");
		a.addParameter(ParameterTypes.Persistent, "FALSE");
		p.addAction(a);
		return p;
	}

	public ActionPackage updateMyCursorPositionPersistent(String mapID, int cursorID, String username, int x, int y) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateCursorPosition, Categories.Map);
//		Action a = new Action("UPDATE-CURSOR-POSITION", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(cursorID));
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.PosX, x + "");
		a.addParameter(ParameterTypes.PosY, y + "");
		a.addParameter(ParameterTypes.Persistent, "TRUE");
		p.addAction(a);
		return p;
	}

	public ActionPackage updateGroupCursorPositionPersistent(String mapID, int cursorID, int x, int y) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateCursorPosition, Categories.Map);
//		Action a = new Action("UPDATE-CURSOR-POSITION", "MAP");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.Id, String.valueOf(cursorID));
		a.addParameter(ParameterTypes.PosX, x + "");
		a.addParameter(ParameterTypes.PosY, y + "");
		a.addParameter(ParameterTypes.Persistent, "TRUE");
		p.addAction(a);
		return p;
	}

	public ActionPackage createAwarenessCursor(String mapID, String username) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.Type, "awareness-cursor");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.ElementId, "awareness-cursor-" + username);
		a.addParameter(ParameterTypes.PosX, "0");
		a.addParameter(ParameterTypes.PosY, "0");
		p.addAction(a);
		return p;
	}

	public ActionPackage createGroupCursor(String mapID, String username, int posX, int posY) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateElement, Categories.Map);
//		Action a = new Action("CREATE-ELEMENT", "MAP");
		a.addParameter(ParameterTypes.Type, "group-cursor");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.ElementId, "group-cursor-" + username);
		a.addParameter(ParameterTypes.PosX, posX + "");
		a.addParameter(ParameterTypes.PosY, posY + "");
		p.addAction(a);
		return p;
	}

	public ActionPackage requestFeedback(String mapID, String username, String agentName, String feedbackName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Request, Categories.Feedback);
//		Action a = new Action("REQUEST", "FEEDBACK");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.AgentId, agentName);
		a.addParameter(ParameterTypes.TypeId, feedbackName);
		p.addAction(a);
		return p;
	}

	public ActionPackage requestFeedback(String mapID, String username, String agentName, String feedbackName, String agentType) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Request, Categories.Feedback);
//		Action a = new Action("REQUEST", "FEEDBACK");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.UserName, username);
		a.addParameter(ParameterTypes.AgentId, agentName);
		a.addParameter(ParameterTypes.TypeId, feedbackName);
		a.addParameter(ParameterTypes.AgentType, agentType);
		p.addAction(a);
		return p;
	}

	public ActionPackage createTemplate(
			String useTemplateName, 
			String useTemplateDescription, 
			String useOntologyWithName, 
			boolean useSeperateViewForDetails, 
			int maxUserCount, 
			boolean useChat, 
			boolean useUserList, 
			boolean useMiniMap, 
			boolean useCursorTracking, 
			boolean useTranscript, 
			String transcriptText,
			boolean onlyAuthorCanModify,
			boolean commitTextByEnter,
			boolean straightLink,
			boolean useAutoGrowTextArea,
			boolean allowLinksToLinks) 
	{
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateTemplate, Categories.Authoring);
//		Action a = new Action("CREATE-TEMPLATE", "AUTHORING");

		// General
		a.addParameter(ParameterTypes.TemplateName, useTemplateName);

		if (useTemplateDescription == null) {
			a.addParameter(ParameterTypes.TemplateDescription, "");
		} else {
			a.addParameter(ParameterTypes.TemplateDescription, useTemplateDescription);
		}
		a.addParameter(ParameterTypes.Ontology_Name, useOntologyWithName);
		a.addParameter(ParameterTypes.UseSelectionDetails, useSeperateViewForDetails + "");

		// Collaboration
		a.addParameter(ParameterTypes.MaxUsers, maxUserCount + "");
		a.addParameter(ParameterTypes.UseChat, useChat + "");
		a.addParameter(ParameterTypes.UseUserlist, useUserList + "");
		a.addParameter(ParameterTypes.UseMiniMap, useMiniMap + "");
		a.addParameter(ParameterTypes.UseCursorTracking, useCursorTracking + "");
		
		a.addParameter(ParameterTypes.OnlyAuthorCanModify, onlyAuthorCanModify + "");
		a.addParameter(ParameterTypes.CommitTextByEnter, commitTextByEnter + "");
		a.addParameter(ParameterTypes.StraightLink, straightLink + "");

		// Transcript
		if (useTranscript) {
			a.addParameter(ParameterTypes.Transcript, transcriptText);
		}
		
		//MODFIED BY BM
		a.addParameter(ParameterTypes.AutoGrowTextArea, useAutoGrowTextArea + "");

		a.addParameter(ParameterTypes.AllowLinksToLinks, allowLinksToLinks + "");
		
		p.addAction(a);
		return p;
	}

	public ActionPackage getTemplatesForOverview() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetTemplates, Categories.Management);
//		Action a = new Action("GET-TEMPLATES", "MANAGEMENT");
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getTemplates() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetTemplates, Categories.Authoring);
//		Action a = new Action("GET-TEMPLATES", "AUTHORING");
		p.addAction(a);
		return p;
	}

	public ActionPackage deleteTemplate(String templateName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteTemplate, Categories.Authoring);
//		Action a = new Action("DELETE-TEMPLATE", "AUTHORING");
		a.addParameter(ParameterTypes.Template, templateName);
		p.addAction(a);
		return p;
	}

	public ActionPackage createUser(String useNickname, String usePassword, String useRole) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateUser, Categories.Authoring);
//		Action a = new Action("CREATE-USER", "AUTHORING");
		a.addParameter(ParameterTypes.UserName, useNickname);
		a.addParameter(ParameterTypes.Password, usePassword);
		a.addParameter(ParameterTypes.Role, useRole);
		p.addAction(a);
		return p;
	}

	public ActionPackage getUsers() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetUsers, Categories.Authoring);
//		Action a = new Action("GET-USERS", "AUTHORING");
		a.addParameter(ParameterTypes.GetRoles, "TRUE");
		p.addAction(a);
		return p;
	}

	public ActionPackage deleteUser(String userName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteUser, Categories.Authoring);
//		Action a = new Action("DELETE-USER", "AUTHORING");
		a.addParameter(ParameterTypes.UserName, userName);
		p.addAction(a);
		return p;
	}

	public ActionPackage createMap(String useMapName, String useTemplate, Vector<String> allUserRestrictions, boolean persistent,String BackgroundImage) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateMap, Categories.Authoring);
//		Action a = new Action("CREATE-MAP", "AUTHORING");
		a.addParameter(ParameterTypes.MapName, useMapName);
		a.addParameter(ParameterTypes.Template, useTemplate);
		if(BackgroundImage != "")
		{
			a.addParameter(ParameterTypes.BackgroundImageURL, BackgroundImage);
		}
		
		
		for(String s : allUserRestrictions) {
			a.addParameter(ParameterTypes.RestrictedTo, s);	
		}
		
		if(persistent) {
			a.addParameter(ParameterTypes.Persistent, "TRUE");
		}
		p.addAction(a);
		return p;
	}

	public ActionPackage getUserListWithoutRole() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetUsers, Categories.Authoring);
//		Action a = new Action("GET-USERS", "AUTHORING");
		a.addParameter(ParameterTypes.GetRoles, "FALSE");
		p.addAction(a);
		return p;
	}

	public ActionPackage deleteMap(String mapName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteMap, Categories.Authoring);
//		Action a = new Action("DELETE-MAP", "AUTHORING");
		a.addParameter(ParameterTypes.MapName, mapName);
		p.addAction(a);
		return p;
	}

	public ActionPackage deleteOntology(String ontologyName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteOntology, Categories.Authoring);
//		Action a = new Action("DELETE-ONTOLOGY", "AUTHORING");
		a.addParameter(ParameterTypes.OntologyName, ontologyName);
		p.addAction(a);
		return p;
	}

	public ActionPackage getOntologyList() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetOntologies, Categories.Authoring);
//		Action a = new Action("GET-ONTOLOGIES", "AUTHORING");
		p.addAction(a);
		return p;
	}

	public ActionPackage getOntologyDetails(String selectedOntology) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetOntologyDetails, Categories.Authoring);
//		Action a = new Action("GET-ONTOLOGY-DETAILS", "AUTHORING");
		a.addParameter(ParameterTypes.OntologyName, selectedOntology);
		p.addAction(a);
		return p;
	}

	public ActionPackage getTemplateDetails(String templateName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.TemplateDetails, Categories.Management);
//		Action a = new Action("TEMPLATEDETAILS", "MANAGEMENT");
		a.addParameter(ParameterTypes.Template, templateName);
		p.addAction(a);
		return p;
	}

	public ActionPackage loadSessionFromXML(String sessionName, String ontology, String template, String content, String username, String chatlog, String backgroundimage, boolean userJoin) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.Load, Categories.File);
//		Action a = new Action("LOAD", "FILE");
		a.addParameter(ParameterTypes.Session, sessionName);
		a.addParameter(ParameterTypes.OntologyXML, ontology);
		a.addParameter(ParameterTypes.TemplateXML, template);
		a.addParameter(ParameterTypes.ContentXML, content);
		a.addParameter(ParameterTypes.ChatLog, chatlog);
		a.addParameter(ParameterTypes.UserJoin, Boolean.toString(userJoin));
		if(backgroundimage != null)
		{
			a.addParameter(ParameterTypes.BackgroundImageURL, GWT.getHostPageBaseURL()+backgroundimage);
		}
		if(username != null) {
			a.addParameter(ParameterTypes.RestrictedTo, username);
		}
		p.addAction(a);
		return p;
	}

	public ActionPackage createOntology(String name, String createEmptyOntology, String clonedOntologyName) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CreateOntology, Categories.Authoring);
//		Action a = new Action("CREATE-ONTOLOGY", "AUTHORING");
		a.addParameter(ParameterTypes.OntologyName, name);
		a.addParameter(ParameterTypes.OntologyXML, createEmptyOntology);
		
		if(clonedOntologyName != null) {
			a.addParameter(ParameterTypes.Clone, clonedOntologyName);	
		}
		p.addAction(a);
		return p;
	}

	public ActionPackage updateOntology(String name, String xml) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.UpdateOntology, Categories.Authoring);
//		Action a = new Action("UPDATE-ONTOLOGY", "AUTHORING");
		a.addParameter(ParameterTypes.OntologyName, name);
		a.addParameter(ParameterTypes.OntologyXML, xml);
		p.addAction(a);
		return p;
	}

	public ActionPackage recoverRequestFromConfirmation(Action a) {
		ActionPackage p = new ActionPackage();	
		Action action = new Action(Commands.valueOf(a.getParameterValue(ParameterTypes.OriginalCommand)), Categories.Authoring);
		action.getParameters().addAll(a.getParameters());
		action.addParameter(ParameterTypes.Confirmed, "TRUE");
		p.addAction(action);
		
		return p;
	}
	
	public static ActionPackage createActionPackage(Action a){	
		ActionPackage p = new ActionPackage();
		p.addAction(a);
		return p;
	}
	
	/*
	 * Feedback Authoring Tool related
	 * Author: Anahuac Valero
	 */
	
	public ActionPackage getOntologyListFA() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ListOntologies, Categories.FeedbackAuthoring);//GetOntologies
		p.addAction(a);
		return p;
	}

	public ActionPackage getOntologyDetailsFA(String selectedOntology) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetOntologyDetails, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.OntologyName, selectedOntology);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getMapListFA() {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ListMap, Categories.FeedbackAuthoring);//List
		p.addAction(a);
		return p;
	}

	public ActionPackage getMapDetailsFA(String mapID) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.MapDetails, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.MapId, mapID);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getAgentListFA() {
       ActionPackage p = new ActionPackage();
       Action a = new Action(Commands.ListAgentsInfo, Categories.FeedbackAuthoring);
       p.addAction(a);
       return p;
   }
	
	public ActionPackage getAgent2OntologyFA() {
       ActionPackage p = new ActionPackage();
       Action a = new Action(Commands.ListAgentsToOntologies, Categories.FeedbackAuthoring);
       p.addAction(a);
       return p;
	}
	
	public ActionPackage getAgent2SessionFA() {
       ActionPackage p = new ActionPackage();
       Action a = new Action(Commands.ListAgentsToSessions, Categories.FeedbackAuthoring);
       p.addAction(a);
       return p;
	}
	
	public ActionPackage getSessionsStatusFA() {
       ActionPackage p = new ActionPackage();
       Action a = new Action(Commands.ListSessionStatus, Categories.FeedbackAuthoring);
       p.addAction(a);
       return p;
	}
	
	public ActionPackage getAddAgentToOntologyFA(String agentId, String agentName, String ontology) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.AddAgentToOntology, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.addParameter(ParameterTypes.AgentName, agentName);
		a.addParameter(ParameterTypes.Ontology, ontology);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getAddAgentToSessionFA(String agentId, String agentName, String session) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.AddAgentToSession, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.addParameter(ParameterTypes.AgentName, agentName);
		a.addParameter(ParameterTypes.SessionId, session);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getAddOrUpdateAgentFA(String agentId, AgentDescriptionFE agent) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.AddOrUpdateAgent, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.setObjectFE(agent);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getDeleteAgentFA(String agentId) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.DeleteAgent, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		p.addAction(a);
		return p;
	}
	public ActionPackage getCompileAgentFA(String agentId, AgentDescriptionFE agent) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.CompileAgent, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.setObjectFE(agent);
		p.addAction(a);
		return p;
	}
	
	
	public ActionPackage getRemoveAgentFromOntologyFA(String agentId, String agentName, String ontology) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.RemoveAgentFromOntology, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.addParameter(ParameterTypes.AgentName, agentName);
		a.addParameter(ParameterTypes.Ontology, ontology);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getRemoveAgentFromSessionFA(String agentId, String agentName, String session) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.RemoveAgentFromSession, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.addParameter(ParameterTypes.AgentName, agentName);
		a.addParameter(ParameterTypes.SessionId, session);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getStartSessionFA(String sessionId) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.StartSession, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.SessionId, sessionId);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getStopSessionFA(String sessionId) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.StopSession, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.SessionId, sessionId);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getFreshAgentIdFA(String requestId) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetFreshAgentId, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.RequestId, requestId);
		p.addAction(a);
		return p;
	}
	
	public ActionPackage getFreshPatternIdFA(String requestId, String agentId) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.GetFreshPatternId, Categories.FeedbackAuthoring);
		a.addParameter(ParameterTypes.AgentId, agentId);
		a.addParameter(ParameterTypes.RequestId, requestId);
		p.addAction(a);
		return p;
	}
	
	/*
	 *  End of Feedback Authoring Tool related
	 */
}