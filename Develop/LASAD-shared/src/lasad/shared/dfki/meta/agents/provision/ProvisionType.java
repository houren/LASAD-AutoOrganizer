package lasad.shared.dfki.meta.agents.provision;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.ServiceType;
import lasad.shared.dfki.meta.agents.common.ActionListDef;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ProvisionType extends ServiceType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6704018956971645783L;
	protected ProvisionTimeDef provisionTime = null;
	protected RecipientDef recipient = null;
	protected ActionListDef providedActions = null;
	
	public ProvisionType(){
		
	}

	public ProvisionType(ServiceID id) {
		super(id);
	}

	public ProvisionTimeDef getProvisionTime() {
		return provisionTime;
	}

	public void setProvisionTime(ProvisionTimeDef provisionTime) {
		this.provisionTime = provisionTime;
	}

	public RecipientDef getRecipient() {
		return recipient;
	}

	public void setRecipient(RecipientDef recipient) {
		this.recipient = recipient;
	}

	public ActionListDef getProvidedActions() {
		return providedActions;
	}

	public void setProvidedActions(ActionListDef providedActions) {
		this.providedActions = providedActions;
	}

	public boolean userSpecificLogic() {
		return (recipient instanceof RecipientDef_Individuals);
	}

	@Override
	public String toString() {
		return "ProvisionType [provisionTime=" + provisionTime + ", recipient="
				+ recipient + ", providedActions=" + providedActions
				+ ", serviceID=" + serviceID + "]";
	}

}
