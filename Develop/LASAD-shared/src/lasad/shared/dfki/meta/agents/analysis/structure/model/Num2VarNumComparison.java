package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * 
 * Comparison of a numeric variable to some other numeric variable.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class Num2VarNumComparison extends VariableComparison {

	private static final long serialVersionUID = -3443736142624808017L;

	private Num2NumOperator operator;
	private ElementVariableProp rightExpr;
	private int offset;

	public Num2NumOperator getOperator() {
		return operator;
	}

	public void setOperator(Num2NumOperator operator) {
		this.operator = operator;
	}

	public ElementVariableProp getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(ElementVariableProp rightExpr) {
		this.rightExpr = rightExpr;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	protected VariableComparison getInverseBasic() {
		Num2VarNumComparison inverted = new Num2VarNumComparison();
		inverted.setOperator(getOperator().invert());
		inverted.setOffset((-1) * getOffset());

		return inverted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + offset;
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((rightExpr == null) ? 0 : rightExpr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Num2VarNumComparison other = (Num2VarNumComparison) obj;
		if (offset != other.offset)
			return false;
		if (operator != other.operator)
			return false;
		if (rightExpr == null) {
			if (other.rightExpr != null)
				return false;
		} else if (!rightExpr.equals(other.rightExpr))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Num2VarNumComparison [operator=" + operator + ", rightExpr="
				+ rightExpr + ", offset=" + offset + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " ["
				+ rightExpr.toCompactString() + "] + " + offset + "\n");
		return buf.toString();
	}
}
