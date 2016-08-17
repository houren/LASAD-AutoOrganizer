package lasad.shared.dfki.meta.agents.action.feedback;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
public abstract class MsgCompDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3972611556749856154L;
	private boolean componentDefined = true;

	public boolean isComponentDefined() {
		return componentDefined;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (componentDefined ? 1231 : 1237);
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
		MsgCompDef other = (MsgCompDef) obj;
		if (componentDefined != other.componentDefined)
			return false;
		return true;
	}

}
