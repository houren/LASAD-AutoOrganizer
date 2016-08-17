package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingInfoDialogue;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.DatabaseFA;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.OntologyFA;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.SessionFA;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.TreeFolder;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.TreeFolderChild;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.AgentsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.ColumnConf;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.GridConf;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PatternsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.tree.CustomizedTreeGridView;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CloneAgentDescriptionFE;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ToasterMessage;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ActionUserEventProperty;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.UserActionEventImpl;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.AddUpdateAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.AgentUserActionEventImpl;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.CompileAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.CopyAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.DeleteAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ontology.AddAgentToOntologyEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ontology.OntologyUserActionEventImpl;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ontology.RemoveAgentFromOntologyEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.session.AddAgentToSessionEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.session.RemoveAgentFromSessionEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.session.SessionUserActionEventImpl;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.sessions.SessionsUserActionEventImpl;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.sessions.StartSessionEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.sessions.StopSessionEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.OntologyXML2ObjReader;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation.ValidateAgentDescription;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddAgent2Ontology;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddAgent2Session;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper.AgentRequestInfo;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper.FreshIdRequestQueue;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper.PatternRequestInfo;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.ServiceStatus;
import lasad.shared.dfki.meta.agents.ActionAgentConfigData;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseModelerDef_Empirical;
import lasad.shared.dfki.meta.agents.analysis.phases.PhasesDef;
import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.common.ActionListDef;
import lasad.shared.dfki.meta.ontology.Ontology;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * Feedback Authoring Tool Tab Content
 * This class creates the tool's main window
 * 
 * @author anahuacv
 * 
 */
public class FeedbackAuthoringTabContent extends ContentPanel{
	private static final int LOADING_TIME = 5000*2; //in milliseconds //Time to wait per init request before giving up
	private final LASADActionSender communicator = LASADActionSender.getInstance();  //To communicate with server
	private final ActionFactory actionBuilder = ActionFactory.getInstance();  //To communicate with server
	
	private FreshIdRequestQueue freshIdRequestQueue = new FreshIdRequestQueue();
	/*
	 * Use to force the processing of the initial information in a given order.
	 * Required because the action messages with the info could arrive in different order. 
	 */
	private Vector<String> initMsgsTracker = new Vector<String>();
	private HashMap<String, Action> initMsgsQueueVal = new HashMap<String, Action>();
	private HashMap<String, Boolean> initMsgsQueueFlags = new HashMap<String, Boolean>();
	private boolean initMsgs = false;
	private Object initMsgsLock = new Object();
	private Timer initTimer;
	
//	public int status = 0;
	
	private static FeedbackAuthoringTabContent instance = null; //
	
	private DatabaseFA db = new DatabaseFA();
	private PatternServerManager patternServerManager = new PatternServerManager();
	/*
	 * ServiceID id = new ServiceID(agentId, patternId, ServiceClass.ANALYSIS);
	 */
	private Map<ServiceID, StructuralAnalysisTypeManipulator> pattternManipulatorMap = new HashMap<ServiceID, StructuralAnalysisTypeManipulator>();
	
	private AgentsGrid agentsPanel;
	
	private TreeGrid<ModelData> agents2OntologyTree;
	private TreeGrid<ModelData> agents2SessionTree;
	private TreeGrid<ModelData> sessionsTree;
	
	public static FeedbackAuthoringTabContent getInstance() {
		if (instance == null) {
			instance = new FeedbackAuthoringTabContent();
		}
		return instance;
	}
	
	private FeedbackAuthoringTabContent() {
		super(new RowLayout(Orientation.HORIZONTAL));
	}
	
	private void initContent(){
		//setUpTestData();
		LoadingInfoDialogue.getInstance().showLoadingScreen();
		startInitTimer();
		getPanelsInfo();
//		initAgentPatternManipulators();
	}
	
	/*
	 * This timer is used to track the loading of the FAT, it is restarted each time one
	 * of the init actions messages arrives. If all the init action messages arrive, the timer
	 * is canceled, otherwise an error is shown.
	 */
	private void startInitTimer(){
		initTimer = new Timer() {
			@Override
			public void run() {
				if(!isInitMsgs()){
					handleInitError();
				}
			}
		};
		initTimer.schedule(LOADING_TIME);
	}
	public void cancelInitTimer(){
		if(initTimer != null){
			initTimer.cancel();
		}
	}
	public void resetInitTimer(){
		cancelInitTimer();
		startInitTimer();
	}
	
	/*
	 * Displayed when the initTimer is triggered. 
	 */
	private void handleInitError(){
		LoadingInfoDialogue.getInstance().closeLoadingScreen();
		MessageBox box = new MessageBox();
		box.setButtons(MessageBox.OK);
		box.setIcon(MessageBox.ERROR);
		box.setTitle(FeedbackAuthoringStrings.ERROR_LABEL);
		box.setMessage(FeedbackAuthoringStrings.INIT_ERROR_MSG);
		box.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
				}
			}
		});
		box.show();
	}
	
	/*
	 * requests all the data necessary to 
	 * populate the panels, the initMsgsTracker is used
	 * to process the incoming messages in the given order
	 * since there are dependencies.  
	 */
	private void getPanelsInfo(){
		initMsgsTracker.add(Commands.ListOntologies.toString());
		initMsgsTracker.add(Commands.ListAgentsInfo.toString());
		initMsgsTracker.add(Commands.ListMap.toString());
		initMsgsTracker.add(Commands.ListAgentsToSessions.toString());
		initMsgsTracker.add(Commands.ListAgentsToOntologies.toString());
		initMsgsTracker.add(Commands.ListSessionStatus.toString());
		initMsgsTracker.add(Commands.GetOntologyDetails.toString());
		
//		initMsgsQueueFlags.put(Commands.ListOntologies.toString(), new Boolean(false));
//		initMsgsQueueFlags.put(Commands.ListAgentsInfo.toString(), new Boolean(false));
//		initMsgsQueueFlags.put(Commands.ListMap.toString(), new Boolean(false));
//		initMsgsQueueFlags.put(Commands.ListAgentsToSessions.toString(), new Boolean(false));
//		initMsgsQueueFlags.put(Commands.ListAgentsToOntologies.toString(), new Boolean(false));
//		initMsgsQueueFlags.put(Commands.ListSessionStatus.toString(), new Boolean(false));
		//initMsgsQueueFlags.put(Commands.GetOntologyDetails.toString(), new Boolean(false));
		
		getOntologyList();
		getAgentList();
		getMapList();
		getAgent2Session();
		getAgent2Ontology();
		getSessionsStatus();
		getOntologyDetails(FATConstants.FA_ONTOLOGY_NAME);
	}
	
	public void refreshPanels(){
		//TODO Clean current data and clean panels
	}
	
	private void getAgentList(){
		communicator.sendActionPackage(actionBuilder.getAgentListFA());
	}
	
	private void getMapList(){
		communicator.sendActionPackage(actionBuilder.getMapListFA());
	}
	
	private void getOntologyList(){
		communicator.sendActionPackage(actionBuilder.getOntologyListFA());
	}
	public void getOntologyDetails(String ontology){
		communicator.sendActionPackage(actionBuilder.getOntologyDetailsFA(ontology));
	}
	
	private void getAgent2Ontology(){
		communicator.sendActionPackage(actionBuilder.getAgent2OntologyFA());
	}
	
	private void getAgent2Session(){
		communicator.sendActionPackage(actionBuilder.getAgent2SessionFA());
	}
	
	private void getSessionsStatus(){
		communicator.sendActionPackage(actionBuilder.getSessionsStatusFA());
	}
	
