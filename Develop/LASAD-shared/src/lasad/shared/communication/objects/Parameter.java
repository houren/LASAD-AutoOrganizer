package lasad.shared.communication.objects;

import java.io.Serializable;

import lasad.shared.communication.objects.parameters.ParameterTypes;



/**
 * Each Parameter consists of a pair of name and value The Parameter will be
 * used in ActionPackage and Action class
 * 
 * @author Frank Loll
 */
public class Parameter implements Serializable {

	private static final long serialVersionUID = -2216971808329964168L;

	private ParameterTypes type; 
	private String value;

	public Parameter() {}

	public Parameter(ParameterTypes type, String value) {
		this.type = type;
		this.value = value;
	}

	public ParameterTypes getType() {
		return type;
	}

	public void setType(ParameterTypes type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}