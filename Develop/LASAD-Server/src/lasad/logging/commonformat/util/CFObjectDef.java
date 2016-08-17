package lasad.logging.commonformat.util;

import java.util.HashMap;

public class CFObjectDef {

	private String elementID;
	private String elementType;
	private boolean propIsRoot = false;	//set to true if it's a box/container/root element
	private String propParentID;
	
	private HashMap<String, String> elemProps = new HashMap<String, String>();	//element
	private HashMap<String, String> elemOptProps = new HashMap<String, String>();	//elementoptions
	private HashMap<String, String> elemUISetProps = new HashMap<String, String>();	//uisettings
	public String getElementID() {
		return elementID;
	}
	public void setElementID(String elementID) {
		this.elementID = elementID;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public boolean isPropIsRoot() {
		return propIsRoot;
	}
	public void setPropIsRoot(boolean propIsRoot) {
		this.propIsRoot = propIsRoot;
	}
	public String getPropParentID() {
		return propParentID;
	}
	public void setPropParentID(String propParentID) {
		this.propParentID = propParentID;
	}
	public HashMap<String, String> getElemProps() {
		return elemProps;
	}
	public void setElemProps(HashMap<String, String> elemProps) {
		this.elemProps = elemProps;
	}
	public HashMap<String, String> getElemOptProps() {
		return elemOptProps;
	}
	public void setElemOptProps(HashMap<String, String> elemOptProps) {
		this.elemOptProps = elemOptProps;
	}
	public HashMap<String, String> getElemUISetProps() {
		return elemUISetProps;
	}
	public void setElemUISetProps(HashMap<String, String> elemUISetProps) {
		this.elemUISetProps = elemUISetProps;
	}
	
	@Override
	public String toString() {
		return "CFObjectDef [elemOptProps=" + elemOptProps + ", elemProps="
				+ elemProps + ", elemUISetProps=" + elemUISetProps
				+ ", elementID=" + elementID + ", elementType=" + elementType
				+ ", propIsRoot=" + propIsRoot + ", propParentID="
				+ propParentID + "]";
	}
	

}
