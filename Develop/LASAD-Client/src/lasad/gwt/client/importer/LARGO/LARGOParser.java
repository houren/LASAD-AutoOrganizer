package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
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
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;



/**
 * Converter to load old LARGO files into LASAD
 * 
 * @author Cornelio Hopmann
 **/
public class LARGOParser extends MVCViewSession implements MVCViewRecipient {

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final LARGOActionFactory actionBuilder = LARGOActionFactory.getInstance();

	private String xmlText;
	private String template;
	private ArgumentMapSpace myMapSpace;
	private Element mapElements;
	private HashMap<String, ElementInfo> boxInfos;
	private HashMap<String, ElementInfo> linkInfos;
	private HashMap<String, Integer> idMapping = new HashMap<String, Integer>();
	private ActionPackage actionSetBox;
	private ActionPackage actionSetLink;
	private int boxCount = 0;
	private boolean makeRelationsCalled = false;

	public LARGOParser(String xmlText, String template, ArgumentMapSpace myMapSpace, MVController controller) {
		super(controller);
		controller.registerViewSession(this);
		this.xmlText = xmlText;
		this.myMapSpace = myMapSpace;
		this.template = template;
	}

	public void parseText() {
		boxInfos = new HashMap<String, ElementInfo>();
		MVCViewSession viewSession = (MVCViewSession) myMapSpace.getMyMap().getMyViewSession();
		Iterator<ElementInfo> iterator = viewSession.getController().getMapInfo().getElementsByType("box").values().iterator();
//		Iterator<ElementInfo> iterator = myMapSpace.getMyMap().getMyViewSession().getController().getMapInfo().getElementsByType("box").values().iterator();
		while (iterator.hasNext()) {
			ElementInfo nextElementInfo = iterator.next();
			boxInfos.put(nextElementInfo.getElementID(), nextElementInfo);
		}
		actionSetBox = new ActionPackage();
		actionSetLink = new ActionPackage();

		linkInfos = new HashMap<String, ElementInfo>();
		
		Iterator<ElementInfo> linkIterator = viewSession.getController().getMapInfo().getElementsByType("relation").values().iterator();
		//Iterator<ElementInfo> linkIterator = myMapSpace.getMyMap().getMyViewSession().getController().getMapInfo().getElementsByType("relation").values().iterator();
		while (linkIterator.hasNext()) {
			ElementInfo nextElementInfo = linkIterator.next();
			linkInfos.put(nextElementInfo.getElementID(), nextElementInfo);
		}

		Document xmlDom = XMLParser.parse(xmlText);

		mapElements = xmlDom.getDocumentElement();
		makeHypoBoxes(mapElements);
		makeFactBoxes(mapElements);
		makeTestBoxes(mapElements);

		communicator.sendActionPackage(actionSetBox);
	}

	public void makeHypoBoxes(Element mapElements) {
		NodeList hypoBoxes = mapElements.getElementsByTagName("HypoNode");
		if (hypoBoxes.item(0) != null) {
			for (int i = 0; i < hypoBoxes.getLength(); i++) {
				boxCount++;
				HashMap<String, String> parsedBoxValues = new HashMap<String, String>();
				Element dummyBox = (Element) hypoBoxes.item(i);

				parsedBoxValues.put("ID", dummyBox.getAttribute("ID"));
				parsedBoxValues.put("author", dummyBox.getAttribute("creator"));
				parsedBoxValues.put("date", dummyBox.getAttribute("creationtime"));
				NamedNodeMap coordinates = dummyBox.getParentNode().getChildNodes().item(3).getAttributes();
				parsedBoxValues.put("posx", coordinates.getNamedItem("x").getNodeValue());
				parsedBoxValues.put("posy", coordinates.getNamedItem("y").getNodeValue());
				NodeList listText = dummyBox.getElementsByTagName("Text");

				if (listText.item(0).getFirstChild() != null) {
					parsedBoxValues.put("text", listText.item(0).getFirstChild().getNodeValue());
				} else {
					parsedBoxValues.put("text", "");
				}

				NodeList listOutcome = dummyBox.getElementsByTagName("Outcome");
				if (listOutcome.item(0).getFirstChild() != null) {
					parsedBoxValues.put("outcome", listOutcome.item(0).getFirstChild().getNodeValue());
				}

				NodeList listQuoteStart = dummyBox.getElementsByTagName("QuoteStart");
				if (!(listQuoteStart.item(0).getFirstChild() == null)) {
					int quoteStart = Integer.parseInt(listQuoteStart.item(0).getFirstChild().getNodeValue());
					if (quoteStart > -1) {
						parsedBoxValues.put("transcript", "true");
						LARGOTranscriptMappingInterface trans = getTranscript();
						trans.mapChars(quoteStart, true);
						parsedBoxValues.put("startRow", "" + trans.getLine());
						parsedBoxValues.put("startPoint", "" + trans.getPoint());
					}
				}

				NodeList listQuoteEnd = dummyBox.getElementsByTagName("QuoteEnd");
				if (!(listQuoteEnd.item(0).getFirstChild() == null)) {
					int quoteEnd = Integer.parseInt(listQuoteEnd.item(0).getFirstChild().getNodeValue());
					if (quoteEnd > -1) {
						parsedBoxValues.put("transcript", "true");
						LARGOTranscriptMappingInterface trans = getTranscript();
						trans.mapChars(quoteEnd, false);
						parsedBoxValues.put("endRow", "" + trans.getLine());
						parsedBoxValues.put("endPoint", "" + trans.getPoint());
					}
				}

				// Create the box
				ActionPackage newBoxWithElements = actionBuilder.createBoxWithElements(myMapSpace.getMyMap().getID(), boxInfos.get("hypothetical"), parsedBoxValues);
				actionSetBox.getActions().addAll(newBoxWithElements.getActions());
			}
		}
	}

