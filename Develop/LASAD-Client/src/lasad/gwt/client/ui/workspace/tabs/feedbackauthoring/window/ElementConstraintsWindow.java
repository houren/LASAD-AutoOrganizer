package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.ui.LASADBoxComponent;
import lasad.gwt.client.ui.box.pattern.BoxPattern;
import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.link.pattern.LinkPanelPattern;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.TreeFolder;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.TreeFolderChild;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.tree.CustomizedTreeGridView;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ComparisonUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.OntologyXML2ObjReader;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Operator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.PropConstr;
import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.ComparisonDataType;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;

/**
 * Box or link element constraints window
 * @author Anahuac
 *
 */
public class ElementConstraintsWindow implements AbstractConfigWindow {
	
	private final Window window = new Window();
	private TextField<String> elemIdField;
	private TextField<String> elemNameField;
	private TextField<String> linkDirectionField;
	private static TreeStore<ModelData> elementTreeStore = new TreeStore<ModelData>();
	private TreeGrid<ModelData> elementTree;
	
	private ElementVariable elementVar;
	private Ontology ontology;
	private StructuralAnalysisTypeManipulator patManipulator;
	
	private String componentId;
	private String componentType;
	private LASADBoxComponent uiElement;
	
	private Map<String, Comparison> comparisonTracker = new HashMap<String, Comparison>();
	private String origDirection;
	
	/*
	 * @param structureAnalysisType
	 * @param componentId pattern component that will be constrained
	 * @param componentType whether component is "box" or "link"  
	 */
	public ElementConstraintsWindow(String agentId, String patternId, String componentId, String componentType){
		super();
		//this.structureAnalysisType = structureAnalysisType;
		ServiceID serviceId = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
		this.patManipulator = FeedbackAuthoringTabContent.getInstance().getPattternManipulator(serviceId);
		this.componentId = componentId;
		this.componentType = componentType;
		init();
	}
	public ElementConstraintsWindow(GraphMap map, LASADBoxComponent uiElement){
		super();
		this.uiElement= uiElement; 
		getVars(map, uiElement);
		init();
	}
	
	private void getVars(GraphMap map, LASADBoxComponent uiElement){
		
		if(uiElement instanceof BoxPattern){
			((BoxPattern)uiElement).setMyControllerFromLASADClient();
			PatternController controller = ((BoxPattern)uiElement).getMyController();
			this.componentType = ComparisonUtil.BOX;
			AbstractUnspecifiedElementModel elemModel = ((BoxPattern)uiElement).getConnectedModel();
			this.componentId = elemModel.getValue(ParameterTypes.Id);
			String patternId = elemModel.getValue(ParameterTypes.PatternId);
			String agentId = controller.getMapInfo().getAgentId();
			ServiceID serviceId = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
			this.patManipulator = FeedbackAuthoringTabContent.getInstance().getPattternManipulator(serviceId);
		} else { //LinkPanelPattern
			((LinkPanelPattern)uiElement).setController(map);
			PatternController controller = (PatternController) ((LinkPanelPattern)uiElement).getController();
			this.componentType = ComparisonUtil.LINK;
			AbstractUnspecifiedElementModel elemModel = ((LinkPanelPattern)uiElement).getConnectedModel();
			this.componentId = elemModel.getValue(ParameterTypes.Id);
			String patternId = elemModel.getValue(ParameterTypes.PatternId);
			String agentId = controller.getMapInfo().getAgentId();
			ServiceID serviceId = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
			this.patManipulator = FeedbackAuthoringTabContent.getInstance().getPattternManipulator(serviceId);
		}
		
	}
	
	private void init(){
		initElementVar();
		initSelectAgentWindow();
		populateTreeGrid();
	}
	
	@Override
	public void show(){
		window.show();
	}
	
	/*
	 * This method gets the StructuralAnalysisTypeManipulator, the component and the ontology
	 */
	private void initElementVar(){
		elementVar = ElementVariableUtil.getElementVariable(patManipulator, componentId);

//		if(ComparisonUtil.BOX.equals(componentType)){
//			int index = patManipulator.getNodeVariables().indexOf(new NodeVariable(componentId));
//			elementVar = patManipulator.getNodeVariables().get(index);
//		} else if(ComparisonUtil.LINK.equals(componentType)){
//			int index = patManipulator.getLinkVariables().indexOf(new LinkVariable(componentId));
//			elementVar = patManipulator.getLinkVariables().get(index);
//		}
		if(elementVar == null){
			FATDebug.print(FATDebug.ERROR, "ERROR [ComponentConstraintsWindow][initElementVar] componentId:" + componentId + " does not exit");
			return;
		}
		
		if(FeedbackAuthoringTabContent.getInstance().getOntologyDB(patManipulator.getOntology().getOntologyID()).getOntology() == null){
	    	Ontology ontology = new Ontology(patManipulator.getOntology().getOntologyID()); //(structureAnalysisType.getOntologyID());
	    	String xml = FeedbackAuthoringTabContent.getInstance().getOntologyDB(patManipulator.getOntology().getOntologyID()).getXml();
			if(xml != null){
				OntologyXML2ObjReader.parseOntology(ontology, FeedbackAuthoringTabContent.getInstance().getOntologyDB(patManipulator.getOntology().getOntologyID()).getXml());
			}
	    	FeedbackAuthoringTabContent.getInstance().getOntologyDB(patManipulator.getOntology().getOntologyID()).setOntology(ontology);
	    }
	    ontology = FeedbackAuthoringTabContent.getInstance().getOntologyDB(patManipulator.getOntology().getOntologyID()).getOntology();
	}

