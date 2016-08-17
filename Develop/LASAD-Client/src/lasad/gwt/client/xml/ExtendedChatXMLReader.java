package lasad.gwt.client.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ExtendedChatXMLReader {


	
public 	ExtendedChatXMLReader(){
	
	
	
	
}
	

public  static ArrayList<ArrayList <Map<String, String>>> GetSentenOpenerConfig(String xmlString){
	Document xmlDoc = XMLParser.parse(xmlString);
	
	ArrayList<ArrayList <Map<String, String>>>sentenceopenerstructure=new ArrayList<ArrayList <Map<String, String>>>();
	
	
	ArrayList <Map<String, String>>proponetcolumn;
	
	System.out.println("Root element " +  xmlDoc.getDocumentElement().getNodeName());
	  NodeList nodeLst =  xmlDoc.getElementsByTagName("sentenceopenercolumn");
	  for (int s = 0; s < nodeLst.getLength(); s++) {

		  proponetcolumn=new ArrayList <Map<String, String>>();
		    Node fstNode = nodeLst.item(s);
		    NodeList childNodes = fstNode.getChildNodes();
		    for(int h=0;h<childNodes.getLength();h++){		    	
		    	
		    	Node cellnode=childNodes.item(h);
		    	if(cellnode.getNodeName().equalsIgnoreCase("sentenceopenercell")){
		    	Map<String, String> item= new HashMap<String, String>();		
		    	Element selement = (Element)cellnode;
		    	item.put("Text", selement.getAttribute("text"));
	    		//item.put("FontSize",selement.getAttribute("fontsize"));
	    		//item.put("FontStyle",selement.getAttribute("fontstyle"));
	    		item.put("Bold",selement.getAttribute("bold"));
	    		item.put("Type",selement.getAttribute("type"));
	    		item.put("Color",selement.getAttribute("color"));
	    		item.put("Backgroundcolor",selement.getAttribute("backgroundcolor"));	    			    		
	    		item.put("ID", selement.getAttribute("cellid"));
	    		proponetcolumn.add(item);	    		
		    	}
		    }	
		    sentenceopenerstructure.add(proponetcolumn);
		    
}
	  return sentenceopenerstructure; 
	  }


public static Map<String, String> GetSentenceOpenerRules(String xmlString){
	
	
Document xmlDoc = XMLParser.parse(xmlString);
	
Map<String, String> sitem= new HashMap<String, String>();
	  NodeList nodeLst =  xmlDoc.getElementsByTagName("sentenceopeners");
	  for (int s = 0; s < nodeLst.getLength(); s++) {

		  
		    Node fstNode = nodeLst.item(s);
			Element selement = (Element)fstNode;
			
	    	sitem.put("Force", selement.getAttribute("forceselection"));
    		sitem.put("DefaultText",selement.getAttribute("defaulttext"));
    		sitem.put("SaveButtonText",selement.getAttribute("sendbuttontext"));
    		sitem.put("ClearButtonText",selement.getAttribute("clearbuttontext"));
    		sitem.put("HeaderText",selement.getAttribute("headertext"));
    		sitem.put("EndString",selement.getAttribute("endstring"));
    		sitem.put("FontSize",selement.getAttribute("fontsize"));
    		sitem.put("FontStyle",selement.getAttribute("fontstyle"));
    		
    		    
}
  	
  	
  	
	  
	
  	return sitem;
	
	
}

}
