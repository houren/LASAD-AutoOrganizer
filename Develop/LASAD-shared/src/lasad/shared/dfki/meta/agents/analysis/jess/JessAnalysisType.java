package lasad.shared.dfki.meta.agents.analysis.jess;

import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;

/**
 * An {@link AnalysisType} that is associated with a declarative description of
 * how to compute corresponding {@link AnalysisResult}s. The interpretation of
 * this string-based description is up to the {@link IAgent} who offers that
 * {@link AnalysisType}.
 * 
 * @author oliverscheuer
 * 
 */
public class JessAnalysisType extends AnalysisType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2021565059505556945L;
	private String rule;

	public JessAnalysisType(){
	}
	
	public JessAnalysisType(AnalysisResultDatatype resultType, ServiceID sID,
			String rule) {
		super(resultType, sID);
		this.rule = rule;
	}

	public String getDefinition() {
		return rule;
	}

	public void setDefinition(String definition) {
		this.rule = definition;
	}
}