//	private void initAgentPatternManipulators(){
//		List<String> agentList = db.getAgents();
//		for(String agentId:agentList){
//			AgentDescriptionFE agentDescFE = db.getAgent(agentId);
//			updatePatternManipulatorMap(agentDescFE);
//		}
//	}
	
	@Override  
	  protected void onRender(Element parent, int index) {  
	    super.onRender(parent, index);
	    
	    this.setHeaderVisible(false);
	    this.setBodyBorder(false);
//	    this.setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		add(initLeftPanel(), new RowData(0.5, 1.0, new Margins(5)));
		add(initRightPanel(), new RowData(0.5, 1.0, new Margins(5)));
		
		layout();
		initContent();
	}
	
	private ContentPanel initRightPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.add(createSessionsPanel());
		
		return panel;
	}
	
	private ContentPanel initLeftPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new AccordionLayout());
		panel.setHeaderVisible(false);
		panel.add(initLeftPanelContent());
		return panel;
	}
	
	private ContentPanel initLeftPanelContent() {
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		panel.setHeaderVisible(false);
		
		agentsPanel = (AgentsGrid) getAgentsPanel();
		panel.add(agentsPanel, new RowData(1.0, 0.34, new Margins(3)));
		panel.add(createOntologiesAgentsPanel(), new RowData(1.0, 0.33, new Margins(3)));
		panel.add(createAgents2SessionPanel(), new RowData(1.0, 0.33, new Margins(3)));
		
		return panel;
	}
    
    /*
     * Add the Agent to the Agent Panel in the UI
     */
    private void addAgent2UI(AgentDescriptionFE agent){
    	String agentId = agent.getAgentID();
    	String agentDisplayName = agent.getDisplayName();
    	String ontology = null;   // =  agent.getOntology();
    	SupportedOntologiesDef supportedOnt = agent.getSupportedOntology();
    	if(supportedOnt != null)
	    	if (supportedOnt.isAllOntologies()){
	    		//TODO add code to handle this,
	    		//this happens when "any" is selected on the GUI
	    	}else{
	    		//TODO ask about this multiple ontology support to update code accordingly
	    		List<String> ontList =  supportedOnt.getSupportedOntologies();
	    		if (ontList.size() >= 1){
	    			ontology = ontList.get(0);
	    		}
	    	}
    	int configCompleted = getAgentCompileStatus(agent);
    	boolean readable = agent.isConfReadable();
    	boolean writeable = agent.isConfWritable();
    	agentsPanel.addAgent2Grid(agentId, agentDisplayName, ontology, readable, writeable, configCompleted);
    }
    
    public int getAgentCompileStatus(AgentDescriptionFE agent){
    	int retVal = FATConstants.AGENT_STATUS_OK;
    	boolean tmp = agent.isConfigCompleted();
    	if(tmp){
    		retVal = FATConstants.AGENT_STATUS_COMPILED;
    	} else{
    		/*
    		 * FATConstants.AGENT_STATUS_OK o FATConstants.AGENT_STATUS_ERROR
    		 */
    		String errorMsg = ValidateAgentDescription.validateConf(agent);
    		if(errorMsg.equals("")){
    			retVal = FATConstants.AGENT_STATUS_OK;
    		} else{
    			retVal = FATConstants.AGENT_STATUS_ERROR;
    			//FATDebug.print(FATDebug.ERROR, "[FeedbackAuthoringTabContent][getAgentCompileStatus]Agent " + agent.getDisplayName() + " has configuration errors\n");
    			//FATDebug.print(FATDebug.ERROR, "\n" + errorMsg);
    		}
    	}
    	return retVal;
    }
    
    private void deleteAgentFromUI(String agentId){
    	AgentDescriptionFE agent = getAgentFromDB(agentId);
    	String agentDisplayName = agent.getDisplayName();
    	
    	ElementModel agentStub = new ElementModel();
		agentStub.set(GridElementLabel.ID, agentId);
		agentStub.set(GridElementLabel.NAME, agentDisplayName);
		agentsPanel.deleteElementFromStore(agentStub);
    }
    
    /*
     * Update the Agent on the Agent Panel in the UI
     */
    private void updateAgent2UI(AgentDescriptionFE agent){
    	agentsPanel.updateAgent(agent);
    }

	
	private ContentPanel getAgentsPanel(){
		ContentPanel panel;
		GridConf gridConf = new GridConf();
		
		gridConf.setHeader(FeedbackAuthoringStrings.HEADING_AGENTS);
		gridConf.setMarkReadyButtonFlag(true);
		gridConf.setAddButtonFlag(true);
		gridConf.setViewButtonFlag(true);
		gridConf.setEditButtonFlag(true);
		gridConf.setDeleteButtonFlag(true);
		gridConf.setDuplicateButtonFlag(true);
		gridConf.setCommonLabel(FeedbackAuthoringStrings.AGENT_LABEL);
		
		ColumnConf colTmp = new ColumnConf(GridElementLabel.NAME, FeedbackAuthoringStrings.NAME_LABEL, 140);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.STATUS, FeedbackAuthoringStrings.STATUS_LABEL, 180);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.ACTIONS, FeedbackAuthoringStrings.ACTIONS_LABEL, 180);
		gridConf.addColConfig(colTmp);
		
		panel = AgentsGrid.getInstance(gridConf, this);
		return panel;
	}
	
	/*
	 * for ontology rows column NAME = ontology
	 * for agent rows column NAME = agentName
	 */
	private ContentPanel createOntologiesAgentsPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());

		final ColumnConfig nameCol = new ColumnConfig(GridElementLabel.NAME, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ONTOLOGY_LABEL + "</div>", 440);
		ColumnConfig actionsCol = new ColumnConfig(GridElementLabel.ACTIONS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ACTIONS_LABEL + "</div>", 80);
		ColumnModel treeCM = new ColumnModel(Arrays.asList(nameCol, actionsCol));
		
		TreeStore<ModelData> agent2OntologyTreeStore = new TreeStore<ModelData>();
		agents2OntologyTree = new TreeGrid<ModelData>(agent2OntologyTreeStore, treeCM);
		
		nameCol.setRenderer(new TreeGridCellRenderer<ModelData>());
		actionsCol.setRenderer(new GridCellRenderer<ModelData>() {
			
			private ContentPanel getAddActionButtonPanel(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				Button addButton = new Button();
				addButton.setStyleName("xfa-btn");
				addButton.setStyleName("x-btn-text", true);
				addButton.setStyleName("x-btn", true);
				addButton.setStylePrimaryName("xfa-btn");
				addButton.setIconStyle("icon-plus-circle");//icon-plus-std
				addButton.setWidth(16);
				addButton.setHeight(16);
				addButton.setToolTip(FeedbackAuthoringStrings.ADD_AGENT_LABEL);
				
				addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
//				    	  String element = (String) model.get(nameCol.getId());
				    	  String ontologyId = model.get(GridElementLabel.ID);
				    	  String ontologyName = model.get(GridElementLabel.NAME);

				    	  //Info.display(ontology, "Ontology:" + ontology);
//				    	  BaseTreeModel ontBTM = (BaseTreeModel) agents2OntologyTree.getStore().findModel(GridElementLabel.NAME, ontology);
//				    	  String ontology2 = (String)ontBTM.get(GridElementLabel.NAME);
				    	  
//				    	  Map<String,String> agentMap = db.getAgentsIdNameMap();
				    	  Map<String,String> agentMap = getCompiledAgents(ontologyId);
				    	  AddAgent2Ontology selOnt = new AddAgent2Ontology(agentMap, ontologyId, ontologyName, instance);
				    	  selOnt.show();
				      }
				});
				
				panel.add(addButton, new RowData(0.25, 1.0, new Margins(0)));
				return panel;
			}
			
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
				deleteButton.setIconStyle("icon-delete-circle");//icon-minus-std
				deleteButton.setWidth(16);
				deleteButton.setHeight(16);
				deleteButton.setToolTip(FeedbackAuthoringStrings.REMOVE_AGENT_LABEL);
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
//				    	  final String elementAgentId = (String) model.get(GridElementLabel.ID);
				    	  final String agentId = (String) model.get(GridElementLabel.ID);
				    	  final String agentName = (String) model.get(GridElementLabel.NAME);
				    	  final String ontology = (String) model.get(GridElementLabel.ONTOLOGY);
//				    	  BaseTreeModel agBTM = (BaseTreeModel) agents2OntologyTree.getStore().findModel(GridElementLabel.ID, elementAgentId);
//				    	  final String agentId = agBTM.get(GridElementLabel.ID);
//				    	  final String agentName = agBTM.get(GridElementLabel.NAME);
//				    	  final String ontology = agBTM.get(GridElementLabel.ONTOLOGY); //the ontology is store here
				    	  
				    	  ce.setCancelled(true);
				    	  //Initialize message box
				    	  MessageBox box = new MessageBox();
				    	  box.setButtons(MessageBox.YESNO);
				    	  box.setIcon(MessageBox.QUESTION);
				    	  box.setTitle(FeedbackAuthoringStrings.REMOVE_AGENT_LABEL);
				    	  box.setMessage(FeedbackAuthoringStrings.REMOVE_LABEL + " " + agentName + " " 
				    			  		+ FeedbackAuthoringStrings.FROM_LABEL + " " 
				    			  		+ ontology + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  box.addCallback(new Listener<MessageBoxEvent>() {
				    		  public void handleEvent(MessageBoxEvent be) {
				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							    	  //Info.display(elementAgentId, elementAgentId + "-" + agentId + "-" + agentName + "-" + ontology);
							    	  handleUserActionEvent(new RemoveAgentFromOntologyEvent(ontology, agentName, agentId));
				    			  }
				    		  }
				    	  });
				    	  box.show();
				      }
				});
				
				panel.add(deleteButton, new RowData(0.25, 0.25, new Margins(0)));
				return panel;
			}

            @Override
            public Widget render(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
            	if (model instanceof TreeFolderChild){
                	//FATDebug.print(FATDebug.INFO, "Is TreeFolderChild!!!!");
                	return getDeleteActionButtonPanel(model, property,
                            config, rowIndex, colIndex, store, grid);
                }
                if (model instanceof TreeFolder){
                	//FATDebug.print(FATDebug.INFO, "Is TreeFolder!!!!");
                }
                return getAddActionButtonPanel(model, property,
                        config, rowIndex, colIndex, store, grid);

            }
        });
		
		agents2OntologyTree.setBorders(true);
		agents2OntologyTree.setAutoExpand(false);
		agents2OntologyTree.setAutoExpandColumn(GridElementLabel.NAME);
		agents2OntologyTree.setAutoExpandMax(1000);
		agents2OntologyTree.setTrackMouseOver(true);
		agents2OntologyTree.getStyle().setNodeCloseIcon(null);
		agents2OntologyTree.getStyle().setNodeOpenIcon(null);
		agents2OntologyTree.setStripeRows(true);
		
		CustomizedTreeGridView treeGridView = new CustomizedTreeGridView();
		agents2OntologyTree.setView(treeGridView);
