package lasad.gwt.client.model.pattern;

import lasad.gwt.client.model.GraphMapInfo;

/**
 * This class stores all the characteristics of the pattern workspace
 * @author Anahuac
 *
 */
public class PatternGraphMapInfo extends GraphMapInfo {

	private String faOntologyName, faXmlOntology, agentId;
	
	public PatternGraphMapInfo(String mapID) {
		super(mapID);
	}

	public String getFaOntologyName() {
		return faOntologyName;
	}

	public void setFaOntologyName(String faOntologyName) {
		this.faOntologyName = faOntologyName;
	}

	public String getFaXmlOntology() {
		return faXmlOntology;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setFaXmlOntology(String faXmlOntology) {
		this.faXmlOntology = faXmlOntology;
	}
}
