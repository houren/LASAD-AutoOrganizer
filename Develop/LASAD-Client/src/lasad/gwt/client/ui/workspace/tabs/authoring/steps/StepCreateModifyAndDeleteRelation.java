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

/**
 * This file is for adding a type of relation when creating an ontology via the authoring tool.
 */

public class StepCreateModifyAndDeleteRelation extends ContentPanel {

	private String panelColor = "red", lineColor = "red";

	private String fontStyleStart = "<span style=\"color: #000000;\">";
	private String fontStyleEnd = "</span>";

	private FormPanel delRelationForm;
	private LASADActionSender communicator = LASADActionSender.getInstance();
	private ActionFactory builder = ActionFactory.getInstance();

	private Radio panelColorRadioOther, panelColorRadioRed, panelColorRadioBlue, panelColorRadioOrange, panelColorRadioYellow, panelColorRadioGreen;
	private Radio lineColorRadioOther, lineColorRadioRed, lineColorRadioBlue, lineColorRadioOrange, lineColorRadioYellow, lineColorRadioGreen;

	private Slider widthSl, lineWidthSl;

	private ColorMenu panelColorMenu, lineColorMenu;
	private CheckBox directed, noDetails, connectsGroup;
	private TextField<String> title;
	private SliderField sfLineWidth, sfWidth;

	private SimpleComboBox<String> relations = new SimpleComboBox<String>();
	private HashMap<String, ElementInfo> relationNamesToElementInfo = new HashMap<String, ElementInfo>();

	public void resetRelationList() {
		relations.removeAll();
		relationNamesToElementInfo.clear();
	}

	public void addRelationToList(String name, ElementInfo info) {
		relations.add(name);
		relationNamesToElementInfo.put(name, info);
	}

	public void clearAddRelationPanel() {

		title.setAllowBlank(true);
		title.reset();
		title.setAllowBlank(false);

		panelColorRadioRed.setValue(true);
		panelColor = "red";

		lineColorRadioRed.setValue(true);
		lineColor = "red";

		directed.reset();
		noDetails.reset();
		connectsGroup.reset();

		sfLineWidth.reset();
		sfWidth.reset();

		widthSl.setValue(120);
		sfWidth.setValue(120);

		lineWidthSl.setValue(2);
		sfLineWidth.setValue(2);
	}

	public void clearDelRelationPanel() {
		relations.setForceSelection(false);
		relations.reset();
		relations.setForceSelection(true);

		if (relationNamesToElementInfo.keySet().size() == 0) {
			delRelationForm.setEnabled(false);
		} else {
			delRelationForm.setEnabled(true);
		}
	}

	StepCreateModifyAndDeleteRelation() {
		setLayout(new AccordionLayout());

		setHeaderVisible(false);
		setBorders(false);

		add(initAddOrModifyRelationPanel(null));
		initDelRelationPanel();

		layout();
	}

	private ElementInfo createNewElement(ElementInfo elInfo) {
		Vector<Parameter> elOptions = new Vector<Parameter>();
		elOptions.add(new Parameter(ParameterTypes.Heading, title.getValue()));
		elOptions.add(new Parameter(ParameterTypes.Endings, directed.getValue().toString()));
		elOptions.add(new Parameter(ParameterTypes.ConnectsGroup, connectsGroup.getValue().toString()));

		Vector<Parameter> uiOptions = new Vector<Parameter>();
		// TODO: Width und Height waren hier klein geschrieben, sind jetzt aber auf den gro�-geschriebenen Wert abgebildet. Pr�fen ob das dennoch funktioniert...
		uiOptions.add(new Parameter(ParameterTypes.Width, widthSl.getValue() + ""));
		uiOptions.add(new Parameter(ParameterTypes.Height, "200"));
		uiOptions.add(new Parameter(ParameterTypes.BackgroundColor, panelColor));
		uiOptions.add(new Parameter(ParameterTypes.LineColor, lineColor));
		uiOptions.add(new Parameter(ParameterTypes.LineWidth, lineWidthSl.getValue() + "px"));

		if (noDetails.getValue()) {
			elOptions.add(new Parameter(ParameterTypes.Details, "false"));
		}

		ElementInfo newElement = null;
		if(elInfo == null || noDetails.getValue() == true) {
			newElement = OntologyGenerator.createElementInfo(title.getValue(), "relation", null, null, null, elOptions, uiOptions);
		}
		else {
			newElement = OntologyGenerator.createElementInfo(title.getValue(), "relation", null, null, null, elOptions, uiOptions, elInfo.getChildElements());
		}
		return newElement;
	}

