package lasad.gwt.client.ui.workspace.tableview;

/**
 * @author erkang.zhao
 * @version 0.1
 */
public enum ArgumentEditionStyleEnum {

	GRAPH("Graph"),
	
	TABLE("Table");
	
	private String name;
	
	private ArgumentEditionStyleEnum(String name) {
		this.name = name;
	}
	

	@Override
	public String toString() {
		return name;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
