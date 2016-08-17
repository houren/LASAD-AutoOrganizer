package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public enum String2StringOperator implements Operator {
	EQUAL("equal"), NOT_EQUAL("not-equal");

	private final String name;

	private static final Map<String, String2StringOperator> map = new HashMap<String, String2StringOperator>();

	static {
		for (String2StringOperator type : String2StringOperator.values()) {
			map.put(type.getName(), type);
		}
	}

	String2StringOperator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static String2StringOperator fromString(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new NoSuchElementException(name + "not found");
	}

	public String2StringOperator invert() {
		if (equals(EQUAL)) {
			return EQUAL;
		} else if (equals(NOT_EQUAL)) {
			return NOT_EQUAL;
		} else {
			System.err.println("Error in method invert()");
			return null;
		}
	}
}
