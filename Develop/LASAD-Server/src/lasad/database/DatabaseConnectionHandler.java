package lasad.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Vector;

import lasad.Config;
import lasad.Server;
import lasad.logging.Logger;

/**
 * @author FL modified by SN DatabaseConnectionHandler handles the connection to the database. It sets up a ConnectionPool. With
 *         getConnection() you have access to that Connection Pool.
 */
public class DatabaseConnectionHandler {

	private static Vector<PoolConnection> connectionPool = new Vector<PoolConnection>();
	private static String dbName = Config.dbName;

	/**
	 * @author FL modified by SN If there is no existing database, it sets up a new one.
	 * @param myServerReference the Server with its parameters
	 */
	public static void initDatabase(Server myServerReference) {
		// If required, you can set this parameter to false, to reload old data
		if ("true".equalsIgnoreCase(myServerReference.conf.parameters.get("Fresh Server"))) {

			Connection con = null;
			try {
				// set up DB connection
				con = getConnection(DatabaseConnectionHandler.class);

				// reset Database (for several reasons), must be set on top ^
				resetDatabase(con);

			} catch (SQLException e) {
				System.err.println("ERROR in SQL: " + e.getSQLState());
				e.printStackTrace();
			} catch (Exception e) {
				Logger.debugLog("ERROR: " + e.toString());
				e.printStackTrace();
			} finally {
				if (con != null) {
					// close Connection
					closeConnection(DatabaseConnectionHandler.class, con);
				}
			}
		}
	}

