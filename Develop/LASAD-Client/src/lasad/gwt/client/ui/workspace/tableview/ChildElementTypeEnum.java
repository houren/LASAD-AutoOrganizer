package lasad.gwt.client.ui.workspace.tableview;

public enum ChildElementTypeEnum {

	AWARENESS("Awareness"),

	TEXT("Text"),

	RATING("Rating"),

	URL("Url"),

	TRANSCRIPT("Transcript");

	private String type;

	private ChildElementTypeEnum(String type) {

		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
