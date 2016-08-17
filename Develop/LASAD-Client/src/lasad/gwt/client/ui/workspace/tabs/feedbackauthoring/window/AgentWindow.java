package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.pattern.PatternGraphMapInfo;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.AgentsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.ColumnConf;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.ControlThreadGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.FeedbackGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.GridConf;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PatternsGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PhasePriorityGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PhasesGrid;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternDrawingAreaMVCViewSession;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CounterCriterionOperatorTranslator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CustomDualListFieldEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CustomDualListFieldEventType;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.CustomDualListFieldListener;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.Data2ModelConverter;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ToasterMessage;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.OntologyXML2ObjReader;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.validation.ValidateAgentDescription;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.CustomDualListField;
import lasad.gwt.client.xml.OntologyReader;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_Highlighting;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_LongText;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_ShortText;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTime;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTimeSetting;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_User;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_UserSetting;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterCriterionDef;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterDef;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeGeneral;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Object;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Pattern;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_OnRequest;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_Periodically;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Group;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Individuals;
import lasad.shared.dfki.meta.agents.provision.priority.MsgFilterDef;
import lasad.shared.dfki.meta.agents.provision.priority.MsgFilterDef_DiscardAllButOneInstancePerGroup;
import lasad.shared.dfki.meta.agents.provision.priority.MsgFilterDef_DiscardAllButOneInstancePerType;
import lasad.shared.dfki.meta.agents.provision.priority.MsgFilterDef_DiscardInstancesAlreadyPointedTo;
import lasad.shared.dfki.meta.agents.provision.priority.MsgSortCriterion;
import lasad.shared.dfki.meta.agents.provision.priority.MsgSortCriterionUtil;
import lasad.shared.dfki.meta.agents.provision.priority.PriorityProvisionType;
import lasad.shared.dfki.meta.ontology.Ontology;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

/* If using method test, uncomment these imports
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ComparisonUtil;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2SetOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2StringOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2VarStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;
import java.util.Set;
*/

/**
 * Agent window, can be displayed in EDIT_MODE or VIEW_MODE
 * @author Anahuac
 *
 */

public class AgentWindow{
	//public static final int ADD_MODE = 1;
	public static final int EDIT_MODE = 2;
	public static final int VIEW_MODE = 3;
	
//	private DatabaseFA db;
	private final Window window = new Window();
	
	private FormData formData = new FormData("-20"); //100%
	
	private AgentWindowConf awc;
	//private AgentDescriptionFE agentInfo;
	private AgentDescriptionFE newAgentInfo;

	private AgentsGrid agGrid;
	
	/*
	 * Status Panel
	 */
	TextArea statusArea;
	/*
	 * General Agent Settings
	 */
	//Basic tab
	private TextField<String> agentIdField;
	private TextField<String> agentNameField;
	private ComboBox<ElementModel> supportedOntCombo;
	private TextArea agentDescriptionTA;
	//Phases tab
	private PhasesGrid phasesGrid;
	/*
	 * Patterns
	 */
	private PatternsGrid patternsGrid;
	//basic frame
	private ContentPanel patternsBasicPanel;
	private TextField<String> patternIdField;
	private TextField<String> patternNameField;
	private TextArea patternDescription;
	//Filter
	private ContentPanel patternsFilterPanel;
//	private RadioGroup userRestFeedFilterRG;
	private Label userRestFeedFilterNoteLabel;
//	private RadioGroup timeRestFeedFilterRG;
	private NumberField timeRestSecsOp2FeedFilterField;
	private NumberField timeRestSecsOp3FeedFilterField;

	private ContentPanel patternsBottomPanel;
	private ContentPanel patternsDefinitionPanel;
	//structure (workspace)
	//counter
	private CheckBox onlySpecificTypesCB;
	private RadioGroup instTypeRestRG;
	private TextField<String> specificTypesField;
	private ComboBox<ElementModel> countCondCombo;
	private NumberField countValueField;
	
	/*
	 * Feedback
	 */
	private ContentPanel feedbackBottomPanel;
	private ContentPanel feedbackBasicPanel;
	private ContentPanel feedbackContentPanel;
	private ContentPanel feedbackPriorityPanel;
	private FeedbackGrid feedbackGrid;
	private PhasePriorityGrid phasePriorityGrid;
	//Basic
	private TextField<String> feedbackIdField;
	private TextField<String> feedbackNameField;
	private TextArea feedbackDescriptionTA;
	private ComboBox<ElementModel> feedbackTriggerByCombo = new ComboBox<ElementModel>();
	private Label feedbackBasicPatternDescLabel;
	private Label feedbackBasicNoteLabel;
	//Content
	private TextField<String> shortMsgContField;
	private TextArea longMsgContTA;
	private RadioGroup highlightingContRG;
	//Priority
	private SliderField prioritySF = null;
//	private ComboBox<ElementModel> phaseIdCombo;
//	private SliderField phasePrioritySF = null;
	
	/*
	 * Strategy
	 */
	//Basic
	private TextField<String> strategyIdField;
	private TextField<String> strategyNameField;
	private TextArea strategyDescriptionTA;
//	private RadioGroup controlThreadProvTimeRG;
	private TextField<String> strategyProvTimeDispNameField;
	private NumberField strategyProvTimeInterField;
	private ContentPanel strategyBottomPanel;
	private ContentPanel strategyBasicPanel;
	private ContentPanel strategyMessagesPanel;
	private ContentPanel strategySortAndFilterPanel;
	private ControlThreadGrid strategyGrid;
	//Messages
	private CustomDualListField<ElementModel> messagesDualList;
	//Sort & Filter
	private SpinnerField maxNumberMsgField;
	private CustomDualListField<ElementModel> sortCriteriaDualList;
	private CheckBox filterMsgOpt1CB;
	private CheckBox filterMsgOpt2CB;
	private CheckBox filterMsgOpt3CB;
	
	public AgentWindow(AgentWindowConf awc, AgentDescriptionFE oldAgentInfo, AgentsGrid agGrid){
		this.awc = awc;
		//TODO enable this temporal copy
//		newAgentInfo = CloneAgentDescriptionFE.doCloning(new String(oldAgentInfo.getAgentID()), 
//				new String(oldAgentInfo.getDisplayName()), oldAgentInfo);
		
		newAgentInfo = oldAgentInfo;
		
		
		this.agGrid = agGrid;
		initAgentWindow();
	}
	
	public void show(){
		window.show();
		String result = ValidateAgentDescription.validateConf(newAgentInfo);
		if(!result.equals("")){
			clearStatusPanel();
			ToasterMessage.log(FeedbackAuthoringStrings.AGENT_CONF_ERRORS);
			statusArea.setValue(FeedbackAuthoringStrings.FOLLOWING_AGENT_CONF_ERRORS_FOUND + "\n");
			addText2StatusPanel(result);
		}
	}
	
	private Window initAgentWindow(){
		
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setSize("90%", "650px");
		window.setMaximizable(true);
		window.setHeading(awc.getTitle());
		
		window.setLayout(new BorderLayout());
		window.add(initAgentConfPanel(), new BorderLayoutData(LayoutRegion.NORTH, (float)0.90));
		window.add(initStatusPanel(), new BorderLayoutData(LayoutRegion.SOUTH, (float)0.10));
		window.addWindowListener(new WindowListener() {  
			@Override
			public void windowHide(WindowEvent we) {
				//Button open = we.getWindow().getData("open");  
				//open.focus();
				if(awc.getMode() == EDIT_MODE){
					//We need to have a local & independent copy of AgentDescriptionFE, to store the changes the user has made
					// or stored them in a local structure. At the end copy them to AgentDescriptionFE.
					
					we.setCancelled(true);
			    	//Initialize message box
			    	MessageBox box = new MessageBox();
			    	box.setButtons(MessageBox.YESNO);
			    	box.setIcon(MessageBox.QUESTION);
			    	box.setTitle(FeedbackAuthoringStrings.SAVE_CONFIG_LABEL);
			    	box.setMessage(FeedbackAuthoringStrings.SAVE_CONFIG_Q_LABEL + FeedbackAuthoringStrings.QUESTION_MARK);
			    	box.addCallback(new Listener<MessageBoxEvent>() {
			    		public void handleEvent(MessageBoxEvent be) {
			    			if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
			    				saveAgentInfo();
			    				agGrid.saveAgent(newAgentInfo);
			    			}
			    		}
			    	});
			    	box.show();
					
				}
			}
		});
		window.layout();
		
		Button saveBtn = new Button(FeedbackAuthoringStrings.SAVE_CONFIG_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				saveAgentInfo();
				String result = ValidateAgentDescription.validateConf(newAgentInfo);
				if(!result.equals("")){
					clearStatusPanel();
					addText2StatusPanel(FeedbackAuthoringStrings.FOLLOWING_AGENT_CONF_ERRORS_FOUND);
					addText2StatusPanel(result);
				}
				agGrid.saveAgent(newAgentInfo);
		    }
		});
		window.addButton(saveBtn);
		if(awc.getMode() == VIEW_MODE){
			saveBtn.setEnabled(false);
		}
		
		window.addButton(new Button(FeedbackAuthoringStrings.CLOSE_LABEL, new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				window.hide();  
			}  
		}));
		
//		window.addButton(new Button("Test Window", new SelectionListener<ButtonEvent>() {  
//			@Override  
//			public void componentSelected(ButtonEvent ce) {  
//				test();
//			}  
//		}));
		
		return window;
	}
	
	/*
	 * code for testing quickly
	 */
	/*
	private void test(){
		String ontologyId = "LARGO"; //LARGO
		String nodeId = "fact";
		
		String agentId = "agent-1";
		String patternId = "analysis-1";
		
		if(FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology() == null){
	    	Ontology ontology = new Ontology(ontologyId);
	    	OntologyXML2ObjReader.parseOntology(ontology, FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getXml());
	    	FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).setOntology(ontology);
	    }
	    Ontology ontology = FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology();
	    Set<String> nodeTypes = ontology.getNodeTypes();
	    Set<String> linkTypes = ontology.getLinkTypes();
	    
	    List<StandardPropDescr> stdElemPropsList = Ontology.getStandardElemProperties();
	    List<NonStandardPropDescr> nonStdElemPropsList = ontology.getNonStandardElemProperties("fact");
		
	    
	    StructureAnalysisType pattern = (StructureAnalysisType)getAgentInfo().getConfData().getAnalysisType(new ServiceID(agentId, patternId, ServiceClass.ANALYSIS));
	    StructuralPattern patDef = pattern.getPattern();
	    
	    StructuralAnalysisTypeManipulator patManipulator = FeedbackAuthoringTabContent.getInstance().getPattternManipulator(pattern.getServiceID());
	    
	    NodeVariable nodeVar = new NodeVariable("Node-1");
	    NodeVariable nodeVar2 = new NodeVariable("Node-2");
	    LinkVariable linkVar = new LinkVariable("Link-3");
	    
	    patManipulator.addNodeVar(nodeVar);
	    patManipulator.addNodeVar(nodeVar2);
	    patManipulator.addLinkVar(linkVar);
	    
	    String2ConstSetComparison comp = new String2ConstSetComparison();
	    ElementVariableProp lhsVarProp = nodeVar.getPropVar("type", PropDescr.DEFAULT_COMPONENT_ID);
	    comp.setLeftExpr(lhsVarProp);
	    comp.setOperator(String2SetOperator.IN);
		List<String> values1 = new Vector<String>();
		values1.add("fact");
		values1.add("test");
		values1.add("hypothetical");
		comp.setRightExpr(values1);
		patManipulator.addComparisonIfNotExists(comp);
		
	    String2ConstStringComparison comp2 = new String2ConstStringComparison();
	    ElementVariableProp lhsVarProp2 = nodeVar.getPropVar("text", PropDescr.DEFAULT_COMPONENT_ID);
	    comp2.setLeftExpr(lhsVarProp2);
		comp2.setOperator(String2StringOperator.NOT_EQUAL); 
		comp2.setRightExpr("");
		patManipulator.addComparisonIfNotExists(comp2);
		
		//Adding constraint to another node
		String2VarStringComparison comp3 = new String2VarStringComparison();
		comp3.setLeftExpr(nodeVar.getPropVar("creator", PropDescr.DEFAULT_COMPONENT_ID));
		comp3.setOperator(String2StringOperator.EQUAL);
		ElementVariableProp rightExpr = nodeVar2.getPropVar("creator", PropDescr.DEFAULT_COMPONENT_ID);
		comp3.setRightExpr(rightExpr);
		patManipulator.addComparisonIfNotExists(comp3);
		
		//adding constraint to link
		String2ConstStringComparison comp4 = new String2ConstStringComparison();
		comp4.setLeftExpr(linkVar.getPropVar("type",
				PropDescr.DEFAULT_COMPONENT_ID));
		comp4.setOperator(String2StringOperator.EQUAL);
		comp4.setRightExpr("leads");
		linkVar.addComparisonIfNotExists(comp4);
		linkVar.setSource(nodeVar);
		linkVar.setTarget(nodeVar2);
		linkVar.setDirectionMatters(true);
		
				
		
//		String rElemId = ((VariableComparison)comparison).getRightExpr().getElementVar().getVarID();
//		String rPropId = ((VariableComparison)comparison).getRightExpr().getPropId();
//		String rRhsComponentID = ((VariableComparison)comparison).getRightExpr().getCompID();
//		String rCompId = null;
//		if (!PropDescr.DEFAULT_COMPONENT_ID.equals(rRhsComponentID)) {
//			rCompId = rRhsComponentID;
//		}
//		boolean rIsNode = (((VariableComparison)comparison).getRightExpr().getElementVar() instanceof NodeVariable);
		
//	    patDef.addNodeVar(nodeVar);
//	    patDef.addNodeVar(nodeVar2);
		
		//ConfigWindowPattern re = new ConfigWindowPattern();
		ElementConstraintsWindow re = new ElementConstraintsWindow(agentId, patternId, nodeVar.getVarID(), ComparisonUtil.getComponentType(nodeVar));
		re.show();
	}
	*/
	
	private ContentPanel initStatusPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		statusArea = new TextArea();
		statusArea.setReadOnly(true);
		panel.add(statusArea);
		
		return panel;
	}
	
	static final String separator = new String("=======================================================================");
	public void addText2StatusPanel(String newText){
		statusArea.setValue((statusArea.getValue() == null? "":statusArea.getValue()) + "\n" + newText);
	}
	public void clearStatusPanel(){
		statusArea.setValue("");
	}
	
	private ContentPanel initAgentConfPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		
		TabPanel mainTabPanel = new TabPanel();
		mainTabPanel.setSize("100%", "100%");
		mainTabPanel.setMinTabWidth(115);
		mainTabPanel.setTabScroll(true);
		
		//General Tab
		TabItem generalTabItem = new TabItem();
		generalTabItem.setText(FeedbackAuthoringStrings.HEADING_GENERAL);
		generalTabItem.setLayout(new FitLayout()); //always add a layout to the tab item.
		generalTabItem.add(getGeneralTabItemContent());
		generalTabItem.setEnabled(true);
		
		//Patterns Tab
		TabItem patternTabItem = new TabItem();
		patternTabItem.setText(FeedbackAuthoringStrings.HEADING_PATTERNS);
		patternTabItem.setLayout(new FitLayout());
		patternTabItem.add(getPatternsTabItemContent());
		
		//Feedback Tab
		TabItem feedbackTabItem = new TabItem();
		feedbackTabItem.setText(FeedbackAuthoringStrings.HEADING_MESSAGES);
		feedbackTabItem.setLayout(new FitLayout());
		feedbackTabItem.add(getFeedbackTabItemContent());
		
		//Provision Tab
		TabItem provisionTabItem = new TabItem();
		provisionTabItem.setText(FeedbackAuthoringStrings.HEADING_STRATEGIES);
		provisionTabItem.setLayout(new FitLayout());
		provisionTabItem.add(getStrategyTabItemContent());

		mainTabPanel.add(generalTabItem);
		mainTabPanel.add(patternTabItem);
		mainTabPanel.add(feedbackTabItem);
		mainTabPanel.add(provisionTabItem);
		
		mainTabPanel.setSelection(generalTabItem);
		
		panel.add(mainTabPanel);
		return panel;
	}
	
	private ContentPanel getGeneralTabItemContent(){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeading(FeedbackAuthoringStrings.HEADING_GENERAL_AGENT_SETTINGS);
		panel.setScrollMode(Scroll.AUTOY);
		panel.add(createGeneralTabInnerTabPanel());
		
		return panel;
	}
	
	private Widget createGeneralTabInnerTabPanel(){
		
		TabPanel innerTabPanel = new TabPanel();
		innerTabPanel.setSize("100%", "100%");
		innerTabPanel.setMinTabWidth(115);
		innerTabPanel.setTabScroll(true);
		innerTabPanel.setStyleName("faSubTab");

		TabItem basicTabItem = new TabItem();
		basicTabItem.setText(FeedbackAuthoringStrings.HEADING_BASIC);
		basicTabItem.setLayout(new FitLayout());
		basicTabItem.add(getGeneralBasicTabContent());
		
		TabItem phasesTabItem = new TabItem();
		phasesTabItem.setText(FeedbackAuthoringStrings.HEADING_PHASES + FeedbackAuthoringStrings.HEADING_OPT);
		phasesTabItem.setLayout(new FitLayout());
		phasesTabItem.add(getGeneralPhaseTabContent());
		
		innerTabPanel.add(phasesTabItem);
		innerTabPanel.add(basicTabItem);
		
		innerTabPanel.setSelection(basicTabItem);
		
		return innerTabPanel;
	}
	
	private Widget getGeneralBasicTabContent(){
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(450);
		formPanel.setLabelWidth(120);
		
		agentIdField = new TextField<String>();
		agentIdField.setFieldLabel(FeedbackAuthoringStrings.ID_LABEL);
		agentIdField.setValue(newAgentInfo.getAgentID());
		agentIdField.setReadOnly(true);
		agentIdField.setEnabled(false);
		agentIdField.setEnabled(false);
		agentIdField.setStyleAttribute("disabled", "disabled");
	    formPanel.add(agentIdField);
	    
	    agentNameField = new TextField<String>();
	    agentNameField.setFieldLabel(FeedbackAuthoringStrings.NAME_LABEL);
	    agentNameField.setAllowBlank(false);
	    agentNameField.setValue(newAgentInfo.getDisplayName());
		agentNameField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
				@SuppressWarnings("unchecked")
				String newVal = (String) ((TextField<String>)event.getField()).getValue();
	    		if(newVal != null){
	    			newAgentInfo.setDisplayName(newVal);
	    		}
	    	}
	    });
		if(awc.getMode() == VIEW_MODE){
			agentNameField.setReadOnly(true);
		}
	    formPanel.add(agentNameField);
	    
	    
	    ListStore<ElementModel> ontologies = new ListStore<ElementModel>();
