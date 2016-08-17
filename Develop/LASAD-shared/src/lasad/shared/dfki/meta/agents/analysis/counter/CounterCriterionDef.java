package lasad.shared.dfki.meta.agents.analysis.counter;

import java.io.Serializable;

/**
 * Defines some threshold number when a count is meaningful (e.g., "> 3" or
 * "<= 7").
 * 
 * @author oliverscheuer
 * 
 */
public class CounterCriterionDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -202267681362108536L;
	private CounterCriterionOperator operator;
	private int referenceValue;

	public CounterCriterionDef(){
		
	}
	public CounterCriterionDef(CounterCriterionOperator operator,
			int referencevalue) {
		this.operator = operator;
		this.referenceValue = referencevalue;
	}

	public CounterCriterionOperator getOperator() {
		return operator;
	}

	public int getReferenceValue() {
		return referenceValue;
	}

	public boolean isFulfilled(int count) {
		if (operator == CounterCriterionOperator.GREATER) {
			return count > referenceValue;
		} else if (operator == CounterCriterionOperator.GREATER_OR_EQUAL) {
			return count >= referenceValue;
		} else if (operator == CounterCriterionOperator.EQUAL) {
			return count == referenceValue;
		} else if (operator == CounterCriterionOperator.LESS_OR_EQUAL) {
			return count <= referenceValue;
		} else {
			// operator == CounterCriterionOperators.LESS
			return count < referenceValue;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + referenceValue;
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CounterCriterionDef other = (CounterCriterionDef) obj;
		if (referenceValue != other.referenceValue)
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CounterCriterion [operator=" + operator + ", referenceValue="
				+ referenceValue + "]";
	}

}