//		agentsOntoTree.getTreeView().setRowHeight(28);
//		agentsOntoTree.getTreeView().setRowSelectorDepth(20);
//		agentsOntoTree.getTreeView().setCellSelectorDepth(10);
		
		TreeGridSelectionModel<ModelData> selectionModel = new TreeGridSelectionModel<ModelData>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setLocked(true);
		agents2OntologyTree.setSelectionModel(selectionModel);

		panel.setHeading(FeedbackAuthoringStrings.HEADING_ONTO_AGENTS);
		panel.setScrollMode(Scroll.AUTOY);
		panel.add(agents2OntologyTree);
		
		return panel;
	}
	/*
	 * Gets compiled agents with the given ontology
	 */
	private Map<String,String> getCompiledAgents(String ontologyId){
		Map<String,String> agentMap = new LinkedHashMap<String, String>();
		Map<String,String> tmpMap = db.getAgentsIdNameMap();
		for(String key : tmpMap.keySet()){
			AgentDescriptionFE tmpAgent = db.getAgent(key);
			if(ontologyId != null &&FATConstants.AGENT_STATUS_COMPILED == getAgentCompileStatus(tmpAgent)){
				SupportedOntologiesDef supportedOnt = tmpAgent.getSupportedOntology();
		    	if (supportedOnt != null && supportedOnt.getSupportedOntologies().size() >=1){
		    		String ontology = supportedOnt.getSupportedOntologies().get(0);
		    		if(ontology.equals(ontologyId)){
		    			agentMap.put(key, tmpMap.get(key));
		    		}
		    	}
			}
		}
		return agentMap;
	}
	
	/*
	 * Functions that interact with Agent to Ontology Panel
	 */
	public void addElems2Agent2OntPanel(Map<String, List<String>> map){
		//Clean previous data
		//here we remove the agents, the ontologies are kept
		for(String ont : getOntologiesDB()){
			ModelData parent = agents2OntologyTree.getTreeStore().findModel(GridElementLabel.ID, ont);
			agents2OntologyTree.getTreeStore().removeAll(parent);
			db.deleteAllAgentsFromOntology(ont);
		}
		
		Set<String> ontlist = map.keySet();
        for(String ontology: ontlist){
        	List<String> agentList = map.get(ontology);
        	for(String agentId:agentList){
        		addElem2Agent2OntPanel(ontology, agentId);
        	}
        }
	}
//	public void addElem2Agent2OntPanel(String ontology, String agentId, String agentName){
//		addElem2Agent2OntPanel(ontology, agentId, agentName);
//	}
	public void addElem2Agent2OntPanel(String ontology, String agentId){
		String agentName = getAgentName(agentId);
		addAgent2OntologyUI(ontology, agentId, agentName);
		db.addAgent2Ontology(agentId, ontology);
	}
	public void deleteElemFromAgent2OntPanel(String ontology, String agentId){
		deleteAgentFromOntologyUI(ontology, agentId);
		deleteAgentFromOntologyDB(ontology, agentId);
	}
	public void addOntologies2OntPanel(List<String> ontologyList){
		addOntologies2OntologyUI(ontologyList);
		for(String ont: ontologyList){
			db.addOntology(new OntologyFA(ont));
		}
	}
	private void addAgent2OntologyUI(String ontology, String agentId, String agentName){
		ModelData parent = agents2OntologyTree.getTreeStore().findModel(GridElementLabel.ID, ontology);
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.NAME, agentName);
		map.put(GridElementLabel.ID, agentId);
		map.put(GridElementLabel.ONTOLOGY, ontology);
		TreeFolderChild child = new TreeFolderChild(map);
		agents2OntologyTree.getTreeStore().add(parent, child, false);
		//agents2OntologyTree.setExpanded(parent, true);
	}
	private void deleteAgentFromOntologyUI(String ontology, String agentId){
		ModelData parent = agents2OntologyTree.getTreeStore().findModel(GridElementLabel.ID, ontology);
		List<ModelData> children = agents2OntologyTree.getTreeStore().getChildren(parent);
		ModelData child = null;
		for(int i=0; i < children.size(); i++){
			String agId = children.get(i).get(GridElementLabel.ID); 
			if(agentId.equals(agId)){
				child = children.get(i);
				break;
			}
		}
		agents2OntologyTree.getTreeStore().remove(parent, child);
	}
	private void refreshViewAgent2OntologyUI(){
		if(agents2OntologyTree.isRendered())
			agents2OntologyTree.getView().refresh(false);
	}
	private void addOntologies2OntologyUI(List<String> ontologyList){
//		Map<String, String> mapOne = new HashMap<String, String>();
//		mapOne.put(GridElementLabel.NAME, "root");
//		TreeFolder model= new TreeFolder(mapOne);
		
		for(String ont: ontologyList){
			Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, ont);
			map.put(GridElementLabel.NAME, ont);
			TreeFolder parent = new TreeFolder(map, new TreeFolderChild[]{});
			//model.add(parent);
			addParent2OntologyUI(parent);
		}
		//agents2OntologyTree.getTreeStore().add(model.getChildren(), true);
	}
	private void addParent2OntologyUI(TreeFolder parent){
		agents2OntologyTree.getTreeStore().add(parent, true);
	}
	
	/*
	 * Functions that interact with Agent to Session Panel
	 */
	public void addElems2Agent2SesPanel(Map<String, List<String>> map){
		//Clean previous data
		//here we remove the agents, the sessions are kept
		for(String sesId : db.getSessions()){
			ModelData parent = agents2SessionTree.getTreeStore().findModel(GridElementLabel.ID, sesId);
			agents2SessionTree.getTreeStore().removeAll(parent);
			db.deleteAllAgentsFromSession(sesId);
		}
		Set<String> seslist = map.keySet();
        for(String sessionId: seslist){
        	String sessionName = getSessionNameDB(sessionId);
        	if(sessionName != null){
        		List<String> agentList = map.get(sessionId);
            	for(String agentId:agentList){
            		addElem2Agent2SesPanel(sessionId, sessionName, agentId);
            	}
        	}else{
        		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent]"
						+ "[addElems2Agent2SesPanel]"
						+ "sessionId '" + sessionId + "' is not registered" , Logger.DEBUG_ERRORS);
        	}
        	
        }
		refreshViewAgent2SesPanelUI();
	}	
	public void addElem2Agent2SesPanel(String sessionId, String sessionName, String agentId){
		addAgent2Agent2SesPanelUI(sessionId, sessionName, agentId);
		db.addAgent2Session(agentId, sessionId);
	}
	public void deleteElemFromAgent2SesPanel(String sessionId, String agentId){
		deleteAgentFromAgent2SesPanelUI(sessionId, agentId);
		deleteAgentFromSessionDB(sessionId, agentId);
	}
	public void addSessions2Agent2SesPanel(Map<String, String> id2OntMap, Map<String, String> id2NameMap){
		addSessions2Agent2SesPanelUI(id2OntMap, id2NameMap);
		addSessions2DB(id2OntMap, id2NameMap);
	}
	private void addAgent2Agent2SesPanelUI(String sessionId, String sessionName, String agentId){
		ModelData parent = agents2SessionTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		
		String agentName = getAgentName(agentId);
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.NAME, agentName);
		map.put(GridElementLabel.ID, agentId);
		map.put(GridElementLabel.SESSION, sessionId);
		//map.put(GridElementLabel.SESSIONID, sessionId);
		map.put(GridElementLabel.ONTOLOGY, "");
		TreeFolderChild child = new TreeFolderChild(map);
		
		agents2SessionTree.getTreeStore().add(parent, child, false);
		//agents2SessionTree.setExpanded(parent, true);
	}
	private void deleteAgentFromAgent2SesPanelUI(String sessionId, String agentId){
		ModelData parent = agents2SessionTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		List<ModelData> children = agents2SessionTree.getTreeStore().getChildren(parent);
		ModelData child = null;
		for(int i=0; i < children.size(); i++){
			String agId = children.get(i).get(GridElementLabel.ID); 
			if(agId.equals(agentId)){
				child = children.get(i);
				break;
			}
		}
		agents2SessionTree.getTreeStore().remove(parent, child);
	}
	private void refreshViewAgent2SesPanelUI(){
		if(agents2SessionTree.isRendered())
			agents2SessionTree.getView().refresh(false);
	}
	private void addSessions2Agent2SesPanelUI(Map<String, String> id2OntMap, Map<String, String> id2NameMap){
		Set<String> ids = id2OntMap.keySet();
		
//		Map<String, String> mapOne = new HashMap<String, String>();
//		mapOne.put(GridElementLabel.NAME, "root");
//		mapOne.put(GridElementLabel.ID, "root");
//		mapOne.put(GridElementLabel.ONTOLOGY, "");
//		TreeFolder model = new TreeFolder(mapOne);
		
		for(String sessionId: ids){
			String sessionOnt = id2OntMap.get(sessionId);
			String sessionName = id2NameMap.get(sessionId);
			
			Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.NAME, sessionName);
			map.put(GridElementLabel.ONTOLOGY, "("+sessionOnt+")");
			map.put(GridElementLabel.ID, sessionId);
			TreeFolder parent = new TreeFolder(map, new TreeFolderChild[]{});
			//model.add(parent);
			addParent2Agent2SesUI(parent);
		}
		//agents2SessionTree.getTreeStore().add(model.getChildren(), true);
	}
	private void addParent2Agent2SesUI(TreeFolder parent){
		agents2SessionTree.getTreeStore().add(parent, true);
	}
	
	/*
	 * Creates the Agent to Session panel, middle left
	 */
	private ContentPanel createAgents2SessionPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());

		final ColumnConfig nameCol = new ColumnConfig(GridElementLabel.NAME, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.SESSION_LABEL + "</div>", 280);
		ColumnConfig statusCol = new ColumnConfig(GridElementLabel.ONTOLOGY, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ONTOLOGY_LABEL + "</div>", 160);
		ColumnConfig actionsCol = new ColumnConfig(GridElementLabel.ACTIONS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ACTIONS_LABEL + "</div>", 80);
		ColumnModel treeCM = new ColumnModel(Arrays.asList(nameCol, statusCol, actionsCol));
		
		statusCol.setStyle("color:black;");
		
		TreeStore<ModelData> agent2SessionTreeStore = new TreeStore<ModelData>();
		agents2SessionTree = new TreeGrid<ModelData>(agent2SessionTreeStore, treeCM);
		
		nameCol.setRenderer(new TreeGridCellRenderer<ModelData>());
		actionsCol.setRenderer(new GridCellRenderer<ModelData>() {
			
			private ContentPanel getAddActionButtonPanel(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				Button addButton = new Button();
				addButton.setStyleName("xfa-btn");
				addButton.setStyleName("x-btn-text", true);
				addButton.setStyleName("x-btn", true);
				addButton.setStylePrimaryName("xfa-btn");
				addButton.setIconStyle("icon-plus-circle");//icon-plus-std
				addButton.setWidth(16);
				addButton.setHeight(16);
				addButton.setToolTip(FeedbackAuthoringStrings.ADD_AGENT_LABEL);
				
				addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
//				    	  String sessionIdTmp = (String) model.get(GridElementLabel.ID);
				    	  String sessionId = (String) model.get(GridElementLabel.ID);
				    	  String sessionName = (String) model.get(GridElementLabel.NAME);
				    	  //String ontologyId = (String) model.get(GridElementLabel.NAME);
				    	  String ontologyId = db.getSession(sessionId).getOntology();
				    	  
//				    	  Info.display(sessionIdTmp, sessionIdTmp);
//				    	  BaseTreeModel ontBTM = (BaseTreeModel) agents2SessionTree.getStore().findModel(GridElementLabel.ID, sessionIdTmp);
//				    	  Map<String,String> agentMap = db.getAgentsIdNameMap();
//				    	  String sessionId = (String)ontBTM.get(GridElementLabel.ID);
//				    	  String sessionName = (String)ontBTM.get(GridElementLabel.NAME);
				    	  Map<String,String> agentMap = getCompiledAgents(ontologyId);
				    	  AddAgent2Session selSe = new AddAgent2Session(agentMap, sessionId, sessionName, instance);
				    	  selSe.show();
				      }
				});
				
				panel.add(addButton, new RowData(0.25, 1.0, new Margins(0)));
				return panel;
			}
			
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
				deleteButton.setIconStyle("icon-delete-circle");
				deleteButton.setWidth(16);
				deleteButton.setHeight(16);
				deleteButton.setToolTip(FeedbackAuthoringStrings.REMOVE_AGENT_LABEL);
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
//				    	  final String elementAgentId = (String) model.get(GridElementLabel.ID);
				    	  final String agentId = (String) model.get(GridElementLabel.ID);
				    	  final String agentName = (String) model.get(GridElementLabel.NAME);
				    	  final String sessionId = (String) model.get(GridElementLabel.SESSION);