//	    ElementModel anyOnt = new ElementModel(FeedbackAuthoringStrings.ANY_LABEL, null);
//	    ontologies.add(anyOnt);
	    ontologies.add(Data2ModelConverter.getOntListAsModelList(awc.getOntologyList()));  
	  
	    supportedOntCombo = new ComboBox<ElementModel>();
	    supportedOntCombo.setFieldLabel(FeedbackAuthoringStrings.SUPPORTED_ONTOLOGY_LABEL);
	    //comboSupportedOnto.setEmptyText("...");
	    supportedOntCombo.setDisplayField(GridElementLabel.NAME);  
	    supportedOntCombo.setWidth(150);  
	    supportedOntCombo.setStore(ontologies);  
	    supportedOntCombo.setForceSelection(true);
	    supportedOntCombo.setTriggerAction(TriggerAction.ALL);
	    SupportedOntologiesDef supportedOnt = newAgentInfo.getSupportedOntology();
    	if (supportedOnt != null && supportedOnt.getSupportedOntologies().size() >=1){
    		String ontology = supportedOnt.getSupportedOntologies().get(0);
    		Map<String, String> map = new HashMap<String, String>();
    		map.put(GridElementLabel.NAME, ontology);
    		map.put(GridElementLabel.ID, ontology);
        	ElementModel value = new ElementModel(map);
        	supportedOntCombo.setValue(value);
    	}
    	supportedOntCombo.addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				ElementModel elem = se.getSelectedItem();
				String elemId = elem.getValue(GridElementLabel.ID);
				String oldOntology = newAgentInfo.getSupportedOntology().getSupportedOntologies().get(0);
				if(!elemId.equals(oldOntology)){
					newAgentInfo.getSupportedOntology().getSupportedOntologies().clear();
					newAgentInfo.getSupportedOntology().getSupportedOntologies().add(elemId);
					//TODO check that the already defined patterns are valid, 
					//if a pattern is invalid, delete it and remove it from message
				}
			}
		});
    	
	    if(awc.getMode() == VIEW_MODE){
	    	supportedOntCombo.setReadOnly(true);
		}
	    formPanel.add(supportedOntCombo);
	    
	    agentDescriptionTA = new TextArea();  
	    agentDescriptionTA.setPreventScrollbars(true);  
	    agentDescriptionTA.setFieldLabel(FeedbackAuthoringStrings.DESCRIPTION_LABEL);
	    agentDescriptionTA.setOriginalValue("");
	    agentDescriptionTA.setValue(newAgentInfo.getDescription());
	    agentDescriptionTA.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
				String newVal = (String) ((TextArea)event.getField()).getValue();
	    		if(newVal != null){
	    			newAgentInfo.setDescription(newVal);
	    		}
	    	}
	    });
	    if(awc.getMode() == VIEW_MODE){
	    	agentDescriptionTA.setReadOnly(true);
		}
	    formPanel.add(agentDescriptionTA);
	    
	    panel.add(formPanel);
		
		return panel;
	}
	
	private Widget getGeneralPhaseTabContent(){
		
		GridConf gridConf = new GridConf();
		gridConf.setHeader(FeedbackAuthoringStrings.HEADING_PHASES);
		gridConf.setAddButtonFlag(true);
		gridConf.setDeleteButtonFlag(true);
		gridConf.setCommonLabel(FeedbackAuthoringStrings.PHASE_LABEL);
		
		ColumnConf colTmp = new ColumnConf(GridElementLabel.NAME, FeedbackAuthoringStrings.NAME_LABEL, 140);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.DESC, FeedbackAuthoringStrings. DESCRIPTION_LABEL, 200);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.ACTIONS, FeedbackAuthoringStrings.ACTIONS_LABEL, 50);
		gridConf.addColConfig(colTmp);
		gridConf.setAutoExpandColumn(GridElementLabel.DESC);
		
		//RowEditorGrid phases = new RowEditorGrid(gridConf, this);
		phasesGrid = new PhasesGrid(gridConf, this);
			
		
		return phasesGrid;
	}
	
	private ContentPanel getPatternsTabItemContent(){
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		panel.add(createPatternsTopPanel(), new RowData(1.0, 0.35, new Margins(3)));
		panel.add(createPatternsBottomPanel(), new RowData(1.0, 0.65, new Margins(3)));
		
		return panel;
	}
	
	private Widget createPatternsBasicPanel(final String id, final AnalysisType pattern){
		pattern.getServiceID().getTypeID();
		patternsBasicPanel = new ContentPanel();
		patternsBasicPanel.setLayout(new FitLayout());
		patternsBasicPanel.setHeaderVisible(false);
		patternsBasicPanel.setBodyBorder(false);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(400);
		formPanel.setLabelWidth(100);
//		formPanel.setStyleAttribute("color", "#000000");
		
		patternIdField = new TextField<String>();
		patternIdField.setFieldLabel(FeedbackAuthoringStrings.ID_LABEL);
		patternIdField.setEnabled(false);
		if(id != null){
			patternIdField.setValue(id);
		}
	    formPanel.add(patternIdField);
	    
	    patternNameField = new TextField<String>();
	    patternNameField.setFieldLabel(FeedbackAuthoringStrings.NAME_LABEL);
	    patternNameField.setAllowBlank(false);
	    patternNameField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		@SuppressWarnings("unchecked")
				String newVal = (String) ((TextField<String>)event.getField()).getValue();
	    		if(newVal != null){
	    			pattern.setName(newVal);
	    			
	    			String userSpecific = "";
    				for(PatternFilterDef patDef : pattern.getFilterDefs()){
    					if(patDef instanceof PatternFilterDef_User){
    						userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
    						break;
    					}
    				}
	    			if(pattern instanceof StructureAnalysisType){
	    				patternsGrid.update(id, newVal, FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL, userSpecific);
	    			}
	    			else{
	    				patternsGrid.update(id, newVal, FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL, userSpecific);
	    			}
	    		}
	    	}
	    });
	    if(pattern.getName() != null){
	    	patternNameField.setValue(pattern.getName());
		}
	    formPanel.add(patternNameField);
	    
	    patternDescription = new TextArea();  
	    patternDescription.setPreventScrollbars(true);  
	    patternDescription.setFieldLabel(FeedbackAuthoringStrings.DESCRIPTION_LABEL);
	    patternDescription.setOriginalValue("");
	    patternDescription.setDeferHeight(true);
	    patternDescription.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
				String newVal = (String) ((TextArea)event.getField()).getValue();
	    		if(newVal != null){
	    			pattern.setDescription(newVal);
	    		}
	    	}
	    });
	    if(pattern.getDescription() != null){
	    	patternDescription.setValue(pattern.getDescription());
		}
	    formPanel.add(patternDescription);
	    
	    patternsBasicPanel.add(formPanel);
		
		return patternsBasicPanel;
	}
	
	private Widget createPatternsFilterPanel(final AnalysisType pattern){
		patternsFilterPanel = new ContentPanel();
		patternsFilterPanel.setHeaderVisible(false);
		patternsFilterPanel.setBodyBorder(false);
		patternsFilterPanel.setScrollMode(Scroll.AUTOY);
		
		//User restrictions
	    FormPanel userRestPanel = new FormPanel();
	    userRestPanel.setLabelWidth(0);
	    userRestPanel.setLabelSeparator("");
	    if(pattern instanceof CounterAnalysisType){
	    	userRestPanel.setHeading(FeedbackAuthoringStrings.HEADING_COUNT_USER_RESTRICTIONS);
	    } else{
	    	userRestPanel.setHeading(FeedbackAuthoringStrings.HEADING_STRUCTURE_USER_RESTRICTIONS);
	    }
		userRestPanel.setBodyBorder(true);
		userRestPanel.setWidth(650);
		userRestPanel.setStyleAttribute("color", "#000000");
		
		userRestFeedFilterNoteLabel = new Label(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_STRUCTURE_WARNING_LABEL);
		userRestFeedFilterNoteLabel.setStyleAttribute("color", "#000000");
		
		Button userRestHelpButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				MessageBox.info(FeedbackAuthoringStrings.HELP_LABEL, FeedbackAuthoringStrings.USER_SPECIFIC_DEF_LABEL, null);
			}
		};
		userRestHelpButton.setToolTip(FeedbackAuthoringStrings.HELP_LABEL);
		userRestHelpButton.setIconStyle("icon-question");
		userRestHelpButton.setStyleName("xfa-btn");
		userRestHelpButton.setStyleName("x-btn-text", true);
		userRestHelpButton.setStyleName("x-btn", true);
		userRestHelpButton.setStylePrimaryName("xfa-btn");
		
		//this code groups the label and the button
		HorizontalPanel hp = new HorizontalPanel();
		hp.setAutoWidth(true);
		hp.setStyleAttribute("color", "#000000");
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 4px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td1.setStyle("style='padding-left: 1px;'");
		TableData td2 = new TableData();
		td2.setHorizontalAlign(HorizontalAlignment.LEFT);
		td2.setStyle("style='padding-left: 6px;'");
		
		hp.add(userRestFeedFilterNoteLabel, td);
		hp.add(userRestHelpButton, td1);		
		
		Button userRestOpt2HelpButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				MessageBox.info(FeedbackAuthoringStrings.HELP_LABEL, FeedbackAuthoringStrings.CONTRIBUTOR_DEF_LABEL, null);
				
			}
		};
		userRestOpt2HelpButton.setToolTip(FeedbackAuthoringStrings.HELP_LABEL);
		userRestOpt2HelpButton.setIconStyle("icon-question");
		userRestOpt2HelpButton.setStyleName("xfa-btn");
		userRestOpt2HelpButton.setStyleName("x-btn-text", true);
		userRestOpt2HelpButton.setStyleName("x-btn", true);
		userRestOpt2HelpButton.setStylePrimaryName("xfa-btn");
		
		//Get Filter settings to set them later
		PatternFilterDef_UserSetting userSetting = null;
		PatternFilterDef_LastModTimeSetting timeSetting = null;
		Integer timeSettingVal = 0;
		for(PatternFilterDef patDef : pattern.getFilterDefs()){
			if(patDef instanceof PatternFilterDef_User){
//				userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
				userSetting = ((PatternFilterDef_User)patDef).getSetting();
				continue;
			}else if(patDef instanceof PatternFilterDef_LastModTime){
				timeSetting = ((PatternFilterDef_LastModTime)patDef).getSetting();
				timeSettingVal = ((PatternFilterDef_LastModTime)patDef).getReferenceValue();
				continue;
			}
		}
		
		
		Radio userRestR1 = new Radio();
		userRestR1.setName(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL);
		if(pattern instanceof CounterAnalysisType){
			userRestR1.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_COUNT_OPT1_LABEL);
		} else{
			userRestR1.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_STRUCTURE_OPT1_LABEL);
		}
		userRestR1.setId(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL+PatternFilterDef_UserSetting.NONE.toString());
		userRestR1.setValueAttribute(PatternFilterDef_UserSetting.NONE.toString());
		if(userSetting != null)
			userRestR1.setValue(userSetting.equals(PatternFilterDef_UserSetting.NONE)? true : false);
		userRestR1.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {
            	updatePatternUserRest(PatternFilterDef_UserSetting.NONE, pattern);
            }
        });
		userRestR1.setStyleAttribute("color", "#000000");
		
	    Radio userRestR2 = new Radio();
	    userRestR2.setName(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL);
	    if(pattern instanceof CounterAnalysisType){
	    	userRestR2.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_COUNT_OPT2_LABEL);
	    } else{
	    	userRestR2.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_STRUCTURE_OPT2_LABEL);
	    }
	    userRestR2.setId(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL+PatternFilterDef_UserSetting.OWNER.toString());
	    userRestR2.setValueAttribute(PatternFilterDef_UserSetting.OWNER.toString());
	    if(userSetting != null)
	    	userRestR2.setValue(userSetting.equals(PatternFilterDef_UserSetting.OWNER)? true : false);
	    userRestR2.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {
            	updatePatternUserRest(PatternFilterDef_UserSetting.OWNER, pattern);
            }
        });
	    userRestR2.setStyleAttribute("color", "#000000");
	    
	    Radio userRestR3 = new Radio();
	    userRestR3.setName(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL);
	    userRestR3.setId(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL+PatternFilterDef_UserSetting.CONTRIBUTOR.toString());
	    if(pattern instanceof CounterAnalysisType){
	    	userRestR3.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_COUNT_OPT3_LABEL);
	    } else{
	    	userRestR3.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_STRUCTURE_OPT3_LABEL);
	    }
	    userRestR3.setValueAttribute(PatternFilterDef_UserSetting.CONTRIBUTOR.toString());
	    if(userSetting != null)
	    	userRestR3.setValue(userSetting.equals(PatternFilterDef_UserSetting.CONTRIBUTOR)? true : false);
	    userRestR3.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {
            	updatePatternUserRest(PatternFilterDef_UserSetting.CONTRIBUTOR, pattern);
            }
        });
	    userRestR3.setStyleAttribute("color", "#000000");
	    
	    Radio userRestR4 = new Radio();
	    userRestR4.setName(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL);
	    userRestR4.setId(FeedbackAuthoringStrings.USER_REST_RADIO_LABEL+PatternFilterDef_UserSetting.NON_CONTRIBUTOR.toString());
	    if(pattern instanceof CounterAnalysisType){
	    	userRestR4.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_COUNT_OPT4_LABEL);
	    } else{
	    	userRestR4.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_USER_FILTER_STRUCTURE_OPT4_LABEL);
	    }
	    userRestR4.setValueAttribute(PatternFilterDef_UserSetting.NON_CONTRIBUTOR.toString());
	    if(userSetting != null)
	    	userRestR4.setValue(userSetting.equals(PatternFilterDef_UserSetting.NON_CONTRIBUTOR)? true : false);
	    userRestR4.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {
            	updatePatternUserRest(PatternFilterDef_UserSetting.NON_CONTRIBUTOR, pattern);
            }
        });
	    userRestR4.setStyleAttribute("color", "#000000");
	    
	    userRestPanel.add(hp);
	    userRestPanel.add(userRestR1);
	    
	    //this code arrange the radio and the help button
	    HorizontalPanel hpUserRest2 = new HorizontalPanel();
	    hpUserRest2.setAutoWidth(true);
	    hpUserRest2.add(userRestR2, td2);
	    hpUserRest2.add(userRestOpt2HelpButton, td1);
	    
	    userRestPanel.add(hpUserRest2);
	    userRestPanel.add(userRestR3);
	    userRestPanel.add(userRestR4);
	    
	    
	    /*
	     * Time restrictions
	     */
	    FormPanel timeRestPanel = new FormPanel();
	    if(pattern instanceof CounterAnalysisType){
	    	timeRestPanel.setHeading(FeedbackAuthoringStrings.HEADING_COUNT_TIME_RESTRICTIONS);
	    } else{
	    	timeRestPanel.setHeading(FeedbackAuthoringStrings.HEADING_STRUCTURE_TIME_RESTRICTIONS);
	    }
	    timeRestPanel.setLabelWidth(0);
	    timeRestPanel.setLabelSeparator("");
	    timeRestPanel.setBodyBorder(true);
	    timeRestPanel.setWidth(650);
	    timeRestPanel.setStyleAttribute("color", "#000000");
		
		Radio timeRestR1 = new Radio();
		if(pattern instanceof CounterAnalysisType){
			timeRestR1.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_COUNT_OPT1_LABEL);
		} else{
			timeRestR1.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_STRUCTURE_OPT1_LABEL);
		}
		timeRestR1.setName(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL);
		timeRestR1.setId(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL+PatternFilterDef_LastModTimeSetting.NONE.toString());
		timeRestR1.setValueAttribute(PatternFilterDef_LastModTimeSetting.NONE.toString());
		if(timeSetting != null)
			timeRestR1.setValue(timeSetting.equals(PatternFilterDef_LastModTimeSetting.NONE)? true : false);
		timeRestR1.setStyleAttribute("color", "#000000");
		timeRestR1.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {
            	timeRestSecsOp2FeedFilterField.setEnabled(false);
            	timeRestSecsOp3FeedFilterField.setEnabled(false);
            	updatePatternTimeRest(PatternFilterDef_LastModTimeSetting.NONE, new Integer(0), pattern);
            }
        });
		
	    Radio timeRestR2 = new Radio();
	    if(pattern instanceof CounterAnalysisType){
	    	timeRestR2.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_COUNT_OPT2_1_LABEL);
	    } else{
	    	timeRestR2.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_STRUCTURE_OPT2_1_LABEL);
	    }
	    timeRestR2.setName(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL);
	    timeRestR2.setId(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL+PatternFilterDef_LastModTimeSetting.MIN_AGE.toString());
		timeRestR2.setValueAttribute(PatternFilterDef_LastModTimeSetting.MIN_AGE.toString());
		if(timeSetting != null)
			timeRestR2.setValue(timeSetting.equals(PatternFilterDef_LastModTimeSetting.MIN_AGE)? true : false);
	    timeRestR2.setStyleAttribute("color", "#000000");
	    timeRestR2.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {                    
            	timeRestSecsOp2FeedFilterField.setEnabled(true);
            	timeRestSecsOp3FeedFilterField.setEnabled(false);
            	timeRestSecsOp2FeedFilterField.setValue(0);
            	timeRestSecsOp3FeedFilterField.setValue(0);
            	updatePatternTimeRest(PatternFilterDef_LastModTimeSetting.MIN_AGE, new Integer(0), pattern);
            }
        });
	    
	    Radio timeRestR3 = new Radio();
	    if(pattern instanceof CounterAnalysisType){
	    	timeRestR3.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_COUNT_OPT3_1_LABEL);
	    } else{
	    	timeRestR3.setBoxLabel(FeedbackAuthoringStrings.FEEDBACK_TIME_FILTER_STRUCTURE_OPT3_1_LABEL);
	    }
	    timeRestR3.setName(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL);
	    timeRestR3.setId(FeedbackAuthoringStrings.TIME_REST_RADIO_LABEL+PatternFilterDef_LastModTimeSetting.MAX_AGE.toString());
		timeRestR3.setValueAttribute(PatternFilterDef_LastModTimeSetting.MAX_AGE.toString());
		if(timeSetting != null)
			timeRestR3.setValue(timeSetting.equals(PatternFilterDef_LastModTimeSetting.MAX_AGE)? true : false);
	    timeRestR3.setStyleAttribute("color", "#000000");
	    timeRestR3.addListener(Events.OnClick, new Listener<FieldEvent>() {              
            @Override
            public void handleEvent(FieldEvent be) {                    
            	timeRestSecsOp2FeedFilterField.setEnabled(false);
            	timeRestSecsOp3FeedFilterField.setEnabled(true);
            	timeRestSecsOp3FeedFilterField.setValue(0);
            	timeRestSecsOp2FeedFilterField.setValue(0);
            	updatePatternTimeRest(PatternFilterDef_LastModTimeSetting.MAX_AGE, new Integer(0), pattern);
            }
        });
	    
	    timeRestSecsOp2FeedFilterField = new NumberField();
	    timeRestSecsOp2FeedFilterField.setPropertyEditorType(Integer.class);
	    timeRestSecsOp2FeedFilterField.setAllowDecimals(false);
	    timeRestSecsOp2FeedFilterField.setAllowNegative(false);
	    timeRestSecsOp2FeedFilterField.setWidth(50);
	    timeRestSecsOp2FeedFilterField.setId("timeRestSecsOp2FeedFilterField");
	    if(timeSetting != null && timeSetting == PatternFilterDef_LastModTimeSetting.MIN_AGE && timeSettingVal != null){
	    	timeRestSecsOp2FeedFilterField.setValue(timeSettingVal);
	    	timeRestSecsOp2FeedFilterField.setEnabled(true);
	    } else{
	    	timeRestSecsOp2FeedFilterField.setValue(0);
	    	timeRestSecsOp2FeedFilterField.setEnabled(false);
	    }
	    timeRestSecsOp2FeedFilterField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		Integer newVal = (Integer) ((NumberField)event.getField()).getValue();
	    		if(newVal != null && newVal > 0){
	    			try{
	    				//newVal = newVal >= 0? newVal : 0;
	    				updatePatternTimeRest(PatternFilterDef_LastModTimeSetting.MIN_AGE, newVal, pattern);
	    			}catch(NumberFormatException e){
	    				
	    			}
	    			
	    		} else{
	    			timeRestSecsOp2FeedFilterField.setValue(0);
	    		}
	    	}
	    });
	    
	    Label l2 = new Label(FeedbackAuthoringStrings.FEEDBACK_FILTER_TIME_REST_OPT2_2_LABEL);
  		l2.setStyleAttribute("color", "#000000");
	    
	    timeRestSecsOp3FeedFilterField = new NumberField();
	    timeRestSecsOp3FeedFilterField.setPropertyEditorType(Integer.class);
	    timeRestSecsOp3FeedFilterField.setAllowDecimals(false);
	    timeRestSecsOp3FeedFilterField.setAllowNegative(false);
	    timeRestSecsOp3FeedFilterField.setWidth(50);
	    timeRestSecsOp3FeedFilterField.setId("timeRestSecsOp3FeedFilterField");
	    if(timeSetting != null && timeSetting.equals(PatternFilterDef_LastModTimeSetting.MAX_AGE) && timeSettingVal != null){
	    	timeRestSecsOp3FeedFilterField.setValue(timeSettingVal);
	    	timeRestSecsOp3FeedFilterField.setEnabled(true);
	    } else{
	    	timeRestSecsOp3FeedFilterField.setValue(0);
	    	timeRestSecsOp3FeedFilterField.setEnabled(false);
	    }
	    timeRestSecsOp3FeedFilterField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		Integer newVal = (Integer) ((NumberField)event.getField()).getValue();
	    		if(newVal != null){
	    			try{
	    				newVal = newVal >= 0? newVal : 0;
//	    				((NumberField)event.getField()).setValue(newVal);
	    				updatePatternTimeRest(PatternFilterDef_LastModTimeSetting.MAX_AGE, newVal, pattern);
	    			}catch(NumberFormatException e){
	    				
	    			}
	    			
	    		} else{
	    			timeRestSecsOp3FeedFilterField.setValue(0);
	    		}
	    	}
	    });
	    Label l3 = new Label(FeedbackAuthoringStrings.FEEDBACK_FILTER_TIME_REST_OPT3_2_LABEL);
  		l3.setStyleAttribute("color", "#000000");
  		
  		timeRestPanel.add(timeRestR1);
  		
  		//this code groups the text field and the label
  		HorizontalPanel hpTimeRestSecsOp2 = new HorizontalPanel();
  		hpTimeRestSecsOp2.add(timeRestR2, td2);
  		hpTimeRestSecsOp2.setAutoWidth(true);
  		hpTimeRestSecsOp2.add(timeRestSecsOp2FeedFilterField, td);
  		hpTimeRestSecsOp2.add(l2, td);
  		
  		//this code groups the text field and the label
  		HorizontalPanel hpTimeRestSecsOp3 = new HorizontalPanel();
  		hpTimeRestSecsOp3.setAutoWidth(true);
  		hpTimeRestSecsOp3.add(timeRestR3, td2);
  		hpTimeRestSecsOp3.add(timeRestSecsOp3FeedFilterField, td);
  		hpTimeRestSecsOp3.add(l3, td);

  		timeRestPanel.add(hpTimeRestSecsOp2);
  		timeRestPanel.add(hpTimeRestSecsOp3);
		
  		patternsFilterPanel.add(userRestPanel);
  		patternsFilterPanel.add(timeRestPanel);
		
		return patternsFilterPanel;
	}
	
	private void updatePatternUserRest(PatternFilterDef_UserSetting userSetting, AnalysisType pattern){
		PatternFilterDef_User userRest = null;
		//search for it
		for(PatternFilterDef patDef : pattern.getFilterDefs()){
			if(patDef instanceof PatternFilterDef_User){
				userRest = (PatternFilterDef_User)patDef;
				break;
			}
		}
		pattern.getFilterDefs().remove(userRest);
		userRest = new PatternFilterDef_User(userSetting);
		pattern.getFilterDefs().add(userRest);
		String userSpecific = userRest.isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
		//update grid
		if(pattern instanceof StructureAnalysisType){
			patternsGrid.update(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL, userSpecific);
		}
		else{
			patternsGrid.update(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL, userSpecific);
		}
	}
	
	private void updatePatternTimeRest(PatternFilterDef_LastModTimeSetting timeSetting, Integer timeSettingVal, AnalysisType pattern){
		PatternFilterDef_LastModTime timeRest = null;
		for(PatternFilterDef patDef : pattern.getFilterDefs()){
			if(patDef instanceof PatternFilterDef_LastModTime){
				timeRest = (PatternFilterDef_LastModTime) patDef;
				break;
			}
		}
		pattern.getFilterDefs().remove(timeRest);
		timeRest = new PatternFilterDef_LastModTime(timeSetting, timeSettingVal);
		pattern.getFilterDefs().add(timeRest);
	}
	
	private Widget createPatternsDefinitionPanel(Widget wid){
		
		patternsDefinitionPanel = new ContentPanel();
		patternsDefinitionPanel.setLayout(new FitLayout());
		patternsDefinitionPanel.setHeaderVisible(false);
		patternsDefinitionPanel.setBodyBorder(false);
		patternsDefinitionPanel.add(wid);
		patternsDefinitionPanel.layout();
		return patternsDefinitionPanel;
		
	}
	
	private Widget createPatternsBottomPanel(){
		patternsBottomPanel = new ContentPanel();
		patternsBottomPanel.setHeaderVisible(false);
		patternsBottomPanel.setBodyBorder(false);
		patternsBottomPanel.setWidth("100%");
		patternsBottomPanel.setHeight("100%");
		patternsBottomPanel.setLayout(new FitLayout());
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_PATTERN_FROM_LABEL));
		patternsBottomPanel.add(panel); //, new RowData(1.0, 1.00, new Margins(1))
		//panel.add(createPatternsBottomPatCountPanel(), new RowData(1.0, 0.50, new Margins(1)));
		return patternsBottomPanel;
	}	
	
	private Widget createPatternsBottomPanelItemSelected(final String patternId){
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.setBodyBorder(true);
		
		TabPanel innerTabPanel = new TabPanel();
		innerTabPanel.setSize("100%", "100%");
		innerTabPanel.setMinTabWidth(115);
		innerTabPanel.setTabScroll(true);
		innerTabPanel.setStyleName("faSubTab");
		//innerTabPanel.setDeferredRender(false);
		
		ServiceID servPatternId = new ServiceID(newAgentInfo.getAgentID(), patternId, ServiceClass.ANALYSIS);
		final AnalysisType pattern = getAgentInfo().getConfData().getAnalysisType(servPatternId);
		
		if(pattern instanceof StructureAnalysisType){ // FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL.equals(patternType)
			panel.setHeading(FeedbackAuthoringStrings.HEADING_PATERN + " (" + (FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL) + ")");
			
			StructureAnalysisType pat = (StructureAnalysisType) pattern;
			
			createPatternsBasicPanel(patternId, pat);
			createPatternsDefinitionPanel(createPatternsBottomPatStructPanel(pat));
			createPatternsFilterPanel(pat);
			
		} else if(pattern instanceof CounterAnalysisType){ //FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL.equals(patternType)
			panel.setHeading(FeedbackAuthoringStrings.HEADING_PATERN + " (" + (FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL) + ")");
			CounterAnalysisType pat = (CounterAnalysisType) pattern;
			
			createPatternsBasicPanel(patternId, pat);
			createPatternsDefinitionPanel(createPatternsBottomPatCountPanel(pat));
			createPatternsFilterPanel(pat);
		}

		TabItem basicTabItem = new TabItem();
		basicTabItem.setText(FeedbackAuthoringStrings.HEADING_BASIC);
		basicTabItem.setLayout(new FitLayout());
		basicTabItem.add(patternsBasicPanel);
		
		TabItem definitionTabItem = new TabItem();
		definitionTabItem.setText(FeedbackAuthoringStrings.HEADING_DEFINITION);
		definitionTabItem.setLayout(new FitLayout());
		definitionTabItem.add(patternsDefinitionPanel);
		definitionTabItem.addListener(Events.Select, new SelectionListener<ComponentEvent>(){  
			@Override
			public void componentSelected(ComponentEvent be){
				if(pattern instanceof StructureAnalysisType){
//					PatternController controller = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(newAgentInfo.getAgentID(), patternId);
//					controller.getViewSession().forceLayout();
//					TabPanelEvent tbe = (TabPanelEvent)be;
//	                TabItem tbi = (TabItem)tbe.getItem();
//	                tbi.layout(true);
	                FeedbackAuthoringTabContent.getInstance().getPatternServerManager().replay(newAgentInfo.getAgentID(), patternId);
				}
            }
        });
		
		TabItem filterTabItem = new TabItem();
		filterTabItem.setText(FeedbackAuthoringStrings.HEADING_FILTER + FeedbackAuthoringStrings.HEADING_OPT);
		filterTabItem.setLayout(new FitLayout());
		filterTabItem.add(patternsFilterPanel);
		
		innerTabPanel.add(filterTabItem);
		innerTabPanel.add(definitionTabItem);
		innerTabPanel.add(basicTabItem);
		
		innerTabPanel.setSelection(basicTabItem);
		panel.add(innerTabPanel);
		
		return panel;
	}
	
	
	public void updatePatternBottomPanel(String patternId){
		patternsBottomPanel.removeAll();
		patternsBottomPanel.setLayout(new FitLayout());
		patternsBottomPanel.add(createPatternsBottomPanelItemSelected(patternId));
		patternsBottomPanel.layout();
//		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().replay(newAgentInfo.getAgentID(), patternId);
	}
	
	public void resetPatternBottomPanel(){
		patternsBottomPanel.removeAll();
		patternsBottomPanel.setLayout(new FitLayout());
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_PATTERN_FROM_LABEL));
		patternsBottomPanel.add(panel);
		patternsBottomPanel.layout();
	}
	
	private Widget createPatternsBottomPatStructPanel(StructureAnalysisType pattern){
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		
		String patternId = pattern.getServiceID().getTypeID();
		String patternName = pattern.getName();
		String ontologyName = pattern.getOntologyID();
		String ontologyXML = agGrid.getOntologyXML(ontologyName);
		PatternGraphMapInfo patternInfo = new PatternGraphMapInfo(patternId);

		patternInfo.setAgentId(newAgentInfo.getAgentID());
		patternInfo.setTitle(patternName);
		patternInfo.setOntologyName(ontologyName);
		patternInfo.setXmlOntology(ontologyXML);
		patternInfo.setFaOntologyName(FATConstants.FA_ONTOLOGY_NAME);
		patternInfo.setFaXmlOntology(agGrid.getOntologyXML(FATConstants.FA_ONTOLOGY_NAME));
		patternInfo = (PatternGraphMapInfo) OntologyReader.buildOntologyInfosFromXML(patternInfo, patternInfo.getFaXmlOntology());
		
		Size size = patternsBottomPanel.getSize();
		//PatternDrawingAreaSpace patternDrawingAreaSpace = new PatternDrawingAreaSpace(size);
		
		//panel.add(patternDrawingAreaSpace);
		
		PatternController controller = new PatternController(patternInfo);
		controller.setPatManipulator(FeedbackAuthoringTabContent.getInstance().getPattternManipulator(pattern.getServiceID()));
		
		PatternDrawingAreaMVCViewSession mapSession = new PatternDrawingAreaMVCViewSession(controller,size);//   (newController, mapTab);
		controller.registerViewSession(mapSession);
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addController(newAgentInfo.getAgentID(), patternId, controller);
		panel.add(mapSession.getArgumentMapSpace());
		
		return panel;
	}
	
	private Widget createPatternsBottomPatCountPanel(final CounterAnalysisType pattern){
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);	
		panel.setBodyBorder(false);
		panel.setScrollMode(Scroll.AUTOY);
		
		/*
		 * Instance type restrictions
		 */		
		FormPanel instTypeRestPanel = new FormPanel();
