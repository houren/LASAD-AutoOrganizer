package lasad.shared.dfki.meta.agents.analysis.structure;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPatternGraphComponentCalculator;
import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

/**
 * Wrapper class providing operations to manipulate a
 * {@link StructureAnalysisType}, and to check for data within a
 * {@link StructureAnalysisType} that would be invalidated when specific
 * manipulations would be performed.
 * 
 * @author oliverscheuer
 * 
 */
public class StructuralAnalysisTypeManipulator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7497357875128910295L;
	private Ontology ontology;
	private StructureAnalysisType analysisType;
	private StructuralPattern pattern;

	private Map<String, ElementVariable> id2ElemVar = new HashMap<String, ElementVariable>();
	private Map<String, StructuralPattern> elemVarId2notPattern = new HashMap<String, StructuralPattern>();

	/*
	 * DON'T USE THIS ONE, IT'S ONLY FOR RMI
	 * */
	public StructuralAnalysisTypeManipulator(){
		
	}
	
	public StructuralAnalysisTypeManipulator(
			StructureAnalysisType analysisType, Ontology ontology) {
		this.ontology = ontology;
		this.analysisType = analysisType;
		this.pattern = analysisType.getPattern();

		for (ElementVariable elemVar : pattern.getNodeVars()) {
			id2ElemVar.put(elemVar.getVarID(), elemVar);
		}
		for (ElementVariable elemVar : pattern.getLinkVars()) {
			id2ElemVar.put(elemVar.getVarID(), elemVar);
		}
		for (StructuralPattern notPattern : pattern.getNotPatterns()) {
			for (ElementVariable elemVar : notPattern.getNodeVars()) {
				id2ElemVar.put(elemVar.getVarID(), elemVar);
				elemVarId2notPattern.put(elemVar.getVarID(), notPattern);
			}
			for (ElementVariable elemVar : notPattern.getLinkVars()) {
				id2ElemVar.put(elemVar.getVarID(), elemVar);
				elemVarId2notPattern.put(elemVar.getVarID(), notPattern);
			}
		}
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public void addNodeVar(NodeVariable nodeVar) {
		if (id2ElemVar.containsKey(nodeVar.getVarID())) {
			return;
		}
		id2ElemVar.put(nodeVar.getVarID(), nodeVar);
		pattern.getNodeVars().add(nodeVar);
	}

	public void addLinkVar(LinkVariable linkVar) {
		if (id2ElemVar.containsKey(linkVar.getVarID())) {
			return;
		}
		id2ElemVar.put(linkVar.getVarID(), linkVar);
		pattern.getLinkVars().add(linkVar);
	}

	public void addNotNodeVar(NodeVariable nodeVar) {
		if (elemVarId2notPattern.containsKey(nodeVar.getVarID())) {
			return;
		}
		StructuralPattern subPattern = pattern.createFreshSubPattern();
		subPattern.addNodeVar(nodeVar);
		pattern.addNotSubPattern(subPattern);
		elemVarId2notPattern.put(nodeVar.getVarID(), subPattern);
	}

	public void addNotLinkVar(LinkVariable linkVar) {
		if (elemVarId2notPattern.containsKey(linkVar.getVarID())) {
			return;
		}
		ElementVariable source = linkVar.getSource();
		ElementVariable target = linkVar.getTarget();

		StructuralPattern sourcePattern = elemVarId2notPattern.get(source
				.getVarID());
		StructuralPattern targetPattern = elemVarId2notPattern.get(target
				.getVarID());
		if (sourcePattern != null && targetPattern != null) {
			StructuralPattern mergedPattern = mergeNotSubPatterns(
					sourcePattern, targetPattern);
			mergedPattern.addLinkVar(linkVar);
			elemVarId2notPattern.put(source.getVarID(), mergedPattern);
			elemVarId2notPattern.put(target.getVarID(), mergedPattern);
			elemVarId2notPattern.put(linkVar.getVarID(), mergedPattern);
			pattern.removeNotSubPattern(sourcePattern);
			pattern.removeNotSubPattern(targetPattern);
			pattern.addNotSubPattern(mergedPattern);
		} else if (sourcePattern != null) {
			sourcePattern.addLinkVar(linkVar);
			elemVarId2notPattern.put(linkVar.getVarID(), sourcePattern);
		} else if (targetPattern != null) {
			targetPattern.addLinkVar(linkVar);
			elemVarId2notPattern.put(linkVar.getVarID(), targetPattern);
		} else {
			StructuralPattern newSubPattern = pattern.createFreshSubPattern();
			newSubPattern.addLinkVar(linkVar);
			pattern.addNotSubPattern(newSubPattern);
			elemVarId2notPattern.put(linkVar.getVarID(), newSubPattern);
		}
	}

	public void removeNodeVar(NodeVariable nodeVar) {
		id2ElemVar.remove(nodeVar.getVarID());
		pattern.removeNodeVar(nodeVar);
	}

	public void removeLinkVar(LinkVariable linkVar) {
		id2ElemVar.remove(linkVar.getVarID());
		pattern.removeLinkVar(linkVar);
	}

	public void removeNotNodeVar(NodeVariable nodeVar) {
		id2ElemVar.remove(nodeVar.getVarID());
		StructuralPattern subPattern = elemVarId2notPattern.remove(nodeVar
				.getVarID());
		if (subPattern != null) {
			subPattern.removeNodeVar(nodeVar);
			recomputeNotPatterns(subPattern);
		}
	}

	public void removeNotLinkVar(LinkVariable linkVar) {
		id2ElemVar.remove(linkVar.getVarID());
		StructuralPattern subPattern = elemVarId2notPattern.remove(linkVar
				.getVarID());
		if (subPattern != null) {
			subPattern.removeLinkVar(linkVar);
			recomputeNotPatterns(subPattern);
		}
	}

	public void addComparisonIfNotExists(Comparison comparison) {
		ElementVariableProp elemVarProp = comparison.getLeftExpr();
		ElementVariable elemVar = elemVarProp.getElementVar();
		elemVar.addComparisonIfNotExists(comparison);
	}

	public void removeComparisonIfExists(Comparison comparison) {
		ElementVariableProp elemVarProp = comparison.getLeftExpr();
		ElementVariable elemVar = elemVarProp.getElementVar();
		elemVar.removeComparisonIfExists(comparison);
	}

	public List<NodeVariable> getNodeVariables() {
		return pattern.getNodeVars();
	}

	public List<LinkVariable> getLinkVariables() {
		return pattern.getLinkVars();
	}

	public Collection<StructuralPattern> getNotPatterns() {
		return pattern.getNotPatterns();
	}

	public void setName(String name) {
		this.analysisType.setName(name);
	}

	public String getName() {
		return this.analysisType.getName();
	}

	public String getDescription() {
		return this.analysisType.getDescription();
	}

	public void setDescription(String description) {
		this.analysisType.setDescription(description);
	}

	/**
	 * Checks which existing {@link Comparison}s would be invalidated when the
	 * used {@link Ontology} would be replaced by the given new one.
	 * 
	 * @param newOntology
	 * @return
	 */
	public List<Comparison> getComparisonsInvalidatedOnOntologyChange(
			Ontology newOntology) {
		List<Comparison> invalidatedComparisons = new Vector<Comparison>();
		//TODO Incomplete
		return invalidatedComparisons;
	}

	/**
	 * Checks which existing {@link Comparison}s would be invalidated when the
	 * given {@link ElementVariable} would be removed from the
	 * {@link StructuralPattern}.
	 * 
	 * @param elemVarToRemove
	 * @return
	 */
	public List<Comparison> getComparisonsInvalidatedOnElemVarRemoval(
			ElementVariable elemVarToRemove) {
		List<Comparison> invalidatedComparisons = new Vector<Comparison>();
		for (ElementVariable elemVar : pattern.getNodeVars()) {
			List<Comparison> invalidatedNodeComparisons = elemVar
					.getComparisonsInvalidatedOnElemVarRemoval(elemVarToRemove);
			invalidatedComparisons.addAll(invalidatedNodeComparisons);
		}
		for (ElementVariable elemVar : pattern.getLinkVars()) {
			List<Comparison> invalidatedLinkComparisons = elemVar
					.getComparisonsInvalidatedOnElemVarRemoval(elemVarToRemove);
			invalidatedComparisons.addAll(invalidatedLinkComparisons);
		}
		for (StructuralPattern notPattern : pattern.getNotPatterns()) {
			for (ElementVariable elemVar : notPattern.getNodeVars()) {
				List<Comparison> invalidatedNodeComparisons = elemVar
						.getComparisonsInvalidatedOnElemVarRemoval(elemVarToRemove);
				invalidatedComparisons.addAll(invalidatedNodeComparisons);
			}
			for (ElementVariable elemVar : notPattern.getLinkVars()) {
				List<Comparison> invalidatedLinkComparisons = elemVar
						.getComparisonsInvalidatedOnElemVarRemoval(elemVarToRemove);
				invalidatedComparisons.addAll(invalidatedLinkComparisons);
			}
		}
		return invalidatedComparisons;
	}

	/**
	 * Checks which existing {@link Comparison}s would be invalidated when the
	 * given {@link Comparison} would be added to the {@link StructuralPattern}.
	 * 
	 * @param comparisonToAdd
	 * @return
	 */
	public List<Comparison> getComparisonsInvalidatedOnComparisonAddition(
			Comparison comparisonToAdd) {
		List<Comparison> result = new Vector<Comparison>();
		ElementVariableProp elemVarProp = comparisonToAdd.getLeftExpr();
		ElementVariable elemVar = elemVarProp.getElementVar();
		if (TypePropDescr.PROP_ID.equals(elemVarProp.getPropId())) {
			// adding comparison provisionally
			elemVar.addComparisonIfNotExists(comparisonToAdd);
			result = getComparisonsInvalidatedOnTypeComparisonChange(elemVar);
			elemVar.removeComparisonIfExists(comparisonToAdd);
		}
		return result;
	}

	/**
	 * Checks which existing {@link Comparison}s would be invalidated when the
	 * given {@link Comparison} would be removed from the
	 * {@link StructuralPattern}.
	 * 
	 * @param comparisonToRemove
	 * @return
	 */
	public List<Comparison> getComparisonsInvalidatedOnComparisonRemoval(
			Comparison comparisonToRemove) {
		List<Comparison> result = new Vector<Comparison>();
		ElementVariableProp elemVarProp = comparisonToRemove.getLeftExpr();
		ElementVariable elemVar = elemVarProp.getElementVar();
		if (TypePropDescr.PROP_ID.equals(elemVarProp.getPropId())) {
			// removing comparison provisionally
			elemVar.removeComparisonIfExists(comparisonToRemove);
			result = getComparisonsInvalidatedOnTypeComparisonChange(elemVar);
			elemVar.addComparisonIfNotExists(comparisonToRemove);
		}
		return result;
	}

	/**
	 * Checks which existing {@link Comparison}s would be invalidated when the
	 * given {@link ElementVariable} version would be used in the
	 * {@link StructuralPattern} rather than the existing version (which is
	 * possibly constrained by a different set of {@link Comparison}s).
	 * 
	 * @param comparisonToRemove
	 * @return
	 */
	private List<Comparison> getComparisonsInvalidatedOnTypeComparisonChange(
			ElementVariable elemVarWithChangedTypeConstrs) {
		List<Comparison> invalidatedComparisons = new Vector<Comparison>();
		for (ElementVariable elemVar : pattern.getNodeVars()) {
			List<Comparison> invalidatedNodeComparisons = elemVar
					.getComparisonsInvalidatedOnTypeChange(ontology,
							elemVarWithChangedTypeConstrs);
			invalidatedComparisons.addAll(invalidatedNodeComparisons);
		}
		for (ElementVariable elemVar : pattern.getLinkVars()) {
			List<Comparison> invalidatedLinkComparisons = elemVar
					.getComparisonsInvalidatedOnTypeChange(ontology,
							elemVarWithChangedTypeConstrs);
			invalidatedComparisons.addAll(invalidatedLinkComparisons);
		}
		for (StructuralPattern notPattern : pattern.getNotPatterns()) {
			for (ElementVariable elemVar : notPattern.getNodeVars()) {
				List<Comparison> invalidatedNodeComparisons = elemVar
						.getComparisonsInvalidatedOnTypeChange(ontology,
								elemVarWithChangedTypeConstrs);
				invalidatedComparisons.addAll(invalidatedNodeComparisons);
			}
			for (ElementVariable elemVar : notPattern.getLinkVars()) {
				List<Comparison> invalidatedLinkComparisons = elemVar
						.getComparisonsInvalidatedOnTypeChange(ontology,
								elemVarWithChangedTypeConstrs);
				invalidatedComparisons.addAll(invalidatedLinkComparisons);
			}
		}
		return invalidatedComparisons;
	}

	public boolean containsElemVar(ElementVariable elemVar) {
		return id2ElemVar.containsKey(elemVar.getVarID());
	}

	/**
	 * Returns a new {@link StructuralPattern}, which is a merged version of the
	 * two given {@link StructuralPattern}s (i.e., union of {@link NodeVariable}
	 * s and union of {@link LinkVariable}s).
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private StructuralPattern mergeNotSubPatterns(StructuralPattern p1,
			StructuralPattern p2) {
		StructuralPattern newSubPattern = pattern.createFreshSubPattern();
		for (NodeVariable nodeVar : p1.getNodeVars()) {
			newSubPattern.addNodeVar(nodeVar);
		}
		for (NodeVariable nodeVar : p2.getNodeVars()) {
			if (!newSubPattern.getNodeVars().contains(nodeVar)) {
				newSubPattern.addNodeVar(nodeVar);
			}
		}
		for (LinkVariable linkVar : p1.getLinkVars()) {
			newSubPattern.addLinkVar(linkVar);
		}
		for (LinkVariable linkVar : p2.getLinkVars()) {
			if (!newSubPattern.getLinkVars().contains(linkVar)) {
				newSubPattern.addLinkVar(linkVar);
			}
		}
		return newSubPattern;
	}

	/**
	 * Determines graph components (i.e., maximally connected sub-graphs) of
	 * given sub-pattern and replaces the original sub pattern by new ones based
	 * on the computed graph components.
	 * 
	 * @param changedNotPattern
	 */
	private void recomputeNotPatterns(StructuralPattern changedNotPattern) {
		pattern.removeNotSubPattern(changedNotPattern);
		List<StructuralPattern> replacementSubPatterns = splitNotSubPatternsIfNeeded(changedNotPattern);
		for (StructuralPattern replacementPattern : replacementSubPatterns) {
			pattern.addNotSubPattern(replacementPattern);
			for (ElementVariable elem : replacementPattern.getNodeVars()) {
				elemVarId2notPattern.put(elem.getVarID(), replacementPattern);
			}
			for (ElementVariable elem : replacementPattern.getLinkVars()) {
				elemVarId2notPattern.put(elem.getVarID(), replacementPattern);
			}
		}
	}

	/**
	 * 
	 * Determines graph components (i.e., maximally connected sub-graphs) of
	 * given {@link StructuralPattern}, constructs a new
	 * {@link StructuralPattern} for each component and returns these newly
	 * created {@link StructuralPattern}s.
	 * 
	 * @param subPattern
	 * @return
	 */
	private List<StructuralPattern> splitNotSubPatternsIfNeeded(
			StructuralPattern subPattern) {
		List<StructuralPattern> newSubPatterns = new Vector<StructuralPattern>();

		StructuralPatternGraphComponentCalculator calculator = new StructuralPatternGraphComponentCalculator(
				subPattern.getNodeVars(), subPattern.getLinkVars());
		List<Set<ElementVariable>> graphComponents = calculator
				.getGraphComponents();
		for (Set<ElementVariable> graphComponent : graphComponents) {
			StructuralPattern newSubPattern = pattern.createFreshSubPattern();
			for (ElementVariable compElem : graphComponent) {
				if (compElem instanceof NodeVariable) {
					newSubPattern.addNodeVar((NodeVariable) compElem);
				} else if (compElem instanceof LinkVariable) {
					newSubPattern.addLinkVar((LinkVariable) compElem);
				}
			}
			newSubPatterns.add(newSubPattern);
		}
		return newSubPatterns;
	}

}
