package lasad.gwt.client.ui.workspace.argumentmap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;

/**
 *	Creates the preferences menu that appears when selected from the LASAD menu, found in ArgumentMapMenuBar.
 *	The preferences menu allows the user to select the font size for LASAD, as well as the default box width and size for auto organization.
 *	@author Kevin Loughlin
 *	@since 31 July 2015, Last Updated 11 August 2015
 */
public class CreatePreferencesDialog extends Window
{
	private String mapID;

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private Slider widthSlider;
	private Slider heightSlider;

	private SliderField widthField;
	private SliderField heightField;

	private CheckBox orientUpward;
	private CheckBox orientDownward;

	//private SimpleComboBox<String> fontSizeSelector;

	private FormData formData;

	public CreatePreferencesDialog(String mapID)
	{
		this.mapID = mapID;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		this.setAutoHeight(true);
		this.setWidth(500);
		this.setHeading("Map Organization Preferences");
		formData = new FormData("-20");
		createForm();
	}

	// Changes the font size and box size in live time
	private void createForm()
	{
		// Save these values for cancel button, which will revert to original values
		final int ORIG_BOX_WIDTH = LASAD_Client.getMapTab(mapID).getMyMapSpace().getMyMap().getOrgBoxWidth();
		final int ORIG_MIN_BOX_HEIGHT = LASAD_Client.getMapTab(mapID).getMyMapSpace().getMyMap().getOrgBoxHeight();

		FormPanel thisForm = new FormPanel();
		thisForm.setFrame(true);
		thisForm.setHeaderVisible(false);
		thisForm.setAutoHeight(true);
		
		orientUpward = new CheckBox();
		orientUpward.setText("Orient Upward");
		//orientUpward.setBoxLabel("Orient Upward");

		orientDownward = new CheckBox();
		orientDownward.setText("Orient Downward");
		//orientDownward.setBoxLabel("Orient Downward");
			

		final boolean IS_DOWNWARD = LASAD_Client.getMapTab(mapID).getMyMapSpace().getMyMap().getOrgOrientation();
		if (IS_DOWNWARD)
		{
			orientDownward.setValue(true);
			orientUpward.setValue(false);
		}
		else
		{
			orientDownward.setValue(false);
			orientUpward.setValue(true);
		}
		

		
		ClickHandler myHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				orientUpward.setValue(true);
				orientDownward.setValue(false);
			}

		};
		orientUpward.addClickHandler(myHandler);

		orientDownward.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				orientUpward.setValue(false);
				orientDownward.setValue(true);
			}

		});
		orientUpward.setHTML(orientUpward.getHTML() + "<br>");
		orientDownward.setHTML(orientDownward.getHTML() + "<br><br>");

		thisForm.add(orientUpward, formData);
		thisForm.add(orientDownward, formData);
			

		widthSlider = new Slider()
		{
			@Override
			protected void onValueChange(int value)
			{
				super.onValueChange(value);
				widthField.setFieldLabel("Box Width [" + value + "]");
			}

		};
		widthSlider.setMinValue(100);
		widthSlider.setMaxValue(450);
		widthSlider.setIncrement(1);
		widthSlider.setMessage("Box Width: {0}");

		widthField = new SliderField(widthSlider);

		// Set the default value as the current box width for auto organizer
		widthSlider.setValue(ORIG_BOX_WIDTH);

		thisForm.add(widthField, formData);

		heightSlider = new Slider()
		{
			@Override
			protected void onValueChange(int value)
			{
				super.onValueChange(value);
				heightField.setFieldLabel("Minimum Box Height [" + value + "]");
			}

		};
		heightSlider.setMinValue(100);
		heightSlider.setMaxValue(450);
		heightSlider.setIncrement(1);
		heightSlider.setMessage("Minimum Box Height: {0}");

		heightField = new SliderField(heightSlider);

		// Set the default value as the current min box height for auto organizer
		heightSlider.setValue(ORIG_MIN_BOX_HEIGHT);

		thisForm.add(heightField, formData);

		// Okay Button
		Button btnOkay = new Button("Organize");
		btnOkay.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				AbstractGraphMap myMap = LASAD_Client.getMapTab(mapID).getMyMapSpace().getMyMap();
				myMap.setOrgBoxWidth(widthSlider.getValue());
				myMap.setOrgBoxHeight(heightSlider.getValue());
				
				final boolean ORIENTATION;
				if (orientUpward.getValue())
				{
					ORIENTATION = true;
				}
				else
				{
					ORIENTATION = false;
				}
				CreatePreferencesDialog.this.hide();
				
				myMap.setOrgOrientation(ORIENTATION);
				myMap.getFocusHandler().releaseAllFocus();
				communicator.sendActionPackage(actionBuilder.autoOrganize(myMap.getID(), myMap.getOrgOrientation(), myMap.getOrgBoxWidth(), myMap.getOrgBoxHeight()));
			}
		});
		thisForm.addButton(btnOkay);

		// Cancel Button
		Button btnCancel = new Button("Cancel");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				CreatePreferencesDialog.this.hide();
			}
		});
		thisForm.addButton(btnCancel);

		thisForm.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(thisForm);
		binding.addButton(btnOkay);

		this.add(thisForm);
	}
}