//				    	  BaseTreeModel agBTM = (BaseTreeModel) agents2SessionTree.getStore().findModel(GridElementLabel.ID, elementAgentId);
//				    	  final String agentId = agBTM.get(GridElementLabel.ID);
//				    	  final String agentName = agBTM.get(GridElementLabel.NAME);
//				    	  final String sessionId = agBTM.get(GridElementLabel.SESSION); //the sessionId is stored here
				    	  final String sessionName = getSessionNameDB(sessionId);
				    	  
				    	  ce.setCancelled(true);
				    	  //Initialize message box
				    	  MessageBox box = new MessageBox();
				    	  box.setButtons(MessageBox.YESNO);
				    	  box.setIcon(MessageBox.QUESTION);
				    	  box.setTitle(FeedbackAuthoringStrings.REMOVE_AGENT_LABEL);
				    	  box.setMessage(FeedbackAuthoringStrings.REMOVE_LABEL + " " + agentName + " " 
			    			  		+ FeedbackAuthoringStrings.FROM_LABEL + " " 
			    			  		+ sessionName + FeedbackAuthoringStrings.QUESTION_MARK);
				    	  
				    	  box.addCallback(new Listener<MessageBoxEvent>() {
				    		  public void handleEvent(MessageBoxEvent be) {
				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
//							    	  Info.display(elementAgentId, elementAgentId + "-" + agentId + "-" + agentName + "-" + sessionId);
							    	  handleUserActionEvent(new RemoveAgentFromSessionEvent(sessionId, agentName,agentId));
				    			  }
				    		  }
				    	  });
				    	  box.show();
				      }
				});
				
				panel.add(deleteButton, new RowData(0.25, 0.25, new Margins(0)));
				return panel;
			}

            @Override
            public Widget render(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
            	if (model instanceof TreeFolderChild){
                	//FATDebug.print(FATDebug.INFO, "Is TreeFolderChild!!!!");
                	return getDeleteActionButtonPanel(model, property,
                            config, rowIndex, colIndex, store, grid);
                }
                if (model instanceof TreeFolder){
                	//FATDebug.print(FATDebug.INFO, "Is TreeFolder!!!!");
                }
                return getAddActionButtonPanel(model, property,
                        config, rowIndex, colIndex, store, grid);
            }
        });
		
		agents2SessionTree.setBorders(true);
		agents2SessionTree.setAutoExpand(false);
		agents2SessionTree.setAutoExpandColumn(GridElementLabel.NAME);
		agents2SessionTree.setAutoExpandMax(1000);
		agents2SessionTree.setTrackMouseOver(true);
		agents2SessionTree.getTreeView().setRowHeight(28);
		agents2SessionTree.getStyle().setNodeCloseIcon(null);
		agents2SessionTree.getStyle().setNodeOpenIcon(null);
		agents2SessionTree.setStripeRows(true);
		
		TreeGridSelectionModel<ModelData> selectionModel = new TreeGridSelectionModel<ModelData>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setLocked(true);
		agents2SessionTree.setSelectionModel(selectionModel);//new TreeGridSelectionModel<ModelData>()

		panel.setHeading(FeedbackAuthoringStrings.HEADING_SESSIONS_AGENTS);
		panel.setScrollMode(Scroll.AUTOY);
		panel.add(agents2SessionTree);
		
		return panel;
	}
	
	
	public void addParent2SessionsPanel(String sessionId, String sessionName, ServiceStatus sessionStatus){
		addParent2SessionsUI(sessionId, sessionName, sessionStatus);
	}
	public void addChild2SessionsPanel(String sessionId, String sessionName, String agentId, ServiceStatus agentStatus){
		addChild2SessionsUI(sessionId, sessionName, agentId, agentStatus);
	}
	public void removeChildFromSessionsPanel(String sessionId, String agentId){
		removeChildFromSessionsUI(sessionId, agentId);
	}
	public void updateParentOnSessionsPanel(String sessionId, ServiceStatus oldStatus, ServiceStatus newStatus){
		updateParentAndChildStatusOnSessionsUI(sessionId, oldStatus, newStatus);
	}
	
	private void addParent2SessionsUI(String sessionId, String sessionName, ServiceStatus sessionStatus){
//		Map<String, String> mapOne = new HashMap<String, String>();
//		mapOne.put(GridElementLabel.NAME, "root");
//		mapOne.put(GridElementLabel.ID, "root");
//		TreeFolder model= new TreeFolder(mapOne);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.ID, sessionId);
		map.put(GridElementLabel.NAME, sessionName);
		map.put(GridElementLabel.STATUS, sessionStatus.toString());
		TreeFolder parent = new TreeFolder(map, new TreeFolderChild[]{});
//		model.add(parent);
//		sessionsTree.getTreeStore().add(model.getChildren(), false);
		sessionsTree.getTreeStore().add(parent, true);
	}
	private void addChild2SessionsUI(String sessionId, String sessionName, String agentId, ServiceStatus agentStatus){
		String agentName = getAgentName(agentId);
		
		ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(GridElementLabel.NAME, agentName);
		map.put(GridElementLabel.ID, agentId);
		map.put(GridElementLabel.STATUS, agentStatus.toString());
		map.put(GridElementLabel.SESSION, sessionId);
		TreeFolderChild child = new TreeFolderChild(map);
		
		sessionsTree.getTreeStore().add(parent, child, false);
		//sessionsTree.setExpanded(parent, true);
	}
	private void removeChildFromSessionsUI(String sessionId, String agentId){
		ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		
		Map<String, String> map = new HashMap<String, String>();
//		map.put(GridElementLabel.NAME, sessionName);
		map.put(GridElementLabel.SESSION, sessionId);
		map.put(GridElementLabel.ID, agentId);
		TreeFolderChild child = new TreeFolderChild(map);
		
		int index = sessionsTree.getTreeStore().getChildren(parent).indexOf(child);
		child = (TreeFolderChild) sessionsTree.getTreeStore().getChild(parent, index);
		sessionsTree.getTreeStore().remove(parent, child);
	}
	
	private void updateParentAndChildStatusOnSessionsUI(String sessionId, ServiceStatus oldStatus, ServiceStatus newStatus){
		ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		if(ServiceStatus.STOPPING == newStatus){
			parent.set(GridElementLabel.STATUS, ServiceStatus.READY_TO_START.toString());
		} else{
			parent.set(GridElementLabel.STATUS, newStatus.toString());
		}

		updateChildrenStatus(sessionId, oldStatus, newStatus);
	}
	
	private void updateChildrenStatus(String sessionId, ServiceStatus oldStatus, ServiceStatus newStatus){
		ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
		if(parent != null){
			List<ModelData>children = sessionsTree.getTreeStore().getChildren(parent);
			for(ModelData child:children){
				if(ServiceStatus.STOPPING == newStatus){
					child.set(GridElementLabel.STATUS, ServiceStatus.READY_TO_START.toString());
				} else{
					child.set(GridElementLabel.STATUS, newStatus.toString());
				}
			}
		} else{
			FATDebug.print(FATDebug.ERROR, "[FeedbackAuthoringToolTabContent][updateChildrenStatus] sessionId " 
											+ sessionId + " not found in TreeStore");
		}
	}
	
	/*
	 * removes the agent from the Sessions panel (top-right).
	 * the agent is not removed if it is assigned in the "Sessions->agents" panel 
	 */
	public boolean removeAllElemInstancesFromSessionsPanel(List<String> sessionList, String agentId){
		boolean retVal = false;

		for(String sessionId:sessionList){
			if(!isAgentInSessionDB(sessionId, agentId)){
				ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sessionId);
				
				Map<String, String> map = new HashMap<String, String>();

				map.put(GridElementLabel.ID, agentId);
				TreeFolderChild child = new TreeFolderChild(map);
				int index = sessionsTree.getTreeStore().getChildren(parent).indexOf(child);
				child = (TreeFolderChild) sessionsTree.getTreeStore().getChild(parent, index);
				sessionsTree.getTreeStore().remove(parent, child);
				retVal = true;
			}
		}
		return retVal;
	}
	
	//public void deleteAgentFromSessionsTree(AgentFA agent, String session){
