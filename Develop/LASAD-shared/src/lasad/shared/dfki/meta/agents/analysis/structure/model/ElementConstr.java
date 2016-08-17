package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.ElementDescr;
import lasad.shared.dfki.meta.ontology.descr.LinkDescr;
import lasad.shared.dfki.meta.ontology.descr.NodeDescr;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public abstract class ElementConstr implements Serializable {

	private static final long serialVersionUID = -2607278529827031548L;

	private List<PropConstr> propConstrs = new Vector<PropConstr>();
	private Map<ElementVariableProp, PropConstr> varProp2constr = new HashMap<ElementVariableProp, PropConstr>();

	boolean uptodate = false;
	private List<ElementDescr> possibleElemTypes = new Vector<ElementDescr>();
	private List<NonStandardPropDescr> nonStdProps = new Vector<NonStandardPropDescr>();

	public List<PropConstr> getPropConstrs() {
		return propConstrs;
	}

	public List<Comparison> getComparisons() {
		List<Comparison> allComparisons = new Vector<Comparison>();
		for (PropConstr propConstr : propConstrs) {
			allComparisons.addAll(propConstr.getComparisons());
		}
		return allComparisons;
	}

	public void addPropConstrs(List<PropConstr> propConstrs) {
		for (PropConstr propConstr : propConstrs) {
			for (Comparison comparison : propConstr.getComparisons()) {
				addComparisonIfNotExists(comparison);
			}
		}
	}

	public void addComparisonIfNotExists(Comparison comparison) {
		ElementVariableProp varProp = comparison.getLeftExpr();
		PropConstr propConstr = varProp2constr.get(varProp);
		if (propConstr == null) {
			propConstr = new PropConstr();
			propConstr.setPropVar(varProp);
			propConstrs.add(propConstr);
			varProp2constr.put(varProp, propConstr);
		}
		boolean added = propConstr.addComparisonIfNotExists(comparison);
		if (added && TypePropDescr.PROP_ID.equals(varProp.getPropId())) {
			uptodate = false;
		}
	}

	public void removeComparisonIfExists(Comparison comparison) {
		ElementVariableProp varProp = comparison.getLeftExpr();
		PropConstr propConstr = varProp2constr.get(varProp);
		if (propConstr != null) {
			boolean removed = propConstr.removeComparisonIfExists(comparison);
			if (removed
					&& TypePropDescr.PROP_ID.equalsIgnoreCase(varProp
							.getPropId())) {
				uptodate = false;
			}
		}
	}

	/**
	 * Returns a list of all Ontology-based types an element variable can
	 * potentially have (see {link {@link #recomputePossibleTypes(Ontology)} ).
	 */
	public List<ElementDescr> getPossibleTypes(Ontology ontology) {
		if (!uptodate) {
			recomputeData(ontology);
		}
		return this.possibleElemTypes;
	}

	/**
	 * Returns list of {@link Comparison}s that are invalid, based on the type
	 * {@link Comparison}s of the given {@link ElementVariable} (i.e.,
	 * {@link Comparison}s that reference on their RHS some property of the
	 * given {@link ElementVariable} that is not available).
	 * 
	 * @param ontology
	 * @param changedElemVar
	 * @return
	 */
	public List<Comparison> getComparisonsInvalidatedOnTypeChange(
			Ontology ontology, ElementVariable changedElemVar) {
		List<PropDescr> availableProps = changedElemVar.getPropDescrs(ontology);

		List<Comparison> invalidatedComparisons = new Vector<Comparison>();
		for (Comparison comp : getComparisons()) {
			if (comp instanceof VariableComparison) {
				ElementVariableProp rightElemVarProp = ((VariableComparison) comp)
						.getRightExpr();
				ElementVariable rightVar = rightElemVarProp.getElementVar();
				if (changedElemVar.getVarID().equals(rightVar.getVarID())) {
					PropDescr rightPropDescr = rightElemVarProp
							.getPropDescr(ontology);
					if (!availableProps.contains(rightPropDescr)) {
						invalidatedComparisons.add(comp);
					}
				}
			}
		}
		return invalidatedComparisons;
	}

	public List<Comparison> getComparisonsInvalidatedOnElemVarRemoval(
			ElementVariable deletedElemVar) {
		List<Comparison> invalidatedComparisons = new Vector<Comparison>();
		for (Comparison comp : getComparisons()) {
			if (comp instanceof VariableComparison) {
				ElementVariableProp rightElemVarProp = ((VariableComparison) comp)
						.getRightExpr();
				ElementVariable rightVar = rightElemVarProp.getElementVar();
				if (deletedElemVar.getVarID().equals(rightVar.getVarID())) {
					invalidatedComparisons.add(comp);
				}
			}
		}
		return invalidatedComparisons;
	}

	public List<StandardPropDescr> getStandardProps() {
		return Ontology.getStandardElemProperties();
	}

/**
	 * Returns all {@link NonStandardPropDescr}s of the element, i.e., ones that
	 * are provided in all possible types (see
	 * {@link #recomputePossibleTypes(Ontology) and
	 * 
	 * @link #recomputeNonStdProps(Ontology)}).
	 * 
	 * @param ontology
	 * @return
	 */
	public List<NonStandardPropDescr> getNonStandardPropDescrs(Ontology ontology) {
		if (!uptodate) {
			recomputeData(ontology);
		}
		return nonStdProps;
	}

	/**
	 * Returns all {@link PropDescr}s of the element, including all
	 * {@link StandardPropDescr} and all {@link NonStandardPropDescr} (see also
	 * {@link #getNonStandardPropDescrs(Ontology)}).
	 * 
	 * @param ontology
	 * @return
	 */
	public List<PropDescr> getPropDescrs(Ontology ontology) {
		List<PropDescr> props = new Vector<PropDescr>();
		props.addAll(getStandardProps());
		List<NonStandardPropDescr> nonStdProps = getNonStandardPropDescrs(ontology);
		if (nonStdProps != null) {
			props.addAll(nonStdProps);
		}
		return props;
	}

	public PropDescr getPropDescr(Ontology ontology, String propID) {
		for (PropDescr prop : getPropDescrs(ontology)) {
			if (propID.equalsIgnoreCase(prop.getPropID())) {
				return prop;
			}
		}
		System.err.println("No property \'" + propID
				+ "' specified in the ontology");
		return null;
	}

	private void recomputeData(Ontology ontology) {
		recomputePossibleTypes(ontology);
		recomputeNonStdProps(ontology);
		uptodate = true;
	}

	private void recomputeNonStdProps(Ontology ontology) {

		if (this.possibleElemTypes == null) {
			return;
		}

		if (this.possibleElemTypes.isEmpty()) {
			return;
		}

		this.nonStdProps = new Vector<NonStandardPropDescr>();
		Iterator<ElementDescr> possibleTypeIter = this.possibleElemTypes
				.iterator();
		ElementDescr firstPossibleType = possibleTypeIter.next();
		nonStdProps.addAll(firstPossibleType.getNonStandardProps());
		while (possibleTypeIter.hasNext()) {
			ElementDescr nextPossibleType = possibleTypeIter.next();
			nonStdProps.retainAll(nextPossibleType.getNonStandardProps());
		}
	}

	private void recomputePossibleTypes(Ontology ontology) {
		if (ontology == null) {
			return;
		}

		boolean isNodeConstr = (this instanceof NodeConstr);
		Set<String> possibleTypeIDs = new HashSet<String>();

		// default: all typeIDs defined in ontology are possible
		if (isNodeConstr) {
			possibleTypeIDs.addAll(ontology.getNodeDescriptions().keySet());
		} else {
			possibleTypeIDs.addAll(ontology.getLinkDescriptions().keySet());
		}

		// sorting comparisons according to type comparators
		List<String2ConstStringComparison> equalityCmps = new Vector<String2ConstStringComparison>();
		List<String2ConstSetComparison> memberCmps = new Vector<String2ConstSetComparison>();
		List<String2ConstStringComparison> unequalityCmps = new Vector<String2ConstStringComparison>();
		List<String2ConstSetComparison> nonMemberCmps = new Vector<String2ConstSetComparison>();
		for (Comparison comp : getTypeComparisons()) {
			if (comp instanceof String2ConstStringComparison) {
				String2ConstStringComparison str2strComp = (String2ConstStringComparison) comp;
				String2StringOperator operator = str2strComp.getOperator();
				if (operator.equals(String2StringOperator.EQUAL)) {
					equalityCmps.add(str2strComp);
				} else if (operator.equals(String2StringOperator.NOT_EQUAL)) {
					unequalityCmps.add(str2strComp);
				}
			} else if (comp instanceof String2ConstSetComparison) {
				String2ConstSetComparison str2setComp = (String2ConstSetComparison) comp;
				String2SetOperator operator = str2setComp.getOperator();
				if (operator.equals(String2SetOperator.IN)) {
					memberCmps.add(str2setComp);
				} else if (operator.equals(String2SetOperator.NOT_IN)) {
					nonMemberCmps.add(str2setComp);
				}
			}
		}

		// if 'inclusive' constraints exist, compute the intersection and
		// re-initialize the set of possible typeIDs to that intersection
		if (!equalityCmps.isEmpty() || !memberCmps.isEmpty()) {
			possibleTypeIDs = getIntersection(equalityCmps, memberCmps);
		}

		// determine typeIDs to exclude and removes these typeIDs from set of
		// possible typeIDs
		Set<String> typesToRemove = getUnion(unequalityCmps, nonMemberCmps);
		possibleTypeIDs.removeAll(typesToRemove);

		this.possibleElemTypes = new Vector<ElementDescr>();
		for (String possibleTypeID : possibleTypeIDs) {
			if (isNodeConstr) {
				NodeDescr nodeDescr = ontology.getNodeDescriptions().get(
						possibleTypeID);
				if (nodeDescr != null) {
					possibleElemTypes.add(nodeDescr);
				} else {
					System.err.println("No node type \'" + possibleTypeID
							+ "' specified in the ontology");
				}
			} else {
				LinkDescr linkDescr = ontology.getLinkDescriptions().get(
						possibleTypeID);
				if (linkDescr != null) {
					possibleElemTypes.add(linkDescr);
				} else {
					System.err.println("No link type \'" + possibleTypeID
							+ "' specified in the ontology");
				}
			}
		}

	}

	private List<Comparison> getTypeComparisons() {
		List<Comparison> comparisons = new ArrayList<Comparison>();
		for (PropConstr pc : propConstrs) {
			String propId = pc.getPropVar().getPropId();
			if (TypePropDescr.PROP_ID.equals(propId)) {
				List<Comparison> typeComparisons = pc.getComparisons();
				comparisons.addAll(typeComparisons);
			}
		}
		return comparisons;
	}

	private Set<String> getIntersection(
			List<String2ConstStringComparison> equalityCmps,
			List<String2ConstSetComparison> memberCmps) {
		List<Set<String>> typeSets = new Vector<Set<String>>();

		for (String2ConstStringComparison eqCmp : equalityCmps) {
			Set<String> typeSet = new HashSet<String>();
			typeSet.add(eqCmp.getRightExpr());
			typeSets.add(typeSet);
		}

		for (String2ConstSetComparison memberCmp : memberCmps) {
			Set<String> typeSet = new HashSet<String>();
			typeSet.addAll(memberCmp.getRightExpr());
			typeSets.add(typeSet);
		}

		Iterator<Set<String>> typeSetIter = typeSets.iterator();
		Set<String> intersection = typeSetIter.next();
		while (typeSetIter.hasNext()) {
			Set<String> next = typeSetIter.next();
			intersection.retainAll(next);
		}

		return intersection;
	}

	private Set<String> getUnion(
			List<String2ConstStringComparison> unequalityCmps,
			List<String2ConstSetComparison> nonMemberCmps) {
		Set<String> union = new HashSet<String>();
		for (String2ConstStringComparison uneqCmp : unequalityCmps) {
			union.add(uneqCmp.getRightExpr());
		}

		for (String2ConstSetComparison nonMemberCmp : nonMemberCmps) {
			//Set<String> typeSet = new HashSet<String>();
			union.addAll(nonMemberCmp.getRightExpr());
		}
		return union;
	}

	@Override
	public String toString() {
		return "ElementConstr [propConstrs=" + propConstrs + ", uptodate="
				+ uptodate + ", possibleElemTypes=" + possibleElemTypes
				+ ", nonStdProps=" + nonStdProps + "]";
	}

}
