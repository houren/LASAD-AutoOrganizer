package lasad.shared.dfki.meta.agents.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class PriorityDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1489145126896822421L;
	public static final int DEFAULT_REPRESENTATIVENESS = 1;
	public static final int DEFAULT_PRIORITY = 1;

	private Integer defaultPriority = DEFAULT_REPRESENTATIVENESS;
	private Integer defaultRepresentativeness = DEFAULT_PRIORITY;
	private Map<String, Integer> phase2Priority = new HashMap<String, Integer>();
	private Map<String, Integer> phase2Representativeness = new HashMap<String, Integer>();

	
	
	public PriorityDef() {
	}

	public Integer getDefaultPriority() {
		return defaultPriority;
	}

	public void setDefaultPriority(Integer defaultPriority) {
		this.defaultPriority = defaultPriority;
	}

	public Integer getDefaultRepresentativeness() {
		return defaultRepresentativeness;
	}

	public void setDefaultRepresentativeness(Integer defaultRepresentativeness) {
		this.defaultRepresentativeness = defaultRepresentativeness;
	}

	public void addPhasePriority(String phaseID, Integer value) {
		phase2Priority.put(phaseID, value);
	}
	public void removePhasePriority(String phaseID) {
		phase2Priority.remove(phaseID);
	}
	public void editPhasePriority(String phaseID, Integer value) {
		removePhasePriority(phaseID);
		addPhasePriority(phaseID, value);
	}

	public void addPhaseRepresentativeness(String phaseID, Integer value) {
		phase2Representativeness.put(phaseID, value);
	}

	public Map<String, Integer> getPhase2Priority() {
		return phase2Priority;
	}

	public Map<String, Integer> getPhase2Representativeness() {
		return phase2Representativeness;
	}

	/**
	 * 
	 * @param phaseID
	 * @return priority value of given phase, if not defined: default value
	 */
	public Integer getPriority(String phaseID) {
		Integer priority = phase2Priority.get(phaseID);
		if (priority != null) {
			return priority;
		}
		return defaultPriority;
	}

	/**
	 * 
	 * @param phaseID
	 * @return representativeness value of given phase, if not defined: default
	 *         value
	 */
	public Integer getRepresentativeness(String phaseID) {
		Integer representativeness = phase2Representativeness.get(phaseID);
		if (representativeness != null) {
			return representativeness;
		}
		return defaultRepresentativeness;
	}

	@Override
	public String toString() {
		return "PriorityDef [defaultPriority=" + defaultPriority
				+ ", defaultRepresentativeness=" + defaultRepresentativeness
				+ ", phase2Priority=" + phase2Priority
				+ ", phase2Representativeness=" + phase2Representativeness
				+ "]";
	}

}
