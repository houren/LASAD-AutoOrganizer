package lasad.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import lasad.Config;
import lasad.database.DatabaseConnectionHandler;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ActionParameter {
	
	public static void saveParametersForRevision(int revision_id, int element_id, Action a) {
		a = removeActionOverhead(a);
		Connection con = null; 	
		PreparedStatement insertParameter = null;	
		
		try {
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			con = DatabaseConnectionHandler.getConnection(ActionParameter.class);
			
			insertParameter = con.prepareStatement("INSERT INTO "+Config.dbName+".actions (id, revision_id, element_id, parameter, value) VALUES (NULL, ?, ?, ?, ?);");
			
			for(Parameter p : a.getParameters()) {
				insertParameter.setInt(1, revision_id);
				insertParameter.setInt(2, element_id);
				insertParameter.setString(3, p.getType().getOldParameter());
				insertParameter.setString(4, p.getValue());
				insertParameter.executeUpdate();
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{insertParameter.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(ActionParameter.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}
	
	public static void saveParametersForRevision(int revision_id, Action a) {
		a = removeActionOverhead(a);
		
		Connection con = null;
		PreparedStatement insertParameter = null; 		
		
		try {
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			con = DatabaseConnectionHandler.getConnection(ActionParameter.class);
			
			insertParameter = con.prepareStatement("INSERT INTO "+Config.dbName+".actions (id, revision_id, element_id, parameter, value) VALUES (NULL, ?, NULL, ?, ?);");
			
			for(Parameter p : a.getParameters()) {
				insertParameter.setInt(1, revision_id);
				insertParameter.setString(2, p.getType().getOldParameter());
				insertParameter.setString(3, p.getValue());
				insertParameter.executeUpdate();
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{insertParameter.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(ActionParameter.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}
	
	/**
	 * Filters out information that are accessible via database anyway and, hence, not have to be saved as twice.
	 * However, these parameters must be recovered on late map join
	 * 
	 * @param a
	 * @return b, i.e. a filtered Action
	 */
	private static Action removeActionOverhead(Action a) {
		Action b = new Action(a.getCmd(), a.getCategory());
		
		for(Parameter p : a.getParameters()) {
			ParameterTypes n = p.getType();
			if(!n.equals(ParameterTypes.Id) && !n.equals(ParameterTypes.MapId) && !n.equals(ParameterTypes.Received) && !n.equals(ParameterTypes.UserName) && !n.equals(ParameterTypes.Time) && !n.equals(ParameterTypes.Type) && !n.equals(ParameterTypes.Parent) && !n.equals(ParameterTypes.Persistent)) {
				b.getParameters().add(p);
			}
		}
		
		return b;
	}
}