//	
//	ModelData parent = sessionsTree.getTreeStore().findModel("name", session);
//	List<ModelData> children = sessionsTree.getTreeStore().getChildren(parent);
//	ModelData child = null;
//	for(int i=0; i < children.size(); i++){
//		String agName = children.get(i).get("name"); 
//		if(agName.equals(agent.getAgentName())){
//			child = children.get(i);
//			break;
//		}
//	}
//	sessionsTree.getTreeStore().remove(parent, child);
//	sessionsTree.getView().refresh(false);
//}
	
	
	private void refreshViewSessionsUI(){
		if(sessionsTree.isRendered()){
			sessionsTree.getView().refresh(false);
			sessionsTree.repaint();
		}
	}
	
	private ContentPanel createSessionsPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());

		final ColumnConfig nameCol = new ColumnConfig(GridElementLabel.NAME, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.SESSION_LABEL + "</div>", 280);
		final ColumnConfig statusCol = new ColumnConfig(GridElementLabel.STATUS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.STATUS_LABEL + "</div>", 160);
		ColumnConfig actionsCol = new ColumnConfig(GridElementLabel.ACTIONS, "<div style=\"color:#000000;\">" + FeedbackAuthoringStrings.ACTIONS_LABEL + "</div>", 80);
		
		statusCol.setStyle("color:black;");
		
		ColumnModel treeCM = new ColumnModel(Arrays.asList(nameCol, statusCol, actionsCol));
		
		nameCol.setRenderer(new TreeGridCellRenderer<ModelData>());
		actionsCol.setRenderer(new GridCellRenderer<ModelData>() {
			
			@Override
            public Widget render(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
            	
                if (model instanceof TreeFolderChild){
                	return getEmptyActionButtonsPanel();
                } else{ //(model instanceof TreeFolder)
                	return getStartStopButtonPanel(model, property, config, rowIndex, colIndex, store, grid);
                }

            }
			
			private ContentPanel getEmptyActionButtonsPanel(){
				ContentPanel panel = new ContentPanel();
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setBodyStyle("backgroundColor: transparent;");
				return panel;
			}
			private ContentPanel getStartStopButtonPanel(final ModelData model, String property,
                    ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				String status = (String) model.get(statusCol.getId());
            	boolean isActive = ServiceStatus.isActive(ServiceStatus.valueOf(status));
				
				Button playButton = new Button();
				playButton.setStyleName("xfa-btn");
				playButton.setStyleName("x-btn-text", true);
				playButton.setStyleName("x-btn", true);
				playButton.setStylePrimaryName("xfa-btn");
				playButton.setIconStyle("icon-play");
				playButton.setWidth(16);
				playButton.setHeight(16);
				playButton.setToolTip(FeedbackAuthoringStrings.START_SESSION_LABEL);
				playButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String sessionIdElement = (String) model.get(GridElementLabel.ID);
				    	  //Info.display(sessionIdElement, sessionIdElement);
				    	  handleUserActionEvent(new StartSessionEvent(sessionIdElement, null, null));
				      }
				});
				panel.add(playButton, new RowData(0.25, 1.0, new Margins(0)));
				
				Button stopButton = new Button();
				stopButton.setStyleName("xfa-btn");
				stopButton.setStyleName("x-btn-text", true);
				stopButton.setStyleName("x-btn", true);
				stopButton.setStylePrimaryName("xfa-btn");
				stopButton.setIconStyle("icon-stop");
				stopButton.setWidth(16);
				stopButton.setHeight(16);
				stopButton.setToolTip(FeedbackAuthoringStrings.STOP_SESSION_LABEL);
				stopButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String sessionIdElement = (String) model.get(GridElementLabel.ID);
				    	  //Info.display(sessionIdElement, sessionIdElement);
				    	  handleUserActionEvent(new StopSessionEvent(sessionIdElement, null, null));
				      }
				});
				panel.add(stopButton, new RowData(0.25, 0.25, new Margins(0)));
				/*
				 * OliverS told me that STOPPING should be considered as STOPPED,
				 */
				if(!isActive || ServiceStatus.STOPPING.toString().equals(status)){
					stopButton.setVisible(false);
					playButton.setVisible(true);
					if(ServiceStatus.STOPPING.toString().equals(status)){
						model.set(GridElementLabel.STATUS, ServiceStatus.READY_TO_START.toString());
					}
				}else{
					stopButton.setVisible(true);
					playButton.setVisible(false);
				}
				return panel;
			}
        });
		
		Map<String, String> mapOne = new HashMap<String, String>();
		mapOne.put(GridElementLabel.NAME, "root");
		TreeFolder model= new TreeFolder(mapOne);

		TreeStore<ModelData> sessionsTreeStore = new TreeStore<ModelData>();
		sessionsTreeStore.add(model.getChildren(), true);
		sessionsTree = new TreeGrid<ModelData>(sessionsTreeStore, treeCM);
		sessionsTree.setBorders(true);
		sessionsTree.setAutoExpand(false);
		sessionsTree.setAutoExpandColumn(GridElementLabel.NAME);
		sessionsTree.setAutoExpandMax(1000);
		sessionsTree.setTrackMouseOver(true);
		sessionsTree.getStyle().setNodeCloseIcon(null);
		sessionsTree.getStyle().setNodeOpenIcon(null);
		sessionsTree.setStripeRows(true);
