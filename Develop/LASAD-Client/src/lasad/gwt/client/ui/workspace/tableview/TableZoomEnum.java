package lasad.gwt.client.ui.workspace.tableview;

public enum TableZoomEnum {

	SIZE25("25 %"),

	SIZE50("50 %"),

	SIZE75("75 %"),

	SIZE100("100 %");

	private String size;

	private TableZoomEnum(String size) {

		this.size = size;
	}

	@Override
	public String toString() {

		return size;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
