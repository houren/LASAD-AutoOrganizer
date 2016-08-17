package lasad;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lasad.entity.Map;
import lasad.entity.Ontology;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * The config holds all relevant information to run the server including port
 * for rmiregistry, database settings and logging activities
 * 
 * @author Frank Loll
 * 
 */
public class Config {

	public final String templatePath = "template";

	public static File configDir = new File("."); 
	public static String dbProvider;
	public static String dbHost;
	public static String dbPort;
	public static String dbName;
	public static String dbUser;
	public static String dbPassword;
	
	public static boolean xmppLogging = false;

	public static String connection;

	public HashMap<String, String> parameters = new HashMap<String, String>();

	private final Server myServerReference;

	public Config(Server myServerReference) {
		this.myServerReference = myServerReference;
	}

	public void initServerConfiguration() {
		String freshServerString = myServerReference.conf.parameters.get("Fresh Server");
		Logger.debugLog("Config - initServerConfiguration");
		boolean freshServer = true;
		if (freshServerString != null) {
			freshServer = Boolean.parseBoolean(freshServerString);
		}

		loadUsers(freshServer);
		loadOntologies(freshServer);
		loadTemplates(freshServer);
		loadMaps(freshServer);
	}

	public void loadConfigFile(String pathToConfigFile) {
		File configFile = new File(pathToConfigFile);

		Document doc = null;

		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(configFile);

			Element root = doc.getRootElement();

			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<Element> configurationParameters = root.getChildren();

			for (Element param : configurationParameters) {
				// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
				@SuppressWarnings("unchecked")
				List<Element> parameterPair = param.getChildren();
				
				String key = parameterPair.get(0).getValue();
				String value = parameterPair.get(1).getValue();
				
				// Take all of the tags that are inside <value/> as string.
				Element valueElement = parameterPair.get(1);
				if (valueElement.getChildren().size() > 0) {
					XMLOutputter xmlOutputter = new XMLOutputter();
					value = xmlOutputter.outputString(valueElement.getChildren());
				}
				
				parameters.put(key, value);
			}

			String homeDirString = myServerReference.conf.parameters.get("Configuration Home");
			if(homeDirString != null && !"".equals(homeDirString)){
				File fileFromAbsolutePath = new File(homeDirString);
				if(fileFromAbsolutePath.exists()){
					configDir = fileFromAbsolutePath;
				}
				else{
					File fileFromRelPath = new File(configDir, homeDirString);
					if(fileFromRelPath.exists()){
						configDir = fileFromRelPath;
					}
					
					else {
						Logger.logError("Server start failed. Could not locate configuration home directory '" + homeDirString + "' in file system. ");
						System.exit(1);
					}
				}
			}
			
			System.out.println("Configuration Home: " + configDir.getAbsolutePath());
			
			dbProvider = myServerReference.conf.parameters.get("Database Provider");
			dbHost = myServerReference.conf.parameters.get("Database Host");
			dbPort = myServerReference.conf.parameters.get("Database Port");
			dbName = myServerReference.conf.parameters.get("Database Name");
			dbUser = myServerReference.conf.parameters.get("Database User");
			dbPassword = myServerReference.conf.parameters.get("Database Password");
			
			String xmppLoggingString = myServerReference.conf.parameters.get("xmpp-logging");
			if (xmppLoggingString != null) {
				try {
					xmppLogging = Boolean.parseBoolean(xmppLoggingString);
				}
				catch(Exception e){
					Logger.logError("ERROR: in Config.loadConfigFile: bad input for boolean xmpp-logging - " + xmppLoggingString);
				}
			}
			

			connection = "jdbc:" + dbProvider + "://" + dbHost + ":" + dbPort + "/" + dbName + "?characterEncoding=UTF-8";
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadOntologies(boolean freshServer) {
//		if (freshServer) {
		Logger.debugLog("Config - loadOntologies");
			File dir = new File(configDir, "ontology");
			File[] ontologies = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")){
						return true;
					}
					return false;
				}
			});

			for (File f : ontologies) {
				if (!f.isHidden()) {
					Logger.debugLog("Ontology found: " + f);
					Ontology.parseOntologyFromFile(f).saveToDatabase();
				}
			}
