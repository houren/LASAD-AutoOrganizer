package lasad.shared.dfki.meta.agents.analysis.phases;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class PhasesDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8091875425149006375L;
	protected List<PhaseDef> phases = new Vector<PhaseDef>();
	
	public PhasesDef(){
		
	}

	public PhasesDef(List<PhaseDef> phases) {
		this.phases = phases;
	}

	public List<PhaseDef> getPhases() {
		return phases;
	}

	public void setPhaseIDs(List<PhaseDef> phases) {
		this.phases = phases;
	}

	@Override
	public String toString() {
		return "PhasesDef [phases=" + phases + "]";
	}
}
