package lasad.shared.dfki.meta.agents.analysis.counter;

/**
 * 
 * @author oliverscheuer
 * 
 */
public enum UserSelectionSetting {

	/**
	 * Count links / nodes / patterns of one specific user only
	 */
	ONE,

	/**
	 * Count links / nodes / patterns across all users
	 */
	NONE,

	/**
	 * Control flag that triggers the creation of user-specific counters for
	 * user (which will use {@link #UserSelectionCriterion.ONE}).
	 */
	FOREACH;

}
