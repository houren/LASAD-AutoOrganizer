package lasad.gwt.client.ui.workspace.argumentmap;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.argument.BoxArgument;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.argument.elements.ExtendedAwarenessElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedDropdownElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedImageElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedRadioButtonElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedRatingElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedSpotsElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedTextElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedTranscriptElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedUFrameElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedURLElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedXmppMessageButtonElementArgument;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.argument.CurvedLinkArgument;
import lasad.gwt.client.ui.link.argument.StraightLinkArgument;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.awareness.argument.AwarenessCursorArgument;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackCluster;
import lasad.gwt.client.ui.workspace.feedback.argument.FeedbackClusterArgument;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.MapTab;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * This class handles all "visible" actions, i.e. whenever an element is
 * manipulated, this class provide the visual results.
 */
public class ArgumentMapMVCViewSession extends MVCViewSession {

	private MapTab mapTab;
	private ArgumentMapSpace mapSpace;
	private GraphMap map;

	public ArgumentMapMVCViewSession(MVController controller, MapTab mapTab) {
		super(controller);
		this.mapTab = mapTab;

		// Create View ArgumentMapSpace
		this.mapSpace = new ArgumentMapSpace(this, mapTab.getSize());
		// add by Erkang.
		if (mapSpace.getMyMap() instanceof GraphMap) {
			map = (GraphMap) mapSpace.getMyMap();
		}

		// add MapSpace to the MapTab
		this.mapTab.add(mapSpace);
		this.mapTab.setMyMapSpace(mapSpace);
		this.mapTab.layout();

		// Work on the Transcript
		LinkedHashMap<Integer, String> transcriptLines = controller.getMapInfo().getTranscriptLines();
		if (transcriptLines != null) {
			// Transcript exists, add it to the Workspace
			mapSpace.setTranscript(true);
			mapSpace.getTranscript().setTranscriptRows(transcriptLines);
		}

		// Work on Tutorial
		if (controller.getMapInfo().getTutorialConfig() != null) {
			// Tutorial
			mapSpace.setTutorial(true);
		}

		// Work on Feedback
		if (controller.getMapInfo().isFeedback()) {
			mapSpace.setFeedback(true);
		} else {
			mapSpace.setFeedback(false);
		}

		// Work on Chat
		if (controller.getMapInfo().isChatSystem()) {
			
			if(controller.getMapInfo().isSentenceOpener()) {
				mapSpace.setSentenceOpener(true,controller.getMapInfo().getSentenceOpenerConfig());
							
			}
			else
			{
			mapSpace.setChat(true);
			}
			//mapSpace.setChat(true);
		} else {
			mapSpace.setChat(false);
		}

		// Is direct linking (i.e. drop from box into white space to create box and link in one step) denied?
		if (controller.getMapInfo().isDirectLinkingDenied()) {
			mapSpace.setDirectLinkingDenied(true);
		} else {
			mapSpace.setDirectLinkingDenied(false);
		}
		
		// Work on Selection Details
		if (controller.getMapInfo().isSelectionDetails()) {
			mapSpace.setSelectionDetails(true);
		} else {
			mapSpace.setSelectionDetails(false);
		}

		// Work on UserList
		if (controller.getMapInfo().isUserList()) {
			mapSpace.setUserList(true);
		} else {
			mapSpace.setUserList(false);
		}

		// Work on MiniMap
		if (controller.getMapInfo().isMiniMap()) {
			mapSpace.setMiniMap(true);
		} else {
			mapSpace.setMiniMap(false);
		}	
		
		// Enable / Disable tracking of cursor movements
		if (controller.getMapInfo().isTrackCursor()) {
			mapSpace.setTrackCursor(true);
		} else {
			mapSpace.setTrackCursor(false);
		}
	}

