package lasad.shared.dfki.meta.agents.analysis.structure.model;


/**
 * Indicates whether LHS and RHS of a {@link Comparison} refer to
 * {@link ElementVariableProp}s of the same {@link ElementVariable} (
 * {@link #INTERNAL}) or not ({@link #EXTERNAL}).
 * 
 * Will also be used as a control flag when generating Jess Fact pattern to
 * decide which {@link Comparison}s to consider as constraints.
 * 
 * @author oliverscheuer
 * 
 */
public enum ComparisonType {

	INTERNAL("INTERNAL"), EXTERNAL("EXTERNAL"), ALL("ALL");

	//private final String selection;

	ComparisonType(String selection) {
		//this.selection = selection;
	}

}