	public void makeFactBoxes(Element mapElements) {
		NodeList factBoxes = mapElements.getElementsByTagName("FactsNode");
		if (!(factBoxes.item(0) == null)) {
			for (int i = 0; i < factBoxes.getLength(); i++) {
				boxCount++;
				HashMap<String, String> parsedBoxValues = new HashMap<String, String>();
				Element dummyBox = (Element) factBoxes.item(i);
				parsedBoxValues.put("ID", dummyBox.getAttribute("ID"));
				parsedBoxValues.put("author", dummyBox.getAttribute("creator"));
				parsedBoxValues.put("date", dummyBox.getAttribute("creationtime"));
				NamedNodeMap coordinates = dummyBox.getParentNode().getChildNodes().item(3).getAttributes();
				parsedBoxValues.put("posx", coordinates.getNamedItem("x").getNodeValue());
				parsedBoxValues.put("posy", coordinates.getNamedItem("y").getNodeValue());
				NodeList listText = dummyBox.getElementsByTagName("Text");
				if (!(listText.item(0).getFirstChild() == null)) {
					parsedBoxValues.put("text", listText.item(0).getFirstChild().getNodeValue());
				} else {
					parsedBoxValues.put("text", "");
				}

				// Create the box
				ActionPackage newBoxWithElements = actionBuilder.createBoxWithElements(myMapSpace.getMyMap().getID(), boxInfos.get("fact"), parsedBoxValues);
				actionSetBox.getActions().addAll(newBoxWithElements.getActions());
			}
		}
	}