	@Override
	public void workOnDeleteElementModel(AbstractUnspecifiedElementModel model) {
		Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: workOnDeleteElementModel : " + model.getType(), Logger.DEBUG);

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
		Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: workOnRegisterNewModel : " + model.getType(), Logger.DEBUG);

		if (model.getType().equalsIgnoreCase("box") || model.getType().equalsIgnoreCase("emptybox")) {
			// CREATE A NEW box REPRESENTATION
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {
				// Model has actually no connected Box as a representative
				SelectionDetailsPanel sdp = new SelectionDetailsPanel(map, this.getController().getMapInfo().getElementsByType("box").get(model.getValue(ParameterTypes.ElementId)));
				BoxArgument reference = new BoxArgument(map, this.getController().getMapInfo().getElementsByType("box").get(model.getValue(ParameterTypes.ElementId)), sdp, model.getIsReplay());
				
				reference.setOnlyAuthorCanModify( this.getController().getMapInfo().isOnlyAuthorCanModify() );
				reference.setCommitTextByEnter( this.getController().getMapInfo().isCommitTextByEnter() );
				map.add(reference);
				map.layout();
				newRecipients.add(reference);
				newRecipients.add(sdp);

				// Centers the view the first time a box is created on the map
				if (model.getValue(ParameterTypes.JoinBox) != null && model.getValue(ParameterTypes.JoinBox).equals("true")) {
					reference.getMap().getLayoutTarget().dom.setScrollLeft(Integer.parseInt(model.getValue(ParameterTypes.PosX)) - reference.getMap().getInnerWidth() / 2 + reference.getWidth() / 2);
					reference.getMap().getLayoutTarget().dom.setScrollTop(Integer.parseInt(model.getValue(ParameterTypes.PosY)) - reference.getMap().getInnerHeight() / 2 + reference.getHeight() / 2);
				}
			}
		} else if (model.getType().equalsIgnoreCase("awareness-cursor")) {
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {

				if (model.getValue(ParameterTypes.UserName).equals(LASAD_Client.getInstance().getUsername())) {
					map.setMyAwarenessCursorID(Integer.parseInt(model.getValue(ParameterTypes.Id)));
					ArgumentMap.mapIDtoCursorID.put(map.getID(), Integer.parseInt(model.getValue(ParameterTypes.Id)));
				} else {
					// Model has actually no connected Box as a representative
					AwarenessCursorArgument ac = new AwarenessCursorArgument(map, model.getValue(ParameterTypes.Id), model.getValue(ParameterTypes.UserName));

					map.add(ac);
					map.layout();

					newRecipients.add(ac);
				}
			}
		}

		else if (model.getType().equalsIgnoreCase("group-cursor")) {
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {

				// If there is no group pointer on this map yet...
				if (map.getMyAwarenessCursorID() == -1) {
					AwarenessCursorArgument ac = new AwarenessCursorArgument(map, model.getValue(ParameterTypes.Id), true);

					map.add(ac);
					map.layout();

					newRecipients.add(ac);
				}
			}
		}

		else if (model.getType().equalsIgnoreCase("feedback-cluster")) {
			if (!this.modelMapped2ViewRecipient.containsKey(model) || this.modelMapped2ViewRecipient.get(model).size() == 0) {

				AbstractFeedbackCluster fc = null;
				boolean responseReq = false;

				String responseParam = model.getValue(ParameterTypes.ResponseRequired);
				if (responseParam != null) {
					if (responseParam.equalsIgnoreCase("TRUE")) {
						responseReq = true;
					}
				}

				if (model.getValue(ParameterTypes.PosX) != null && model.getValue(ParameterTypes.PosY) != null) {
					if (model.getValue(ParameterTypes.Details) != null) {
						fc = new FeedbackClusterArgument(map, model.getId(), model.getValue(ParameterTypes.Message), model.getValue(ParameterTypes.Details), model.getValue(ParameterTypes.PosX), model.getValue(ParameterTypes.PosY), responseReq);
					} else {
						fc = new FeedbackClusterArgument(map, model.getId(), model.getValue(ParameterTypes.Message), model.getValue(ParameterTypes.PosX), model.getValue(ParameterTypes.PosY), responseReq);
					}
				} else {
					if (model.getValue(ParameterTypes.Details) != null) {
						fc = new FeedbackClusterArgument(map, model.getId(), model.getValue(ParameterTypes.Message), model.getValue(ParameterTypes.Details), 0 + "", 0 + "", responseReq);
					} else {
						fc = new FeedbackClusterArgument(map, model.getId(), model.getValue(ParameterTypes.Message), 0 + "", 0 + "", responseReq);
					}
				}

				map.add(fc);
				map.layout();

				newRecipients.add(fc);
			}
		}

		else if (model.getType().equalsIgnoreCase("relation") || model.getType().equalsIgnoreCase("emptyrelation")) {

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
							Logger.log("map = null", Logger.DEBUG_ERRORS);
						if (this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)) == null)
							Logger.log("ElementInfo = null", Logger.DEBUG_ERRORS);

//						LinkArgument link = new LinkArgument(map, this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay());
						
