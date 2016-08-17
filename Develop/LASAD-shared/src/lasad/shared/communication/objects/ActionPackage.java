package lasad.shared.communication.objects;

import java.io.Serializable;
import java.util.Vector;

import lasad.shared.communication.objects.parameters.ParameterTypes;



/**
 * An ActionPackage holds a set of Actions. Each Actionpackage will get a SESSION-ID as parameter.
 * 
 * @author Frank Loll
 */
public class ActionPackage implements Serializable {

	private static final long serialVersionUID = 7837877603551418909L;

	private Vector<Action> actions;
	private Vector<Parameter> parameters;

	public ActionPackage() {
		actions = new Vector<Action>();
		parameters = new Vector<Parameter>();
	}

	public ActionPackage addAction(Action a) {
		if (a != null)
			this.actions.add(a);
		return this;
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
			// parameters.add(new Parameter(type, value));
		}
	}

	/**
	 * Remove parameters by name
	 * 
	 * @param name
	 */
	public void removeParameter(ParameterTypes type) {
		for (Parameter param : parameters) {
			if (param.getType().equals(type)) {
				parameters.remove(param);
			}
		}
	}

	/**
	 * Remove parameter by name and value
	 * 
	 * @param name
	 * @param value
	 */
	public void removeParameter(ParameterTypes type, String value) {
		for (Parameter param : parameters) {
			if (param.getType().equals(type) && param.getValue().equals(value)) {
				parameters.remove(param);
			}
		}
	}

	/**
	 * Get the first value of a parameter
	 * 
	 * @param name
	 * @return value
	 */
	public String getParameterValue(ParameterTypes type) {
		for (Parameter param : parameters) {
			if (param.getType().equals(type)) {
				return param.getValue();
			}
		}
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
			if (param.getType().equals(type)) {
				returnVector.add(param.getValue());
			}
		}
		if (returnVector.size() == 0)
			return null;

		return returnVector;
	}

	public void replaceParameter(ParameterTypes type, String newValue) {
		for (Parameter param : parameters) {
			if (param.getType().equals(type)) {
				param.setValue(newValue);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder action = new StringBuilder();
		action.append("ACTIONPACKAGE: ");
		for (Parameter param : parameters) {
			action.append("\n\tParameter: " + param.getType() + " : " + param.getValue());
		}

		for (Action a : actions) {
			action.append("\n" + a.toString());
		}

		return action.toString();
	}

	public Vector<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Parameter> parameters) {
		this.parameters = parameters;
	}

	public Vector<Action> getActions() {
		return actions;
	}

	public void setActions(Vector<Action> actions) {
		this.actions = actions;
	}

	public static ActionPackage wrapAction(Action a) {
		ActionPackage p = new ActionPackage();
		p.addAction(a);

		return p;
	}

	public ActionPackage addActionPackage(ActionPackage ap) {
		for (Action a : ap.getActions()) {
			if (a != null)
				this.actions.add(a);
		}
		return this;
	}

}