//		sessionsTree.setLazyRowRender(0);
		
		CustomizedTreeGridView treeGridView = new CustomizedTreeGridView();
		sessionsTree.setView(treeGridView);
		
		TreeGridSelectionModel<ModelData> selectionModel = new TreeGridSelectionModel<ModelData>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setLocked(true);
		sessionsTree.setSelectionModel(selectionModel);

		panel.setHeading(FeedbackAuthoringStrings.HEADING_SESSIONS);
		panel.setScrollMode(Scroll.AUTOY);
		panel.add(sessionsTree);
		
		return panel;
	}
	
	
	public String getAgentName(String agentId){
		if(getAgentFromDB(agentId) != null){
			return getAgentFromDB(agentId).getDisplayName();
		}
		return null;
	}
	/*
	 * DB methods wrappers
	 */
	public void addOntologyDB(String ontology){
		db.addOntology(new OntologyFA(ontology));
	}
	public void resetOntologies(){
		db.resetOntologies();
	}
	public Set<String> getOntologiesDB(){
		return db.getOntologies();
	}
	public OntologyFA getOntologyDB(String ontology){
		return db.getOntology(ontology);
	}
	public boolean updateOntologyXMLDB(String ontologyName, String ontologyXML){
		return db.updateOntologyXML(ontologyName, ontologyXML);
	}
	
	public AgentDescriptionFE getAgentFromDB(String agentId){
		return db.getAgent(agentId);
	}
	public boolean existAgentByNameDB(String agentName){
		return db.existAgentByName(agentName);
	}
	public void deleteAgentFromDB(String agentId){
		db.deleteAgent(agentId);
	}
	public void addAgent2DB(AgentDescriptionFE agent){
		db.addAgent(agent);
	}
	public void resetAgentsDB(){
		db.resetAgents();
	}
	public List<String> getOnt4AgentFromDB(String agentId){
		return db.getOnt4Ag(agentId);
	}
	public List<String> getSes4AgentFromDB(String agentId){
		return db.getSes4Ag(agentId);
	}
//	public void deleteAllAgentsFromSessionDB(String session){
//		db.deleteAllAgentsFromSession(session);
//	}
	public List<String> getSessionsWithOntologyFromDB(String ontology){
		return db.getSessionsWithOntology(ontology);
	}
//	public void addAgent2OntologyDB(String ontology, String agentId){
//		db.addAgent2Ontology(agentId, ontology);
//	}
	public void deleteAgentFromOntologyDB(String ontology, String agentId){
		db.deleteAgentFromOntology(agentId, ontology);
	}
//	public void deleteAllAgentsFromOntologyDB(String ontology){
//		db.deleteAllAgentsFromOntology(ontology);
//	}
	public boolean isAgentInOntologyDB(String ontology, String agentId){ 
		return db.isAgentInOntology(ontology, agentId);
	}
	
//	public void addAgent2SessionDB(String session, String agentId){
//		db.addAgent2Session(agentId, session);
//	}
	public boolean isAgentInSessionDB(String sessionId, String agentId){ 
		return db.isAgentInSession(sessionId, agentId);
	}
	public void deleteAgentFromSessionDB(String sessionId, String agentId){
		db.deleteAgentFromSession(agentId, sessionId);
	}
	public void addSessionDB(SessionFA session){
		db.addSession(session);
	}
	public void addSessions2DB(Map<String, String> id2OntMap, Map<String, String> id2NameMap){
		Set<String> ids = id2OntMap.keySet();
		for(String sessionId: ids){
			String sessionOnt = id2OntMap.get(sessionId);
			String sessionName = id2NameMap.get(sessionId);
			SessionFA sessionFA = new SessionFA(sessionId, sessionName, sessionOnt, ""); 
			db.addSession(sessionFA);
		}
	}
	public void resetSessionDB(){
		db.resetSessions();
	}