//		instTypeRestPanel.setHeading(FeedbackAuthoringStrings.INSTANCE_TYPE_RESTRICTIONS_HEADER);
		instTypeRestPanel.setHeaderVisible(false);
		instTypeRestPanel.setBodyBorder(true);
		instTypeRestPanel.setWidth(500);
		instTypeRestPanel.setLabelWidth(130);
		instTypeRestPanel.setStyleAttribute("color", "#000000");
		
		InstanceTypeGeneral instanceTypeGeneral = pattern.getCounterDefinition().getInstanceTypeGeneral();
		
		Radio gralInstClassR1 = new Radio();  
	    gralInstClassR1.setBoxLabel(FeedbackAuthoringStrings.BOX_LABEL);
	    gralInstClassR1.setValueAttribute(InstanceTypeGeneral.NODE.toString());
	    gralInstClassR1.setValue(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE)? true: false);
	    gralInstClassR1.setStyleAttribute("color", "#000000");
	    Radio gralInstClassR2 = new Radio();
	    gralInstClassR2.setBoxLabel(FeedbackAuthoringStrings.LINK_LABEL);
	    gralInstClassR2.setValueAttribute(InstanceTypeGeneral.LINK.toString());
	    gralInstClassR2.setValue(instanceTypeGeneral.equals(InstanceTypeGeneral.LINK)? true: false);
	    gralInstClassR2.setStyleAttribute("color", "#000000");
	    Radio gralInstClassR3 = new Radio();
	    gralInstClassR3.setBoxLabel(FeedbackAuthoringStrings.BOX_AND_LINK_LABEL);
	    gralInstClassR3.setValueAttribute(InstanceTypeGeneral.NODE_OR_LINK.toString());
	    gralInstClassR3.setValue(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE_OR_LINK)? true: false);
	    gralInstClassR3.setStyleAttribute("color", "#000000");
	    Radio gralInstClassR4 = new Radio();
	    gralInstClassR4.setBoxLabel(FeedbackAuthoringStrings.PATTERNS_LABEL);
	    gralInstClassR4.setValueAttribute(InstanceTypeGeneral.PATTERN.toString());
	    gralInstClassR4.setValue(instanceTypeGeneral.equals(InstanceTypeGeneral.PATTERN)? true: false);
	    gralInstClassR4.setStyleAttribute("color", "#000000");
	    instTypeRestRG = new RadioGroup();
	    instTypeRestRG.setFieldLabel(FeedbackAuthoringStrings.WHAT_IS_COUNTED_LABEL);
	    instTypeRestRG.add(gralInstClassR1);
	    instTypeRestRG.add(gralInstClassR2);
	    instTypeRestRG.add(gralInstClassR3);
	    instTypeRestRG.add(gralInstClassR4);
	    instTypeRestRG.setOrientation(Orientation.HORIZONTAL);
	    instTypeRestRG.addListener(Events.Change, new Listener<BaseEvent>(){
	        public void handleEvent(BaseEvent be) {
	        	String selVal = instTypeRestRG.getValue().getValueAttribute();
	        	if(selVal.equals(InstanceTypeGeneral.NODE.toString())){
	        		CounterDef counterDef = new CounterDef(InstanceTypeGeneral.NODE, null); //new Vector<InstanceTypeSpecific>() // null == ANY type of nodes
	        		pattern.setCounterDefinition(counterDef);
	        	} else if(selVal.equals(InstanceTypeGeneral.LINK.toString())){
	        		CounterDef counterDef = new CounterDef(InstanceTypeGeneral.LINK, null); // null == ANY type of nodes
	        		pattern.setCounterDefinition(counterDef);
	        	} else if(selVal.equals(InstanceTypeGeneral.NODE_OR_LINK.toString())){
	        		CounterDef counterDef = new CounterDef(InstanceTypeGeneral.NODE_OR_LINK, null); // null == ANY type of nodes
	        		pattern.setCounterDefinition(counterDef);
	        	} else if(selVal.equals(InstanceTypeGeneral.PATTERN.toString())){
	        		CounterDef counterDef = new CounterDef(InstanceTypeGeneral.PATTERN, null); // null == ANY type of nodes
	        		pattern.setCounterDefinition(counterDef);
	        	}
	        	specificTypesField.setValue("");
	        }
	    });
	    
	    instTypeRestPanel.add(instTypeRestRG);
	    
	    final Button addSpecificTypesButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				//TODO validate if only specific types is checked, then empty vector, not check -> null
				Map<String, String> dualListOptions = new HashMap<String, String>();
				Map<String, String> selectedDualListOptions = new HashMap<String, String>();
				
				String instanceTypeGeneral = pattern.getCounterDefinition().getInstanceTypeGeneral().toString();
