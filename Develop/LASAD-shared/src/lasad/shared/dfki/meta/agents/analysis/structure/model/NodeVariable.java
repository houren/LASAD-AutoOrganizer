package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.List;

/**
 * (see {@link ElementVariable})
 * 
 * @author Almer Bolatov
 * 
 */
public class NodeVariable extends ElementVariable {

	private static final long serialVersionUID = 2476332996723355839L;

	protected NodeConstr constrs = new NodeConstr();

	/**
	 * Only for RMI serialization. DO NOT USE THIS CONSTRUCTOR!
	 */
	public NodeVariable() {

	}

	public NodeVariable(String varID) {
		super(varID);
	}

	@Override
	public NodeConstr getConstrs() {
		return constrs;
	}

	@Override
	public String toString() {
		return "NodeVariable [constrs=" + constrs + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		String prefixL1 = prefix + "...";
		// String prefixL2 = prefixL1 + "...";

		buf.append(prefix + "[node] " + getVarID() + "\n");
		// buf.append(prefixL1 + "properties" + "\n");
		buf.append(prefixL1 + "[debug-info] " + getDebugInfo() + "\n");
		List<PropConstr> propConstrs = getConstrs().getPropConstrs();
		for (PropConstr propConstr : propConstrs) {
			buf.append(propConstr.toPrettyString(prefixL1));
		}
		return buf.toString();
	}

}
