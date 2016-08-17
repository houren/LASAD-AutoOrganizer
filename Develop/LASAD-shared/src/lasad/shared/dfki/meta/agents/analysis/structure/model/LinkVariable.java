package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.List;

/**
 * (see {@link ElementVariable})
 * 
 * @author Almer Bolatov
 * 
 */
public class LinkVariable extends ElementVariable {

	private static final long serialVersionUID = 2249966500017709578L;

	private LinkConstr constrs = new LinkConstr();

	/**
	 * Only for RMI serialization. DO NOT USE THIS CONSTRUCTOR!
	 */
	public LinkVariable() {

	}

	public LinkVariable(String varID) {
		super(varID);
	}

	@Override
	protected LinkConstr getConstrs() {
		return constrs;
	}

	public ElementVariable getSource() {
		return constrs.getSource();
	}

	public void setSource(ElementVariable source) {
		constrs.setSource(source);
	}

	public ElementVariable getTarget() {
		return constrs.getTarget();
	}

	public void setTarget(ElementVariable target) {
		constrs.setTarget(target);
	}

	public boolean getDirectionMatters() {
		return constrs.getDirectionMatters();
	}

	public void setDirectionMatters(boolean directionMatters) {
		constrs.setDirectionMatters(directionMatters);
	}

	@Override
	public String toString() {
		return "LinkVariable [constrs=" + constrs + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		String prefixL1 = prefix + "...";

		buf.append(prefix + "[link] " + getVarID() + "\n");

		ElementVariable source = getConstrs().getSource();
		ElementVariable target = getConstrs().getTarget();
		buf.append(prefixL1 + "[source] "
				+ (source != null ? source.getVarID() : "null") + "\n");
		buf.append(prefixL1 + "[target] "
				+ (target != null ? target.getVarID() : "null") + "\n");
		buf.append(prefixL1 + "[direction-matters] "
				+ getConstrs().getDirectionMatters() + "\n");
		buf.append(prefixL1 + "[debug-info] " + getDebugInfo() + "\n");
		// buf.append(prefix + "properties" + "\n");
		List<PropConstr> propConstrs = getConstrs().getPropConstrs();
		for (PropConstr propConstr : propConstrs) {
			buf.append(propConstr.toPrettyString(prefixL1));
		}
		return buf.toString();
	}

}
