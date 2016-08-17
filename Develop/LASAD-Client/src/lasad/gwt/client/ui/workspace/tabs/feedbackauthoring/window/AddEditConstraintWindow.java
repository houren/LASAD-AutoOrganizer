package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ComparisonUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.CustomDualListField;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.InputFieldAndListField;
import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2ConstNumComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2NumOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2VarNumComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Operator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2SetOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2StringOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2VarSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2VarStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2SetOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2StringOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2VarSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2VarStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.ontology.descr.ComparisonDataType;
import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.CreatorPropDescr;
import lasad.shared.dfki.meta.ontology.descr.FirstTsPropDescr;
import lasad.shared.dfki.meta.ontology.descr.LastTsPropDescr;
import lasad.shared.dfki.meta.ontology.descr.ModifiersPropDescr;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.ui.Widget;

/**
 * Add Edit Constraint window
 * @author Anahuac
 *
 */
public class AddEditConstraintWindow {
	
	private final Window window = new Window();
	private ElementConstraintsWindow parentRef;
	
	private ContentPanel compareAgainstPanel;
	private ContentPanel operatorPanel;
	private ContentPanel inputValuePanel;
	
	private PropDescr property;
//	private String parentId;
	private String parentName;
//	private String childId;
	private String parentDesc;
	private StructuralAnalysisTypeManipulator patManipulator;
	private ElementVariable elementVar;
	
	private ComboBox<ElementModel> compareAgaintsCombo;
	private ComboBox<ElementModel> operatorCombo;
	
	private TextField<String> strValueField;
	private ComboBox<ElementModel> valueCombo;
	private NumberField numValueField;
	private ComboBox<ElementModel> plusMinusCombo;
	private NumberField extraNumValueField;
	private CustomDualListField<ElementModel> dualList;
	private InputFieldAndListField<ElementModel> inputFieldAndListField;
	private Label nothing2Compare;
	
	private Comparison comparison;
	
	/* @param parentId propertyId
	 * @param childId if it is null -> add mode, otherwise -> edit mode
	 * @param parentRef reference to ElementConstraintsWindow
	 */
	public AddEditConstraintWindow(ElementVariable elementVar, String parentId, String parentName, String childId, String parentDesc, ElementConstraintsWindow parentRef){
		this.elementVar = elementVar;
//		this.parentId = parentId;
		this.parentName = parentName;
//		this.childId = childId;
		this.parentDesc = parentDesc;
		this.parentRef = parentRef;
		this.comparison = parentRef.getComparison(childId);
		if (parentRef.isStandardPropDescr(parentId)){
			property = parentRef.getStandardPropDescr(parentId);
		} else {
			property = parentRef.getNonStandardPropDescr(parentId);
		}
		patManipulator = this.parentRef.getPatManipulator();
		nothing2Compare = new Label(FeedbackAuthoringStrings.NO_OTHER_VAR_2_COMP_LABEL);
		nothing2Compare.setStyleAttribute("color", "#FF0000");
		initSelectAgentWindow();
	}
	
	public void show(){
		window.show();
	}

	private void initSelectAgentWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("450px", "350px");
		window.setResizable(false);
		if(comparison == null){
			window.setHeading(FeedbackAuthoringStrings.ADD_CONSTRAINT_LABEL
					+ " " + FeedbackAuthoringStrings.FOR_LABEL 
					+ " " + FeedbackAuthoringStrings.PROPERTY_LABEL + ":"
					+ " " + parentName + " " + parentDesc);
		} else {
			window.setHeading(FeedbackAuthoringStrings.EDIT_CONSTRAINT_LABEL
					+ " " + FeedbackAuthoringStrings.FOR_LABEL 
					+ " " + FeedbackAuthoringStrings.PROPERTY_LABEL + ":"
					+ " " + parentName + " " + parentDesc);
		}
		
		window.setLayout(new FitLayout());
		window.add(getPanelContent());
		window.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//TODO check if we need to do something else before closing
			}
		});
		
		Button saveBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				ElementModel operElem = (ElementModel) operatorCombo.getValue();
				if(operElem != null){
					String operatorId = operElem.getValue(GridElementLabel.ID);
					ElementModel value = (ElementModel) compareAgaintsCombo.getValue();
					if(value != null){
						//String compareAgaintsId = value.getValue(GridElementLabel.ID);
						//setComparisonRightHandSide(compareAgaintsId, operatorId);
						if(setOperatorAndRightHandSide(operatorId)){
							window.hide();
							saveInfo();
						} else {
							MessageBox.info(FeedbackAuthoringStrings.ERROR_LABEL,
									"An error occurred while saving the Constraint. Please check that" +
									"the values are correct", null);
						}
					} else{
						MessageBox.info(FeedbackAuthoringStrings.ERROR_LABEL,
								FeedbackAuthoringStrings.OPTION_LABEL + " " +
								FeedbackAuthoringStrings.FOR_LABEL + " " +
								FeedbackAuthoringStrings.COMPARE_AGAINST_LABEL + " " +
								FeedbackAuthoringStrings.NOT_SELECTED_LABEL, null);
					}
				} else {
					MessageBox.info(FeedbackAuthoringStrings.ERROR_LABEL,
							FeedbackAuthoringStrings.OPTION_LABEL + " " +
							FeedbackAuthoringStrings.FOR_LABEL + " " +
							FeedbackAuthoringStrings.OPERATOR_LABEL + " " +
							FeedbackAuthoringStrings.NOT_SELECTED_LABEL, null);
				}
				
		    }
		});
		window.addButton(saveBtn);
		
		Button cancelBtn = new Button(FeedbackAuthoringStrings.CANCEL_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				window.hide();  
			}  
		});
		window.addButton(cancelBtn);
		window.layout();
	}
	
	private ContentPanel getPanelContent(){
		return createMainPanel();
	}
	
	private ContentPanel createMainPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
//		panel.setHeading("hola");
		panel.setBodyBorder(false);
		panel.setScrollMode(Scroll.AUTOY);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		panel.setWidth(550);
		panel.setHeight(500);
		panel.add(createCompareAgainstPanel(), new RowData(1.0, Style.DEFAULT, new Margins(3)));
		panel.add(createOperatorPanel(), new RowData(1.0, Style.DEFAULT, new Margins(3)));
		panel.add(createInputValuePanel(), new RowData(1.0, Style.DEFAULT, new Margins(3)));
