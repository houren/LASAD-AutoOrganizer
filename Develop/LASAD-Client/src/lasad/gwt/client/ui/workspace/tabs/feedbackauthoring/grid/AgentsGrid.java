package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.List;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.OntologyFA;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ToasterMessage;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.AddUpdateAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.CompileAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.CopyAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent.DeleteAgentEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AddAgentWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindow;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.AgentWindowConf;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Creates the agents panel in the feedback authoring tool main window.
 * @author Anahuac
 *
 */
public class AgentsGrid extends CustomizedGrid { 
	
	private static AgentsGrid instance = null;
	private FeedbackAuthoringTabContent faTab;
	
	public static AgentsGrid getInstance(GridConf gridConf, FeedbackAuthoringTabContent faTab) {
		if (instance == null) {
			instance = new AgentsGrid(gridConf, faTab);
		}
		return instance;
	}
	
	private AgentsGrid(GridConf gridConf, FeedbackAuthoringTabContent faTab) {
		super(gridConf);
		this.faTab = faTab;
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	}
	
	@Override
	void markReadyElement(String id) {
		AgentDescriptionFE agent = faTab.getAgentFromDB(id);
		faTab.handleUserActionEvent(new CompileAgentEvent(id, agent));
	}

	@Override
	void viewElement(String id) {
		AgentDescriptionFE agent = faTab.getAgentFromDB(id);
		 FeedbackAuthoringTabContent.getInstance().updatePatternManipulatorMap(agent);
		AgentWindow agentAddWin = new AgentWindow(getAgWinConf4ViewAg(), agent, this);
		agentAddWin.show();
		
	}
	private AgentWindowConf getAgWinConf4ViewAg(){
		AgentWindowConf awc = new AgentWindowConf(FeedbackAuthoringStrings.VIEW_AGENT_LABEL,  
												AgentWindow.VIEW_MODE,
												new Vector<String>(faTab.getOntologiesDB()));
		return awc;
	}

	/* 
	 * Edit agent, Agent Window is prompted
	 * Method call when the edit agent button is pressed
	 * (non-Javadoc)
	 * @see lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.CustomizedGrid#editElement(java.lang.String)
	 */
	@Override
	void editElement(String id) {
		 //Info.display("Edit", id);
		 AgentDescriptionFE agent = faTab.getAgentFromDB(id);
		 FeedbackAuthoringTabContent.getInstance().updatePatternManipulatorMap(agent);
		 AgentWindow agentAddWin = new AgentWindow(getAgWinConf4EditAg(), agent, this);
		 agentAddWin.show();
	}
	private AgentWindowConf getAgWinConf4EditAg(){
		AgentWindowConf awc = new AgentWindowConf(FeedbackAuthoringStrings.EDIT_AGENT_LABEL,  
												AgentWindow.EDIT_MODE,
												new Vector<String>(faTab.getOntologiesDB()));
		return awc;
	}