	private void initSelectAgentWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("600px", "500px");
//		window.setResizable(false);
		String comType = null;
		if (ComparisonUtil.BOX.equals(componentType)){
			comType = new String(FeedbackAuthoringStrings.BOX_LABEL); 
		} else{
			comType = new String(FeedbackAuthoringStrings.LINK_LABEL);
		}
		window.setHeading(FeedbackAuthoringStrings.PATTERN_COMPONENT_LABEL + " (" + comType + ")");
		
		window.setLayout(new FitLayout());
		window.add(createPanel());
		window.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//TODO check if we need to do something else before closing
			}
		});
		
		Button okBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				//TODO save config
				//update box/link's text element with info about type constraint
				updateTextField();
				if(!ComparisonUtil.isNodeVariable(elementVar)
						 && ((LinkVariable)elementVar).getDirectionMatters()){
					changeDirectionIfModified();
				}
				window.hide();
		    }
		});
		window.addButton(okBtn);
		
		Button cancelBtn = new Button(FeedbackAuthoringStrings.CANCEL_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				window.hide();  
			}  
		});
		window.addButton(cancelBtn);
//		window.layout();
	}
	
	
	private ContentPanel createPanel(){
		ContentPanel panel = new ContentPanel();
//		panel.setHeading(FeedbackAuthoringStrings.HEADING_PATTERN_COMPONENT + " (" + "Box/Link" + ")");
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setScrollMode(Scroll.AUTOY);
		panel.setWidth(550);
		panel.setHeight(500);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		
		FormPanel formPanel = new FormPanel();
		formPanel.setLabelWidth(90);
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(550);
		formPanel.setStyleAttribute("color", "#000000");
		
		elemIdField = new TextField<String>();
		elemIdField.setFieldLabel(FeedbackAuthoringStrings.ELEMENT_ID_LABEL);
		elemIdField.setEnabled(false);
		elemIdField.setValue(elementVar.getVarID());
		formPanel.add(elemIdField);
		
		elemNameField = new TextField<String>();
		elemNameField.setFieldLabel(FeedbackAuthoringStrings.ELEMENT_NAME_LABEL);
		elemNameField.setEnabled(false);
		elemNameField.setValue((elementVar.getName() != null)? elementVar.getName() : elementVar.getVarID());
		formPanel.add(elemNameField);
		panel.add(formPanel, new RowData(1.0, Style.DEFAULT, new Margins(3)));
	    
		//Directionality
	    linkDirectionField = new TextField<String>();
	    linkDirectionField.setFieldLabel(FeedbackAuthoringStrings.DIRECTIONALITY_LABEL);
	    linkDirectionField.setEnabled(false);
	    
	    Button directionalityButton = new Button(){
	    	@Override
	    	protected void onClick(ComponentEvent ce) {
	    		//MessageBox.info(FeedbackAuthoringStrings.DIRECTIONALITY_LABEL, FeedbackAuthoringStrings.DIRECTIONALITY_LABEL, null);
	    		handleActionSwitchDirection();
	    	}
	    };
	    
	    if(!ComparisonUtil.isNodeVariable(elementVar)){
	    	ElementVariable sourceVar = ((LinkVariable)elementVar).getSource();
	    	ElementVariable targetVar = ((LinkVariable)elementVar).getTarget();
	    	StringBuffer direction = new StringBuffer();
	    	direction.append(((sourceVar.getName() != null)? sourceVar.getName() : sourceVar.getVarID()));
	    	if(!((LinkVariable)elementVar).getDirectionMatters()){
	    		//Not Directed, disable change direction button
	    		directionalityButton.setEnabled(false);
	    		direction.append(" <-> ");
	    	} else{
	    		direction.append(" -> ");
	    	}
	    	direction.append(((targetVar.getName() != null)? targetVar.getName() : targetVar.getVarID()));
	    	origDirection = new String(direction.toString());
	    	linkDirectionField.setValue(direction.toString());
	    }
	    
	    directionalityButton.setToolTip(FeedbackAuthoringStrings.SWITCH_DIRECTION_LABEL);
	    directionalityButton.setIconStyle("icon-switch-direction");
	    directionalityButton.setStyleName("xfa-btn");
	    directionalityButton.setStyleName("x-btn-text", true);
	    directionalityButton.setStyleName("x-btn", true);
	    directionalityButton.setStylePrimaryName("xfa-btn");
	    
	    FormPanel directionalityPanel = new FormPanel();
	    directionalityPanel.setLabelWidth(0);
	    directionalityPanel.setLabelSeparator("");
	    directionalityPanel.setHeading(FeedbackAuthoringStrings.DIRECTIONALITY_LABEL);
	    directionalityPanel.setBodyBorder(true);
	    directionalityPanel.setWidth(550);
	    directionalityPanel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 1px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td1.setStyle("style='padding-left: 16px;'");
		TableData td2 = new TableData();
		td2.setHorizontalAlign(HorizontalAlignment.LEFT);
		td2.setStyle("style='padding-left: 120px;'");
		TableData td3 = new TableData();
		td3.setHorizontalAlign(HorizontalAlignment.LEFT);
		td3.setStyle("style='padding-left: 107px;'");
		
	    HorizontalPanel hp = new HorizontalPanel();
		hp.setAutoWidth(true);
		hp.add(linkDirectionField, td);
		hp.add(directionalityButton, td1);
		directionalityPanel.add(hp);
	    
		if(!ComparisonUtil.isNodeVariable(elementVar)){
	    	panel.add(directionalityPanel, new RowData(1.0, Style.DEFAULT, new Margins(3)));
	    }
	    
	    ///////////////////////////
	    ///////TreeGrid///////////
	    //////////////////////////
		ContentPanel propertiesPanel = new ContentPanel();
		propertiesPanel.setHeading(FeedbackAuthoringStrings.PROPERTIES_LABEL);
		propertiesPanel.setBodyBorder(true);
		propertiesPanel.setWidth(550);
		propertiesPanel.setHeight(400);
		propertiesPanel.setStyleAttribute("color", "#000000");
		propertiesPanel.setLayout(new FitLayout());
		
		Button buttonAdd = new Button(FeedbackAuthoringStrings.ADD_UPC_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL){
			@Override
			protected void onClick(ComponentEvent ce) {
				handleActionAddParentElement();
				
			}
		};
		
		buttonAdd.setIconStyle("icon-plus-circle");//## icon-round-add
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
		toolBar.add(buttonAdd);
		propertiesPanel.setTopComponent(toolBar);
		
		Map<String, String> mapOne = new HashMap<String, String>();
		mapOne.put(GridElementLabel.NAME, "root");
		TreeFolder model= new TreeFolder(mapOne);
		
		elementTreeStore = new TreeStore<ModelData>();
		elementTreeStore.add(model.getChildren(), true);

		final ColumnConfig nameCol = new ColumnConfig(GridElementLabel.NAME, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.PROPERTY_LABEL + "</div>", 80);
		final ColumnConfig operCol = new ColumnConfig(GridElementLabel.OPER, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.OPERATOR_LABEL + "</div>", 60);
		final ColumnConfig statusCol = new ColumnConfig(GridElementLabel.STATUS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.VALUE_LABEL + "</div>", 110);
		ColumnConfig actionsCol = new ColumnConfig(GridElementLabel.ACTIONS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ACTIONS_LABEL + "</div>", 80);
		ColumnModel treeCM = new ColumnModel(Arrays.asList(nameCol, operCol, statusCol, actionsCol));
		
		elementTree = new TreeGrid<ModelData>(elementTreeStore, treeCM);
		elementTree.setBorders(true);
		elementTree.setAutoExpand(true);
		elementTree.setAutoExpandColumn(GridElementLabel.STATUS);
		elementTree.setAutoExpandMax(500);
		elementTree.setTrackMouseOver(true);
		elementTree.getStyle().setNodeCloseIcon(null);
		elementTree.getStyle().setNodeOpenIcon(null);
		elementTree.setStripeRows(true);
//		elementTree.getStyle().setLeafIcon(IconHelper.create("icon-restriction"));
		
		
		CustomizedTreeGridView treeGridView = new CustomizedTreeGridView();
		elementTree.setView(treeGridView);
		
		TreeGridSelectionModel<ModelData> selectionModel = new TreeGridSelectionModel<ModelData>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setLocked(true);
		elementTree.setSelectionModel(selectionModel);
		
		elementTree.addStyleName("text-black");
		operCol.setStyle("color:black; text-align:center;");
		statusCol.setStyle("color:black;");
		
		nameCol.setRenderer(new TreeGridCellRenderer<ModelData>());
		statusCol.setRenderer(new GridCellRenderer<ModelData>() {
			
			@Override
			public String render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
                if (model instanceof TreeFolderChild){
                	String status = model.get(GridElementLabel.STATUS);
                	return status;
                } else{ //(model instanceof TreeFolder)
                	return new String("");
                }

            }
		});
			
		actionsCol.setRenderer(new GridCellRenderer<ModelData>() {
			
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
                if (model instanceof TreeFolderChild){
                	return getDeleteActionButtonPanel(model, property, config, rowIndex, colIndex, store, grid);
                } else{ //(model instanceof TreeFolder)
//                	String status = (String) model.get(statusCol.getId());
//                	String name = (String) model.get(GridElementLabel.ID); //nameCol.getId()
                	return getAddDeleteActionButtonPanel(model, property, config, rowIndex, colIndex, store, grid);
                }

            }
			
			/*
			 * Displayed for the parents in the Tree Grid
			 * Shows:
			 * button to add constraint and 
			 * button to delete the property. Only if it is not a default property(TODO).
			 */
			private LayoutContainer getAddDeleteActionButtonPanel(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    final ListStore<ModelData> store, Grid<ModelData> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));//RowLayout
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
//				panel.setHeight(20);
				
				Button addButton = new Button();
				addButton.setStyleName("xfa-btn");
				addButton.setStyleName("x-btn-text", true);
				addButton.setStyleName("x-btn", true);
				addButton.setStylePrimaryName("xfa-btn");
				addButton.setIconStyle("icon-plus-circle");//icon-add-restriction
				addButton.setWidth(16);
				addButton.setHeight(16);
				addButton.setToolTip(FeedbackAuthoringStrings.ADD_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL);
				
				addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String id = model.get(GridElementLabel.ID);
				    	  String name = model.get(GridElementLabel.NAME);
				    	  String parentDesc = model.get(GridElementLabel.STATUS);

				    	  StringBuffer s = new StringBuffer();
				    	  s.append("-id:" + id + ("\n"));
				    	  s.append("-name:" + name + ("\n"));
				    	  //Info.display("INFO", s.toString());
				    	  handleActionAddChildToParentElement(id, name, parentDesc);
				      }
				});
				
				Button deleteButton = new Button();
				deleteButton.setStyleName("xfa-btn");
				deleteButton.setStyleName("x-btn-text", true);
				deleteButton.setStyleName("x-btn", true);
				deleteButton.setStylePrimaryName("xfa-btn");
				deleteButton.setIconStyle("icon-delete-circle");
				deleteButton.setWidth(16);
				deleteButton.setHeight(16);
				deleteButton.setToolTip(FeedbackAuthoringStrings.REMOVE_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL);
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  
				    	  final String id = model.get(GridElementLabel.ID); //property id
				    	  String name = model.get(GridElementLabel.NAME); //property name
				    	  String status = model.get(GridElementLabel.STATUS); //property value, eg. "(type, text, 1 ... 1)"
				    	  
				    	  StringBuffer s = new StringBuffer();
				    	  s.append("-id:" + id + "\n");
				    	  s.append("-name:" + name + "\n");
				    	  s.append("-status:" + status + "\n");
				    	  //Info.display("INFO", s.toString());
				    	  
				    	  ce.setCancelled(true);
				    	  
				    	  MessageBox box = new MessageBox();
				    	  box.setButtons(MessageBox.YESNO);
				    	  box.setIcon(MessageBox.QUESTION);
				    	  box.setTitle(FeedbackAuthoringStrings.REMOVE_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL);
				    	  StringBuffer msg = new StringBuffer();
				    	  int num = getParentChildrenNumber(id);
				    	  if(num > 0){
				    		  //parent has children, warn the user that they will be deleted as well.
				    		  msg.append("\"" + name + "\""  + " " + FeedbackAuthoringStrings.HAS_LABEL);
				    		  msg.append(" " + num + " ");
				    		  msg.append(FeedbackAuthoringStrings.CONSTRAINTS_LABEL + "\n");
				    		  msg.append(FeedbackAuthoringStrings.REMOVE_LABEL + " \"" + name  
				    			  		+ "\" " + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  } else{
				    		  msg.append(FeedbackAuthoringStrings.REMOVE_LABEL + " \"" + name  
				    			  		+ "\" " + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  }
				    	  box.setMessage(msg.toString());
				    	  box.addCallback(new Listener<MessageBoxEvent>() {
				    		  public void handleEvent(MessageBoxEvent be) {
				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
				    				  deleteParent(id);
				    				  handleActionDeleteParent(id);
				    			  }
				    		  }
				    	  });
				    	  box.show();
				      }
				});
				String propId = model.get(GridElementLabel.ID); //property id
				if(!isStandardPropDescr(propId)){
					panel.add(deleteButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}
				panel.add(addButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				return panel;
			}
			
			/*
			 * Displayed for the children in the Tree Grid
			 * Shows button to delete the constraint.
			 */
			private ContentPanel getDeleteActionButtonPanel(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				Button deleteButton = new Button();
				
				deleteButton.setStyleName("xfa-btn");
				deleteButton.setStyleName("x-btn-text", true);
				deleteButton.setStyleName("x-btn", true);
				deleteButton.setStylePrimaryName("xfa-btn");
				deleteButton.setIconStyle("icon-delete-circle");//icon-delete-restriction
				deleteButton.setWidth(16);
				deleteButton.setHeight(16);
				deleteButton.setToolTip(FeedbackAuthoringStrings.REMOVE_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL);
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  final String id = model.get(GridElementLabel.ID); //constraint id
				    	  String name = model.get(GridElementLabel.OPER); //constraint operator
				    	  String status = model.get(GridElementLabel.STATUS); //constraint value
				    	  final String parentId = model.get(GridElementLabel.PARENT_ID); //property id
				    	  String parentName = model.get(GridElementLabel.PARENT_NAME); //property name

				    	  ce.setCancelled(true);
				    	  
				    	  MessageBox box = new MessageBox();
				    	  box.setButtons(MessageBox.YESNO);
				    	  box.setIcon(MessageBox.QUESTION);
				    	  box.setTitle(FeedbackAuthoringStrings.REMOVE_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL);
				    	  box.setMessage(FeedbackAuthoringStrings.REMOVE_LABEL
				    			  		+ " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL
				    			  		+ " (\"" + name + "\", "
				    			  		+ "\"" + status + "\")" 
				    			  		+ " " + FeedbackAuthoringStrings.FROM_LABEL + " " 
				    			  		+ "\"" + parentName+ "\"" + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  box.addCallback(new Listener<MessageBoxEvent>() {
				    		  public void handleEvent(MessageBoxEvent be) {
				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
				    				  handleActionDeleteChildFromParent(parentId, id);
				    			  }
				    		  }
				    	  });
				    	  box.show();
				      }
				});
				
				Button editButton = new Button();
				
				editButton.setStyleName("xfa-btn");
				editButton.setStyleName("x-btn-text", true);
				editButton.setStyleName("x-btn", true);
				editButton.setStylePrimaryName("xfa-btn");
				editButton.setIconStyle("icon-edit-gear");//
				editButton.setWidth(16);
				editButton.setHeight(16);
				editButton.setToolTip(FeedbackAuthoringStrings.EDIT_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL);
				editButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  final String id = model.get(GridElementLabel.ID); //constraint id
				    	  //String name = model.get(GridElementLabel.OPER); //constraint operator
				    	  String status = model.get(GridElementLabel.STATUS); //constraint value
				    	  final String parentId = model.get(GridElementLabel.PARENT_ID); //property id
				    	  String parentName = model.get(GridElementLabel.PARENT_NAME); //property name
				    	  ce.setCancelled(true);
				    	  
				    	  handleActionEditChild(parentId, parentName, id, status);
				      }
				});
				
				panel.add(editButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				panel.add(deleteButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				return panel;
			}
		});
		propertiesPanel.add(elementTree);
		panel.add(propertiesPanel, new RowData(1.0, Style.DEFAULT, new Margins(3)));
		return panel;
	}
	
	/*
	 * changes the direction of the link
	 */
	private void handleActionSwitchDirection(){
		String newDirection = linkDirectionField.getValue();
		if(origDirection.equals(newDirection)){
			LinkVariable link = (LinkVariable)elementVar;
			linkDirectionField.setValue(((link.getTarget().getName() != null)? link.getTarget().getName() : link.getTarget().getVarID()) 
	    			+ " -> " 
	    			+ ((link.getSource().getName() != null)? link.getSource().getName() : link.getSource().getVarID()));
		} else{
			linkDirectionField.setValue(new String(origDirection));
		}
		
//		ElementVariable sourceVar = link.getSource();
//    	link.setSource(link.getTarget());
//    	link.setTarget(sourceVar);
//    	linkDirectionField.setValue(((link.getSource().getName() != null)? link.getSource().getName() : link.getSource().getVarID()) 
//    			+ " -> " 
//    			+ ((link.getTarget().getName() != null)? link.getTarget().getName() : link.getTarget().getVarID()));
		
	}
	
	/*
	 * prompts a window dialog to add a new parent element (property) to the TreeGrid
	 */
	private void handleActionAddParentElement(){
		Map<String,String> optionsMap = new LinkedHashMap<String, String>();
		//Add StandardPropDescr that are not in grid yet
		List<StandardPropDescr> stdProps = elementVar.getStandardPropDescrs();
		for(StandardPropDescr prop:stdProps){
			if(prop instanceof TypePropDescr){
				continue;
			} else if(!isParentInTreeGrid(prop.getPropID())){
				optionsMap.put(prop.getPropID(), prop.getPropID());
			}
		}
		//
		List<NonStandardPropDescr> nonStdProps = elementVar.getNonStandardPropDescrs(ontology);
		for(NonStandardPropDescr prop:nonStdProps){
			if(!isParentInTreeGrid(prop.getPropID())){
				optionsMap.put(prop.getPropID(), prop.getPropID());
			}
		}
//		List<NonStandardPropDescr> notInUseList = getNotInUseNonStandardPropDescr();
//		for(NonStandardPropDescr prop:notInUseList){
//			optionsMap.put(prop.getPropID(), prop.getPropID());
//		}
		
		AddPropertyWindow addPropertyWindow = new AddPropertyWindow(optionsMap, this);
		addPropertyWindow.show();
	}
	/*
	 * prompts a window dialog to add a new child (constraint) to a parent (property)
	 */
	private void handleActionAddChildToParentElement(String parentId, String parentName, String parentDesc){
		AddEditConstraintWindow window = new AddEditConstraintWindow(elementVar, parentId, parentName, null, parentDesc, this);
		window.show();
	}
	/*
	 * deletes a parent (property) and its children
	 * this is only valid for non-standard properties
	 */
	private void handleActionDeleteParent(String parentId){
		deleteParent(parentId);
	}
	
	/*
	 * deletes a child (constraint) from a parent (property)
	 * if the child is the last child, the parent is still displayed
	 * in the TreeGrid
	 */
	private void handleActionDeleteChildFromParent(String parentId, String childId){
		deleteChildFromParent(parentId, childId);
	}
	
	/*
	 * prompts a window dialog to edit a constraint
	 */
	private void handleActionEditChild(String parentId, String parentName, String childId, String parentDesc){
		Info.display(FeedbackAuthoringStrings.WARNING, FeedbackAuthoringStrings.FUNCT_UNDER_DEV);
//		AddEditConstraintWindow window = new AddEditConstraintWindow(elementVar, parentId, parentName, childId, parentDesc, this);
//		window.show();
	}
	
	protected Comparison getComparison(String id){
		return comparisonTracker.get(id);
	}
	
	/*
	 * Deletes child from parent
	 */
	private void deleteChildFromParent(String parentId, String childId){
		Comparison comparison = comparisonTracker.remove(childId);
		deleteChildFromParentUI(parentId, childId);
		patManipulator.removeComparisonIfExists(comparison);
	}
	/*
	 * Adds a new parent element to the grid if it's not there yet
	 * Note: its not necessary to add it to the data structure
	 */
	public void addParent(String parentId, String parentName){
		if(!isParentInTreeGrid(parentId)){
			String parentStatus = getParentStatus(parentId);
			addParent2TreeGridUI(parentId, parentName, parentStatus);
			//cannot add this to the structure until we have a constraint.
		} else{
			Info.display(FeedbackAuthoringStrings.PROPERTY_ALREADY_ADDED_LABEL, parentId 
							+ " " + FeedbackAuthoringStrings.IS_ALREADY_ADDED_LABEL);
		}
	}
	
	private void addParent(String parentId, String parentName, String parentStatus){
		addParent2TreeGridUI(parentId, parentName, parentStatus);
	}
	/*
	 * Deletes a parent from GUI and from data structure
	 */
	private void deleteParent(String parentId){
		deleteParentFromTreeGridUI(parentId);
		//delete from data structure
		for(PropConstr propConstr : elementVar.getPropConstrs()){
			if(propConstr.getPropVar().getPropId().equals(parentId)){
				List<Comparison> comps = new Vector<Comparison>(propConstr.getComparisons());
				for(Comparison comp :  comps){
					patManipulator.removeComparisonIfExists(comp);
				}
				break;
			}
		}
	}
	
	private String getParentStatus(String parentId){
		NonStandardPropDescr propTmp = getNonStandardPropDescr(parentId);
		if(propTmp != null){
			return createPropInfoStr(propTmp);
		}
		StandardPropDescr stdPropTmp = getStandardPropDescr(parentId);
		if(stdPropTmp != null){
			return createPropInfoStr(stdPropTmp);
		}
		FATDebug.print(FATDebug.ERROR, "ERROR [ElementConstraintsWindow]getParentStatus propId:" + parentId + " not found");
		return "ERROR";
	}
	
	private boolean isParentInTreeGrid(String parentId){
		boolean retVal = false;
		ModelData parent = elementTree.getTreeStore().findModel(GridElementLabel.ID, parentId);
		if(parent != null){
			retVal = true;
		}
		
		return retVal;
	}
	/*
	 * Adds child to parent in TreeGrid
	 */
	private void addChild2ParentUI(String parentId, TreeFolderChild child){
		ModelData parent = elementTree.getTreeStore().findModel(GridElementLabel.ID, parentId);
		elementTree.getTreeStore().add(parent, child, false);
		elementTree.setExpanded(parent, true);
	}
	/*
	 * Adds child to parent in TreeGrid
	 */
	private void addChild2ParentUI(String parentId, String parentName, String childId, String childName, String childStatus){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.NAME, "");
		map.put(GridElementLabel.OPER, ComparisonUtil.fromOperId2Str(childName)); //constraint operator
		map.put(GridElementLabel.STATUS, childStatus); //constraint value
		map.put(GridElementLabel.ID, childId);
		map.put(GridElementLabel.PARENT_ID, parentId); //property id
		map.put(GridElementLabel.PARENT_NAME, parentName); //property name
		TreeFolderChild child = new TreeFolderChild(map);
		
		addChild2ParentUI(parentId, child);
	}
	/*
	 * Deletes child from parent in TreeGrid
	 */
	private void deleteChildFromParentUI(String parentId, String childId){
		ModelData parent = elementTree.getTreeStore().findModel(GridElementLabel.ID, parentId);
		List<ModelData> children = elementTree.getTreeStore().getChildren(parent);
		ModelData child = null;
		for(int i=0; i < children.size(); i++){
			String tmpId = children.get(i).get(GridElementLabel.ID); 
			if(childId.equals(tmpId)){
				child = children.get(i);
				break;
			}
		}
		elementTree.getTreeStore().remove(parent, child);
		refreshView();
	}
	/*
	 * Adds parent to TreeGrid
	 */
	private void addParent2TreeGridUI(TreeFolder parent){
		elementTreeStore.add(parent, true);
		refreshView();
	}
	private void addParent2TreeGridUI(String parentId, String parentName, String parentStatus){
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, parentId);
		map.put(GridElementLabel.NAME, parentName);
		map.put(GridElementLabel.OPER, "");
		map.put(GridElementLabel.STATUS, parentStatus);
		TreeFolder parent = new TreeFolder(map, new TreeFolderChild[]{});
		addParent2TreeGridUI(parent);
	}
	/*
	 * Deletes parent from the TreeGrid and its respective children
	 */
	private void deleteParentFromTreeGridUI(String parentId){
		ModelData parent = elementTree.getTreeStore().findModel(GridElementLabel.ID, parentId);
		elementTree.getTreeStore().getChildren(parent).clear(); //delete children
		elementTree.getTreeStore().remove(parent); //delete parent
	}
	/*
	 * Checks if parentId has children
	 * @returns -1 if parent does not exist
	 * 			 n where n is equal to the number of children
	 */
	private int getParentChildrenNumber(String parentId){
		int retVal = -1;
		ModelData parent = elementTree.getTreeStore().findModel(GridElementLabel.ID, parentId);
		elementTree.getTreeStore().getChildren(parent);
		if(elementTree.getTreeStore().getChildren(parent) != null){
			retVal = elementTree.getTreeStore().getChildren(parent).size();
		}
		return retVal;
	}

	/*
	 * Populates the TreeGrid with the current pattern configuration.
	 */
	void populateTreeGrid(){
		
	    List<StandardPropDescr> stdProps = elementVar.getStandardPropDescrs();
	    
	    //add parent entry only for "type"
	    for(StandardPropDescr stdProp:stdProps){
	    	if(stdProp instanceof TypePropDescr){
	    		addParent2TreeGridUI(stdProp.getPropID(), stdProp.getPropID(), createPropInfoStr(stdProp));
	    		break;
	    	}
	    }
	    
	    //iterate over the comparisons already defined
	    List<Comparison> comparisonList = elementVar.getComparisons();
	    for(Comparison comparison:comparisonList){
	    	ElementVariableProp leftExpr = comparison.getLeftExpr();
	    	String propId = leftExpr.getPropId();
	    	Operator oper = comparison.getOperator();
	    	
	    	//if parent entry is not there already -> add parent entry
    		//otherwise just continue
    		if(!isParentInTreeGrid(propId)){
    			if(isStandardPropDescr(propId)){
    	    		StandardPropDescr propTmp = getStandardPropDescr(propId);
    	    		if(propTmp != null){
    	    			addParent(propId, propId, createPropInfoStr(propTmp));
    	    		} else{
    	    			System.out.println("ERROR [ComponentConstraintsWindow]populateTreeGrid could not add propId:" + propId + " not found");
    	    			continue;
    	    		}
    	    	} else {
	    			NonStandardPropDescr propTmp = getNonStandardPropDescr(propId);
		    		if(propTmp != null){
		    			addParent(propId, propId, createPropInfoStr(propTmp));
		    		} else{
		    			System.out.println("ERROR [ComponentConstraintsWindow]populateTreeGrid could not add propId:" + propId + " not found");
		    			continue;
		    		}
    	    	}
    		}
	    	
	    	String childId = IdGenerator.getNewConstraintId();
	    	comparisonTracker.put(childId, comparison);
	    	addChild2ParentUI(propId, propId, childId, oper.getName(), ElementVariableUtil.generatedReadableStrFromComparison(patManipulator, comparison));
	    }
	}
	
	/*
	 * Adds a new constraint to the grid and to 
	 * the ElementVariable through the StructuralAnalysisTypeManipulator,
	 */
	void addNewConstraint(Comparison comparison){
		ElementVariableProp leftExpr = comparison.getLeftExpr();
    	String propId = leftExpr.getPropId();
    	Operator oper = comparison.getOperator();
    	
    	String childId = IdGenerator.getNewConstraintId();
    	comparisonTracker.put(childId, comparison);
    	addChild2ParentUI(propId, propId, childId, oper.getName(), ElementVariableUtil.generatedReadableStrFromComparison(patManipulator, comparison));

    	patManipulator.addComparisonIfNotExists(comparison);
//    	if(propId.equals(TypePropDescr.PROP_ID)){
//    		updateTextField();
//    	}
	}

	private void refreshView(){
		if(elementTree.isRendered())
			elementTree.getView().refresh(false);
	}
	
	public boolean isStandardPropDescr(String propId){
		boolean retVal = false;
		if (getStandardPropDescr(propId) != null){
			retVal = true;
		}
		return retVal;
	}
	
	public StandardPropDescr getStandardPropDescr(String propId){
		List<StandardPropDescr> stdProps = elementVar.getStandardPropDescrs();
		StandardPropDescr propTmp = null;
		for(StandardPropDescr prop:stdProps){
			if(prop.getPropID().equals(propId)){
				propTmp = prop;
				break;
			}
		}
		return propTmp;
	}
	
	public List<NonStandardPropDescr> getNotInUseNonStandardPropDescr(){
		List<NonStandardPropDescr> nonStdProps = elementVar.getNonStandardPropDescrs(ontology);
		List<NonStandardPropDescr> notInUseList = new Vector<NonStandardPropDescr>(nonStdProps);
		
		//iterate over the comparisons already defined
	    List<Comparison> comparisonList = elementVar.getComparisons();
	    for(Comparison comparison:comparisonList){
	    	ElementVariableProp leftExpr = comparison.getLeftExpr();
	    	String propId = leftExpr.getPropId();
	    	if(isStandardPropDescr(propId)){
	    		continue;
	    	} else {
	    		for(NonStandardPropDescr prop: nonStdProps){
	    			if(propId.equals(prop.getPropID())){
	    				notInUseList.remove(prop);
	    			}
	    		}
	    	}
	    }
	    
	    return notInUseList;
	}
	
	public NonStandardPropDescr getNonStandardPropDescr(String propId){
		List<NonStandardPropDescr> nonStdProps = elementVar.getNonStandardPropDescrs(ontology);
		NonStandardPropDescr propTmp = null;
		for(NonStandardPropDescr prop:nonStdProps){
			if(prop.getPropID().equals(propId)){
				propTmp = prop;
				break;
			}
		}
		return propTmp;
	}
	
	public StructuralAnalysisTypeManipulator getPatManipulator(){
		return patManipulator;
	}
	
