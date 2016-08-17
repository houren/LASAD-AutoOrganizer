package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

/**
 * Class to save a session in the frontend
 * @author Anahuac
 *
 */
public class SessionFA{

	private String id;
	private String name;
	private String ontology;
	private String status;

	public SessionFA(){
		
	}

	public SessionFA(String id, String name, String ontology, String status) {
		super();
		this.id = id;
		this.name = name;
		this.ontology = ontology;
		this.status = status;
	}
	
	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatusLabel(){
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(getOntology());
		buf.append(", ");
		buf.append(getStatus());
		buf.append(")");
		return buf.toString();
	}

		
}
