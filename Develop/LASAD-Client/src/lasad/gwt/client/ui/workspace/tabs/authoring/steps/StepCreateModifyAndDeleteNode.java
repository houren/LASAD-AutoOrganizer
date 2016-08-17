package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.HashMap;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.OntologyGenerator;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.menu.ColorMenu;

public class StepCreateModifyAndDeleteNode extends ContentPanel {

	LASADActionSender communicator = LASADActionSender.getInstance();
	ActionFactory builder = ActionFactory.getInstance();

	private String fontStyleStart = "<span style=\"color: #000000;\">";
	private String fontStyleEnd = "</span>";

	private FormPanel delNodeForm;

	private ColorMenu cm;
	private String color = "red";

	private Radio radio, radio2, radio3, radio4, radio5, colorRadioRed, colorRadioBlue, colorRadioGreen, colorRadioYellow,
			colorRadioOrange, colorRadioOther;
	private TextField<String> title;
	private Slider widthSl;
	private CheckBox addTextField;
	private CheckBox addAwarenessCheckbox;
	private CheckBox canBeGrouped;

	private SliderField sfWidth;

	private SimpleComboBox<String> nodes = new SimpleComboBox<String>();
	private HashMap<String, ElementInfo> nodeNamesToElementInfo = new HashMap<String, ElementInfo>();

	public void resetNodeList() {
		nodes.removeAll();
		nodeNamesToElementInfo.clear();
	}

	public void addNodeToList(String name, ElementInfo info) {
		nodes.add(name);
		nodeNamesToElementInfo.put(name, info);
	}

	// private void clearAddOntologyPanel() {
	//
	// title.setAllowBlank(true);
	// title.reset();
	// title.setAllowBlank(false);
	//
	// colorRadioRed.setValue(true);
	// color = "red";
	//
	// radio.setValue(true);
	//
	// addTextField.setValue(true);
	//
	// sfWidth.reset();
	// sfWidth.setValue(200);
	// widthSl.setValue(200);
	// }
	//
	// private void clearDelOntologyPanel() {
	//
	// nodes.setForceSelection(false);
	// nodes.reset();
	// nodes.setForceSelection(true);
	//
	// if(nodeNamesToElementInfo.keySet().size() == 0) {
	// delNodeForm.setEnabled(false);
	// }
	// else {
	// delNodeForm.setEnabled(true);
	// }
	// }

	public StepCreateModifyAndDeleteNode() {
		setLayout(new AccordionLayout());

		setHeaderVisible(false);
		setBorders(false);

		add(initAddOrModifyNodePanel(null));

		initDelNodePanel();

		layout();
	}

