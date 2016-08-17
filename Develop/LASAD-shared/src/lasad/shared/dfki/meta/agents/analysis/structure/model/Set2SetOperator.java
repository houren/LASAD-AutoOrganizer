package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public enum Set2SetOperator implements Operator {
	EQUAL("equal"), NOT_EQUAL("not-equal"), SUBSET("subset"), NOT_SUBSET(
			"not-subset"), SUPERSET("superset"), NOT_SUPERSET("not-superset"), INTERSECT(
			"intersect"), NOT_INTERSECT("not-intersect");

	private final String name;

	private static final Map<String, Set2SetOperator> map = new HashMap<String, Set2SetOperator>();

	static {
		for (Set2SetOperator type : Set2SetOperator.values()) {
			map.put(type.getName(), type);
		}
	}

	Set2SetOperator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Set2SetOperator fromString(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new NoSuchElementException(name + "not found");
	}

	public Set2SetOperator invert() {

		if (equals(EQUAL)) {
			return EQUAL;
		} else if (equals(NOT_EQUAL)) {
			return NOT_EQUAL;
		} else if (equals(SUBSET)) {
			return SUPERSET;
		} else if (equals(NOT_SUBSET)) {
			return NOT_SUPERSET;
		} else if (equals(SUPERSET)) {
			return SUBSET;
		} else if (equals(NOT_SUPERSET)) {
			return NOT_SUBSET;
		} else if (equals(INTERSECT)) {
			return INTERSECT;
		} else if (equals(NOT_INTERSECT)) {
			return NOT_INTERSECT;
		} else {
			System.err.println("Error in method invert()");
			return null;
		}
	}

	public static void main(String[] args) {
		Set2SetOperator op = SUBSET;
		System.out.println(op.invert());
	}
}
