package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class StructuralPatternSuppl implements Serializable {

	private static final long serialVersionUID = -2640854806268543719L;
	private Map<Integer, List<String>> constantNumID2Values = new HashMap<Integer, List<String>>();

	public Map<Integer, List<String>> getConstantNumID2Values() {
		return constantNumID2Values;
	}

	public void setConstantNumID2Values(
			Map<Integer, List<String>> constantNumID2Values) {
		this.constantNumID2Values = constantNumID2Values;
	}

	@Override
	public String toString() {
		return "StructuralPatternSuppl [constantNumID2Values="
				+ constantNumID2Values + "]";
	}

}
