package lasad.gwt.client.importer.ARGUNAUT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;



/**
 * Provides capability to parse old Digalo / ARGUNAUT .gml files
 * 
 * @author Marcel Bergmann
 * **/
public class ArgunautParser extends MVCViewSession implements MVCViewRecipient {

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ARGUNAUTActionFactory actionBuilder = ARGUNAUTActionFactory.getInstance();

	private String xmlText;
	private ArgumentMapSpace myMapSpace;
	private HashMap<String, ElementInfo> boxInfos;
	private Vector<Element> boxes = new Vector<Element>();
	private HashMap<String, ElementInfo> linkInfos;
	private Vector<Element> links = new Vector<Element>();
	private boolean parseLinkCalled = false;

	private ActionPackage boxesActionSet = null;
	private ActionPackage linksActionSet = null;

	private HashMap<String, Integer> idMapping = new HashMap<String, Integer>();

	public ArgunautParser(String xmlText, ArgumentMapSpace myMapSpace, MVController controller) {
		super(controller);
		Logger.log(xmlText, 1);
		controller.registerViewSession(this);
		this.xmlText = xmlText;
		this.myMapSpace = myMapSpace;
	}

	public void parseText() {
		// collect box infos
		boxInfos = new HashMap<String, ElementInfo>();
		MVCViewSession viewSession = (MVCViewSession) myMapSpace.getMyMap().getMyViewSession();
		Iterator<ElementInfo> boxIterator = viewSession.getController().getMapInfo().getElementsByType("box").values().iterator();
		//Iterator<ElementInfo> boxIterator = myMapSpace.getMyMap().getMyViewSession().getController().getMapInfo().getElementsByType("box").values().iterator();
		while (boxIterator.hasNext()) {
			ElementInfo nextElementInfo = boxIterator.next();
			boxInfos.put(nextElementInfo.getElementID(), nextElementInfo);
		}

		// collect link infos
		linkInfos = new HashMap<String, ElementInfo>();
		Iterator<ElementInfo> linkIterator = viewSession.getController().getMapInfo().getElementsByType("relation").values().iterator();
//		Iterator<ElementInfo> linkIterator = myMapSpace.getMyMap().getMyViewSession().getController().getMapInfo().getElementsByType("relation").values().iterator();
		while (linkIterator.hasNext()) {
			ElementInfo nextElementInfo = linkIterator.next();
			linkInfos.put(nextElementInfo.getElementID(), nextElementInfo);
		}

		Document doc = XMLParser.parse(xmlText);
		Element graphml = doc.getDocumentElement();
		NodeList graphmlChildren = graphml.getChildNodes();

		// Get graph tag
		Node graph = null;
		for (int i = 0; i < graphmlChildren.getLength(); i++) {
			if (graphmlChildren.item(i).getNodeName().equals("rc:graph") || graphmlChildren.item(i).getNodeName().equals("graph")) {
				graph = graphmlChildren.item(i);
			}
		}

		// Get nodes and edges
		NodeList tempGraphElements = graph.getChildNodes();

		// workaround, because there're nasty blank lines in the node list when
		// you get the text from the input window :(
		LinkedList<Element> graphElements = new LinkedList<Element>();
		for (int i = 0; i < tempGraphElements.getLength(); i++) {
			if (!tempGraphElements.item(i).toString().trim().equals("")) {
				graphElements.add((Element) tempGraphElements.item(i));
			}
		}

		Iterator<Element> graphElementsIterator = graphElements.iterator();
		Element currentElement = null;

		boxesActionSet = new ActionPackage();
		linksActionSet = new ActionPackage();

		while (graphElementsIterator.hasNext()) {
			currentElement = graphElementsIterator.next();

			// in case the parsed element is a vertex/box
			if (currentElement.getAttributeNode("id").getNodeValue().charAt(0) == 'V') {
				boxes.add(currentElement);
			}

			// in case the parsed element is an edge/a link
			else if (currentElement.getAttributeNode("id").getNodeValue().charAt(0) == 'E') {
				links.add(currentElement);
			}
		}

		// Notice the side effect of the method workOnRegisterNewModel(...),
		// which calls the parseLink() method
		// after all boxes have been created on server side and have been send
		// back to the clients
		this.parseBoxes();
		communicator.sendActionPackage(boxesActionSet);
	}

