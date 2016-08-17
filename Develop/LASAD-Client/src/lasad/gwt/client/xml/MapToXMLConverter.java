package lasad.gwt.client.xml;

import java.util.List;
import java.util.Vector;

import lasad.gwt.client.settings.DebugSettings;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.chat.ChatMessage;
import lasad.gwt.client.ui.workspace.chat.CommonChatInterface;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;



/**
 * @author Marcel Bergmann, Frank Loll (modified)
 *         This class converts a map into an xml-string for saving issues. XML
 *         structure: <lasad> <ontology> <template> <content> <element>
 *         <sub-element> <parameter> <{name}> <{value}> ...(collect all
 *         parameters) Date: November 16th, 2010
 */
public class MapToXMLConverter {

	private AbstractGraphMap map;
	private String ontology;
	private String template;

	private Vector<AbstractBox> boxes = new Vector<AbstractBox>();
	private Vector<AbstractLinkPanel> linkPanels = new Vector<AbstractLinkPanel>();

	private Document doc;
	private String xmlString;

	public MapToXMLConverter(AbstractGraphMap map, String ontology, String template) {
		this.map = map;

		this.ontology = ontology.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		this.template = template.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		this.xmlString = createXML();
	}

	private String createXML() {
		// Fetch all Box and LinkPanel objects
		List<Component> mapComponents = this.map.getItems();
		for (Component mapComponent : mapComponents) {
			if (mapComponent instanceof AbstractBox) {
				boxes.add((AbstractBox) mapComponent);
			} else if (mapComponent instanceof AbstractLinkPanel) {
				linkPanels.add((AbstractLinkPanel) mapComponent);
			}
		}

		this.doc = XMLParser.createDocument();

		// root tag
		// lasad
		Element tagLasad = doc.createElement("lasad");
		tagLasad.setAttribute("version", DebugSettings.version);

		Text valueOntology = doc.createTextNode(this.ontology);
		Text valueTemplate = doc.createTextNode(this.template);

		Element tagContent = doc.createElement("content");
		Element tagChatlog = doc.createElement("chatlog");

		/**
		 * build up the xml-tree
		 */
		doc.appendChild(tagLasad);

		tagLasad.appendChild(valueOntology);
		tagLasad.appendChild(valueTemplate);
		tagLasad.appendChild(tagContent);
		tagLasad.appendChild(tagChatlog);

		// Boxes and links
		for (Element element : createContentTags()) {
			tagContent.appendChild(element);
		}

		// Chat log
		// Fetching the correct chat panel. Condition should be checked by a getChat-Method in MyMapSpace :-/
		CommonChatInterface chatIf;
		if ((chatIf = map.getMyArgumentMapSpace().getChatPanel()) == null) {
			chatIf = map.getMyArgumentMapSpace().getExtendedChatPanel();
		}
		// some maps don't have a map
		if (chatIf != null) {
			Vector<ChatMessage> chatMessages = chatIf.getChatMessages();
			Element elementChatMessage;

			for (ChatMessage chatMessage : chatMessages) {
				elementChatMessage = doc.createElement("msg");
				elementChatMessage.setAttribute("timestamp", chatMessage.getTimestamp());
				elementChatMessage.setAttribute("author", chatMessage.getAuthor());
				if (chatMessage.getSentenceOpener() != null) {
					elementChatMessage.setAttribute("opener", chatMessage.getSentenceOpener());
					elementChatMessage.setAttribute("openercolor", chatMessage.getSentenceOpenerTextColor());
				}

				elementChatMessage.appendChild(doc.createCDATASection(chatMessage.getMessage()));
				tagChatlog.appendChild(elementChatMessage);
			}
		}

		if(imageToString()!=null)
		{
			Element tagImg = doc.createElement("backgroundimg");
			tagImg.appendChild(doc.createTextNode(imageToString()));
			tagLasad.appendChild(tagImg);
		}
		
		String xml = doc.getDocumentElement().toString();

		xml = xml.replace("&lt;", "<");
		xml = xml.replace("&gt;", ">");

		return xml;
	}

	private Vector<Element> createBoxTags() {
		Vector<Element> returnElements = new Vector<Element>();
		Element element;
		for (AbstractBox box : boxes) {
			// create initial element tag <element> and collect attribute values
			element = doc.createElement("element");
			element.setAttribute("id", box.getConnectedModel().getId() + "");
			element.setAttribute("type", box.getConnectedModel().getType());
			element.setAttribute("element-id", box.getElementInfo().getElementID());
			element.setAttribute("root-element-id", box.getConnectedModel().getValue(ParameterTypes.RootElementId));
			element.setAttribute("pos-x", Integer.toString(box.getPosition(true).x));
			element.setAttribute("pos-y", Integer.toString(box.getPosition(true).y));
			element.setAttribute("height", Integer.toString(box.getSize().height));
			element.setAttribute("width", Integer.toString(box.getSize().width));
			element.setAttribute("username", box.getConnectedModel().getValue(ParameterTypes.UserName));

			// fetch ExtendedElements of the current mapElement and create tags
			// for them either --> <sub-element>
			for (AbstractExtendedElement extendedElement : box.getExtendedElements()) {
				element.appendChild(createExtendedElementTag(extendedElement));
			}

			returnElements.add(element);
		}

		return returnElements;
	}

