package lasad.logging.commonformat.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lasad.logging.commonformat.util.jaxb.Conditions;
import lasad.logging.commonformat.util.jaxb.Groups;
import lasad.logging.commonformat.util.jaxb.ObjectDef;
import lasad.logging.commonformat.util.jaxb.ObjectFactory;
import lasad.logging.commonformat.util.jaxb.Objects;
import lasad.logging.commonformat.util.jaxb.Preamble;
import lasad.logging.commonformat.util.jaxb.Properties;
import lasad.logging.commonformat.util.jaxb.Property;
import lasad.logging.commonformat.util.jaxb.Roles;
import lasad.logging.commonformat.util.jaxb.TimeRange;
import lasad.logging.commonformat.util.jaxb.Users;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PreambleTranslator {

	public static final String testFileName = "preamble.xml";
	public static final String testFilePath = "src/";
	public static final String separator = "_";
	
	/** Returns element value
	* @param elem element (it is XML tag)
	* @return Element value otherwise empty String
	*/
	public final static String getElementValue( Node elem ) {
		Node kid;
		if( elem != null){
			if (elem.hasChildNodes()){
				for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
					if( kid.getNodeType() == Node.TEXT_NODE  ){
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}
	
	/** Writes node and all child nodes into System.out
	* @param node XML node from from XML tree wrom which will output statement start
	* @param indent number of spaces used to indent output
	*/
	public void writeDocumentToOutput(Node node) {
		// get element name
		//String nodeName = node.getNodeName();
		// get element value
		//String nodeValue = getElementValue(node);
		//System.out.println("NodeName: " + nodeName + ", NodeValue: " + nodeValue);
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			//Node attribute = attributes.item(i);
			//System.out.println("AttributeName: " + attribute.getNodeName() + ", attributeValue: " + attribute.getNodeValue());
		}
		
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				writeDocumentToOutput(child);
			}
		}
	}
	
	//public String translateOntology(String actionCategory, String actionCommand, ParamMap paramMap) {
	public Preamble translateOntology(String ontologyData) {
		
		ObjectFactory objFactory = new ObjectFactory();
		Preamble preamble = objFactory.createPreamble();
		Users users = objFactory.createUsers();
		Groups groups = objFactory.createGroups();
		Objects objects = objFactory.createObjects();
		Roles roles = objFactory.createRoles();
		Conditions conditions = objFactory.createConditions();
		TimeRange timeRange = objFactory.createTimeRange();
		preamble.setUsers(users);
		preamble.setGroups(groups);
		preamble.setObjects(objects);
		preamble.setRoles(roles);
		preamble.setConditions(conditions);
		preamble.setTimeRange(timeRange);
		
		//File file = new File(testFilePath+testFileName);
		if ( ontologyData != null){
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				Document doc = db.parse(new ByteArrayInputStream(ontologyData.getBytes(StandardCharsets.UTF_8)));
				
//				InputSource is = new InputSource();
//				is.setEncoding("UTF-8");
//				is.setCharacterStream(new StringReader(ontologyData));
//				Document doc = db.parse(is);

				doc.getDocumentElement().normalize();
				
				//System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
				// get root node of xml tree structure
				Node root = doc.getDocumentElement();
				//String nodeName = root.getNodeName();		// get element name
				//String nodeValue = getElementValue(root);	// get element value
				
				//NamedNodeMap ontologyAttributes = root.getAttributes();
				//Node ontAttribute = ontologyAttributes.getNamedItem(CFVocabulary.ATTR_TYPE);
				//String ontologyType = ontAttribute.getNodeValue();
				
				//System.out.println("NodeName: " + nodeName + " , Type:" + ontologyType);
				
				List<CFObjectDef> childrenList = null;
				NodeList children = root.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node nodeElements = children.item(i);
					if (nodeElements.getNodeType() == Node.ELEMENT_NODE) {
						
						//String childName = nodeElements.getNodeName(); // get element name
						//String childValue = getElementValue(nodeElements); // get element value
						//System.out.println("NodeName: " + childName);
						
						//get the children of "elements" and parse them
						NodeList childrenOne = nodeElements.getChildNodes();
						
						childrenList = getChildrenAsObjDef(childrenOne, null);
//						for(CFObjectDef objDef : childrenList){
//							System.out.println(objDef);
//						}
					}
				}
				if(childrenList != null){
					for(CFObjectDef objDef : childrenList){
						ObjectDef obj = new ObjectDef();
						obj.setId(objDef.getElementID());
						obj.setType(objDef.getElementType());
						Properties properties = new Properties();
						
						Property propIsRoot = new Property();
						propIsRoot.setName(CFVocabulary.PROP_IS_ROOT);
						propIsRoot.setValue(Boolean.toString(objDef.isPropIsRoot()));
						properties.getProperty().add(propIsRoot);
						
						Property propParent = null;
						if (objDef.getPropParentID() != null){
							propParent = new Property();
							propParent.setName(CFVocabulary.PROP_PARENT_ID);
							propParent.setValue(objDef.getPropParentID());
							properties.getProperty().add(propParent);
						}
						
						Set<Entry<String, String>> elemProps = objDef.getElemProps().entrySet();
						for(Entry<String, String> elemProp: elemProps){
							Property prop = new Property();
							prop.setValue(elemProp.getValue());
							prop.setName(elemProp.getKey());
							properties.getProperty().add(prop);
						}
						
						Set<Entry<String, String>> elemOptProps = objDef.getElemOptProps().entrySet();
						for(Entry<String, String> elemProp: elemOptProps){
							Property prop = new Property();
							prop.setValue(elemProp.getValue());
							prop.setName(elemProp.getKey());
							properties.getProperty().add(prop);
						}
						
						Set<Entry<String, String>> elemUISetProps = objDef.getElemUISetProps().entrySet();
						for(Entry<String, String> elemProp: elemUISetProps){
							Property prop = new Property();
							prop.setValue(elemProp.getValue());
							prop.setName(elemProp.getKey());
							properties.getProperty().add(prop);
						}
						
						obj.setProperties(properties);
						objects.getObjectDef().add(obj);
					}
				}				
			} catch (ParserConfigurationException e){
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				
			}
		}
		return preamble;
	}
	
	public List<CFObjectDef> getChildrenAsObjDef(NodeList children, String parentID){
		List<CFObjectDef> objDefList = new Vector<CFObjectDef>();
		for (int m = 0; m < children.getLength(); m++) {
			Node child = children.item(m);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				CFObjectDef objDef = new CFObjectDef();
				objDef.setPropParentID(parentID);
				if(parentID == null){
					objDef.setPropIsRoot(true);
				}
				
				//String childElementName = child.getNodeName();		// get element name
				//String childElementValue = getElementValue(child);	// get element value
				//System.out.println("---childElementName: " + childElementName);
				NamedNodeMap elementAttributes = child.getAttributes();
				
				Node attribute = elementAttributes.getNamedItem(CFVocabulary.ELEMENT_ID);
				if(parentID != null){
					//add parent name as prefix
					objDef.setElementID(parentID + separator + attribute.getNodeValue());
				}
				else{
					objDef.setElementID(attribute.getNodeValue());
				}
				attribute = elementAttributes.getNamedItem(CFVocabulary.ELEMENT_TYPE);
				objDef.setElementType(attribute.getNodeValue());
				
				try {
					elementAttributes.removeNamedItem(CFVocabulary.ELEMENT_ID);
					elementAttributes.removeNamedItem(CFVocabulary.ELEMENT_TYPE);
				} catch (DOMException e) {
					e.printStackTrace();
				}
				
				for(int i = 0; i < elementAttributes.getLength(); i++){
					Node myAttr = elementAttributes.item(i);
					String attrName = myAttr.getNodeName();
					String attrValue = myAttr.getNodeValue();
					objDef.getElemProps().put(attrName, attrValue);
				}
				
				List<CFObjectDef> childrenList = null;
				NodeList childChildren = child.getChildNodes();
				for (int n = 0; n < childChildren.getLength(); n++) {
					Node grandChild = childChildren.item(n);
					if (grandChild.getNodeType() == Node.ELEMENT_NODE) {
						String grandChildElementName = grandChild.getNodeName();		// get element name
						//String grandChildElementValue = getElementValue(grandChild);	// get element value
						//System.out.println("--grandChildElementName: " + grandChildElementName);
						
						if(grandChildElementName != null 
								&& grandChildElementName.equalsIgnoreCase(CFVocabulary.ELEMENT_OPTIONS)){
							NamedNodeMap grandChildAttributes = grandChild.getAttributes();
							for(int i = 0; i < grandChildAttributes.getLength(); i++){
								Node myAttr = grandChildAttributes.item(i);
								String attrName = myAttr.getNodeName();
								String attrValue = myAttr.getNodeValue();
								objDef.getElemOptProps().put(attrName, attrValue);
							}
						}
						else if(grandChildElementName != null 
								&& grandChildElementName.equalsIgnoreCase(CFVocabulary.ELEMENT_UISETTINGS)){
							NamedNodeMap grandChildAttributes = grandChild.getAttributes();
							for(int i = 0; i < grandChildAttributes.getLength(); i++){
								Node myAttr = grandChildAttributes.item(i);
								String attrName = myAttr.getNodeName();
								String attrValue = myAttr.getNodeValue();
								objDef.getElemUISetProps().put(attrName, attrValue);
							}
						}
						else if(grandChildElementName != null 
								&& grandChildElementName.equalsIgnoreCase(CFVocabulary.CHILD_ELEMENTS)){
							NodeList childrenNodeList = grandChild.getChildNodes();
							childrenList = getChildrenAsObjDef(childrenNodeList, objDef.getElementID());
						}
					}
				}
				//System.out.println(objDef);
				objDefList.add(objDef);
				if(childrenList != null){
					objDefList.addAll(childrenList);
					childrenList.clear();
					childrenList = null;
				}
			}
		}
		return objDefList;
	}
	
	
//	public static void main(String args[]){
//		PreambleTranslator preTrans = new PreambleTranslator();
//		preTrans.translateOntology("");
//	}
	
}