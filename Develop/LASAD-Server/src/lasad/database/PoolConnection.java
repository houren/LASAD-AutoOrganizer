package lasad.database;

import java.sql.Connection;

public class PoolConnection{

	private Connection con;
	private boolean	isInUse;
	private Object owner;


	public PoolConnection(Connection con, boolean isInUse, Object owner){
		this.con=con;
		this.isInUse=isInUse;
		this.owner = owner;
	}


	public Connection getConnection() {
		return con;
	}


	public boolean isInUse() {
		return isInUse;
	}


	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}


	public Object getOwner() {
		return owner;
	}


	public void setOwner(Object owner) {
		this.owner = owner;
	}


}
