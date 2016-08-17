package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Computes graph components (i.e., maximally connected sub-graphs) of a given
 * {@link StructuralPattern} (depth-first search).
 * 
 * @author oliverscheuer
 * 
 */
public class StructuralPatternGraphComponentCalculator implements Serializable {

	private static final long serialVersionUID = -874363820795046073L;
	private List<NodeVariable> nodes;
	private List<LinkVariable> links;

	private Set<ElementVariable> processedElements = new HashSet<ElementVariable>();
	private List<Set<ElementVariable>> graphComponents = new Vector<Set<ElementVariable>>();

	/*
	 * DON'T USE THIS ONE, IT'S ONLY FOR RMI
	 * */
	public StructuralPatternGraphComponentCalculator(){
		
	}
	
	public StructuralPatternGraphComponentCalculator(List<NodeVariable> nodes,
			List<LinkVariable> links) {
		this.nodes = nodes;
		this.links = links;
		computeGraphComponents();
	}

	public List<Set<ElementVariable>> getGraphComponents() {
		return graphComponents;
	}

	private void computeGraphComponents() {

		for (NodeVariable nodeVar : nodes) {
			Set<ElementVariable> component = new HashSet<ElementVariable>();
			processElem(nodeVar, component);
			if (component.size() > 0) {
				graphComponents.add(component);
			}
		}
		for (LinkVariable linkVar : links) {
			Set<ElementVariable> component = new HashSet<ElementVariable>();
			processElem(linkVar, component);
			if (component.size() > 0) {
				graphComponents.add(component);
			}
		}
	}

	private void processElem(ElementVariable elemVar,
			Set<ElementVariable> component) {

		boolean notYetProcessed = !processedElements.contains(elemVar);
		if (notYetProcessed) {
			component.add(elemVar);
			if (elemVar instanceof LinkVariable) {
				LinkVariable linkVar = (LinkVariable) elemVar;
				processElem(linkVar.getSource(), component);
				processElem(linkVar.getTarget(), component);
			}
			for (LinkVariable connection : getConnectingLinks(elemVar)) {
				processElem(connection, component);
			}
		}
	}

	private List<LinkVariable> getConnectingLinks(ElementVariable elemVar) {
		List<LinkVariable> connectingLinks = new Vector<LinkVariable>();

		for (LinkVariable linkVar : links) {
			if (linkVar.getSource().equals(elemVar)
					|| linkVar.getTarget().equals(elemVar)) {
				connectingLinks.add(linkVar);
			}
		}

		return connectingLinks;
	}
}
