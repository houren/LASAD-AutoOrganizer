package lasad.shared.dfki.meta.agents.action.feedback;

import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;

/**
 * An {@link AnalysisType} that triggers the generation of system actions
 * associated with a specification of how to generate these actions(long and
 * short feedback text, highlighting).
 * 
 * @author Oliver Scheuer
 * 
 */
public class FeedbackActionType extends ActionType {

	private static final long serialVersionUID = 6611905659679934399L;

	private ServiceID triggerID = null;
	private List<MsgCompDef> msgCompDefs = new Vector<MsgCompDef>();

	public FeedbackActionType() {

	}

	public FeedbackActionType(ServiceID ID, ServiceID triggerID) {
		super(ID);
		this.triggerID = triggerID;
	}

	public ServiceID getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(ServiceID triggerID) {
		this.triggerID = triggerID;
	}

	public void addMsgComp(MsgCompDef cmp) {
		this.msgCompDefs.add(cmp);
	}

	public List<MsgCompDef> getMsgCompDefs() {
		return msgCompDefs;
	}

	public void setMsgCompDefs(List<MsgCompDef> msgCompDefs) {
		this.msgCompDefs = msgCompDefs;
	}

	@Override
	public String toString() {
		return "FeedbackActionType [triggerID=" + triggerID + ", msgCompDefs="
				+ msgCompDefs + ", priorityDef=" + priorityDef + "]";
	}

}
