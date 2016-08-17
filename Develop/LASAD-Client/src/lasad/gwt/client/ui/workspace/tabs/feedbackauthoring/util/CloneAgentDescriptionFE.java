package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.ActionAgentConfigData;
import lasad.shared.dfki.meta.agents.ServiceClass;
import lasad.shared.dfki.meta.agents.ServiceID;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.action.PriorityDef;
import lasad.shared.dfki.meta.agents.action.feedback.FeedbackActionType;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_Highlighting;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_LongText;
import lasad.shared.dfki.meta.agents.action.feedback.MsgCompDef_ShortText;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTime;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_LastModTimeSetting;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_User;
import lasad.shared.dfki.meta.agents.action.feedback.PatternFilterDef_UserSetting;
import lasad.shared.dfki.meta.agents.analysis.AnalysisResultDatatype;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterCriterionDef;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterCriterionOperator;
import lasad.shared.dfki.meta.agents.analysis.counter.CounterDef;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeGeneral;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Object;
import lasad.shared.dfki.meta.agents.analysis.counter.InstanceTypeSpecific_Pattern;
import lasad.shared.dfki.meta.agents.analysis.jess.JessAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseDef;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseModelerDef;
import lasad.shared.dfki.meta.agents.analysis.phases.PhaseModelerDef_Empirical;
import lasad.shared.dfki.meta.agents.analysis.phases.PhasesDef;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariableProp;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2ConstNumComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2NumOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Num2VarNumComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.PropConstr;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2SetOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2StringOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2VarSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Set2VarStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2ConstStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2SetOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2StringOperator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2VarSetComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.String2VarStringComparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPatternSuppl;
import lasad.shared.dfki.meta.agents.common.ActionListDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_OnRequest;
import lasad.shared.dfki.meta.agents.provision.ProvisionTimeDef_Periodically;
import lasad.shared.dfki.meta.agents.provision.ProvisionType;
import lasad.shared.dfki.meta.agents.provision.RecipientDef;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Group;
import lasad.shared.dfki.meta.agents.provision.RecipientDef_Individuals;
import lasad.shared.dfki.meta.agents.provision.priority.PriorityProvisionType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * This class provides functionality to clone/copy AgentDescriptionFE
 * @author Anahuac
 *
 */

public class CloneAgentDescriptionFE {
	
	static Map<String, String> idMap = new HashMap<String, String>(); //used to tracks (oldId, newId)

	/**
	 * method to start a deep-copy of oldAgent
	 * @param newAgentId new agent id, obtained from backend
	 * @param newAgentName new agent name, getNewName(oldName) method can be used
	 * @param oldAgent agent to be cloned
	 * @return
	 */
	public static AgentDescriptionFE doCloning(String newAgentId, String newAgentName, AgentDescriptionFE oldAgent){
		idMap.clear();
		//String agentId = "agent-" + IdGenerator.getNewAgentId();
		
		idMap.put(oldAgent.getAgentID(), newAgentId);
		AgentDescriptionFE newAgent = new AgentDescriptionFE();
		
		newAgent.setAgentID(newAgentId);
		newAgent.setDisplayName(newAgentName);
		if(oldAgent.getDescription() != null){
			newAgent.setDescription(new String(oldAgent.getDescription()));
		}
		newAgent.setConfigCompleted(oldAgent.isConfigCompleted());
		newAgent.setConfReadable(true);
		newAgent.setConfWritable(true);
		
		//copy SupportedOntologiesDef
		newAgent.setSupportedOntology(cloneSupportedOntologiesDef(oldAgent.getSupportedOntology()));
		newAgent.setConfData(cloneActionAgentConfigData(newAgentId, oldAgent.getConfData()));
		return newAgent;
	}
	

	/**
	 * Does a deep-copy of ActionAgentConfigData from oldAgent
	 * @param newAgentId
	 * @param oldConfigData
	 * @return {@link ActionAgentConfigData} a deep-copy of oldAgent.ActionAgentConfigData
	 */
	private static ActionAgentConfigData cloneActionAgentConfigData(String newAgentId, ActionAgentConfigData oldConfigData){
		ActionAgentConfigData newConfigData = new ActionAgentConfigData(newAgentId);
		if(oldConfigData != null){
			newConfigData.setSupportedOntologiesDef(cloneSupportedOntologiesDef(oldConfigData.getSupportedOntologiesDef()));
			newConfigData.setPhaseModelerDef(clonePhaseModelerDef(oldConfigData.getPhaseModelerDef()));
			newConfigData.addAnalysisTypes(cloneAnalysisTypeList(new Vector<AnalysisType>(oldConfigData.getAnalysisTypes())));
			newConfigData.addActionTypes(cloneActionTypeList(new Vector<ActionType>(oldConfigData.getActionTypes())));
			newConfigData.addProvisionTypes(cloneProvisionTypeList(new Vector<ProvisionType>(oldConfigData.getProvisionTypes())));
		}
		
		return newConfigData;
		
	}
	
	/**
	 * Does a deep-copy of List<{@link AnalysisType}> oldList
	 * @param oldList
	 * @return List<{@link AnalysisType}>
	 */
	private static List<AnalysisType> cloneAnalysisTypeList(List<AnalysisType> oldList){
		List<AnalysisType> newList = new Vector<AnalysisType>();
		if(oldList != null){
			for(AnalysisType an : oldList){
				newList.add(cloneAnalysisType(an));
			}
		}
		
		return newList;
	}
	
