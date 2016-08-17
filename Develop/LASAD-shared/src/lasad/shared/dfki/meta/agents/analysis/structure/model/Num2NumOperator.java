package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public enum Num2NumOperator implements Operator {
	EQUAL("equal"), NOT_EQUAL("not-equal"), LESS("less"), LESS_OR_EQUAL(
			"less-or-equal"), GREATER("greater"), GREATER_OR_EQUAL(
			"greater-or-equal");

	private final String name;

	private static final Map<String, Num2NumOperator> map = new HashMap<String, Num2NumOperator>();

	static {
		for (Num2NumOperator type : Num2NumOperator.values()) {
			map.put(type.getName(), type);
		}
	}

	Num2NumOperator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Num2NumOperator fromString(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new NoSuchElementException(name + "not found");
	}

	public Num2NumOperator invert() {
		if (equals(EQUAL)) {
			return EQUAL;
		} else if (equals(NOT_EQUAL)) {
			return NOT_EQUAL;
		} else if (equals(LESS)) {
			return GREATER;
		} else if (equals(LESS_OR_EQUAL)) {
			return GREATER_OR_EQUAL;
		} else if (equals(GREATER)) {
			return LESS;
		} else if (equals(GREATER_OR_EQUAL)) {
			return LESS_OR_EQUAL;
		} else {
			// logger.error("Error in method invert()");
			System.err.println("Error in method invert()");
			return null;
		}
	}
}