//		panel.add(initNumberRestrictionPanel(), new RowData(1.0, 0.2, new Margins(3)));
		
		return panel;
	}
	
	/*
	 * creates a panel with a select field displaying the following options:
   	 * constant value
   	 * variable value
     * constant set
     * variable set
	 */
	private Widget createCompareAgainstPanel(){
		compareAgainstPanel = new ContentPanel();
		compareAgainstPanel.setLayout(new FitLayout());
		compareAgainstPanel.setHeaderVisible(false);
		compareAgainstPanel.setBodyBorder(false);
		
//		Label infoLabel = new Label(FeedbackAuthoringStrings.COMPARE_AGAINST_LABEL);
//		compareAgainstPanel.add(infoLabel);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(400);
		fPanel.setLabelWidth(100);
		
		ListStore<ElementModel> compareAgaintsStore = new ListStore<ElementModel>();
		compareAgaintsStore.add(Data2ModelConverter.getMapAsModelList(getValidComparisonDataType()));
	  
		compareAgaintsCombo = new ComboBox<ElementModel>();
	    compareAgaintsCombo.setFieldLabel(FeedbackAuthoringStrings.COMPARE_AGAINST_LABEL);
	    compareAgaintsCombo.setDisplayField(GridElementLabel.NAME);  
	    compareAgaintsCombo.setWidth(100);  
	    compareAgaintsCombo.setStore(compareAgaintsStore);
	    compareAgaintsCombo.setForceSelection(true);  
	    compareAgaintsCombo.setTriggerAction(TriggerAction.ALL);
//	    if(compareAgaintsStore.getCount() > 0){
//	    	compareAgaintsCombo.setValue(compareAgaintsStore.getAt(0));
//	    }
	    compareAgaintsCombo.addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				ElementModel operElem = se.getSelectedItem();
				String optionId = operElem.getValue(GridElementLabel.ID);
				//String optionName = operElem.getValue(GridElementLabel.NAME);
				operatorCombo.setEnabled(true);
				Operator oper = createComparisonObj(optionId);
				//update the values available in operatorCombo based on Operator
				updateValuesOfOperatorCombo(oper);
				resetInputValuePanel(); //empty input value panel
			}
		});
	    fPanel.add(compareAgaintsCombo);
	    compareAgainstPanel.add(fPanel);
		
		return compareAgainstPanel;
	}
	private void resetInputValuePanel(){
		inputValuePanel.removeAll();
		inputValuePanel.setLayout(new FitLayout());
//		inputValuePanel.layout();
//		inputValuePanel.repaint();
	}
	
	private Widget createOperatorPanel(){
		operatorPanel = new ContentPanel();
		operatorPanel.setLayout(new FitLayout());
		operatorPanel.setHeaderVisible(false);
		operatorPanel.setBodyBorder(false);
		
		FormPanel fPanel = new FormPanel();
		fPanel.setHeaderVisible(false);
		fPanel.setBodyBorder(false);
		fPanel.setWidth(400);
		fPanel.setLabelWidth(100);
		
		ListStore<ElementModel> operatorStore = new ListStore<ElementModel>();
	  
		// http://www.sencha.com/forum/showthread.php?81366-ComboBox-default-value
		operatorCombo = new ComboBox<ElementModel>();
		operatorCombo.setId("constraintOperatorCombo");
	    operatorCombo.setFieldLabel(FeedbackAuthoringStrings.OPERATOR_LABEL);
	    operatorCombo.setDisplayField(GridElementLabel.VAL);  
	    operatorCombo.setWidth(100);  
	    operatorCombo.setStore(operatorStore);  
	    operatorCombo.setForceSelection(true); 
	    operatorCombo.setTriggerAction(TriggerAction.ALL);
	    operatorCombo.setEnabled(false);
	    operatorCombo.setValueField(GridElementLabel.NAME);

	    operatorCombo.addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				ElementModel operElem = se.getSelectedItem();
				if(operElem != null){
					String optionId = operElem.getValue(GridElementLabel.ID);
					String optionName = operElem.getValue(GridElementLabel.NAME);
					optionName = ComparisonUtil.decodeHTML(optionName);
					operatorCombo.setRawValue(optionName);

					
					ElementModel value = (ElementModel) compareAgaintsCombo.getValue();
					if(value != null){
						String compareAgaintsId = value.getValue(GridElementLabel.ID);
						setComparisonOperatorAndInitDisplayValuePanel(compareAgaintsId, optionId);
					}
				}
				
				
			}
		});
	    fPanel.add(operatorCombo);
	    operatorPanel.add(fPanel);
		
		return operatorPanel;
	}

	private Widget createInputValuePanel(){
		inputValuePanel = new ContentPanel();
		inputValuePanel.setLayout(new FitLayout());
		inputValuePanel.setHeaderVisible(false);
		inputValuePanel.setBodyBorder(false);
		/*
		 * the size of the panel MUST be defined, otherwise
		 * the panel will not render properly and the elements will no be displayed
		 * because it will have width=0 and height=0.
		 */
		inputValuePanel.setWidth(400);
		inputValuePanel.setHeight(150);
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		//panel.add(new Label("test text" + System.currentTimeMillis()));
		
		inputValuePanel.add(panel);
		inputValuePanel.layout();

		return inputValuePanel;
	}
	
	private Widget createStrValuePanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
