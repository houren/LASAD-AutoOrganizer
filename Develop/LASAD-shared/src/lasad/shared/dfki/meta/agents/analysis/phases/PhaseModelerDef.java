package lasad.shared.dfki.meta.agents.analysis.phases;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
//public abstract class PhaseModelerDef implements Serializable {
public class PhaseModelerDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3831652941408994137L;
	protected PhasesDef phaseDef = null;
	
	public PhaseModelerDef(){
		
	}

	public PhaseModelerDef(PhasesDef phaseDef) {
		this.phaseDef = phaseDef;
	}

	public PhasesDef getPhaseDef() {
		return phaseDef;
	}

	public void setPhaseDef(PhasesDef phaseDef) {
		this.phaseDef = phaseDef;
	}

	@Override
	public String toString() {
		return "PhaseModelerDef [phaseDef=" + phaseDef + "]";
	}

}