//	private String generatedReadableStrFromComparison(Comparison comparison){
//		ElementVariableProp leftExpr = comparison.getLeftExpr();
//		String propId = leftExpr.getPropId();
//		Operator oper = comparison.getOperator();
//		
//		StringBuffer value = new StringBuffer();
//		if (comparison instanceof VariableComparison){
//			String rElemId = ((VariableComparison)comparison).getRightExpr().getElementVar().getVarID();
//			String rPropId = ((VariableComparison)comparison).getRightExpr().getPropId();
//			String rRhsComponentID = ((VariableComparison)comparison).getRightExpr().getCompID();
//			String rCompId = null;
//			if (!PropDescr.DEFAULT_COMPONENT_ID.equals(rRhsComponentID)) {
//				rCompId = rRhsComponentID;
//			}
//			boolean rIsNode = (((VariableComparison)comparison).getRightExpr().getElementVar() instanceof NodeVariable);
//			ElementVariable elemVar = ElementVariableUtil.getElementVariable(patManipulator, rElemId);
//			value.append((elemVar != null)? (elemVar.getName() + "." + rPropId) : (rElemId + "." + rPropId)); 
//			//value.append(rElemId + "." + rPropId);
//			if(comparison instanceof Num2VarNumComparison){
//				int offset = ((Num2VarNumComparison)comparison).getOffset();
//				if(offset > 0){
//					value.append(" " + ComparisonUtil.PLUS_SIGN + " " + offset);
//				} else if(offset < 0){
//					value.append(" " + ComparisonUtil.MINUS_SIGN + " " + Math.abs(offset));
//				} 
//			}
//		} else{
//			List<String> valueElems = ComparisonUtil.getValueElems(comparison);
//			boolean commaflag = false;
//			boolean isSet = (valueElems.size() > 1);
//			if(isSet){
//				value.append("{");
//			}
//			for(String val:valueElems){
//				if(commaflag){
//					value.append(", ");
//				}
//				value.append("\"" + val + "\"");
//				commaflag = true;
//			}
//			if(isSet){
//				value.append("}");
//			}
//		}
//		return value.toString();
//	}
	
	/*
	 * Updates the text field with the TypePropDescr restrictions
	 * 2 Lines max, n characters max per row
	 * oper value1, value2, ...
	 * oper value1, value2, ...
	 * ...
	 */
	private final int MAX_LINES = 2;
	private final int MAX_CHAR_LINE = 30;
	void updateTextField(){
		AbstractUnspecifiedElementModel elemModel;
		if(uiElement instanceof BoxPattern){
			elemModel = ((BoxPattern)uiElement).getConnectedModel();
		} else if(uiElement instanceof LinkPanelPattern){ //instanceof 
			elemModel = ((LinkPanelPattern)uiElement).getConnectedModel();
		} else {
			FATDebug.print(FATDebug.ERROR, "ERROR [ElementConstraintsWindow]updateTextField unknown uiElement type:" + uiElement);
			return;
		}
		Vector<AbstractUnspecifiedElementModel> children = elemModel.getChildren();
		
		List<String> readableValues = new Vector<String>();
		StringBuffer value = new StringBuffer();
		List<Comparison> comparisons = elementVar.getComparisons();
		int count = 0;
		//generate text to be displayed
		for(Comparison comparison : comparisons){
			if(comparison.getLeftExpr().getPropId().equals(TypePropDescr.PROP_ID)){
				String tmp = ElementVariableUtil.generatedReadableStrFromComparison(patManipulator, comparison);
				readableValues.add(tmp);
				if(count < MAX_LINES+1){
					if(count < MAX_LINES){
						value.append(ComparisonUtil.fromOperId2Str(comparison.getOperator().getName()) + " ");
						value.append(tmp.substring(0, (tmp.length() < MAX_CHAR_LINE ? tmp.length() : MAX_CHAR_LINE)));
						if(tmp.length() >= MAX_CHAR_LINE){
							value.append("...\n");
						}else{
							value.append("\n");
						}
					} else{
						value.append("...");
					}
					count++;
				} else{
					break;
				}
			}
		}
		
		/*
		 * Update the text field of the box/link
		 */
//		if(readableValues.size() > 0){
			for(AbstractUnspecifiedElementModel child : children){
				if(child.getElementid().equalsIgnoreCase("text")){					
//					Vector<Parameter> values = new Vector<Parameter>();
//					Parameter parameter = new Parameter();
//					parameter.setType(ParameterTypes.Text);
//					parameter.setValue(value.toString());
//					values.add(parameter);
//					PatternController controller;
//					
//					if(uiElement instanceof BoxPattern){
//						controller = ((BoxPattern)uiElement).getMyController();
//					} else if(uiElement instanceof LinkPanelPattern){ //instanceof 
//						controller = (PatternController) ((LinkPanelPattern)uiElement).getController();
//					} else {
//						FATDebug.print(FATDebug.ERROR, "ERROR [ElementConstraintsWindow]updateTextField controller. unknown uiElement type:" + uiElement);
//						return;
//					}
//					controller.updateElement(child.getId(), values);
					
					FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					updatePatternElement(
							child.getValue(ParameterTypes.AgentId), 
							child.getValue(ParameterTypes.PatternId),
							child.getValue(ParameterTypes.Id), ParameterTypes.Text.toString(),
							value.toString());
					break;
				}
			}
//		}
	}
	
	/*
	 * Sends a change direction event to PatternServerManager only if the direction has been changed,
	 * when it's received on PatternController the LinkVariable is updated. 
	 */
	private void changeDirectionIfModified(){
		LinkVariable link = (LinkVariable)elementVar;
		String newDirection = linkDirectionField.getValue();
		if(!origDirection.equals(newDirection)){
			FATDebug.print(FATDebug.DEBUG, "[ElementConstraintsWindow][changeDirectionIfModified] old:" + origDirection + " -new:" + newDirection);
			AbstractUnspecifiedElementModel elemModel = ((LinkPanelPattern)uiElement).getConnectedModel();
			String patternId = elemModel.getValue(ParameterTypes.PatternId);
			PatternController controller = (PatternController) ((LinkPanelPattern)uiElement).getController();
			String agentId = controller.getMapInfo().getAgentId();
	    	FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
			setLinkDirection(agentId, patternId, link.getVarID(), link.getSource().getVarID(), link.getTarget().getVarID());
		}
	}
	
	private String createPropInfoStr(PropDescr propDescr){
		StringBuffer str = new StringBuffer();
		str.append("(");
		str.append(propDescr.getComparisonGroup(PropDescr.DEFAULT_COMPONENT_ID) + ", ");
		if(propDescr.getSlotDataType(PropDescr.DEFAULT_COMPONENT_ID) == JessDataType.LIST){
			str.append(ComparisonDataType.SET.toString() + ", ");
		} else{
			str.append(propDescr.getSlotDataType(PropDescr.DEFAULT_COMPONENT_ID) + ", ");
		}
		
		if(propDescr instanceof StandardPropDescr){
			if(propDescr.getSlotDataType(PropDescr.DEFAULT_COMPONENT_ID).equals(JessDataType.LIST)){
				str.append("1..*");
			} else{
				str.append("1..1");
			}
		} else if(propDescr instanceof NonStandardPropDescr){
			str.append(((NonStandardPropDescr)propDescr).getMaxCardinality() + ".." + ((NonStandardPropDescr)propDescr).getMinCardinality());
		}
		str.append(")");
		return str.toString();
	}
	
//	void populateTreeGridForTesting(){
//		addParent("type", "type", "parentStatus");
//		addChild2Parent("type", "type", "childId1", "=", "test, hypothetical");
//		addChild2Parent("type", "type", "childId2", "=", "b1.type");
//		
//		addParent("creator", "creator", "parentStatus");
//		
//		addParent("modifiers", "modifiers", "parentStatus");
//		
//		addParent("first-interaction-ts", "first-interaction-ts", "parentStatus");
//		addChild2Parent("first-interaction-ts", "first-interaction-ts", "childId3", ">", "300");
//		
//		addParent("last-interaction-ts", "last-interaction-ts", "parentStatus");
//		
//		addParent("text", "text", "parentStatus");
//		addChild2Parent("text", "text", "childId4", "!=", "");
//	}

}
