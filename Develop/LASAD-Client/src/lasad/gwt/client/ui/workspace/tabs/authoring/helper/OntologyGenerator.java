package lasad.gwt.client.ui.workspace.tabs.authoring.helper;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.RadioButtonParameter;
import lasad.shared.communication.objects.parameters.OntologyParameterTypeConverter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;



public class OntologyGenerator {

	public static String createOntology(String ontologyName, Set<ElementInfo> set) {
		String ontologyXML = null;

		Document doc = XMLParser.createDocument();
		
		Element ontologyElement = doc.createElement("ontology");
		ontologyElement.setAttribute("type", ontologyName);
		
		Element elementsContainer = doc.createElement("elements");
		
		for(ElementInfo topLevelElement : set) {
			Element topLevelElementNode = buildElement(doc, topLevelElement);
			elementsContainer.appendChild(topLevelElementNode);			
		}
		
		ontologyElement.appendChild(elementsContainer);
		doc.appendChild(ontologyElement);
		
		ontologyXML = doc.toString();
		
		return ontologyXML;
	}
	
	private static Element buildElement(Document doc, ElementInfo e) {
		Element el = doc.createElement("element");

		el.setAttribute("elementtype", e.getElementType());
		el.setAttribute("elementid", e.getElementID());
		
		// TODO Check if quantity is set
		el.setAttribute("minquantity", e.getMinQuantity()+"");
		el.setAttribute("maxquantity", e.getMaxQuantity()+"");
		el.setAttribute("quantity", e.getQuantity()+"");
		
		// Parse elementoptions
		Element options = doc.createElement("elementoptions");
		for(ParameterTypes option : e.getElementOptions().keySet()) {
			try {
				options.setAttribute(OntologyParameterTypeConverter.getOntologyValueByParamter(option), e.getElementOptions().get(option));
			} catch (Exception e1) {
				Logger.log("Failed to set attribute", Logger.DEBUG);
				e1.printStackTrace();
			}
		}
		el.appendChild(options);
		
		//Radio Buttons Option
		Element radioButtonOptions;
		if(e.getRadioButtonElements()!=null && e.getRadioButtonElements().size()>0){
			radioButtonOptions= doc.createElement("radiobuttonitems");
			
			for(Map<String, String> radiobuttonOption : e.getRadioButtonElements()) {
				
				Element radioButton = doc.createElement("radiobuttonitem");
				radioButton.setAttribute("id", radiobuttonOption.get("id"));
				radioButton.setAttribute("label", radiobuttonOption.get("label"));
				radioButton.setAttribute("checked", radiobuttonOption.get("checked"));
				radioButtonOptions.appendChild(radioButton);
			}
			el.appendChild(radioButtonOptions);
		}
		
		
		
		// Parse uisettings
		Element uiOptions = doc.createElement("uisettings");
		for(ParameterTypes option : e.getUiOptions().keySet()) {
			try {
				uiOptions.setAttribute(OntologyParameterTypeConverter.getOntologyValueByParamter(option), e.getUiOptions().get(option));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		el.appendChild(uiOptions);
		
		Element children = doc.createElement("childelements");
		for(String childName : e.getChildElements().keySet()) {
			children.appendChild(buildElement(doc, e.getChildElements().get(childName)));
		}
		el.appendChild(children);
		
		return el;
	}

	public static String createEmptyOntology(String name) {
		String ontologyXML = null;
			
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<ontology type=\"");
		sb.append(name);
		sb.append("\">");
		sb.append("</ontology>");
		
		ontologyXML = sb.toString();
		
		return ontologyXML;
	}
	
	/**
	 * Generates a new ElementInfo that can be used to create / define elements and, hence, to create the ontology definition
	 * @param id
	 * @param type
	 * @param minQuantity
	 * @param maxQuantity
	 * @param startQuantity
	 * @param elOptions
	 * @param uiOptions
	 * @return
	 */
	
	public static ElementInfo createElementInfo(String id, String type, Integer minQuantity, Integer maxQuantity, Integer startQuantity, Vector<Parameter> elOptions, Vector<Parameter> uiOptions, Map<String, ElementInfo> children) {
		ElementInfo el = createElementInfo(id, type, minQuantity, maxQuantity, startQuantity, elOptions, uiOptions);
		el.setChildElements(children);
		
		return el;
	}
	
	public static ElementInfo createElementInfo(String id, String type, Integer minQuantity, Integer maxQuantity, Integer startQuantity, Vector<Parameter> elOptions, Vector<Parameter> uiOptions) {
		ElementInfo newElement = new ElementInfo();
		
		newElement.setElementID(id);
		newElement.setElementType(type);
		
		if(minQuantity != null) {
			newElement.setMinQuantity(minQuantity);	
		}
		
		if(maxQuantity != null) {
			newElement.setMaxQuantity(maxQuantity);
		}
		
		if(startQuantity != null) {
			newElement.setQuantity(startQuantity);
		}
		
		for(Parameter p : elOptions) {
			newElement.addElementOption(p.getType(), p.getValue());
		}
		
		for(Parameter p : uiOptions) {
			newElement.addUiOption(p.getType(), p.getValue());
		}		
		return newElement;
	}	
	
	
	
	
	
	
	public static ElementInfo createElementInfo(String id, String type, Integer minQuantity, Integer maxQuantity, Integer startQuantity, Vector<Parameter> elOptions, Vector<Parameter> uiOptions,RadioButtonParameter  myradiobuttons) {
		ElementInfo newElement = new ElementInfo();
		
		newElement.setElementID(id);
		newElement.setElementType(type);
		
		if(minQuantity != null) {
			newElement.setMinQuantity(minQuantity);	
		}
		
		if(maxQuantity != null) {
			newElement.setMaxQuantity(maxQuantity);
		}
		
		if(startQuantity != null) {
			newElement.setQuantity(startQuantity);
		}
		
		for(Parameter p : elOptions) {
			newElement.addElementOption(p.getType(), p.getValue());
		}
		
		for(Parameter p : uiOptions) {
			newElement.addUiOption(p.getType(), p.getValue());
		}
		
		if(myradiobuttons!=null){
			
			newElement.addRadioElementOption(myradiobuttons.getRadioButtonsValue());
		
		}
		return newElement;
	}	
	
	
	
	
	
	
	public static ElementInfo createElementInfo4Preview(String id, String type, Integer minQuantity, Integer maxQuantity, Integer startQuantity, Vector<Parameter> elOptions, Vector<Parameter> uiOptions) {
		ElementInfo newElement = createElementInfo(id, type, minQuantity, maxQuantity, startQuantity, elOptions, uiOptions);
	
		// Add a text area to show the concrete height of the new box
		
		Vector<Parameter> textUiOptions = new Vector<Parameter>();
		textUiOptions.add(new Parameter(ParameterTypes.MinHeight, "28"));
		
		ElementInfo textElement = createElementInfo("text", "text", 1, 1, 1, new Vector<Parameter>(), textUiOptions);
		newElement.addChildElement(textElement);
		
		return newElement;
	}
}
