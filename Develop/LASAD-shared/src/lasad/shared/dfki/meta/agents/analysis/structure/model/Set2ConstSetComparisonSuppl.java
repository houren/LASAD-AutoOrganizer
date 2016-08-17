package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class Set2ConstSetComparisonSuppl extends ComparisonSuppl {

	private static final long serialVersionUID = -8998466726020817052L;
	private boolean useJessConstantSetFact;

	public boolean isUseJessConstantSetFact() {
		return useJessConstantSetFact;
	}

	public void setUseJessConstantSetFact(boolean useJessConstantSetFact) {
		this.useJessConstantSetFact = useJessConstantSetFact;
	}

	@Override
	public String toString() {
		return "Set2ConstSetComparisonSuppl [useJessConstantSetFact="
				+ useJessConstantSetFact + "]";
	}

}
