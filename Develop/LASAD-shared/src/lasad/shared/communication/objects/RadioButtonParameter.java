package lasad.shared.communication.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class RadioButtonParameter implements Serializable {

	
	
	private static final long serialVersionUID = -2216971808329964168L;

	private String name;
	
	 ArrayList <Map<String, String>> radiobuttons=new ArrayList <Map<String, String>>();

	public RadioButtonParameter() {}

	
	//new ()
	public RadioButtonParameter(String name) {
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList <Map<String, String>> getRadioButtonsValue() {
		return radiobuttons;
	}

	public void addValue(Map<String, String> radiobutton) {
		radiobuttons.add(radiobutton);
	}
	
}