//				String selVal = instTypeRestRG.getValue().getValueAttribute();
				if(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE.toString())
						|| instanceTypeGeneral.equals(InstanceTypeGeneral.LINK.toString())
						|| instanceTypeGeneral.equals(InstanceTypeGeneral.NODE_OR_LINK.toString())){
					
					AgentDescriptionFE agent = getAgentInfo(); 
					SupportedOntologiesDef supOnt = agent.getSupportedOntology();
					List<String> ontList = supOnt.getSupportedOntologies();
					String ontologyId = null;
					if(ontList != null && ontList.size()>0){
						ontologyId = ontList.get(0);
					}
					if(FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology() == null){
						Ontology ontology = new Ontology(ontologyId);
						OntologyXML2ObjReader.parseOntology(ontology, FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getXml());
						FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).setOntology(ontology);
					}
					Ontology ontology = FeedbackAuthoringTabContent.getInstance().getOntologyDB(ontologyId).getOntology();
					
					List<String> elemTypes = new Vector<String>();
					if(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE.toString())){
						if(ontology.getNodeTypes() != null){
							elemTypes.addAll(ontology.getNodeTypes());
						}
					} else if(instanceTypeGeneral.equals(InstanceTypeGeneral.LINK.toString())){
						if(ontology.getLinkTypes() != null){
							elemTypes.addAll(ontology.getLinkTypes());
						}
					} else if(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE_OR_LINK.toString())){
						if(ontology.getNodeTypes() != null){
							elemTypes.addAll(ontology.getNodeTypes());
						}
						if(ontology.getLinkTypes() != null){
							elemTypes.addAll(ontology.getLinkTypes());
						}
					} 
					
					for(String elemType:elemTypes){
						dualListOptions.put(elemType, elemType);
					}
					
					List<InstanceTypeSpecific> selectedTypes = pattern.getCounterDefinition().getInstanceTypesSpecific();
					if(selectedTypes != null){
						for(InstanceTypeSpecific type: selectedTypes){
		        			if(type instanceof InstanceTypeSpecific_Object){
		        				 InstanceTypeSpecific_Object tmp = (InstanceTypeSpecific_Object)type;
		        				 if(dualListOptions.containsKey(tmp.getTypeID())){
		        					 selectedDualListOptions.put(tmp.getTypeID(), tmp.getTypeID());
		        				 } else{
		        					 FATDebug.print(FATDebug.ERROR,"ERROR [AgentWindow][createPatternsBottomPatCountPanel] patternId:" + tmp.getTypeID() + " was not found as part of ontology:" + ontology.getOntologyID()
		        							 + ", it was used as a specific type in a count pattern");
		        				 }
		        				 
		        			 }
		        		 }
					}
				}
	        	else if(instanceTypeGeneral.equals(InstanceTypeGeneral.PATTERN.toString())){
	        		 Collection<AnalysisType> patternList = getAgentInfo().getConfData().getAnalysisTypes();
	        		 for(AnalysisType pat: patternList){
	        			 if(pat.getResultDatatype() == AnalysisResultDatatype.object_binary_result){//object_binary_result only applies to structural patterns
	        				 dualListOptions.put(pat.getServiceID().getTypeID(), pat.getName());
	        			 }
	        		 }
	        		 
	        		 List<InstanceTypeSpecific> selectedTypes = pattern.getCounterDefinition().getInstanceTypesSpecific();
	        		 if(selectedTypes != null){
	        			 for(InstanceTypeSpecific type: selectedTypes){
		        			 if(type instanceof InstanceTypeSpecific_Pattern){
		        				 InstanceTypeSpecific_Pattern tmp = (InstanceTypeSpecific_Pattern)type;
		        				 if(dualListOptions.containsKey(tmp.getTypeID())){
		        					 String name = dualListOptions.get(tmp.getTypeID());
		        					 selectedDualListOptions.put(tmp.getTypeID(), name);
		        				 } else{
		        					 FATDebug.print(FATDebug.ERROR,"ERROR [AgentWindow][createPatternsBottomPatCountPanel]patternId:" + tmp.getTypeID() + " was not found as part of agentId:" + tmp.getAgentID()
		        							 + ", it was used as a specific type in a count pattern, check AgentWindow.createPatternsBottomPatCountPanel");
		        				 }
		        				 
		        			 }
		        		 }
	        		 }
	        		 
	        	}

				AddCountPatternSpecificTypesWindow win = new AddCountPatternSpecificTypesWindow(dualListOptions, selectedDualListOptions, pattern.getServiceID().getTypeID(), instanceTypeGeneral, AgentWindow.this);
				win.show();
			}
		};
		addSpecificTypesButton.setToolTip(FeedbackAuthoringStrings.ADD_SPECIFIC_TYPES_LABEL);
		addSpecificTypesButton.setIconStyle("icon-edit-gear");
		addSpecificTypesButton.setStyleName("xfa-btn");
		addSpecificTypesButton.setStyleName("x-btn-text", true);
		addSpecificTypesButton.setStyleName("x-btn", true);
		addSpecificTypesButton.setStylePrimaryName("xfa-btn");
	    
	    onlySpecificTypesCB = new CheckBox();
	    onlySpecificTypesCB.addListener(Events.Change, new Listener<BaseEvent>(){
	        public void handleEvent(BaseEvent be) {
	        	if (onlySpecificTypesCB.getValue()){
	        		//enabled
	        		specificTypesField.setEnabled(true);
	        		addSpecificTypesButton.setEnabled(true);
	        	} else{
	        		//disabled
	        		specificTypesField.setValue("");
	        		specificTypesField.setEnabled(false);
	        		addSpecificTypesButton.setEnabled(false);
	        		//pattern.getCounterDefinition().getInstanceTypesSpecific().clear();
	        		/*List<InstanceTypeSpecific> selectedTypes = pattern.getCounterDefinition().getInstanceTypesSpecific();
	        		selectedTypes = null; */
	        	}
	        }
	    });
		
	    
		
	    specificTypesField = new TextField<String>();
//	    specificTypesField.setFieldLabel(FeedbackAuthoringStrings.ONLY_SPECIFIC_TYPES_LABEL);
	    specificTypesField.setWidth(300);
	    specificTypesField.setReadOnly(true);
	    
	    if(pattern.getCounterDefinition().getInstanceTypesSpecific() == null ||
				pattern.getCounterDefinition().getInstanceTypesSpecific().size() == 0){
			onlySpecificTypesCB.setValue(false);
			specificTypesField.setEnabled(false);
			specificTypesField.setValue("");
			addSpecificTypesButton.setEnabled(false);
		} else{
			onlySpecificTypesCB.setValue(true);
			specificTypesField.setValue(getInstanceTypesSpecificAsString(pattern.getCounterDefinition().getInstanceTypesSpecific()));
			specificTypesField.setEnabled(true);
			addSpecificTypesButton.setEnabled(true);
		}
	    
	    
	    Label onlySpecificTypesLabel = new Label(FeedbackAuthoringStrings.ONLY_SPECIFIC_TYPES_LABEL+":");
	    onlySpecificTypesLabel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 4px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td1.setStyle("style='padding-left: 1px;'");
		
	    HorizontalPanel hp = new HorizontalPanel();
		hp.setAutoWidth(true);
		hp.add(onlySpecificTypesCB, td);
		hp.add(onlySpecificTypesLabel, td);
		hp.add(specificTypesField, td1);
		hp.add(addSpecificTypesButton, td1);
		instTypeRestPanel.add(hp);
//	    instTypeRestPanel.add(specificTypesField);

	    panel.add(instTypeRestPanel);
	    panel.addText(" ");
	    
	    /*
	     * Count condition
	     */	    
	    ContentPanel countCondPanel = new ContentPanel();
	    countCondPanel.setHeading(FeedbackAuthoringStrings.COUNT_CONDITION_HEADER);
	    countCondPanel.setBodyBorder(true);
	    countCondPanel.setWidth(500);
	    countCondPanel.setStyleAttribute("color", "#000000");
	    
	    ListStore<ElementModel> countCondStore = new ListStore<ElementModel>();
	    countCondStore.add(Data2ModelConverter.getStrAsModel(CounterCriterionOperatorTranslator.getOperatorStrs()));  

	    countCondCombo = new ComboBox<ElementModel>();
	    countCondCombo.setFieldLabel(FeedbackAuthoringStrings.COUNT_LABEL);
	    countCondCombo.setDisplayField(GridElementLabel.NAME);  
	    countCondCombo.setWidth(150);  
	    countCondCombo.setStore(countCondStore);  
	    countCondCombo.setTypeAhead(true);  
	    countCondCombo.setTriggerAction(TriggerAction.ALL);
	    
	    //set selected element
	    List<CounterCriterionDef> countConditionList = pattern.getCounterCriteria();
	    if(countCondStore.getCount() > 0){
	    	if(countConditionList.size() > 0){	    		
	    		Map<String, String> map = new HashMap<String, String>();
				map.put(GridElementLabel.NAME, CounterCriterionOperatorTranslator.translate(countConditionList.get(0).getOperator()));
				ElementModel currentElem = new ElementModel(map);
				countCondCombo.setValue(currentElem);
	    	} else{
	    		countCondCombo.setValue(countCondStore.getAt(0));
	    	}
	    }
	    countCondCombo.addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				ElementModel operElem = se.getSelectedItem();
				String oper = operElem.getValue(GridElementLabel.NAME);
				
				pattern.getCounterCriteria().clear();
				CounterCriterionDef crit =  new CounterCriterionDef(CounterCriterionOperatorTranslator.translate(oper), 0);
				pattern.getCounterCriteria().add(crit);
				countValueField.setValue(0);
			}
		});
	    
	    countValueField = new NumberField();
	    countValueField.setPropertyEditorType(Integer.class);
	    countValueField.setAllowDecimals(false);
	    countValueField.setAllowNegative(false);
	    countValueField.setMinValue(0);
	    countValueField.setLabelSeparator("");
	    countValueField.setAllowBlank(false);
	    countValueField.setWidth(50);
	    if(pattern.getCounterCriteria().size() > 0){
	    	CounterCriterionDef crit = pattern.getCounterCriteria().get(0);
	    	countValueField.setValue(crit.getReferenceValue());
	    } else{
	    	countValueField.setValue(0);
	    }
	    countValueField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		Integer newVal = (Integer) ((NumberField)event.getField()).getValue();
	    		if(newVal != null){
	    			ElementModel operElem = (ElementModel) countCondCombo.getValue();
	    			String oper = operElem.getValue(GridElementLabel.NAME);
					pattern.getCounterCriteria().clear();
					CounterCriterionDef crit =  new CounterCriterionDef(CounterCriterionOperatorTranslator.translate(oper), newVal);
					pattern.getCounterCriteria().add(crit);
	    		}
	    	}
	    });
	    
