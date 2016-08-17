package lasad.entity.helper;

public class Contribution {

	private String name, type;
	
	public Contribution(String type, String name) {
		this.type = type;
		this.name = name;
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

	

}
