package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Arrays;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.OntologyXML2ObjReader;
import lasad.shared.dfki.meta.ontology.Ontology;
import lasad.shared.dfki.meta.ontology.descr.ElementDescr;
import lasad.shared.dfki.meta.ontology.descr.NodeDescr;
//import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

//NOT IN USE
public class ConfigWindowPattern implements AbstractConfigWindow{

	private final Window window = new Window();
	
	private TextField<String> elemIdField;
	private TextField<String> elemNameField;
	private TextField<String> linkDirectionField;
	
	private ElementDescr elemDesc;
	private String ontologyId = "LARGO";
	//private String nodeId;
	
	public ConfigWindowPattern(){
		super();
		initSelectAgentWindow();
	}

	private void initSelectAgentWindow(){
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("600px", "500px");
		//window.setMaximizable(true);
		String componentType = "Box/Link";
		window.setHeading(FeedbackAuthoringStrings.PATTERN_COMPONENT_LABEL + " (" + componentType+ ")");
		
		window.setLayout(new FitLayout());
		window.add(getPanelContent());
		window.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//TODO check if we need to do something else before closing
			}
		});
		window.layout();
		
		Button okBtn = new Button(FeedbackAuthoringStrings.OK_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				//TODO save config
//				hide();
		    }
		});
		window.addButton(okBtn);
		
		Button cancelBtn = new Button(FeedbackAuthoringStrings.CANCEL_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
//				hide();  
			}  
		});
		window.addButton(cancelBtn);
	}
	
	private boolean isNode(ElementDescr elem){
		boolean retVal = false;
		if(elem != null && elem instanceof NodeDescr){
			retVal = true;
		}
		return retVal;
	}
	
	private void addElement(){
		
	}
	
	private void editElement(String id){
		
	}
	
	private void deleteElement(String id){
		
	}
	
	void populateGridForTesting(GroupingStore<ElementModel> store) {
		
		Map<String, String> map;
		ElementModel patternStub;
		
		map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, String.valueOf(1));
		map.put(GridElementLabel.PROPERTY_ID, "last-interaction-ts");
		map.put(GridElementLabel.OPER, "");
		map.put(GridElementLabel.VAL, "");
		patternStub = new ElementModel(map);
		store.add(patternStub);
		
		
		map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, String.valueOf(4));
		map.put(GridElementLabel.PROPERTY_ID, "first-interaction-ts");
		map.put(GridElementLabel.OPER, ">");
		map.put(GridElementLabel.VAL, "300");
		patternStub = new ElementModel(map);
		store.add(patternStub);
		
		map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, String.valueOf(4));
		map.put(GridElementLabel.PROPERTY_ID, "modifiers");
		map.put(GridElementLabel.OPER, "");
		map.put(GridElementLabel.VAL, "");
		patternStub = new ElementModel(map);
		store.add(patternStub);
		
		map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, String.valueOf(3));
		map.put(GridElementLabel.PROPERTY_ID, "creator");
		map.put(GridElementLabel.OPER, "");
		map.put(GridElementLabel.VAL, "");
		patternStub = new ElementModel(map);
		store.add(patternStub);
		
		map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, String.valueOf(2));
		map.put(GridElementLabel.PROPERTY_ID, "type");
		map.put(GridElementLabel.OPER, "=");
		map.put(GridElementLabel.VAL, "test, hypothetical");
		patternStub = new ElementModel(map);
		store.add(patternStub);	
		
	}
	
	private Widget getPanelContent(){
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setScrollMode(Scroll.AUTOY);
		panel.setWidth(600);
		panel.setHeight(500);
		
		Button buttonAdd = new Button(FeedbackAuthoringStrings.ADD_UPC_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL){
			@Override
			protected void onClick(ComponentEvent ce) {
				addElement();
				
			}
		};
		
		buttonAdd.setIconStyle("icon-plus-circle");//## icon-round-add
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
		toolBar.add(buttonAdd);
		panel.setTopComponent(toolBar);
		
//		FormPanel formPanel = new FormPanel();
//		formPanel.setHeaderVisible(false);
//		formPanel.setBodyBorder(false);
//		formPanel.setWidth(350);
//		formPanel.setLabelWidth(70);
		
		elemIdField = new TextField<String>();
		elemIdField.setFieldLabel(FeedbackAuthoringStrings.ELEMENT_ID_LABEL);
		elemIdField.setEnabled(false);
		//elemIdField.setValue(elemDesc.getTypeID());
		elemIdField.setValue("Elem ID");
//	    panel.add(elemIdField);
		
		elemNameField = new TextField<String>();
		elemNameField.setFieldLabel(FeedbackAuthoringStrings.ELEMENT_NAME_LABEL);
		elemNameField.setEnabled(false);
//		elemNameField.setValue(elemDesc.getTypeID());
		elemNameField.setValue("Elem name");
//	    panel.add(elemNameField);
	    
	    linkDirectionField = new TextField<String>();
	    linkDirectionField.setFieldLabel(FeedbackAuthoringStrings.DIRECTIONALITY_LABEL);
	    linkDirectionField.setEnabled(false);
	    if(!isNode(elemDesc)){
	    	linkDirectionField.setValue("S->T");
//	    	panel.add(linkDirectionField);
	    }
	    
	    Button directionalityButton = new Button(){
	    	@Override
	    	protected void onClick(ComponentEvent ce) {
	    		//MessageBox.info(FeedbackAuthoringStrings.ADD_SPECIFIC_TYPES_LABEL, FeedbackAuthoringStrings.ADD_SPECIFIC_TYPES_LABEL, null);
	    		
	    	}
	    };
	    directionalityButton.setToolTip(FeedbackAuthoringStrings.SWITCH_DIRECTION_LABEL);
	    directionalityButton.setIconStyle("icon-switch-direction");
	    directionalityButton.setStyleName("xfa-btn");
	    directionalityButton.setStyleName("x-btn-text", true);
	    directionalityButton.setStyleName("x-btn", true);
	    directionalityButton.setStylePrimaryName("xfa-btn");
	    if(!isNode(elemDesc)){
//	    	panel.add(directionalityButton);
	    }
	    
	    //panel.add(formPanel);
	    
	    ///////////////////////////
	    /*
	     * Grouping Grid
	     */
	    if(FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology() == null){
	    	Ontology ontology = new Ontology(ontologyId);
	    	OntologyXML2ObjReader.parseOntology(ontology, FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getXml());
	    	FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).setOntology(ontology);
	    }
	    //Ontology ontology = FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology();
	    
	    //List<StandardPropDescr> stdElemPropsList = Ontology.getStandardElemProperties();