	public void makeTestBoxes(Element mapElements) {
		NodeList testBoxes = mapElements.getElementsByTagName("TestNode");
		if (!(testBoxes.item(0) == null)) {
			for (int i = 0; i < testBoxes.getLength(); i++) {
				boxCount++;
				HashMap<String, String> parsedBoxValues = new HashMap<String, String>();
				Element dummyBox = (Element) testBoxes.item(i);
				parsedBoxValues.put("ID", dummyBox.getAttribute("ID"));
				parsedBoxValues.put("author", dummyBox.getAttribute("creator"));
				parsedBoxValues.put("date", dummyBox.getAttribute("creationtime"));
				NamedNodeMap coordinates = dummyBox.getParentNode().getChildNodes().item(3).getAttributes();
				parsedBoxValues.put("posx", coordinates.getNamedItem("x").getNodeValue());
				parsedBoxValues.put("posy", coordinates.getNamedItem("y").getNodeValue());

				NodeList listIf = dummyBox.getElementsByTagName("Cond");
				if (!(listIf.item(0).getFirstChild() == null)) {
					parsedBoxValues.put("if", listIf.item(0).getFirstChild().getNodeValue());
				} else {
					parsedBoxValues.put("if", "");
				}

				NodeList listAnd = dummyBox.getElementsByTagName("And");
				if (!(listAnd.item(0) == null) && !(listAnd.item(0).getFirstChild() == null)) {
					for (int j = 0; j < listAnd.getLength(); j++) {
						parsedBoxValues.put("and" + j, listAnd.item(j).getFirstChild().getNodeValue());
					}
					parsedBoxValues.put("andQnt", "" + listAnd.getLength());
				}
				NodeList listText = dummyBox.getElementsByTagName("Text");
				if (!(listText.item(0).getFirstChild() == null)) {
					parsedBoxValues.put("then", listText.item(0).getFirstChild().getNodeValue());
				} else {
					parsedBoxValues.put("then", "");
				}

				NodeList listEvenThough = dummyBox.getElementsByTagName("EvenThough");
				if (!(listEvenThough.item(0).getFirstChild() == null)) {
					parsedBoxValues.put("even", listEvenThough.item(0).getFirstChild().getNodeValue());
				}

				NodeList listExcept = dummyBox.getElementsByTagName("Except");
				if (!(listExcept.item(0).getFirstChild() == null)) {
					parsedBoxValues.put("except", listExcept.item(0).getFirstChild().getNodeValue());
				}
				NodeList listQuoteStart = dummyBox.getElementsByTagName("QuoteStart");
				if (!(listQuoteStart.item(0).getFirstChild() == null)) {
					int quoteStart = Integer.parseInt(listQuoteStart.item(0).getFirstChild().getNodeValue());
					if (quoteStart > -1) {
						parsedBoxValues.put("transcript", "true");
						LARGOTranscriptMappingInterface trans = getTranscript();
						trans.mapChars(quoteStart, true);
						parsedBoxValues.put("startRow", "" + trans.getLine());
						parsedBoxValues.put("startPoint", "" + trans.getPoint());
					}
				}

				NodeList listQuoteEnd = dummyBox.getElementsByTagName("QuoteEnd");
				if (!(listQuoteEnd.item(0).getFirstChild() == null)) {
					int quoteEnd = Integer.parseInt(listQuoteEnd.item(0).getFirstChild().getNodeValue());
					if (quoteEnd > -1) {
						parsedBoxValues.put("transcript", "true");
						LARGOTranscriptMappingInterface trans = getTranscript();
						trans.mapChars(quoteEnd, false);
						parsedBoxValues.put("endRow", "" + trans.getLine());
						parsedBoxValues.put("endPoint", "" + trans.getPoint());
					}
				}

				// Create the box
				ActionPackage newBoxWithElements = actionBuilder.createBoxWithElements(myMapSpace.getMyMap().getID(), boxInfos.get("test"), parsedBoxValues);
				actionSetBox.getActions().addAll(newBoxWithElements.getActions());
			}
		}
	}

