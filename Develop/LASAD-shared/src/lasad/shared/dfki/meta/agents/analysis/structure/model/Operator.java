package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Operator used to compare values (see {@link Comparison}).
 * 
 * @author oliverscheuer
 * 
 */
public interface Operator {

	public String getName();
	
	public Operator invert();
}