//	    List<NonStandardPropDescr> nonStdElemPropsList = ontology.getNonStandardElemProperties(nodeId);
	    
	    
	    GroupingStore<ElementModel> store = new GroupingStore<ElementModel>();  
//	    store.add();  //TODO add data to be displayed
	    populateGridForTesting(store);
	    store.groupBy(GridElementLabel.PROPERTY_ID); //eg. type, first-ts, creator, creator, modifiers, text, awareness, etc.
	    
//	    ColumnConfig id = new ColumnConfig(GridElementLabel.ID, FeedbackAuthoringStrings.ID_LABEL, 60);
	    ColumnConfig operCol = new ColumnConfig(GridElementLabel.OPER, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.OPERATOR_LABEL + "</div>", 30);
		ColumnConfig valCol = new ColumnConfig(GridElementLabel.VAL, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.VALUE_LABEL + "</div>", 200);
		ColumnConfig propertyIdCol = new ColumnConfig(GridElementLabel.PROPERTY_ID, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.PROPERTY_ID_LABEL + "</div>", 60);
		ColumnConfig actionsCol = new ColumnConfig(GridElementLabel.ACTIONS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ACTIONS_LABEL + "</div>", 40);
		final ColumnModel cm = new ColumnModel(Arrays.asList(propertyIdCol, operCol, valCol, actionsCol));
		
		actionsCol.setRenderer(new GridCellRenderer<ElementModel>(){
			
			@Override
//			public Widget render(ModelData model, String property,
//					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
//					int rowIndex, int colIndex, ListStore store, Grid grid) {
			public Object render(ElementModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ElementModel> store, Grid<ElementModel> grid) {
				
				return getActionButtonsPanel(model, property, config, rowIndex, colIndex, store, grid);
			}
			
			private ContentPanel getActionButtonsPanel(final ModelData model, String property,
					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
					int rowIndex, int colIndex, final ListStore<ElementModel> store, final Grid<ElementModel> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				Button editButton = new Button();
				Button deleteButton = new Button();
				
				editButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String elementId = model.get(GridElementLabel.ID);
				    	  editElement(elementId);
				      }
				});
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  final String elementId = model.get(GridElementLabel.ID);
				    	  final String elementOper = model.get(GridElementLabel.OPER);
				    	  final String elementValue = model.get(GridElementLabel.OPER);
				    	  
				    	  final ElementModel bm = (ElementModel) store.findModel(GridElementLabel.ID, elementId);
				    	  
				    	  ce.setCancelled(true);
				    	  //Initialize message box
				    	  MessageBox box = new MessageBox();
				    	  box.setButtons(MessageBox.YESNO);
				    	  box.setIcon(MessageBox.QUESTION);
				    	  box.setTitle(FeedbackAuthoringStrings.DELETE_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL);
				    	  box.setMessage(FeedbackAuthoringStrings.DELETE_LABEL + " " + FeedbackAuthoringStrings.CONSTRAINT_LABEL + " \"" + elementOper + "\"-\"" + elementValue + "\"" + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  box.addCallback(new Listener<MessageBoxEvent>() {
				    		  public void handleEvent(MessageBoxEvent be) {
				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
				    				  grid.getStore().remove(bm);
							    	  deleteElement(elementId);
				    			  }
				    		  }
				    	  });
				    	  box.show();
				      }
				});
				
				editButton.setStyleName("xfa-btn");
				deleteButton.setStyleName("xfa-btn");
				
				editButton.setStyleName("x-btn-text", true);
				deleteButton.setStyleName("x-btn-text", true);
				
				editButton.setStyleName("x-btn", true);
				deleteButton.setStyleName("x-btn", true);
				
				editButton.setStylePrimaryName("xfa-btn");
				deleteButton.setStylePrimaryName("xfa-btn");
				
				editButton.setIconStyle("icon-edit-gear");
				deleteButton.setIconStyle("icon-delete-circle");
				
				editButton.setWidth(16);
				deleteButton.setWidth(16);
				
				editButton.setHeight(16);
				deleteButton.setHeight(16);
				
				editButton.setMinWidth(16);
				deleteButton.setMinWidth(16);
				
				editButton.setToolTip(FeedbackAuthoringStrings.EDIT_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL);
				deleteButton.setToolTip(FeedbackAuthoringStrings.DELETE_LABEL + " " + FeedbackAuthoringStrings.PROPERTY_LABEL);
				
				panel.add(editButton, new RowData(0.25, 0.25, new Margins(0)));
				panel.add(deleteButton, new RowData(0.25, 0.25, new Margins(0)));

				return panel;
			}

		});
		
		GroupingView view = new GroupingView();  
	    view.setShowGroupedColumn(false);  
	    view.setForceFit(true);  
	    view.setGroupRenderer(new GridGroupRenderer() {  
	      public String render(GroupColumnData data) {  
	        //String f = cm.getColumnById(data.field).getHeader();  
	        String l = data.models.size() == 1 ? "Item" : "Items";  
	        //return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
	        return data.group + " (" + data.models.size() + " " + l + ")";
	      }  
	    });
	    
	    Grid<ElementModel> grid = new Grid<ElementModel>(store, cm);
	    grid.setView(view);
	    grid.setLazyRowRender(0);
	    grid.setHideHeaders(false);//for column headers
	    grid.setBorders(false);
	    grid.setAutoExpandColumn(GridElementLabel.VAL);
	    grid.setAutoExpandMax(350);
	    grid.setAutoWidth(true);
	    grid.setTrackMouseOver(true);
	    grid.getSelectionModel().setLocked(true);
	    grid.setStripeRows(true);
	    
	    panel.add(grid);
	    
		return panel;
	}

	@Override
	public void show() {
		window.show();
		
	}
}
