package lasad.gwt.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lasad.shared.communication.objects.parameters.ParameterTypes;





public class ElementInfo {

	public Map<ParameterTypes, String> getUiOptions() {
		return uiOptions;
	}

	public Map<ParameterTypes, String> getElementOptions() {
		return elementOptions;
	}

	// I.e. box, relation, etc.
	String elementType;

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	// I.e. what type of box or relation
	String elementID;

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String type) {
		this.elementID = type;
	}

	int quantity;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	int minQuantity;

	public int getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(int quantity) {
		this.minQuantity = quantity;
	}

	int maxQuantity;

	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(int quantity) {
		this.maxQuantity = quantity;
	}
	
//	boolean configButton;
//	
//	public boolean getConfigButton(){
//		return configButton;
//	}
//	public void setConfigButton(boolean configButton){
//		this.configButton = configButton;
//	}

	// General Option

	Map<ParameterTypes, String> uiOptions = new HashMap<ParameterTypes, String>();

	public void addUiOption(ParameterTypes vName, String value) {
		uiOptions.put(vName, value);
	}

	public String getUiOption(ParameterTypes vName) {
		return uiOptions.get(vName);
	}

	Map<ParameterTypes, String> elementOptions = new HashMap<ParameterTypes, String>();

	public void addElementOption(ParameterTypes vName, String value) {
		elementOptions.put(vName, value);
	}

	public String getElementOption(ParameterTypes vName) {
		return elementOptions.get(vName);
	}
	
	ArrayList <Map<String, String>> radiobuttons= new ArrayList <Map<String, String>>();

	public void addRadioElementOption(ArrayList <Map<String, String>> myradiobuttons ) {
	radiobuttons=myradiobuttons;

	}

	public ArrayList <Map<String, String>> getRadioButtonElements() {
		return radiobuttons;
	}

	Map<String, ElementInfo> childElements = new LinkedHashMap<String, ElementInfo>();

	public void addChildElement(ElementInfo info) {
		childElements.put(info.getElementID(), info);
	}

	public Map<String, ElementInfo> getChildElements() {
		return childElements;
	}

	public void setChildElements(Map<String, ElementInfo> newOrder) {
		this.childElements = newOrder;
		
	}
}
