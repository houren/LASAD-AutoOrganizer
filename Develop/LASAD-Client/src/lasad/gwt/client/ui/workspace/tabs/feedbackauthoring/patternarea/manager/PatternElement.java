package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager;

import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * Represents a pattern element (box, link, children)
 * @author Anahuac
 *
 */
public class PatternElement {
	private String id;
	private Vector<Parameter> parameters;
	
	public PatternElement(String id) {
		this.id = id;
		parameters = new Vector<Parameter>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Add parameter to action
	 * 
	 * @param name
	 * @param value
	 */
	public void addParameter(ParameterTypes type, String value) {
		if (type == null) {
			throw new NullPointerException();
		}
		parameters.add(new Parameter(type, value));
	}

	/**
	 * Add parameter vector to action
	 * 
	 * @param name
	 * @param values
	 */
	public void addParameterVector(ParameterTypes type, Vector<String> values) {
		for (String value : values) {
			addParameter(type, value);
		}
	}
	
	public boolean removeParameter(ParameterTypes type){
		Parameter toRemove = null;
		for (Parameter param : parameters) {
			if (param.getType().equals(type)){
				toRemove = param;
				break;
			}
		}
		if (toRemove != null) {
			return parameters.remove(toRemove);
		}
		return false;
	}
	public boolean removeParameters(ParameterTypes type){
		Vector<Parameter> toRemove = new Vector<Parameter>();
		for (Parameter param : parameters) {
			if (param.getType().equals(type)){
				toRemove.add(param);
			}
		}
		boolean flag = false;
		for (Parameter param : toRemove) {
			parameters.remove(param);
			flag = true;
		}
		return flag;
	}
	
	/**
	 * Get the first value of a parameter
	 * 
	 * @param name
	 * @return value
	 */
	public String getParameterValue(ParameterTypes type) {
		for (Parameter param : parameters) {
			if (param.getType() != null && param.getType().equals(type)) {
				return param.getValue();
			}
		}//TODO pruefen, ob das nicht einen Vector <String> zuruekliefern muesste... (siehe addParameterVector...)
		return null;
	}

	/**
	 * Get the values of a parameter vector
	 * 
	 * @param name
	 * @return value vector
	 */
	public Vector<String> getParameterValues(ParameterTypes type) {
		Vector<String> returnVector = new Vector<String>();

		for (Parameter param : parameters) {
			if (param.getType() != null && param.getType().equals(type)) {
				returnVector.add(param.getValue());
			}
		}
		if (returnVector.size() == 0)
			return null;

		return returnVector;
	}
	
	public Vector<ParameterTypes> getParameterTypes() {
		Vector<ParameterTypes> returnVector = new Vector<ParameterTypes>();
		
		for (Parameter param : parameters) {
			returnVector.add(param.getType());
		}

		return returnVector;
	}

	public boolean replaceParameter(ParameterTypes type, String newValue) {
		boolean retVal = false;
		for (Parameter param : parameters) {
			FATDebug.print(FATDebug.DEBUG, "Value: " + param.getValue() + " mit Typ: " + param.getType());
			if (param.getType() != null && param.getType().equals(type)) {
				param.setValue(newValue);
				retVal = true;
			}
		}
		return retVal;
	}
	@Override
	public String toString() {
		StringBuilder action = new StringBuilder();
		action.append("PatternElement: ");
		action.append("\nId: " + id);
		for (Parameter param : parameters) {
			action.append("\n\tParameter: " + param.getType() + " : " + param.getValue());
		}
		return action.toString();
	}
	
	public Vector<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatternElement other = (PatternElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
