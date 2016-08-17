package lasad.shared.dfki.meta.agents;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseModelerDef;
import lasad.shared.dfki.meta.agents.common.ActionListDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;

/**
 * an agent configuration (to be used in the graphical frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class ActionAgentConfigData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -211466861494095183L;

	private String agentID = null;

	private SupportedOntologiesDef supportedOntologiesDef = null;
	private PhaseModelerDef phaseModelerDef = null;

	private List<ServiceType> serviceTypes = new Vector<ServiceType>();
	private Map<ServiceID, AnalysisType> analysisTypes = new HashMap<ServiceID, AnalysisType>();
	private Map<ServiceID, ActionType> actionTypes = new HashMap<ServiceID, ActionType>();
	private Map<ServiceID, ProvisionType> provisionTypes = new HashMap<ServiceID, ProvisionType>();

	public ActionAgentConfigData() {

	}

	public ActionAgentConfigData(String agentID) {
		this.agentID = agentID;
	}

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public SupportedOntologiesDef getSupportedOntologiesDef() {
		return supportedOntologiesDef;
	}

	public void setSupportedOntologiesDef(
			SupportedOntologiesDef supportedOntologiesDef) {
		this.supportedOntologiesDef = supportedOntologiesDef;
	}

	public PhaseModelerDef getPhaseModelerDef() {
		return phaseModelerDef;
	}

	public void setPhaseModelerDef(PhaseModelerDef phaseModelerDef) {
		this.phaseModelerDef = phaseModelerDef;
	}

	public List<ServiceType> getServiceTypes() {
		return this.serviceTypes;
	}

	public Collection<AnalysisType> getAnalysisTypes() {
		return analysisTypes.values();
	}

	public AnalysisType getAnalysisType(ServiceID sID) {
		return analysisTypes.get(sID);
	}

	public void removeAnalysisType(ServiceID sID) {
		AnalysisType a = this.analysisTypes.remove(sID);
		this.serviceTypes.remove(a);
	}

	public void addAnalysisTypes(List<AnalysisType> analysisTypes) {
		for (AnalysisType type : analysisTypes) {
			this.analysisTypes.put(type.getServiceID(), type);
			this.serviceTypes.add(type);
		}
	}

	public void addAnalysisType(AnalysisType analysisType) {
		this.analysisTypes.put(analysisType.getServiceID(), analysisType);
		this.serviceTypes.add(analysisType);
	}

	public Collection<ActionType> getActionTypes() {
		return actionTypes.values();
	}

	public ActionType getActionType(ServiceID sID) {
		return actionTypes.get(sID);
	}
	
	public void removeActionType(ServiceID sID) {
		ActionType a = this.actionTypes.remove(sID);
		this.serviceTypes.remove(a);
	}

	public void addActionTypes(List<ActionType> actionTypes) {
		for (ActionType type : actionTypes) {
			this.actionTypes.put(type.getServiceID(), type);
			this.serviceTypes.add(type);
		}
	}

	public Collection<ProvisionType> getProvisionTypes() {
		return provisionTypes.values();
	}

	public ProvisionType getProvisionTypes(ServiceID sID) {
		return provisionTypes.get(sID);
	}
	
	public void removeProvisionType(ServiceID sID) {
		ProvisionType a = this.provisionTypes.remove(sID);
		this.serviceTypes.remove(a);
	}

	public void addProvisionTypes(List<ProvisionType> provisionTypes) {
		for (ProvisionType type : provisionTypes) {
			this.provisionTypes.put(type.getServiceID(), type);
			this.serviceTypes.add(type);
		}
	}

	public void replaceAgentID(String newAgentID) {
		String oldID = getAgentID();
		setAgentID(newAgentID);

		for (ServiceType sType : serviceTypes) {
			if (sType instanceof AnalysisType) {
				AnalysisType type = (AnalysisType) sType;
				ServiceID sID = type.getServiceID();
				sID.setAgentID(newAgentID);
			} else if (sType instanceof FeedbackActionType) {
				FeedbackActionType type = (FeedbackActionType) sType;
				ServiceID sID = type.getServiceID();
				sID.setAgentID(newAgentID);
				ServiceID triggerID = type.getTriggerID();
				if (triggerID.getAgentID().equals(oldID)) {
					triggerID.setAgentID(newAgentID);
				}
			} else if (sType instanceof ProvisionType) {
				ProvisionType type = (ProvisionType) sType;
				ServiceID sID = type.getServiceID();
				sID.setAgentID(newAgentID);
				ActionListDef subTypes = type.getProvidedActions();
				if (!subTypes.isAllOwnActionTypes()) {
					for (ServiceID subTypeID : subTypes.getServiceIDs()) {
						if (subTypeID.getAgentID().equals(oldID)) {
							subTypeID.setAgentID(newAgentID);
						}
					}
				}
			}
		}

	}

}
