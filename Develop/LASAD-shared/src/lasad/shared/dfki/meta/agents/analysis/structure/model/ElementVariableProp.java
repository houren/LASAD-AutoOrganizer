package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.List;

import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * Represents a property of an {@link ElementVariable}s within a
 * {@link StructuralPattern}. Used in {@link PropConstr}) to limit the set of
 * matched elements.
 * 
 * constructed on the fly, immutable, identical instances identifiable through
 * {@link #equals(Object)}.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class ElementVariableProp implements Serializable {

	private static final long serialVersionUID = 6707607110279721593L;

	private ElementVariable elementVar;
	private String propID;
	private String compID;

	/*
	 * DON'T USE THIS; IT'S ONLY FOR RMI
	 */
	public ElementVariableProp() {

	}

	public ElementVariableProp(ElementVariable elementVar, String propID,
			String compID) {
		this.elementVar = elementVar;
		this.propID = propID;
		this.compID = compID;
	}

	public ElementVariable getElementVar() {
		return elementVar;
	}

	public PropDescr getPropDescr(Ontology ontology) {
		List<PropDescr> propDescrCandidates = elementVar
				.getPropDescrs(ontology);
		for (PropDescr candidate : propDescrCandidates) {
			if (propID.equals(candidate.getPropID())) {
				return candidate;
			}
		}
		System.err
				.println("ElementVariableProp, getPropDescr(...): Property not defined in ontology ("
						+ propID + ").");
		return null;
	}

	public String getPropId() {
		return propID;
	}

	public String getCompID() {
		return compID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compID == null) ? 0 : compID.hashCode());
		result = prime * result
				+ ((elementVar == null) ? 0 : elementVar.hashCode());
		result = prime * result + ((propID == null) ? 0 : propID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementVariableProp other = (ElementVariableProp) obj;
		if (compID == null) {
			if (other.compID != null)
				return false;
		} else if (!compID.equals(other.compID))
			return false;
		if (elementVar == null) {
			if (other.elementVar != null)
				return false;
		} else if (!elementVar.equals(other.elementVar))
			return false;
		if (propID == null) {
			if (other.propID != null)
				return false;
		} else if (!propID.equals(other.propID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ElementVariableProp [elementVar=" + elementVar.getVarID()
				+ ", propID=" + propID + ", compID=" + compID + "]";
	}

	public String toCompactString() {
		StringBuffer buf = new StringBuffer();
		String compIDSuffix = PropDescr.DEFAULT_COMPONENT_ID.equals(compID) ? ""
				: "." + compID;
		buf.append(elementVar.getVarID() + "." + propID + compIDSuffix);
		return buf.toString();
	}
}
