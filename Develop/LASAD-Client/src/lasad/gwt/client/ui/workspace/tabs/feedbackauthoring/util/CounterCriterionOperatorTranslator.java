package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.analysis.counter.CounterCriterionOperator;

/**
 * Class with text representations of available comparison operators for counter patterns
 * @author Anahuac
 *
 */
public class CounterCriterionOperatorTranslator {
	
	private static final LinkedHashMap<String , CounterCriterionOperator> str2Enum = new LinkedHashMap<String, CounterCriterionOperator>();
	static {
		str2Enum.put("<", CounterCriterionOperator.LESS);
		str2Enum.put("<=", CounterCriterionOperator.LESS_OR_EQUAL);
		str2Enum.put("=", CounterCriterionOperator.EQUAL);
		str2Enum.put(">", CounterCriterionOperator.GREATER);
		str2Enum.put(">=", CounterCriterionOperator.GREATER_OR_EQUAL);
	}
	private static final LinkedHashMap<CounterCriterionOperator, String> enum2Str = new LinkedHashMap<CounterCriterionOperator, String>();
	static {
		enum2Str.put(CounterCriterionOperator.LESS, "<");
		enum2Str.put(CounterCriterionOperator.LESS_OR_EQUAL, "<=");
		enum2Str.put(CounterCriterionOperator.EQUAL, "=");
		enum2Str.put(CounterCriterionOperator.GREATER, ">");
		enum2Str.put(CounterCriterionOperator.GREATER_OR_EQUAL, ">=");
	}
	
	public static String translate(CounterCriterionOperator oper){
		return enum2Str.get(oper);
	}
	
	public static CounterCriterionOperator translate(String oper){
		return str2Enum.get(oper);
	}
	
	public static List<String> getOperatorStrs(){
		Set<String> set = str2Enum.keySet();
		Vector<String> v = new Vector<String>();
		for(String s : set){
			v.add(s);
		}
		return v;
	}
}
