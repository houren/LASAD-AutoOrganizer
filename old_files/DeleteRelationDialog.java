package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Set;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.model.organization.ArgumentModel;
import lasad.gwt.client.model.organization.LinkedBox;
import lasad.gwt.client.model.organization.OrganizerLink;
import lasad.gwt.client.ui.workspace.LASADInfo;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;

/**
 *	This class creates the dialog box for when the user selects to delete a relation via the argument map drop down menu.
 *	As of right now, it only supports deleting links between boxes, not links between a box and another link.
 *	@author Kevin Loughlin
 *	@since 20 July 2015, Updated 21 July 2015
 */
public class DeleteRelationDialog extends Window
{
	private FormData formData;
	private SimpleComboBox<String> comboStart = new SimpleComboBox<String>();
	private SimpleComboBox<String> comboEnd = new SimpleComboBox<String>();
	private String correspondingMapId;
	private ArgumentModel argModel;
	//private Set<LinkedBox> boxes;
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	private MVController myController = null;
	protected boolean allowLinksToLinks;

	/**
	 *	Constructor
	 *	@param mapId - The id of the map from which we will extract the argument model and boxes
	 */
	public DeleteRelationDialog(String mapId)
	{
		this.correspondingMapId = mapId;
		this.argModel = LASAD_Client.getMapTab(mapId).getMyMapSpace().getMyMap().getArgModel();
		//this.boxes = argModel.getBoxes();
		this.allowLinksToLinks = LASAD_Client.getMapTab(mapId).getMyMapSpace().getMyMap().getMyViewSession().getController().getMapInfo().isAllowLinksToLinks();
		this.comboStart.setTriggerAction(ComboBox.TriggerAction.ALL);
		this.comboEnd.setTriggerAction(ComboBox.TriggerAction.ALL);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setAutoHeight(true);
		this.setWidth(200);
		this.setHeading("Delete relation...");
		formData = new FormData("-20");
		createForm();
	}

	/*
	 *	Creates the dialog based from the specifications of onRender
	 */
	private void createForm()
	{
		FormPanel simple = new FormPanel();
		simple.setFrame(true);
		simple.setHeaderVisible(false);
		simple.setAutoHeight(true);
		comboStart.setFieldLabel("<font color=\"#000000\">" + "Start" + "</font>");
		comboStart.setAllowBlank(false);

		// Fills the start combo box with all available box root IDs
		for (LinkedBox box : argModel.getBoxes())
		{
			if (box.getNumRelations() > 0)
			{
				Integer rootID = box.getRootID();
				comboStart.add(rootID.toString());
			}
		}

		simple.add(comboStart, formData);

		comboEnd.setFieldLabel("<font color=\"#000000\">" + "End" + "</font>");
		comboEnd.setAllowBlank(false);

		comboEnd.setEnabled(false);
		simple.add(comboEnd, formData);

		// Filter comboEnd depending on comboStart selection
		comboStart.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se)
			{
				comboEnd.removeAll();

				// Important to remember to fetch box by rootID
				LinkedBox startBox = argModel.getBoxByRootID(Integer.parseInt(comboStart.getRawValue()));
				Set<LinkedBox> relatedBoxes = startBox.getRelatedBoxes();

				// Add related boxes to end combo box
				for (LinkedBox box : relatedBoxes)
				{
					Integer rootID = box.getRootID();
					comboEnd.add(rootID.toString());
				}

				// Set the controller and enable the end combo box
				if (getMyController() == null)
				{
					setMyController();
				}

				comboEnd.setEnabled(true);
			}
		});

		// Essentially same thing as the combo start box, just selected after
		comboEnd.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>()
		{

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se)
			{
				comboStart.removeAll();
				// Important to use rootID
				LinkedBox endBox = argModel.getBoxByRootID(Integer.parseInt(comboEnd.getRawValue()));
				Set<LinkedBox> relatedBoxes = endBox.getRelatedBoxes();

				for (LinkedBox box : relatedBoxes)
				{
					Integer rootID = box.getRootID();
					comboStart.add(rootID.toString());
				}

				if (getMyController() == null)
				{
					setMyController();
				}
			}
		});

		// Upon click of the okay button, find the link between the two listed boxes and delete it
		Button btnOkay = new Button("Ok");
		btnOkay.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				LinkedBox startBox = argModel.getBoxByRootID(Integer.parseInt(comboStart.getRawValue()));
				LinkedBox endBox =  argModel.getBoxByRootID(Integer.parseInt(comboEnd.getRawValue()));
				OrganizerLink link = startBox.getConnectingLink(endBox);
				if (link != null)
				{
					onClickSendRemoveElementToServer(correspondingMapId, link.getLinkID());
				}
				else
				{
					LASADInfo.display("Error", "Link does not exist between these boxes.");
				}
				
				DeleteRelationDialog.this.hide();
			}
		});
		simple.addButton(btnOkay);

		// Cancel Button hides dialog
		Button btnCancel = new Button("Cancel");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				DeleteRelationDialog.this.hide();
			}
		});
		simple.addButton(btnCancel);

		simple.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(simple);
		binding.addButton(btnOkay);

		this.add(simple);
	}

	/*
	 *	Sends the command to remove the element
	 */
	private void onClickSendRemoveElementToServer(String mapID, int linkID)
	{
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, linkID));
	}

	private AbstractMVController getMyController()
	{
		return myController;
	}

	private void setMyController()
	{
		myController = LASAD_Client.getMVCController(correspondingMapId);
	}
}