	/**
	 * If elInfo is null, this will return a normal add node window. Otherwise, the window will be filled with the data that is
	 * already saved in the ElementInfo.
	 * 
	 * @param elInfo
	 * @return
	 */
	public FormPanel initAddOrModifyNodePanel(final ElementInfo elInfo) {

		FormPanel addTopLevelElementForm = new FormPanel();
		addTopLevelElementForm.setLabelWidth(150);
		addTopLevelElementForm.setScrollMode(Scroll.AUTOY);
		addTopLevelElementForm.setButtonAlign(HorizontalAlignment.LEFT);

		if (elInfo == null) {
			addTopLevelElementForm.setHeading("Add a new node...");
		} else {
			addTopLevelElementForm.setHeading("Modify this node...");
		}

		title = new TextField<String>();
		title.setFieldLabel("Title");
		title.setEmptyText("Enter title of the new node");
		title.setMinLength(1);
		title.setAllowBlank(false);

		if (elInfo != null) {
			title.setValue(elInfo.getElementOption(ParameterTypes.Heading));
		}

		colorRadioRed = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				color = "red";
			}
		};
		colorRadioRed.setBoxLabel(fontStyleStart + "Red" + fontStyleEnd);

		colorRadioOrange = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				color = "orange";
			}
		};
		colorRadioOrange.setBoxLabel(fontStyleStart + "Orange" + fontStyleEnd);

		colorRadioYellow = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				color = "yellow";
			}
		};
		colorRadioYellow.setBoxLabel(fontStyleStart + "Yellow" + fontStyleEnd);

		colorRadioGreen = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				color = "green";
			}
		};
		colorRadioGreen.setBoxLabel(fontStyleStart + "Green" + fontStyleEnd);

		colorRadioBlue = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				color = "blue";
			}
		};
		colorRadioBlue.setBoxLabel(fontStyleStart + "Blue" + fontStyleEnd);

		colorRadioOther = new Radio() {

			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);

				cm = new ColorMenu() {

					@Override
					protected void onDetach() {
						if (cm.getColor() != null) {
							color = '#' + cm.getColor();
						}
						colorRadioOther.setBoxLabel(fontStyleStart + "Other... " + fontStyleEnd + "(<span style=\"color: " + color + "\">"
								+ color + "</span>)");
						super.onDetach();
					}
				};
				cm.show(colorRadioOther);
			}

		};
		colorRadioOther.setBoxLabel(fontStyleStart + "Other..." + fontStyleEnd);

		if (elInfo == null) {
			colorRadioRed.setValue(true);
		} else {
			String elColor = elInfo.getUiOption(ParameterTypes.BackgroundColor);

			boolean colorCode = false;

			if (elColor.contains("#")) {
				colorCode = true;
				elColor = elColor.replace("#", "");
			}

			else {
				colorCode = false;
				if (elColor.equalsIgnoreCase("red")) {
					colorRadioRed.setValue(true);
				} else if (elColor.equalsIgnoreCase("orange")) {
					colorRadioOrange.setValue(true);
				} else if (elColor.equalsIgnoreCase("yellow")) {
					colorRadioYellow.setValue(true);
				} else if (elColor.equalsIgnoreCase("green")) {
					colorRadioGreen.setValue(true);
				} else if (elColor.equalsIgnoreCase("blue")) {
					colorRadioBlue.setValue(true);
				}
				color = elColor;
			}

			if (colorCode) {
				elColor = "#" + elColor;

				colorRadioOther.setBoxLabel(fontStyleStart + "Other... " + fontStyleEnd + "(<span style=\"color: " + elColor + "\">"
						+ elColor + "</span>)");
				colorRadioOther.setValue(true);
				color = elColor;
			}
		}

		RadioGroup colorRadioGroup = new RadioGroup();
		colorRadioGroup.setFieldLabel("Color");
		colorRadioGroup.add(colorRadioRed);
		colorRadioGroup.add(colorRadioOrange);
		colorRadioGroup.add(colorRadioYellow);
		colorRadioGroup.add(colorRadioGreen);
		colorRadioGroup.add(colorRadioBlue);
		colorRadioGroup.add(colorRadioOther);

		radio = new Radio();
		radio.setBoxLabel(fontStyleStart + "Standard" + fontStyleEnd);

		radio2 = new Radio();
		radio2.setBoxLabel(fontStyleStart + "Zig Zag" + fontStyleEnd);

		radio3 = new Radio();
		radio3.setBoxLabel(fontStyleStart + "Double" + fontStyleEnd);

		radio4 = new Radio();
		radio4.setBoxLabel(fontStyleStart + "Round Edges" + fontStyleEnd);

		radio5 = new Radio();
		radio5.setBoxLabel(fontStyleStart + "Dashed" + fontStyleEnd);

		if (elInfo == null || elInfo.getUiOption(ParameterTypes.Border).equalsIgnoreCase("standard")) {
			radio.setValue(true);
		} else if (elInfo.getUiOption(ParameterTypes.Border).equalsIgnoreCase("zigzag")) {
			radio2.setValue(true);
		} else if (elInfo.getUiOption(ParameterTypes.Border).equalsIgnoreCase("double")) {
			radio3.setValue(true);
		} else if (elInfo.getUiOption(ParameterTypes.Border).equalsIgnoreCase("round")) {
			radio4.setValue(true);
		} else if (elInfo.getUiOption(ParameterTypes.Border).equalsIgnoreCase("dashed")) {
			radio5.setValue(true);
		}

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Border-style");
		radioGroup.add(radio);
		radioGroup.add(radio2);
		radioGroup.add(radio3);
		radioGroup.add(radio4);
		radioGroup.add(radio5);

		widthSl = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfWidth.setFieldLabel("Width [" + value + "]");
			}

		};
		widthSl.setMinValue(150);
		widthSl.setMaxValue(450);
		widthSl.setIncrement(1);
		widthSl.setMessage("Width: {0}");

		sfWidth = new SliderField(widthSl);

		if (elInfo == null) {
			widthSl.setValue(200);
		} else {
			widthSl.setValue(Integer.parseInt(elInfo.getUiOption(ParameterTypes.Width) == null ? elInfo.getUiOption(ParameterTypes.Width)
					: elInfo.getUiOption(ParameterTypes.Width)));
		}

		if (elInfo == null) {
			addTextField = new CheckBox();
			addTextField.setFieldLabel("Add text field (default)");
			addTextField.setValue(true);
		} else {
			addTextField.setValue(false);
		}
		// SN: Added Awareness Checkbox
		if (elInfo == null) {
			addAwarenessCheckbox = new CheckBox();
			addAwarenessCheckbox.setFieldLabel("Add awareness");
			addAwarenessCheckbox.setValue(true);
		} else {
			addAwarenessCheckbox.setValue(false);
		}
		
		canBeGrouped = new CheckBox();
		canBeGrouped.setFieldLabel("Can be grouped with other boxes of the same type");

		if (elInfo == null) {
			canBeGrouped.setValue(true);
		} else {
			canBeGrouped.setValue(false);
		}
		// SN: End Awareness Checkbox

		Button previewButton = new Button("Preview") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields(true)) {
					ElementInfo newElement = createNewElement(elInfo);

					if (addTextField.getValue()) {
						// Add default text field
						Vector<Parameter> subElOptions = new Vector<Parameter>();
						Vector<Parameter> subUiOptions = new Vector<Parameter>();
						subUiOptions.add(new Parameter(ParameterTypes.MinHeight, "42"));

						String subElementName = title.getValue() + "-text";
						ElementInfo newElementTextField = OntologyGenerator.createElementInfo(subElementName, "text", 1, 1, 1,
								subElOptions, subUiOptions);
						newElement.getChildElements().put(subElementName, newElementTextField);
					}
					// SN: Added Awareness Checkbox - Behavior during preview.
					if (addAwarenessCheckbox.getValue()) {
						addAwareness(newElement);
					}

					// SN: End Awareness Checkbox
					

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(newElement);
				}
			}
		};

		Button saveButton = new Button("Save") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields(true)) {

					ElementInfo newElement = createNewElement(elInfo);

					if (addTextField.getValue()) {
						// Add default text field
						Vector<Parameter> subElOptions = new Vector<Parameter>();
						Vector<Parameter> subUiOptions = new Vector<Parameter>();
						subUiOptions.add(new Parameter(ParameterTypes.MinHeight, "42"));

						String subElementName = title.getValue() + "-text";
						ElementInfo newElementTextField = OntologyGenerator.createElementInfo(subElementName, "text", 1, 1, 1,
								subElOptions, subUiOptions);
						newElement.getChildElements().put(subElementName, newElementTextField);
					}
					
					// SN: Added Awareness Checkbox - Behavior during preview.
					if (addAwarenessCheckbox.getValue()) {
						addAwareness(newElement);
					}
					// SN: End Awareness Checkbox

					newElement.addElementOption(ParameterTypes.CanBeGrouped, canBeGrouped.getValue().toString());


					if (elInfo != null) {
						CreateModifyAndDeleteOntology.contributionToElement.remove(elInfo);
					}
					CreateModifyAndDeleteOntology.contributionToElement.put(newElement, new Vector<ElementInfo>());

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology,
							CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(newElement);

					LASADInfo.display("Info", "Ontology will be updated on server...");

					CreateModifyAndDeleteOntology.clearElementActions();
				}
			}
		};

		addTopLevelElementForm.setBodyBorder(false);

		addTopLevelElementForm.add(title);
		addTopLevelElementForm.add(colorRadioGroup);
		addTopLevelElementForm.add(radioGroup);
		addTopLevelElementForm.add(sfWidth);

		if (elInfo == null) {
			addTopLevelElementForm.add(addTextField);
			addTopLevelElementForm.add(addAwarenessCheckbox);
		}

		addTopLevelElementForm.add(canBeGrouped);

		addTopLevelElementForm.addButton(previewButton);
		addTopLevelElementForm.addButton(saveButton);

		addTopLevelElementForm.layout();

		return addTopLevelElementForm;
	}

	protected void addAwareness(ElementInfo newElement) {
		// TODO Add Awareness
		/*
		Vector<Parameter> subElOptions = new Vector<Parameter>();
		Vector<Parameter> subUiOptions = new Vector<Parameter>();
		subUiOptions.add(new Parameter(ParameterTypes.MinHeight, "42"));

		String subElementName = title.getValue() + "-text";
		ElementInfo newElementTextField = OntologyGenerator.createElementInfo(subElementName, "text", 1, 1, 1,
				subElOptions, subUiOptions);
		newElement.getChildElements().put(subElementName, newElementTextField);
		*/
		Vector<Parameter> subElOptions = new Vector<Parameter>();
		Vector<Parameter> subUiOptions = new Vector<Parameter>();
		
		subUiOptions.add(new Parameter(ParameterTypes.MinHeight, "16"));
		subUiOptions.add(new Parameter(ParameterTypes.MaxHeight, "16"));
		ElementInfo newElementAwareness= OntologyGenerator.createElementInfo("author", "awareness", 1, 1, 1, subElOptions, subUiOptions);
		newElement.getChildElements().put(title.getValue()+"-awareness", newElementAwareness);

	}

	protected String getBorder() {
		String border = null;

		if (radio.getValue()) {
			border = "standard";
		} else if (radio2.getValue()) {
			border = "zigzag";
		} else if (radio3.getValue()) {
			border = "double";
		} else if (radio4.getValue()) {
			border = "round";
		} else if (radio5.getValue()) {
			border = "dashed";
		}

		return border;
	}

	private void initDelNodePanel() {
		delNodeForm = new FormPanel();
		delNodeForm.setButtonAlign(HorizontalAlignment.LEFT);
		delNodeForm.setHeading("Delete an existing node...");

		nodes = new SimpleComboBox<String>();
		nodes.setFieldLabel("Node");
		nodes.setEmptyText("Choose a node");
		nodes.setAllowBlank(false);
		nodes.setForceSelection(true);
		nodes.setTriggerAction(TriggerAction.ALL);

		Button deleteButton = new Button("Delete") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (nodes.validate()) {

					String nodeName = nodes.getSimpleValue();
					CreateModifyAndDeleteOntology.contributionToElement.remove(nodeNamesToElementInfo.get(nodeName));

					nodes.setAllowBlank(true);
					nodes.remove(nodeName);
					nodes.reset();
					nodes.setAllowBlank(false);

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology,
							CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					CreateModifyAndDeleteOntology.clearElementActions();
				} else {
					LASADInfo.display("Error", "Please select a node that should be deleted");
				}
			}
		};

		delNodeForm.setBodyBorder(false);
		delNodeForm.add(nodes);
		delNodeForm.addButton(deleteButton);

		add(delNodeForm);
	}

	private boolean validateFields(boolean ignoreIfExisting) {
		boolean ok = true;

		if (!title.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please specify a concrete title for the node");
		}

		if (!ignoreIfExisting) {
			if (nodeNamesToElementInfo.keySet().contains(title.getValue())) {
				ok = false;
				LASADInfo.display("Error", "There is already a node with this title");
			}

			// To support "old" by-hand created ontologies, which do not use the title as id
			for (String s : nodeNamesToElementInfo.keySet()) {
				ElementInfo ei = nodeNamesToElementInfo.get(s);
				if (title.getValue().equals(ei.getElementID())) {
					ok = false;
					LASADInfo.display("Error", "There is already a node with this title");
					break;
				}
			}
		}

		return ok;
	}

	private ElementInfo createNewElement(ElementInfo elInfo) {
		Vector<Parameter> elOptions = new Vector<Parameter>();
		elOptions.add(new Parameter(ParameterTypes.Heading, title.getValue()));
		elOptions.add(new Parameter(ParameterTypes.CanBeGrouped, canBeGrouped.getValue().toString()));

		Vector<Parameter> uiOptions = new Vector<Parameter>();
		// TODO: Width und Height waren hier klein geschrieben, sind jetzt aber auf den gro�1�7-geschriebenen Wert abgebildet.
		// Pr�fen ob das dennoch funktioniert...
		uiOptions.add(new Parameter(ParameterTypes.Width, widthSl.getValue() + ""));
		uiOptions.add(new Parameter(ParameterTypes.Height, "200"));
		uiOptions.add(new Parameter(ParameterTypes.BackgroundColor, color));
		uiOptions.add(new Parameter(ParameterTypes.Border, getBorder()));

		ElementInfo newElement = null;
		if (elInfo != null) {
			newElement = OntologyGenerator.createElementInfo(title.getValue(), "box", null, null, null, elOptions, uiOptions,
					elInfo.getChildElements());
		} else {
			newElement = OntologyGenerator.createElementInfo(title.getValue(), "box", null, null, null, elOptions, uiOptions);
		}

		return newElement;
	}
}