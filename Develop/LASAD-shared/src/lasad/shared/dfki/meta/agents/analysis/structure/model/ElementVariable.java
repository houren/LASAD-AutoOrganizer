package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.ElementDescr;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;

/**
 * Represents an element (i.e., node or link) within a {@link StructuralPattern}
 * . {@link ElementVariable}s are typically restricted in some way to match only
 * specific elements (see {@link ElementConstr}).
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public abstract class ElementVariable implements Serializable {

	private static final long serialVersionUID = -4617540888658132782L;

	private String debugInfo = null;

	private String varID;
	private String name; //used in Frontend
	private ElementVariableSuppl supplData = null;
	private Map<ParameterTypes, String> parameters = new HashMap<ParameterTypes, String>(); //x, y coordinates

	protected abstract ElementConstr getConstrs();

	/**
	 * Only for RMI serialization. DO NOT USE THIS CONSTRUCTOR!
	 */
	public ElementVariable() {
	}

	public ElementVariable(String varID) {
		this.varID = varID;
	}

	public String getVarID() {
		return varID;
	}

	public void addPropConstrs(List<PropConstr> propConstrs) {
		getConstrs().addPropConstrs(propConstrs);
	}

	public List<Comparison> getComparisons() {
		return getConstrs().getComparisons();
	}

	public void addComparisonIfNotExists(Comparison comparison) {
		getConstrs().addComparisonIfNotExists(comparison);
	}

	public void removeComparisonIfExists(Comparison comparison) {
		getConstrs().removeComparisonIfExists(comparison);
	}

	public List<Comparison> getComparisonsInvalidatedOnTypeChange(
			Ontology ontology, ElementVariable changedElemVar) {
		return getConstrs().getComparisonsInvalidatedOnTypeChange(ontology,
				changedElemVar);
	}

	public List<Comparison> getComparisonsInvalidatedOnElemVarRemoval(
			ElementVariable deletedElemVar) {
		return getConstrs().getComparisonsInvalidatedOnElemVarRemoval(
				deletedElemVar);
	}

	/**
	 * see comments at {@link ElementConstr#getPossibleTypes(Ontology)}
	 */
	public List<ElementDescr> getPossibleTypes(Ontology ontology) {
		return getConstrs().getPossibleTypes(ontology);
	}

	public List<StandardPropDescr> getStandardPropDescrs() {
		return Ontology.getStandardElemProperties();
	}

	public List<NonStandardPropDescr> getNonStandardPropDescrs(Ontology ontology) {
		return getConstrs().getNonStandardPropDescrs(ontology);
	}

	public List<PropDescr> getPropDescrs(Ontology ontology) {
		return getConstrs().getPropDescrs(ontology);
	}

	public PropDescr getPropDescr(Ontology ontology, String propID) {
		return getConstrs().getPropDescr(ontology, propID);
	}

	public List<PropConstr> getPropConstrs() {
		return getConstrs().getPropConstrs();
	}

	public ElementVariableProp getPropVar(String propID, String componentID) {
		return new ElementVariableProp(this, propID, componentID);
	}

	public ElementVariableSuppl getSupplData() {
		return supplData;
	}

	public void setSupplData(ElementVariableSuppl supplData) {
		this.supplData = supplData;
	}
	
	public void addParameter(ParameterTypes parameterType, String value){
		parameters.put(parameterType, value);
	}
	public String getParameter(ParameterTypes parameterType){
		return parameters.get(parameterType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((varID == null) ? 0 : varID.hashCode());
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
		ElementVariable other = (ElementVariable) obj;
		if (varID == null) {
			if (other.varID != null)
				return false;
		} else if (!varID.equals(other.varID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ElementVariable [varID=" + varID + ", supplData=" + supplData
				+ "]";
	}

	public String getDebugInfo() {
		return debugInfo;
	}

	public void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
