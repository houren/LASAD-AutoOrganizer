package lasad.shared.dfki.meta.agents.analysis.phases;

import lasad.shared.dfki.meta.agents.common.ActionListDef;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class PhaseModelerDef_Empirical extends PhaseModelerDef {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7800714324336864623L;
	private ActionListDef relevantActions = null;

	public PhaseModelerDef_Empirical(){
		
	}
	
	public PhaseModelerDef_Empirical(PhasesDef phasesDef,
			ActionListDef relevantActions) {
		super(phasesDef);
		this.relevantActions = relevantActions;
	}

	public ActionListDef getRelevantActions() {
		return relevantActions;
	}

	@Override
	public String toString() {
		return "PhaseModelerDef_Empirical [relevantActions=" + relevantActions
				+ ", phaseDef=" + phaseDef + "]";
	}

}