						AbstractLink link = (this.getController().getMapInfo().isStraightLink()) ?
								new StraightLinkArgument(map, this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay())
								:
								new CurvedLinkArgument(map, this.getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId)), connector1, connector2, model.getIsReplay());
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
		} else if (model.getType().equalsIgnoreCase("transcript-link")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add TranscriptLink", Logger.DEBUG);

			int count = 0;

			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {

					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add TranscriptLink to a ExtendedElementContainer", Logger.DEBUG);

						
						if (model.getValue(ParameterTypes.Text) != null) {
							String[] values = model.getValue(ParameterTypes.Text).split(",");
							model.setValue(ParameterTypes.StartRow, values[0]);
							model.setValue(ParameterTypes.StartPoint, values[1]);
							model.setValue(ParameterTypes.EndRow, values[2]);
							model.setValue(ParameterTypes.EndPoint, values[3]);
						}
						
						//Because the transcript concept changed 
						//from char-based to line-based the STARTPOINT 
						//and ENDPOINT must be set to zero
						model.setValue(ParameterTypes.StartPoint, "0");
						model.setValue(ParameterTypes.EndPoint, "0");


						// Create Transcript Link Data
						try{
							TranscriptLinkData tData = new TranscriptLinkData(mapSpace.getTranscript(), Integer.parseInt(model.getValue(ParameterTypes.StartRow)), Integer.parseInt(model.getValue(ParameterTypes.StartPoint)), Integer.parseInt(model.getValue(ParameterTypes.EndRow)), Integer.parseInt(model.getValue(ParameterTypes.EndPoint)));

							tData.setElementID(parent.getId());
							tData.setModel(model);

							tData.setStyle("trans-highlight");

							// Update Box
							if (recipient instanceof AbstractBox) {
								tData.setColor(((AbstractBox) recipient).getColor());
								((AbstractBox) recipient).setTData(tData);
							} else if (recipient instanceof SelectionDetailsPanel) {
								tData.setColor(((SelectionDetailsPanel) recipient).getColor());
								((SelectionDetailsPanel) recipient).setTData(tData);
							}

							// Create the concrete transcript element
							ExtendedTranscriptElementArgument transcriptLink = new ExtendedTranscriptElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), tData);
							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add TranscriptLink : element was created!", Logger.DEBUG);

							// Makes the element visible
							((ExtendedElementContainerInterface) recipient).addExtendedElement(transcriptLink, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));

							newRecipients.add(transcriptLink);
							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add TranscriptLink to a Link. done!", Logger.DEBUG);

							// Add the Transcript itselfs as a subscriber
							// Create Transcript Link Data
							tData = new TranscriptLinkData(mapSpace.getTranscript(), Integer.parseInt(model.getValue(ParameterTypes.StartRow)), Integer.parseInt(model.getValue(ParameterTypes.StartPoint)), Integer.parseInt(model.getValue(ParameterTypes.EndRow)), Integer.parseInt(model.getValue(ParameterTypes.EndPoint)));

							tData.setElementID(parent.getId());
							tData.setModel(model);

							if (recipient instanceof AbstractBox) {
								tData.setColor(((AbstractBox) recipient).getColor());
							} else if (recipient instanceof SelectionDetailsPanel) {
								tData.setColor(((SelectionDetailsPanel) recipient).getColor());
							}

							tData.setStyle("trans-highlight");

							if (count < 1) {
								mapSpace.getTranscript().addTranscriptLink(tData);
								newRecipients.add(mapSpace.getTranscript());
							}
							count++;
						} catch (Exception e) {
							LASADInfo.display("Parsing error", "A certain transcript link couldn't be created.");
						}

					}
				}
			}
		} else if (model.getType().equalsIgnoreCase("text")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Textfield", Logger.DEBUG);
			// CREATE A NEW TEXTFIELD
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Textfield to a ExtendedElementContainer", Logger.DEBUG);
						
						ElementInfo currentElementInfo = ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId));

						// TODO Actually, that should never happen. However, it does... Check why!
						if(currentElementInfo == null) {
							continue;
						}
						if (("true").equalsIgnoreCase(currentElementInfo.getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {
							// Create textField
							ExtendedTextElementArgument textField = null;

							if (recipient instanceof SelectionDetailsPanel) {
								textField = new ExtendedTextElementArgument(model.getIsReplay(),(ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), true);
							} else {
								textField = new ExtendedTextElementArgument(model.getIsReplay(),(ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
							}

							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Textfield : element was created!", Logger.DEBUG);

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

						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Textfield to a Link. done!", Logger.DEBUG);
					}
				}
			}
		} else if (model.getType().equalsIgnoreCase("dropdown")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Dropdown", Logger.DEBUG);
			// CREATE A NEW Dropdown
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Dropdown to a ExtendedElementContainer", Logger.DEBUG);

						// Create dropdown
						ExtendedDropdownElementArgument dropdown = new ExtendedDropdownElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)),model.getIsReplay());
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Dropdown : element was created!", Logger.DEBUG);

						// Makes the Element Visible
						if (model.getValue(ParameterTypes.ElementId).equalsIgnoreCase("headline")) {
							((ExtendedElementContainerInterface) recipient).addExtendedElement(dropdown, 0);
						} else {
							((ExtendedElementContainerInterface) recipient).addExtendedElement(dropdown, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));
						}

						newRecipients.add(dropdown);
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Dropdown to a Link. done!", Logger.DEBUG);
					}
				}
			}
		} else if (model.getType().equalsIgnoreCase("awareness")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Awareness-Field", Logger.DEBUG);
			// CREATE A NEW AWARENESS FIELD
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add awareness-field to an ExtendedElementContainer", Logger.DEBUG);

						if (("true").equalsIgnoreCase(((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {
							// Create awarenessField
							ExtendedAwarenessElementArgument awarenessField = null;

							if (model.getValue(ParameterTypes.Text) != null) {
								awarenessField = new ExtendedAwarenessElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), model.getValue(ParameterTypes.Text));
							} else if (model.getValue(ParameterTypes.Time) != null) {
								if (model.getValue(ParameterTypes.UserName) != null) {
									Date date = new Date(Long.parseLong(model.getValue(ParameterTypes.Time)));
									String timeString = date.toString();
									timeString = timeString.substring(4, 16);
									String authorAndTime = model.getValue(ParameterTypes.UserName) + ", " + timeString;

									awarenessField = new ExtendedAwarenessElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), authorAndTime);
								}
							}
							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Awareness-Field : predefined element (with text: " + model.getValue(ParameterTypes.Text) + " was created!", Logger.DEBUG);

							// Makes the Element Visible
							((ExtendedElementContainerInterface) recipient).addExtendedElement(awarenessField, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));

							newRecipients.add(awarenessField);

							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add AwarenessElement to a Box. done!", Logger.DEBUG);
						}
					}
				}
			}
		} else if (model.getType().equalsIgnoreCase("rating")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Rating-Field", Logger.DEBUG);
			// CREATE A NEW RATING ELEMENT
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {

						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add rating-field to an ExtendedElementContainer", Logger.DEBUG);

						if (("true").equalsIgnoreCase(((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {

							ExtendedRatingElementArgument ratingElement = null;
							ratingElement = new ExtendedRatingElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));

							// Makes the Element Visible
							((ExtendedElementContainerInterface) recipient).addExtendedElement(ratingElement, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));

							newRecipients.add(ratingElement);

							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Rating-Element to a box done!", Logger.DEBUG);
						}
						if (model.getValue(ParameterTypes.Text) != null) {
							model.setValue(ParameterTypes.Score, model.getValue(ParameterTypes.Text));
						}
					}
				}
			}
		} 
		// Adding the Radiobutton element to box element
		
		else if(model.getType().equalsIgnoreCase("radiobtn")){
			
			   Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Radiobutton-Field",Logger.DEBUG);
			    // CREATE A NEW RATING ELEMENT
			    for(AbstractUnspecifiedElementModel parent : model.getParents()){
				for(MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)){
				    if(recipient instanceof ExtendedElementContainerInterface){
				    	
					Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add rating-field to an ExtendedElementContainer",Logger.DEBUG);

					if(("true").equalsIgnoreCase(((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {	
						
					}
					else {
						
			
						ExtendedRadioButtonElementArgument rad=null;
			rad= new ExtendedRadioButtonElementArgument (model.getIsReplay(),(ExtendedElementContainerInterface)recipient,
					((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
			
			((ExtendedElementContainerInterface)recipient).addExtendedElement(rad,calculateNewExtendedElementPosition(((ExtendedElementContainerInterface)recipient), model.getValue(ParameterTypes.ElementId)));

			newRecipients.add(rad);
					   }
				    }
				}
			    }
		
				}
			
		// Adding the Image element to box element
		
		else if(model.getType().equalsIgnoreCase("image")){
			
			   Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add  Image -Field",Logger.DEBUG);
			    // CREATE A NEW IMAGE ELEMENT
			    for(AbstractUnspecifiedElementModel parent : model.getParents()){
				for(MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)){
				    if(recipient instanceof ExtendedElementContainerInterface){
				    	
					Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add image-field to an ExtendedElementContainer",Logger.DEBUG);

					if(("true").equalsIgnoreCase(((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {	
						
					}
					else {
						
			
						ExtendedImageElementArgument image=null;
			image= new ExtendedImageElementArgument (model.getIsReplay(),(ExtendedElementContainerInterface)recipient,
					((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
			
			((ExtendedElementContainerInterface)recipient).addExtendedElement(image,calculateNewExtendedElementPosition(((ExtendedElementContainerInterface)recipient), model.getValue(ParameterTypes.ElementId)));

			newRecipients.add(image);
					   }
				    }
				}
	    	}
		
		}
		
		else if (model.getType().equalsIgnoreCase("frame")) {
			AbstractExtendedElement element = createFrameElementAndAddToAllRecipients(model);
			if (element != null){
				newRecipients.add(element);
			}
		}
		else if (model.getType().equalsIgnoreCase("xmppMessageButton")) {
			AbstractExtendedElement element = createXmppMessageButtonElementAndAddToAllRecipients(model);
			if (element != null){
				newRecipients.add(element);
			}
		}
		else if (model.getType().equalsIgnoreCase("url")) {
			// CREATE A NEW URL ELEMENT

			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add URL Element", Logger.DEBUG);

			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {
						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add URL Element to an ExtendedElementContainer", Logger.DEBUG);

						if (("true").equalsIgnoreCase(((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {
							// Create URL Element
//							ExtendedFrameElement urlElement = new ExtendedFrameElement((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
							ExtendedURLElementArgument urlElement = new ExtendedURLElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add URL Element : element was created!", Logger.DEBUG);

							// Load saved value if necessary
							if (model.getValue(ParameterTypes.Text) != null) {
								model.setValue(ParameterTypes.Link, model.getValue(ParameterTypes.Text));
							}

							// Makes the Element Visible
							((ExtendedElementContainerInterface) recipient).addExtendedElement(urlElement, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));

							newRecipients.add(urlElement);
							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add URL Element to a Container. done!", Logger.DEBUG);
						}
					}
				}
			}
		} else if (model.getType().equalsIgnoreCase("spots")) {
			Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Spots-Field", Logger.DEBUG);
			// CREATE A NEW SPOTS FIELD
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				for (MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)) {
					if (recipient instanceof ExtendedElementContainerInterface) {

						Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add spots-field to an ExtendedElementContainer", Logger.DEBUG);

						if (("true").equalsIgnoreCase(((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {

						} else {
							// Create spotsField
							ExtendedSpotsElementArgument spotsField = null;

							spotsField = new ExtendedSpotsElementArgument((ExtendedElementContainerInterface) recipient, ((ExtendedElementContainerInterface) recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)), 0, 0);

							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add Spots-Field : predefined element was created!", Logger.DEBUG);

							// Makes the Element Visible
							((ExtendedElementContainerInterface) recipient).addExtendedElement(spotsField, calculateNewExtendedElementPosition(((ExtendedElementContainerInterface) recipient), model.getValue(ParameterTypes.ElementId)));

							newRecipients.add(spotsField);

							Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add spots-element to a box. done!", Logger.DEBUG);
						}
					}
				}
			}
		}
		else {
			Logger.log("[ArgumentMapMVCViewSession] Model type not recognized - " + model.getType(), Logger.DEBUG);
		}
		
		return newRecipients;
	}
	
	public AbstractExtendedElement createFrameElementAndAddToAllRecipients(AbstractUnspecifiedElementModel model){
		Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add  field",Logger.DEBUG);
		for(AbstractUnspecifiedElementModel parent : model.getParents()){
			for(MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)){
				if(recipient instanceof ExtendedElementContainerInterface){
			    	Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add field to an ExtendedElementContainer",Logger.DEBUG);
			    	//?
			    	if(("true").equalsIgnoreCase(((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)).getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {	
					}
			    	else {
			    		AbstractExtendedElement frameElement=null;
					//	frameElement = new ExtendedFrameElementOld ((ExtendedElementContainerInterface)recipient,
							//	((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
					frameElement = new ExtendedUFrameElementArgument ((ExtendedElementContainerInterface)recipient,
								((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
						((ExtendedElementContainerInterface)recipient).addExtendedElement(frameElement,
								calculateNewExtendedElementPosition(((ExtendedElementContainerInterface)recipient), model.getValue(ParameterTypes.ElementId)));
						return frameElement;
				   }
			    }
			}
		}
		return null;
	}
	
	public AbstractExtendedElement createXmppMessageButtonElementAndAddToAllRecipients(AbstractUnspecifiedElementModel model){
		Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add  field",Logger.DEBUG);
		for(AbstractUnspecifiedElementModel parent : model.getParents()){
			for(MVCViewRecipient recipient : this.modelMapped2ViewRecipient.get(parent)){
				if(recipient instanceof ExtendedElementContainerInterface){
			    	Logger.log("[lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession]: add field to an ExtendedElementContainer",Logger.DEBUG);
			    	//?
			    	boolean shouldCreateElement = true;
			    	ExtendedElementContainerInterface elRec = (ExtendedElementContainerInterface)recipient;
			    	String elId = model.getValue(ParameterTypes.ElementId);
			    	ElementInfo elInfo = elRec.getElementInfo();
			    	if (elId!= null && elInfo != null){
			    		ElementInfo elInfo2 = elInfo.getChildElements().get(elId);
			    		if (elInfo2 != null && elInfo2.getElementOption(ParameterTypes.DetailsOnly) != null){
			    			if(("true").equalsIgnoreCase(elInfo2.getElementOption(ParameterTypes.DetailsOnly)) && recipient instanceof AbstractBox) {	
			    				shouldCreateElement = false;
			    			}
			    		}
			    	}
			    	if (shouldCreateElement) {
			    		AbstractExtendedElement xmppMessageElement=null;
						xmppMessageElement = new ExtendedXmppMessageButtonElementArgument ((ExtendedElementContainerInterface)recipient,
								((ExtendedElementContainerInterface)recipient).getElementInfo().getChildElements().get(model.getValue(ParameterTypes.ElementId)));
						((ExtendedElementContainerInterface)recipient).addExtendedElement(xmppMessageElement,
								calculateNewExtendedElementPosition(((ExtendedElementContainerInterface)recipient), model.getValue(ParameterTypes.ElementId)));
						return xmppMessageElement;
				   }
			    }
			}
		}
		return null;
	}

	@Override
	public Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		// Return a List of MVCViewRecipients, which has to be deleted
		if (child.getType().equals("TRANSCRIPTLINK")) {
			// If the box is deleted, all Transcript Representations has to be
			// deleted to
			return this.modelMapped2ViewRecipient.get(child);
		}
		return null;
	}

	private int calculateNewExtendedElementPosition(ExtendedElementContainerInterface container, String newChildElementID) {

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
	
	public ArgumentMapSpace getArgumentMapSpace()
	{
		return this.mapSpace;
	}
	
	public void releaseArgumentMapListeners()
	{
		this.map.releaseAllListeners();
	}
}