//		} else {
//
//		}
	}

	public void loadTemplates(boolean freshServer) {
//		if (freshServer) {
		Logger.debugLog("Config - loadTemplates");
			File dir = new File(configDir, templatePath);
			File[] templates = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")){
						return true;
					}
					return false;
				}
			});
			
			for (File f : templates) {
				if (!f.isHidden()) {
					Logger.debugLog("Template found: " + f);
					Template.parseTemplateFromFile(f).saveToDatabase();
				}
			}
//		} else {
//
//		}
	}

	public void loadUsers(boolean freshServer) {
		Logger.debugLog("Config - loadUser");
		createDefaultUser();
		
//		if (freshServer) {
			File dir = new File(configDir, "user");
			File[] configurations = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")){
						return true;
					}
					return false;
				}
			});

			for (File f : configurations) {
				if (!f.isHidden()) {
					Logger.debugLog("User definition found: " + f);

					parseUsersFromConfiguration(f);
				}
			}
//		} else {
//
//		}
	}

	/**
	 * Should be called before any other user is created. The default user is used as author of elements which are created by a user that does not exist anymore and must have the ID 1 in the database.
	 */
	private void createDefaultUser() {
		if(User.getUserID("Unknown") == -1) { // User does not exist
			User u = new User("Unknown", ((int) (Math.random()*1234567890)) + "", "Standard");
			u.saveToDatabase();
		}
	}

	public void loadMaps(boolean freshServer) {
//		if (freshServer) {
		Logger.debugLog("Config - loadMaps");
			File dir = new File(configDir, "map");
			File[] configurations = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")){
						return true;
					}
					return false;
				}
			});

			for (File f : configurations) {
				if (!f.isHidden()) {
					Logger.debugLog("Map definition found: " + f);

					parseMapsFromConfiguration(f);
				}
			}
//		} else {
			Vector<Map> existingMaps = Map.getMapList();
			
			for(Map m : existingMaps) {
				myServerReference.currentState.maps.put(m.getId(), m);
				myServerReference.currentState.mapUsers.put(m.getId(), new Vector<User>());
	
				Logger.log("Map loaded from database: " + m.getName() + " // " + m.getTemplate_id() + " // " + m.getCreator_user_id());
			}
//		}
	}

	private void parseUsersFromConfiguration(File f) {

		Document doc = null;

		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(f);

			Element root = doc.getRootElement();

			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<Element> userList = root.getChildren("user");

			for (Element user : userList) {
				String nickname = user.getChild("nickname").getValue();
				String password = user.getChild("password").getValue();
				String role = user.getChild("role").getValue();

				new User(nickname, password, role).saveToDatabase();
				Logger.log("User created: " + nickname + " // " + password + " // " + role);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseMapsFromConfiguration(File f) {
		Document doc = null;

		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(f);

			Element root = doc.getRootElement();

			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<Element> mapList = root.getChildren("map");

			for (Element map : mapList) {
				String name = map.getChild("name").getValue();
				String template = map.getChild("template").getValue();
				String creator = map.getChild("creator").getValue();

				// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
				@SuppressWarnings("unchecked")
				List<Element>  restrictedToUser = map.getChild("restricted-to-user").getChildren(); 
				Vector<Integer> userList = new Vector<Integer>();
				if (restrictedToUser.size()>0) 
				{
					for (Element ruser : restrictedToUser)
					{
						userList.add(User.getUserID((ruser.getValue())));
					}
				}
				
				Map m = new Map(name, Template.getTemplateID(template), User.getUserID(creator), userList);
				m.saveToDatabase(myServerReference.ap);

				Logger.log("Map created: " + name + " // " + template + " // " + creator);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
