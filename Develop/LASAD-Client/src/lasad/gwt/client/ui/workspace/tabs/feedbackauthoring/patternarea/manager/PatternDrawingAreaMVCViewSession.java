package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.pattern.BoxPattern;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.pattern.elements.ExtendedTextElementPattern;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.pattern.CurvedLinkPattern;
import lasad.gwt.client.ui.link.pattern.StraightLinkPattern;
import lasad.gwt.client.ui.workspace.awareness.argument.AwarenessCursorArgument;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMapSpace;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.util.Size;

/**
 * 
 * @author Anahuac
 *
 */
public class PatternDrawingAreaMVCViewSession extends PatternMVCViewSession {
	private PatternGraphMapSpace mapSpace;
	private GraphMap map;
	
	public PatternDrawingAreaMVCViewSession(PatternController controller, Size size) {
		super(controller);
		// Create View ArgumentMapSpace
		this.mapSpace = new PatternGraphMapSpace(this, size);
		if (mapSpace.getMyMap() instanceof GraphMap) {
			map = (GraphMap) mapSpace.getMyMap();
		}
	}
	
	@Override
	public void workOnDeleteElementModel(AbstractUnspecifiedElementModel model) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession][workOnDeleteElementModel]" + model.getType(), Logger.DEBUG);

		if (model.getType().equalsIgnoreCase("transcript-link")) {
			// Delete tData model from connected Box
			for (AbstractUnspecifiedElementModel parent : MVCHelper.getParentModelsByType(model, "box")) {
				for (MVCViewRecipient recipient : getMVCViewRecipientsByModel(parent)) {
					if (recipient instanceof AbstractBox) {
						((AbstractBox) recipient).setTData(null);
					}
				}
			}
		}
	}

	@Override
	public Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model) {
		Vector<MVCViewRecipient> newRecipients = new Vector<MVCViewRecipient>();
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession][workOnRegisterNewModel] : " + model.getType(), Logger.DEBUG);

		if (model.getType().equalsIgnoreCase("box") || model.getType().equalsIgnoreCase("emptybox")) {
			// CREATE A NEW box REPRESENTATION
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {
				// Model has actually no connected Box as a representative
//				SelectionDetailsPanel sdp = new SelectionDetailsPanel(map, this.getController().getDrawingAreaInfo().getElementsByType("box").get(model.getValue(ParameterTypes.ElementId)));
				
				BoxPattern reference = new BoxPattern(map, this.getController().getDrawingAreaInfo().getElementsByType("box").get(model.getValue(ParameterTypes.ElementId)), null, model.getIsReplay());
				reference.setOnlyAuthorCanModify( this.getController().getMapInfo().isOnlyAuthorCanModify() );
				reference.setCommitTextByEnter( this.getController().getMapInfo().isCommitTextByEnter() );
				
				map.add(reference);
				map.layout();
				newRecipients.add(reference);
//				newRecipients.add(sdp);

				// Centers the view the first time a box is created on the map
				if (model.getValue(ParameterTypes.JoinBox) != null && model.getValue(ParameterTypes.JoinBox).equals("true")) {
					reference.getMap().getLayoutTarget().dom.setScrollLeft(Integer.parseInt(model.getValue(ParameterTypes.PosX)) - reference.getMap().getInnerWidth() / 2 + reference.getWidth() / 2);
					reference.getMap().getLayoutTarget().dom.setScrollTop(Integer.parseInt(model.getValue(ParameterTypes.PosY)) - reference.getMap().getInnerHeight() / 2 + reference.getHeight() / 2);
				}
			}
		}
		else if (model.getType().equalsIgnoreCase("awareness-cursor")) {
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {

				if (model.getValue(ParameterTypes.UserName).equals(LASAD_Client.getInstance().getUsername())) {
					map.setMyAwarenessCursorID(Integer.parseInt(model.getValue(ParameterTypes.Id)));
					PatternGraphMap.mapIDtoCursorID.put(map.getID(), Integer.parseInt(model.getValue(ParameterTypes.Id)));
				} else {
					// Model has actually no connected Box as a representative
					AwarenessCursorArgument ac = new AwarenessCursorArgument(map, model.getValue(ParameterTypes.Id), model.getValue(ParameterTypes.UserName));

					map.add(ac);
					map.layout();

					newRecipients.add(ac);
				}
			}
		}
		else if (model.getType().equalsIgnoreCase("relation") || model.getType().equalsIgnoreCase("emptyrelation")) {
			// Create a new Relation between some elements

			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {

				// First at all, test the requirements: two representatives
				if (model.getParents().size() == 2) {
					// Get the graphical connectors
					AbstractUnspecifiedElementModel parentModel1 = model.getParents().get(0);
					AbstractUnspecifiedElementModel parentModel2 = model.getParents().get(1);

					// For Parent One
					// Box parentBox1 = null;
					Connector connector1 = null;
					for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parentModel1)) {
						if (recipient instanceof AbstractBox) {
							// All fine
							// parentBox1 = (Box)recipient;
							connector1 = ((AbstractBox) recipient).getConnector();
							break;
						} else if (recipient instanceof AbstractLink) {
							connector1 = ((AbstractLink) recipient).getConnector();
							break;
						}
					}

					// For Parent Two
					// Box parentBox2 = null;
					Connector connector2 = null;
					for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parentModel2)) {
						if (recipient instanceof AbstractBox) {
							// All fine
							// parentBox2 = (Box)recipient;
							connector2 = ((AbstractBox) recipient).getConnector();
							break;
						} else if (recipient instanceof AbstractLink) {
							connector2 = ((AbstractLink) recipient).getConnector();
							break;
						}
					}

					if (connector1 != null && connector2 != null) {
						// All Fine, create Graphical relation

						if (map == null)
							Logger.log("map == null", Logger.DEBUG_ERRORS);
						if (this.getController().getDrawingAreaInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)) == null)
							Logger.log("ElementInfo == null", Logger.DEBUG_ERRORS);