	private Vector<Element> createContentTags() {
		Vector<Element> returnElements = new Vector<Element>();
		returnElements.addAll(createBoxTags());
		returnElements.addAll(createLinkTags());

		return returnElements;
	}

	private Vector<Element> createLinkTags() {
		Vector<Element> returnElements = new Vector<Element>();
		Element element;
		for (AbstractLinkPanel linkPanel : linkPanels) {
			// create initial element tag <element> and collect attribute values
			element = doc.createElement("element");
			element.setAttribute("type", linkPanel.getElementInfo().getElementType());
			element.setAttribute("element-id", linkPanel.getElementInfo().getElementID());
			element.setAttribute("root-element-id", linkPanel.getMyLink().getConnectedModel().getValue(ParameterTypes.RootElementId));
			
			//The following hack is needed because there are two different parent parameter: parents as objects and direction
			//as a way to store in which direction the link points. Unfortunately, one uses the model id and the user uses the 
			//rootelementid. The if-else-clause checks whether the first direction id is equal with the second 
			//parent id. If that's the case, then the direction was switched after link creation
			String direction = linkPanel.getMyLink().getConnectedModel().getValue(ParameterTypes.Direction);
			if (direction != null && direction.split(",")[0].equalsIgnoreCase(Integer.toString(linkPanel.getMyLink().getConnectedModel().getParents().get(1).getId()))) {
				element.setAttribute("start-parent-root-element-id",
						linkPanel.getMyLink().getConnectedModel().getParents().get(1).getValue(ParameterTypes.RootElementId) + "");
				element.setAttribute("end-parent-root-element-id",
						linkPanel.getMyLink().getConnectedModel().getParents().get(0).getValue(ParameterTypes.RootElementId) + "");
			} 
			else {
				element.setAttribute("start-parent-root-element-id",
						linkPanel.getMyLink().getConnectedModel().getParents().get(0).getValue(ParameterTypes.RootElementId) + "");
				element.setAttribute("end-parent-root-element-id",
						linkPanel.getMyLink().getConnectedModel().getParents().get(1).getValue(ParameterTypes.RootElementId) + "");
			};
			
			
			element.setAttribute("height", Integer.toString(linkPanel.getSize().height));
			element.setAttribute("width", Integer.toString(linkPanel.getSize().width));
			element.setAttribute("username", linkPanel.getMyLink().getConnectedModel().getValue(ParameterTypes.UserName));

			// fetch ExtendedElements of the current mapElement and create tags
			// for them either --> <sub-element>
			for (AbstractExtendedElement extendedElement : linkPanel.getMyLink().getExtendedElements()) {
				element.appendChild(createExtendedElementTag(extendedElement));
			}

			returnElements.add(element);
		}

		return returnElements;
	}

	private Element createExtendedElementTag(AbstractExtendedElement extendedElement) {

		Element subElement = doc.createElement("sub-element");
		subElement.setAttribute("type", extendedElement.getConfig().getElementType());

		for (ParameterTypes parameterName : extendedElement.getConnectedModel().getElementValues().keySet()) {
			if (!(parameterName == ParameterTypes.Status) && !(parameterName == ParameterTypes.Id)) {
				this.appendExtendedElementParameter(subElement, parameterName,
						extendedElement.getConnectedModel().getElementValues().get(parameterName));
			}
		}

		return subElement;
	}

	/**
	 * Adds a "parameter"-tag to parentElement
	 * 
	 * @param parentElement
	 * @param name
	 *            the parameter's name
	 * @param value
	 *            the parameter's value
	 */
	private void appendExtendedElementParameter(Element parentElement, ParameterTypes name, String value) {
		Element tagParameter = doc.createElement("parameter");
		Element tagName = doc.createElement("name");
		Text valueName = doc.createTextNode(name.getOldParameter().toString());
		Element tagText = doc.createElement("value");

		Text valueText;
		if (name == ParameterTypes.Text) {
			String maskedValue = "<![CDATA[" + value + "]]>";
			valueText = doc.createTextNode(maskedValue);
		} else {
			valueText = doc.createTextNode(value);
		}

		parentElement.appendChild(tagParameter);
		tagParameter.appendChild(tagName);
		tagName.appendChild(valueName);
		tagParameter.appendChild(tagText);
		tagText.appendChild(valueText);
	}

	public String getXmlString() {
		return xmlString;
	}
	
	private String imageToString()
	{
		String img = this.map.getMyArgumentMapSpace().getWorspace().getBody().getInnerHtml();
		if(img.contains("img id=\""))
		{
			img = img.substring(img.indexOf("img id=\""));
			img = img.substring(img.indexOf("src=\"")+5);
			img = img.substring(0,img.indexOf("\"")+1);
			img = img.substring(img.lastIndexOf("/")+1);
			return img;
		}
//		if(image != null && image.contains("url"))
//		{
//			int begin= image.indexOf('(')+2;
//			int end = image.indexOf(')');
//			image = image.substring(begin,end);
//			begin = image.lastIndexOf('/')+1;
//			image = image.substring(begin);
//			image = image.substring(image.lastIndexOf('\\')+1);
//		}
//		return image;
		return "";
	}
}
