package lasad.controller.xmlController;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import lasad.controller.MapController;
import lasad.entity.Map;
import lasad.entity.User;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * XML parser class for every task related to map xml text
 * 
 * @author MB
 */
public class MapParser {
	private Document doc;
	private Element rootElement;
	StringReader in;

	// TODO Resolve cyclic dependencies
	//private ActionProcessor aproc;

	/**
	 * Builds root document from {@xmlText}
	 * 
	 * @param xmlText The map xml
	 * @author MB
	 */
	public MapParser(String xmlText, ActionProcessor aproc) {
		//this.aproc = aproc;
		in = new StringReader(xmlText);

		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(in);
			rootElement = doc.getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parseContentFromXML(int mapID, int userID) {
		int maxRootElementID = 0;

		HashMap<String, Integer> rootElementIDToElementID = new HashMap<String, Integer>();

		try {
			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<org.jdom.Element> elements = rootElement.getChildren("element");
			for (org.jdom.Element element : elements) {

				String rootElementID = element.getAttributeValue("root-element-id");

				if (rootElementID != null) {
					int currentRootElementID = Integer.parseInt(rootElementID);
					if (currentRootElementID > maxRootElementID) {
						maxRootElementID = currentRootElementID;
					}
				}

				Action a = workonElements(rootElementIDToElementID, element, mapID, rootElementID);

				workonSubElements(a, mapID, element, rootElementIDToElementID, rootElementID);
			}

		} catch (Exception e) {

		} finally {
			in.close();
		}

		// Update last root element id used on this map
		Map.setLastRootElementID(mapID, maxRootElementID);
	}

	private Action workonElements(HashMap<String, Integer> rootElementIDToElementID, org.jdom.Element element, int mapID,
			String rootElementID) {
		String ontologyID = element.getAttributeValue("element-id");
		String type = element.getAttributeValue("type");
		String posX = element.getAttributeValue("pos-x");
		String posY = element.getAttributeValue("pos-y");

		String startParent = element.getAttributeValue("start-parent-root-element-id");
		String endParent = element.getAttributeValue("end-parent-root-element-id");

		Action a = new Action(Commands.CreateElement, Categories.Map);
		a.addParameter(ParameterTypes.MapId, mapID + "");
		a.addParameter(ParameterTypes.Type, type);
		a.addParameter(ParameterTypes.ElementId, ontologyID);
		a.addParameter(ParameterTypes.RootElementId, rootElementID);

		if (startParent != null && endParent != null) {
			startParent = rootElementIDToElementID.get(startParent).toString();
			endParent = rootElementIDToElementID.get(endParent).toString();

			a.addParameter(ParameterTypes.Parent, startParent);
			a.addParameter(ParameterTypes.Parent, endParent);
		} else {
			a.addParameter(ParameterTypes.PosX, posX);
			a.addParameter(ParameterTypes.PosY, posY);
		}

		return a;
	}

	private synchronized void workonSubElements(Action a, int mapID, org.jdom.Element element,
			HashMap<String, Integer> rootElementIDToElementID, String rootElementID) {
		String height = element.getAttributeValue("height");
		String width = element.getAttributeValue("width");
		String username = element.getAttributeValue("username");

		int concreteElementID = MapController.processCreatePreDefinedElement(a, mapID, getUserID(username));
		rootElementIDToElementID.put(rootElementID, concreteElementID);

		// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
		@SuppressWarnings("unchecked")
		List<org.jdom.Element> subelements = element.getChildren("sub-element");
		for (org.jdom.Element subelement : subelements) {
			// Parse attributes
			String subType = subelement.getAttributeValue("type");
			String subUsername = null;

			Action b = new Action(Commands.CreateElement, Categories.Map);
			b.addParameter(ParameterTypes.MapId, mapID + "");
			b.addParameter(ParameterTypes.Type, subType);
			b.addParameter(ParameterTypes.Parent, "LAST-ID");

			// Parse parameters
			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<org.jdom.Element> parameters = subelement.getChildren("parameter");
			for (org.jdom.Element parameter : parameters) {
				String name = parameter.getChildText("name");
				String value = parameter.getChildText("value");

				b.addParameter(ParameterTypes.fromString(name), value);

				if (name.equalsIgnoreCase(ParameterTypes.UserName.toString())) {
					subUsername = value;
				}
			}
			MapController.processCreatePreDefinedElement(b, mapID, getUserID(subUsername));
		}

		// The update action is required to force the client to show
		// the
		// correct size (especially height)

		Action upd = new Action(Commands.UpdateElement, Categories.Map);
		upd.addParameter(ParameterTypes.MapId, mapID + "");
		upd.addParameter(ParameterTypes.Id, concreteElementID + "");
		upd.addParameter(ParameterTypes.Height, height);
		upd.addParameter(ParameterTypes.Width, width);
		MapController.processUpdateElementLocal(upd, getUserID(username));
	}

	private int getUserID(String username) {
		int creatorID = 1;
		if (username != null) {
			int tempID = User.getUserID(username);
			if (tempID != -1) {
				creatorID = tempID;
			}
		}
		return creatorID;
	}
}