//						LinkPattern link = new LinkPattern(map, this.getController().getDrawingAreaInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay());
						
						AbstractLink link = (this.getController().getMapInfo().isStraightLink()) ?
								new StraightLinkPattern(map, this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay())
								:
								new CurvedLinkPattern(map, this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay());
						link.setOnlyAuthorCanModify( this.getController().getMapInfo().isOnlyAuthorCanModify() );
						link.setCommitTextByEnter(this.getController().getMapInfo().isCommitTextByEnter());
						

						map.add(link);
						map.layout();

						// ToDo: Set Endings dynamically
						link.setEndings(false, true);

						// Return new representative to the session instance
						newRecipients.add(link);
					}
				}
			}
		}
		else if (model.getType().equalsIgnoreCase("text")) {
			Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession]: add Textfield", Logger.DEBUG);
			// CREATE A NEW TEXTFIELD
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession]: add Textfield to a ExtendedElementContainer", Logger.DEBUG);
						
						ElementInfo currentElementInfo = ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId));

						// TODO Actually, that should never happen. However, it does... Check why!
						if(currentElementInfo == null) {
							continue;
						}
						if (("true").equalsIgnoreCase(currentElementInfo.getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {
							// Create textField
							ExtendedTextElementPattern textField = null;

							if (recipient instanceof SelectionDetailsPanel) {
								textField = new ExtendedTextElementPattern(model.getIsReplay(),(ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), true);
							} else {
								textField = new ExtendedTextElementPattern(model.getIsReplay(),(ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
							}

							Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession]: add Textfield : element was created!", Logger.DEBUG);

							// This is needed in case there are loaded
							// boxes/links with existing content
							String value;
							if ((value = model.getValue(ParameterTypes.Text)) != null) {
								if (model.getType().equalsIgnoreCase("text")) {
									model.setValue(ParameterTypes.Text, value);
								}
							}

							// Makes the Element Visible
							if (model.getValue(ParameterTypes.ElementId).equalsIgnoreCase("headline")) {
								((ExtendedElementContainerInterface) recipient).addExtendedElement(textField, 0);
							} else {
								((ExtendedElementContainerInterface) recipient).addExtendedElement(textField, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));
							}

							newRecipients.add(textField);
						}

						Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession]: add Textfield to an element. done!", Logger.DEBUG);
					}
				}
			}
		}
		else {
			Logger.log("[ArgumentMapMVCViewSession] Model type not recognized - " + model.getType(), Logger.DEBUG);
		}
		
		return newRecipients;
	}

	@Override
	public Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession][workOnUnregisterParent]",Logger.DEBUG);
		// Return a List of MVCViewRecipients, which has to be deleted
		if (child.getType().equals("TRANSCRIPTLINK")) {
			// If the box is deleted, all Transcript Representations has to be
			// deleted to
			return this.modelMapped2ViewRecipient.get(child);
		}
		return null;
	}

	private int calculateNewExtendedElementPosition(ExtendedElementContainerInterface container, String newChildElementID) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession][calculateNewExtendedElementPosition]",Logger.DEBUG);
		if (container.getExtendedElements().size() == 0) {
			return 0;
		}

		// Calculate ElementID before
		String beforeElementID = "";
		for (String tempElementID : container.getElementInfo().getChildElements().keySet()) {
			if (!tempElementID.equals(newChildElementID)) {
				for (AbstractExtendedElement tempElement : container.getExtendedElements()) {
					if (tempElement.getConnectedModel().getElementid().equals(tempElementID)) {
						beforeElementID = tempElementID;
						break;
					}
				}
			} else {
				break;
			}
		}

		// Calculate new Position
		int pos = 0;
		boolean elementIDreached = false;
		boolean beforeElementIDreached = false;
		for (AbstractExtendedElement tempElement : container.getExtendedElements()) {
			String tempElementID = tempElement.getConnectedModel().getElementid();

			if (beforeElementID.equals(tempElementID)) {
				beforeElementIDreached = true;
				pos++;
				continue;
			}

			if (beforeElementIDreached && !tempElementID.equals(beforeElementID)) {
				break;
			}

			if (tempElementID.equals(newChildElementID)) {
				elementIDreached = true;
				pos++;
				continue;
			}

			if (elementIDreached && !tempElementID.equals(newChildElementID)) {
				break;
			}

			pos++;
		}
		return pos;
	}
	
	public PatternGraphMapSpace getArgumentMapSpace()
	{
		return this.mapSpace;
	}
	
	public void releaseArgumentMapListeners()
	{
		this.map.releaseAllListeners();
	}
//	public void forceLayout(){
//		map.layout(true);
//	}
	
}
