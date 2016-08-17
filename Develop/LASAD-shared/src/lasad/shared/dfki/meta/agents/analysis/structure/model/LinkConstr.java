package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;

/**
 * (see {@link ElementConstr})
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class LinkConstr extends ElementConstr implements Serializable {

	private static final long serialVersionUID = 5625910740089857651L;

	private ElementVariable source;
	private ElementVariable target;
	private boolean directionMatters;

	public ElementVariable getSource() {
		return source;
	}

	public void setSource(ElementVariable source) {
		this.source = source;
	}

	public ElementVariable getTarget() {
		return target;
	}

	public void setTarget(ElementVariable target) {
		this.target = target;
	}

	public boolean getDirectionMatters() {
		return directionMatters;
	}

	public void setDirectionMatters(boolean directionMatters) {
		this.directionMatters = directionMatters;
	}

	@Override
	public String toString() {
		return "LinkConstr [source=" + source.getVarID() + ", target="
				+ target.getVarID() + ", directionMatters="
				+ directionMatters + "], " + super.toString();
	}

}
