package lasad.gwt.client.xml;

import java.util.Vector;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class LoadSessionFromXMLFileParser {

	private static String getFilePart(String xml, String part) {
		Document doc = null; 
		try {
			System.out.println("-> LoadSessionFromXMLFileParser ->getFilePart Part="+part);
		    doc = XMLParser.parse(xml);
			Element lasadElement = doc.getDocumentElement();
			XMLParser.removeWhitespace(lasadElement);
			
			NodeList nodes = lasadElement.getChildNodes();
			
			for(int i = 0; i<nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if(n.getNodeName().equalsIgnoreCase(part)) {
					return (n.toString()).trim();
				}
			}
		}
		catch (DOMParseException ex) {
//		    LASADInfo.display("Error", "An error occured while parsing the xml-file. File could not be loaded.");
		    ex.printStackTrace();
		}
		

		// Part definition is missing
		return null;
	}
	
	/**
	 * 
	 * @param xml The xml string.
	 * @return True if there are multiple maps together in one xml text.
	 * @author MB
	 */
	public static boolean checkMultipleMaps(String xml) {
		if (xml.contains("<lasad-list>")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static Vector<String> getMultipleMaps(String xml) {
		Document doc = null; 
	    doc = XMLParser.parse(xml);
		NodeList lasadElements = doc.getElementsByTagName("lasad");
		Vector<String> returnValue = new Vector<String>();
		for (int i = 0; i < lasadElements.getLength(); i++) {
			returnValue.add(lasadElements.item(i).toString());
		}
		return returnValue;
	}
	
	public static String getOntology(String xml) {
		System.out.println("-> LoadSessionFromXMLFileParser ->getOntology");
		return LoadSessionFromXMLFileParser.getFilePart(xml, "ontology");
	}
	
	public static String getTemplate(String xml) {
		System.out.println("-> LoadSessionFromXMLFileParser ->getTemplate");
		return LoadSessionFromXMLFileParser.getFilePart(xml, "maptemplate");
	}

	public static String getContent(String xml) {
		System.out.println("-> LoadSessionFromXMLFileParser ->getContent");
		return LoadSessionFromXMLFileParser.getFilePart(xml, "content");
	}
	
	public static String getChatlog(String xml) {
		System.out.println("-> LoadSessionFromXMLFileParser ->getChatlog");
		return LoadSessionFromXMLFileParser.getFilePart(xml, "chatlog");
	}
	
}