	public FormPanel initAddOrModifyRelationPanel(final ElementInfo elInfo) {
		FormPanel addTopLevelElementForm = new FormPanel();
		addTopLevelElementForm.setScrollMode(Scroll.AUTOY);
		addTopLevelElementForm.setLabelWidth(140);
		addTopLevelElementForm.setButtonAlign(HorizontalAlignment.LEFT);
		
		if(elInfo == null) {
			addTopLevelElementForm.setHeading("Add a new relation...");
		}
		else {
			addTopLevelElementForm.setHeading("Modify this relation...");
		}

		title = new TextField<String>();
		title.setFieldLabel("Title");
		title.setEmptyText("Enter title of the new relation");
		title.setMinLength(1);
		title.setAllowBlank(false);
		
		if(elInfo != null) {
			title.setValue(elInfo.getElementOption(ParameterTypes.Heading));
		}

		panelColorRadioRed = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				panelColor = "red";
			}
		};
		panelColorRadioRed.setBoxLabel(fontStyleStart + "Red" + fontStyleEnd);

		panelColorRadioOrange = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				panelColor = "orange";
			}
		};
		panelColorRadioOrange.setBoxLabel(fontStyleStart + "Orange" + fontStyleEnd);

		panelColorRadioYellow = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				panelColor = "yellow";
			}
		};
		panelColorRadioYellow.setBoxLabel(fontStyleStart + "Yellow" + fontStyleEnd);

		panelColorRadioGreen = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				panelColor = "green";
			}
		};
		panelColorRadioGreen.setBoxLabel(fontStyleStart + "Green" + fontStyleEnd);

		panelColorRadioBlue = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				panelColor = "blue";
			}
		};
		panelColorRadioBlue.setBoxLabel(fontStyleStart + "Blue" + fontStyleEnd);

		panelColorRadioOther = new Radio() {

			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);

				panelColorMenu = new ColorMenu() {

					@Override
					protected void onDetach() {
						if (panelColorMenu.getColor() != null) {
							panelColor = '#' + panelColorMenu.getColor();
						}
						panelColorRadioOther.setBoxLabel(fontStyleStart + "Other... " + fontStyleEnd + "(<span style=\"color: " + panelColor + "\">" + panelColor + "</span>)");
						super.onDetach();
					}
				};
				panelColorMenu.show(panelColorRadioOther);
			}

		};
		panelColorRadioOther.setBoxLabel(fontStyleStart + "Other..." + fontStyleEnd);

		if(elInfo == null) {
			panelColorRadioRed.setValue(true);
		}
		else {
			String elColor = elInfo.getUiOption(ParameterTypes.BackgroundColor);
			
			boolean colorCode = false;
			
			if(elColor.contains("#")) {
				colorCode = true;
				elColor = elColor.replace("#", "");
			}
			
			if(elColor.equalsIgnoreCase("red")) {
				panelColorRadioRed.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("orange")) {
				panelColorRadioOrange.setValue(true);
			} 
			else if(elColor.equalsIgnoreCase("yellow")) {
				panelColorRadioYellow.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("green")) {
				panelColorRadioGreen.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("blue")) {
				panelColorRadioBlue.setValue(true);
			}
			else {
				if(colorCode) {
					elColor = "#"+elColor;
				}
				panelColorRadioOther.setBoxLabel(fontStyleStart+"Other... "+fontStyleEnd+"(<span style=\"color: " + elColor + "\">" + elColor + "</span>)");
				panelColorRadioOther.setValue(true);
				panelColor = elColor;
			}
		}
		
		RadioGroup panelColorRadioGroup = new RadioGroup();
		panelColorRadioGroup.setFieldLabel("Panel color");
		panelColorRadioGroup.add(panelColorRadioRed);
		panelColorRadioGroup.add(panelColorRadioOrange);
		panelColorRadioGroup.add(panelColorRadioYellow);
		panelColorRadioGroup.add(panelColorRadioGreen);
		panelColorRadioGroup.add(panelColorRadioBlue);
		panelColorRadioGroup.add(panelColorRadioOther);

		lineColorRadioRed = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				lineColor = "red";
			}
		};
		lineColorRadioRed.setBoxLabel(fontStyleStart + "Red" + fontStyleEnd);

		lineColorRadioOrange = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				lineColor = "orange";
			}
		};
		lineColorRadioOrange.setBoxLabel(fontStyleStart + "Orange" + fontStyleEnd);

		lineColorRadioYellow = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				lineColor = "yellow";
			}
		};
		lineColorRadioYellow.setBoxLabel(fontStyleStart + "Yellow" + fontStyleEnd);

		lineColorRadioGreen = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				lineColor = "green";
			}
		};
		lineColorRadioGreen.setBoxLabel(fontStyleStart + "Green" + fontStyleEnd);

		lineColorRadioBlue = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				lineColor = "blue";
			}
		};
		lineColorRadioBlue.setBoxLabel(fontStyleStart + "Blue" + fontStyleEnd);

		lineColorRadioOther = new Radio() {

			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);

				lineColorMenu = new ColorMenu() {

					@Override
					protected void onDetach() {
						if (lineColorMenu.getColor() != null) {
							lineColor = '#' + lineColorMenu.getColor();
						}
						lineColorRadioOther.setBoxLabel(fontStyleStart + "Other... " + fontStyleEnd + "(<span style=\"color: " + lineColor + "\">" + lineColor + "</span>)");
						super.onDetach();
					}
				};
				lineColorMenu.show(lineColorRadioOther);
			}

		};
		lineColorRadioOther.setBoxLabel(fontStyleStart + "Other..." + fontStyleEnd);

		if(elInfo == null) {
			lineColorRadioRed.setValue(true);
		}
		else {
			String elColor = elInfo.getUiOption(ParameterTypes.LineColor);
			
			boolean colorCode = false;
			
			if(elColor.contains("#")) {
				colorCode = true;
				elColor = elColor.replace("#", "");
			}
			
			if(elColor.equalsIgnoreCase("red")) {
				lineColorRadioRed.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("orange")) {
				lineColorRadioOrange.setValue(true);
			} 
			else if(elColor.equalsIgnoreCase("yellow")) {
				lineColorRadioYellow.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("green")) {
				lineColorRadioGreen.setValue(true);
			}
			else if(elColor.equalsIgnoreCase("blue")) {
				lineColorRadioBlue.setValue(true);
			}
			else {
				if(colorCode) {
					elColor = "#"+elColor;
				}
				lineColorRadioOther.setBoxLabel(fontStyleStart+"Other... "+fontStyleEnd+"(<span style=\"color: " + elColor + "\">" + elColor + "</span>)");
				lineColorRadioOther.setValue(true);
				lineColor = elColor;
			}
		}
		
		RadioGroup lineColorRadioGroup = new RadioGroup();
		lineColorRadioGroup.setFieldLabel("Line color");
		lineColorRadioGroup.add(lineColorRadioRed);
		lineColorRadioGroup.add(lineColorRadioOrange);
		lineColorRadioGroup.add(lineColorRadioYellow);
		lineColorRadioGroup.add(lineColorRadioGreen);
		lineColorRadioGroup.add(lineColorRadioBlue);
		lineColorRadioGroup.add(lineColorRadioOther);

		

		lineWidthSl = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfLineWidth.setFieldLabel("Thickness of line [" + value + "]");
			}

		};
		lineWidthSl.setMinValue(1);
		lineWidthSl.setMaxValue(5);
		
		lineWidthSl.setIncrement(1);
		lineWidthSl.setMessage("Thickness: {0}");

		sfLineWidth = new SliderField(lineWidthSl);
		
		widthSl = new Slider() {

			@Override
			protected void onValueChange(int value) {
				super.onValueChange(value);
				sfWidth.setFieldLabel("Width [" + value + "]");
			}

		};
		widthSl.setMinValue(50);
		widthSl.setMaxValue(350);

		widthSl.setValue(120);
		widthSl.setIncrement(1);
		widthSl.setMessage("Width: {0}");
		
		sfWidth = new SliderField(widthSl);
		sfWidth.setFieldLabel("Width [120]");
		sfWidth.setValue(120);

		Button previewButton = new Button("Preview") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields()) {
					ElementInfo newElement = createNewElement(elInfo);
					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(newElement);
				}
			}
		};
		
		if(elInfo == null) {
			lineWidthSl.setValue(2);
			sfLineWidth.setFieldLabel("Thickness of line [2]");
			sfLineWidth.setValue(2);
		}
		else {	
			String lw = elInfo.getUiOption(ParameterTypes.LineWidth);
			lw = lw.replace("px", "");
			
			lineWidthSl.setValue(Integer.parseInt(lw));
			sfLineWidth.setFieldLabel("Thickness of line ["+lw+"]");
			sfLineWidth.setValue(Integer.parseInt(lw));
		}
		
		if(elInfo == null) {
			widthSl.setValue(120);
			sfWidth.setFieldLabel("Width [120]");
			sfWidth.setValue(120);
		}
		else {	
			String w = elInfo.getUiOption(ParameterTypes.Width);
			w = w.replace("px", "");
			
			widthSl.setValue(Integer.parseInt(w));
			sfWidth.setFieldLabel("Width ["+w+"]");
			sfWidth.setValue(Integer.parseInt(w));
		}
		

		directed = new CheckBox();
		directed.setFieldLabel("Directed");
		if(elInfo == null) {
			directed.setValue(true);
		}
		else {
			directed.setValue(Boolean.parseBoolean(elInfo.getElementOption(ParameterTypes.Endings)));
		}

		noDetails = new CheckBox();
		noDetails.setFieldLabel("Draw line only");
		if(elInfo == null) {
			noDetails.setValue(false);	
		}
		else {
			noDetails.setValue(Boolean.parseBoolean(elInfo.getElementOption(ParameterTypes.Details)));
		}

		connectsGroup = new CheckBox();
		connectsGroup.setFieldLabel("Connects grouped boxes");
		if(elInfo == null) {
			connectsGroup.setValue(false);
		}
		else {
			connectsGroup.setValue(Boolean.parseBoolean(elInfo.getElementOption(ParameterTypes.ConnectsGroup)));
		}

		Button saveButton = new Button("Save") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (validateFields()) {
					ElementInfo newElement = createNewElement(elInfo);

					newElement.addElementOption(ParameterTypes.ConnectsGroup, connectsGroup.getValue().toString());
					
					if(elInfo != null) {
						CreateModifyAndDeleteOntology.contributionToElement.remove(elInfo);
					}
					CreateModifyAndDeleteOntology.contributionToElement.put(newElement, new Vector<ElementInfo>());

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology, CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					CreateModifyAndDeleteOntology.getInstance().createPreviewForElement(newElement);
					
					LASADInfo.display("Info", "Ontology will be updated on server...");
					
					CreateModifyAndDeleteOntology.clearElementActions();
				}
			}
		};

		addTopLevelElementForm.setBodyBorder(false);

		addTopLevelElementForm.add(title);
		addTopLevelElementForm.add(directed);
		addTopLevelElementForm.add(noDetails);
		addTopLevelElementForm.add(connectsGroup);
		addTopLevelElementForm.add(panelColorRadioGroup);
		addTopLevelElementForm.add(lineColorRadioGroup);
		addTopLevelElementForm.add(sfLineWidth);
		addTopLevelElementForm.add(sfWidth);

		addTopLevelElementForm.addButton(previewButton);
		addTopLevelElementForm.addButton(saveButton);

		addTopLevelElementForm.layout();

		return addTopLevelElementForm;
	}

	private void initDelRelationPanel() {
		delRelationForm = new FormPanel();
		delRelationForm.setButtonAlign(HorizontalAlignment.LEFT);
		delRelationForm.setHeading("Delete an existing relation...");

		relations = new SimpleComboBox<String>();
		relations.setFieldLabel("Relation");
		relations.setEmptyText("Choose a relation");
		relations.setForceSelection(true);
		relations.setTriggerAction(TriggerAction.ALL);

		Button deleteButton = new Button("Delete") {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if (relations.validate()) {

					String relationName = relations.getSimpleValue();
					CreateModifyAndDeleteOntology.contributionToElement.remove(relationNamesToElementInfo.get(relationName));

					relations.setAllowBlank(true);
					relations.remove(relationName);
					relations.reset();
					relations.setAllowBlank(false);

					String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology, CreateModifyAndDeleteOntology.contributionToElement.keySet());
					communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));

					LASADInfo.display("Info", "Ontology will be updated on server...");
					CreateModifyAndDeleteOntology.clearElementActions();

				} else {
					LASADInfo.display("Error", "Please select a relation");
				}
			}
		};

		delRelationForm.setBodyBorder(false);
		delRelationForm.add(relations);
		delRelationForm.addButton(deleteButton);

		add(delRelationForm);
	}

	private boolean validateFields() {
		boolean ok = true;

		if (!title.validate()) {
			ok = false;
			LASADInfo.display("Error", "Please specify a concrete title for the node");
		}

		if (relationNamesToElementInfo.keySet().contains(title.getValue())) {
			ok = false;
			LASADInfo.display("Error", "There is already a relation with this title");
		}

		// To support "old" by-hand created ontologies, which do not use the title as id
		for (String s : relationNamesToElementInfo.keySet()) {
			ElementInfo ei = relationNamesToElementInfo.get(s);
			if (title.getValue().equals(ei.getElementID())) {
				ok = false;
				LASADInfo.display("Error", "There is already a relation with this title");
				break;
			}
		}

		return ok;
	}
}