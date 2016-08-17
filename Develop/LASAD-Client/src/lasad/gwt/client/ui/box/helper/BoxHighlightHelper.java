/* This class provides a more sophisticated highlighting mechanism for the boxes.
 * Unfortunately it works only for the boxes and thus, is different from the link highlight mechanism.
 * 
 * We are not using this class currently, but we may use it in future. So please to not remove!
 */

//package lasad.gwt.client.ui.box.helper;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//import com.google.gwt.user.client.DOM;
//import com.google.gwt.user.client.Element;
//
//import lasad.gwt.client.ui.box.Box;
//
//public class BoxHighlightHelper {
//	
//	private Box myBox = null;
//
//	private Element topLeft;
//	private Element topRight;
//	private Element bottomLeft;
//	private Element bottomRight;
//	
//	private Vector<String> highlightStyle = new Vector<String>();
//	private Map<String,String> highlightBackgroundColor = new HashMap<String,String>();
//	
//	private String defaultBackgroundColor= "transparent";
//	
//	public BoxHighlightHelper(Box myBox) {
//		this.myBox = myBox;
//		
//		initElements();
//		
//		addStyle("default", defaultBackgroundColor);
//	}
//	
//	private void initElements() {
//		Element dummy;
//
//		topLeft = DOM.createDiv();
//		topLeft.setClassName("box-highlight-topLeft");
//		DOM.appendChild(this.myBox.northArea, topLeft);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horLeft_long");
//		DOM.appendChild(topLeft,dummy);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horLeft_short");
//		DOM.appendChild(topLeft,dummy);
//
//		topRight = DOM.createDiv();
//		topRight.setClassName("box-highlight-topRight");
//		DOM.appendChild(this.myBox.northArea, topRight);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horRight_long");
//		DOM.appendChild(topRight,dummy);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horRight_short");
//		DOM.appendChild(topRight,dummy);
//
//		bottomLeft = DOM.createDiv();
//		bottomLeft.setClassName("box-highlight-bottomLeft");
//		DOM.appendChild(this.myBox.southArea, bottomLeft);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horLeft_long");
//		DOM.appendChild(bottomLeft,dummy);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horLeft_short");
//		DOM.appendChild(bottomLeft,dummy);
//			
//		bottomRight = DOM.createDiv();
//		bottomRight.setClassName("box-highlight-bottomRight");
//		DOM.appendChild(this.myBox.southArea, bottomRight);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horRight_long");
//		DOM.appendChild(bottomRight,dummy);
//
//		dummy= DOM.createDiv();
//		dummy.setClassName("box-highlight-horRight_short");
//		DOM.appendChild(bottomRight,dummy);
//	}
//	
//	public void calculateElements() {
//		DOM.setStyleAttribute(topLeft, "width", ((this.myBox.width-this.myBox.CONNECTOR_WIDTH)/2-this.myBox.CONNECTOR_HEIGHT-7)+"px");
//		DOM.setStyleAttribute(topLeft, "backgroundColor", highlightBackgroundColor.get(highlightStyle.lastElement()));
//		
//		DOM.setStyleAttribute(topRight, "width", ((this.myBox.width-this.myBox.CONNECTOR_WIDTH)/2-this.myBox.CONNECTOR_HEIGHT-7)+"px");
//		DOM.setStyleAttribute(topRight, "backgroundColor", highlightBackgroundColor.get(highlightStyle.lastElement()));
//
//		DOM.setStyleAttribute(bottomLeft, "width", ((this.myBox.width-this.myBox.CONNECTOR_WIDTH)/2-this.myBox.CONNECTOR_HEIGHT-7)+"px");
//		DOM.setStyleAttribute(bottomLeft, "backgroundColor", highlightBackgroundColor.get(highlightStyle.lastElement()));
//
//		DOM.setStyleAttribute(bottomRight, "width", ((this.myBox.width-this.myBox.CONNECTOR_WIDTH)/2-this.myBox.CONNECTOR_HEIGHT-7)+"px");
//		DOM.setStyleAttribute(bottomRight, "backgroundColor", highlightBackgroundColor.get(highlightStyle.lastElement()));
//	}
//
//	public void addStyle(String styleName, String backgroundColor) {
//		this.highlightBackgroundColor.put(styleName, backgroundColor);
//		this.highlightStyle.add(styleName);
//		
//		calculateElements();
//	}
//
//	public void addStyle(String styleName, String backgroundColor, int pos) {
//		this.highlightBackgroundColor.put(styleName, backgroundColor);
//		this.highlightStyle.add(pos,styleName);
//		
//		calculateElements();
//	}
//
//	public void removeStyle(String styleName){
//		if(this.highlightStyle.contains(styleName) && styleName != "default") {
//			this.highlightStyle.remove(styleName);
//			this.highlightBackgroundColor.remove(styleName);
//			
//			calculateElements();
//		}
//	}
//}