	/**
	 * @author FL extracted from initDatabase by SN Does the reset from initDatabase.
	 */
	private static void resetDatabase(Connection con) throws SQLException {
		// Check which tables exist and delete them
		PreparedStatement getTableList = con.prepareStatement("SHOW TABLES FROM " + dbName + ";");
		ResultSet rs = getTableList.executeQuery();

		boolean first = true;
		StringBuilder dropSQLString = new StringBuilder();
		dropSQLString.append("DROP TABLE ");
		while (rs.next()) {
			if (first) {
				dropSQLString.append(dbName + "." + rs.getString(1));
				System.out.println("first-----dropSQLString.append" + dbName + "." + rs.getString(1) + "-----");
				first = false;
			} else {
				dropSQLString.append(", " + dbName + "." + rs.getString(1));
				System.out.println("else-----dropSQLString.append" + dbName + "." + rs.getString(1) + "-----");
			}
		}

		if (!first) {
			Statement clearAll = con.createStatement();
			clearAll.execute(dropSQLString.toString());
			System.out.println("-----Clear All executed-----");
			try{clearAll.close();}catch(Exception e){}
		}
		System.out.println("-----Create Table-----");
		// Create new tables needed for the system
		Statement createTable = con.createStatement();

		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".users (id SMALLINT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR ( 255 ) NOT NULL, pw CHAR ( 32 ) NOT NULL, role VARCHAR ( 255 ) NOT NULL, PRIMARY KEY (id), KEY name (name), KEY pw (pw)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".ontologies (id TINYINT( 3 ) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR ( 255 ) NOT NULL, xmlConfig TEXT NOT NULL) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		// A possible foreign key constraint (Example)
		// createTable.execute("CREATE TABLE " + dbName +
		// ".templates (id SMALLINT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR ( 255 ) NOT NULL, xmlConfig TEXT NOT NULL, ontology_id TINYINT ( 3 ) UNSIGNED NOT NULL, INDEX ( `ontology_id` ), FOREIGN KEY ( `ontology_id` ) REFERENCES `lasad`.`ontologies` (`id`) ON DELETE CASCADE) engine 'InnoDB';");

		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".templates (id SMALLINT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR ( 255 ) NOT NULL, xmlConfig TEXT NOT NULL, ontology_id TINYINT ( 3 ) UNSIGNED NOT NULL, PRIMARY KEY (id), KEY ontology_id (ontology_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");

		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".maps (id SMALLINT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR ( 255 ) NOT NULL, template_id SMALLINT ( 5 ) UNSIGNED NOT NULL, creator_user_id SMALLINT ( 5 ) UNSIGNED NOT NULL, last_root_element_id INT ( 11 ) UNSIGNED NOT NULL,backgroundimageurl VARCHAR ( 255 ), timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), KEY template_id (template_id)) engine 'InnoDB'DEFAULT CHARSET=utf8 ;");

		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".revisions (id INT( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT, map_id SMALLINT ( 5 ) UNSIGNED NOT NULL, creator_user_id SMALLINT ( 5 ) UNSIGNED NOT NULL, description VARCHAR ( 255 ) NULL, timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), KEY map_id (map_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".actions (id INT( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT, revision_id INT ( 11 ) UNSIGNED NOT NULL, element_id INT ( 11 ) UNSIGNED DEFAULT NULL, parameter VARCHAR ( 255 ) NULL, value TEXT, PRIMARY KEY (id), KEY revision_id (revision_id), KEY element_id (element_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".elements (id INT( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT, map_id SMALLINT ( 5 ) UNSIGNED NOT NULL, start_revision_id INT( 11 ) UNSIGNED NOT NULL, end_revision_id INT( 11 ) UNSIGNED DEFAULT NULL, type VARCHAR ( 255 ) NOT NULL, last_modified BIGINT NOT NULL DEFAULT 0, PRIMARY KEY (id), KEY map_id (map_id), KEY start_revision_id (start_revision_id), KEY end_revision_id (end_revision_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".element_parent_mapping (id SMALLINT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT, element_id INT ( 11 ) UNSIGNED NOT NULL, parent_element_id INT( 11 ) UNSIGNED NOT NULL, PRIMARY KEY (id), KEY element_id (element_id), KEY parent_element_id (parent_element_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");

		// Add ArgumentModel to database for AutoOrganization.
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".argument_model (id INT( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT, map_id INT ( 11 ) UNSIGNED NOT NULL, element_id INT ( 11 ) NOT NULL, element_type VARCHAR ( 255 ) NOT NULL, root_id INT ( 11 ) NOT NULL, x_left DOUBLE PRECISION ( 7 , 2 ) UNSIGNED NOT NULL, y_top DOUBLE PRECISION ( 7 , 2 ) UNSIGNED NOT NULL, width INT ( 5 ) NOT NULL, height INT ( 5 ) NOT NULL, can_be_grouped VARCHAR ( 255 ) NOT NULL, connects_group VARCHAR ( 255 ) NOT NULL, start_box_id INT( 11 ) NOT NULL, end_box_id INT( 11 ) NOT NULL, start_revision_id INT( 11 ) UNSIGNED NOT NULL, end_revision_id INT( 11 ) UNSIGNED DEFAULT NULL, PRIMARY KEY (id), KEY map_id (map_id), KEY element_id (element_id), KEY element_type(element_type), KEY start_revision_id (start_revision_id), KEY end_revision_id (end_revision_id)) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");

		// A seperated Table for map and the corresponding user Gzy
		createTable
				.execute("CREATE TABLE "
						+ dbName
						+ ".map_to_user_restriction (id INT( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, map_id SMALLINT ( 5 ) UNSIGNED NOT NULL, user_id SMALLINT ( 5 ) UNSIGNED NOT NULL) engine 'InnoDB' DEFAULT CHARSET=utf8 ;");
		
		try{getTableList.close();}catch(Exception e){}
		try{rs.close();}catch(Exception e){}
		try{createTable.close();}catch(Exception e){}
	}

	/**
	 * @author SN return an unused connection from the connectionPool vector (a connection Pool), creates a new one if there is no
	 *         unused connection. synchronized, to ensure no two clients get the same connection.
	 * @param Object owner -> who wants to get the connection
	 * @return unused connection
	 */
	public static synchronized Connection getConnection(Object owner) {
		// Date timestamp = new Date();
		// System.out.println("--> Connection angefordert von: " +owner.toString()+" "
		// +timestamp.getHours()+":"+timestamp.getMinutes()+":"+timestamp.getSeconds());

		LinkedList<PoolConnection> toRemove = new LinkedList<PoolConnection>();
		for (PoolConnection poolCon : connectionPool) {
			if (!poolCon.isInUse()) { // if there is any free Connection
				try {
					if (poolCon.getConnection().isValid(0)) {
						poolCon.setInUse(true); // set it inUse = true
						poolCon.setOwner(owner);
						// System.out.println("------connection reuse: Connection at Pool Position: " +
						// connectionPool.indexOf(poolCon));
						return poolCon.getConnection(); // break
					}
					// System.out.println("Removed connection from the connection pool since it has been closed");
					toRemove.add(poolCon);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (!toRemove.isEmpty()) {
			connectionPool.removeAll(toRemove);
		}

		// no free Connection
		Connection con;
		try {
			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword); // set up a new connection
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		connectionPool.addElement(new PoolConnection(con, true, owner));
		Logger.debugLog("new Connection in Connection Pool");
		return con;
	}

	/**
	 * @author SN gives the connection back to the pool => set connection as unused
	 * @param owner -> who wants to close
	 * @param con -> connection to be closed
	 */
	public static synchronized void closeConnection(Object owner, Connection con) {

		for (PoolConnection poolCon : connectionPool) {
			if (poolCon.getConnection().equals(con)) {
				// System.out.println("------give back Connection: Connection at Pool Position: "+connectionPool.indexOf(poolCon));

				// check that only the owner of a connection can close it
				boolean ownercheck = false;
				if (poolCon.getOwner().equals(owner)) {
					ownercheck = true;
				}
				assert (ownercheck);

				// System.out.println("Besitzer passt"+ownercheck);
				// System.out.println("Besitzer der Connection: "+poolCon.getOwner().toString()+"; Dieser hier will schliessen: "+owner.toString());
				poolCon.setInUse(false);
				break;
			}
		}
	}

}