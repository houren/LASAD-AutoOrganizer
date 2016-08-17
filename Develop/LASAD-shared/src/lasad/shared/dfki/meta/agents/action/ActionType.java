package lasad.shared.dfki.meta.agents.action;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.ServiceType;

/**
 * Metadata how to generate system actions (pedagogical strategies).
 * 
 * @author Oliver Scheuer
 * 
 */
public abstract class ActionType extends ServiceType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -631097723363060287L;
	protected PriorityDef priorityDef = null;
	
	public ActionType (){
		
	}

	public ActionType(ServiceID id) {
		super(id);
	}

	public PriorityDef getPriorityDef() {
		return priorityDef;
	}

	public void setPriorityDef(PriorityDef priorityDef) {
		this.priorityDef = priorityDef;
	}

}
