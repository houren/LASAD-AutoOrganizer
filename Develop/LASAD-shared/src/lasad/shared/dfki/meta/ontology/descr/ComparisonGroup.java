package lasad.shared.dfki.meta.ontology.descr;

/**
 * Logical grouping to enable / disable certain comparisons (e.g., to disable a
 * comparison between a {@link #USER} property and and an element {@link #TYPE}
 * property).
 * 
 * @author oliverscheuer
 * 
 */
public enum ComparisonGroup {

	/**
	 * disable all comparisons
	 */
	NONE("none"),

	/**
	 * only allow comparisons with other 'user' properties
	 */
	USER("user"),

	/**
	 * only allow comparisons with other 'timestamp' properties
	 */
	TS("timestamp"),

	/**
	 * only allow comparisons with other 'element type' properties
	 */
	TYPE("type"),

	/**
	 * only allow comparisons with other 'text' properties
	 */
	TEXT("text"),

	/**
	 * only allow comparisons with other 'number' properties
	 */
	NUMBER("number");

	//private final String group;

	ComparisonGroup(String group) {
		//this.group = group;
	}
}
