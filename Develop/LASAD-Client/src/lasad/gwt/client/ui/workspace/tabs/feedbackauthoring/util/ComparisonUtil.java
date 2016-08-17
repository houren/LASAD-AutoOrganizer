package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.LASADBoxComponent;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2ConstNumComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstStringComparison;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Class with text representations of available comparison operators
 * @author Anahuac
 *
 */
public class ComparisonUtil {
	public static final String BOX = "box";
	public static final String LINK = "link";
	
	public static final int CONSTANT_VALUE = 0;
	public static final int VARIABLE_VALUE = 1;
	public static final int CONSTANT_SET = 2;
	public static final int VARIABLE_SET = 3;
	
	public static final int PLUS = 0;
	public static final int MINUS = 1;
	public static final String PLUS_SIGN = "+";
	public static final String MINUS_SIGN = "-";
	
	static final Map<String, String> compareAgainstMap = new HashMap<String, String>();
	static{
		compareAgainstMap.put(String.valueOf(VARIABLE_SET), FeedbackAuthoringStrings.VARIABLE_SET_LABEL);
		compareAgainstMap.put(String.valueOf(CONSTANT_SET), FeedbackAuthoringStrings.CONSTANT_SET_LABEL);
		compareAgainstMap.put(String.valueOf(VARIABLE_VALUE), FeedbackAuthoringStrings.VARIABLE_VALUE_LABEL);
		compareAgainstMap.put(String.valueOf(CONSTANT_VALUE), FeedbackAuthoringStrings.CONSTANT_VALUE_LABEL);
	}

	/*
	 * characters
	 * http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
	 * http://en.wikipedia.org/wiki/Unicode_Mathematical_Operators
	 */
	public static final String LESS_OP = "&lt;";
	public static final String LESS_OR_EQUAL_OP = "&le;";
	public static final String GREATER_OP = "&gt;";
	public static final String GREATER_OR_EQUAL_OP = "&ge;";
	public static final String EQUAL_OP = "=";
	public static final String NOT_EQUAL_OP = "&ne;";
	public static final String INTERSECT_OP = "&cap;" + "=" + "&empty;";
	public static final String NOT_INTERSECT_OP = "&cap;" + "&ne;" + "&empty;";
	public static final String SUBSET_OP = "&sub;";
	public static final String NOT_SUBSET_OP = "&nsub;";
	public static final String SUPERSET_OP = "&sup;";
	public static final String NOT_SUPERSET_OP = "&nsup;";
	public static final String IN_OP = "&isin;";
	public static final String NOT_IN_OP = "&notin;";
	public static final String CONTAINS_OP = "&ni;";
	public static final String NOT_CONTAINS_OP = "&notni;";

