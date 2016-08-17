//package lasad.gwt.client.ui.common;
//
//import java.io.Serializable;
//
///**
// * Serves for ontology definitions. Each element of a box or link has got a ElementConfig.
// * @author Frank Loll
// *
// */
//public class ElementConfig implements Serializable {
//
//	private static final long serialVersionUID = -3300740968550381254L;
//
//	private String type, backgroundColor, fontColor, label;
//	private boolean required, resizable;
//	
//	public ElementConfig() {
//		this.label = "";
//	}
//	
//	public ElementConfig(String type) {
//		this();
//		this.type = type;
//	}
//	
//	public ElementConfig(String type, String backgroundColor) {
//		this(type);
//		this.backgroundColor = backgroundColor;
//	}
//	
//	public ElementConfig(String type, boolean required) {
//		this(type);
//		this.required = required;
//	}
//	
//	public ElementConfig(String type, String backgroundColor, String fontColor) {
//		this(type, backgroundColor);
//		this.fontColor = fontColor;
//	}
//	
//	public ElementConfig(String type, String backgroundColor, String fontColor, boolean required) {
//		this(type, backgroundColor, fontColor);
//		this.required = required;
//	}
//	
//	public String getType() {
//		return type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
//	public String getBackgroundColor() {
//		return backgroundColor;
//	}
//	public void setBackgroundColor(String backgroundColor) {
//		this.backgroundColor = backgroundColor;
//	}
//	public String getFontColor() {
//		return fontColor;
//	}
//	public void setFontColor(String fontColor) {
//		this.fontColor = fontColor;
//	}
//	public boolean isRequired() {
//		return required;
//	}
//	public void setRequired(boolean required) {
//		this.required = required;
//	}
//	public boolean isResizable() {
//		return resizable;
//	}
//	public void setResizable(boolean resizable) {
//		this.resizable = resizable;
//	}
//	public String getLabel() {
//		return label;
//	}
//	public void setLabel(String label) {
//		this.label = label;
//	}	
//}