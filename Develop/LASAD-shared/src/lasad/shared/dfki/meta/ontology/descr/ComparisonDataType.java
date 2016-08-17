package lasad.shared.dfki.meta.ontology.descr;

/**
 * Data type that determines which comparison operators can be used for a given
 * {@link PropDescr} (e.g., comparing a {@link #STRING} property to a
 * {@link #SET} property enables operators such as 'element of' and 'not element
 * of').
 * 
 * <br/>
 * <br/>
 * <b>NOTE:</b> Might differ from the {@link JessDataType}, which describes how
 * properties are stored in the Jess working memory.
 * 
 * @author oliverscheuer
 * 
 */
public enum ComparisonDataType {

	/**
	 * will be treated as a string when comparing to other values
	 */
	STRING("string"),

	/**
	 * will be treated as a number when comparing to other values
	 */
	NUMBER("number"),

	/**
	 * will be treated as a set when comparing to other values
	 */
	SET("set");

	//private final String dataType;

	ComparisonDataType(String dataType) {
		//this.dataType = dataType;
	}
}
