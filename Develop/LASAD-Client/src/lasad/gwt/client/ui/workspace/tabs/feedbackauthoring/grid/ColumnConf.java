package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

/**
 * Class to define the characteristics of a {@link CustomizedGrid}'s column
 * @author Anahuac
 *
 */
public class ColumnConf {
	private String id;
	private String label;
	private int width;
	private int type;
	
	public ColumnConf(String id, String label, int width) {
		super();
		this.id = id;
		this.label = label;
		this.width = width;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
