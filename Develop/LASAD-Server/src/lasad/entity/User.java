package lasad.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import lasad.Config;
import lasad.database.DatabaseConnectionHandler;
import lasad.helper.MD5Converter;
import lasad.logging.Logger;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class User {
	
	private int userID;
	private long lastHeartbeat;
	private String nickname, password, role, sessionID;
	
	boolean usingWebservice = false;
	
	public User() { }
	
	public User(String nickname, String password, String role) {
		this(nickname, password, role, false);
	}
	
	public User(String nickname, String password, String role, boolean isPasswordEncrypted) {
		this.nickname = nickname;
		
		if (!isPasswordEncrypted){
			this.password = MD5Converter.toMD5(password);
		}
		else {
			this.password = password;
		}
		
		this.role = role;
		this.lastHeartbeat = System.currentTimeMillis();
	}
	
	private User(String nickname, String role) {
		this.nickname = nickname;
		this.role = role;
	}
	
	public User(ResultSet rs) throws SQLException {		
		this(rs.getString("name"), rs.getString("pw"), rs.getString("role"));
		this.userID = rs.getInt("id");	
	}
	
	public boolean isUsingWebservice() {
		return usingWebservice;
	}

	public void setUsingWebservice(boolean usesWebservice) {
		this.usingWebservice = usesWebservice;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public static Integer getUserID(String name) {
		if(name.equals("")) {
			return null;
		}
		
		Connection con = null; 
		PreparedStatement getUserID = null;		
		ResultSet rs = null;
		int returnVal = -1;
		
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getUserID = con.prepareStatement("SELECT id FROM "+Config.dbName+".users WHERE name = ?;");
			getUserID.setString(1, name);
			rs = getUserID.executeQuery();
			if(rs.next()) {
				returnVal = rs.getInt(1);
			}
						
		} catch (SQLException e){
			e.printStackTrace();
			returnVal = -1;
		} catch (Exception e) {
			e.printStackTrace();
			returnVal = -1;
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getUserID.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}

		return returnVal;
	}
	
	public static File generateUserConfigurationFile(Action a) {

		Document doc = null;
		org.jdom.Element root = new org.jdom.Element("lasad-users");
				
		doc = new Document(root);

		org.jdom.Element user = new org.jdom.Element("user");
		
		org.jdom.Element nickname = new org.jdom.Element("nickname");
		nickname.setText(a.getParameterValue(ParameterTypes.UserName));
		
		org.jdom.Element password = new org.jdom.Element("password");
		password.setText(a.getParameterValue(ParameterTypes.Password));
		
		org.jdom.Element role = new org.jdom.Element("role");
		role.setText(a.getParameterValue(ParameterTypes.Role));
		
		user.addContent(nickname);
		user.addContent(password);
		user.addContent(role);
		
		root.addContent(user);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		File userDir = new File(Config.configDir, "user");
		File f = new File(userDir,  a.getParameterValue(ParameterTypes.UserName) + " - " + timestamp.toString().substring(0, 10) + ".xml");

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(f);
			outputter.output(doc, output);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{output.close();}catch(Exception e){}
		}
		
		return f;
	}
	
	public static User login(String name, String pw) {
		return login(name, pw, false);
	}
	
	public static User login(String name, String pw, boolean isPasswordEncrypted) {
		
		User u = null;
		
		ResultSet rs = null;
		
		if (!isPasswordEncrypted){
			pw = MD5Converter.toMD5(pw);
		}
		Connection con = null; 	
		PreparedStatement getUser = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getUser = con.prepareStatement("SELECT * FROM "+Config.dbName+".users WHERE name = ? AND pw = ?;");
			getUser.setString(1, name);
			getUser.setString(2, pw);
			rs = getUser.executeQuery();
			
			if(rs.next()) {
				u = new User(rs);	
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getUser.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		// If login failed...
		if(u == null) {
			u = new User();
		}
		
		return u;
	}

	public void saveToDatabase() {
		
		if(User.getUserID(this.nickname) == -1) { // User does not exist
		
			Connection con = null; 
			PreparedStatement insertUser = null;		
			
			try {
				con = DatabaseConnectionHandler.getConnection(User.class);
//				con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
				
				insertUser = con.prepareStatement("INSERT INTO "+Config.dbName+".users (id, name, pw, role) VALUES (NULL, ?, ?, ?);");
				insertUser.setString(1, this.nickname);
				insertUser.setString(2, this.password);
				insertUser.setString(3, this.role);
				insertUser.executeUpdate();
				
			} catch (SQLException e){
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try{insertUser.close();}catch(Exception e){}
				if(con != null) {
					DatabaseConnectionHandler.closeConnection(User.class, con);
//					try {
//						con.close();
//					} catch (SQLException e){
//						e.printStackTrace();
//					}
				}
			}
		}
		else {
			// User already exists, do nothing
		}
	}
	
	public static Vector<User> getAllUsers() {
		Vector<User> u = new Vector<User>();
	
		Connection con = null; 	
		PreparedStatement getAllUsersWithoutPW = null;	
		ResultSet rs = null;
		
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getAllUsersWithoutPW = con.prepareStatement("SELECT name, role FROM "+Config.dbName+".users;");
			
			rs = getAllUsersWithoutPW.executeQuery();
			
			while(rs.next()) {
				if(!rs.getString("name").equals("Unknown")) {
					u.add(new User(rs.getString("name"), rs.getString("role")));
				}
			}
				
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getAllUsersWithoutPW.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return u;
	}
	
	public static Vector<String> getUserListWithoutRoles() {
		Vector<String> u = new Vector<String>();
	
		Connection con = null; 	
		PreparedStatement getAllUsernames = null;	
		ResultSet rs = null;
		
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getAllUsernames = con.prepareStatement("SELECT name FROM "+Config.dbName+".users;");
			
			rs = getAllUsernames.executeQuery();
			
			while(rs.next()) {
				u.add(rs.getString("name"));
			}
				
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getAllUsernames.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return u;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public long getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static String getName(int id) {
		
		String name = null;
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getUser = null;
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getUser = con.prepareStatement("SELECT name FROM "+Config.dbName+".users WHERE id = ?;");
			getUser.setInt(1, id);
			
			rs = getUser.executeQuery();
			rs.next();
			
			name = rs.getString(1);
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getUser.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return name;		
	}
	
	public static int getId(String name) {
		
		int id=-1;
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getUser = null;
		try {
			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getUser = con.prepareStatement("SELECT id FROM "+Config.dbName+".users WHERE name = ?;");
			getUser.setString(1, name);
			
			rs = getUser.executeQuery();
			if (rs.next()){
				id = rs.getInt(1);
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getUser.close();}catch(Exception e){}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		return id;		
	}
	
	public static String getRole(String username) {
		
		String role = null;
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getRole = null;
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getRole = con.prepareStatement("SELECT role FROM "+Config.dbName+".users WHERE name = ?;");
			getRole.setString(1, username);
			
			rs = getRole.executeQuery();
			rs.next();
			
			role = rs.getString("role");
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getRole.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return role;		
	}
	
	public static String getRoleByID(int userID) {
		
		String role = null;
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getRole = null;
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getRole = con.prepareStatement("SELECT role FROM "+Config.dbName+".users WHERE id = ?;");
			getRole.setInt(1, userID);
			
			rs = getRole.executeQuery();
			rs.next();
			
			role = rs.getString("role");
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getRole.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return role;		
	}

	public static boolean delete(String userName, int userID, int newUserID) {
		// Delete from file system
		User.removeFromFileSystem(userName);
		
		// Delete from database and replace important relations by another user
		User.removeUserFromDB(userID, newUserID);

		return true;
	}
	
	public static void removeUserFromDB(int userID, int newUserID) {
		Connection con = null; 
		PreparedStatement deleteUser = null;
		PreparedStatement updateUserP1 = null;
		PreparedStatement updateUserP2 = null;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(User.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			// Delete the concrete user
			deleteUser = con.prepareStatement("DELETE FROM "+Config.dbName+".users WHERE id = ? LIMIT 1;");
			deleteUser.setInt(1, userID);
			
			deleteUser.executeUpdate();
			
			// Replace the user relations by a new one - Part 1
			updateUserP1 = con.prepareStatement("UPDATE revisions SET creator_user_id = ? WHERE creator_user_id = ?;");
			updateUserP1.setInt(1, newUserID);
			updateUserP1.setInt(2, userID);
			updateUserP1.executeUpdate();
			
			// Replace the user relations by a new one - Part 2
			updateUserP2 = con.prepareStatement("UPDATE maps SET creator_user_id = ? WHERE creator_user_id = ?;");
			updateUserP2.setInt(1, newUserID);
			updateUserP2.setInt(2, userID);
			updateUserP2.executeUpdate();
			
//	Not needed, if the user is deleted.			
//			// Replace map restrictions with a new user
//			PreparedStatement updateUserRestriction = con.prepareStatement("UPDATE maps SET restricted_to_user_id = ? WHERE restricted_to_user_id = ?;");
//			updateUserRestriction.setInt(1, newUserID);
//			updateUserRestriction.setInt(2, userID);
//			updateUserRestriction.executeUpdate();
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{deleteUser.close();}catch(Exception e){}
			try{updateUserP1.close();}catch(Exception e){}
			try{updateUserP2.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(User.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}

	public static void removeFromFileSystem(String username) {
		File dir = new File(Config.configDir, "user");
		File[] users = dir.listFiles();
		
		for(File f : users) {
			if(!f.isHidden()) {
				
				Document doc = null;
				
				try {
					SAXBuilder builder = new SAXBuilder();
					doc = builder.build(f);
					
					Element root = doc.getRootElement();

					// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
					@SuppressWarnings("unchecked")
					List<Element> userList = root.getChildren("user");
					
					for(Element user : userList) {
						String nickname = user.getChild("nickname").getValue();
						if(nickname.equals(username)) {
							
							// If this is the only user of the file...
							if(userList.size() == 1) {
								Logger.log("Deleting user file: "+f);
								f.delete();
								return;
							}
							
							// If there are other user definitions in this file...
							else {
								root.removeContent(user);
								
								XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

								FileOutputStream output = null;
								try {
									output = new FileOutputStream(f);
									outputter.output(doc, output);
									output.flush();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									output.close();
								}
								
								Logger.log("Removing user from file: "+f);
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
}