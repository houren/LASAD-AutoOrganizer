package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Set;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.model.organization.ArgumentModel;
import lasad.gwt.client.model.organization.LinkedBox;

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
 * 	This class creates the dialog box for when the user selects to delete a contirbution via the argument map drop down menu.
 * 	@author Kevin Loughlin
 *	@since 20 July 2015, Updated 21 July 2015
 */
public class DeleteContributionDialog extends Window
{
	private FormData formData;

	// The box where the user selects the contribution to be deleted
	private SimpleComboBox<String> boxSelector = new SimpleComboBox<String>();

	private String correspondingMapId;

	private ArgumentModel argModel;
	private Set<LinkedBox> boxes;
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	private MVController myController = null;

	/**
	 *	Constructor
	 *	@param mapId - The id of the map from which we will extract the argument model and boxes
	 */
	public DeleteContributionDialog(String mapId)
	{
		this.correspondingMapId = mapId;
		this.argModel = LASAD_Client.getMapTab(mapId).getMyMapSpace().getMyMap().getArgModel();
		this.boxes = argModel.getBoxes();
		this.boxSelector.setTriggerAction(ComboBox.TriggerAction.ALL);
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		this.setAutoHeight(true);
		this.setWidth(200);
		this.setHeading("Delete contribution...");
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
		boxSelector.setFieldLabel("<font color=\"#000000\">" + "Contribution" + "</font>");
		boxSelector.setAllowBlank(false);

		// Add the rootIDs to the comboBox
		for (LinkedBox box : boxes)
		{
			Integer rootID = box.getRootID();
			boxSelector.add(rootID.toString());
		}

		simple.add(boxSelector, formData);

		// Set the controller once a selection is made
		boxSelector.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se)
			{
				if (getMyController() == null)
				{
					setMyController();
				}
			}	
		});

		// Okay Button will send the remove element request to the server when clicked
		Button btnOkay = new Button("Ok");
		btnOkay.addSelectionListener(new SelectionListener<ButtonEvent>()
		{

			@Override
			public void componentSelected(ButtonEvent ce)
			{
				int rootID = Integer.parseInt(boxSelector.getRawValue());
				onClickSendRemoveElementToServer(correspondingMapId, rootID);
				DeleteContributionDialog.this.hide();
			}
		});
		simple.addButton(btnOkay);

		// Cancel button will hide the dialog
		Button btnCancel = new Button("Cancel");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				DeleteContributionDialog.this.hide();
			}
		});
		simple.addButton(btnCancel);

		simple.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(simple);
		binding.addButton(btnOkay);

		this.add(simple);
	}

	/*
	 *	Sends the request to the server to remove the element.  Be sure to send the boxID, not rootID.
	 *	@param mapID - The ID of the map
	 *	@param rootID - The box's rootID, used to find the box and then extract its boxID
	 */
	private void onClickSendRemoveElementToServer(String mapID, int rootID)
	{
		int boxID = argModel.getBoxByRootID(rootID).getBoxID();
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, boxID));
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