package lasad.shared.dfki.meta.agents.analysis.counter;

import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;

/**
 * An {@link AnalysisType} that defines conditions when the number of workspace
 * elements (i.e., nodes and/or links) or detected {@link AnalysisResult}s is
 * meaningful. The {@link #counterDefinition} specifies what to count (i.e.,
 * nodes / links / patterns fulfilling specific conditions); the
 * {@link counterCriterion} defines some threshold number when this count is
 * meaningful (e.g., "> 3" or "<= 7").
 * 
 * @author oliverscheuer
 * 
 */
public class CounterAnalysisType extends AnalysisType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8800715985661629021L;
	private CounterDef counterDefinition;
	private List<CounterCriterionDef> counterCriteria = new Vector<CounterCriterionDef>();

	public CounterAnalysisType() {
	}

	public CounterAnalysisType(ServiceID id, CounterDef counterDef,
			List<CounterCriterionDef> counterCriteria) {
		super(getDatatype(counterDef), id);
		this.counterDefinition = counterDef;
		this.counterCriteria = counterCriteria;
	}

	public CounterAnalysisType(ServiceID id, CounterDef counterDef,
			CounterCriterionDef counterCriterion) {
		super(getDatatype(counterDef), id);
		this.counterDefinition = counterDef;
		this.counterCriteria.add(counterCriterion);
	}

	public CounterDef getCounterDefinition() {
		return counterDefinition;
	}

	public void setCounterDefinition(CounterDef counterDefinition) {
		this.counterDefinition = counterDefinition;
	}

	public List<CounterCriterionDef> getCounterCriteria() {
		return counterCriteria;
	}

	public void setCounterCriteria(List<CounterCriterionDef> counterCriteria) {
		this.counterCriteria = counterCriteria;
	}

	private static AnalysisResultDatatype getDatatype(CounterDef counterDef) {
		if (counterDef.getUserSelectionSetting() == UserSelectionSetting.NONE) {
			return AnalysisResultDatatype.session_binary_result;
		} else {
			return AnalysisResultDatatype.user_binary_result;
		}
	}

	@Override
	public boolean userSpecificLogic() {
		boolean userSpecificCounterDef = (UserSelectionSetting.NONE != counterDefinition
				.getUserSelectionSetting());
		boolean userSpecificFilters = super.userSpecificLogic();

		return userSpecificCounterDef || userSpecificFilters;
	}
}
