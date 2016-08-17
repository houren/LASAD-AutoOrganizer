package lasad.shared.dfki.meta.agents.action.feedback;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
public abstract class PatternFilterDef implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3348312711618985266L;

	public boolean isUserSpecific() {
		return false;
	}
}