//	    TableData td = new TableData();
//	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
//	    td.setStyle("style='padding-left: 4px;'");

	    //this code groups the text field and the label
	    HorizontalPanel hpCountCond = new HorizontalPanel();
	    hpCountCond.add(countCondCombo, td);
	    hpCountCond.setAutoWidth(true);
	    hpCountCond.add(countValueField, td);
	    
	    countCondPanel.add(hpCountCond);
	    
	    panel.add(countCondPanel);
	    
	    
		return panel;
	}
	
	/*
	 * Updates the specific types text field and the pattern
	 */
	public void updateSpecificTypes(String patternId, String instanceTypeGeneral, Map<String, String> selectedOptions){
		//update textfield
		specificTypesField.setValue(getInstanceTypesSpecificAsString(selectedOptions));
		//update pattern
		CounterAnalysisType pattern = (CounterAnalysisType) getAgentInfo().getConfData().getAnalysisType(new ServiceID(getAgentInfo().getAgentID(), patternId, ServiceClass.ANALYSIS));
		
		if(selectedOptions.keySet().size() > 0){
			pattern.getCounterDefinition().setInstanceTypesSpecific(new Vector<InstanceTypeSpecific>());
			for(String type: selectedOptions.keySet()){
				InstanceTypeSpecific its = null;
				if(instanceTypeGeneral.equals(InstanceTypeGeneral.NODE.toString())
						|| instanceTypeGeneral.equals(InstanceTypeGeneral.LINK.toString())
						|| instanceTypeGeneral.equals(InstanceTypeGeneral.NODE_OR_LINK.toString())){
					its = new InstanceTypeSpecific_Object(type);
				} else if(instanceTypeGeneral.equals(InstanceTypeGeneral.PATTERN.toString())){
					its = new InstanceTypeSpecific_Pattern(getAgentInfo().getAgentID(), type);
				}
				pattern.getCounterDefinition().getInstanceTypesSpecific().add(its);
			}
		} else{
			pattern.getCounterDefinition().setInstanceTypesSpecific(null);
		}
	}
	
	private String getInstanceTypesSpecificAsString(Map<String, String> selectedOptions){
		StringBuffer retVal = new StringBuffer();
		
		boolean flag = false;
		for(String type: selectedOptions.keySet()){
			if(flag){
				retVal.append(", ");
			}
			flag = true;
			retVal.append("\"" + selectedOptions.get(type) + "\"");
		}
		return retVal.toString();
	}
	
	private String getInstanceTypesSpecificAsString(List<InstanceTypeSpecific> typesList){
		StringBuffer retVal = new StringBuffer();
		
		Map<String, String> patternMap = new HashMap<String, String>();
		Collection<AnalysisType> patternList = getAgentInfo().getConfData().getAnalysisTypes();
		 for(AnalysisType pat: patternList){
			 if(pat.getResultDatatype() == AnalysisResultDatatype.object_binary_result){//object_binary_result only applies to structural patterns
				 patternMap.put(pat.getServiceID().getTypeID(), pat.getName());
			 }
		 }
		
		boolean flag = false;
		for(InstanceTypeSpecific type: typesList){
			if(flag){
				retVal.append(", ");
			}
			flag = true;
			if(type instanceof InstanceTypeSpecific_Object){
				String id = ((InstanceTypeSpecific_Object)type).getTypeID();
				retVal.append("\""+ id + "\"");
			} else if(type instanceof InstanceTypeSpecific_Pattern){
				String id = ((InstanceTypeSpecific_Pattern)type).getTypeID();
				retVal.append("\"" + patternMap.get(id) + "\"");
			}
		}
		return retVal.toString();
	}
	
	private Widget createPatternsTopPanel(){
		GridConf gridConf = new GridConf();
		
		gridConf.setHeader(FeedbackAuthoringStrings.HEADING_PATTERN_DB);
		gridConf.setAddButtonFlag(true);
		//gridConf.setViewButtonFlag(true);
		//gridConf.setEditButtonFlag(true);
		gridConf.setDeleteButtonFlag(true);
		gridConf.setDuplicateButtonFlag(true);
		gridConf.setEnableSelection(true);
		gridConf.setCommonLabel(" "+FeedbackAuthoringStrings.PATTERN_LABEL);
		
		ColumnConf colTmp = new ColumnConf(GridElementLabel.NAME, FeedbackAuthoringStrings.NAME_LABEL, 140);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.TYPE, FeedbackAuthoringStrings.TYPE_LABEL, 120);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.INFO, FeedbackAuthoringStrings.INFO_LABEL, 120);
		gridConf.addColConfig(colTmp);

		colTmp = new ColumnConf(GridElementLabel.ACTIONS, FeedbackAuthoringStrings.ACTIONS_LABEL, 150);
		gridConf.addColConfig(colTmp);
		
		patternsGrid = new PatternsGrid(gridConf, this);
//		populatePatternGrid(patternsGrid);
		
		return patternsGrid;
	}
	
//	private void populatePatternGrid(PatternsGrid patternsGrid){
//		Collection<AnalysisType> patternList = newAgentInfo.getConfData().getAnalysisTypes();
//		for(AnalysisType pattern:patternList){
//			if(pattern instanceof StructureAnalysisType){
//				//patternsGrid.addPattern(pattern.getServiceID().getAgentID(), pattern.getServiceID().getTypeID(), FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL);
//				patternsGrid.addPattern2Grid(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.STRUCTURE_PATTERN_LABEL, "");
//				FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(newAgentInfo.getAgentID(), pattern.getServiceID().getTypeID());
//			} else if(pattern instanceof CounterAnalysisType){
//				//patternsGrid.addPattern(pattern.getServiceID().getAgentID(), pattern.getServiceID().getTypeID(), FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL);
//				patternsGrid.addPattern2Grid(pattern.getServiceID().getTypeID(), pattern.getName(), FeedbackAuthoringStrings.COUNTER_PATTERN_LABEL, "");
//				FeedbackAuthoringTabContent.getInstance().getPatternServerManager().addPattern(newAgentInfo.getAgentID(), pattern.getServiceID().getTypeID());
//			}  
//		}
//		
//		
//	}
	
	private ContentPanel getFeedbackTabItemContent(){
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		panel.add(createFeedbackTopPanel(), new RowData(1.0, 0.35, new Margins(3)));
		panel.add(createFeedbackBottomPanel(), new RowData(1.0, 0.65, new Margins(3)));
		
		return panel;
	}
	
	private Widget createFeedbackTopPanel(){
		GridConf gridConf = new GridConf();
		
		gridConf.setHeader(FeedbackAuthoringStrings.MESSAGE_DB_HEADING);
		gridConf.setAddButtonFlag(true);
		//gridConf.setViewButtonFlag(false);
		gridConf.setEditButtonFlag(false);
		gridConf.setDeleteButtonFlag(true);
		gridConf.setDuplicateButtonFlag(false);
		gridConf.setEnableSelection(true);
		gridConf.setCommonLabel(" " + FeedbackAuthoringStrings.MESSAGE_LABEL);
		
		ColumnConf colTmp = new ColumnConf("name", "Name", 160);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf("type", "Type", 150);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf("info", "Info", 200);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf("actions", "Actions", 150);
		gridConf.addColConfig(colTmp);
		
		feedbackGrid = new FeedbackGrid(gridConf, this);
		
		return feedbackGrid;
	}
	
	private Widget createFeedbackBottomPanel(){
		feedbackBottomPanel = new ContentPanel();
		feedbackBottomPanel.setHeaderVisible(false);
		feedbackBottomPanel.setBodyBorder(false);
		feedbackBottomPanel.setWidth("100%");
		feedbackBottomPanel.setHeight("100%");
		feedbackBottomPanel.setLayout(new FitLayout());
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_MESSAGE_FROM_LABEL));

		feedbackBottomPanel.add(panel);
		return feedbackBottomPanel;
	}	
	
	public void updateFeedbackBottomPanel(String elemId){
		
		feedbackBottomPanel.removeAll();
		feedbackBottomPanel.setLayout(new FitLayout());
		feedbackBottomPanel.add(createFeedbackBottomPanelItemSelected(elemId));
		feedbackBottomPanel.layout();
		
	}
	public void resetFeedbackBottomPanel(){
		feedbackBottomPanel.removeAll();
		feedbackBottomPanel.setLayout(new FitLayout());
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_MESSAGE_FROM_LABEL));
		feedbackBottomPanel.add(panel);
		feedbackBottomPanel.layout();
		
	}
	
	private Widget createFeedbackBottomPanelItemSelected(String elemId){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.setBodyBorder(true);
		
		TabPanel innerTabPanel = new TabPanel();
		innerTabPanel.setSize("100%", "100%");
		innerTabPanel.setMinTabWidth(115);
		innerTabPanel.setTabScroll(true);
		innerTabPanel.setStyleName("faSubTab");
		
		String agentId = this.getAgentInfo().getAgentID();
		ServiceID eID = new ServiceID(agentId, elemId, ServiceClass.ACTION);
		FeedbackActionType feedback = (FeedbackActionType) this.getAgentInfo().getConfData().getActionType(eID);
		
		if(feedback instanceof FeedbackActionType){
			panel.setHeading(FeedbackAuthoringStrings.MESSAGE_HEADING + " (" + (FeedbackAuthoringStrings.STD_MSG_LABEL) + ")");
			
			createFeedbackBottomBasicPanel(feedback);
			createFeedbackBottomContentPanel(feedback);
			createFeedbackBottomPriorityPanel(feedback);
			
			
		}

		TabItem basicTabItem = new TabItem();
		basicTabItem.setText(FeedbackAuthoringStrings.HEADING_BASIC);
		basicTabItem.setLayout(new FitLayout());
		basicTabItem.add(feedbackBasicPanel);
		
		TabItem contentTabItem = new TabItem();
		contentTabItem.setText(FeedbackAuthoringStrings.HEADING_CONTENT);
		contentTabItem.setLayout(new FitLayout());
		contentTabItem.add(feedbackContentPanel);
		
		TabItem priorityTabItem = new TabItem();
		priorityTabItem.setText(FeedbackAuthoringStrings.HEADING_PRIORITY + FeedbackAuthoringStrings.HEADING_OPT);
		priorityTabItem.setLayout(new FitLayout());
		priorityTabItem.add(feedbackPriorityPanel);
		
		innerTabPanel.add(priorityTabItem);
		innerTabPanel.add(contentTabItem);
		innerTabPanel.add(basicTabItem);
		
		innerTabPanel.setSelection(basicTabItem);
		panel.add(innerTabPanel);
		
		return panel;
	}
	
	private Widget createFeedbackBottomBasicPanel(final FeedbackActionType feedback){
		
		feedbackBasicPanel = new ContentPanel();
		feedbackBasicPanel.setHeaderVisible(false);
		feedbackBasicPanel.setBodyBorder(false);
		feedbackBasicPanel.setScrollMode(Scroll.AUTOY);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(650);
		formPanel.setLabelWidth(100);
		
		
		feedbackIdField = new TextField<String>();
		feedbackIdField.setFieldLabel(FeedbackAuthoringStrings.ID_LABEL);
		feedbackIdField.setEnabled(false);
		feedbackIdField.setValue(feedback.getServiceID().getTypeID());
		formPanel.add(feedbackIdField);
		
		feedbackNameField = new TextField<String>();
		feedbackNameField.setFieldLabel(FeedbackAuthoringStrings.NAME_LABEL);
		feedbackNameField.setAllowBlank(false);
		if(awc.getMode() == VIEW_MODE){
			feedbackNameField.setReadOnly(true);
		}
		if(feedback.getName() != null){
			feedbackNameField.setValue(feedback.getName());
		}
		feedbackNameField.addListener(Events.OnChange, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent event) {
				@SuppressWarnings("unchecked")
				String newVal = (String) ((TextField<String>)event.getField()).getValue();
				if(newVal != null && !newVal.equals("")){
					feedback.setName(newVal);
					
					//TODO update grid, check patterns
					String userSpecific = "";
					if(isUserSpecific(feedback)){
						userSpecific = FeedbackAuthoringStrings.USER_SPECIFIC_LABEL;
					}
					feedbackGrid.update(feedback.getServiceID().getTypeID(), newVal, FeedbackAuthoringStrings.STD_MSG_LABEL, userSpecific);
				}
			}
		});
		
	    formPanel.add(feedbackNameField);
	    
	    feedbackDescriptionTA = new TextArea();  
	    feedbackDescriptionTA.setPreventScrollbars(true);  
	    feedbackDescriptionTA.setFieldLabel(FeedbackAuthoringStrings.DESCRIPTION_LABEL);
	    feedbackDescriptionTA.setWidth(150);
	    feedbackDescriptionTA.setOriginalValue("");
	    if(awc.getMode() == VIEW_MODE){
	    	feedbackDescriptionTA.setReadOnly(true);
		}
	    if(feedback.getDescription() != null){
	    	feedbackDescriptionTA.setValue(feedback.getDescription());
		}
	    feedbackDescriptionTA.addListener(Events.OnChange, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent event) {
				String newVal = (String) ((TextArea)event.getField()).getValue();
				if(newVal != null){
					feedback.setDescription(newVal);
				}
			}
		});
	    
	    formPanel.add(feedbackDescriptionTA);
	    
	    feedbackBasicPatternDescLabel = new Label("= ");
		feedbackBasicPatternDescLabel.setStyleAttribute("color", "#000000");
	    
	    final Collection<AnalysisType> patternList = this.getAgentInfo().getConfData().getAnalysisTypes();
	    
	    ListStore<ElementModel> feedbackTriggerByStore = new ListStore<ElementModel>();
	    feedbackTriggerByStore.add(Data2ModelConverter.getPatternCollectionAsModelList(patternList));
	    feedbackTriggerByStore.sort(GridElementLabel.NAME, SortDir.ASC);
	  
	    feedbackTriggerByCombo = new ComboBox<ElementModel>();
	    feedbackTriggerByCombo.setFieldLabel(FeedbackAuthoringStrings.TRIGGER_BY_LABEL);
	    feedbackTriggerByCombo.setDisplayField(GridElementLabel.NAME);  
//	    feedbackTriggerByCombo.setAutoWidth(true);
	    feedbackTriggerByCombo.setStore(feedbackTriggerByStore);
	    feedbackTriggerByCombo.setTriggerAction(TriggerAction.ALL);
	    feedbackTriggerByCombo.setForceSelection(true);
	    
	    if(feedback.getTriggerID() != null){
	    	AnalysisType pattern = getAgentInfo().getConfData().getAnalysisType(feedback.getTriggerID());
	    	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, feedback.getTriggerID().getTypeID());
			map.put(GridElementLabel.NAME, pattern.getName());
			ElementModel elem = new ElementModel(map);
	    	feedbackTriggerByCombo.setValue(elem);
	    	updatePatternDescLabel(pattern.getDescription());
	    }
	    	
	    feedbackTriggerByCombo.addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				ElementModel pat = se.getSelectedItem();
				String patId = pat.getValue(GridElementLabel.ID);
				ServiceID patternId = new ServiceID(getAgentInfo().getAgentID(), patId, ServiceClass.ANALYSIS);
				AnalysisType pattern = getAgentInfo().getConfData().getAnalysisType(patternId);
				feedback.setTriggerID(patternId);
				updatePatternDescLabel(pattern.getDescription());

				String userSpecific = "";
				if(isUserSpecific(feedback)){
					userSpecific = FeedbackAuthoringStrings.USER_SPECIFIC_LABEL;
				}
				feedbackGrid.update(feedback.getServiceID().getTypeID(), feedback.getName(), FeedbackAuthoringStrings.STD_MSG_LABEL, userSpecific);
			}
		});
		
	    if(awc.getMode() == VIEW_MODE){
	    	feedbackTriggerByCombo.setReadOnly(true);
		}
	    formPanel.add(feedbackTriggerByCombo);
	    
	    //used in the Horizontal panels
	    TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 4px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td1.setStyle("style='padding-left: 1px;'");
		TableData td2 = new TableData();
		td2.setHorizontalAlign(HorizontalAlignment.LEFT);
		td2.setStyle("style='padding-left: 20px;'");
	    
	    Button feedbackTriggedHelpButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				MessageBox.info(FeedbackAuthoringStrings.HELP_LABEL, FeedbackAuthoringStrings.FEEDBACK_BASIC_TRIGGERED_HELP_LABEL, null);
			}
		};
		feedbackTriggedHelpButton.setToolTip(FeedbackAuthoringStrings.HELP_LABEL);
		feedbackTriggedHelpButton.setIconStyle("icon-question");
		feedbackTriggedHelpButton.setStyleName("xfa-btn");
		feedbackTriggedHelpButton.setStyleName("x-btn-text", true);
		feedbackTriggedHelpButton.setStyleName("x-btn", true);
		feedbackTriggedHelpButton.setStylePrimaryName("xfa-btn");
		
		//this code groups the label and the button
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.setAutoWidth(true);
		hp1.setStyleAttribute("color", "#000000");
		hp1.add(feedbackTriggedHelpButton, td2);
		hp1.add(feedbackBasicPatternDescLabel, td1);
		
		formPanel.add(hp1);
		
	    feedbackBasicNoteLabel = new Label(FeedbackAuthoringStrings.FEEDBACK_BASIC_INFO_LABEL);
	    feedbackBasicNoteLabel.setStyleAttribute("color", "#000000");
