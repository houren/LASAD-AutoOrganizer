package lasad.shared.dfki.meta.agents.analysis.counter;

/**
 * Role a user should have in relation to an instance to make this instance
 * relevant for a counter.
 * 
 * @author oliverscheuer
 * 
 */
public enum UserRoleSetting {

	/**
	 * any user matches
	 */
	NONE,

	/**
	 * user is the one and only contributor (i.e., creator and modifier) of a
	 * node, link or pattern.
	 */
	OWNER,

	/**
	 * user is one of the contributors (i.e., creator and modifier) of a node,
	 * link or pattern.
	 */
	CONTRIBUTOR,

	/**
	 * user did not contribute (i.e., create and modify) to a node, link or
	 * pattern.
	 */
	NON_CONTRIBUTOR;
}
