package lasad.shared.communication.objects;

import java.io.Serializable;
import java.util.Vector;

import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.authoring.frontenddata.ObjectFE;


/**
 * Exchange object which contains a specific LASAD action Each action has a command (cmd), a category and a set of parameters
 * 
 * @author Frank Loll
 */
public class Action implements Serializable {

	private static final long serialVersionUID = 2636683835916757245L;

	private Commands cmd;
	private Categories category;
	private Vector<Parameter> parameters;
	private ObjectFE objectFE;
	private long timestamp;

	/**
	 * Creates a general action
	 */
	public Action() {
		this(Commands.None, Categories.None);
//		cmd = ServerCommands.None;
//		category = Categories.None;
//		parameters = new Vector<Parameter>();
	}

	/**
	 * Creates a specified action
	 * 
	 * @param command
	 */
	public Action(Commands command) {
		this(command, Categories.None);
//		cmd = command;
//		category = Categories.None;
//		parameters = new Vector<Parameter>();
	}

	/**
	 * Creates a specified action defined by command and category
	 * 
	 * @param command
	 * @param category
	 */
	public Action(Commands command, Categories category) {
		this.cmd = command;
		this.category = category;
		parameters = new Vector<Parameter>();
		timestamp = System.currentTimeMillis();
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

	public long getTimeStamp()
	{
		return timestamp;
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
	
	public boolean removeParameter(ParameterTypes type){
		Parameter toRemove = null;
		for (Parameter param : parameters) {
			// TD, 24.11.
			if (param.getType().equals(type)){
				toRemove = param;
			}
		}
		if (toRemove != null) {
			return parameters.remove(toRemove);
		}
		return false;
	}

	/**
	 * Create a new Action which is equal to the old one, but does not have the
	 * parameter in it.
	 */
	public static Action removeParameter(Action a, ParameterTypes type) {

		Action b = new Action(a.getCmd(), a.getCategory());

		for (Parameter param : a.parameters) {
			if (!param.getType().equals(type)) {
				b.getParameters().add(param);
			}
		}

		return b;
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
		}//TODO pr�fen, ob das nicht einen Vector <String> zur�ckliefern m�sste... (siehe addParameterVector...)
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

	public void replaceParameter(ParameterTypes type, String newValue) {
		for (Parameter param : parameters) {
			System.out.println("Value: " + param.getValue() + " mit Typ: " + param.getType());
			if (param.getType() != null && param.getType().equals(type)) {
				param.setValue(newValue);
			}
		}
	}

//	public void replaceParameter(String parameterName, String newValue) {
//		for (Parameter param : parameters) {
//			if (param.getType().equals(parameterName)) {
//				param.setValue(newValue);
//			}
//		}
//	}

	/**
	 * Sorts vector so that Parameter USERNAME is at position 0
	 * 
	 * @return
	 */
	public void sortParameterUsername() {
		// Check if USERNAME already on position 0
		if (parameters.size() > 0 && !parameters.get(0).getType().equals(ParameterTypes.UserName)) {
			// not at position 0
			int index = -1;
			// search for USERNAME value, if found save index
			for (Parameter param : parameters) {
				if(param.getType().equals(ParameterTypes.UserName)){
					//DELETE USERNAME from values
					index = parameters.indexOf(param);
					break;
				}
			}

			// if USERNAME was found bring it to position 0
			if (index != -1) {
				Parameter tmpUsername = parameters.get(index);
				parameters.remove(index);
				parameters.insertElementAt(tmpUsername, 0);
			}
		}

	}

	@Override
	public String toString() {
		StringBuilder action = new StringBuilder();
		action.append("ACTION: ");
		action.append("\n\tCategory: " + this.category);
		action.append("\n\tCommand: " + this.cmd);
		for (Parameter param : parameters) {
			action.append("\n\tParameter: " + param.getType() + " : " + param.getValue());
		}
		return action.toString();
	}

	/**
	 * Print out the complete action
	 */
	public void printAction() {
		printCommand();
		printCategory();
		printParameters(); //TODO warum nicht gleich das System.out?
	}

	/**
	 * Print out the action's category
	 */
	public void printCategory() {
		System.out.println("Category: " + category);
	}

	/**
	 * Print out the action's command
	 */
	public void printCommand() {
		System.out.println("Command: " + cmd);
	}

	/**
	 * Print out all parameters to console
	 */
	private void printParameters() {
		for (Parameter param : parameters) {
			System.out.println("Parameter: " + param.getType() + " : " + param.getValue());
		}
	}

	public Commands getCmd() {
		return cmd;
	}

	public void setCmd(Commands cmd) {
		this.cmd = cmd;
	}

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public Vector<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public ObjectFE getObjectFE() {
		return objectFE;
	}

	public void setObjectFE(ObjectFE objectFE) {
		this.objectFE = objectFE;
	}
}