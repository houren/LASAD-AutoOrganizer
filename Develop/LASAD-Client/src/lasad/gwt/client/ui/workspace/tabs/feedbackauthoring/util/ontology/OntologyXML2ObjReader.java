package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology;
import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.base.BaseType;
import lasad.shared.dfki.meta.ontology.base.DropDownType;
import lasad.shared.dfki.meta.ontology.base.MatchedPassageType;
import lasad.shared.dfki.meta.ontology.base.RadioButtonsType;
import lasad.shared.dfki.meta.ontology.base.TranscriptType;
import lasad.shared.dfki.meta.ontology.descr.ElementDescr;
import lasad.shared.dfki.meta.ontology.descr.LinkDescr;
import lasad.shared.dfki.meta.ontology.descr.NodeDescr;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;


/**	
 * 
 * @author anahuacv
 * This class parses the XML definition of the ontology (elements +
 * links) and store the information in {@link lasad.shared.dfki.meta.ontology Ontology}  
 * 
 */
public class OntologyXML2ObjReader {

	public static final String ONTOLOGY = "ontology";
	public static final String ELEMENTS = "elements";
	public static final String ELEMENT = "element";
	public static final String ELEMENT_OPTIONS = "elementoptions";
	public static final String UI_SETTINGS = "uisettings";
	public static final String CHILD_ELEMENTS = "childelements";

	//properties
	public static final String TYPE = "type";
	public static final String ELEMENT_ID = "elementid";
	public static final String ELEMENT_TYPE = "elementtype";
	public static final String QUANTITY = "quantity";
	public static final String MIN_QUANTITY = "minquantity";
	public static final String MAX_QUANTITY = "maxquantity";
	/**/
	public static final String HEADING = "heading";
	public static final String ENDINGS = "endings";
	public static final String CONFIG_BUTTON = "configbutton";
	/**/
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String RESIZABLE = "resizable";
	public static final String BORDER = "border";
	public static final String BACKGROUND_COLOR = "background-color";
	public static final String FONT_COLOR = "font-color";
	public static final String LINE_WIDTH = "linewidth";
	public static final String LINE_COLOR = "linecolor";
	/**/
	public static final String MIN_HEIGHT = "minheight";
	public static final String MAX_HEIGHT = "maxheight";
	public static final String LABEL = "label";
	public static final String TEXT_TYPE = "texttype";
	public static final String MANUAL_ADD = "manualadd";
	public static final String LONG_LABEL = "longlabel";
	public static final String START_ROW = "startrow";
	public static final String END_ROW = "endrow";
	public static final String START_POINT = "startpoint";
	public static final String END_POINT = "endpoint";
	public static final String TEXT = "text";
	public static final String RADIO_BUTTON_ITEMS = "radiobuttonitems";
	public static final String RADIO_BUTTON_ITEM = "radiobuttonitem";
	public static final String ID = "id";
	public static final String CHECKED = "checked";
	public static final String SELECTED_OPTION = "selectedoption";
	public static final String OPTIONS = "options";
	//	public static final String  = "";


	//values
	public static final String ELEMENT_TYPE_BOX = "box";
	public static final String ELEMENT_TYPE_RELATION = "relation";
	public static final String ELEMENT_TYPE_TEXT = "text";
	public static final String ONE = "1";
	public static final String DASH_MP = "-mp";
	public static final String TRUE = "true";
	public static final String EMPTY_STR = "";
	public static final String COMMA = ",";


