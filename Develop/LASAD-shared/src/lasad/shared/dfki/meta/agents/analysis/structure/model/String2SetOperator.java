package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public enum String2SetOperator implements Operator {
	IN("in"), NOT_IN("not-in");

	private final String name;

	private static final Map<String, String2SetOperator> map = new HashMap<String, String2SetOperator>();

	static {
		for (String2SetOperator type : String2SetOperator.values()) {
			map.put(type.getName(), type);
		}
	}

	String2SetOperator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static String2SetOperator fromString(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new NoSuchElementException(name + "not found");
	}

	public Set2StringOperator invert() {
		if (equals(IN)) {
			return Set2StringOperator.CONTAINS;
		} else if (equals(NOT_IN)) {
			return Set2StringOperator.NOT_CONTAINS;
		} else {
			System.err.println("Error in method invert()");
			return null;
		}
	}
}
