package lasad.shared.dfki.meta.agents.analysis.structure;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class StructureAnalysisType extends AnalysisType {

	private static final long serialVersionUID = -3337504199674867730L;
	private StructuralPattern pattern;
	private String ontologyID = null;

	/*
	 * DON'T USE THIS ONE, IT'S ONLY FOR RMI
	 */
	public StructureAnalysisType() {

	}

	public StructureAnalysisType(ServiceID id) {
		super(AnalysisResultDatatype.object_binary_result, id);
	}

	public StructureAnalysisType(ServiceID id, StructuralPattern pattern) {
		this(id);
		this.pattern = pattern;
	}

	public StructuralPattern getPattern() {
		return pattern;
	}

	public void setPattern(StructuralPattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * 
	 * @return ID of ontology this pattern depends on or <code>null</code> of no
	 *         such dependency exists
	 */
	public String getOntologyID() {
		return ontologyID;
	}

	public void setOntologyID(String ontologyID) {
		this.ontologyID = ontologyID;
	}

}
