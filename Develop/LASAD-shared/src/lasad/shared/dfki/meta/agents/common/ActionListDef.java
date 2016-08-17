package lasad.shared.dfki.meta.agents.common;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.ServiceID;



/**
 * 
 * @author oliverscheuer
 * 
 */
public class ActionListDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5262687502593118364L;
	private boolean allOwnActions = false;
	private List<ServiceID> actionIDs = new Vector<ServiceID>();

	public ActionListDef() {
	}
	
	public ActionListDef(boolean allOwnTypes) {
		this.allOwnActions = allOwnTypes;
	}

	public ActionListDef(List<ServiceID> serviceIDs) {
		this.actionIDs = serviceIDs;
	}

	public boolean isAllOwnActionTypes() {
		return allOwnActions;
	}

	public List<ServiceID> getServiceIDs() {
		return actionIDs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allOwnActions ? 1231 : 1237);
		result = prime * result
				+ ((actionIDs == null) ? 0 : actionIDs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionListDef other = (ActionListDef) obj;
		if (allOwnActions != other.allOwnActions)
			return false;
		if (actionIDs == null) {
			if (other.actionIDs != null)
				return false;
		} else if (!actionIDs.equals(other.actionIDs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProvidedActionsDef [allOwnActions=" + allOwnActions
				+ ", actionIDs=" + actionIDs + "]";
	}

}
