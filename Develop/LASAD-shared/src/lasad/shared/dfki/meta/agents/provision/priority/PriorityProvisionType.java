package lasad.shared.dfki.meta.agents.provision.priority;

import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;


/**
 * 
 * @author oliverscheuer
 * 
 */
public class PriorityProvisionType extends ProvisionType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5710223064216658987L;

	public static final int ALL_RESULTS = -1;

	private List<MsgSortCriterion> sortCriteriaInOrder = new Vector<MsgSortCriterion>();
	private List<MsgFilterDef> filterCriteria = new Vector<MsgFilterDef>();
	private int maxNumResults = 1;

	public PriorityProvisionType(){
		
	}
	
	public PriorityProvisionType(ServiceID id) {
		super(id);
	}

	public List<MsgSortCriterion> getSortCriteriaInOrder() {
		return sortCriteriaInOrder;
	}

	public void setSortCriteriaInOrder(
			List<MsgSortCriterion> sortCriteriaInOrder) {
		this.sortCriteriaInOrder = sortCriteriaInOrder;
	}

	public List<MsgFilterDef> getFilterDefs() {
		return filterCriteria;
	}

	public void setFilterCriteria(List<MsgFilterDef> filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	public int getMaxNumResults() {
		return maxNumResults;
	}

	public void setMaxNumResults(int maxNumResults) {
		this.maxNumResults = maxNumResults;
	}

	@Override
	public String toString() {
		return "PriorityProvisionType [sortCriteriaInOrder="
				+ sortCriteriaInOrder + ", filterCriteria=" + filterCriteria
				+ ", maxNumResults=" + maxNumResults + ", provisionTime="
				+ provisionTime + ", recipient=" + recipient
				+ ", providedActions=" + providedActions + ", serviceID="
				+ serviceID + ", name=" + name + "]";
	}

}
