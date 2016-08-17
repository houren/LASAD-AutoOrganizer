package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

/**
 * Class to save a pattern in the frontend
 * @author Anahuac
 *
 */
public class PatternFA {
	private int id;
	private String name;
	private String type;
	private String info;
	
	public PatternFA(int id, String name, String type, String info) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.info = info;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
	
	
}
