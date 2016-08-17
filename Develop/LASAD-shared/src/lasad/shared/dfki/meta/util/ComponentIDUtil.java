package lasad.shared.dfki.meta.util;

/**
 * Valid component-IDs underlie certain restrictions: (1) IDs should only
 * contain alpha-numeric characters, underscores ("_") and dashes ("-"), (2) IDs
 * must not start with a number, and (3) IDs should be unique. This class
 * provides services (a) to check whether a given ID satisfies condition (1) and
 * (2), and (b) to generate valid component IDs from component names, which can
 * be freely defined, under consideration of constraints (1) and (2). This class
 * does NOT test or ensure uniqueness.
 * 
 * <br/>
 * <br/>
 * The given constraints are derived from the Jess symbol syntax such that
 * component IDs can be used, for instance, as part of Jess rule names
 * 
 * @author oliverscheuer
 * 
 */
public class ComponentIDUtil {

	/**
	 * Checks component id for syntactic correctness (see general comments in
	 * class {@link ComponentIDUtil})
	 * 
	 * @param id
	 * @return
	 */
	public static boolean checkID(String id) {
		return (id.matches("^[a-zA-Z\\-\\_]+[a-zA-Z0-9\\-\\_]*$"));
	}

	/**
	 * Generates valid component id from component name (see general comments in
	 * class {@link ComponentIDUtil})
	 * 
	 * @param compName
	 * @return
	 */
	public static String generateIDFromName(String compName) {
		String alphaDigitsUnderscoreDashString = compName.replaceAll(
				"[^a-zA-Z0-9\\-\\_]+", "_");
		if (alphaDigitsUnderscoreDashString.matches("^[0-9].*")) {
			alphaDigitsUnderscoreDashString = "_"
					+ alphaDigitsUnderscoreDashString;
		}
		return alphaDigitsUnderscoreDashString;
	}

	/**
	 * Return error message string that indicates that the given id violates
	 * syntax constraints
	 * 
	 * @param id
	 * @return
	 */
	public static String getErrorMessageInvalidID(String id) {
		return "invalid component ID '" + id
				+ "'. Violates id syntax: ^[a-zA-Z\\-\\_]+[a-zA-Z0-9\\-\\_]*$";
	}
}
