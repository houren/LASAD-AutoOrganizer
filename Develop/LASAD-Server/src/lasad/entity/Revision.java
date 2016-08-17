package lasad.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import lasad.Config;
import lasad.database.DatabaseConnectionHandler;

public class Revision {
	private int id;
	private int mapID;
	private int creatorUserID;
	
	private String description;
	
	private Timestamp timestamp;
	
	public Revision(int mapID, int creatorUserID) {
		this.mapID = mapID;
		this.creatorUserID = creatorUserID;
		
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Revision(int mapID, String description, int creatorUserID) {
		this.mapID = mapID;
		this.creatorUserID = creatorUserID;
		this.description = description;
		
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Revision(int mapID, int creatorUserID, long time) {
		this.mapID = mapID;
		this.creatorUserID = creatorUserID;
		
		this.timestamp = new Timestamp(time);
	}

	public void saveToDatabase() {
		Connection con = null; 	
		PreparedStatement insertRevision = null;
		PreparedStatement getLastID = null;	
		ResultSet rs = null;
		
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			insertRevision = con.prepareStatement("INSERT INTO "+Config.dbName+".revisions (id, map_id, creator_user_id, description, timestamp) VALUES (NULL, ?, ?, ?, ?);");
			insertRevision.setInt(1, this.mapID);
			insertRevision.setInt(2, this.creatorUserID);
			insertRevision.setString(3, this.description);
			insertRevision.setTimestamp(4, this.timestamp);
			insertRevision.executeUpdate();		
			
			getLastID = con.prepareStatement("SELECT LAST_INSERT_ID()");
			rs = getLastID.executeQuery();
			rs.next();
			
			this.id = rs.getInt(1);
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getLastID.close();}catch(Exception e){}
			try{insertRevision.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getCreatorUserID() {
		return creatorUserID;
	}

	public void setCreatorUserID(int creatorUserID) {
		this.creatorUserID = creatorUserID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public static boolean isEndRevisionOfAnElement(int revisionID) {
		boolean end = false;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement isEndRevision = null;
		
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			isEndRevision = con.prepareStatement("SELECT id FROM "+Config.dbName+".elements WHERE end_revision_id = ?;");
			isEndRevision.setInt(1, revisionID);

			rs = isEndRevision.executeQuery();
			
			if(rs.next()) {
				end = true;
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{isEndRevision.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return end;
	}

	public static void delete(int i) {
		Vector<Integer> elementIDs = Element.getIDsOfElementsConnectedToRevision(i);
		for(int j : elementIDs) {
			Element.delete(j);
		}
		
		Revision.removeRevisionFromDB(i);
	}
	

	public static void removeRevisionFromDB(int revID) {
		Connection con = null; 	
		PreparedStatement deleteRevision = null;
		PreparedStatement deleteRevisionActions = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			// Delete actual revision
			deleteRevision = con.prepareStatement("DELETE FROM "+Config.dbName+".revisions WHERE id = ? LIMIT 1;");
			deleteRevision.setInt(1, revID);
			deleteRevision.executeUpdate();
			
			// Delete revision actions
			deleteRevisionActions = con.prepareStatement("DELETE FROM "+Config.dbName+".actions WHERE revision_id = ?;");
			deleteRevisionActions.setInt(1, revID);
			deleteRevisionActions.executeUpdate();
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{deleteRevision.close();}catch(Exception e){}
			try{deleteRevisionActions.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}

	public static Vector<Integer> getIDsOfRevisionsConnectedToMap(int mapID) {
	Vector<Integer> revisionIDs = new Vector<Integer>();
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getRevisionIDs = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getRevisionIDs = con.prepareStatement("SELECT id FROM "+Config.dbName+".revisions WHERE map_id = ?;");
			getRevisionIDs.setInt(1, mapID);
			
			rs = getRevisionIDs.executeQuery();
			
			while(rs.next()) {
				revisionIDs.add(rs.getInt(1));
			}
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getRevisionIDs.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return revisionIDs;
	}

	public static long getTime(Integer firstElement) {		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getRevisionTime = null;
		long returnVal = -1;
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getRevisionTime = con.prepareStatement("SELECT timestamp FROM "+Config.dbName+".revisions WHERE id = ?;");
			getRevisionTime.setInt(1, firstElement);
			
			rs = getRevisionTime.executeQuery();
			
			if(rs.next()) {
				returnVal = (rs.getTimestamp("timestamp")).getTime();
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
			try{getRevisionTime.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return returnVal;
	}

	public static long getTimeOfMapRevision(int mapID, int whichTime) {

		long time = 0;
		
		String sort = "";
		// Return time of first revision;
		if(whichTime == 0) {
			sort = "ASC";
		}
		// Return time of last revision;
		else if(whichTime == 1) {
			sort = "DESC";
		}
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getRevisionTime = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Revision.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getRevisionTime = con.prepareStatement("SELECT timestamp FROM "+Config.dbName+".revisions WHERE map_id = ? ORDER BY id "+sort+" LIMIT 1;");
			getRevisionTime.setInt(1, mapID);
			
			rs = getRevisionTime.executeQuery();
			
			if(rs.next()) {
				time = rs.getTimestamp("timestamp").getTime();
			}
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getRevisionTime.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Revision.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return time;
	}
}