package lasad.shared.dfki.meta.agents.analysis;

import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.ServiceType;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef;

/**
 * Metadata about a type of analysis an {@link IAgent} can carry out.
 * 
 * @author Oliver Scheuer, isha
 * 
 */
public class AnalysisType extends ServiceType {

	private static final long serialVersionUID = 8365168400985153693L;

	private AnalysisResultDatatype resultDatatype;

	private boolean onlyPositiveInstances = true;

	private List<PatternFilterDef> filterDefs = new Vector<PatternFilterDef>();

	public AnalysisType() {

	}

	public AnalysisType(AnalysisResultDatatype resultDataype, ServiceID id) {
		super(id);
		this.resultDatatype = resultDataype;
	}

	public AnalysisResultDatatype getResultDatatype() {
		return resultDatatype;
	}

	/**
	 * 
	 * @return whether only positive results are meaningful and will be provided
	 */
	public boolean isOnlyPositiveInstances() {
		return onlyPositiveInstances;
	}

	public void setOnlyPositiveInstances(boolean onlyPositiveInstances) {
		this.onlyPositiveInstances = onlyPositiveInstances;
	}

	public void addFilterDef(PatternFilterDef filterDef) {
		this.filterDefs.add(filterDef);
	}

	public List<PatternFilterDef> getFilterDefs() {
		return filterDefs;
	}

	public void setFilterDefs(List<PatternFilterDef> filterDefs) {
		this.filterDefs = filterDefs;
	}

	public boolean userSpecificLogic() {
		for (PatternFilterDef filterDef : filterDefs) {
			if (filterDef.isUserSpecific()) {
				return true;
			}
		}
		return false;
	}

}
