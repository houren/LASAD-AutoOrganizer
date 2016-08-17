package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.TreeMap;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.pattern.BoxPattern;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateSpecialLinkDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;

public class CreateSpecialLinkDialogPattern extends
		AbstractCreateSpecialLinkDialog {

	private PatternController myController;
	
	public CreateSpecialLinkDialogPattern(ElementInfo config, String mapId,
			TreeMap<String, AbstractBox> boxes,
			TreeMap<String, AbstractLink> links) {
		super(config, mapId, boxes, links);
	}

	@Override
	protected void onClickSendCreateLinkWithElementsToServer(ElementInfo info,
			String mapID, String startElementID, String endElementID) {
		
		if(myController == null){
			setMyController();
		}
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
				createLinkWithElements(info, myController.getMapInfo().getAgentId(), mapID, startElementID, endElementID);
	}


	@Override
	protected PatternController getMyController() {
		return myController;
	}

	@Override
	protected void setMyController() {
		for (AbstractBox box : getBoxes().values()) {
			if(((BoxPattern)box).getMyController() == null){
				((BoxPattern)box).setMyControllerFromLASADClient();
			}
			myController = ((BoxPattern)box).getMyController();
			if(myController != null)
				break;
		}
		if(myController == null){
			for (AbstractLink link : getLinks().values()) {
				myController = (PatternController) ((AbstractLink)link).getLinkPanel().getMVCViewSession().getController();
				if(myController != null)
					break;
			}
		}
		if(myController == null)
			FATDebug.print(FATDebug.DEBUG, "ERROR [CreateSpecialLinkDialogPattern][setMyController]myController is null");
		//myController = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, correspondingMapId);
	}
	
	@Override
	protected void createForm() {
		FormPanel simple = new FormPanel();
		simple.setFrame(true);
		simple.setHeaderVisible(false);
		simple.setAutoHeight(true);

		// Fill combo boxes
		comboStart.setFieldLabel("<font color=\"#000000\">" + "Start" + "</font>");
		comboStart.setAllowBlank(false);
		for (AbstractBox box : getBoxes().values()) {
			String label = ElementVariableUtil.createHeaderForBoxLink(box.getConnectedModel().getValue(ParameterTypes.ElementId), 
					box.getConnectedModel().getValue(ParameterTypes.RootElementId));
			comboStart.add(label);
			comboEnd.add(label);
		}
		simple.add(comboStart, formData);

		comboEnd.setFieldLabel("<font color=\"#000000\">" + "End" + "</font>");
		comboEnd.setAllowBlank(false);
		for (AbstractLink link : getLinks().values()) {
			String label = ElementVariableUtil.createHeaderForBoxLink(link.getConnectedModel().getValue(ParameterTypes.ElementId), 
					link.getConnectedModel().getValue(ParameterTypes.RootElementId));
			comboEnd.add(label);
		}
//		for (String id : getLinks().keySet()) {
//			comboEnd.add(id);
//		}
		comboEnd.setEnabled(false);
		simple.add(comboEnd, formData);

		// Filter comboEnd depending on comboStart selection
		comboStart.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				comboEnd.setEnabled(true);

				boolean comboEndHasElements = false;

				if (getMyController() == null) {
//					myController = LASAD_Client.getMVCController(correspondingMapId);
					setMyController();
				}
				comboEnd.removeAll();

				Vector<String> restrictedIds = new Vector<String>();

				//TODO (Marcel) unite following two loops
//				for (String boxId : getBoxes().keySet()) {
				for (AbstractBox box : getBoxes().values()) {
					String label = ElementVariableUtil.createHeaderForBoxLink(box.getConnectedModel().getValue(ParameterTypes.ElementId), 
							box.getConnectedModel().getValue(ParameterTypes.RootElementId));
					if (label.equals(comboStart.getRawValue())) {
						restrictedIds.add(label);

						// It's not allowed to connect two boxes that already
						// are connected to each other
						//for (String linkId : getLinks().keySet()) {
						for (AbstractLink link : getLinks().values()) {
							String rootElementId = link.getConnectedModel().getValue(ParameterTypes.RootElementId);
							String parentOneRootElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(0).getValue(ParameterTypes.RootElementId);
							String parentOneElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(0).getValue(ParameterTypes.ElementId);
							String parentTwoRootElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(1).getValue(ParameterTypes.RootElementId);
							String parentTwoElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(1).getValue(ParameterTypes.ElementId);
							String parentOne = ElementVariableUtil.createHeaderForBoxLink(parentOneElemId, parentOneRootElemId);
							String parentTwo = ElementVariableUtil.createHeaderForBoxLink(parentTwoElemId, parentTwoRootElemId);
							if (parentOne.equals(comboStart.getRawValue())) {
								restrictedIds.add(parentTwo);
							} else if (parentTwo.equals(comboStart.getRawValue())) {
								restrictedIds.add(parentOne);
							}
						}
						//break;
					}
				}

				//for (String boxId : getBoxes().keySet()) {
				for (AbstractBox box : getBoxes().values()) {
					String label = ElementVariableUtil.createHeaderForBoxLink(box.getConnectedModel().getValue(ParameterTypes.ElementId), 
							box.getConnectedModel().getValue(ParameterTypes.RootElementId));
					
					if (!restrictedIds.contains(label)) {
						comboEnd.add(label);
						if (comboEndHasElements == false) comboEndHasElements = true;
					}
				}

				//for (String id : getLinks().keySet()) {
				for (AbstractLink link : getLinks().values()) {
					String rootElementId = link.getConnectedModel().getValue(ParameterTypes.RootElementId);
					String parentOneRootElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(0).getValue(ParameterTypes.RootElementId);
					String parentOneElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(0).getValue(ParameterTypes.ElementId);
					String parentTwoRootElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(1).getValue(ParameterTypes.RootElementId);
					String parentTwoElemId = getLinks().get(rootElementId).getConnectedModel().getParents().get(1).getValue(ParameterTypes.ElementId);
					String parentOne = ElementVariableUtil.createHeaderForBoxLink(parentOneElemId, parentOneRootElemId);
					String parentTwo = ElementVariableUtil.createHeaderForBoxLink(parentTwoElemId, parentTwoRootElemId);
				
					// It's not allowed to have a link from a box to its
					// connected links
					if (!(parentOne.equals(comboStart.getRawValue()) 
							|| parentTwo.equals(comboStart.getRawValue()))) {
						String label = ElementVariableUtil.createHeaderForBoxLink(link.getConnectedModel().getValue(ParameterTypes.ElementId), 
																					rootElementId);
						comboEnd.add(label);
						if (comboEndHasElements == false) comboEndHasElements = true;
					}
				}

				if (!comboEndHasElements) {
					LASADInfo.display("Error", "No more connections available for this starting element.");
				}
			}
		});

		comboEnd.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				comboStart.removeAll();
				//for (String id : getBoxes().keySet()) {
				for (AbstractBox box : getBoxes().values()) {
					String label = ElementVariableUtil.createHeaderForBoxLink(box.getConnectedModel().getValue(ParameterTypes.ElementId), 
							box.getConnectedModel().getValue(ParameterTypes.RootElementId));
					if (!label.equals(comboEnd.getRawValue())) {
						comboStart.add(label);
					}
				}
			}
		});

		// Okay Button
		Button btnOkay = new Button("Ok");
		btnOkay.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				String selValue = comboStart.getRawValue();
				String[] array = selValue.split("-");
				//String startId = Integer.toString(getBoxes().get(comboStart.getRawValue()).getConnectedModel().getId());
				String startId = Integer.toString(getBoxes().get(array[array.length-1]).getConnectedModel().getId());
				String endId = "";
				selValue = comboEnd.getRawValue();
				array = selValue.split("-");
				if (getBoxes().containsKey(array[array.length-1])) {
//					endId = Integer.toString(getBoxes().get(comboEnd.getRawValue()).getConnectedModel().getId());
					endId = Integer.toString(getBoxes().get(array[array.length-1]).getConnectedModel().getId());
				} else {
//					endId = Integer.toString(getLinks().get(comboEnd.getRawValue()).getConnectedModel().getId());
					endId = Integer.toString(getLinks().get(array[array.length-1]).getConnectedModel().getId());
				}
				//communicator.sendActionPackage(actionBuilder.createLinkWithElements(config, correspondingMapId, startId, endId));
				onClickSendCreateLinkWithElementsToServer(config, correspondingMapId, startId, endId);
				CreateSpecialLinkDialogPattern.this.hide();
			}
		});
		simple.addButton(btnOkay);

		// Cancel Button
		Button btnCancel = new Button("Cancel");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				CreateSpecialLinkDialogPattern.this.hide();
			}
		});
		simple.addButton(btnCancel);

		simple.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(simple);
		binding.addButton(btnOkay);

		this.add(simple);
	}

}
