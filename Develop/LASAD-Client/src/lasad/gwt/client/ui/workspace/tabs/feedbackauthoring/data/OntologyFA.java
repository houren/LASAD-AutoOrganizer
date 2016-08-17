package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

import lasad.shared.dfki.meta.ontology.Ontology;

/**
 * Class to save an ontology in the frontend 
 * @author Anahuac
 *
 */
public class OntologyFA{
	
	private String name;
	private String xml;
	private Ontology ontology;

	public OntologyFA(){
		
	}

	public OntologyFA(String name) {
		super();
		this.name = name;
	}

	public OntologyFA(String name, String xml) {
		super();
		this.name = name;
		this.xml = xml;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}
	
}