	public void makeRelations(Element mapElements) {
		NodeList modifyEdges = mapElements.getElementsByTagName("ModifyEdge");
		NodeList distinguishEdge = mapElements.getElementsByTagName("DistinguishEdge");
		NodeList analogizeEdge = mapElements.getElementsByTagName("AnalogizeEdge");
		NodeList causeEdge = mapElements.getElementsByTagName("CauseEdge");
		NodeList singleDelEdge = mapElements.getElementsByTagName("SingleDelEdge");
		if (!(modifyEdges.item(0) == null)) {
			for (int i = 0; i < modifyEdges.getLength(); i++) {
				HashMap<String, Object> edgeValues = new HashMap<String, Object>();
				Element dummyEdge = (Element) modifyEdges.item(i);
				NodeList idList = dummyEdge.getElementsByTagName("UniqueID");
				edgeValues.put("source", idMapping.get(idList.item(0).getFirstChild().getNodeValue()));
				edgeValues.put("target", idMapping.get(idList.item(1).getFirstChild().getNodeValue()));

				ActionPackage newLinksWithElements = actionBuilder.createLink(myMapSpace.getMyMap().getID(), linkInfos.get("modified"), edgeValues);
				actionSetLink.getActions().addAll(newLinksWithElements.getActions());
			}
		}
		if (!(distinguishEdge.item(0) == null)) {
			for (int i = 0; i < distinguishEdge.getLength(); i++) {
				HashMap<String, Object> edgeValues = new HashMap<String, Object>();
				Element dummyEdge = (Element) distinguishEdge.item(i);
				NodeList idList = dummyEdge.getElementsByTagName("UniqueID");
				edgeValues.put("source", idMapping.get(idList.item(0).getFirstChild().getNodeValue()));
				edgeValues.put("target", idMapping.get(idList.item(1).getFirstChild().getNodeValue()));
				NodeList listText = dummyEdge.getElementsByTagName("Text");
				if (!(listText.item(0).getFirstChild() == null)) {
					edgeValues.put("text", listText.item(0).getFirstChild().getNodeValue());
				} else {
					edgeValues.put("text", "");
				}

				ActionPackage newLinksWithElements = actionBuilder.createLink(myMapSpace.getMyMap().getID(), linkInfos.get("distinguished"), edgeValues);
				actionSetLink.getActions().addAll(newLinksWithElements.getActions());

			}
		}

		if (!(analogizeEdge.item(0) == null)) {
			for (int i = 0; i < analogizeEdge.getLength(); i++) {
				HashMap<String, Object> edgeValues = new HashMap<String, Object>();
				Element dummyEdge = (Element) analogizeEdge.item(i);
				NodeList idList = dummyEdge.getElementsByTagName("UniqueID");
				edgeValues.put("source", idMapping.get(idList.item(0).getFirstChild().getNodeValue()));
				edgeValues.put("target", idMapping.get(idList.item(1).getFirstChild().getNodeValue()));
				NodeList listText = dummyEdge.getElementsByTagName("Text");
				if (!(listText.item(0).getFirstChild() == null)) {
					edgeValues.put("text", listText.item(0).getFirstChild().getNodeValue());
				} else {
					edgeValues.put("text", "");
				}

				ActionPackage newLinksWithElements = actionBuilder.createLink(myMapSpace.getMyMap().getID(), linkInfos.get("analogized"), edgeValues);
				actionSetLink.getActions().addAll(newLinksWithElements.getActions());
			}
		}

		if (!(causeEdge.item(0) == null)) {
			for (int i = 0; i < causeEdge.getLength(); i++) {
				HashMap<String, Object> edgeValues = new HashMap<String, Object>();
				Element dummyEdge = (Element) causeEdge.item(i);
				NodeList idList = dummyEdge.getElementsByTagName("UniqueID");
				edgeValues.put("source", idMapping.get(idList.item(0).getFirstChild().getNodeValue()));
				edgeValues.put("target", idMapping.get(idList.item(1).getFirstChild().getNodeValue()));

				ActionPackage newLinksWithElements = actionBuilder.createLink(myMapSpace.getMyMap().getID(), linkInfos.get("leads"), edgeValues);
				actionSetLink.getActions().addAll(newLinksWithElements.getActions());
			}
		}

		if (!(singleDelEdge.item(0) == null)) {
			for (int i = 0; i < singleDelEdge.getLength(); i++) {
				HashMap<String, Object> edgeValues = new HashMap<String, Object>();
				Element dummyEdge = (Element) singleDelEdge.item(i);
				NodeList idList = dummyEdge.getElementsByTagName("UniqueID");
				edgeValues.put("source", idMapping.get(idList.item(0).getFirstChild().getNodeValue()));
				edgeValues.put("target", idMapping.get(idList.item(1).getFirstChild().getNodeValue()));
				NodeList listText = dummyEdge.getElementsByTagName("Text");
				if (!(listText.item(0).getFirstChild() == null)) {
					edgeValues.put("text", listText.item(0).getFirstChild().getNodeValue());
				} else {
					edgeValues.put("text", "");
				}
				ActionPackage newLinksWithElements = actionBuilder.createLink(myMapSpace.getMyMap().getID(), linkInfos.get("general"), edgeValues);
				actionSetLink.getActions().addAll(newLinksWithElements.getActions());
			}
		}
		communicator.sendActionPackage(actionSetLink);
		
		ActionPackage ap = actionBuilder.parsingFinished("LARGO");
		communicator.sendActionPackage(ap);
	}

	public LARGOTranscriptMappingInterface getTranscript() {
		LARGOTranscriptMappingInterface trans;
		if (template.equals("largo_carney")) {
			trans = new CarneyTranscript();
		} else if (template.equals("largo_carney_tutorial")) {
			trans = new CarneyTranscript();
		} else if (template.equals("largo_asahi_petitioner")) {
			trans = new AsahiPetTranscript();
		} else if (template.equals("largo_asahi_respondent")) {
			trans = new AsahiResTranscript();
		} else if (template.equals("largo_burnham_petitioner")) {
			trans = new BurnhamPetTranscript();
		} else if (template.equals("largo_burnham_respondent")) {
			trans = new BurnhamResTranscript();

		} else if (template.equals("largo_burgerKing_petitioner")) {
			trans = new BurgerPetTranscript();
		} else if (template.equals("largo_burgerKing_respondent")) {
			trans = new BurgerResTranscript();
		} else {
			trans = new CarneyTranscript();
		}

		return trans;
	}

	@Override
	public void workOnDeleteElementModel(AbstractUnspecifiedElementModel model) {}

	@Override
	public Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model) {
		if (model.getType().equals("box") && !(idMapping.containsKey(model.getValue(ParameterTypes.ObjectId)))) {
			if (!(model.getValue(ParameterTypes.ObjectId) == null)) {
				idMapping.put(model.getValue(ParameterTypes.ObjectId), model.getId());
			}
			if (boxCount == idMapping.size() && !makeRelationsCalled) {

				makeRelations(mapElements);
				makeRelationsCalled = true;
			}
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
}