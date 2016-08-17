package lasad.gwt.client.ui.workspace.tableview;

public enum TableCellTypeEnum {

	BOX("box"),

	RELEATION("relation"),

	BLANK("blank");

	
	private String type;

	private TableCellTypeEnum(String type) {
		this.type = type;
	}

	
	@Override
	public String toString() {
		
		return type;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
