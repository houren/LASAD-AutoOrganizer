package lasad.gwt.client.ui.workspace.tableview;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.awareness.argument.AwarenessCursorArgument;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.tableview.argument.MapTableArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.TableCellArgument;
import lasad.gwt.client.ui.workspace.tabs.MapTab;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;



public class ArgumentMapTableMVCViewSession extends MVCViewSession{

	private ArgumentMapSpace mapSpace;
	private MapTableArgument map;
	
	public ArgumentMapTableMVCViewSession(MVController controller, MapTab mapTab) {
		super(controller);
		
		this.mapSpace = new ArgumentMapSpace(this, mapTab.getSize());
		
		if (mapSpace.getMyMap() instanceof MapTableArgument) {
			this.map = (MapTableArgument)mapSpace.getMyMap();
		}
		
	
		// add MapSpace to the MapTab
		mapTab.add(mapSpace);
		mapTab.setMyMapSpace(mapSpace);
		mapTab.layout();
		
		// later this will be refactored

		// Work on the Transcript
		LinkedHashMap<Integer, String> transcriptLines = controller.getMapInfo().getTranscriptLines();
		if(transcriptLines != null){
			// Transcript exists, add it to the Workspace
			mapSpace.setTranscript(true);
			mapSpace.getTranscript().setTranscriptRows(transcriptLines);
		}

		// Work on Tutorial
		if(controller.getMapInfo().getTutorialConfig()!=null) {
			// Tutorial
			mapSpace.setTutorial(true);
		}

		// Work on Feedback
		if(controller.getMapInfo().isFeedback()) {
			mapSpace.setFeedback(true);
		}
		else {
			mapSpace.setFeedback(false);
			
		}

		// Work on Chat
		if(controller.getMapInfo().isChatSystem()) {
			
			
			if(controller.getMapInfo().isSentenceOpener()) {
				mapSpace.setSentenceOpener(true,controller.getMapInfo().getSentenceOpenerConfig());
							
			}
			else
			{
			mapSpace.setChat(true);
			}

		}
		else {
			mapSpace.setChat(false);
		}
		
		
		// Work on SentenceOpener
		
		
		

		// Work on UserList
		if(controller.getMapInfo().isUserList()) {
			mapSpace.setUserList(true);
		}
		else {
			mapSpace.setUserList(false);
		}
		
		// Work on MiniMap + Anchor
		if(controller.getMapInfo().isMiniMap()) {
			mapSpace.setMiniMap(true);
		}
		else {
			mapSpace.setMiniMap(false);
		}	

		// Enable / Disable tracking of cursor movements
		if(controller.getMapInfo().isTrackCursor()) {
			mapSpace.setTrackCursor(true);
		}
		else {
			mapSpace.setTrackCursor(false);
		}
		
	}
	