//	    formPanel.add(feedbackBasicNoteLabel);
	    
	    Button feedbackNoteHelpButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				MessageBox.info(FeedbackAuthoringStrings.HELP_LABEL, FeedbackAuthoringStrings.FEEDBACK_BASIC_INFO_HELP_LABEL, null);
			}
		};
		feedbackNoteHelpButton.setToolTip(FeedbackAuthoringStrings.HELP_LABEL);
		feedbackNoteHelpButton.setIconStyle("icon-question");
		feedbackNoteHelpButton.setStyleName("xfa-btn");
		feedbackNoteHelpButton.setStyleName("x-btn-text", true);
		feedbackNoteHelpButton.setStyleName("x-btn", true);
		feedbackNoteHelpButton.setStylePrimaryName("xfa-btn");
		
		//this code groups the label and the button
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setAutoWidth(true);
		hp2.setStyleAttribute("color", "#000000");
		hp2.add(feedbackBasicNoteLabel, td);
		hp2.add(feedbackNoteHelpButton, td1);
		
		formPanel.add(hp2);
	    
		feedbackBasicPanel.add(formPanel);
		
		return feedbackBasicPanel;
	}
	
	private void updatePatternDescLabel(String newText){
		if(newText != null){
			feedbackBasicPatternDescLabel.setText("= " + newText);
		} else{
			feedbackBasicPatternDescLabel.setText("= " + FeedbackAuthoringStrings.NO_DESC_AVAILABLE);
		}
	}
	
	private boolean isUserSpecific(FeedbackActionType fb){
		AnalysisType pattern = this.getAgentInfo().getConfData().getAnalysisType(fb.getTriggerID());
		//String userSpecific = "";
		if(pattern instanceof StructureAnalysisType){
			for(PatternFilterDef patDef : ((StructureAnalysisType)pattern).getFilterDefs()){
				if(patDef instanceof PatternFilterDef_User){
					//userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
					return ((PatternFilterDef_User)patDef).isUserSpecific();
				}
			}
		} else if(pattern instanceof CounterAnalysisType){
			for(PatternFilterDef patDef : ((CounterAnalysisType)pattern).getFilterDefs()){
				if(patDef instanceof PatternFilterDef_User){
//					userSpecific = ((PatternFilterDef_User)patDef).isUserSpecific()? FeedbackAuthoringStrings.USER_SPECIFIC_LABEL : "";
					return ((PatternFilterDef_User)patDef).isUserSpecific();
				}
			}
		}
		return false;
	}
	
	private Widget createFeedbackBottomContentPanel(final FeedbackActionType feedback){
		
		feedbackContentPanel = new ContentPanel();
		feedbackContentPanel.setHeaderVisible(false);
		feedbackContentPanel.setBodyBorder(false);
		feedbackContentPanel.setScrollMode(Scroll.AUTOY);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(450);
		formPanel.setLabelWidth(100);
		
		shortMsgContField = new TextField<String>();
		shortMsgContField.setFieldLabel(FeedbackAuthoringStrings.SHORT_MESSAGE_LABEL);
		shortMsgContField.setAllowBlank(false);
		shortMsgContField.addListener(Events.OnChange, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent event) {
				@SuppressWarnings("unchecked")
				String newVal = (String) ((TextField<String>)event.getField()).getValue();
				if(newVal != null && !newVal.equals("")){
					updateContentVal(new MsgCompDef_ShortText(newVal), newVal, feedback);
				}
			}
		});
		
		if(awc.getMode() == VIEW_MODE){
			shortMsgContField.setReadOnly(true);
		}
	    formPanel.add(shortMsgContField);
	    
	    longMsgContTA = new TextArea();  
	    longMsgContTA.setPreventScrollbars(false);  
	    longMsgContTA.setFieldLabel(FeedbackAuthoringStrings.LONG_MESSAGE_LABEL);
	    longMsgContTA.setAllowBlank(false);
	    longMsgContTA.addListener(Events.OnChange, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent event) {
				String newVal = (String) ((TextArea)event.getField()).getValue();
				if(newVal != null && !newVal.equals("")){
					updateContentVal(new MsgCompDef_LongText(newVal), newVal, feedback);
				}
			}
		});
		
	    if(awc.getMode() == VIEW_MODE){
	    	longMsgContTA.setReadOnly(true);
		}
	    formPanel.add(longMsgContTA);
	    
	    Radio highlightingR1 = new Radio();
	    highlightingR1.setStyleAttribute("color", "#000000");
	    highlightingR1.setBoxLabel(FeedbackAuthoringStrings.YES_LABEL);
	    highlightingR1.setValueAttribute(FeedbackAuthoringStrings.YES_LABEL);
	    Radio highlightingR2 = new Radio();
	    highlightingR2.setStyleAttribute("color", "#000000");
	    highlightingR2.setBoxLabel(FeedbackAuthoringStrings.NO_LABEL);
	    highlightingR2.setValueAttribute(FeedbackAuthoringStrings.NO_LABEL);
	    
	    highlightingContRG = new RadioGroup();
	    highlightingContRG.setFieldLabel(FeedbackAuthoringStrings.HIGHLIGHTING_LABEL);
	    highlightingContRG.add(highlightingR1);
	    highlightingContRG.add(highlightingR2);
	    highlightingContRG.addListener(Events.Change, new Listener<BaseEvent>(){
	        public void handleEvent(BaseEvent be) {
	        	String selVal = highlightingContRG.getValue().getValueAttribute();
	        	if(selVal.equals(FeedbackAuthoringStrings.YES_LABEL)){  //add highlighting
	        		updateContentVal(new MsgCompDef_Highlighting(), "", feedback);
	        	} else{  //remove highlighting
	        		updateContentVal(new MsgCompDef_Highlighting(), null, feedback);
	        	}
	        }
	    });
	    
	    /*
	     * set values to fields
	     */
	    boolean highlighting = false;
	    for(MsgCompDef msgCompDef : feedback.getMsgCompDefs()){
	    	if(msgCompDef instanceof MsgCompDef_ShortText){
	    		shortMsgContField.setValue(((MsgCompDef_ShortText)msgCompDef).getText());
	    	} else if(msgCompDef instanceof MsgCompDef_LongText){
	    		longMsgContTA.setValue(((MsgCompDef_LongText)msgCompDef).getText());
	    	} else if(msgCompDef instanceof MsgCompDef_Highlighting){
	    		highlighting = true;
	    	}
	    }
		if(highlighting){
			highlightingContRG.setValue(highlightingR1);
		} else {
			highlightingContRG.setValue(highlightingR2);
		}
	    
	    
	    formPanel.add(highlightingContRG);
	    
	    feedbackContentPanel.add(formPanel);
		
		return feedbackContentPanel;
	}
	
	private void updateContentVal(MsgCompDef msgCompDef, String newVal, FeedbackActionType feedback){
		MsgCompDef_ShortText shortText = null;
		MsgCompDef_LongText longText = null;
		MsgCompDef_Highlighting highlighting = null;
		boolean shortMsg = false, longMsg = false, highlight = false;
		
		for(MsgCompDef tmp : feedback.getMsgCompDefs()){
			if(tmp instanceof MsgCompDef_ShortText && msgCompDef instanceof MsgCompDef_ShortText){
				shortText = (MsgCompDef_ShortText) tmp;
				shortMsg = true;
				break;
			} else if(tmp instanceof MsgCompDef_LongText && msgCompDef instanceof MsgCompDef_LongText){
				longText = (MsgCompDef_LongText) tmp;
				longMsg = true;
				break;
			} else if(tmp instanceof MsgCompDef_Highlighting 
					&& msgCompDef instanceof MsgCompDef_Highlighting){
				highlighting = (MsgCompDef_Highlighting) tmp;
				highlight = true;
				break;
			}
		}
		if(shortMsg){
			feedback.getMsgCompDefs().remove(shortText);
			shortText = new MsgCompDef_ShortText(newVal);
			feedback.addMsgComp(shortText);
		} else if(longMsg){
			feedback.getMsgCompDefs().remove(longText);
			longText = new MsgCompDef_LongText(newVal);
			feedback.addMsgComp(longText);
		} else if(msgCompDef instanceof MsgCompDef_Highlighting) {
			if(newVal != null && !highlight){ // highlighting yes
				highlighting = new MsgCompDef_Highlighting();
				feedback.addMsgComp(highlighting);
			}else if(newVal == null){
				feedback.getMsgCompDefs().remove(highlighting);
			}
		}
	}
	
	private Widget createFeedbackBottomPriorityPanel(final FeedbackActionType feedback){
		feedbackPriorityPanel = new ContentPanel();
		feedbackPriorityPanel.setHeaderVisible(false);
		feedbackPriorityPanel.setBodyBorder(false);
		feedbackPriorityPanel.setScrollMode(Scroll.AUTOY);
		
		FormPanel defSetFormPanel = new FormPanel();
		defSetFormPanel.setHeading(FeedbackAuthoringStrings.DEFAULT_SETTING_LABEL);
		defSetFormPanel.setBodyBorder(true);
		defSetFormPanel.setWidth(450);

		Label noteLabel = new Label(FeedbackAuthoringStrings.PRIORITY_HELP_LABEL);
		
		Slider prioritySlider = new Slider();
	    prioritySlider.setMinValue(FATConstants.SLIDER_MIN_VAL);
	    prioritySlider.setMaxValue(FATConstants.SLIDER_MAX_VAL);
	    prioritySlider.setIncrement(FATConstants.SLIDER_INC_VAL);
	    prioritySlider.setMessage(FeedbackAuthoringStrings.HEADING_PRIORITY + " {0}");
	    prioritySlider.setValue(feedback.getPriorityDef().getDefaultPriority().intValue());
	    prioritySlider.addListener(Events.Change, new  Listener<SliderEvent>(){
	        public void handleEvent(SliderEvent be) {
	        	int selVal = be.getNewValue();
	        	feedback.getPriorityDef().setDefaultPriority(selVal);
	        }
	    });
	    
	    prioritySF = new SliderField(prioritySlider);
	    prioritySF.setFieldLabel(FeedbackAuthoringStrings.HEADING_PRIORITY);
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 4px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 1px;'");

		defSetFormPanel.add(prioritySF);
		defSetFormPanel.add(noteLabel);
		
		feedbackPriorityPanel.add(defSetFormPanel);
		
		feedbackPriorityPanel.add(createPhasePriorityGridPanel(feedback.getServiceID()));		
		
		return feedbackPriorityPanel;
	}
	
	private Widget createPhasePriorityGridPanel(ServiceID serviceID){
		GridConf gridConf = new GridConf();
		
		gridConf.setHeader(FeedbackAuthoringStrings.HEADING_PHASE_SPECIFIC_PRIORITY_DB);
		gridConf.setAddButtonFlag(true);
		gridConf.setDeleteButtonFlag(true);
		gridConf.setHideHeaders(false);
		gridConf.setCommonLabel(" "+FeedbackAuthoringStrings.PHASE_LABEL + " priority");
		gridConf.setWidth(450);
		gridConf.setHeight(240);
		
		ColumnConf colTmp = new ColumnConf(GridElementLabel.NAME, FeedbackAuthoringStrings.NAME_LABEL, 140);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.PRIORITY, FeedbackAuthoringStrings.PRIORITY_LABEL, 120);
		colTmp.setType(FATConstants.TEXT_TYPE);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.ACTIONS, FeedbackAuthoringStrings.ACTIONS_LABEL, 50);
		gridConf.addColConfig(colTmp);
		gridConf.setAutoExpandColumn(GridElementLabel.NAME);
		
		phasePriorityGrid = new PhasePriorityGrid(gridConf, this, serviceID);
		
		return phasePriorityGrid;
	}
	
	private ContentPanel getStrategyTabItemContent(){
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));
		panel.add(createStrategyTopPanel(), new RowData(1.0, 0.35, new Margins(3)));
		panel.add(createStrategyBottomPanel(), new RowData(1.0, 0.65, new Margins(3)));
		
		return panel;
	}
	
	private Widget createStrategyTopPanel(){
		GridConf gridConf = new GridConf();
		
		gridConf.setHeader(FeedbackAuthoringStrings.HEADING_STRATEGIES_DB);
		gridConf.setAddButtonFlag(true);
		//gridConf.setViewButtonFlag(true);
//		gridConf.setEditButtonFlag(true);
		gridConf.setDeleteButtonFlag(true);
//		gridConf.setDuplicateButtonFlag(true);
		gridConf.setEnableSelection(true);
		gridConf.setCommonLabel(" " + FeedbackAuthoringStrings.STRATEGY_LABEL);
		
		
		ColumnConf colTmp = new ColumnConf(GridElementLabel.NAME, FeedbackAuthoringStrings.NAME_LABEL, 160);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.TYPE, FeedbackAuthoringStrings.TYPE_LABEL, 150);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.INFO, FeedbackAuthoringStrings.INFO_LABEL, 200);
		gridConf.addColConfig(colTmp);
		colTmp = new ColumnConf(GridElementLabel.ACTIONS, FeedbackAuthoringStrings.ACTIONS_LABEL, 150);
		gridConf.addColConfig(colTmp);
		
		strategyGrid = new ControlThreadGrid(gridConf, this);
		
		return strategyGrid;
	}
	
	private Widget createStrategyBottomPanel(){
		strategyBottomPanel = new ContentPanel();
		strategyBottomPanel.setHeaderVisible(false);
		strategyBottomPanel.setBodyBorder(false);
		strategyBottomPanel.setWidth("100%");
		strategyBottomPanel.setHeight("100%");
		strategyBottomPanel.setLayout(new FitLayout());
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_STRATEGY_FROM_LABEL));
		strategyBottomPanel.add(panel);
		
		return strategyBottomPanel;
	}
	
	public void updateStrategyBottomPanel(String elemId){
		strategyBottomPanel.removeAll();
		strategyBottomPanel.setLayout(new FitLayout());
		strategyBottomPanel.add(createStrategyBottomPanelItemSelected(elemId));
		strategyBottomPanel.layout();
	}
	public void resetStrategyBottomPanel(){
		strategyBottomPanel.removeAll();
		strategyBottomPanel.setLayout(new FitLayout());
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.add(new Label(FeedbackAuthoringStrings.SELECT_STRATEGY_FROM_LABEL));
		strategyBottomPanel.add(panel);
		strategyBottomPanel.layout();
	}
	
	private Widget createStrategyBottomPanelItemSelected(String elemId){
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(true);
		panel.setBodyBorder(true);
		
		TabPanel innerTabPanel = new TabPanel();
		innerTabPanel.setSize("100%", "100%");
		innerTabPanel.setMinTabWidth(115);
		innerTabPanel.setTabScroll(true);
		innerTabPanel.setStyleName("faSubTab");
		
		String agentId = this.getAgentInfo().getAgentID();
		ServiceID sID = new ServiceID(agentId, elemId, ServiceClass.PROVISION);
		ProvisionType provision = this.getAgentInfo().getConfData().getProvisionTypes(sID);
		
		if(provision.getRecipient() != null && provision.getRecipient() instanceof RecipientDef_Individuals){
			panel.setHeading(FeedbackAuthoringStrings.STRATEGY_LABEL
								+ " (" + FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL + ")");
		} else if(provision.getRecipient() != null && provision.getRecipient() instanceof RecipientDef_Group){
			panel.setHeading(FeedbackAuthoringStrings.STRATEGY_LABEL 
					+ " (" + FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL + ")");
		}
		
		if(provision instanceof PriorityProvisionType){
			PriorityProvisionType prioProv = (PriorityProvisionType)provision;
			createProvisionBottomBasicPanel(prioProv);
			createProvisionBottomMessagesPanel(prioProv);
			createProvisionBottomSortAndFilterPanel(prioProv);
		} else { 
			//this should be coded once new "ProvisionType"s are created
		}
		
		TabItem basicTabItem = new TabItem();
		basicTabItem.setText(FeedbackAuthoringStrings.HEADING_BASIC);
		basicTabItem.setLayout(new FitLayout());
		basicTabItem.add(strategyBasicPanel);
		
		TabItem messagesTabItem = new TabItem();
		messagesTabItem.setText(FeedbackAuthoringStrings.HEADING_MESSAGES);
		messagesTabItem.setLayout(new FitLayout());
		messagesTabItem.add(strategyMessagesPanel);
		
		TabItem sortAndFilterTabItem = new TabItem();
		sortAndFilterTabItem.setText(FeedbackAuthoringStrings.HEADING_SORT_AND_FILTER + FeedbackAuthoringStrings.HEADING_OPT);
		sortAndFilterTabItem.setLayout(new FitLayout());
		sortAndFilterTabItem.add(strategySortAndFilterPanel);
		
		innerTabPanel.add(sortAndFilterTabItem);
		innerTabPanel.add(messagesTabItem);
		innerTabPanel.add(basicTabItem);
		
		innerTabPanel.setSelection(basicTabItem);
		panel.add(innerTabPanel);
		
		return panel;
		
	}