//		panel.setWidth(400);
//		panel.setHeight(300);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(100);
		
		strValueField = new TextField<String>();
		strValueField.setFieldLabel(FeedbackAuthoringStrings.VALUE_LABEL);
		strValueField.setAllowBlank(false);
		strValueField.setId("constantStrField");
	    formPanel.add(strValueField);
	    
	    panel.add(formPanel);
		
		return panel;
	}
	
	private Widget createNumValuePanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(100);
		
		numValueField = new NumberField();
		numValueField.setFieldLabel(FeedbackAuthoringStrings.VALUE_LABEL);
		numValueField.setAllowBlank(false);
		numValueField.setWidth(50);
		numValueField.setPropertyEditorType(Integer.class);
		numValueField.setId("constantNumField");
		numValueField.setAllowBlank(false);
	    formPanel.add(numValueField);
	    
	    panel.add(formPanel);
		
		return panel;
	}
	
	private Widget createValueComboPanel(boolean isNum){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
//		panel.setWidth(400);
//		panel.setHeight(300);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(100);
		
		nothing2Compare.setVisible(false);
		formPanel.add(nothing2Compare);
		ListStore<ElementModel> comboStore = new ListStore<ElementModel>();
		valueCombo = new ComboBox<ElementModel>();
		valueCombo.setFieldLabel(FeedbackAuthoringStrings.VALUE_LABEL);//VARIABLE_VALUE_UP_LABEL
		valueCombo.setDisplayField(GridElementLabel.NAME);  
		valueCombo.setWidth(150);  
		valueCombo.setStore(comboStore);  
		valueCombo.setTypeAhead(false);
		valueCombo.setForceSelection(true);
		valueCombo.setTriggerAction(TriggerAction.ALL);
		valueCombo.setId("variableField");
	    formPanel.add(valueCombo);
	    
	    if(isNum){
	    	Map<String, String> valuesMap = new LinkedHashMap<String, String>();
			valuesMap.put(ComparisonUtil.PLUS_SIGN, ComparisonUtil.PLUS_SIGN);
			valuesMap.put(ComparisonUtil.MINUS_SIGN, ComparisonUtil.MINUS_SIGN);
			ListStore<ElementModel> operComboStore = new ListStore<ElementModel>();
			operComboStore.add(Data2ModelConverter.getMapAsModelList(valuesMap));
			plusMinusCombo = new ComboBox<ElementModel>();
			plusMinusCombo.setFieldLabel(FeedbackAuthoringStrings.OPERATOR_LABEL);
			plusMinusCombo.setDisplayField(GridElementLabel.NAME);  
			plusMinusCombo.setWidth(150);  
			plusMinusCombo.setStore(operComboStore);  
			plusMinusCombo.setTypeAhead(false);  
			plusMinusCombo.setTriggerAction(TriggerAction.ALL);
			plusMinusCombo.setId("plusMinusCombo");
			plusMinusCombo.setValue(operComboStore.getAt(0));
			plusMinusCombo.setForceSelection(true);
		    formPanel.add(plusMinusCombo);
			
			extraNumValueField = new NumberField();
			extraNumValueField.setFieldLabel(FeedbackAuthoringStrings.OFFSET_VALUE_LABEL);
			extraNumValueField.setAllowBlank(false);
			extraNumValueField.setWidth(50);
			extraNumValueField.setPropertyEditorType(Integer.class);
			extraNumValueField.setId("extraNumValueField");
			extraNumValueField.setValue(0);
			extraNumValueField.setAllowBlank(false);
		    formPanel.add(extraNumValueField);
	    }
	    
	    panel.add(formPanel);
		
		return panel;
	}
	
	private Widget createInputFieldAndListField(){
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
//		panel.setWidth(400);
//		panel.setHeight(300);
		
		Label availMsgTypesLabel = new Label(FeedbackAuthoringStrings.TYPE_A_VALUE_LABEL);
		availMsgTypesLabel.setStyleAttribute("color", "#000000");
	    Label selMsgTypesLabel = new Label(FeedbackAuthoringStrings.DEFINED_VALUES_LABEL);
	    selMsgTypesLabel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td.setStyle("style='padding-left: 1px;'");
	    TableData td1 = new TableData();
	    td1.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td1.setStyle("style='padding-left: 120px;'");

	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setAutoWidth(true);
	    hp.setHeight(20);
	    hp.add(availMsgTypesLabel, td);
	    hp.add(new Label(""), td1);
	    hp.add(selMsgTypesLabel, td);
	    panel.add(hp, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
	    Vector<Integer> buttonsToRemoveList = new Vector<Integer>();
//	    buttonsToRemoveList.add(InputFieldAndListField.ALL_LEFT_BUTTON);
//	    buttonsToRemoveList.add(InputFieldAndListField.UP_BUTTON);
//	    buttonsToRemoveList.add(InputFieldAndListField.DOWN_BUTTON);
		
	    inputFieldAndListField = new InputFieldAndListField<ElementModel>(buttonsToRemoveList);
	    inputFieldAndListField.setMode(InputFieldAndListField.Mode.INSERT);
		panel.add(inputFieldAndListField, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
		inputFieldAndListField.setWidth(400);		

//		ListStore<ElementModel> leftStore = new ListStore<ElementModel>();
		ListStore<ElementModel> rightStore = new ListStore<ElementModel>();

//		ListField<ElementModel> leftList = inputFieldAndListField.getFromList();
//		leftList.setStore(leftStore);
//		leftList.setDisplayField(GridElementLabel.NAME);
//		leftList.setWidth(165);

		ListField<ElementModel> rightList = inputFieldAndListField.getToList();
		rightList.setStore(rightStore);
		rightList.setDisplayField(GridElementLabel.NAME);
		rightList.setWidth(165);
		
		return panel;
	}
	
	private Widget createDualListPanel(){
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
//		panel.setWidth(400);
//		panel.setHeight(300);
		
		Label availMsgTypesLabel = new Label(FeedbackAuthoringStrings.AVAILABLE_VALUES_LABEL);
		availMsgTypesLabel.setStyleAttribute("color", "#000000");
	    Label selMsgTypesLabel = new Label(FeedbackAuthoringStrings.SELECTED_VALUES_LABEL);
	    selMsgTypesLabel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td.setStyle("style='padding-left: 1px;'");
	    TableData td1 = new TableData();
	    td1.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td1.setStyle("style='padding-left: 120px;'");

	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setAutoWidth(true);
	    hp.setHeight(20);
	    hp.add(availMsgTypesLabel, td);
	    hp.add(new Label(""), td1);
	    hp.add(selMsgTypesLabel, td);
	    panel.add(hp, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
	    Vector<Integer> buttonsToRemoveList = new Vector<Integer>();
	    buttonsToRemoveList.add(CustomDualListField.ALL_LEFT_BUTTON);
	    buttonsToRemoveList.add(CustomDualListField.ALL_RIGHT_BUTTON);
		
		dualList = new CustomDualListField<ElementModel>(buttonsToRemoveList);
		dualList.setMode(CustomDualListField.Mode.INSERT);
		panel.add(dualList, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
		dualList.setWidth(400);		

		ListStore<ElementModel> leftStore = new ListStore<ElementModel>();
		ListStore<ElementModel> rightStore = new ListStore<ElementModel>();

		ListField<ElementModel> leftList = dualList.getFromList();
		leftList.setStore(leftStore);
		leftList.setDisplayField(GridElementLabel.NAME);
		leftList.setWidth(165);

		ListField<ElementModel> rightList = dualList.getToList();
		rightList.setStore(rightStore);
		rightList.setDisplayField(GridElementLabel.NAME);
		rightList.setWidth(165);
		
		return panel;
	}
	
	
	/*
	 * this method uses the ComparisonDataType of the property to determine 
	 * the valid comparisons options
	 * -string
	 * String2ConstSetComparison
	 * String2ConstStringComparison
	 * String2VarSetComparison
	 * String2VarStringComparison
	 * -number
	 * Num2ConstNumComparison
	 * Num2VarNumComparison
	 * -set
	 * Set2ConstSetComparison
	 * Set2ConstStringComparison
	 * Set2VarSetComparison
	 * Set2VarStringComparison
	 */
	private Map<String, String> getValidComparisonDataType(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		ComparisonDataType cdt = property.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID);
		switch(cdt){
			case STRING:{
				map.put(String.valueOf(ComparisonUtil.CONSTANT_VALUE), FeedbackAuthoringStrings.CONSTANT_VALUE_LABEL);
				map.put(String.valueOf(ComparisonUtil.VARIABLE_VALUE), FeedbackAuthoringStrings.VARIABLE_VALUE_LABEL);
				map.put(String.valueOf(ComparisonUtil.CONSTANT_SET), FeedbackAuthoringStrings.CONSTANT_SET_LABEL);
				map.put(String.valueOf(ComparisonUtil.VARIABLE_SET), FeedbackAuthoringStrings.VARIABLE_SET_LABEL);
				break;
			}
			case NUMBER:{
				map.put(String.valueOf(ComparisonUtil.CONSTANT_VALUE), FeedbackAuthoringStrings.CONSTANT_VALUE_LABEL);
				map.put(String.valueOf(ComparisonUtil.VARIABLE_VALUE), FeedbackAuthoringStrings.VARIABLE_VALUE_LABEL);
				break;
			}
			case SET:{
				map.put(String.valueOf(ComparisonUtil.CONSTANT_VALUE), FeedbackAuthoringStrings.CONSTANT_VALUE_LABEL);
				map.put(String.valueOf(ComparisonUtil.VARIABLE_VALUE), FeedbackAuthoringStrings.VARIABLE_VALUE_LABEL);
				map.put(String.valueOf(ComparisonUtil.CONSTANT_SET), FeedbackAuthoringStrings.CONSTANT_SET_LABEL);
				map.put(String.valueOf(ComparisonUtil.VARIABLE_SET), FeedbackAuthoringStrings.VARIABLE_SET_LABEL);
				break;
			}
			default:{
				//...
			}
		}
		return map;
		
	}
	
//	/*
//	 * updates the values available in operatorCombo based on ComparisonDataType
//	 * and the compare against option selected 
//	 */ 
//	private void updateValuesOfOperatorCombo(String compareAgainstOption){
//		operatorCombo.clear();
//		ListStore<ElementModel> operatorStore = operatorCombo.getStore();
//		operatorStore.removeAll();
//		operatorStore.add(Data2ModelConverter.getOperatorMapAsModelList(getValidOperators(compareAgainstOption)));
//	}
	private void updateValuesOfOperatorCombo(Operator operator){
		operatorCombo.clear();
		ListStore<ElementModel> operatorStore = operatorCombo.getStore();
		operatorStore.removeAll();
		operatorStore.add(Data2ModelConverter.getOperatorMapAsModelList(getValidOperators(operator)));
		//select the first element
//		if(operatorStore.getCount() > 0){
//			operatorCombo.setValue(operatorStore.getAt(0));
//	    }
	}
	
	private Map<String, String> getValidOperators(Operator operator){
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if(operator != null){
			if(operator instanceof Num2NumOperator){
				Num2NumOperator[] values = Num2NumOperator.values();
				if(values != null){
					for(int i=0; i< values.length; i++){
						map.put(values[i].getName(), ComparisonUtil.fromOperId2Html(values[i].getName()));
					}
				}
			} else if(operator instanceof Set2SetOperator){
				Set2SetOperator[] values = Set2SetOperator.values();
				if(values != null){
					for(int i=0; i< values.length; i++){
						map.put(values[i].getName(), ComparisonUtil.fromOperId2Html(values[i].getName()));
					}
				}
			} else if(operator instanceof Set2StringOperator){
				Set2StringOperator[] values = Set2StringOperator.values();
				if(values != null){
					for(int i=0; i< values.length; i++){
						map.put(values[i].getName(), ComparisonUtil.fromOperId2Html(values[i].getName()));
					}
				}
			} else if(operator instanceof String2SetOperator){
				String2SetOperator[] values = String2SetOperator.values();
				if(values != null){
					for(int i=0; i< values.length; i++){
						map.put(values[i].getName(), ComparisonUtil.fromOperId2Html(values[i].getName()));
					}
				}
			} else if(operator instanceof String2StringOperator){
				String2StringOperator[] values = String2StringOperator.values();
				if(values != null){
					for(int i=0; i< values.length; i++){
						map.put(values[i].getName(), ComparisonUtil.fromOperId2Html(values[i].getName()));
					}
				}
			} else {
				FATDebug.print(FATDebug.ERROR, "[AddEditConstraintWindow][getValidOperators] unknown Operator:" + operator);
			}
		}
		return map;
	}
	
//	/*
//	 * this method uses the ComparisonDataType of the property to determine the valid operators
//	 */
//	private Map<String, String> getValidOperators(String compareAgainstOption){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		ComparisonDataType cdt = property.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID);
//		int option = Integer.parseInt(compareAgainstOption);
//		switch(cdt){
//			case STRING:{
//				switch(option){
//					case ComparisonUtil.CONSTANT_VALUE:
//					case ComparisonUtil.VARIABLE_VALUE:{
//						map.put(ComparisonUtil.EQUAL_ID, ComparisonUtil.EQUAL_OP);
//						map.put(ComparisonUtil.NOT_EQUAL_ID, ComparisonUtil.NOT_EQUAL_OP);
//						break;
//					}
//					case ComparisonUtil.CONSTANT_SET:
//					case ComparisonUtil.VARIABLE_SET:{
//						map.put(ComparisonUtil.IN_ID, ComparisonUtil.IN_OP);
//						map.put(ComparisonUtil.NOT_IN_ID, ComparisonUtil.NOT_IN_OP);
//						break;
//					}
//					default:{
//					}
//				}
//				break;
//			}
//			case NUMBER:{
//				switch(option){
//					case ComparisonUtil.CONSTANT_VALUE:
//					case ComparisonUtil.VARIABLE_VALUE:{
//						map.put(ComparisonUtil.GREATER_ID, ComparisonUtil.GREATER_OP);
//						map.put(ComparisonUtil.GREATER_OR_EQUAL_ID, ComparisonUtil.GREATER_OR_EQUAL_OP);
//						map.put(ComparisonUtil.LESS_ID, ComparisonUtil.LESS_OP);
//						map.put(ComparisonUtil.LESS_OR_EQUAL_ID, ComparisonUtil.LESS_OR_EQUAL_OP);
//						map.put(ComparisonUtil.EQUAL_ID, ComparisonUtil.EQUAL_OP);
//						map.put(ComparisonUtil.NOT_EQUAL_ID, ComparisonUtil.NOT_EQUAL_OP);
//						break;
//					}
//	//				case ComparisonUtil.CONSTANT_SET:
//	//				case ComparisonUtil.VARIABLE_SET:{
//	//					map.put(ComparisonUtil.IN_STR, ComparisonUtil.IN_OP);
//	//					map.put(ComparisonUtil.NOT_IN_STR, ComparisonUtil.NOT_IN_OP);
//	//					break;
//	//				}
//					default:{
//					}
//				}
//				break;
//			}
//			case SET:{
//				switch(option){
//					case ComparisonUtil.CONSTANT_VALUE:
//					case ComparisonUtil.VARIABLE_VALUE:{
//						map.put(ComparisonUtil.CONTAINS_ID, ComparisonUtil.CONTAINS_OP);
//						map.put(ComparisonUtil.NOT_CONTAINS_ID, ComparisonUtil.NOT_CONTAINS_OP);
//						break;
//					}
//					case ComparisonUtil.CONSTANT_SET:
//					case ComparisonUtil.VARIABLE_SET:{
//						map.put(ComparisonUtil.INTERSECT_ID, ComparisonUtil.INTERSECT_OP);
//						map.put(ComparisonUtil.NOT_INTERSECT_ID, ComparisonUtil.NOT_INTERSECT_OP);
//						map.put(ComparisonUtil.EQUAL_ID, ComparisonUtil.EQUAL_OP);
//						map.put(ComparisonUtil.NOT_EQUAL_ID, ComparisonUtil.NOT_EQUAL_OP);
//						map.put(ComparisonUtil.SUBSET_ID, ComparisonUtil.SUBSET_OP);
//						map.put(ComparisonUtil.NOT_SUBSET_ID, ComparisonUtil.NOT_SUBSET_OP);
//						map.put(ComparisonUtil.SUPERSET_ID, ComparisonUtil.SUPERSET_OP);
//						map.put(ComparisonUtil.NOT_SUPERSET_ID, ComparisonUtil.NOT_SUPERSET_OP);
//						break;
//					}
//					default:{
//					}
//				}
//				break;
//			}
//			default:{
//				//...
//			}
//		}
//		return map;
//		
//	}
	
	/*
	 * initializes comparison object based on property's ComparisonDataType and selected compareAgainstOpt
	 */
	private Operator createComparisonObj(String compareAgainstOpt){
		ComparisonDataType cdt = property.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID);
		//ComparisonGroup cg = property.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID);
		Operator operator = null;
		
		int compareAgainstOption = Integer.parseInt(compareAgainstOpt);
		switch(cdt){
			case STRING:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						comparison = new String2ConstStringComparison();
						operator = String2StringOperator.EQUAL;
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						comparison = new String2VarStringComparison();
						operator = String2StringOperator.EQUAL;
						break;
					}
					case ComparisonUtil.CONSTANT_SET:{
						comparison = new String2ConstSetComparison();
						operator = String2SetOperator.IN;
						break;
					}
					case ComparisonUtil.VARIABLE_SET:{
						comparison = new String2VarSetComparison();
						operator = String2SetOperator.IN;
						break;
					}
					default:{
					}
				}
				break;
			}
			case NUMBER:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						comparison = new Num2ConstNumComparison();
						operator = Num2NumOperator.EQUAL;
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						comparison = new Num2VarNumComparison();
						operator = Num2NumOperator.EQUAL;
						break;
					}
					default:{
					}
				}
				break;
			}
			case SET:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						comparison = new Set2ConstStringComparison();
						operator = Set2StringOperator.CONTAINS;
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						comparison = new Set2VarStringComparison();
						operator =  Set2StringOperator.CONTAINS;
						break;
					}
					case ComparisonUtil.CONSTANT_SET:{
						comparison = new Set2ConstSetComparison();
						operator = Set2SetOperator.EQUAL;
						break;
					}
					case ComparisonUtil.VARIABLE_SET:{
						comparison = new Set2VarSetComparison();
						operator = Set2SetOperator.EQUAL;
						break;
					}
					default:{
					}
				}
				break;
			}
			default:{
				//...
			}
		}
		if(comparison != null){
			ElementVariableProp lhsVarProp = elementVar.getPropVar(property.getPropID(), PropDescr.DEFAULT_COMPONENT_ID);
			comparison.setLeftExpr(lhsVarProp);
		}
		return operator;
	}
	
	
	/*
	 * sets the comparison operator and 
	 * displays the input value panel based on the Property, the ComparisonDataType
	 * and the selected compareAgainstOption
	 */
	private void setComparisonOperatorAndInitDisplayValuePanel(String compareAgainstOpt, String operatorId){		
		resetInputValuePanel();
		
		//String propId = property.getPropID();
		//boolean isStdProp = parentRef.isStandardPropDescr(propId);
		
		ComparisonDataType cdt = property.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID);
		ComparisonGroup cg = property.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID);
		int compareAgainstOption = Integer.parseInt(compareAgainstOpt);
		
		switch(cdt){
			case STRING:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						((String2ConstStringComparison)comparison).setOperator(String2StringOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createStrValuePanel());
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getAllTypesAsMap());
								break;
							}
							case USER:{
								inputValuePanel.add(createStrValuePanel());
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						((String2VarStringComparison)comparison).setOperator(String2StringOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksNonStdProps(cdt, cg));
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								//populateValueCombo(getBoxesAndLinksTypes());
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(cdt, cg, TypePropDescr.PROP_ID));
								break;
							}
							case USER:{
								inputValuePanel.add(createValueComboPanel(false));
								//populateValueCombo(getAllUserCreators());
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(cdt, cg, CreatorPropDescr.PROP_ID));
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.CONSTANT_SET:{
						((String2ConstSetComparison)comparison).setOperator(String2SetOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createInputFieldAndListField());
								break;
							}
							case TYPE:{
								inputValuePanel.add(createDualListPanel());
								populateDualList(getAllTypesAsMap(), new LinkedHashMap<String, String>());
								break;
							}
							case USER:{
								inputValuePanel.add(createInputFieldAndListField());
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.VARIABLE_SET:{
						((String2VarSetComparison)comparison).setOperator(String2SetOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksNonStdProps(ComparisonDataType.SET, cg)); //TODO is this ok?
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksStdProps(ComparisonDataType.SET, cg)); //TODO is this ok?
								break;
							}
							case USER:{
								inputValuePanel.add(createValueComboPanel(false));
								//populateValueCombo(getAllUserModifiers());
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(ComparisonDataType.SET, cg, ModifiersPropDescr.PROP_ID));
								break;
							}
							default:
								break;
						}
						break;
					}
					default:{
					}
				}
				break;
			}
			case NUMBER:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						((Num2ConstNumComparison)comparison).setOperator(Num2NumOperator.fromString(operatorId));
						switch(cg){
							case NUMBER:{
								inputValuePanel.add(createNumValuePanel());
								break;
							}
							case TS:{
								inputValuePanel.add(createNumValuePanel());
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						((Num2VarNumComparison)comparison).setOperator(Num2NumOperator.fromString(operatorId));
						switch(cg){
							case NUMBER:{
								inputValuePanel.add(createValueComboPanel(true));
								populateValueCombo(getBoxesAndLinksNonStdProps(cdt, cg));
								break;
							}
							case TS:{
								inputValuePanel.add(createValueComboPanel(true));
								//populateValueCombo(getBoxesAndLinksTS());
								Map<String, String> map = getBoxesAndLinksPropDescrOfStdProps(cdt, cg, FirstTsPropDescr.PROP_ID);
								map.putAll(getBoxesAndLinksPropDescrOfStdProps(cdt, cg, LastTsPropDescr.PROP_ID));
								populateValueCombo(map);
								break;
							}
							default:
								break;
						}
						break;
					}
					default:{
					}
				}
				break;
			}
			case SET:{
				switch(compareAgainstOption){
					case ComparisonUtil.CONSTANT_VALUE:{
						((Set2ConstStringComparison)comparison).setOperator(Set2StringOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createStrValuePanel());
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getAllTypesAsMap());
								break;
							}
							case USER:{
								inputValuePanel.add(createStrValuePanel());
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.VARIABLE_VALUE:{
						((Set2VarStringComparison)comparison).setOperator(Set2StringOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksNonStdProps(cdt, cg));
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								//populateValueCombo(getBoxesAndLinksTypes());
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(cdt, cg, TypePropDescr.PROP_ID));
								break;
							}
							case USER:{
								inputValuePanel.add(createValueComboPanel(false));
//								populateValueCombo(getAllUserCreators());
								//if(propId.equals(CreatorPropDescr.PROP_ID))
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(ComparisonDataType.STRING, cg, CreatorPropDescr.PROP_ID));
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.CONSTANT_SET:{
						((Set2ConstSetComparison)comparison).setOperator(Set2SetOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createInputFieldAndListField());
								break;
							}
							case TYPE:{
								inputValuePanel.add(createDualListPanel());
								populateDualList(getAllTypesAsMap(), new LinkedHashMap<String, String>());
								break;
							}
							case USER:{
								inputValuePanel.add(createInputFieldAndListField());
								break;
							}
							default:
								break;
						}
						break;
					}
					case ComparisonUtil.VARIABLE_SET:{
						((Set2VarSetComparison)comparison).setOperator(Set2SetOperator.fromString(operatorId));
						switch(cg){
							case TEXT:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksNonStdProps(cdt, cg));
								break;
							}
							case TYPE:{
								inputValuePanel.add(createValueComboPanel(false));
								populateValueCombo(getBoxesAndLinksStdProps(cdt, cg));
								break;
							}
							case USER:{
								inputValuePanel.add(createValueComboPanel(false));
								//populateValueCombo(getAllUserModifiers());
								populateValueCombo(getBoxesAndLinksPropDescrOfStdProps(cdt, cg, ModifiersPropDescr.PROP_ID));
								break;
							}
							default:
								break;
						}
						break;
					}
					default:{
					}
				}
				break;
			}
			default:{
				//...
			}
		}
		inputValuePanel.layout();
	}

	/*
	 * Used for variable comparisons
	 * Get all the available StdProps with the given compDataType and compGroup from created boxes and links
	 * Returns a map like(b1.prop, b1.prop), (b1.prop, b2.prop), ...
	 * @return Map<String, String>
	 */
	private Map<String, String> getBoxesAndLinksPropDescrOfStdProps(ComparisonDataType compDataType, ComparisonGroup compGroup, String propDescr){
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<NodeVariable> nodeVariableList =  patManipulator.getNodeVariables();
		List<LinkVariable> linkVariableList =  patManipulator.getLinkVariables();
		
		for(NodeVariable elem : nodeVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
			for(StandardPropDescr prop:stdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType
						&& prop.getPropID().equals(propDescr)){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		for(LinkVariable elem : linkVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
			for(StandardPropDescr prop:stdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
					&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType
					&& prop.getPropID().equals(propDescr)){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		//Look into NotNode and NotLink
		List<StructuralPattern> notPatternsList = new Vector<StructuralPattern>(patManipulator.getNotPatterns());
		for(StructuralPattern notPattern : notPatternsList){
			for(NodeVariable elem : notPattern.getNodeVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
				for(StandardPropDescr prop:stdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
							&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType
							&& prop.getPropID().equals(propDescr)){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
			for(LinkVariable elem : notPattern.getLinkVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
				for(StandardPropDescr prop:stdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType
						&& prop.getPropID().equals(propDescr)){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
		}
		
		return map;
	}
	
	
	/*
	 * Get all the available types for boxes or links (depending on element)
	 * from the ontology.
	 * @return Set<String>
	 */
	private Set<String> getAllTypes(){
		boolean isNodeVar = (elementVar instanceof NodeVariable);
		Set<String> possibleTypes = null;
		if(isNodeVar){
			possibleTypes = patManipulator.getOntology().getNodeTypes();
		} else{
			possibleTypes = patManipulator.getOntology().getLinkTypes();
		}
		if(possibleTypes == null){
			possibleTypes = new HashSet<String>();
		}
		
		return possibleTypes;
	}
	/*
	 * Get all the available types for boxes or links (depending on element)
	 * from the ontology.
	 * //b1.type, b2.type, l3.type...
	 * @return Set<String>
	 */
	private Map<String, String> getAllTypesAsMap(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		for(String tmp : getAllTypes()){
			map.put(tmp, tmp);
		}
		return map;
	}
	
//	/*
//	 * Get all the available types for created boxes and links
//	 * Create map like(b1.type, b1.type), (b2.type, b2.type), (l3.type, l3.type)...
//	 * @return Map<String, String>
//	 */
//	private Map<String, String> getBoxesAndLinksTypes(){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		List<NodeVariable> nodeVariableList =  patManipulator.getNodeVariables();
//		List<LinkVariable> linkVariableList =  patManipulator.getLinkVariables();
//		
//		for(NodeVariable elem : nodeVariableList){
//			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
//			String type = new String(elemId + "." + TypePropDescr.PROP_ID);
//			String typeLabel = (elem.getName() != null)? (elem.getName() + "." + TypePropDescr.PROP_ID) : (elemId + "." + TypePropDescr.PROP_ID);
//			map.put(type, typeLabel);
//		}
//		for(LinkVariable elem : linkVariableList){
//			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
//			String type = new String(elemId + "." + TypePropDescr.PROP_ID);
//			String typeLabel = (elem.getName() != null)? (elem.getName() + "." + TypePropDescr.PROP_ID) : (elemId + "." + TypePropDescr.PROP_ID);
//			map.put(type, typeLabel);
//		}
//		return map;
//	}
	
//	/*
//	 * Get all the available TS (TimeStamp) for created boxes and links
//	 * Create map like(b1.first-ts, b1.first-ts), (b1.last-ts, b2.last-ts), ...
//	 * @return Map<String, String>
//	 */
//	private Map<String, String> getBoxesAndLinksTS(){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		List<NodeVariable> nodeVariableList =  patManipulator.getNodeVariables();
//		List<LinkVariable> linkVariableList =  patManipulator.getLinkVariables();
//		
//		for(NodeVariable elem : nodeVariableList){
//			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
//			String first = new String(elemId + "." + FirstTsPropDescr.PROP_ID);
//			String last = new String(elemId + "." + LastTsPropDescr.PROP_ID);
//			String firstLabel = (elem.getName() != null)? (elem.getName() + "." + FirstTsPropDescr.PROP_ID) : (elemId + "." + FirstTsPropDescr.PROP_ID);
//			String lastLabel = (elem.getName() != null)? (elem.getName() + "." + LastTsPropDescr.PROP_ID) : (elemId + "." + LastTsPropDescr.PROP_ID);
//			map.put(first, firstLabel);
//			map.put(last, lastLabel);
//		}
//		for(LinkVariable elem : linkVariableList){
//			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
//			String first = new String(elemId + "." + FirstTsPropDescr.PROP_ID);
//			String last = new String(elemId + "." + LastTsPropDescr.PROP_ID);
//			String firstLabel = (elem.getName() != null)? (elem.getName() + "." + FirstTsPropDescr.PROP_ID) : (elemId + "." + FirstTsPropDescr.PROP_ID);
//			String lastLabel = (elem.getName() != null)? (elem.getName() + "." + LastTsPropDescr.PROP_ID) : (elemId + "." + LastTsPropDescr.PROP_ID);
//			map.put(first, firstLabel);
//			map.put(last, lastLabel);
//		}
//		return map;
//	}
	
	/*
	 * Used for variable comparisons
	 * Get all the available NonStdProps with the given compDataType and compGroup from created boxes and links
	 * Create map like(b1.prop, b1.prop), (b1.prop, b2.prop), ...
	 * @return Map<String, String>
	 */
	private Map<String, String> getBoxesAndLinksNonStdProps(ComparisonDataType compDataType, ComparisonGroup compGroup){
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<NodeVariable> nodeVariableList =  patManipulator.getNodeVariables();
		List<LinkVariable> linkVariableList =  patManipulator.getLinkVariables();
		
		for(NodeVariable elem : nodeVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<NonStandardPropDescr> nonStdPropDescList = elem.getNonStandardPropDescrs(patManipulator.getOntology());
			for(NonStandardPropDescr prop:nonStdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		for(LinkVariable elem : linkVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<NonStandardPropDescr> nonStdPropDescList = elem.getNonStandardPropDescrs(patManipulator.getOntology());
			for(NonStandardPropDescr prop:nonStdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
					&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		//Look into NotNode and NotLink
		List<StructuralPattern> notPatternsList = new Vector<StructuralPattern>(patManipulator.getNotPatterns());
		for(StructuralPattern notPattern : notPatternsList){
			for(NodeVariable elem : notPattern.getNodeVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<NonStandardPropDescr> nonStdPropDescList = elem.getNonStandardPropDescrs(patManipulator.getOntology());
				for(NonStandardPropDescr prop:nonStdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
							&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
			for(LinkVariable elem : notPattern.getLinkVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<NonStandardPropDescr> nonStdPropDescList = elem.getNonStandardPropDescrs(patManipulator.getOntology());
				for(NonStandardPropDescr prop:nonStdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
		}
		return map;
	}
	
	/*
	 * Used for variable comparisons
	 * Get all the available StdProps with the given compDataType and compGroup from created boxes and links
	 * Returns a map like(b1.prop, b1.prop), (b1.prop, b2.prop), ...
	 * @return Map<String, String>
	 */
	private Map<String, String> getBoxesAndLinksStdProps(ComparisonDataType compDataType, ComparisonGroup compGroup){
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<NodeVariable> nodeVariableList =  patManipulator.getNodeVariables();
		List<LinkVariable> linkVariableList =  patManipulator.getLinkVariables();
		
		for(NodeVariable elem : nodeVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
			for(StandardPropDescr prop:stdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		for(LinkVariable elem : linkVariableList){
			String elemId = new String(elem.getVarID());
//			if(elementVar.getVarID().equals(elemId))
//				continue;
			List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
			for(StandardPropDescr prop:stdPropDescList){
				if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
					&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
					String tmp = new String(elemId + "." + prop.getPropID());
					String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
					map.put(tmp, tmpLabel);
				}
			}
		}
		//Look into NotNode and NotLink
		List<StructuralPattern> notPatternsList = new Vector<StructuralPattern>(patManipulator.getNotPatterns());
		for(StructuralPattern notPattern : notPatternsList){
			for(NodeVariable elem : notPattern.getNodeVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
				for(StandardPropDescr prop:stdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
							&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
			for(LinkVariable elem : notPattern.getLinkVars()){
				String elemId = new String(elem.getVarID());
//				if(elementVar.getVarID().equals(elemId))
//					continue;
				List<StandardPropDescr> stdPropDescList = elem.getStandardPropDescrs();
				for(StandardPropDescr prop:stdPropDescList){
					if(prop.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) == compGroup
						&& prop.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID) == compDataType){
						String tmp = new String(elemId + "." + prop.getPropID());
						String tmpLabel = (elem.getName() != null)? (elem.getName() + "." + prop.getPropID()) : (elemId + "." + prop.getPropID());
						map.put(tmp, tmpLabel);
					}
				}
			}
		}
		return map;
	}
	

	/*
	 * sets operator and the right hand side of the comparison
	 */
	private boolean setOperatorAndRightHandSide(String operatorId){
		boolean retVal = false;		
		//String propId = property.getPropID();
		
		//ComparisonDataType cdt = property.getComparisonDataType(PropDescr.DEFAULT_COMPONENT_ID);
		ComparisonGroup cg = property.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID);
		
		if(comparison instanceof String2ConstStringComparison){
			((String2ConstStringComparison)comparison).setOperator(String2StringOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case USER:{
					String value = strValueField.getValue();
					if(value==null){
						value = new String();
					}
					((String2ConstStringComparison)comparison).setRightExpr(value);
					retVal = true;
					break;
				}
				case TYPE:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						((String2ConstStringComparison)comparison).setRightExpr(value);
						retVal = true;
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof String2VarStringComparison){
			((String2VarStringComparison)comparison).setOperator(String2StringOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case TYPE:
				case USER:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						String[] values = value.split("\\.");
						ElementVariable elemVarReferenced = ElementVariableUtil.getElementVariable(patManipulator, values[0]);
						ElementVariableProp rightExpr = elemVarReferenced.getPropVar(values[1], PropDescr.DEFAULT_COMPONENT_ID);
						((String2VarStringComparison)comparison).setRightExpr(rightExpr);
						retVal = true;
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof String2ConstSetComparison){
			((String2ConstSetComparison)comparison).setOperator(String2SetOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case USER:{
					List<ElementModel> selValues = inputFieldAndListField.getToList().getStore().getModels();
					List<String> values = new Vector<String>();
					for(ElementModel val:selValues){
						values.add((String)val.get(GridElementLabel.NAME));
					}
					((String2ConstSetComparison)comparison).setRightExpr(values);
					retVal = true;
					break;
				}
				case TYPE:{
					List<ElementModel> selValues = dualList.getToList().getStore().getModels();
					List<String> values = new Vector<String>();
					for(ElementModel val:selValues){
						values.add((String)val.get(GridElementLabel.NAME));
					}
					((String2ConstSetComparison)comparison).setRightExpr(values);
					retVal = true;
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof String2VarSetComparison){
			((String2VarSetComparison)comparison).setOperator(String2SetOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case TYPE:
				case USER:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						String[] values = value.split("\\.");
						ElementVariable elemVarReferenced = ElementVariableUtil.getElementVariable(patManipulator, values[0]);
						ElementVariableProp rightExpr = elemVarReferenced.getPropVar(values[1], PropDescr.DEFAULT_COMPONENT_ID);
						((String2VarSetComparison)comparison).setRightExpr(rightExpr);
						retVal = true;
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Num2ConstNumComparison){
			((Num2ConstNumComparison)comparison).setOperator(Num2NumOperator.fromString(operatorId));
			switch(cg){
				case NUMBER:
				case TS:{
					Number value = numValueField.getValue();
					if(value!=null){
						((Num2ConstNumComparison)comparison).setRightExpr(value.intValue());
						retVal = true;
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Num2VarNumComparison){
			((Num2VarNumComparison)comparison).setOperator(Num2NumOperator.fromString(operatorId));
			switch(cg){
				case NUMBER:
				case TS:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					ElementModel secondOper = plusMinusCombo.getValue();
					Number secondValue = extraNumValueField.getValue();
					if(selValue != null && secondOper != null && secondValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						String[] values = value.split("\\.");
						ElementVariable elemVarReferenced = ElementVariableUtil.getElementVariable(patManipulator, values[0]);
						ElementVariableProp rightExpr = elemVarReferenced.getPropVar(values[1], PropDescr.DEFAULT_COMPONENT_ID);
						((Num2VarNumComparison)comparison).setRightExpr(rightExpr);
						int offSet = 0;
						if(ComparisonUtil.PLUS_SIGN.equals(secondOper.getValue(GridElementLabel.ID))){
							offSet = secondValue.intValue();
						} else if(ComparisonUtil.MINUS_SIGN.equals(secondOper.getValue(GridElementLabel.ID))){
							offSet = secondValue.intValue() * -1;
						} else {
							FATDebug.print(FATDebug.ERROR, "[AddEditConstraintWindow][setComparisonRightHandSide] invalid 2nd operator:" + secondOper.getValue(GridElementLabel.ID));
							Info.display(FeedbackAuthoringStrings.ERROR_LABEL, "Invalid value for " + plusMinusCombo.getFieldLabel());  
						}
						((Num2VarNumComparison)comparison).setOffset(offSet);
						retVal = true;
					} else{
						Info.display(FeedbackAuthoringStrings.ERROR_LABEL, "Invalid values. Please verify "
								+ valueCombo.getFieldLabel() + ", "
								+ plusMinusCombo.getFieldLabel() + ", "
								+ extraNumValueField.getFieldLabel());
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Set2ConstStringComparison){
			((Set2ConstStringComparison)comparison).setOperator(Set2StringOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case USER:{
					String value = strValueField.getValue();
					if(value==null){
						value = new String();
					}
					((Set2ConstStringComparison)comparison).setRightExpr(value);
					retVal = true;
					break;
				}
				case TYPE:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						((Set2ConstStringComparison)comparison).setRightExpr(value);
						retVal = true;
					}
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Set2VarStringComparison){
			((Set2VarStringComparison)comparison).setOperator(Set2StringOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case TYPE:
				case USER:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						String[] values = value.split("\\.");
						ElementVariable elemVarReferenced = ElementVariableUtil.getElementVariable(patManipulator, values[0]);
						ElementVariableProp rightExpr = elemVarReferenced.getPropVar(values[1], PropDescr.DEFAULT_COMPONENT_ID);
						((Set2VarStringComparison)comparison).setRightExpr(rightExpr);
						retVal = true;
					}
					
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Set2ConstSetComparison){
			((Set2ConstSetComparison)comparison).setOperator(Set2SetOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case USER:{
					List<ElementModel> selValues = inputFieldAndListField.getToList().getStore().getModels();
					List<String> values = new Vector<String>();
					for(ElementModel val:selValues){
						values.add((String)val.get(GridElementLabel.NAME));
					}
					((Set2ConstSetComparison)comparison).setRightExpr(values);
					retVal = true;
					break;
				}
				case TYPE:{
					List<ElementModel> selValues = dualList.getToList().getStore().getModels();
					List<String> values = new Vector<String>();
					for(ElementModel val:selValues){
						values.add((String)val.get(GridElementLabel.NAME));
					}
					((Set2ConstSetComparison)comparison).setRightExpr(values);
					retVal = true;
					break;
				}
				default:
					break;
			}
		} else if(comparison instanceof Set2VarSetComparison){
			((Set2VarSetComparison)comparison).setOperator(Set2SetOperator.fromString(operatorId));
			switch(cg){
				case TEXT:
				case TYPE:
				case USER:{
					ElementModel selValue = (ElementModel) valueCombo.getValue();
					if(selValue != null){
						String value = selValue.getValue(GridElementLabel.ID);
						String[] values = value.split("\\.");
						ElementVariable elemVarReferenced = ElementVariableUtil.getElementVariable(patManipulator, values[0]);
						ElementVariableProp rightExpr = elemVarReferenced.getPropVar(values[1], PropDescr.DEFAULT_COMPONENT_ID);
						((String2VarSetComparison)comparison).setRightExpr(rightExpr);
						retVal = true;
					}
					break;
				}
				default:
					FATDebug.print(FATDebug.ERROR, "[AddEditConstraintWindow][setComparisonRightHandSide2] Invalid ComparisonGroup:" + cg
													+ " for comparison:" + comparison);
					break;
			}
		} else{
			FATDebug.print(FATDebug.ERROR, "[AddEditConstraintWindow][setComparisonRightHandSide2] comparison:" + comparison);
		}
		return retVal;
	}
	
	private void saveInfo(){
		parentRef.addNewConstraint(comparison);
	}
	
	/***********************************************************************************/
	
	private void populateValueCombo(Map<String, String> map){
		ListStore<ElementModel> comboStore= valueCombo.getStore();
		comboStore.removeAll();
		comboStore.add(Data2ModelConverter.getMapAsModelList(map));
		if(comboStore.getCount() == 0){
			nothing2Compare.setVisible(true);
		}
	}
	/*
	 * @param available is equal to all options
	 */
	private void populateDualList(Map<String, String> allAvailable, Map<String, String> selected){
		Map<String, String> notSelectedDualListOptions = new LinkedHashMap<String, String>();
		
		for(String key:allAvailable.keySet()){
        	if(!selected.containsKey(key)){
        		notSelectedDualListOptions.put(key, allAvailable.get(key));
        	}
        }

		ListField<ElementModel> leftList = dualList.getFromList();
		leftList.setDisplayField(GridElementLabel.NAME);
		leftList.setWidth(165);
		ListStore<ElementModel> leftStore = leftList.getStore();
		leftStore.add(Data2ModelConverter.getMapAsModelList(notSelectedDualListOptions));

		ListField<ElementModel> rightList = dualList.getToList();
		rightList.setDisplayField(GridElementLabel.NAME);
		rightList.setWidth(165);
		ListStore<ElementModel> rightStore =rightList.getStore();
		rightStore.add(Data2ModelConverter.getMapAsModelList(selected));
	}

}