	@Override
	public Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model) {
		Vector<MVCViewRecipient> recipients = new Vector<MVCViewRecipient>();

		Logger.log("===== TabelViewSession.workOnRegisterNewModel model.getType() "+ model.getType() + " =====", Logger.DEBUG_DETAILS);

		String type = model.getType();
		
		//may be can with Factory dynamic setup
		
		if ("box".equalsIgnoreCase(type)) {
			
			ElementInfo info = getController().getMapInfo().getElementsByType("box").get(model.getValue(ParameterTypes.ElementId));
			
			List<MVCViewRecipient> cells = map.addElement(info);
//			map.fillTableCell();
			
			recipients.addAll(cells);
			
		} else if ("text".equalsIgnoreCase(type)) {
			
			Logger.log("====== TableViewSession.getParents = " + model.getParents().size() + " ======", Logger.DEBUG_DETAILS);
			
			// CREATE A NEW TEXTFIELD
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
					
					if (recipient instanceof CellContainer) {
						
						CellContainer container = (CellContainer)recipient;
						
						MVCViewRecipient cellComponent = container.addComponent(type, model.getValue(ParameterTypes.ElementId));
					
						recipients.add(cellComponent);
					
					}
				}
			}
			
		} else if ("awareness".equalsIgnoreCase(type)) {
			
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
					
					if (recipient instanceof CellContainer) {
						
						CellContainer container = (CellContainer)recipient;
						
						MVCViewRecipient cellComponent = container.addComponent(type, model.getValue(ParameterTypes.ElementId));
						
						recipients.add(cellComponent);
					}
				}
			}
			
			
		} else if("relation".equalsIgnoreCase(type)){
			
			if (!modelMapped2ViewRecipient.containsKey(model) || modelMapped2ViewRecipient.get(model).size() == 0) {

				if (model.getParents().size() == 2){
					
					ElementInfo info = getController().getMapInfo().getElementsByType("relation").get(model.getValue(ParameterTypes.ElementId));
					
					List<TableCellInfo> cellInfos = new ArrayList<TableCellInfo>();
					
					AbstractUnspecifiedElementModel parent1 = model.getParents().get(0);
					AbstractUnspecifiedElementModel parent2 = model.getParents().get(1);
					
					// row Cell
					for (MVCViewRecipient recipient: modelMapped2ViewRecipient.get(parent1)) {
						
						if (recipient instanceof CellContainer){
							
							TableCellInfo cellInfo = ((CellContainer)recipient).getTableCellInfo();
							int rowNumber = cellInfo.getRowNumber();
							int colNumber = cellInfo.getColNumber();
							
							if (rowNumber != 0 && colNumber == 0) {
								cellInfos.add(cellInfo);
								break;
							}
						}
					}
					
					
					List<MVCViewRecipient> cells = new ArrayList<MVCViewRecipient>();
					
					for (MVCViewRecipient recipient: modelMapped2ViewRecipient.get(parent2)) {
						
						if (recipient instanceof CellContainer){
							
							CellContainer cellContainer = (CellContainer)recipient;
							
							if (TableCellTypeEnum.BOX == cellContainer.getType()) {
								
								TableCellInfo cellInfo = ((CellContainer)recipient).getTableCellInfo();
								int rowNumber = cellInfo.getRowNumber();
								int colNumber = cellInfo.getColNumber();
								
								if (rowNumber == 0 && colNumber!= 0) {
									cellInfos.add(cellInfo);
									
									cells = map.addRelation(info, cellInfos);
									
									break;
								}
								
							} else {
								
								cells = map.addRelatedRelation(info);
								break;
							}
							
						}
					}
					
					recipients.addAll(cells);
					
				}
			}
			
		
		} else if ("rating".equalsIgnoreCase(type)) {
			
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
					
					if (recipient instanceof CellContainer) {
						
						CellContainer container = (CellContainer)recipient;
						
						MVCViewRecipient cellComponent = container.addComponent(type, model.getValue(ParameterTypes.ElementId));
						
						recipients.add(cellComponent);
					}
				}
			}
			
		} else if ("url".equalsIgnoreCase(type)) {
			
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
					
					if (recipient instanceof CellContainer) {
						
						CellContainer container = (CellContainer)recipient;
						
						MVCViewRecipient cellComponent = container.addComponent(type, model.getValue(ParameterTypes.ElementId));
						
						recipients.add(cellComponent);
					}
				}
			}
		
		} else if ("transcript-link".equalsIgnoreCase(type)) {
			
			for (AbstractUnspecifiedElementModel parent : model.getParents()) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
					
					if (recipient instanceof CellContainer) {
						
						// Create Transcript Link Data
						TranscriptLinkData tData = new TranscriptLinkData(
								mapSpace.getTranscript(),
								Integer.parseInt(model.getValue(ParameterTypes.StartRow)),
								Integer.parseInt(model.getValue(ParameterTypes.StartPoint)),
								Integer.parseInt(model.getValue(ParameterTypes.EndRow)),
								Integer.parseInt(model.getValue(ParameterTypes.EndPoint))
						);

						tData.setElementID(parent.getId());
						tData.setModel(model);
						tData.setColor(((TableCellArgument)recipient).getColor());
						tData.setStyle("trans-highlight");
					
						
						
						CellContainer container = (CellContainer)recipient;
						
						MVCViewRecipient cellComponent = container.addComponent(type, model.getValue(ParameterTypes.ElementId));
						
						recipients.add(cellComponent);
						
						
						mapSpace.getTranscript().addTranscriptLink(tData);
						recipients.add(mapSpace.getTranscript());
					}
				}
			}
		
		} else if ("awareness-cursor".equalsIgnoreCase(type)) { 
			
			if(!modelMapped2ViewRecipient.containsKey(model) || modelMapped2ViewRecipient.get(model).size() == 0) {

				if(model.getValue(ParameterTypes.UserName).equals(LASAD_Client.getInstance().getUsername())) {
					
					map.setMyAwarenessCursorID(Integer.parseInt(model.getValue(ParameterTypes.Id)));
					
					AbstractGraphMap.mapIDtoCursorID.put(map.getID(), Integer.parseInt(model.getValue(ParameterTypes.Id)));
				
				} else {
					// Model has actually no connected Box as a representative
					AwarenessCursorArgument ac = new AwarenessCursorArgument(map, model.getValue(ParameterTypes.Id), model.getValue(ParameterTypes.UserName));

					map.add(ac, new AbsoluteData(1, 1));
					map.layout();

					recipients.add(ac);
				}
			}
			
		} else {
			
		}
		
		
//		map.resize(TableZoomEnum.SIZE100);
		
		return recipients;
	}
	

	@Override
	public void workOnDeleteElementModel(AbstractUnspecifiedElementModel model) {
		
		if (model.getParents() != null && model.getParents().size() > 0) {
			
			
			//remove childComponents
			if (model.getParents().size() == 1) {
				
				for (AbstractUnspecifiedElementModel parent : model.getParents()) {
					
					for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(parent)) {
						
						if (recipient instanceof CellContainer) {
							
							CellContainer container = (CellContainer)recipient;
							
							container.removeComponent(model);
							
						}
					}
				}
				
			}
			
			
			//remove relations
			if (model.getParents().size() == 2) {
				
				for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(model)) {
						
					if (recipient instanceof TableCellArgument) {
						
						TableCellArgument relation = (TableCellArgument)recipient;
						((MapTableArgument)map).removeRelation(relation);
						
					}
					
					if (recipient instanceof AbstractRelatedRelation) {
						
						AbstractRelatedRelation relatedRelation = (AbstractRelatedRelation)recipient;
						
						if (relatedRelation.getParent() instanceof AbstractRelatedRelationDialog) {
							
							AbstractRelatedRelationDialog dialog = (AbstractRelatedRelationDialog)relatedRelation.getParent();
							dialog.removeComponent(relatedRelation);
							
						} 
						
					}
				}

			}
			
			
		} else {
			
			//remove elements
			Logger.log("======= delete elements here model.id " + model.getId() + " ========", Logger.DEBUG_DETAILS);
			
			for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(model)) {
				
				if (recipient instanceof TableCellArgument) {
					
					TableCellArgument element = (TableCellArgument)recipient;
					((MapTableArgument)map).removeElement(element);
					break;
				}
			}
			
		}
		
	}

	public ArgumentMapSpace getArgumentMapSpace()
	{
		return this.mapSpace;
	}

	@Override
	public Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		// TODO Auto-generated method stub
		return null;
	}

}