	/*
	 * Deletes an agent from the GUI and the DB
	 * Method call when the delete agent button is pressed
	 * (non-Javadoc)
	 * @see lasad.gwt.feedbackauthoringtool.client.grid.CustomizedGrid#deleteElement(java.lang.String)
	 */
	@Override
	void deleteElement(final String id, String name) {
		//Initialize message box
		MessageBox box = new MessageBox();
		box.setButtons(MessageBox.YESNO);
		box.setIcon(MessageBox.QUESTION);
		box.setTitle(FeedbackAuthoringStrings.DELETE_AGENT_LABEL);
		box.setMessage(FeedbackAuthoringStrings.DELETE_LABEL + " " + name + FeedbackAuthoringStrings.QUESTION_MARK);
		box.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					FATDebug.print(FATDebug.DEBUG, "[AgentsGrid][deleteElement][OK]");
					faTab.handleUserActionEvent(new DeleteAgentEvent(id));
				}
			}
		});
	  	box.show();
	}

	/*
	 * Duplicates an agent
	 * Method call when the duplicate agent button is pressed
	 * (non-Javadoc)
	 * @see lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.CustomizedGrid#duplicateElement(java.lang.String)
	 */
	@Override
	void duplicateElement(String id) {
		faTab.handleUserActionEvent(new CopyAgentEvent(id));
//		AgentDescriptionFE agentDesc = faTab.getAgentFromDB(id);
//		String newName = CloneAgentDescriptionFE.getNewName(agentDesc.getDisplayName());
//		faTab.doGetFreshAgentId(newName, null, id);
		
		/*
		 * Code for testing
		 */
//		AgentDescriptionFE duplicatedAgent = CloneAgentDescriptionFE.doCloning("agent-" + IdGenerator.getNewAgentId(), newName, agentDesc);
//		FATDebug.print(FATDebug.DEBUG, "[AgentsGrid][duplicateElement] result after duplicating " + agentDesc.getDisplayName() + ":");
//		FATDebug.print(FATDebug.DEBUG, "[AgentsGrid][duplicateElement] duplicated agent:" + duplicatedAgent);
	}

	/*
	 * Add agent, , Agent Window is prompted so the user can provide the agent conf
	 * Method is called when the add agent button is pressed
	 * (non-Javadoc)
	 * @see lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.CustomizedGrid#addElement()
	 */
	@Override
	void addElement() {
		AddAgentWindow addAgentWindow = new AddAgentWindow(this, new Vector<String>(faTab.getOntologiesDB()));
		addAgentWindow.show();
	}
	
	/*
	 * Method called from AddAgentWindow save button
	 */
	public void saveAgent(String agentName, String ontology){
		faTab.doGetFreshAgentId(agentName, ontology, null);
	}
	/*
	 * Method called from AgentWindow save button
	 */
	public void saveAgent(AgentDescriptionFE agentDesc){
		faTab.handleUserActionEvent(new AddUpdateAgentEvent(agentDesc.getAgentID(), agentDesc));
	}
	
	public void addAgent2Grid(String agentId, String agentDisplayName, String ontology, boolean isRead, boolean isWrite, int configCompleted){
		ElementModel agentStub = new ElementModel();
		agentStub.set(GridElementLabel.ID, agentId);
		agentStub.set(GridElementLabel.NAME, agentDisplayName);
		agentStub.set(GridElementLabel.COMPILE_STATUS, configCompleted);
		agentStub.set(GridElementLabel.READ, isRead);
		agentStub.set(GridElementLabel.WRITE, isWrite);
		agentStub.set(GridElementLabel.DATA_TYPE, CustomizedGrid.AGENT_TYPE);
		StringBuffer status = new StringBuffer(); 
		status.append("(" + ontology +	", ");
		status.append(isRead ? "read":"-");
		status.append(" / ");
		status.append(isWrite ? "write":"-");
		status.append(")");
		agentStub.set(GridElementLabel.STATUS, status.toString());
		addElement2GridStore(agentStub);
	}
	
	public void updateAgent(AgentDescriptionFE agent){
		String agentId = agent.getAgentID();
    	String agentDisplayName = agent.getDisplayName();
    	String ontology = null;   // =  agent.getOntology();
    	SupportedOntologiesDef supportedOnt = agent.getSupportedOntology();
    	if(supportedOnt != null)
	    	if (supportedOnt.isAllOntologies()){
	    		//TODO add code handle this,
	    		//this happens when "any" is selected on the GUI
	    	}else{
	    		//TODO ask about this multiple ontology support to update code accordingly
	    		List<String> ontList =  supportedOnt.getSupportedOntologies();
	    		if (ontList.size() >= 1){
	    			ontology = ontList.get(0);
	    		}
	    	}
    	int configCompleted = faTab.getAgentCompileStatus(agent);
    	boolean isRead = agent.isConfReadable();
    	boolean isWrite = agent.isConfWritable();
    	
    	ElementModel agentStub = new ElementModel();
		agentStub.set(GridElementLabel.ID, agentId);
		agentStub.set(GridElementLabel.NAME, agentDisplayName);
		agentStub.set(GridElementLabel.COMPILE_STATUS, configCompleted);
		agentStub.set(GridElementLabel.READ, isRead);
		agentStub.set(GridElementLabel.WRITE, isWrite);
		agentStub.set(GridElementLabel.DATA_TYPE, CustomizedGrid.AGENT_TYPE);
		StringBuffer status = new StringBuffer(); 
		status.append("(" + ontology +	", ");
		status.append(isRead ? "read":"-");
		status.append(" / ");
		status.append(isWrite ? "write":"-");
		status.append(")");
		agentStub.set(GridElementLabel.STATUS, status.toString());
		this.updateElementFromStore(agentStub);
		if(FATConstants.AGENT_STATUS_COMPILED == configCompleted){
			ToasterMessage.log(FeedbackAuthoringStrings.AGENT_LABEL + " " +
					agentDisplayName + " " + FeedbackAuthoringStrings.AGENT_COMP_SUCCESS_LABEL);
		}
	}

	@Override
	void populateGridForTesting() {
	}

	@Override
	void selectedRowEvent(ElementModel selectedRow) {
	}
	
	public String getOntologyXML(String ontology){
		OntologyFA ont = faTab.getOntologyDB(ontology);
		return ont.getXml();
	}

	@Override
	void populateGrid() {
	}
	
}
