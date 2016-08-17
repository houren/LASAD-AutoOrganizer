package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
//import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2VarNumComparison;
//import lasad.shared.dfki.meta.agents.analysis.structure.model.Operator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.analysis.structure.model.VariableComparison;

/**
 * This class provides functionality to search for an {@link ElementVariable} using its Id.
 * @author Anahuac
 *
 */
public class ElementVariableUtil {
	
	public static final int NOT_FOUND = -1;

	/**
	 * Gets a reference to the {@link ElementVariable} having varID == id
	 * @param patManipulator
	 * @param id the {@link ElementVariable} varID
	 * @return {@link ElementVariable} or null if a {@link ElementVariable} with varID == id was not found
	 */
	public static ElementVariable getElementVariable(StructuralAnalysisTypeManipulator patManipulator, String id){
		ElementVariable elementVar = null;
		int index = patManipulator.getNodeVariables().indexOf(new NodeVariable(id));
		if(index != NOT_FOUND){
			elementVar = patManipulator.getNodeVariables().get(index);
		} else {
			index = patManipulator.getLinkVariables().indexOf(new LinkVariable(id));
			if(index != NOT_FOUND){ 
				elementVar = patManipulator.getLinkVariables().get(index);
			} else{
				//Look into NotNode and NotLink
				List<StructuralPattern> notPatternsList = new Vector<StructuralPattern>(patManipulator.getNotPatterns());
				for(StructuralPattern notPattern : notPatternsList){
					index = notPattern.getNodeVars().indexOf(new NodeVariable(id));
					if(index != NOT_FOUND){
						elementVar = notPattern.getNodeVars().get(index);
						break;
					} else {
						index = notPattern.getLinkVars().indexOf(new LinkVariable(id));
						if(index != NOT_FOUND){
							elementVar = notPattern.getLinkVars().get(index);
							break;
						} 
					}
				}
			}
		}
		
		if(elementVar == null){
			FATDebug.print(FATDebug.ERROR, "ERROR [ElementVariableUtil][getElementVariable] id:" + id + " does not exit");
		}
		return elementVar;
	}
	
	public static String createHeaderForBoxLink(String elementId, String rootElementId){
		return new String(elementId + "-" + rootElementId);
	}
	
	public static String generatedReadableStrFromComparison(StructuralAnalysisTypeManipulator patManipulator, Comparison comparison){
		//ElementVariableProp leftExpr = comparison.getLeftExpr();
		//String propId = leftExpr.getPropId();
		//Operator oper = comparison.getOperator();
		
		StringBuffer value = new StringBuffer();
		if (comparison instanceof VariableComparison){
			String rElemId = ((VariableComparison)comparison).getRightExpr().getElementVar().getVarID();
			String rPropId = ((VariableComparison)comparison).getRightExpr().getPropId();
//			String rRhsComponentID = ((VariableComparison)comparison).getRightExpr().getCompID();
//			String rCompId = null;
//			if (!PropDescr.DEFAULT_COMPONENT_ID.equals(rRhsComponentID)) {
//				rCompId = rRhsComponentID;
//			}
			
			//boolean rIsNode = (((VariableComparison)comparison).getRightExpr().getElementVar() instanceof NodeVariable);
			ElementVariable elemVar = null;
			if(patManipulator != null){
				elemVar = getElementVariable(patManipulator, rElemId);
			}
			value.append((elemVar != null && elemVar.getName() != null) ? elemVar.getName() : rElemId);
			value.append("." + rPropId);
			if(comparison instanceof Num2VarNumComparison){
				int offset = ((Num2VarNumComparison)comparison).getOffset();
				if(offset > 0){
					value.append(" " + ComparisonUtil.PLUS_SIGN + " " + offset);
				} else if(offset < 0){
					value.append(" " + ComparisonUtil.MINUS_SIGN + " " + Math.abs(offset));
				} 
			}
		} else{
			List<String> valueElems = ComparisonUtil.getValueElems(comparison);
			boolean commaflag = false;
			boolean isSet = (valueElems.size() > 1);
			if(isSet){
				value.append("{");
			}
			for(String val:valueElems){
				if(commaflag){
					value.append(", ");
				}
				value.append("\"" + val + "\"");
				commaflag = true;
			}
			if(isSet){
				value.append("}");
			}
		}
		return value.toString();
	}
	
	public static String generatedReadableStrFromComparison(HashMap<String, String> elemMap, Comparison comparison){
		//ElementVariableProp leftExpr = comparison.getLeftExpr();
		//String propId = leftExpr.getPropId();
		//Operator oper = comparison.getOperator();
		
		StringBuffer value = new StringBuffer();
		if (comparison instanceof VariableComparison){
			String rElemId = ((VariableComparison)comparison).getRightExpr().getElementVar().getVarID();
			String rPropId = ((VariableComparison)comparison).getRightExpr().getPropId();
//			String rRhsComponentID = ((VariableComparison)comparison).getRightExpr().getCompID();
//			String rCompId = null;
//			if (!PropDescr.DEFAULT_COMPONENT_ID.equals(rRhsComponentID)) {
//				rCompId = rRhsComponentID;
//			}
			//String label = FAOntElementIds.getLabelForElemId(elemMap.get(rElemId));
			value.append(elemMap.get(rElemId));
			value.append("." + rPropId);
			if(comparison instanceof Num2VarNumComparison){
				int offset = ((Num2VarNumComparison)comparison).getOffset();
				if(offset > 0){
					value.append(" " + ComparisonUtil.PLUS_SIGN + " " + offset);
				} else if(offset < 0){
					value.append(" " + ComparisonUtil.MINUS_SIGN + " " + Math.abs(offset));
				} 
			}
		} else{
			List<String> valueElems = ComparisonUtil.getValueElems(comparison);
			boolean commaflag = false;
			boolean isSet = (valueElems.size() > 1);
			if(isSet){
				value.append("{");
			}
			for(String val:valueElems){
				if(commaflag){
					value.append(", ");
				}
				value.append("\"" + val + "\"");
				commaflag = true;
			}
			if(isSet){
				value.append("}");
			}
		}
		return value.toString();
	}

}
