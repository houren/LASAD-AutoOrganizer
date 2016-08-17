package lasad.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import lasad.Config;
import lasad.controller.MapController;
import lasad.database.DatabaseConnectionHandler;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class Map {

	private int id;
	private int template_id;
	private int creator_user_id;

	private Vector<Integer> restricted_to_user_id;
	private String name;
	private Timestamp timestamp;

	public Map(String name, int template_id, int creator, Vector<Integer> restrictedToUserID) {
		this.name = name;
		this.template_id = template_id;
		this.creator_user_id = creator;

		if (restrictedToUserID != null) {
			this.restricted_to_user_id = restrictedToUserID;
		} else {
			this.restricted_to_user_id = new Vector<Integer>();
		}

		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public Map(int id, String name, int template_id, int creator, Vector<Integer> restrictedToUserID) {
		this(name, template_id, creator, restrictedToUserID);
		this.id = id;
	}

	public void generateMapConfigurationFile() {

		Document doc = null;
		org.jdom.Element root = new org.jdom.Element("lasad-maps");

		doc = new Document(root);

		org.jdom.Element map = new org.jdom.Element("map");

		org.jdom.Element mapName = new org.jdom.Element("name");
		mapName.setText(this.name);

		org.jdom.Element mapTemplate = new org.jdom.Element("template");
		mapTemplate.setText(Template.getTemplateName(this.template_id));

		org.jdom.Element mapCreator = new org.jdom.Element("creator");
		mapCreator.setText(User.getName(this.creator_user_id));

		org.jdom.Element mapRestrictedToUser = new org.jdom.Element("restricted-to-user");
		if (this.restricted_to_user_id != null) {
			for (int i = 0; i < this.restricted_to_user_id.size(); i++) {
				org.jdom.Element user = new org.jdom.Element("user");
				user.setText(User.getName(this.restricted_to_user_id.get(i)));
				mapRestrictedToUser.addContent(user);
			}
		}

		map.addContent(mapName);
		map.addContent(mapTemplate);
		map.addContent(mapCreator);
		map.addContent(mapRestrictedToUser);

		root.addContent(map);

		File mapDir = new File(Config.configDir, "map");
		File f = new File(mapDir, this.getName() + " - " + this.timestamp.toString().substring(0, 10) + ".xml");

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

		try {
			FileOutputStream output = new FileOutputStream(f);
			outputter.output(doc, output);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean saveToDatabase(ActionProcessor ap) {
		boolean success = true;
		if (Map.getMapID(this.name) == -1) { // Map does not exist

			if (this.creator_user_id == -1) { // User who created the map does
												// no longer exist...
				this.creator_user_id = User.getUserID("Unknown");
			}

			Connection con = null;
			PreparedStatement insertRevision = null;
			PreparedStatement getLastID = null;
			ResultSet rs = null;

			try {
				con = DatabaseConnectionHandler.getConnection(Map.class);
				// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

				/**
				 * if(this.restricted_to_user_id != null) { PreparedStatement insertRevision =
				 * con.prepareStatement("INSERT INTO "+Config.dbName+
				 * ".maps (id, name, template_id, creator_user_id, restricted_to_user_id, last_root_element_id, timestamp) VALUES (NULL, ?, ?, ?, ?, ?, ?);"
				 * ); insertRevision.setString(1, this.name); insertRevision.setInt(2, this.template_id); insertRevision.setInt(3,
				 * this.creator_user_id); insertRevision.setInt(4, this.restricted_to_user_id); insertRevision.setInt(5, 0);
				 * insertRevision.setTimestamp(6, this.timestamp); insertRevision.executeUpdate(); } else { PreparedStatement
				 * insertRevision = con.prepareStatement("INSERT INTO "+Config.dbName+
				 * ".maps (id, name, template_id, creator_user_id, restricted_to_user_id, last_root_element_id, timestamp) VALUES (NULL, ?, ?, ?, NULL, ?, ?);"
				 * ); insertRevision.setString(1, this.name); insertRevision.setInt(2, this.template_id); insertRevision.setInt(3,
				 * this.creator_user_id); insertRevision.setInt(4, 0); insertRevision.setTimestamp(5, this.timestamp);
				 * insertRevision.executeUpdate(); }
				 */

				insertRevision = con.prepareStatement("INSERT INTO " + Config.dbName
						+ ".maps (id, name, template_id, creator_user_id, last_root_element_id, timestamp) VALUES (NULL, ?, ?, ?, ?, ?);");
				insertRevision.setString(1, this.name);
				insertRevision.setInt(2, this.template_id);
				insertRevision.setInt(3, this.creator_user_id);
				insertRevision.setInt(4, 0);
				insertRevision.setTimestamp(5, this.timestamp);
				insertRevision.executeUpdate();

				getLastID = con.prepareStatement("SELECT LAST_INSERT_ID()");
				rs = getLastID.executeQuery();
				rs.next();

				this.id = rs.getInt(1);

				if (this.restricted_to_user_id.size() > 0) {
					for (int i = 0; i < this.restricted_to_user_id.size(); i++) {
						insertRevision = con.prepareStatement("INSERT INTO " + Config.dbName
								+ ".map_to_user_restriction (id, map_id, user_id) VALUES (NULL, ?, ?);");
						insertRevision.setInt(1, this.id);
						insertRevision.setInt(2, this.restricted_to_user_id.get(i));
						insertRevision.executeUpdate();

					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try{rs.close();}catch(Exception e){}
				try{insertRevision.close();}catch(Exception e){}
				try{getLastID.close();}catch(Exception e){}
				if (con != null) {
					DatabaseConnectionHandler.closeConnection(Map.class, con);
					// try {
					// con.close();
					// } catch (SQLException e) {
					// e.printStackTrace();
					// }
				}
			}

			new Revision(this.id, "Map created", this.creator_user_id).saveToDatabase();

			// If the map template has pre-defined elements --> create them
			try {
				String templateXML = Template.getXMLString(this.template_id);

				if (templateXML == null) {
					System.out.println("ERROR: In Map.saveToDatabase: template not found for template_id - " + template_id
							+ ", map not saved");
					return false;
				}

				StringReader in = new StringReader(templateXML);

				SAXBuilder saxIn = new SAXBuilder();
				Document doc;

				doc = saxIn.build(in);

				org.jdom.Element rootElement = doc.getRootElement();

				org.jdom.Element elementTemplate = null;
				elementTemplate = rootElement.getChild("elementtemplate");

				if (elementTemplate != null) {

					// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
					@SuppressWarnings("unchecked")
					List<org.jdom.Element> elements = elementTemplate.getChildren("element");
					for (org.jdom.Element e : elements) {
						Action a = new Action(Commands.CreateElement, Categories.Map);
						String typeOfE = e.getAttributeValue("type");
						String elementIDofE = e.getAttributeValue("elementid");

						a.addParameter(ParameterTypes.MapId, this.id + "");
						a.addParameter(ParameterTypes.Type, typeOfE);
						a.addParameter(ParameterTypes.ElementId, elementIDofE);

						// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
						@SuppressWarnings("unchecked")
						List<org.jdom.Element> parametersOfE = e.getChildren("parameter");
						for (org.jdom.Element pOfE : parametersOfE) {
							a.addParameter(ParameterTypes.fromString(pOfE.getChild("name").getText()), pOfE.getChild("value").getText());
						}

						MapController.processCreatePreDefinedElement(a, this.id, User.getUserID("Administrator"));

						// Get information about all sub-elements
						org.jdom.Element children = e.getChild("children");

						// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
						@SuppressWarnings("unchecked")
						List<org.jdom.Element> childElements = children.getChildren("element");
						for (org.jdom.Element c : childElements) {
							Action b = new Action(Commands.CreateElement, Categories.Map);
							String typeOfC = c.getAttributeValue("type");
							String elementIDofC = c.getAttributeValue("elementid");

							b.addParameter(ParameterTypes.MapId, this.id + "");
							b.addParameter(ParameterTypes.Type, typeOfC);
							b.addParameter(ParameterTypes.ElementId, elementIDofC);

							// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
							@SuppressWarnings("unchecked")
							List<org.jdom.Element> parametersOfC = c.getChildren("parameter");
							for (org.jdom.Element pOfC : parametersOfC) {
								b.addParameter(ParameterTypes.fromString(pOfC.getChild("name").getText()), pOfC.getChild("value").getText());
							}

							MapController.processCreatePreDefinedElement(b, this.id, User.getUserID("Administrator"));
						}
					}
				}
			} catch (JDOMException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			// Map exists already, do nothing
		}
		return success;
	}

	public static Vector<Map> getMapList() {

		Vector<Map> resultingMapList = new Vector<Map>();

		ResultSet rs = null;
		Connection con = null;
		PreparedStatement mapList = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			mapList = con.prepareStatement("SELECT * FROM " + Config.dbName + ".maps;");
			rs = mapList.executeQuery();

			while (rs.next()) {

				PreparedStatement restricted_user_List = null;
				ResultSet rs1 = null;

				// resultingMapList.add(new Map(rs.getInt(1),
				// rs.getString("name"), rs.getInt("template_id"),
				// rs.getInt("creator_user_id"),
				// rs.getInt("restricted_to_user_id")));
				Vector<Integer> userList = new Vector<Integer>();
				try {

					restricted_user_List = con.prepareStatement("SELECT user_id FROM " + Config.dbName
							+ ".map_to_user_restriction WHERE map_id = ?;");
					restricted_user_List.setInt(1, rs.getInt(1));
					rs1 = restricted_user_List.executeQuery();

					while (rs1.next()) {
						userList.add(rs1.getInt(1));
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try{restricted_user_List.close();}catch(Exception e){}
					try{rs1.close();}catch(Exception e){}
				}

				resultingMapList.add(new Map(rs.getInt(1), rs.getString("name"), rs.getInt("template_id"), rs.getInt("creator_user_id"),
						userList));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{mapList.close();}catch(Exception e){}
			try{rs.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
			try{con.close();}catch(Exception e){}
		}
		return resultingMapList;
	}

	public static boolean isExisting(int mapID) {
		boolean exists = false;

		ResultSet rs = null;
		Connection con = null;
		PreparedStatement mapList = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			mapList = con.prepareStatement("SELECT id FROM " + Config.dbName + ".maps WHERE id = ?;");
			mapList.setInt(1, mapID);
			rs = mapList.executeQuery();
			exists = rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{mapList.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
		return exists;
	}

	public static Template getTemplate(int mapID) {
		Template t = null;

		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement mapIDQuery = null;
		PreparedStatement templateQuery = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			// Get template ID
			mapIDQuery = con.prepareStatement("SELECT template_id FROM " + Config.dbName + ".maps WHERE id = ?;");
			mapIDQuery.setInt(1, mapID);

			rs = mapIDQuery.executeQuery();
			rs.next();

			int templateID = rs.getInt(1);

			// Get template information
			templateQuery = con.prepareStatement("SELECT * FROM " + Config.dbName + ".templates WHERE id = ?;");
			templateQuery.setInt(1, templateID);

			rs1 = templateQuery.executeQuery();
			rs1.next();

			t = new Template(templateID, rs1.getString("name"), rs1.getString("xmlConfig"), rs1.getInt("ontology_id"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{rs1.close();}catch(Exception e){}
			try{mapIDQuery.close();}catch(Exception e){}
			try{templateQuery.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return t;
	}

	public Vector<Integer> getRestricted_to_user_id() {

		return this.restricted_to_user_id;
	}

	public void setRestricted_to_user_id(Vector<Integer> restrictedToUserId) {
		restricted_to_user_id = restrictedToUserId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(int templateId) {
		template_id = templateId;
	}

	public int getCreator_user_id() {
		return creator_user_id;
	}

	public void setCreator_user_id(int creatorUserId) {
		creator_user_id = creatorUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public static int getNewRootElementID(int mapID) {
		int rootElementID = 0;

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement mapIDQuery = null;
		PreparedStatement rootElementIDUpdate = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			// Get ROOTELEMENTID
			mapIDQuery = con
					.prepareStatement("SELECT last_root_element_id FROM " + Config.dbName + ".maps WHERE id = ?;");
			mapIDQuery.setInt(1, mapID);

			rs = mapIDQuery.executeQuery();
			rs.next();

			rootElementID = rs.getInt(1);
			rootElementID++;

			// Update the ROOTELEMENTID counter in maps table
			rootElementIDUpdate = con.prepareStatement("UPDATE " + Config.dbName
					+ ".maps SET last_root_element_id = ? WHERE maps.id = ?;");
			rootElementIDUpdate.setInt(1, rootElementID);
			rootElementIDUpdate.setInt(2, mapID);

			rootElementIDUpdate.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{mapIDQuery.close();}catch(Exception e){}
			try{rootElementIDUpdate.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return rootElementID;
	}

	public static ActionPackage getCompleteElementInformation(int mapID) {
		// Only actions that focus on an actual element will be reconstructed,
		// i.e. chat messages, user joins etc. will not be recovered

		ActionPackage p = new ActionPackage();

		// Regenerate information filtered out earlier based on database
		// entries, i.e.
		// ID .
		// MAP-ID .
		// USERNAME .
		// TIME .
		// TYPE .
		// PARENT .
		// PERSISTENT (not required)

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement mapIDQuery = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			String sql = "SELECT R.id, A.parameter, A.value, U.name, R.timestamp, A.element_id, E.type FROM " + Config.dbName
					+ ".revisions AS R LEFT JOIN " + Config.dbName + ".actions AS A ON R.id = A.revision_id LEFT JOIN " + Config.dbName
					+ ".users AS U ON U.id = R.creator_user_id LEFT JOIN " + Config.dbName
					+ ".elements AS E ON A.element_id = E.id WHERE R.map_id = ?";

			mapIDQuery = con.prepareStatement(sql);
			mapIDQuery.setInt(1, mapID);

			rs = mapIDQuery.executeQuery();

			// int elementID = 0;
			int revisionID = 0;

			boolean revisionChanged = false;

			Action a = null;
			while (rs.next()) {

				if (revisionID != rs.getInt("id")) {
					int olDRevisionID = revisionID; // rev 1948 anahuac
					revisionID = rs.getInt("id");
					revisionChanged = true;
					if (a != null) {
						// Revision 1948 anahuac start
						if (a.getCmd().equals(Commands.DeleteElement)) {
							Vector<Integer> deletedElementIDs = Element.getAllElementsDeletedOnRevision(olDRevisionID);

							String itemsSize = deletedElementIDs.size() + "";
							a.replaceParameter(ParameterTypes.NumActions, itemsSize);
							int tmpID = Integer.parseInt(a.getParameterValue(ParameterTypes.Id));
							//String tmpUserActionID = a.getParameterValue(ParameterTypes.UserActionId);

							for (Integer i : deletedElementIDs) {
								if (tmpID == i) { // only create actions for the children, the container delete-action is "a".
									continue;
								}
								Action tmpAction = new Action(Commands.DeleteElement, Categories.Map);
								tmpAction.addParameter(ParameterTypes.Id, i + "");

								Vector<ParameterTypes> paramList = a.getParameterTypes();

								for (ParameterTypes paramName : paramList) {
									if (paramName.equals(ParameterTypes.Id)) {
										continue;
									}
									tmpAction.addParameter(paramName, a.getParameterValue(paramName));
								}

								p.addAction(tmpAction);
							}
						}
						// Ende Revision 1948 anahuac
						p.addAction(a);
						a = null;
					}
				}

				// Generate new Action
				if (revisionChanged) {
					// If the new revision is the start revision of an element,
					// then the element is freshly created
					if (rs.getInt("id") == Element.getStartRevisionID(rs.getInt("element_id"))) {

						a = new Action(Commands.CreateElement, Categories.Map);
						a.addParameter(ParameterTypes.Type, rs.getString("type"));

						Vector<Integer> parents = Element.getParentElementIDs(rs.getInt("element_id"));
						for (Integer i : parents) {
							a.addParameter(ParameterTypes.Parent, i.toString());
						}

						a.addParameter(ParameterTypes.Id, rs.getInt("element_id") + "");
					}
					// Check if it is a delete revision
					else if (Revision.isEndRevisionOfAnElement(rs.getInt("id"))) {
						// Revision 1948
						a = new Action(Commands.DeleteElement, Categories.Map);
						a.addParameter(ParameterTypes.Id, rs.getInt("element_id") + "");

						// Revision 1931
						// Vector<Integer> deletedElementIDs = Element.getAllElementsDeletedOnRevision(rs.getInt("id"));
						//
						// for (Integer i : deletedElementIDs) {
						// a = new Action(Commands.DeleteElement, Categories.Map);
						// a.addParameter(ParameterTypes.MapId, mapID + "");
						// a.addParameter(ParameterTypes.Id, i + "");
						// a.addParameter(ParameterTypes.UserName, rs.getString("name") + "");
						// a.addParameter(ParameterTypes.Time, rs.getTimestamp("timestamp").getTime() + "");
						//
						// p.addAction(a);
						// }
						//
						// // This is commented because it caused errors with the
						// // NUM-ACTIONS that are saved for the AI client.
						// // However, it should work without. If not, we have to
						// // find another solution.
						// // revisionChanged = false;
						// // a = null;
						// continue;
					}

					// Chat message
					else if ("MESSAGE".equalsIgnoreCase(rs.getString("parameter"))) {
						a = new Action(Commands.ChatMsg, Categories.Communication);
						// TD, 24.11.
						// a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));
						// a.addParameter(ParameterTypes.Replay, "TRUE");
					}

					// Otherwise it is only an update
					else if (rs.getString("parameter") != null) {
						a = new Action(Commands.UpdateElement, Categories.Map);
						a.addParameter(ParameterTypes.Id, rs.getInt("element_id") + "");
					}

					// Revision without any effect which not needs to be
					// replayed (e.g. map creation, user join, user leave, etc)
					else {
						continue;
					}

					a.addParameter(ParameterTypes.MapId, mapID + "");
					a.addParameter(ParameterTypes.UserName, rs.getString("name") + "");
					a.addParameter(ParameterTypes.Time, rs.getTimestamp("timestamp").getTime() + "");
					a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));

					revisionChanged = false;
				}

				// No new action, just add the parameter
				else {
					a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));
				}
			}

			// Add last action (requires at least one action in total)
			if (a != null) {
				p.addAction(a);
				a = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{mapIDQuery.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
		return p;
	}

	public static ActionPackage getCompleteElementInformationForReplay(ActionPackage p, int mapID, boolean includeTimeIndex,
			long realStartSec) {
		// Only actions that focus on an actual element will be reconstructed,
		// i.e. chat messages, user joins etc. will not be recovered

		// Regenerate information filtered out earlier based on database
		// entries, i.e.
		// ID .
		// MAP-ID .
		// USERNAME .
		// TIME .
		// TYPE .
		// PARENT .
		// PERSISTENT (not required)

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement mapIDQuery = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			String sql = "SELECT R.id, A.parameter, A.value, U.name, R.timestamp, A.element_id, E.type FROM " + Config.dbName
					+ ".revisions AS R LEFT JOIN " + Config.dbName + ".actions AS A ON R.id = A.revision_id LEFT JOIN " + Config.dbName
					+ ".users AS U ON U.id = R.creator_user_id LEFT JOIN " + Config.dbName
					+ ".elements AS E ON A.element_id = E.id WHERE R.map_id = ?";

			mapIDQuery = con.prepareStatement(sql);
			mapIDQuery.setInt(1, mapID);

			rs = mapIDQuery.executeQuery();

			// int elementID = 0;
			int revisionID = 0;

			boolean revisionChanged = false;

			Action a = null;
			while (rs.next()) {

				if (revisionID != rs.getInt("id")) {
					revisionID = rs.getInt("id");
					revisionChanged = true;
					if (a != null) {
						p.addAction(a);
						a = null;
					}
				}

				// Generate new Action
				if (revisionChanged) {
					// If the new revision is the start revision of an element,
					// then the element is freshly created
					if (rs.getInt("id") == Element.getStartRevisionID(rs.getInt("element_id"))) {

						a = new Action(Commands.CreateElement, Categories.Replay);
						a.addParameter(ParameterTypes.Type, rs.getString("type"));

						Vector<Integer> parents = Element.getParentElementIDs(rs.getInt("element_id"));
						for (Integer i : parents) {
							a.addParameter(ParameterTypes.Parent, i.toString());
						}

						a.addParameter(ParameterTypes.Id, rs.getInt("element_id") + "");
					}
					// Check if it is a delete revision
					else if (Revision.isEndRevisionOfAnElement(rs.getInt("id"))) {
						Vector<Integer> deletedElementIDs = Element.getAllElementsDeletedOnRevision(rs.getInt("id"));

						for (Integer i : deletedElementIDs) {
							a = new Action(Commands.DeleteElement, Categories.Replay);
							a.addParameter(ParameterTypes.MapId, mapID + "");
							a.addParameter(ParameterTypes.Id, i + "");
							a.addParameter(ParameterTypes.UserName, rs.getString("name") + "");

							if (includeTimeIndex) {
								a.addParameter(ParameterTypes.ReplayTime, (rs.getTimestamp("timestamp").getTime() / 1000) - realStartSec
										+ "");
							}
							a.addParameter(ParameterTypes.Time, rs.getTimestamp("timestamp").getTime() + "");

							p.addAction(a);
						}

						revisionChanged = false;
						a = null;
						continue;
					}

					// Chat message
					else if ("MESSAGE".equalsIgnoreCase(rs.getString("parameter"))) {
						// a = new Action("CHAT-MSG", "COMMUNICATION");
						a = new Action(Commands.ChatMsg, Categories.Replay);
						// TD, 24.11.
						// a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));
						a.addParameter(ParameterTypes.Replay, "TRUE");
					}

					// Otherwise it is only an update
					else if (rs.getString("parameter") != null) {
						a = new Action(Commands.UpdateElement, Categories.Replay);
						a.addParameter(ParameterTypes.Id, rs.getInt("element_id") + "");
					}

					// Revision without any effect which not needs to be
					// replayed (e.g. map creation, user join, user leave, etc)
					else {
						continue;
					}

					a.addParameter(ParameterTypes.MapId, mapID + "");
					a.addParameter(ParameterTypes.UserName, rs.getString("name") + "");

					if (includeTimeIndex) {
						a.addParameter(ParameterTypes.ReplayTime, (rs.getTimestamp("timestamp").getTime() / 1000) - realStartSec + "");
					}

					a.addParameter(ParameterTypes.Time, rs.getTimestamp("timestamp").getTime() + "");

					a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));

					revisionChanged = false;
				} else {
					// No new action, just add the parameter

					a.addParameter(ParameterTypes.fromString(rs.getString("parameter")), rs.getString("value"));
				}
			}

			// Add last action (requires at least one action in total)
			if (a != null) {
				p.addAction(a);
				a = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getSQLState());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{mapIDQuery.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
		return p;
	}

	public static void removeMapFromDB(int mapID) {
		Connection con = null;
		PreparedStatement deleteMap = null;
		PreparedStatement deleteMapAlt = null;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			deleteMap = con.prepareStatement("DELETE FROM " + Config.dbName + ".maps WHERE id = ? LIMIT 1;");
			deleteMap.setInt(1, mapID);
			deleteMap.executeUpdate();

			deleteMapAlt = con.prepareStatement("DELETE FROM " + Config.dbName + ".map_to_user_restriction WHERE map_id = ? LIMIT 1;");
			deleteMapAlt.setInt(1, mapID);
			deleteMapAlt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{deleteMap.close();}catch(Exception e){}
			try{deleteMapAlt.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
	}

	public static void removeFromFileSystem(String mapName) {
		File dir = new File(Config.configDir, "map");
		File[] maps = dir.listFiles();

		for (File f : maps) {
			if (!f.isHidden()) {

				Document doc = null;

				try {
					SAXBuilder builder = new SAXBuilder();
					doc = builder.build(f);

					org.jdom.Element root = doc.getRootElement();
					
					// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
					@SuppressWarnings("unchecked")
					List<org.jdom.Element> mapList = root.getChildren("map");

					for (org.jdom.Element map : mapList) {
						String nameElement = map.getChild("name").getValue();
						if (nameElement.equals(mapName)) {

							// If this is the only map of the file...
							if (mapList.size() == 1) {
								Logger.log("Deleting map file: " + f);
								f.delete();
								return;
							}

							// If there are other map definitions in this
							// file...
							else {
								root.removeContent(map);

								XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

								try {
									FileOutputStream output = new FileOutputStream(f);
									outputter.output(doc, output);
									output.flush();
									output.close();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

								Logger.log("Removing map from file: " + f);
								return;
							}
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static String getMapName(int mapID) {

		String name = null;

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement getMapName = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			getMapName = con.prepareStatement("SELECT name FROM " + Config.dbName + ".maps WHERE id = ?;");
			getMapName.setInt(1, mapID);

			rs = getMapName.executeQuery();
			if (rs.next()) {
				name = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{getMapName.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return name;
	}

	public static boolean delete(int mapID) {

		// Delete all revisions of this map
		Vector<Integer> revisionIDs = Revision.getIDsOfRevisionsConnectedToMap(mapID);

		for (int i : revisionIDs) {
			Revision.delete(i);
		}

		// Delete from file system
		String mapName = Map.getMapName(mapID);
		Map.removeFromFileSystem(mapName);

		// Delete from database
		Map.removeMapFromDB(mapID);

		return true;
	}

	public static int getMapID(String mapName) {
		Connection con = null;
		PreparedStatement getMapName = null;
		ResultSet rs = null;
		int returnInt = -1;

		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			getMapName = con.prepareStatement("SELECT id FROM " + Config.dbName + ".maps WHERE name = ? LIMIT 1;");
			getMapName.setString(1, mapName);
			rs = getMapName.executeQuery();
			if (rs.next()) {
				returnInt = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{getMapName.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
		return returnInt;
	}

	public static Vector<Integer> getIDsOfMapsThatUseTheTemplate(int templateID) {
		Vector<Integer> mapIDs = new Vector<Integer>();

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement getMapIDs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			getMapIDs = con.prepareStatement("SELECT id FROM " + Config.dbName + ".maps WHERE template_id = ?;");
			getMapIDs.setInt(1, templateID);

			rs = getMapIDs.executeQuery();

			while (rs.next()) {
				mapIDs.add(rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{getMapIDs.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return mapIDs;
	}

	public static Vector<Integer> getIDsOfMapsThatAreCreatedByUser(int userID) {
		Vector<Integer> mapIDs = new Vector<Integer>();

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement getMapIDs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			getMapIDs = con.prepareStatement("SELECT id FROM " + Config.dbName + ".maps WHERE creator_user_id = ?;");
			getMapIDs.setInt(1, userID);

			rs = getMapIDs.executeQuery();

			while (rs.next()) {
				mapIDs.add(rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{getMapIDs.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return mapIDs;
	}

	public static void setLastRootElementID(int mapID, int maxRootElementID) {
		Connection con = null;
		PreparedStatement update = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			update = con.prepareStatement("UPDATE " + Config.dbName
					+ ".maps SET last_root_element_id = ? WHERE maps.id = ? LIMIT 1;");
			update.setInt(1, maxRootElementID);
			update.setInt(2, mapID);

			System.err.println(update.toString());

			update.executeUpdate();
			System.err.println("Done");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{update.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}
	}

	public static int getNumberOfMapRevisionsCreatedByUser(int userID) {
		int sessionCount = 0;

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement getMapIDs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			getMapIDs = con.prepareStatement("SELECT COUNT(*) FROM " + Config.dbName
					+ ".revisions WHERE creator_user_id = ?;");
			getMapIDs.setInt(1, userID);

			rs = getMapIDs.executeQuery();

			while (rs.next()) {
				sessionCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{getMapIDs.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return sessionCount;
	}
	
	// TODO Zhenyu
	public static void setBackgroundImage(int mapID, String imageurl) {
		Connection con = null;
		PreparedStatement tableCol = null;
		PreparedStatement update = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			tableCol = con.prepareStatement("desc maps");
			rs = tableCol.executeQuery();
			boolean exitsImageUrl = false;
			while (rs.next()) {
				if(rs.getString(1).contains("backgroundimageurl"))
				{
					exitsImageUrl = true;
				}
			}
			
			if(exitsImageUrl)
			{
				update = con.prepareStatement("UPDATE " + Config.dbName
						+ ".maps SET backgroundimageurl = ? WHERE maps.id = ? LIMIT 1;");
				update.setString(1, imageurl);
				update.setInt(2, mapID);
				
				update.executeUpdate();

			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{tableCol.close();}catch(Exception e){}
			try{update.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
			}
		}
	}

	// TODO Zhenyu
	public static String getImageUrl(int mapID) {
		String url = null;

		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement tableCol = null;
		PreparedStatement getMapIDs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Map.class);
			// con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);

			tableCol = con.prepareStatement("desc maps");
			rs = tableCol.executeQuery();
			boolean exitsImageUrl = false;
			while (rs.next()) {
				if(rs.getString(1).contains("backgroundimageurl"))
				{
					exitsImageUrl = true;
				}
			}
			
			if(exitsImageUrl)
			{
				getMapIDs = con.prepareStatement("SELECT backgroundimageurl FROM " + Config.dbName
						+ ".maps WHERE maps.id = ?;");
				getMapIDs.setInt(1, mapID);

				rs1 = getMapIDs.executeQuery();

				while (rs1.next()) {
					url = rs1.getString(1);
				}
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{rs.close();}catch(Exception e){}
			try{rs1.close();}catch(Exception e){}
			try{tableCol.close();}catch(Exception e){}
			try{getMapIDs.close();}catch(Exception e){}
			if (con != null) {
				DatabaseConnectionHandler.closeConnection(Map.class, con);
				// try {
				// con.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		}

		return url;
	}
}