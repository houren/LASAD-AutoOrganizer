package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialogListener;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;

/**
 * Handles events on the create box and link dialog displayed on click and drag of a box's knob on the diagramming area
 * @author Anahuac
 *
 */
public class CreateBoxLinkDialogListenerPattern extends
		AbstractCreateBoxLinkDialogListener {

	public CreateBoxLinkDialogListenerPattern(GraphMap map,
			AbstractCreateBoxLinkDialog dialog) {
		super(map, dialog);
	}

	@Override
	protected AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(
			GraphMap map, AbstractBox start, int posX, int posY, int step,
			ElementInfo boxConfig) {
		return new CreateBoxLinkDialogPattern(map, start, posX, posY, step, boxConfig);
	}

	@Override
	protected void onClickSendUpdateToServer(ElementInfo boxInfo,
			ElementInfo linkInfo, String mapID, int x, int y, String start,
			String end) {
		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
				createBoxAndLink(boxInfo, linkInfo, agentId, mapID, x, y, start, end);
	}

//		public ActionPackage createBoxAndLink(ElementInfo boxInfo, ElementInfo linkInfo, int mapID, int x, int y, String start, String end) {
//			ActionPackage p = new ActionPackage();
//			Action a = createBox(boxInfo, mapID, x, y);
//			p.addAction(a);
//
//			Vector<Action> b = createBoxElementsAction(boxInfo, mapID);
//			if (b.size() > 0) {
//				for (Action c : b) {
//					p.addAction(c);
//				}
//			}
//
//			Vector<Action> c = createLinkWithElementsAction(linkInfo, mapID, start, end, null);
//			if (c.size() > 0) {
//				for (Action d : c) {
//					p.addAction(d);
//				}
//			}
//
//			return p;
//		}
	
//	private Vector<Action> createBoxElementsAction(ElementInfo currentElement, int mapID) {
//
//		Vector<Action> allActions = new Vector<Action>();
//
//		for (ElementInfo childElement : currentElement.getChildElements().values()) {
//			Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG_DETAILS);
//			for (int i = 0; i < childElement.getQuantity(); i++) {
//				Action a = new Action(Commands.CreateElement, Categories.Map);
//				a.addParameter(ParameterTypes.Type, childElement.getElementType());
//				a.addParameter(ParameterTypes.MapId, mapID + "");
//				a.addParameter(ParameterTypes.Parent, "LAST-ID");
//				a.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
//				a.addParameter(ParameterTypes.UserName, LASAD_Client.getInstance().getUsername());
//
//				if (childElement.getElementType().equals("rating")) {
//					a.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
//				} else if (childElement.getElementType().equals("awareness")) {
//					a.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will be
//
//					// filled in by the
//					// server
//				}
//
//				if (a != null) {
//					allActions.add(a);
//				}
//			}
//		}
//		return allActions;
//	}
	
//	private Vector<Action> createLinkWithElementsAction(ElementInfo info, int mapID, String startID, String endID, Vector<ExtendedElement> existingChildElements) {
//
//		Vector<Action> resultingActions = new Vector<Action>();
//
//		Action a = new Action(Commands.CreateElement, Categories.Map);
//		a.addParameter(ParameterTypes.Type, info.getElementType());
//		a.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
//		a.addParameter(ParameterTypes.ElementId, String.valueOf(info.getElementID()));
//		a.addParameter(ParameterTypes.Parent, startID);
//		a.addParameter(ParameterTypes.Parent, endID);
//
//		resultingActions.add(a);
//
//		if (existingChildElements == null) {
//			Collection<ElementInfo> childElements = info.getChildElements().values();
//			for (ElementInfo childElement : childElements) {
//				Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG);
//
//				// Initializing loop variable and changing it in case there're
//				// no existing child elements
//				int quantity = childElement.getQuantity();
//				int i = 0;
//				while (i < quantity) {
//					Action b = new Action(Commands.CreateElement, Categories.Map);
//					b.addParameter(ParameterTypes.Type, childElement.getElementType());
//					b.addParameter(ParameterTypes.MapId, mapID + "");
//					b.addParameter(ParameterTypes.Parent, "LAST-ID");
//					b.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
//
//					if (childElement.getElementType().equals("rating")) {
//						b.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
//					} else if (childElement.getElementType().equals("awareness")) {
//						b.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will
//						// be filled in
//						// by the server
//					}
//					if (b != null) {
//						resultingActions.add(b);
//					}
//
//					i++;
//				}
//			}
//		} else {
//			for (ExtendedElement element : existingChildElements) {
//				Logger.log("Creating action for ChildElement: " + element.getConfig().getElementType(), Logger.DEBUG);
//				Action b = new Action(Commands.CreateElement, Categories.Map);
//				b.addParameter(ParameterTypes.Type, element.getConfig().getElementType());
//				b.addParameter(ParameterTypes.MapId, mapID + "");
//				b.addParameter(ParameterTypes.Parent, "LAST-ID");
//				b.addParameter(ParameterTypes.ElementId, String.valueOf(element.getConfig().getElementID()));
//
//				if (element.getConfig().getElementType().equals("rating")) {
//					b.addParameter(ParameterTypes.Score, element.getConfig().getElementOption(ParameterTypes.Score));
//				} else if (element.getConfig().getElementType().equals("text")) {
//					String text;
//					if (element.getConnectedModel().getValue(ParameterTypes.Text) == null) {
//						text = "";
//					} else {
//						text = element.getConnectedModel().getValue(ParameterTypes.Text);
//					}
//					b.addParameter(ParameterTypes.Text, text);
//				} else if (element.getConfig().getElementType().equals("awareness")) {
//					b.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will be
//					// filled in by the
//					// server
//				}
//				if (b != null) {
//					resultingActions.add(b);
//				}
//			}
//		}
//		return resultingActions;
//	}
	

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((PatternMVCViewSession)myMap.getMyViewSession()).getController().getDrawingAreaInfo().getElementsByType(type).values();
//		PatternDrawingAreaSpace patternDrawingAreaSpace = (PatternDrawingAreaSpace) myMap.getMyArgumentMapSpace();
//		return patternDrawingAreaSpace.getPatternInfo().getElementsByType(type).values();
	}

}