	public static final String LESS_ID = "less";
	public static final String LESS_OR_EQUAL_ID = "less-or-equal";
	public static final String GREATER_ID = "greater";
	public static final String GREATER_OR_EQUAL_ID = "greater-or-equal";
	public static final String EQUAL_ID = "equal";
	public static final String NOT_EQUAL_ID = "not-equal";
	public static final String INTERSECT_ID = "intersect";
	public static final String NOT_INTERSECT_ID = "not-intersect";
	public static final String SUBSET_ID= "subset";
	public static final String NOT_SUBSET_ID = "not-subset";
	public static final String SUPERSET_ID = "superset";
	public static final String NOT_SUPERSET_ID = "not-superset";
	public static final String IN_ID = "in";
	public static final String NOT_IN_ID = "not-in";
	public static final String CONTAINS_ID = "contains";
	public static final String NOT_CONTAINS_ID = "not-contains";
	
	
	static final Map<String, String> operatorId2StrMap = new LinkedHashMap<String, String>();
	static{
		operatorId2StrMap.put(ComparisonUtil.LESS_ID, decodeHTML(LESS_OP));
		operatorId2StrMap.put(ComparisonUtil.LESS_OR_EQUAL_ID, decodeHTML(LESS_OR_EQUAL_OP));
		operatorId2StrMap.put(ComparisonUtil.GREATER_ID, decodeHTML(GREATER_OP));
		operatorId2StrMap.put(ComparisonUtil.GREATER_OR_EQUAL_ID, decodeHTML(GREATER_OR_EQUAL_OP));
		operatorId2StrMap.put(ComparisonUtil.EQUAL_ID, decodeHTML(EQUAL_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_EQUAL_ID, decodeHTML(NOT_EQUAL_OP));
		operatorId2StrMap.put(ComparisonUtil.INTERSECT_ID, decodeHTML(INTERSECT_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_INTERSECT_ID, decodeHTML(NOT_INTERSECT_OP));
		operatorId2StrMap.put(ComparisonUtil.SUBSET_ID, decodeHTML(SUBSET_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_SUBSET_ID, decodeHTML(NOT_SUBSET_OP));
		operatorId2StrMap.put(ComparisonUtil.SUPERSET_ID, decodeHTML(SUPERSET_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_SUPERSET_ID, decodeHTML(NOT_SUPERSET_OP));
		operatorId2StrMap.put(ComparisonUtil.IN_ID, decodeHTML(IN_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_IN_ID, decodeHTML(NOT_IN_OP));
		operatorId2StrMap.put(ComparisonUtil.CONTAINS_ID, decodeHTML(CONTAINS_OP));
		operatorId2StrMap.put(ComparisonUtil.NOT_CONTAINS_ID, decodeHTML(NOT_CONTAINS_OP));
	}
	
	static final Map<String, String> operatorId2HtmlMap = new LinkedHashMap<String, String>();
	static{
		operatorId2HtmlMap.put(ComparisonUtil.LESS_ID, LESS_OP);
		operatorId2HtmlMap.put(ComparisonUtil.LESS_OR_EQUAL_ID, LESS_OR_EQUAL_OP);
		operatorId2HtmlMap.put(ComparisonUtil.GREATER_ID, GREATER_OP);
		operatorId2HtmlMap.put(ComparisonUtil.GREATER_OR_EQUAL_ID, GREATER_OR_EQUAL_OP);
		operatorId2HtmlMap.put(ComparisonUtil.EQUAL_ID, EQUAL_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_EQUAL_ID, NOT_EQUAL_OP);
		operatorId2HtmlMap.put(ComparisonUtil.INTERSECT_ID, INTERSECT_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_INTERSECT_ID, NOT_INTERSECT_OP);
		operatorId2HtmlMap.put(ComparisonUtil.SUBSET_ID, SUBSET_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_SUBSET_ID, NOT_SUBSET_OP);
		operatorId2HtmlMap.put(ComparisonUtil.SUPERSET_ID, SUPERSET_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_SUPERSET_ID, NOT_SUPERSET_OP);
		operatorId2HtmlMap.put(ComparisonUtil.IN_ID, IN_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_IN_ID, NOT_IN_OP);
		operatorId2HtmlMap.put(ComparisonUtil.CONTAINS_ID, CONTAINS_OP);
		operatorId2HtmlMap.put(ComparisonUtil.NOT_CONTAINS_ID, NOT_CONTAINS_OP);
	}
	
	public static final String COMP_NUM2NUM_ELEMENT_NAME = "num2num";
	public static final String COMP_STR2STR_ELEMENT_NAME = "str2str";
	public static final String COMP_SET2SET_ELEMENT_NAME = "set2set";
	public static final String COMP_SET2STR_ELEMENT_NAME = "set2str";
	public static final String COMP_STR2SET_ELEMENT_NAME = "str2set";
	
	/*
	 * This function converts a character entity reference in HTML
	 * to its character representation.
	 * For example input="&gt;" output=">"
	 */
	public static String decodeHTML(String str) {
		String result = null;
		if(LESS_OP.equals(str)){
			result = "<"; 
		} else if(GREATER_OP.equals(str)){
			result = ">"; 
		} else{
			Element el = DOM.createDiv();
			el.setInnerHTML(str);
			result = el.getInnerHTML();
		}
		return result;
	}
	
	public static String fromOperId2Str(String operatorId){
		return operatorId2StrMap.get(operatorId);
	}
	public static String fromOperId2Html(String operatorId){
		return operatorId2HtmlMap.get(operatorId);
	}
	
	public static List<String> getValueElems(Comparison comparison) {
		List<String> valueElems = new Vector<String>();
		
		if (comparison instanceof Num2ConstNumComparison) {
			String value = Integer.toString(((Num2ConstNumComparison) comparison).getRightExpr());
			valueElems.add(value);
		} else if (comparison instanceof Set2ConstSetComparison) {
			List<String> values = ((Set2ConstSetComparison) comparison).getRightExpr();
			valueElems.addAll(values);
		} else if (comparison instanceof String2ConstStringComparison) {
			String value = ((String2ConstStringComparison) comparison).getRightExpr();
			valueElems.add(value);
		} else if (comparison instanceof Set2ConstStringComparison) {
			String value = ((Set2ConstStringComparison) comparison).getRightExpr();
			valueElems.add(value);
		} else if (comparison instanceof String2ConstSetComparison) {
			List<String> values = ((String2ConstSetComparison) comparison).getRightExpr();
			valueElems.addAll(values);
		}
		return valueElems;
	}
	
	public static String getComponentType(ElementVariable elementVariable){
		if(elementVariable instanceof NodeVariable){
			return BOX;
		} else{
			return LINK;
		}
	}
	public static String getComponentType(LASADBoxComponent element){
		if(element instanceof AbstractBox){
			return BOX;
		} else{
			return LINK;
		}
	}
	public static boolean isNodeVariable(ElementVariable elementVariable){
		if(elementVariable instanceof NodeVariable){
			return true;
		} else{
			return false;
		}
	}
	
	public static String getCompareAgainstOptionAsStr(int val){
		return compareAgainstMap.get(String.valueOf(val));
	}
	
	public static Map<String, String> getCompareAgainstOptions(){
		return compareAgainstMap;
	}
	

}