//	private Widget createProvisionBottomPanel(){
//		
//		ContentPanel panel = new ContentPanel();
//		panel.setLayout(new FitLayout());
//		panel.setHeaderVisible(true);
//		String headerTitle = FeedbackAuthoringStrings.STRATEGY_LABEL 
//							+ " (" + FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL + ")";
//		panel.setHeading(headerTitle);
//		panel.setBodyBorder(true);
//		
//		TabPanel innerTabPanel = new TabPanel();
//		innerTabPanel.setSize("100%", "100%");
//		innerTabPanel.setMinTabWidth(115);
//		innerTabPanel.setTabScroll(true);
//		innerTabPanel.setStyleName("faSubTab");		
//
//		TabItem basicTabItem = new TabItem();
//		basicTabItem.setText(FeedbackAuthoringStrings.HEADING_BASIC);
//		basicTabItem.setLayout(new FitLayout());
//		basicTabItem.add(createProvisionBottomBasicPanel(new PriorityProvisionType()));
//		
//		TabItem messagesTabItem = new TabItem();
//		messagesTabItem.setText(FeedbackAuthoringStrings.HEADING_MESSAGES);
//		messagesTabItem.setLayout(new FitLayout());
//		messagesTabItem.add(createProvisionBottomMessagesPanel(new PriorityProvisionType()));
//		
//		TabItem sortAndFilterTabItem = new TabItem();
//		sortAndFilterTabItem.setText(FeedbackAuthoringStrings.HEADING_SORT_AND_FILTER);
//		sortAndFilterTabItem.setLayout(new FitLayout());
//		sortAndFilterTabItem.add(createProvisionBottomSortAndFilterPanel(new PriorityProvisionType()));
//		
//		innerTabPanel.add(sortAndFilterTabItem);
//		innerTabPanel.add(messagesTabItem);
//		innerTabPanel.add(basicTabItem);
//		
//		innerTabPanel.setSelection(basicTabItem);
//		panel.add(innerTabPanel);
//		
//		return panel;
//	}
	
	final static int MIN_INTERVAL = 60;
	private Widget createProvisionBottomBasicPanel(final PriorityProvisionType item){
		strategyBasicPanel = new ContentPanel();
		strategyBasicPanel.setHeaderVisible(false);
		strategyBasicPanel.setBodyBorder(false);
		strategyBasicPanel.setScrollMode(Scroll.AUTOY);
		
//		ContentPanel tpanel = new ContentPanel();
//		tpanel.setHeaderVisible(false);
//		tpanel.setBodyBorder(false);
//		tpanel.setScrollMode(Scroll.AUTOY);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setWidth(450);
		formPanel.setLabelWidth(100);
		
		strategyIdField = new TextField<String>();
		strategyIdField.setFieldLabel(FeedbackAuthoringStrings.ID_LABEL);
		strategyIdField.setEnabled(false);
		strategyIdField.setValue(item.getServiceID().getTypeID());
		formPanel.add(strategyIdField);
		
		strategyNameField = new TextField<String>();
		strategyNameField.setFieldLabel(FeedbackAuthoringStrings.NAME_LABEL);
		strategyNameField.setWidth(50);
		strategyNameField.setAllowBlank(false);
		if(item.getName() != null){
			strategyNameField.setValue(item.getName());
		}
		strategyNameField.addListener(Events.OnChange, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent event) {
				@SuppressWarnings("unchecked")
				String newVal = (String) ((TextField<String>)event.getField()).getValue();
				if(newVal != null && !newVal.equals("")){
					//update data-structure
					item.setName(newVal);
					
					String recipient = item.getRecipient() instanceof RecipientDef_Group ? FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL : FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL;
					String provTime = item.getProvisionTime() instanceof ProvisionTimeDef_OnRequest ? FeedbackAuthoringStrings.ON_REQUEST_LABEL : FeedbackAuthoringStrings.PERIODICALLY_LABEL;
					//update grid
					strategyGrid.update(item.getServiceID().getTypeID(), item.getName(), recipient, provTime);
				}
			}
		});
		if(awc.getMode() == VIEW_MODE){
			strategyNameField.setReadOnly(true);
		}
	    formPanel.add(strategyNameField);
	    
	    strategyDescriptionTA = new TextArea();  
	    strategyDescriptionTA.setPreventScrollbars(false);  
	    strategyDescriptionTA.setFieldLabel(FeedbackAuthoringStrings.DESCRIPTION_LABEL);
	    strategyDescriptionTA.setOriginalValue("");
	    if(item.getDescription() != null){
	    	strategyDescriptionTA.setValue(item.getDescription());
	    }
		
	    if(awc.getMode() == VIEW_MODE){
	    	strategyDescriptionTA.setReadOnly(true);
		}
	    strategyDescriptionTA.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		String newVal = (String) ((TextArea)event.getField()).getValue();
	    		if(newVal != null){
	    			item.setDescription(newVal);
	    		}
	    	}
	    });
	    formPanel.add(strategyDescriptionTA);
	    strategyBasicPanel.add(formPanel);
	    
	    
	    Radio provTimeR1 = new Radio();
	    provTimeR1.setBoxLabel(FeedbackAuthoringStrings.PROVISION_TIME_OPT1_LABEL);
	    provTimeR1.setName(FeedbackAuthoringStrings.PROV_TIME_RADIO_LABEL);
	    provTimeR1.setId(FeedbackAuthoringStrings.PROV_TIME_RADIO_LABEL + FeedbackAuthoringStrings.ON_REQ_LABEL);
	    provTimeR1.setValueAttribute(FeedbackAuthoringStrings.ON_REQ_LABEL);
	    provTimeR1.setStyleAttribute("color", "#000000");
	    provTimeR1.addListener(Events.OnClick, new Listener<FieldEvent>() {              
			@Override
			public void handleEvent(FieldEvent be) {
				updateProvisionTime(FeedbackAuthoringStrings.ON_REQ_LABEL, FeedbackAuthoringStrings.GET_HINT_LABEL, item);
			}
		});
	    
	    Radio provTimeR2 = new Radio();
	    provTimeR2.setBoxLabel(FeedbackAuthoringStrings.PROVISION_TIME_OPT2_LABEL);
	    provTimeR2.setName(FeedbackAuthoringStrings.PROV_TIME_RADIO_LABEL);
	    provTimeR2.setId(FeedbackAuthoringStrings.PROV_TIME_RADIO_LABEL + FeedbackAuthoringStrings.PERIOD_LABEL);
	    provTimeR2.setValueAttribute(FeedbackAuthoringStrings.PERIOD_LABEL);
	    provTimeR2.setStyleAttribute("color", "#000000");
	    provTimeR2.addListener(Events.OnClick, new Listener<FieldEvent>() {              
			@Override
			public void handleEvent(FieldEvent be) {
				updateProvisionTime(FeedbackAuthoringStrings.PERIOD_LABEL, "60", item);
			}
		});
	    
	    strategyProvTimeDispNameField = new TextField<String>();
	    strategyProvTimeDispNameField.setAllowBlank(false);
	    strategyProvTimeDispNameField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		@SuppressWarnings("unchecked")
	    		String newVal = (String) ((TextField<String>)event.getField()).getValue();
	    		if(newVal != null){
	    			updateProvisionTime(FeedbackAuthoringStrings.ON_REQ_LABEL, newVal, item);
	    		}
	    	}
	    });
	    
	    strategyProvTimeInterField = new NumberField();
	    strategyProvTimeInterField.setPropertyEditorType(Integer.class);
	    strategyProvTimeInterField.setAllowDecimals(false);
	    strategyProvTimeInterField.setAllowNegative(false);
	    strategyProvTimeInterField.setMinValue(MIN_INTERVAL);
	    strategyProvTimeInterField.setWidth("60px");
	    strategyProvTimeInterField.setAllowBlank(false);
	    strategyProvTimeInterField.addListener(Events.OnChange, new Listener<FieldEvent>() {
	    	public void handleEvent(FieldEvent event) {
	    		Integer newVal = (Integer) event.getField().getValue();
	    		if(newVal != null){
	    			updateProvisionTime(FeedbackAuthoringStrings.PERIOD_LABEL, newVal.toString(), item);
	    		}
	    	}
	    });
	    
	    //setting current value for radio and related fields
	    if(item.getProvisionTime() != null && item.getProvisionTime() instanceof ProvisionTimeDef_Periodically){
	    	provTimeR2.setValue(true);
	    	ProvisionTimeDef_Periodically p = (ProvisionTimeDef_Periodically)item.getProvisionTime();
	    	if(p !=null && p.getCheckInterval() != null){
	    		strategyProvTimeInterField.setValue(p.getCheckInterval());
	    	} else{
	    		strategyProvTimeInterField.setValue(MIN_INTERVAL);
	    	}
	    	strategyProvTimeDispNameField.disable();
	    	strategyProvTimeDispNameField.setValue(FeedbackAuthoringStrings.GET_HINT_LABEL);
	    } else {
	    	provTimeR1.setValue(true);
	    	ProvisionTimeDef_OnRequest p = (ProvisionTimeDef_OnRequest)item.getProvisionTime();
	    	if(p !=null && p.getDisplayName() != null){
	    		strategyProvTimeDispNameField.setValue(p.getDisplayName());
	    	} else{
	    		strategyProvTimeDispNameField.setValue(FeedbackAuthoringStrings.GET_HINT_LABEL);
	    	}
	    	strategyProvTimeInterField.disable();
	    	strategyProvTimeInterField.setValue(MIN_INTERVAL);
	    }
	    
	    Label provTimeLabel = new Label(FeedbackAuthoringStrings.PROVISION_TIME_LABEL + ":");
	    provTimeLabel.setStyleAttribute("color", "#000000");
	    
	    //User restrictions
	    FormPanel radioPanel = new FormPanel();
		radioPanel.setLabelWidth(0);
		radioPanel.setLabelSeparator("");
		radioPanel.setHeaderVisible(false);
		radioPanel.setBodyBorder(false);
		radioPanel.setWidth(500);
		radioPanel.setStyleAttribute("color", "#000000");
	    
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
		hp.add(provTimeLabel, td);
		hp.add(provTimeR1, td1);
		radioPanel.add(hp);
	    
		Label onReqLabel = new Label(FeedbackAuthoringStrings.DISPLAY_NAME_LABEL + ":");
		onReqLabel.setStyleAttribute("color", "#000000");
	    
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.setAutoWidth(true);
		hp1.setStyleAttribute("color", "#000000");
		hp1.add(new Label(" "), td2);
		hp1.add(onReqLabel, td1);
		hp1.add(strategyProvTimeDispNameField, td1);
		radioPanel.add(hp1);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setAutoWidth(true);
		hp2.setStyleAttribute("color", "#000000");
		hp2.add(new Label(" "), td3);
		hp2.add(provTimeR2, td);
		radioPanel.add(hp2);
		
		Label periodLabel = new Label("Check interval every ");
		periodLabel.setStyleAttribute("color", "#000000");
		Label periodSecsLabel = new Label(FeedbackAuthoringStrings.SEC_LABEL);
		periodSecsLabel.setStyleAttribute("color", "#000000");
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.setAutoWidth(true);
		hp3.add(new Label(" "), td2);
		hp3.add(periodLabel, td1);
		hp3.add(strategyProvTimeInterField, td1);
		hp3.add(periodSecsLabel, td1);
		radioPanel.add(hp3);
	    
		strategyBasicPanel.add(radioPanel);
		
		return strategyBasicPanel;
	}
	
	private void updateProvisionTime(String selection, String value, ProvisionType item){
		ProvisionTimeDef prov = null;
		if(FeedbackAuthoringStrings.ON_REQ_LABEL.equals(selection)){
			if(isStrategyDisplayNameUnique(item, value)){
				//prov = new ProvisionTimeDef_OnRequest(FeedbackAuthoringStrings.GET_HINT_LABEL);
				prov = new ProvisionTimeDef_OnRequest(value);
//				strategyProvTimeDispNameField.setValue(value);
				strategyProvTimeInterField.setValue(MIN_INTERVAL);
				strategyProvTimeDispNameField.enable();
				strategyProvTimeInterField.disable();
			} else{
				MessageBox.info(FeedbackAuthoringStrings.ERROR_LABEL,
						FeedbackAuthoringStrings.DISPLAY_NAME_IS_BEING_USED, null);
			}
		} else if(FeedbackAuthoringStrings.PERIOD_LABEL.equals(selection)){
//			prov = new ProvisionTimeDef_Periodically(0L);
			prov = new ProvisionTimeDef_Periodically(Long.valueOf(value));
//			strategyProvTimeInterField.setValue(Integer.valueOf(value));
			strategyProvTimeDispNameField.setValue(FeedbackAuthoringStrings.GET_HINT_LABEL);
			strategyProvTimeDispNameField.disable();
			strategyProvTimeInterField.enable();
		}
		//update data-structure
		item.setProvisionTime(prov);
		
		//update grid
		String recipient = item.getRecipient() instanceof RecipientDef_Group ? FeedbackAuthoringStrings.GROUP_SUPPORT_STRATEGY_LABEL : FeedbackAuthoringStrings.INDIVIDUAL_SUPPORT_STRATEGY_LABEL;
		String provTime = item.getProvisionTime() instanceof ProvisionTimeDef_OnRequest ? FeedbackAuthoringStrings.ON_REQUEST_LABEL : FeedbackAuthoringStrings.PERIODICALLY_LABEL;
		strategyGrid.update(item.getServiceID().getTypeID(), item.getName(), recipient, provTime);
	}
	
	/*
	 * Check if a new Strategy Display Name is unique within the agent
	 */
	private boolean isStrategyDisplayNameUnique(ProvisionType item, String newName){
		boolean retVal = true;
		List<ProvisionType> provisionList = new Vector<ProvisionType>(this.getAgentInfo().getConfData().getProvisionTypes());
		for(ProvisionType prov : provisionList){
			if(prov.getProvisionTime() instanceof ProvisionTimeDef_OnRequest
					&& prov != item
					&& ((ProvisionTimeDef_OnRequest)prov.getProvisionTime()).getDisplayName().equalsIgnoreCase(newName)){
				retVal = false;
				break;
			}
		}
		return retVal;
	}
	
	private Widget createProvisionBottomMessagesPanel(final PriorityProvisionType provision){
		
		strategyMessagesPanel = new ContentPanel();
		strategyMessagesPanel.setHeaderVisible(false);
		strategyMessagesPanel.setBodyBorder(false);
		strategyMessagesPanel.setScrollMode(Scroll.AUTOY);
		strategyMessagesPanel.setWidth(400);
		strategyMessagesPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		
//		ContentPanel formPanel = new ContentPanel();
//		formPanel.setHeaderVisible(false);
//		formPanel.setBodyBorder(false);
//		formPanel.setWidth(500);
//		formPanel.setLayout(new FitLayout());
		
		Label availMsgTypesLabel = new Label(FeedbackAuthoringStrings.AVAILABLE_MSG_TYPES_LABEL);
		availMsgTypesLabel.setStyleAttribute("color", "#000000");
	    Label selMsgTypesLabel = new Label(FeedbackAuthoringStrings.SELECTED_MSG_TYPES_LABEL);
	    selMsgTypesLabel.setStyleAttribute("color", "#000000");
	    
	    TableData td = new TableData();
	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td.setStyle("style='padding-left: 1px;'");
	    TableData td1 = new TableData();
	    td1.setHorizontalAlign(HorizontalAlignment.LEFT);
	    td1.setStyle("style='padding-left: 100px;'");

	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setAutoWidth(true);
	    hp.setHeight(20);
	    hp.add(availMsgTypesLabel, td);
	    hp.add(new Label(""), td1);
	    hp.add(selMsgTypesLabel, td);
	    //formPanel.add(hp);
	    strategyMessagesPanel.add(hp, new RowData(0.5, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
	    Vector<Integer> buttonsToRemoveList = new Vector<Integer>();
	    buttonsToRemoveList.add(CustomDualListField.ALL_LEFT_BUTTON);
	    buttonsToRemoveList.add(CustomDualListField.ALL_RIGHT_BUTTON);
	    buttonsToRemoveList.add(CustomDualListField.UP_BUTTON);
	    buttonsToRemoveList.add(CustomDualListField.DOWN_BUTTON);

	    messagesDualList = new CustomDualListField<ElementModel>(buttonsToRemoveList);
	    messagesDualList.setMode(CustomDualListField.Mode.INSERT);
	    strategyMessagesPanel.add(messagesDualList, new RowData(0.5, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
//	    messagesDualList.addListener(CustomDualListFieldEventType.MoveSelectedDown, new CustomDualListFieldListener(){
//	    	@Override
//	    	public void moveSelectedDown(CustomDualListFieldEvent e){
//	    		updateStrategyMsgs(provision, messagesDualList.getToList().getStore());
//	    	}
//	    });
//	    messagesDualList.addListener(CustomDualListFieldEventType.MoveSelectedUp, new CustomDualListFieldListener(){
//	    	@Override
//	    	public void moveSelectedUp(CustomDualListFieldEvent e){
//	    		updateStrategyMsgs(provision, messagesDualList.getToList().getStore());
//	    	}
//	    });
	    messagesDualList.addListener(CustomDualListFieldEventType.AddSelected, new CustomDualListFieldListener(){
	    	@Override
	    	public void addSelected(CustomDualListFieldEvent e){
	    		updateStrategyMsgs(provision, messagesDualList.getToList().getStore());
	    	}
	    });
	    messagesDualList.addListener(CustomDualListFieldEventType.RemoveSelected, new CustomDualListFieldListener(){
	    	@Override
	    	public void removeSelected(CustomDualListFieldEvent e){
	    		updateStrategyMsgs(provision, messagesDualList.getToList().getStore());
	    	}
	    });

	    List<ServiceID> provActionList = provision.getProvidedActions().getServiceIDs();
		Collection<ActionType> allActions = this.getAgentInfo().getConfData().getActionTypes();
		List<ActionType> notSelectedActions = new Vector<ActionType>();
		List<ActionType> selectedActions = new Vector<ActionType>();
		
	    for(ActionType action:allActions){
	    	//only show messages that are equal to the strategy in the case of group strategies.
	    	//Individual strategies can use both types
//	    	if((provision.getRecipient() instanceof RecipientDef_Individuals)
//	    			&& !isUserSpecific((FeedbackActionType)action)){
//	    		continue;
//			} else 
			if((provision.getRecipient() instanceof RecipientDef_Group)
	    			&& isUserSpecific((FeedbackActionType)action)){
				continue;
			}
	    	if(!provActionList.contains(action.getServiceID())){
	    		notSelectedActions.add(action);
	    	} else {
	    		selectedActions.add(action);
	    	}
	    }

	    ListStore<ElementModel> leftStore = new ListStore<ElementModel>();
	    ListStore<ElementModel> rightStore = new ListStore<ElementModel>();

	    ListField<ElementModel> leftList = messagesDualList.getFromList();
	    leftList.setStore(leftStore);
	    leftList.setDisplayField(GridElementLabel.NAME);
	    leftList.setWidth(165);
	    leftStore.add(Data2ModelConverter.getMsgListAsModelList(notSelectedActions));

	    ListField<ElementModel> rightList = messagesDualList.getToList();
	    rightList.setStore(rightStore);
	    rightList.setDisplayField(GridElementLabel.NAME);
	    rightList.setWidth(165);

	    rightStore.add(Data2ModelConverter.getMsgListAsModelList(selectedActions));
		
		return strategyMessagesPanel;
	}
	
	private void updateStrategyMsgs(PriorityProvisionType provision, ListStore<ElementModel> tmpStore){
		if(tmpStore != null){
			provision.getProvidedActions().getServiceIDs().clear();			
			String agentId = this.getAgentInfo().getAgentID();
		    for(ElementModel elem:tmpStore.getModels()){
		    	ServiceID sID = new ServiceID(agentId,  elem.getValue(GridElementLabel.ID), ServiceClass.ACTION);
		    	provision.getProvidedActions().getServiceIDs().add(sID);
		    }
		}
	}
	
	private Widget createProvisionBottomSortAndFilterPanel(final PriorityProvisionType provision){
		
		strategySortAndFilterPanel = new ContentPanel();
		strategySortAndFilterPanel.setHeaderVisible(false);
		strategySortAndFilterPanel.setBodyBorder(false);
		strategySortAndFilterPanel.setScrollMode(Scroll.AUTOY);
		
		//Max Number of messages provided at a time
		FormPanel generalFormPanel = new FormPanel();
		generalFormPanel.setHeaderVisible(false);
		generalFormPanel.setBodyBorder(false);
		generalFormPanel.setWidth(600);
		generalFormPanel.setLabelWidth(260);
		generalFormPanel.setFieldWidth(40);
		
		maxNumberMsgField = new SpinnerField();
		maxNumberMsgField.setFieldLabel(FeedbackAuthoringStrings.MAX_NUM_MSGS_PROV_LABEL);
	    maxNumberMsgField.setIncrement(1);  
	    maxNumberMsgField.getPropertyEditor().setType(Integer.class);  
	    maxNumberMsgField.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());  
	    maxNumberMsgField.setMinValue(1);  
	    maxNumberMsgField.setMaxValue(20);
	    maxNumberMsgField.setValue(provision.getMaxNumResults());
	    maxNumberMsgField.setWidth(40);
	    maxNumberMsgField.addListener(Events.Change, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				Number value = maxNumberMsgField.getValue();
				provision.setMaxNumResults(value.intValue());
			}
		});
	    
	    generalFormPanel.add(maxNumberMsgField);
	    
	    strategySortAndFilterPanel.add(generalFormPanel);  
		
		//Sort Messages
	    ContentPanel sortMsgsFormPanel = new ContentPanel();
		sortMsgsFormPanel.setHeading(FeedbackAuthoringStrings.SORT_MSGS_LABEL);
		sortMsgsFormPanel.setBodyBorder(true);
		sortMsgsFormPanel.setWidth(500);
//		sortMsgsFormPanel.setLayout(new FitLayout());
		sortMsgsFormPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		
		Label availSortCritLabel  = new Label(FeedbackAuthoringStrings.AVAILABLE_SORT_CRIT_LABEL);
		availSortCritLabel.setStyleAttribute("color", "#000000");
	    Label selSortCritLabel  = new Label(FeedbackAuthoringStrings.SELECTED_SORT_CRIT_LABEL);
	    selSortCritLabel.setStyleAttribute("color", "#000000");

	    Button selSortCritHelpButton = new Button(){
			@Override
			protected void onClick(ComponentEvent ce) {
				MessageBox.info(FeedbackAuthoringStrings.HELP_LABEL, FeedbackAuthoringStrings.SELECTED_SORT_CRIT_HELP_LABEL, null);
			}
		};
		selSortCritHelpButton.setToolTip(FeedbackAuthoringStrings.HELP_LABEL);
		selSortCritHelpButton.setIconStyle("icon-question");
		selSortCritHelpButton.setStyleName("xfa-btn");
		selSortCritHelpButton.setStyleName("x-btn-text", true);
		selSortCritHelpButton.setStyleName("x-btn", true);
		selSortCritHelpButton.setStylePrimaryName("xfa-btn");
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setStyle("style='padding-left: 1px;'");
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		td1.setStyle("style='padding-left: 100px;'");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setAutoWidth(true);
		hp.setHeight(20);
		hp.add(availSortCritLabel, td);
		hp.add(new Label(""), td1);
		hp.add(selSortCritLabel, td);
		hp.add(selSortCritHelpButton, td);
//		sortMsgsFormPanel.add(hp);
		sortMsgsFormPanel.add(hp, new RowData(1, Style.DEFAULT, new Margins(0, 4, 0, 4)));
	    
		Vector<Integer> buttonsToRemoveList = new Vector<Integer>();
		buttonsToRemoveList.add(CustomDualListField.ALL_LEFT_BUTTON);
		buttonsToRemoveList.add(CustomDualListField.ALL_RIGHT_BUTTON);
		
		sortCriteriaDualList = new CustomDualListField<ElementModel>(buttonsToRemoveList);
		sortCriteriaDualList.setMode(CustomDualListField.Mode.INSERT);
		sortMsgsFormPanel.add(sortCriteriaDualList, new RowData(0.9, Style.DEFAULT, new Margins(0, 4, 0, 4)));
		sortCriteriaDualList.setWidth(400);
		sortCriteriaDualList.addListener(CustomDualListFieldEventType.MoveSelectedDown, new CustomDualListFieldListener(){
			@Override
			public void moveSelectedDown(CustomDualListFieldEvent e){
				updateMsgSortCriterion(provision, sortCriteriaDualList.getToList().getStore());
			}
		});
		sortCriteriaDualList.addListener(CustomDualListFieldEventType.MoveSelectedUp, new CustomDualListFieldListener(){
			@Override
			public void moveSelectedUp(CustomDualListFieldEvent e){
				updateMsgSortCriterion(provision, sortCriteriaDualList.getToList().getStore());
			}
		});
		sortCriteriaDualList.addListener(CustomDualListFieldEventType.AddSelected, new CustomDualListFieldListener(){
			@Override
			public void addSelected(CustomDualListFieldEvent e){
				updateMsgSortCriterion(provision, sortCriteriaDualList.getToList().getStore());
			}
		});
		sortCriteriaDualList.addListener(CustomDualListFieldEventType.RemoveSelected, new CustomDualListFieldListener(){
			@Override
			public void removeSelected(CustomDualListFieldEvent e){
				updateMsgSortCriterion(provision, sortCriteriaDualList.getToList().getStore());
			}
		});

		List<MsgSortCriterion> allMsgList = MsgSortCriterionUtil.getAllMsgSortCriterionAvailable();
		List<MsgSortCriterion> sortCriteriaList = provision.getSortCriteriaInOrder();
		
		for(MsgSortCriterion msgSortCrit:sortCriteriaList){
			allMsgList.remove(msgSortCrit);
		}
		
		ListStore<ElementModel> leftStore = new ListStore<ElementModel>();
		ListStore<ElementModel> rightStore = new ListStore<ElementModel>();
		
		ListField<ElementModel> leftList = sortCriteriaDualList.getFromList();
		leftList.setStore(leftStore);
		leftList.setDisplayField(GridElementLabel.NAME);
		leftList.setWidth(165);
		leftStore.add(Data2ModelConverter.getMsgSortCriterionListAsModelList(allMsgList));
		
        ListField<ElementModel> rightList = sortCriteriaDualList.getToList();
        rightList.setStore(rightStore);
        rightList.setDisplayField(GridElementLabel.NAME);
        rightList.setWidth(165);
        
        rightStore.add(Data2ModelConverter.getMsgSortCriterionListAsModelList(sortCriteriaList));

        strategySortAndFilterPanel.add(sortMsgsFormPanel);
		
		//Filter Messages
		FormPanel filterMsgsFormPanel = new FormPanel();
		filterMsgsFormPanel.setHeading(FeedbackAuthoringStrings.FILTER_MSGS_LABEL);
		filterMsgsFormPanel.setBodyBorder(true);
		filterMsgsFormPanel.setWidth(500);
		filterMsgsFormPanel.setLabelWidth(0);
		filterMsgsFormPanel.setLabelSeparator("");
		
		filterMsgOpt1CB = new CheckBox();
		filterMsgOpt1CB.setBoxLabel(FeedbackAuthoringStrings.FILTER_MSGS_OPT1_LABEL);
		filterMsgOpt1CB.addListener(Events.OnChange, new Listener<FieldEvent>(){
			 
			@Override public void handleEvent(FieldEvent be) { 
				CheckBox check = (CheckBox)be.getSource();
				//Info.display("Event", check.getBoxLabel() + " " + (check.getValue() ? "selected" : "not selected"));
				if(check.getValue()){
					provision.getFilterDefs().add(new MsgFilterDef_DiscardAllButOneInstancePerType());
				} else {
					provision.getFilterDefs().remove(new MsgFilterDef_DiscardAllButOneInstancePerType());
				}
				
			}
		});
		
		filterMsgOpt2CB = new CheckBox();
		filterMsgOpt2CB.setBoxLabel(FeedbackAuthoringStrings.FILTER_MSGS_OPT2_LABEL);
		filterMsgOpt2CB.addListener(Events.OnChange, new Listener<FieldEvent>(){
			 
			@Override public void handleEvent(FieldEvent be) { 
				CheckBox check = (CheckBox)be.getSource();
				if(check.getValue()){
					provision.getFilterDefs().add(new MsgFilterDef_DiscardAllButOneInstancePerGroup());
				} else {
					provision.getFilterDefs().remove(new MsgFilterDef_DiscardAllButOneInstancePerGroup());
				}
				
			}
		});
		
		filterMsgOpt3CB = new CheckBox();
		filterMsgOpt3CB.setBoxLabel(FeedbackAuthoringStrings.FILTER_MSGS_OPT3_LABEL);
		filterMsgOpt3CB.addListener(Events.OnChange, new Listener<FieldEvent>(){
		 
			@Override public void handleEvent(FieldEvent be) { 
				CheckBox check = (CheckBox)be.getSource();
				if(check.getValue()){
					provision.getFilterDefs().add(new MsgFilterDef_DiscardInstancesAlreadyPointedTo());
				} else {
					provision.getFilterDefs().remove(new MsgFilterDef_DiscardInstancesAlreadyPointedTo());
				}
				
			}
		});
		
		for(MsgFilterDef filter:provision.getFilterDefs()){
			if(filter instanceof MsgFilterDef_DiscardAllButOneInstancePerType){
				filterMsgOpt1CB.setValue(true);
			} else if(filter instanceof MsgFilterDef_DiscardAllButOneInstancePerGroup){
				filterMsgOpt2CB.setValue(true);
			} else if(filter instanceof MsgFilterDef_DiscardInstancesAlreadyPointedTo){
				filterMsgOpt3CB.setValue(true);
			}
		}
	    
		filterMsgsFormPanel.add(filterMsgOpt1CB, formData);
//		filterMsgsFormPanel.add(filterMsgOpt2CB, formData);
		filterMsgsFormPanel.add(filterMsgOpt3CB, formData);
		
		strategySortAndFilterPanel.add(filterMsgsFormPanel);
		
		return strategySortAndFilterPanel;
	}
	
	private void updateMsgSortCriterion(PriorityProvisionType provision, ListStore<ElementModel> tmpStore){
		if(tmpStore != null){
			provision.getSortCriteriaInOrder().clear();
			for(ElementModel elem: tmpStore.getModels()){
				provision.getSortCriteriaInOrder().add(MsgSortCriterionUtil.getMsgSortCriterionAsObj(elem.getValue(GridElementLabel.ID)));
			}
		}
	} 
	
	private void saveAgentInfo(){
		newAgentInfo.setDisplayName(agentNameField.getValue());
		
		ElementModel value = (ElementModel) supportedOntCombo.getValue();
		SupportedOntologiesDef supOnt = new SupportedOntologiesDef();
		supOnt.setAllOntologies(false);
		List<String> ontList = new Vector<String>();
		ontList.add((String)value.get("name"));
		supOnt.setSupportedOntologies(ontList);
		newAgentInfo.setSupportedOntology(supOnt);
		newAgentInfo.setDescription(agentDescriptionTA.getValue());
		newAgentInfo.setConfReadable(true);
		newAgentInfo.setConfWritable(true);

	}
	
	
	public AgentDescriptionFE getAgentInfo() {
		return newAgentInfo;
	}

}
