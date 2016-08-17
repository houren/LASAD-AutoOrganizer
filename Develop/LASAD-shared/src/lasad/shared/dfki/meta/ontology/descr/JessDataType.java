package lasad.shared.dfki.meta.ontology.descr;

/**
 * Specification how properties (or their components) are represented in Jess.
 * 
 * <br/>
 * <br/>
 * <b>NOTE:</b> Might differ from the {@link ComparisonDataType}, which
 * determines the comparison operators that can be used.
 * 
 * @author oliverscheuer
 * 
 */
public enum JessDataType {

	/**
	 * Jess string slot
	 */
	STRING("STRING"),

	/**
	 * Jess number slot
	 */
	NUMBER("NUMBER"),

	/**
	 * Jess multislot
	 */
	LIST("LIST");

	//private final String type;

	JessDataType(String type) {
		//this.type = type;
	}
}
