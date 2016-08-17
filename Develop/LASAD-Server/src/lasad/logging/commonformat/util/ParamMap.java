package lasad.logging.commonformat.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ParamMap {


	private Map<String, List<String>> paramName2ValueList = new HashMap<String, List<String>>();

	public void addParam(String paramName, String paramValue) {
		List<String> paramValues = paramName2ValueList.get(paramName
				.toUpperCase());
		if (paramValues == null) {
			paramValues = new Vector<String>();
			paramName2ValueList.put(paramName.toUpperCase(), paramValues);
		}
		paramValues.add(paramValue);
	}

	public List<String> getAllValues(String paramName) {
		return paramName2ValueList.get(paramName.toUpperCase());
	}

	public String getFirstValue(String paramName) {
		List<String> paramValues = paramName2ValueList.get(paramName
				.toUpperCase());
		if (paramValues == null) {
			return null;
		}
		return paramValues.get(0);
	}

	public List<String> remove(String paramName) {
		return paramName2ValueList.remove(paramName.toUpperCase());
	}

	public String removeAndGetFirst(String paramName) {
		List<String> paramValues = paramName2ValueList.remove(paramName
				.toUpperCase());
		if (paramValues == null) {
			return null;
		}
		return paramValues.get(0);
	}

	public boolean containsKey(String paramName) {
		return paramName2ValueList.containsKey(paramName.toUpperCase());
	}

	public Set<String> keySet() {
		return paramName2ValueList.keySet();
	}

}