//	public Set<String> getSessionsDB(){
//		return db.getSessions();
//	}
	public String getSessionNameDB(String sessionId){
		return db.getSessionName(sessionId);
	}
	public String getSessionIdDB(String sessionName){
		return db.getSessionId(sessionName);
	}
	public SessionFA getSessionDB(String sessionId){
		return db.getSession(sessionId);
	}
	
	/*
	 * Methods to handle incoming messages from Feedback Engine
	 */
	public void handleAddOrUpdateAgent(AgentDescriptionFE agent){
		FATDebug.print(FATDebug.DEBUG, "[FeedbackAuthoringTabContent][handleAddOrUpdateAgent] ");
		if(!db.existAgent(agent.getAgentID())){
			//update
			//add to grid
			addAgent2UI(agent);
			//add to local DB
			db.addAgent(agent);
			
			//Get ontology details, i.e. XML, if not available yet
	    	SupportedOntologiesDef supOnt = agent.getSupportedOntology();
	    	if (supOnt != null && supOnt.getSupportedOntologies().size() >=1){
	    		String ontology = supOnt.getSupportedOntologies().get(0);
	    		if(getOntologyDB(ontology).getXml() == null){
	    			getOntologyDetails(ontology);
	    		}
	    	}
	    	//Add to PatternServerManager
	    	FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addAgent(agent.getAgentID());
	    	for(AnalysisType pattern:agent.getConfData().getAnalysisTypes()){
	    		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(agent.getAgentID(), pattern.getServiceID().getTypeID()); 
	    	}
	    	ToasterMessage.log(FeedbackAuthoringStrings.AGENT_LABEL + " "  + agent.getDisplayName() 
	    					+ " " + FeedbackAuthoringStrings.AGENT_ADDED_SUCCESS_LABEL);
		}else {
			//update grid
			updateAgent2UI(agent);
			//update DB
			db.updateAgent(agent);
			/*
			 * TODO what should I do if the ontology has been changed.
			 * invalidate/delete all things related to the former ontology
			 */
			//Call to get ontology
	    	SupportedOntologiesDef supOnt = agent.getSupportedOntology();
	    	if (supOnt != null && supOnt.getSupportedOntologies().size() >=1){
	    		String ontology = supOnt.getSupportedOntologies().get(0);
	    		if(getOntologyDB(ontology).getXml() == null){
	    			getOntologyDetails(ontology);
	    		}
	    	}
		}
    	agentsPanel.refreshView();
    	ToasterMessage.log(FeedbackAuthoringStrings.AGENT_LABEL + " "  + agent.getDisplayName() 
				+ " " + FeedbackAuthoringStrings.AGENT_UPDATED_SUCCESS_LABEL);
	}
	public void handleDeleteAgent(String agentId){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleDeleteAgent] ");
		//remove from agents grid
		deleteAgentFromUI(agentId);
		//remove from DB
		db.deleteAgent(agentId);
		agentsPanel.refreshView();
		//remove from PatternServerManager
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().deleteAgent(agentId);
		/*
		 * IMPORTANT!
		 * the agent will be removed from Ontologies -> Agents, 
		 * Sessions -> Agents and Sessions Panel once
		 * the ActionPackage with Command AgentMappingsDeleted is received
		 */
		ToasterMessage.log(FeedbackAuthoringStrings.AGENT_LABEL + " "  + getAgentName(agentId)
				+ " " + FeedbackAuthoringStrings.AGENT_DELETED_SUCCESS_LABEL);
	}
	
	
	public void handleListOntologies(List<String> ontologyList){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleListOntologies] ");
		addOntologies2OntPanel(ontologyList);
		refreshViewAgent2OntologyUI();
	}
	public void handleGetOntologyDetails(String ontology, String ontologyXML){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleGetOntologyDetails] " + ontology + " received. ontologyXML==Null->" + (ontologyXML==null?true:false));
		updateOntologyXMLDB(ontology, ontologyXML);
	}
	public void handleListMap(Map<String, String> id2OntMap, Map<String, String> id2NameMap){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleListMap] ");
		addSessions2Agent2SesPanel(id2OntMap, id2NameMap);
		refreshViewAgent2SesPanelUI();
	}
	public void handleListAgentsInfo(List<AgentDescriptionFE> agentList){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleListAgentsInfo] ");
		//Clean previous data
    	agentsPanel.resetGrid();
    	resetAgentsDB();
    	//TODO check if this is OK.
    	FeedbackAuthoringTabContent.getInstance().getPatternServerManager().deleteAllAgents();
    	
    	//Add new data
    	for(AgentDescriptionFE agent: agentList){
//    		//This code updates the agents and patterns required in PatternServerManager
//    		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addAgent(agent.getAgentID());
//    		for(AnalysisType pattern:agent.getConfData().getAnalysisTypes()){
//    			FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(agent.getAgentID(), pattern.getServiceID().getTypeID()); 
//    		}
    		updatePatternServerManagerWithAgent(agent);
    		
    		addAgent2UI(agent);
    		db.addAgent(agent);
    		
    		SupportedOntologiesDef supOnt = agent.getSupportedOntology();
    		if (supOnt != null && supOnt.getSupportedOntologies().size() >=1){
    			String ontology = supOnt.getSupportedOntologies().get(0);
    			if(getOntologyDB(ontology) == null){
    				FATDebug.print(FATDebug.ERROR,"[FeedbackAuthoringTabContent][handleListAgentsInfo] " + ontology + " was not found");
    			} else{
    				if(getOntologyDB(ontology).getXml() == null){
        				getOntologyDetails(ontology);
        			}
    			}
    		}
        }
    	agentsPanel.refreshView();
	}
	
	/*
	 * This code updates the PatternServerManager with all the info from the agent, i.e. patterns, pattern elements
	 */
	private void updatePatternServerManagerWithAgent(AgentDescriptionFE agent){
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addAgentDescription(agent);
	}
	
	public void handleMapdetails(){
		FATDebug.print(FATDebug.DEBUG,"[FeedbackAuthoringTabContent][handleMapdetails] ");
	}

	/*
	 * called before the agent configuration window open.
	 */
	public void updatePatternManipulatorMap(AgentDescriptionFE agent){
//		String agentId = agent.getAgentID();
		Collection<AnalysisType> patternList = agent.getConfData().getAnalysisTypes();
		for(AnalysisType pattern:patternList){
//			String patternId = pattern.getServiceID().getTypeID();
			// only for Structural pattern
			if(pattern instanceof StructureAnalysisType){
				addPatternManipulator((StructureAnalysisType)pattern);
			}
		}
	}
	/*
	 * To be call when a new structural pattern is created or
	 * when the agent window is open
	 */
	public void addPatternManipulator(StructureAnalysisType pattern){
		Ontology ontology = new Ontology(pattern.getOntologyID());
		String xml = FeedbackAuthoringTabContent.getInstance().getOntologyDB(pattern.getOntologyID()).getXml();
		if(xml != null){
			OntologyXML2ObjReader.parseOntology(ontology, xml);
			StructuralAnalysisTypeManipulator patManipulator = new StructuralAnalysisTypeManipulator(pattern, ontology);
			getPattternManipulatorMap().put(pattern.getServiceID(), patManipulator);
		}
	}
	
	public void handleAddAgentToOntology(String agentId, String ontology){
		addElem2Agent2OntPanel(ontology, agentId);
		refreshViewAgent2OntologyUI();
		
		//add agent to all sessions with the given ontology in sessions panel
		boolean flag = false;
		List<String> sessionList =  getSessionsWithOntologyFromDB(ontology);
		for(String sessionId:sessionList){
			if(!isAgentInSessionDB(sessionId, agentId)){
				//the agent is not assigned in sessions panel
				String sessionName = getSessionNameDB(sessionId);
				addChild2SessionsPanel(sessionId, sessionName, agentId, ServiceStatus.READY_TO_START);
				flag = true;
			}
		}
		if (flag){
			refreshViewSessionsUI();
		}
		
	}
	public void handleRemoveAgentFromOntology(String agentId, String ontology){
		deleteElemFromAgent2OntPanel(ontology, agentId);
		refreshViewAgent2OntologyUI();
		
		//delete from all sessions with the given ontology in sessions panel
		//but not if the agent is assigned in session panel
		List<String> sessionList =  getSessionsWithOntologyFromDB(ontology);
		if (removeAllElemInstancesFromSessionsPanel(sessionList, agentId)){
			refreshViewSessionsUI();
		}
	}
	public void handleListAgentsToOntologies(Map<String, List<String>> map){
		addElems2Agent2OntPanel(map);
		refreshViewAgent2OntologyUI();
	}
	public void handleAddAgentToSession(String agentId, String sessionId){
		String sessionName = getSessionNameDB(sessionId);
		addElem2Agent2SesPanel(sessionId, sessionName, agentId);
		refreshViewAgent2SesPanelUI();
		
		//add agent to session in sessions panel
		SessionFA sessionFA = getSessionDB(sessionId);
		if(!isAgentInOntologyDB(sessionFA.getOntology(), agentId)){
			//the agent is not assigned in sessions panel
			addChild2SessionsPanel(sessionId, sessionName, agentId, ServiceStatus.READY_TO_START);
			refreshViewSessionsUI();
		}
	}
	public void handleRemoveAgentFromSession(String agentId, String sessionId){
		//String sessionName = getSessionNameDB(sessionId);
		deleteElemFromAgent2SesPanel(sessionId, agentId);
		refreshViewAgent2SesPanelUI();
		
		//delete agent from session in sessions panel
		//but not if the agent is assigned in ontologies->agents panel
		SessionFA sessionFA = getSessionDB(sessionId);
		if(!isAgentInOntologyDB(sessionFA.getOntology(), agentId)){
			//the agent is not assigned in ontology panel
			removeChildFromSessionsUI(sessionId, agentId);
			refreshViewSessionsUI();
		}
	}
	public void handleListAgentsToSessions(Map<String, List<String>> map){
		addElems2Agent2SesPanel(map);
		refreshViewAgent2SesPanelUI();
	}
	public void handleListSessionStatus(Map<String, ServiceStatus> sessionID2Status, Map<String, Map<String, ServiceStatus>> sessionID2agent2Status){
		//this relation is not kept in an specific structure
		//but can be inferred from current structures
		
		//Clean previous data
		//here we remove the agents, the sessions are kept
		for(String sesId : db.getSessions()){
			ModelData parent = sessionsTree.getTreeStore().findModel(GridElementLabel.ID, sesId);
			sessionsTree.getTreeStore().removeAll(parent);
		}
		
		Set<String> seslist = sessionID2Status.keySet();
        for(String sessionId: seslist){
        	ServiceStatus sessionStatus = sessionID2Status.get(sessionId);
        	String sessionName = getSessionNameDB(sessionId);
        	addParent2SessionsPanel(sessionId, sessionName, sessionStatus);
        	
        	Map<String, ServiceStatus> agent2StatusMap = sessionID2agent2Status.get(sessionId);
        	if(agent2StatusMap != null){
        		Set<String> agentList = agent2StatusMap.keySet();
            	for(String agentId : agentList){
            		ServiceStatus agentStatus = agent2StatusMap.get(agentId);
            		addChild2SessionsPanel(sessionId, sessionName, agentId, agentStatus);
            	}
        	}
        	
        }
        refreshViewSessionsUI();
	}
	public void handleCompileAgent(String agentId){
		
	}
	public void handleAgentMappingsDeleted(String agentId, List<String> ontologyList, List<String> sessionIdList){
		//TODO check if this works
		
		if(ontologyList != null){
			//remove agent from ontology panel
			for(String ontology:ontologyList){
				deleteElemFromAgent2OntPanel(ontology, agentId);
			}
			refreshViewAgent2OntologyUI();
		}
		if(sessionIdList != null){
			for(String sessionId:sessionIdList){
				//remove agent from session panel
				deleteElemFromAgent2SesPanel(sessionId, agentId);
				refreshViewAgent2SesPanelUI();
			}
			//remove agent from sessions panel
			if (removeAllElemInstancesFromSessionsPanel(sessionIdList, agentId)){
				refreshViewSessionsUI();
			}
		}
	}
	
	public void handleComponentRuntimeStatusChanged(String componentId, ServiceStatus oldStatus, ServiceStatus newStatus){
		//TODO Incomplete
	}
	public void handleSessionRuntimeStatusChanged(String sessionId, ServiceStatus oldStatus, ServiceStatus newStatus){
		//String sessionName = getSessionNameDB(sessionId);
		updateParentOnSessionsPanel(sessionId, oldStatus, newStatus);
		refreshViewSessionsUI();
	}
	public void handleGetFreshAgentId(String requestId, String agentId){
		//TODO add user as parameter to get direct response 
		if(freshIdRequestQueue.containsKey(Integer.valueOf(requestId))){
			AgentRequestInfo agentRequestInfo = (AgentRequestInfo) freshIdRequestQueue.remove(Integer.valueOf(requestId));
			
			if(agentRequestInfo.getOntology() != null){ //add new agent
				AgentDescriptionFE agent = new AgentDescriptionFE();
				agent.setConfData(new ActionAgentConfigData(null));
				agent.setAgentID(agentId);
				agent.setDisplayName(agentRequestInfo.getAgentName());
				ActionAgentConfigData conf = new ActionAgentConfigData();
				conf.setAgentID(agent.getAgentID());
				agent.setConfData(conf);
				List<String> ontList =  new Vector<String>();
				ontList.add(agentRequestInfo.getOntology());
				SupportedOntologiesDef supportedOnt = new SupportedOntologiesDef(ontList);
				agent.setSupportedOntology(supportedOnt);
				conf.setSupportedOntologiesDef(supportedOnt);
				conf.setPhaseModelerDef(new PhaseModelerDef_Empirical(new PhasesDef(), new ActionListDef(true)));
				
				agent.setConfReadable(true);
				agent.setConfWritable(true);
				handleUserActionEvent(new AddUpdateAgentEvent(agent.getAgentID(), agent));
			} else{ //duplicate agent
				AgentDescriptionFE duplicatedAgent = CloneAgentDescriptionFE.doCloning(agentId, 
						agentRequestInfo.getAgentName(), getAgentFromDB(agentRequestInfo.getOldAgentId()));
				handleUserActionEvent(new AddUpdateAgentEvent(duplicatedAgent.getAgentID(), duplicatedAgent));
			}
		}
		
		
	}
	public void handleGetFreshPatternId(String requestId, String agentId, String patternId, String serviceClassStr){
		//TODO add user as parameter to get direct response 
		if(freshIdRequestQueue.containsKey(Integer.valueOf(requestId))){
			//ServiceClass serviceClass = ServiceClass.valueOf(serviceClassStr);
			
			PatternRequestInfo patternRequestInfo = (PatternRequestInfo) freshIdRequestQueue.remove(Integer.valueOf(requestId));
			PatternsGrid patternsGrid = patternRequestInfo.getPatternsGrid();
			patternsGrid.addPattern(agentId, patternId, patternRequestInfo.getPatternName(), patternRequestInfo.getPatternType());
		}
		
	}
	
	public void handleInfoMsg(String text, String agentId, String patternId){
		
	}
	
	
	
	/*
	 * Sends a request for a new agent id
	 * @param agentName entered by user, or auto-generated in case of duplicate agent action
	 * @param ontology entered by user or null if we are requesting a new id for a duplicate agent action
	 */
	public void doGetFreshAgentId(String agentName, String ontology, String oldAgentId){
		int requestId = IdGenerator.getNewRequestId();
		AgentRequestInfo agentRequestInfo = new AgentRequestInfo(requestId, agentName, ontology);
		if(ontology == null){
			agentRequestInfo.setOldAgentId(oldAgentId);
		}
		communicator.sendActionPackage(actionBuilder.getFreshAgentIdFA(String.valueOf(requestId)));
		
		freshIdRequestQueue.add(requestId, agentRequestInfo);
	}
	/*
	 * Sends a request for a new pattern id
	 */
	public void doGetFreshPatternId(String patternName, String patternType, String agentId, PatternsGrid patternsGrid){
		int requestId = IdGenerator.getNewRequestId();
		PatternRequestInfo agentRequestInfo = new PatternRequestInfo(requestId, agentId, patternName, patternType, patternsGrid);
		communicator.sendActionPackage(actionBuilder.getFreshPatternIdFA(String.valueOf(requestId), agentId));
		
		freshIdRequestQueue.add(requestId, agentRequestInfo);
	}
	
	/*
	 * Handles user action events(UserActionEvent) on GUI
	 */
	public void handleUserActionEvent(UserActionEventImpl event){
		if (event instanceof AgentUserActionEventImpl){
			if (event instanceof AddUpdateAgentEvent){
				AddUpdateAgentEvent ev = (AddUpdateAgentEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				AgentDescriptionFE agentDesc = ev.getAgentDesc();
				communicator.sendActionPackage(actionBuilder.getAddOrUpdateAgentFA(agentId, agentDesc));
				
			}else if (event instanceof DeleteAgentEvent){
				DeleteAgentEvent ev = (DeleteAgentEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				communicator.sendActionPackage(actionBuilder.getDeleteAgentFA(agentId));
				
			}else if (event instanceof CompileAgentEvent){
				CompileAgentEvent ev = (CompileAgentEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				AgentDescriptionFE agentDesc = ev.getAgentDesc();
				String errorMsg = ValidateAgentDescription.validateConf(agentDesc);
				if(!errorMsg.equals("")){
					MessageBox box = new MessageBox();
					box.setButtons(MessageBox.OK);
					box.setIcon(MessageBox.ERROR);
					box.setTitle(FeedbackAuthoringStrings.ERROR_LABEL);
					box.setMessage(FeedbackAuthoringStrings.ERROR_AGENT_CANNOT_BE_MARKED_READY 
							+ errorMsg.replaceAll(ValidateAgentDescription.BREAK_LINE, ValidateAgentDescription.BREAK_LINE_HTML));
				  	box.show();
				} else{
					communicator.sendActionPackage(actionBuilder.getCompileAgentFA(agentId, agentDesc));
				}
				
			}else if (event instanceof CopyAgentEvent){
				CopyAgentEvent ev = (CopyAgentEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				AgentDescriptionFE agentDesc = getAgentFromDB(agentId);
				String newName = CloneAgentDescriptionFE.getNewName(agentDesc.getDisplayName());
				doGetFreshAgentId(newName, null, agentId);
				
//				AgentDescriptionFE oldagentDesc = getAgentFromDB(agentId);
//				AgentDescriptionFE newAgentDesc = CopyAgentDescriptionFE.doCopy(oldagentDesc);
//				String newAgentId = IdGenerator.getNewAgentId();
//				newAgentDesc.setAgentID(newAgentId);
//				communicator.sendActionPackage(actionBuilder.getAddOrUpdateAgentFA(agentId, newAgentDesc));
			}
//			else if (event instanceof ViewAgentEvent){
//				ViewAgentEvent ev = (ViewAgentEvent)event;
//				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
//				AgentDescriptionFE agentDesc = getAgentFromDB(agentId);
//				AgentWindow agentAddWin = new AgentWindow(getAgWinConf4ViewAg(), agentDesc, this.agentsPanel);
//				agentAddWin.show();
//				
//			}
		}else if (event instanceof OntologyUserActionEventImpl){
			if (event instanceof AddAgentToOntologyEvent){
				AddAgentToOntologyEvent ev = (AddAgentToOntologyEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				String agentName = ev.getProperty(ActionUserEventProperty.agentName);
				String ontology = ev.getProperty(ActionUserEventProperty.ontology);
				communicator.sendActionPackage(actionBuilder.getAddAgentToOntologyFA(agentId, agentName, ontology));
			}else if (event instanceof RemoveAgentFromOntologyEvent){
				RemoveAgentFromOntologyEvent ev = (RemoveAgentFromOntologyEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				String agentName = ev.getProperty(ActionUserEventProperty.agentName);
				String ontology = ev.getProperty(ActionUserEventProperty.ontology);
				communicator.sendActionPackage(actionBuilder.getRemoveAgentFromOntologyFA(agentId, agentName, ontology));
			}
		}else if (event instanceof SessionUserActionEventImpl){
			if (event instanceof AddAgentToSessionEvent){
				AddAgentToSessionEvent ev = (AddAgentToSessionEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				String agentName = ev.getProperty(ActionUserEventProperty.agentName);
				String sessionId = ev.getProperty(ActionUserEventProperty.session);
				communicator.sendActionPackage(actionBuilder.getAddAgentToSessionFA(agentId, agentName, sessionId));
			}else if (event instanceof RemoveAgentFromSessionEvent){
				RemoveAgentFromSessionEvent ev = (RemoveAgentFromSessionEvent)event;
				String agentId = ev.getProperty(ActionUserEventProperty.agentId);
				String agentName = ev.getProperty(ActionUserEventProperty.agentName);
				String sessionId = ev.getProperty(ActionUserEventProperty.session);
				communicator.sendActionPackage(actionBuilder.getRemoveAgentFromSessionFA(agentId, agentName, sessionId));
			}
		}else if (event instanceof SessionsUserActionEventImpl){
			if (event instanceof StartSessionEvent){
				StartSessionEvent ev = (StartSessionEvent)event;
				String sessionID = ev.getProperty(ActionUserEventProperty.session);
				//String sessionID = getSessionIdDB(session);
				communicator.sendActionPackage(actionBuilder.getStartSessionFA(sessionID));
			}else if (event instanceof StopSessionEvent){
				StopSessionEvent ev = (StopSessionEvent)event;
				String sessionID = ev.getProperty(ActionUserEventProperty.session);
				//String sessionID = getSessionIdDB(session);
				communicator.sendActionPackage(actionBuilder.getStopSessionFA(sessionID));
			}
		}else{
			Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent]"
					+ "[handleUserActionEvent]"
					+ "Invalid event", Logger.DEBUG_ERRORS);
		}
	}
	
	public PatternServerManager getPatternServerManager(){
		return patternServerManager;
	}

	public Map<ServiceID, StructuralAnalysisTypeManipulator> getPattternManipulatorMap() {
		return pattternManipulatorMap;
	}
	public StructuralAnalysisTypeManipulator getPattternManipulator(ServiceID pattern) {
		return pattternManipulatorMap.get(pattern);
	}
	public void deletePattternManipulator(ServiceID pattern) {
		pattternManipulatorMap.remove(pattern);
	}
	public void addPattternManipulator(ServiceID pattern, StructuralAnalysisTypeManipulator manipulator) {
		pattternManipulatorMap.put(pattern, manipulator);
	}

	public Vector<String> getInitMsgsTracker() {
		return initMsgsTracker;
	}

	public HashMap<String, Action> getInitMsgsQueueVal() {
		return initMsgsQueueVal;
	}

	public boolean isInitMsgs() {
		return initMsgs;
	}

	public void setInitMsgs(boolean initMsgs) {
		synchronized (initMsgsLock) {
			this.initMsgs = initMsgs;
		}
	}

	public HashMap<String, Boolean> getInitMsgsQueueFlags() {
		return initMsgsQueueFlags;
	}
	
	
	
	

//	public void handleDeleteAgentEvent(String agent){
//		communicator.sendActionPackage(actionBuilder.getDeleteAgentFA(agent));
//	}
	
//	public static AgentInfo convertFromAgentDescFE2AgentInfo(AgentDescriptionFE agent){
//		AgentInfo agentTmp = new AgentInfo();
//		agentTmp.setAgentId(agent.getAgentID()); 
//		agentTmp.setAgentName(agent.getDisplayName());
//		if(agent.getSupportedOntology().isAllOntologies()){
//			
//		}else{
//			agentTmp.setOntology(agent.getSupportedOntology().getSupportedOntologies().get(0));
//		}
//		agentTmp.setAgentDescription(agent.getDescription());
//		
//		agentTmp.setConfReadable(agent.isConfReadable());
//		agentTmp.setConfWritable(agent.isConfWritable());
//		agentTmp.setConfigCompleted(agent.isConfigCompleted());
//		
//		return agentTmp;
//	}

}
