package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ElementVariableManager implements Serializable {

	private static final long serialVersionUID = -7429796113680236845L;
	private int seqNum = 0;
	private Map<String, NodeVariable> id2nodeVar = new HashMap<String, NodeVariable>();
	private Map<String, LinkVariable> id2linkVar = new HashMap<String, LinkVariable>();

	public NodeVariable createNodeVar() {
		String ID = "n-" + seqNum++;
		NodeVariable nodeVar = new NodeVariable(ID);
		id2nodeVar.put(ID, nodeVar);
		return nodeVar;
	}

	public LinkVariable createLinkVar() {
		String ID = "l-" + seqNum++;
		LinkVariable linkVar = new LinkVariable(ID);
		id2linkVar.put(ID, linkVar);
		return linkVar;
	}

	public NodeVariable createNodeVar(String ID) {
		NodeVariable nodeVar = new NodeVariable(ID);
		id2nodeVar.put(ID, nodeVar);
		return nodeVar;
	}

	public LinkVariable createLinkVar(String ID) {
		LinkVariable linkVar = new LinkVariable(ID);
		id2linkVar.put(ID, linkVar);
		return linkVar;
	}

	public ElementVariable getElemVar(String ID) {
		NodeVariable nodeVar = getNodeVar(ID);
		if (nodeVar != null) {
			return nodeVar;
		}
		return getLinkVar(ID);
	}

	public NodeVariable getNodeVar(String ID) {
		NodeVariable nodeVar = id2nodeVar.get(ID);
		return nodeVar;
	}

	public LinkVariable getLinkVar(String ID) {
		LinkVariable linkVar = id2linkVar.get(ID);
		return linkVar;
	}
}