	public static void parseOntology(Ontology ont, String ontologyXML) {

		if(ontologyXML == null){
			FATDebug.print(FATDebug.ERROR, "[OntologyXML2ObjReader][parseOntology]ontologyXML==null");
			return;
		}
		Document xmlDoc = XMLParser.parse(ontologyXML);

		// The <ontology> element
		Node ontology = xmlDoc.getLastChild();

		if (ontology.getNodeName().equalsIgnoreCase(ONTOLOGY)) {

			// work on ChildNodes
			for (int i = 0; i < ontology.getChildNodes().getLength(); i++) {
				Node node = ontology.getChildNodes().item(i);

				if (node.getNodeName().equalsIgnoreCase(ELEMENTS)) {

					// Work on Ontology Elements
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node elementNode = node.getChildNodes().item(d);

						if (elementNode.getNodeName().equalsIgnoreCase(ELEMENT)) {
							workOnElementNode(ont, elementNode);
						}
					}
				}
			}
		}
	}
	
	public static void workOnElementNode(Ontology ontology, Node elementNode){

		String elemType = getAttribute(elementNode, ELEMENT_TYPE);
		String elemID = getAttribute(elementNode, ELEMENT_ID);
		ElementDescr elemDescr = null;
		if (ELEMENT_TYPE_BOX.equals(elemType)) {
			elemDescr = new NodeDescr(elemID);
			ontology.addNodeDescription((NodeDescr) elemDescr);
		} else if (ELEMENT_TYPE_RELATION.equals(elemType)) {
			elemDescr = new LinkDescr(elemID);
			ontology.addLinkDescription((LinkDescr) elemDescr);
		}
		if (elemDescr != null) {			
			for (int i = 0; i < elementNode.getChildNodes().getLength(); i++) {
				Node node = elementNode.getChildNodes().item(i);
				if (node.getNodeName().equalsIgnoreCase(CHILD_ELEMENTS)) {
					// work on child elements
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node childElementNode = node.getChildNodes().item(d);
						if (childElementNode.getNodeName().equalsIgnoreCase(ELEMENT)){
							//info.addChildElement(workOnElementNode(node.getChildNodes().item(d)));
							String propID = getAttribute(childElementNode, ELEMENT_ID);
							String propType = getAttribute(childElementNode, ELEMENT_TYPE);
							int minquantity = Integer.parseInt(getAttribute(childElementNode, MIN_QUANTITY));
							int maxquantity = Integer.parseInt(getAttribute(childElementNode, MAX_QUANTITY));
							BaseType baseType = BaseType.getBaseType(propType);
							
							if (baseType != null) {
								NonStandardPropDescr propDescr = new NonStandardPropDescr(baseType, propID, minquantity, maxquantity);
								elemDescr.addPropDescr(propDescr);
								if (baseType instanceof RadioButtonsType){
									Map<String, String> code2value = new HashMap<String, String>();
									for (int counter = 0; counter < childElementNode.getChildNodes().getLength(); counter++) {
										Node child2 = node.getChildNodes().item(counter);
										String value = getAttribute(child2, LABEL);
										code2value.put(String.valueOf(counter), value);
										boolean selected = ONE.equals(getAttribute(child2, CHECKED));
										if (selected) {
											propDescr.setDefaultValue(value);
										}
									}
									propDescr.setCodedValues(code2value);
								} else if (baseType instanceof DropDownType) {
									Node child2 = null;
									for (int counter = 0; counter < childElementNode.getChildNodes().getLength(); counter++) {
										child2 = node.getChildNodes().item(counter);
										if(child2.getNodeName().equalsIgnoreCase(ELEMENT_OPTIONS)){
											break;
										}
									}
									
									String defaultValue = getAttribute(child2, SELECTED_OPTION);
									propDescr.setDefaultValue(defaultValue);
									String valuesString = getAttribute(child2, OPTIONS);
									String[] values = valuesString.split(COMMA);
									
									Map<String, String> code2value = new HashMap<String, String>();
									for (int code = 0; code < values.length; ++code) {
										String value = values[code].trim();
										code2value.put(String.valueOf(code), value);
									}
									propDescr.setCodedValues(code2value);
								} else if (baseType instanceof TranscriptType) {
									NonStandardPropDescr matchedPassagePropDescr = new NonStandardPropDescr(
											MatchedPassageType.getInstance(), propID + DASH_MP, 0,
											NonStandardPropDescr.UNRESTRICTED);
									elemDescr.addPropDescr(matchedPassagePropDescr);
								}
							} else {
								FATDebug.print(FATDebug.WAR, "OntologyXML2ObjReader" + " Unknown propType: " + propType);

							}
						}
						
					}
				}
			}
		}
	}

	public static String getAttribute(Node node, String name) {
		if (node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return EMPTY_STR;
	}

	public static int getIntAttribut(Node node, String name) {
		String value = getAttribute(node, name);
		if (value.equalsIgnoreCase(EMPTY_STR)) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	public static boolean getBooleanAttribut(Node node, String name) {
		String value = getAttribute(node, name);
		if (value == null || !value.equalsIgnoreCase(TRUE)) {
			return false;
		}
		return true;
	}

}
