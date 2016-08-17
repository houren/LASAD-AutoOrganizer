package lasad.shared.dfki.meta.agents.analysis.counter;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Defines a counter, i.e., specific conditions that nodes, links or patterns
 * must fulfill to be counted.
 * 
 * <br/>
 * <br/>
 * Conditions include: the general type of relevant instances (node, link or
 * pattern; {@link #objectTypeGeneral}), the specific types (e.g., specific node
 * types; {@link #objectTypesSpecific}), the instance's age (i.e., when the
 * instance has been touched last time; {@link #minAgeInSecs} and {@link #maxAgeInSecs}),
 * which user has interacted and in which way with the instance (e.g., count
 * only objects one specific user has contributed to, count objects no matter
 * which user has interacted with; {@link #userSelectionCriterion},
 * {@link #userRoleCriterion} and {@link #userID}).
 * 
 * @author oliverscheuer
 * 
 */
public class CounterDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5626434704252745340L;
	private UserSelectionSetting userSelectionCriterion; //Not used
	private UserRoleSetting userRoleCriterion; //Not used
	private String userID; //Not used

	private InstanceTypeGeneral objectTypeGeneral;

	// null == ANY or size == 0
	private List<InstanceTypeSpecific> objectTypesSpecific = new Vector<InstanceTypeSpecific>();

	// null == NONE
	private Integer minAgeInSecs; //Not used

	// null == NONE
	private Integer maxAgeInSecs; //Not used

	public CounterDef(){
		
	}
	/*
	 * TODO seems that this constructor is not valid anymore
	 */
	public CounterDef(UserSelectionSetting userSelectionCriterion,
			String userID, UserRoleSetting userRoleCriterion,
			InstanceTypeGeneral objectTypeGeneral,
			List<InstanceTypeSpecific> objectTypesSpecific, Integer minAgeInSecs,
			Integer maxAgeInSecs) {
		this.userSelectionCriterion = userSelectionCriterion;
		this.userID = userID;
		this.userRoleCriterion = userRoleCriterion;
		this.objectTypeGeneral = objectTypeGeneral;
		this.objectTypesSpecific = objectTypesSpecific;
		this.minAgeInSecs = minAgeInSecs;
		this.maxAgeInSecs = maxAgeInSecs;
	}
	
	public CounterDef(InstanceTypeGeneral objectTypeGeneral, List<InstanceTypeSpecific> objectTypesSpecific) {
		this.objectTypeGeneral = objectTypeGeneral;
		this.objectTypesSpecific = objectTypesSpecific;
	}
	

	public UserSelectionSetting getUserSelectionSetting() {
		return userSelectionCriterion;
	}

	public UserRoleSetting getUserRoleSetting() {
		return userRoleCriterion;
	}

	public String getUserID() {
		return userID;
	}

	public InstanceTypeGeneral getInstanceTypeGeneral() {
		return objectTypeGeneral;
	}

	public List<InstanceTypeSpecific> getInstanceTypesSpecific() {
		return objectTypesSpecific;
	}
	
	public void setInstanceTypesSpecific(List<InstanceTypeSpecific> objectTypesSpecific) {
		this.objectTypesSpecific = objectTypesSpecific;
	}

	public Integer getMinAgeInSecs() {
		return minAgeInSecs;
	}

	public Integer getMaxAgeInSecs() {
		return maxAgeInSecs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxAgeInSecs == null) ? 0 : maxAgeInSecs.hashCode());
		result = prime * result + ((minAgeInSecs == null) ? 0 : minAgeInSecs.hashCode());
		result = prime
				* result
				+ ((objectTypeGeneral == null) ? 0 : objectTypeGeneral
						.hashCode());
		result = prime
				* result
				+ ((objectTypesSpecific == null) ? 0 : objectTypesSpecific
						.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		result = prime
				* result
				+ ((userRoleCriterion == null) ? 0 : userRoleCriterion
						.hashCode());
		result = prime
				* result
				+ ((userSelectionCriterion == null) ? 0
						: userSelectionCriterion.hashCode());
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
		CounterDef other = (CounterDef) obj;
		if (maxAgeInSecs == null) {
			if (other.maxAgeInSecs != null)
				return false;
		} else if (!maxAgeInSecs.equals(other.maxAgeInSecs))
			return false;
		if (minAgeInSecs == null) {
			if (other.minAgeInSecs != null)
				return false;
		} else if (!minAgeInSecs.equals(other.minAgeInSecs))
			return false;
		if (objectTypeGeneral != other.objectTypeGeneral)
			return false;
		if (objectTypesSpecific == null) {
			if (other.objectTypesSpecific != null)
				return false;
		} else if (!objectTypesSpecific.equals(other.objectTypesSpecific))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		if (userRoleCriterion != other.userRoleCriterion)
			return false;
		if (userSelectionCriterion != other.userSelectionCriterion)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CounterDefinition [userSelectionCriterion="
				+ userSelectionCriterion + ", userRoleCriterion="
				+ userRoleCriterion + ", userID=" + userID
				+ ", objectTypeGeneral=" + objectTypeGeneral
				+ ", objectTypesSpecific=" + objectTypesSpecific + ", minAgeInSecs="
				+ minAgeInSecs + ", maxAgeInSecs=" + maxAgeInSecs + "]";
	}

}
