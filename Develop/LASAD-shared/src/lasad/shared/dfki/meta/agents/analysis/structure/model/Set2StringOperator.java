package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public enum Set2StringOperator implements Operator {
	CONTAINS("contains"), NOT_CONTAINS("not-contains");

	private final String name;

	private static final Map<String, Set2StringOperator> map = new HashMap<String, Set2StringOperator>();

	static {
		for (Set2StringOperator type : Set2StringOperator.values()) {
			map.put(type.getName(), type);
		}
	}

	Set2StringOperator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Set2StringOperator fromString(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new NoSuchElementException(name + "not found");
	}

	public String2SetOperator invert() {
		if (equals(CONTAINS)) {
			return String2SetOperator.IN;
		} else if (equals(NOT_CONTAINS)) {
			return String2SetOperator.NOT_IN;
		} else {
			System.err.println("Error in method invert()");
			return null;
		}
	}
}
