package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * Supplemental data only available in the back-end after pre-processing
 * 
 * @author oliverscheuer
 * 
 */
public class ElementVariableSuppl implements Serializable {

	private static final long serialVersionUID = -2153567670323398959L;
	private Map<PropDescr, List<String>> prop2CompsUsedAsReferenceValues = new HashMap<PropDescr, List<String>>();
	private Map<Bin, Map<ElementVariableProp, List<Comparison>>> bin2propVars2Comparisons = new HashMap<Bin, Map<ElementVariableProp, List<Comparison>>>();
	private NeighborConstrs neighborConstrs = new NeighborConstrs();

	public Map<PropDescr, List<String>> getProp2CompsUsedAsReferenceValues() {
		return prop2CompsUsedAsReferenceValues;
	}

	public void setProp2CompsUsedAsReferenceValues(
			Map<PropDescr, List<String>> prop2CompsUsedAsReferenceValues) {
		this.prop2CompsUsedAsReferenceValues = prop2CompsUsedAsReferenceValues;
	}

	public Map<Bin, Map<ElementVariableProp, List<Comparison>>> getBin2propVars2Comparisons() {
		return bin2propVars2Comparisons;
	}

	public void setBin2propVars2Comparisons(
			Map<Bin, Map<ElementVariableProp, List<Comparison>>> bin2propVars2Comparisons) {
		this.bin2propVars2Comparisons = bin2propVars2Comparisons;
	}

	public List<PropDescr> getPropsUsedAsRefVal() {
		return new Vector<PropDescr>(prop2CompsUsedAsReferenceValues.keySet());
	}

	public boolean isPropUsedAsRefValue(ElementVariableProp varProp) {
		String propID = varProp.getPropId();
		String compID = varProp.getCompID();

		for (PropDescr prop : prop2CompsUsedAsReferenceValues.keySet()) {
			if (propID.equals(prop.getPropID())) {
				List<String> components = prop2CompsUsedAsReferenceValues
						.get(prop);
				if (components.contains(compID)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<String> getPropComponentsUsedAsRefVal(PropDescr prop) {
		return prop2CompsUsedAsReferenceValues.get(prop);
	}

	/**
	 * Determines all {@link Comparison}s, involving the given
	 * {@link ElementVariableProp} and classified into the given {@link Bin},
	 * that are used to constrain this {@link ElementVariable}.
	 * 
	 */
	public List<Comparison> getComparisons(Bin bin, ElementVariableProp propVar) {
		Map<ElementVariableProp, List<Comparison>> propVars2Comparisons = bin2propVars2Comparisons
				.get(bin);
		if (propVars2Comparisons == null) {
			return new Vector<Comparison>();
		}
		List<Comparison> comparisons = propVars2Comparisons.get(propVar);
		if (comparisons == null) {
			return new Vector<Comparison>();
		}
		return comparisons;
	}

	/**
	 * Determines all {@link Comparison}s, involving some component of the given
	 * {@link PropDescr} and classified into the given {@link Bin}, that are
	 * used to constrain this {@link ElementVariable}.
	 * 
	 */
	public List<Comparison> getComparisons(ElementVariable elemVar, Bin bin,
			PropDescr prop) {
		List<Comparison> allComparisons = new Vector<Comparison>();
		for (String compID : prop.getComponentIDs()) {
			ElementVariableProp propVar = elemVar.getPropVar(prop.getPropID(),
					compID);
			List<Comparison> comparisons = getComparisons(bin, propVar);
			allComparisons.addAll(comparisons);
		}
		return allComparisons;
	}

	public NeighborConstrs getNeighborConstrs() {
		return neighborConstrs;
	}

	public void setNeighborConstrs(NeighborConstrs neighborConstrs) {
		this.neighborConstrs = neighborConstrs;
	}

	@Override
	public String toString() {
		return "ElementVariableSuppl [prop2CompsUsedAsReferenceValues="
				+ prop2CompsUsedAsReferenceValues
				+ ", bin2propVars2Comparisons=" + bin2propVars2Comparisons
				+ "neighborConstrs=" + neighborConstrs + "]";
	}

}