	/**
	 * Does a deep-copy of {@link AnalysisType} oldAnalysisType
	 * @param oldAnalysisType
	 * @return {@link AnalysisType}
	 */
	private static AnalysisType cloneAnalysisType(AnalysisType oldAnalysisType){
		AnalysisType newAnalysisType = null;
		String id = new String(oldAnalysisType.getServiceID().getTypeID()); //IdGenerator.getNewPatternId();
		//idMap.put(oldAnalysisType.getServiceID().getTypeID(), id);
		String newAgentId = idMap.get(oldAnalysisType.getServiceID().getAgentID());
		ServiceID serviceId = new ServiceID(newAgentId, id, ServiceClass.ANALYSIS);
		if(oldAnalysisType instanceof CounterAnalysisType){
			List<CounterCriterionDef> counterCriteria = ((CounterAnalysisType)oldAnalysisType).getCounterCriteria();
			CounterDef newCounterDef = cloneCounterDef(((CounterAnalysisType) oldAnalysisType).getCounterDefinition());
			newAnalysisType = new CounterAnalysisType(serviceId, newCounterDef, cloneCounterCriterionDefList(counterCriteria));
			newAnalysisType.setName(new String(oldAnalysisType.getName()));
			if(oldAnalysisType.getDescription() != null){
				newAnalysisType.setDescription(oldAnalysisType.getDescription());
			}
			newAnalysisType.setFilterDefs(clonePatternFilterDefList(oldAnalysisType.getFilterDefs()));
			newAnalysisType.setOnlyPositiveInstances(oldAnalysisType.isOnlyPositiveInstances());
			
		} else if(oldAnalysisType instanceof StructureAnalysisType){
			newAnalysisType = new StructureAnalysisType(serviceId);
			((StructureAnalysisType)newAnalysisType).setPattern(
						cloneStructuralPattern(((StructureAnalysisType) oldAnalysisType).getPattern(), id));
			String ont = ((StructureAnalysisType) oldAnalysisType).getOntologyID();
			((StructureAnalysisType)newAnalysisType).setOntologyID(new String(ont));
			
			newAnalysisType.setName(new String(oldAnalysisType.getName()));
			if(oldAnalysisType.getDescription() != null){
				newAnalysisType.setDescription(oldAnalysisType.getDescription());
			}
			newAnalysisType.setFilterDefs(clonePatternFilterDefList(oldAnalysisType.getFilterDefs()));
			newAnalysisType.setOnlyPositiveInstances(oldAnalysisType.isOnlyPositiveInstances());
			
		} else if(oldAnalysisType instanceof JessAnalysisType){
			newAnalysisType = new JessAnalysisType(AnalysisResultDatatype.valueOf(((JessAnalysisType)oldAnalysisType).getResultDatatype().toString()), serviceId, new String(((JessAnalysisType)oldAnalysisType).getDefinition()));
			newAnalysisType.setName(new String(oldAnalysisType.getName()));
			if(oldAnalysisType.getDescription() != null){
				newAnalysisType.setDescription(oldAnalysisType.getDescription());
			}
			
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneAnalysisType] unknown type for oldAnalysisType:" + oldAnalysisType + " creating ...");
		}
		return newAnalysisType;
	}
	
	/**
	 * Does a deep-copy of {@link StructuralPattern} oldPat
	 * @param oldPat
	 * @param patternId
	 * @return {@link StructuralPattern}
	 */
	private static StructuralPattern cloneStructuralPattern(StructuralPattern oldPat, String patternId){
		StructuralPattern newPat = null;
		if(oldPat != null){
			newPat = new StructuralPattern(patternId);
			newPat.setSupplData(cloneStructuralPatternSuppl(oldPat.getSupplData()));
			cloneAllNodeAndLinkVariable(newPat, oldPat, new LinkedHashMap<ElementVariable, ElementVariable>());
			newPat.setNotPatterns(cloneNotPatterns((Set<StructuralPattern>)oldPat.getNotPatterns()));
		}
		return newPat;
	}
	
	private static Set<StructuralPattern> cloneNotPatterns(Set<StructuralPattern> oldList){
		Set<StructuralPattern> newList = new TreeSet<StructuralPattern>();
		for(StructuralPattern structPat: oldList){
			String id = IdGenerator.getNewPatternId(); //TODO get new id from server
			newList.add(cloneStructuralPattern(structPat, id));
		}
		return newList;
	}
	
	
	private static void cloneAllNodeAndLinkVariable(StructuralPattern newPat, StructuralPattern oldPat, Map<ElementVariable, ElementVariable> nodeLinkMap){
		for(NodeVariable elemVar : oldPat.getNodeVars()){
			NodeVariable newNode = new NodeVariable(new String(elemVar.getVarID()));
			newPat.addNodeVar(newNode);
			nodeLinkMap.put(elemVar, newNode);
		}
		for(LinkVariable elemVar : oldPat.getLinkVars()){
			LinkVariable newLink = new LinkVariable(new String(elemVar.getVarID()));
			newPat.addLinkVar(newLink);
			nodeLinkMap.put(elemVar, newLink);
		}
		for(ElementVariable oldElementVariable : nodeLinkMap.keySet()){
			clonePropConstrs(nodeLinkMap.get(oldElementVariable), oldElementVariable, nodeLinkMap);
		}
	}
	
	private static List<PropConstr> clonePropConstrs(ElementVariable newElementVariable, ElementVariable oldElementVariable, Map<ElementVariable, ElementVariable> nodeLinkMap){
		List<PropConstr> newList = new Vector<PropConstr>();
		
		for (PropConstr oldPropConstr : oldElementVariable.getPropConstrs()) {
			PropConstr newPropConstr = clonePropConstr(newElementVariable, oldPropConstr);
			if (oldPropConstr.getComparisons().size() > 0) {
				for(Comparison oldComparison : oldPropConstr.getComparisons()){
					newPropConstr.addComparisonIfNotExists(cloneComparison(newPropConstr, oldComparison, nodeLinkMap));
				}
			}
			newList.add(newPropConstr);
		}
		return newList;
	}
	
	private static PropConstr clonePropConstr(ElementVariable newElementVariable, PropConstr oldPropConstr){
		PropConstr newPropConstr = new PropConstr();
		ElementVariableProp oldElemVarProp = oldPropConstr.getPropVar();
		ElementVariableProp lhsVarProp = newElementVariable.getPropVar(oldElemVarProp.getPropId(), PropDescr.DEFAULT_COMPONENT_ID);
		newPropConstr.setPropVar(lhsVarProp);
		return newPropConstr;
	}
	
	private static Comparison cloneComparison(PropConstr newPropConstr, Comparison oldComparison, Map<ElementVariable, ElementVariable> nodeLinkMap){
		Comparison newComparison = null;
		
		ElementVariableProp oldLeftExpr = oldComparison.getLeftExpr();
		ElementVariableProp leftExpr = new ElementVariableProp(newPropConstr.getPropVar().getElementVar(), 
				new String(oldLeftExpr.getPropId()), new String(oldLeftExpr.getCompID()));
		
		if(oldComparison instanceof String2ConstStringComparison){
			String rightExpr = ((String2ConstStringComparison) oldComparison).getRightExpr();
			String2StringOperator oldOperator = ((String2ConstStringComparison) oldComparison).getOperator();
			newComparison = new String2ConstStringComparison();
			newComparison.setLeftExpr(leftExpr);
			((String2ConstStringComparison)newComparison).setOperator(String2StringOperator.fromString(oldOperator.getName()));
			((String2ConstStringComparison)newComparison).setRightExpr(new String(rightExpr));
		} else if(oldComparison instanceof String2VarStringComparison){
			ElementVariableProp oldRightExpr = ((String2VarStringComparison) oldComparison).getRightExpr();
			ElementVariable newRefElem = nodeLinkMap.get(oldRightExpr.getElementVar());
			ElementVariableProp rightExpr = newRefElem.getPropVar(new String(oldRightExpr.getPropId()), 
					new String(oldRightExpr.getCompID()));
			String2StringOperator oldOperator = ((String2VarStringComparison) oldComparison).getOperator();
			newComparison = new String2VarStringComparison();
			newComparison.setLeftExpr(leftExpr);
			((String2VarStringComparison)newComparison).setOperator(String2StringOperator.fromString(oldOperator.getName()));
			((String2VarStringComparison)newComparison).setRightExpr(rightExpr);

		} else if(oldComparison instanceof String2ConstSetComparison){
			List<String> rightExpr = ((String2ConstSetComparison) oldComparison).getRightExpr();
			String2SetOperator oldOperator = ((String2ConstSetComparison) oldComparison).getOperator();
			newComparison = new String2ConstSetComparison();
			newComparison.setLeftExpr(leftExpr);
			((String2ConstSetComparison)newComparison).setOperator(String2SetOperator.fromString(oldOperator.getName()));
			List<String> list = null;
			if(rightExpr != null){
				list = new Vector<String>();
				for(String s : rightExpr){
					list.add(new String(s));
				}
			}
			((String2ConstSetComparison)newComparison).setRightExpr(list);
		} else if(oldComparison instanceof String2VarSetComparison){
			ElementVariableProp oldRightExpr = ((String2VarSetComparison) oldComparison).getRightExpr();
			ElementVariable newRefElem = nodeLinkMap.get(oldRightExpr.getElementVar());
			ElementVariableProp rightExpr = newRefElem.getPropVar(new String(oldRightExpr.getPropId()), 
					new String(oldRightExpr.getCompID()));
			String2SetOperator oldOperator = ((String2VarSetComparison) oldComparison).getOperator();
			newComparison = new String2VarSetComparison();
			newComparison.setLeftExpr(leftExpr);
			((String2VarSetComparison)newComparison).setOperator(String2SetOperator.fromString(oldOperator.getName()));
			((String2VarSetComparison)newComparison).setRightExpr(rightExpr);

		} else if(oldComparison instanceof Num2ConstNumComparison){
			int rightExpr = ((Num2ConstNumComparison) oldComparison).getRightExpr();
			Num2NumOperator oldOperator = ((Num2ConstNumComparison) oldComparison).getOperator();
			newComparison = new Num2ConstNumComparison();
			newComparison.setLeftExpr(leftExpr);
			((Num2ConstNumComparison)newComparison).setOperator(Num2NumOperator.fromString(oldOperator.getName()));
			((Num2ConstNumComparison)newComparison).setRightExpr(rightExpr);
		} else if(oldComparison instanceof Num2VarNumComparison){
			ElementVariableProp oldRightExpr = ((Num2VarNumComparison) oldComparison).getRightExpr();
			ElementVariable newRefElem = nodeLinkMap.get(oldRightExpr.getElementVar());
			ElementVariableProp rightExpr = newRefElem.getPropVar(new String(oldRightExpr.getPropId()), 
					new String(oldRightExpr.getCompID()));
			Num2NumOperator oldOperator = ((Num2VarNumComparison) oldComparison).getOperator();
			newComparison = new Num2VarNumComparison();
			newComparison.setLeftExpr(leftExpr);
			((Num2VarNumComparison)newComparison).setOperator(Num2NumOperator.fromString(oldOperator.getName()));
			((Num2VarNumComparison)newComparison).setOffset(((Num2VarNumComparison) oldComparison).getOffset());
			((Num2VarNumComparison)newComparison).setRightExpr(rightExpr);
			
		} else if(oldComparison instanceof Set2ConstStringComparison){
			String rightExpr = ((Set2ConstStringComparison) oldComparison).getRightExpr();
			Set2StringOperator oldOperator = ((Set2ConstStringComparison) oldComparison).getOperator();
			newComparison = new Set2ConstStringComparison();
			newComparison.setLeftExpr(leftExpr);
			((Set2ConstStringComparison)newComparison).setOperator(Set2StringOperator.fromString(oldOperator.getName()));
			((Set2ConstStringComparison)newComparison).setRightExpr(new String(rightExpr));
		} else if(oldComparison instanceof Set2VarStringComparison){
			ElementVariableProp oldRightExpr = ((Set2VarStringComparison) oldComparison).getRightExpr();
			ElementVariable newRefElem = nodeLinkMap.get(oldRightExpr.getElementVar());
			ElementVariableProp rightExpr = newRefElem.getPropVar(new String(oldRightExpr.getPropId()), 
					new String(oldRightExpr.getCompID()));
			Set2StringOperator oldOperator = ((Set2VarStringComparison) oldComparison).getOperator();
			newComparison = new Set2VarStringComparison();
			newComparison.setLeftExpr(leftExpr);
			((Set2VarStringComparison)newComparison).setOperator(Set2StringOperator.fromString(oldOperator.getName()));
			((String2VarStringComparison)newComparison).setRightExpr(rightExpr);
		} else if(oldComparison instanceof Set2ConstSetComparison){
			List<String> rightExpr = ((Set2ConstSetComparison) oldComparison).getRightExpr();
			Set2SetOperator oldOperator = ((Set2ConstSetComparison) oldComparison).getOperator();
			newComparison = new Set2ConstSetComparison();
			newComparison.setLeftExpr(leftExpr);
			((Set2ConstSetComparison)newComparison).setOperator(Set2SetOperator.fromString(oldOperator.getName()));
			List<String> list = null;
			if(rightExpr != null){
				list = new Vector<String>();
				for(String s : rightExpr){
					list.add(new String(s));
				}
			}
			((Set2ConstSetComparison)newComparison).setRightExpr(list);
		} else if(oldComparison instanceof Set2VarSetComparison){
			ElementVariableProp oldRightExpr = ((Set2VarSetComparison) oldComparison).getRightExpr();
			ElementVariable newRefElem = nodeLinkMap.get(oldRightExpr.getElementVar());
			ElementVariableProp rightExpr = newRefElem.getPropVar(new String(oldRightExpr.getPropId()), 
					new String(oldRightExpr.getCompID()));
			Set2SetOperator oldOperator = ((Set2VarSetComparison) oldComparison).getOperator();
			newComparison = new Set2VarSetComparison();
			newComparison.setLeftExpr(leftExpr);
			((Set2VarSetComparison)newComparison).setOperator(Set2SetOperator.fromString(oldOperator.getName()));
			((Set2VarSetComparison)newComparison).setRightExpr(rightExpr);
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneComparison] comparison:" + oldComparison);
		}
		return newComparison;
	}

	
	/**
	 *  Does a deep-copy of {@link StructuralPatternSuppl} oldStructPatSuppl
	 * @param oldStructPatSuppl
	 * @return {@link StructuralPatternSuppl}
	 */
	private static StructuralPatternSuppl cloneStructuralPatternSuppl(StructuralPatternSuppl oldStructPatSuppl){
		StructuralPatternSuppl newStructPatSuppl = null;
		if(oldStructPatSuppl != null){
			newStructPatSuppl = new StructuralPatternSuppl();
			Map<Integer, List<String>> constantNumID2Values = new HashMap<Integer, List<String>>();
			
			for(Integer key : oldStructPatSuppl.getConstantNumID2Values().keySet()){
				List<String> list = oldStructPatSuppl.getConstantNumID2Values().get(key);
				List<String> newList = new Vector<String>();
				if(list != null){
					for(String val : list){
						newList.add(new String(val));
					}
				}
				constantNumID2Values.put(new Integer(key), newList);
			}
		}
		return newStructPatSuppl;
	}
	
	/**
	 * Does a deep-copy of {@link CounterDef} oldCounterDef
	 * @param oldCounterDef
	 * @return {@link CounterDef}
	 */
	private static CounterDef cloneCounterDef(CounterDef oldCounterDef){
		CounterDef newCounterDef = null;
		if(oldCounterDef != null){
			InstanceTypeGeneral instanceTypeGeneral = oldCounterDef.getInstanceTypeGeneral();
			List<InstanceTypeSpecific> newList = cloneInstanceTypeSpecificList(oldCounterDef.getInstanceTypesSpecific());
			newCounterDef = new CounterDef(InstanceTypeGeneral.valueOf(instanceTypeGeneral.toString()), newList);
			
//			newCounterDef = new CounterDef(UserSelectionSetting.ONE, new String(oldCounterDef.getUserID()), UserRoleSetting.NONE, 
//					InstanceTypeGeneral.valueOf(instanceTypeGeneral.toString()), newList, 
//					new Integer(oldCounterDef.getMinAgeInSecs()), new Integer(oldCounterDef.getMaxAgeInSecs()));
			//CounterDef counterDef = new CounterDef(UserSelectionSetting.ONE, "", UserRoleSetting.NONE, InstanceTypeGeneral.NODE, new Vector<InstanceTypeSpecific>(), Integer.valueOf(0), Integer.valueOf(0));
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneCounterDef] oldCounterDef:" + oldCounterDef + " is null");
		}
		return newCounterDef;
	}
	
	/**
	 * Does a deep-copy of List<{@link InstanceTypeSpecific}> oldList
	 * @param oldList
	 * @return List<{@link InstanceTypeSpecific}>
	 */
	private static List<InstanceTypeSpecific> cloneInstanceTypeSpecificList(List<InstanceTypeSpecific> oldList){
		List<InstanceTypeSpecific> newList = new Vector<InstanceTypeSpecific>();
		if(oldList != null){
			for(InstanceTypeSpecific instanceType : oldList){
				newList.add(cloneInstanceTypeSpecific(instanceType));
			}
		}
		return newList;
	}

	/**
	 * Does a deep-copy of {@link InstanceTypeSpecific} oldInstanceType
	 * @param oldInstanceType
	 * @return {@link InstanceTypeSpecific}
	 */
	private static InstanceTypeSpecific cloneInstanceTypeSpecific(InstanceTypeSpecific oldInstanceType){
		InstanceTypeSpecific newInstanceType = null;
		if(oldInstanceType instanceof InstanceTypeSpecific_Object){
			String typeID = ((InstanceTypeSpecific_Object) oldInstanceType).getTypeID();
			newInstanceType = new InstanceTypeSpecific_Object(new String(typeID));
		} else if(oldInstanceType instanceof InstanceTypeSpecific_Pattern){
			String agentId = idMap.get(((InstanceTypeSpecific_Pattern) oldInstanceType).getAgentID()); //agent Id
			String typeId = new String(((InstanceTypeSpecific_Pattern) oldInstanceType).getTypeID());
			//String typeId = idMap.get(((InstanceTypeSpecific_Pattern) oldInstanceType).getTypeID()); //pattern Id
			newInstanceType = new InstanceTypeSpecific_Pattern(agentId, typeId);
		}
		return newInstanceType;
	}
	
	/**
	 * Does a deep-copy of List<{@link CounterCriterionDef}> oldList
	 * @param oldList
	 * @return List<{@link CounterCriterionDef}>
	 */
	private static List<CounterCriterionDef> cloneCounterCriterionDefList(List<CounterCriterionDef> oldList){
		List<CounterCriterionDef> newList = new Vector<CounterCriterionDef>();
		if(oldList != null){
			for(CounterCriterionDef countCrit : oldList){
				newList.add(cloneCounterCriterionDef(countCrit));
			}
		}
		return newList;
	}
	
	/**
	 * Does a deep-copy of {@link CounterCriterionDef} oldCounterCrit
	 * @param oldCounterCrit
	 * @return {@link CounterCriterionDef}
	 */
	private static CounterCriterionDef cloneCounterCriterionDef(CounterCriterionDef oldCounterCrit){
		CounterCriterionOperator oldOper = oldCounterCrit.getOperator(); 
		CounterCriterionDef newCounterCrit = new CounterCriterionDef(CounterCriterionOperator.valueOf(oldOper.toString()), oldCounterCrit.getReferenceValue());
		return newCounterCrit;
	}
	
	/**
	 * Does a deep-copy of List<{@linkPatternFilterDef}> oldList
	 * @param oldList
	 * @return List<{@linkPatternFilterDef}>
	 */
	private static List<PatternFilterDef> clonePatternFilterDefList(List<PatternFilterDef> oldList){
		List<PatternFilterDef> newlist = new Vector<PatternFilterDef>();
		if(oldList != null){
			for(PatternFilterDef patFilter :  oldList){
				newlist.add(clonePatternFilterDef(patFilter));
			}
		}
		return newlist;
	}
	
	/**
	 * Does a deep-copy of {@linkPatternFilterDef} oldPatternFilter
	 * @param oldPatternFilter
	 * @return {@linkPatternFilterDef}
	 */
	private static PatternFilterDef clonePatternFilterDef(PatternFilterDef oldPatternFilter){
		PatternFilterDef newPatternFilter = null;
		if(oldPatternFilter instanceof PatternFilterDef_LastModTime){
			PatternFilterDef_LastModTimeSetting val = ((PatternFilterDef_LastModTime)oldPatternFilter).getSetting();
			Integer refVal = ((PatternFilterDef_LastModTime)oldPatternFilter).getReferenceValue();
			newPatternFilter = new PatternFilterDef_LastModTime(PatternFilterDef_LastModTimeSetting.valueOf(val.toString()), 
					(refVal!=null)?new Integer(refVal):refVal);
		} else if(oldPatternFilter instanceof PatternFilterDef_User){
			PatternFilterDef_UserSetting val = ((PatternFilterDef_User)oldPatternFilter).getSetting();
			newPatternFilter = new PatternFilterDef_User(PatternFilterDef_UserSetting.valueOf(val.toString()));
		}
		return newPatternFilter;
	}
	
	/**
	 * Does a deep-copy of List<{@link ProvisionType}> oldList
	 * @param oldList
	 * @return List<{@link ProvisionType}>
	 */
	private static List<ProvisionType> cloneProvisionTypeList(List<ProvisionType> oldList){
		List<ProvisionType> newList = new Vector<ProvisionType>();
		if(oldList != null){
			for(ProvisionType prov : oldList){
				newList.add(cloneProvisionType(prov));
			}
		}
		return newList;
	}
	
	/**
	 * Does a deep-copy of {@link ProvisionType} oldProvisionType
	 * @param oldProvisionType
	 * @return {@link ProvisionType}
	 */
	private static ProvisionType cloneProvisionType(ProvisionType oldProvisionType){
		ProvisionType newProvisionType;
		String id = new String(oldProvisionType.getServiceID().getTypeID()); //IdGenerator.getNewProvisionId();
		//idMap.put(oldProvisionType.getServiceID().getTypeID(), id);
		String newAgentId = idMap.get(oldProvisionType.getServiceID().getAgentID());
		ServiceID serviceId = new ServiceID(newAgentId, id, ServiceClass.PROVISION);
		
		if(oldProvisionType instanceof PriorityProvisionType){
			newProvisionType = new PriorityProvisionType(serviceId);
			//basic
			newProvisionType.setName(new String(oldProvisionType.getName()));
			if(oldProvisionType.getDescription() != null)
				newProvisionType.setDescription(new String(oldProvisionType.getDescription()));
			//ProvisionType
			ProvisionTimeDef oldProv = ((PriorityProvisionType)oldProvisionType).getProvisionTime();
			((PriorityProvisionType)newProvisionType).setProvisionTime(cloneProvisionTimeDef(oldProv));
			RecipientDef oldRecipient = ((PriorityProvisionType)oldProvisionType).getRecipient();
			((PriorityProvisionType)newProvisionType).setRecipient(cloneRecipientDef(oldRecipient));
			ActionListDef oldActionList = ((PriorityProvisionType)oldProvisionType).getProvidedActions();
			((PriorityProvisionType)newProvisionType).setProvidedActions(cloneActionListDef(oldActionList));
			
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneProvisionType] unknown type for oldProvisionType:" + oldProvisionType + " creating PriorityProvisionType");
			newProvisionType = new PriorityProvisionType(serviceId);
		}
		return newProvisionType;
	}
	
	/**
	 * Does a deep-copy of {@link ActionListDef} oldActionList
	 * @param oldActionList
	 * @return {@link ActionListDef}
	 */
	private static ActionListDef cloneActionListDef(ActionListDef oldActionList){
		ActionListDef newActionList = null;
		if(oldActionList != null){
			if(oldActionList.isAllOwnActionTypes()){
				newActionList = new ActionListDef(true);
			} else{
				List<ServiceID> serviceIDs = new Vector<ServiceID>();
				for(ServiceID sId : oldActionList.getServiceIDs()){
					String newAgentId = idMap.get(sId.getAgentID());
					String newTypeId = new String(sId.getTypeID()); //idMap.get(sId.getTypeID());
					ServiceID serviceId = new ServiceID(newAgentId, newTypeId, ServiceClass.valueOf(sId.getServiceClass().toString()));
					serviceIDs.add(serviceId);
				}
				newActionList = new ActionListDef(serviceIDs);
			}
		}
		return newActionList;
	}
	
	/**
	 * Does a deep-copy of {@link RecipientDef} oldRecipient
	 * @param oldRecipient
	 * @return
	 */
	private static RecipientDef cloneRecipientDef(RecipientDef oldRecipient){
		RecipientDef newRecipient = null;
		if(oldRecipient != null){
			if(oldRecipient instanceof RecipientDef_Group){
				newRecipient = new RecipientDef_Group();
			} else if(oldRecipient instanceof RecipientDef_Individuals){
				newRecipient = new RecipientDef_Individuals();
			} else{
				FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneRecipientDef] unknown type for oldRecipient:" + oldRecipient + " creating RecipientDef_Group");
				newRecipient = new RecipientDef_Group();
			}
		}
		return newRecipient;
	}
	
	/**
	 * Does a deep-copy of {@link ProvisionTimeDef} oldProv
	 * @param oldProv
	 * @return
	 */
	private static ProvisionTimeDef cloneProvisionTimeDef(ProvisionTimeDef oldProv){
		ProvisionTimeDef newProvisionTimeDef = null;
		if(oldProv != null){
			if(oldProv instanceof ProvisionTimeDef_OnRequest){
				String name = ((ProvisionTimeDef_OnRequest)oldProv).getDisplayName();
				newProvisionTimeDef = new ProvisionTimeDef_OnRequest((new String(name)));
			} else if(oldProv instanceof ProvisionTimeDef_Periodically){
				Long checkInterval = ((ProvisionTimeDef_Periodically)oldProv).getCheckInterval();
				newProvisionTimeDef = new ProvisionTimeDef_Periodically(new Long(checkInterval));
			} else{
				FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneProvisionTimeDef] unknown type for oldProv:" + oldProv + " creating ProvisionTimeDef_OnRequest");
				newProvisionTimeDef = new ProvisionTimeDef_OnRequest((new String("unknown")));
			}
		}
		return newProvisionTimeDef;
	}
	
	/**
	 * Does a deep-copy of List<{@link ActionType}> oldList
	 * @param oldList
	 * @return List<{@link ActionType}>
	 */
	private static List<ActionType> cloneActionTypeList(List<ActionType> oldList){
		List<ActionType> newList = new Vector<ActionType>();
		if(oldList != null){
			for(ActionType actionType : oldList){
				newList.add(cloneActionType(actionType));
			}
		}
		return newList;
	}
	
	/**
	 * Does a deep-copy of {@link ActionType} oldActionType i.e. a Feedback ActionType
	 * @param oldActionType
	 * @return {@link ActionType}
	 */
	private static ActionType cloneActionType(ActionType oldActionType){
		ActionType newActionType;
		String id = new String(oldActionType.getServiceID().getTypeID()); //IdGenerator.getNewFeedbackId();
		//idMap.put(oldActionType.getServiceID().getTypeID(), id);
		String newAgentId = idMap.get(oldActionType.getServiceID().getAgentID());
		ServiceID serviceId = new ServiceID(newAgentId, id, ServiceClass.ACTION);		
		if(oldActionType instanceof FeedbackActionType){
			ServiceID trigger = ((FeedbackActionType)oldActionType).getTriggerID();
			if(trigger != null){
				String newTriggerAgentId = idMap.get(trigger.getAgentID());
				String newTriggerPatId = new String(trigger.getTypeID());// idMap.get(trigger.getTypeID());
				ServiceID triggerServiceId = new ServiceID(newTriggerAgentId, newTriggerPatId, ServiceClass.valueOf(trigger.getServiceClass().toString()));
				newActionType = new FeedbackActionType(serviceId, triggerServiceId);
			} else{
				newActionType = new FeedbackActionType(serviceId, null);
			}
			
			//basic
			newActionType.setName(new String(oldActionType.getName()));
			if(oldActionType.getDescription() != null)
				newActionType.setDescription(new String(oldActionType.getDescription()));
			newActionType.setPriorityDef(clonePriorityDef(oldActionType.getPriorityDef()));
			//Content
			((FeedbackActionType)newActionType).setMsgCompDefs(cloneMsgCompDefList(((FeedbackActionType) oldActionType).getMsgCompDefs()));
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneActionType] unknown type for oldActionType:" + oldActionType + " creating FeedbackActionType");
			newActionType = new FeedbackActionType(serviceId, null);
			newActionType.setName(new String(oldActionType.getName()));
			newActionType.setDescription(new String(oldActionType.getDescription()));
			newActionType.setPriorityDef(clonePriorityDef(oldActionType.getPriorityDef()));
		}
		return newActionType;
	}
	
	/**
	 * Does a deep-copy of List<{@link MsgCompDef}> oldList
	 * @param oldList
	 * @return List<{@link MsgCompDef}>
	 */
	private static List<MsgCompDef> cloneMsgCompDefList(List<MsgCompDef> oldList){
		List<MsgCompDef> newList = new Vector<MsgCompDef>();
		if(oldList != null){
			for(MsgCompDef msg : oldList){
				newList.add(cloneMsgCompDef(msg));
			}
		}
		return newList;
	}
	
	/**
	 * Does a deep-copy of {@link MsgCompDef} oldMsg
	 * @param oldMsg
	 * @return
	 */
	private static MsgCompDef cloneMsgCompDef(MsgCompDef oldMsg){
		MsgCompDef newMsg;
		if(oldMsg instanceof MsgCompDef_ShortText){
			newMsg = new MsgCompDef_ShortText(new String(((MsgCompDef_ShortText)oldMsg).getText()));
		} else if(oldMsg instanceof MsgCompDef_LongText){
			newMsg = new MsgCompDef_LongText(new String(((MsgCompDef_LongText)oldMsg).getText()));
		} else if(oldMsg instanceof MsgCompDef_Highlighting){
			newMsg = new MsgCompDef_Highlighting();
		} else{
			FATDebug.print(FATDebug.ERROR, "[CloneAgentDescriptionFE][cloneMsgCompDef] unknown type for oldMsg:" + oldMsg + " creating MsgCompDef_Highlighting");
			newMsg = new MsgCompDef_Highlighting();
		}
		return newMsg;
	}
	
	/**
	 * Does a deep-copy of {@link PriorityDef} oldPriorityDef
	 * @param oldPriorityDef
	 * @return {@link PriorityDef}
	 */
	private static PriorityDef clonePriorityDef(PriorityDef oldPriorityDef){
		PriorityDef newPriorityDef = new PriorityDef();
		if(oldPriorityDef != null){
			newPriorityDef.setDefaultPriority(new Integer(oldPriorityDef.getDefaultPriority()));
			newPriorityDef.setDefaultRepresentativeness(new Integer(oldPriorityDef.getDefaultRepresentativeness()));
			for(String phaseId : oldPriorityDef.getPhase2Priority().keySet()){
				String newId = new String(phaseId);// idMap.get(phaseId);
				newPriorityDef.addPhasePriority(newId, new Integer(oldPriorityDef.getPhase2Priority().get(phaseId)));
			}
			for(String phaseId : oldPriorityDef.getPhase2Representativeness().keySet()){
				String newId = new String(phaseId);// idMap.get(phaseId);
				newPriorityDef.addPhaseRepresentativeness(newId, new Integer(oldPriorityDef.getPhase2Representativeness().get(phaseId)));
			}
		}
		return newPriorityDef;
	}
	
	/**
	 * Does a deep-copy of {@link PhaseModelerDef} oldPhaseMod
	 * @param oldPhaseMod
	 * @return {@link PhaseModelerDef}
	 */
	private static PhaseModelerDef clonePhaseModelerDef(PhaseModelerDef oldPhaseMod){
		PhaseModelerDef newPhaseMod = null;
		PhasesDef phasesDef;
		if(oldPhaseMod == null){
			phasesDef = clonePhasesDef(null);
		} else{
			phasesDef = clonePhasesDef(oldPhaseMod.getPhaseDef());
		}
		if(oldPhaseMod instanceof PhaseModelerDef_Empirical){
			ActionListDef newActionListDef = cloneActionListDef(((PhaseModelerDef_Empirical) oldPhaseMod).getRelevantActions());
			newPhaseMod = new PhaseModelerDef_Empirical(phasesDef, newActionListDef);
		}
		return newPhaseMod;
	}
	
	/**
	 * Does a deep-copy of {@link PhasesDef} oldPhasesDef
	 * @param oldPhasesDef
	 * @return {@link PhasesDef}
	 */
	private static PhasesDef clonePhasesDef(PhasesDef oldPhasesDef){
		PhasesDef newPhasesDef = new PhasesDef();
		if(oldPhasesDef != null){
			List<PhaseDef> phases = new Vector<PhaseDef>();
			for(PhaseDef phase : oldPhasesDef.getPhases()){
				phases.add(clonePhaseDef(phase));
			}
			newPhasesDef.setPhaseIDs(phases);
		}
		return newPhasesDef;
	}
	
	/**
	 * Does a deep-copy of {@link PhaseDef} oldPhaseDef
	 * @param oldPhaseDef
	 * @return {@link PhaseDef}
	 */
	private static PhaseDef clonePhaseDef(PhaseDef oldPhaseDef){
		PhaseDef newPhaseDef = new PhaseDef();
		String newPhaseId = new String(oldPhaseDef.getID()); //IdGenerator.getNewPhaseId();
		//idMap.put(oldPhaseDef.getID(), newPhaseId);
		newPhaseDef.setID(newPhaseId);
		newPhaseDef.setName(new String(oldPhaseDef.getName()));
		newPhaseDef.setDescription(new String(oldPhaseDef.getDescription()));
		return newPhaseDef;
	}
	
	/**
	 * Does a deep-copy of {@link SupportedOntologiesDef} oldSupportedOntologiesDef
	 * @param oldSupportedOntologiesDef
	 * @return {@link SupportedOntologiesDef}
	 */
	private static SupportedOntologiesDef cloneSupportedOntologiesDef(SupportedOntologiesDef oldSupportedOntologiesDef){
		SupportedOntologiesDef supportedOntologiesDef;
		
		if(oldSupportedOntologiesDef == null){
			supportedOntologiesDef = new SupportedOntologiesDef(new Vector<String>());
		} else if(oldSupportedOntologiesDef.isAllOntologies()){
			supportedOntologiesDef = new SupportedOntologiesDef(true);
		} else{
			List<String> ontologies = new Vector<String>();
			for(String ont : oldSupportedOntologiesDef.getSupportedOntologies()){
				ontologies.add(new String(ont));
			}
			supportedOntologiesDef = new SupportedOntologiesDef(ontologies);
		}
		return supportedOntologiesDef;
	}
	
	/**
	 * Gets a new unique name for the agent.
	 * @param oldName
	 * @return String containing the new name
	 */
	public static String getNewName(String oldName){
		int counter = 1;
		String agentName;
		do{
			agentName = new String(oldName + "-" + FeedbackAuthoringStrings.COPY_LABEL + counter++);
			FATDebug.print(FATDebug.DEBUG, "[CloneAgentDescriptionFE][getNewName] checking:" + agentName);
		}while(checkIfNameExist(agentName));
		return agentName;
	}
	
	/**
	 * Checks if agentName is being used by another agent.
	 * @param agentName
	 * @return true if agentName is being used, false otherwise.
	 */
	public static boolean checkIfNameExist(String agentName){
		boolean retVal = false;
		retVal = FeedbackAuthoringTabContent.getInstance().existAgentByNameDB(agentName);
		return retVal;
	}

}