	private void parseBoxes() {

		// box properties
		String nodeId = "";
		String nodeType = "";
		String nodeContent = "";
		String nodeHeadline = "";
		String nodeCreator = "";
		String nodeCreationDate = "";
		String nodeModificationDate = "";
		String nodeFirstModificationDate = "";
		int coord_x = 0;
		int coord_y = 0;

		for (Element currentElement : boxes) {

			nodeId = currentElement.getAttributeNode("id").getNodeValue();
			NodeList tempNodeProperties = currentElement.getChildNodes();

			// workaround, because there're nasty blank lines in the node list
			// when you get the text from the input window :(
			LinkedList<Element> nodeProperties = new LinkedList<Element>();
			for (int i = 0; i < tempNodeProperties.getLength(); i++) {
				if (!tempNodeProperties.item(i).toString().trim().equals("")) {
					nodeProperties.add((Element) tempNodeProperties.item(i));
				}
			}

			Iterator<Element> nodePropertiesIterator = nodeProperties.iterator();
			Element currentNodeProperties = null;
			while (nodePropertiesIterator.hasNext()) {
				currentNodeProperties = nodePropertiesIterator.next();
				if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodetype")) {
					nodeType = currentNodeProperties.getFirstChild().toString().toLowerCase();
					// Next code line is needed because there're some nasty
					// inconsistencies in the gml-files o_0
					if (nodeType.equals("argument (claim + reason)") || nodeType.equals("argument (claim+reason)")) {
						nodeType = "argument";
					}
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodecontent")) {
					if (currentNodeProperties.getFirstChild() != null) {
						nodeContent = currentNodeProperties.getFirstChild().toString();
						nodeContent = replaceHTML(nodeContent);
					} else {
						nodeContent = "";
					}
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodecoordx")) {
					coord_x = Integer.parseInt(currentNodeProperties.getFirstChild().toString());
					if (coord_x > 0) coord_x = (int) (coord_x * 1.5);
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodecoordy")) {
					coord_y = Integer.parseInt(currentNodeProperties.getFirstChild().toString());
					if (coord_y > 0) coord_y = (int) (coord_y * 1.5);
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodetitle")) {
					if (currentNodeProperties.getFirstChild() != null) {
						nodeHeadline = currentNodeProperties.getFirstChild().toString();
						nodeHeadline = replaceHTML(nodeHeadline);
					}
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodecreator")) {
					nodeCreator = currentNodeProperties.getFirstChild().toString();
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodecreationdate")) {
					nodeCreationDate = currentNodeProperties.getFirstChild().toString();
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodemodificationdate")) {
					nodeModificationDate = currentNodeProperties.getFirstChild().toString();
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("nodefirstmodificationdate")) {
					nodeFirstModificationDate = currentNodeProperties.getFirstChild().toString();
				}
			}

			ActionPackage tmp = actionBuilder.createBoxWithElementsAndContent(boxInfos.get(nodeType), nodeId, myMapSpace.getMyMap().getID(), coord_x, coord_y, nodeHeadline, nodeContent, nodeCreator, nodeCreationDate, nodeModificationDate, nodeFirstModificationDate);
			boxesActionSet.getActions().addAll(tmp.getActions());
		}
	}

	private void parseLinks() {
		// link properties
		String edgeType = "";
		String source = "";
		String target = "";
		String edgeCreator = "";
		String edgeCreationDate = null;

		for (Element currentElement : links) {
			source = Integer.toString(idMapping.get(currentElement.getAttributeNode("source").getValue()));
			target = Integer.toString(idMapping.get(currentElement.getAttributeNode("target").getValue()));
			NodeList tempNodeProperties = currentElement.getChildNodes();

			// workaround, because there're nasty blank lines in the node list
			// when you get the text from the input window :(
			LinkedList<Element> nodeProperties = new LinkedList<Element>();
			for (int i = 0; i < tempNodeProperties.getLength(); i++) {
				if (!tempNodeProperties.item(i).toString().trim().equals("")) {
					nodeProperties.add((Element) tempNodeProperties.item(i));
				}
			}

			Iterator<Element> nodePropertiesIterator = nodeProperties.iterator();
			Element currentNodeProperties = null;
			while (nodePropertiesIterator.hasNext()) {
				currentNodeProperties = nodePropertiesIterator.next();
				if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("edgetype")) {
					edgeType = currentNodeProperties.getFirstChild().toString();
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("edgecreator")) {
					edgeCreator = currentNodeProperties.getFirstChild().toString();
				} else if (currentNodeProperties.getAttributeNode("key").getValue().equalsIgnoreCase("edgecreationdate")) {
					edgeCreationDate = currentNodeProperties.getFirstChild().toString();
				}
			}

			ActionPackage tmp = actionBuilder.createLinkWithElements(linkInfos.get(edgeType), myMapSpace.getMyMap().getID(), source, target, edgeCreator, edgeCreationDate);
			linksActionSet.getActions().addAll(tmp.getActions());
		}
		communicator.sendActionPackage(linksActionSet);
		
		ActionPackage ap = actionBuilder.parsingFinished("ARGUNAUT");
		communicator.sendActionPackage(ap);
	}

	@Override
	public void workOnDeleteElementModel(AbstractUnspecifiedElementModel model) {}

	@Override
	public Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model) {

		if (model.getType().equals("box")) {
			if (model.getValue(ParameterTypes.ObjectId) != null && !idMapping.containsKey(model.getValue(ParameterTypes.ObjectId))) {
				idMapping.put(model.getValue(ParameterTypes.ObjectId), model.getId());
			}
		}

		if (boxes.size() != 0 && boxes.size() == idMapping.size() && parseLinkCalled == false) {
			this.parseLinks();
			parseLinkCalled = true;
		}

		Vector<MVCViewRecipient> newRecipients = new Vector<MVCViewRecipient>();
		newRecipients.add(this);
		return newRecipients;
	}

	@Override
	public Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		return null;
	}

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		return false;
	}

	@Override
	public AbstractUnspecifiedElementModel getConnectedModel() {
		return null;
	}

	private String replaceHTML(String in) {
		String out = in.replace("&quot;", "\"");
		out = out.replace("&semi;", ";");
		out = out.replace("&gt;", ">");
		out = out.replace("&lt;", "<");
		out = out.replace("&amp;", "&");
		out = out.replace("&apos;", "'");

		return out;
	}
}
