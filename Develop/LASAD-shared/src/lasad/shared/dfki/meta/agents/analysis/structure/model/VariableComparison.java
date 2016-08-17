package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Comparison of some variable with another variable (as opposed to comparisons
 * with constant values).
 * 
 * @author oliverscheuer
 * 
 */
public abstract class VariableComparison extends Comparison {

	private static final long serialVersionUID = -3731609160246431532L;

	public abstract ElementVariableProp getRightExpr();

	public abstract void setRightExpr(ElementVariableProp rightExpr);

	// Returns a semantically equivalent Comparison with switched variables,
	// i.e., (a op b) => (b inv-op a)
	public VariableComparison getInverse() {
		VariableComparison returnVal = getInverseBasic();
		returnVal.setLeftExpr(getRightExpr());
		returnVal.setRightExpr(getLeftExpr());
		returnVal.setDebugInfo(getDebugInfo());

		ComparisonSuppl thisSupplData = getSupplData();
		ComparisonSuppl returnSupplData = new ComparisonSuppl();
		returnSupplData.setId(thisSupplData.getId());
		returnSupplData.setType(thisSupplData.getType());
		returnSupplData.setBin(thisSupplData.getBin());
		returnVal.setSupplData(returnSupplData);

		return returnVal;
	}

	protected abstract VariableComparison getInverseBasic();

	@Override
	public String toString() {
		return "VariableComparison [], " + super.toString();
	}

}
