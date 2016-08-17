package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.OntologyFA;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.CustomizedGrid;
import lasad.shared.dfki.meta.agents.action.ActionType;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.provision.priority.MsgSortCriterion;
import lasad.shared.dfki.meta.agents.provision.priority.MsgSortCriterionUtil;

/**
 * Class to convert Object into {@link ElementModel}, so they can be displayed in {@link CustomizedGrid}
 * @author Anahuac
 *
 */
public class Data2ModelConverter {
	
	public static List<ElementModel> getOntAsModel(List<OntologyFA> list){
        List<ElementModel> listModel = new ArrayList<ElementModel>();
        for(OntologyFA ont:list){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.NAME, ont.getName());
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
	public static List<ElementModel> getOntListAsModelList(List<String> list){
        List<ElementModel> listModel = new ArrayList<ElementModel>();
        for(String ont:list){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.NAME, ont);
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
	
	public static List<ElementModel> getPatternCollectionAsModelList(Collection<AnalysisType> patternList){
		List<ElementModel> listModel = new ArrayList<ElementModel>();
        for(AnalysisType pat:patternList){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, pat.getServiceID().getTypeID());
			map.put(GridElementLabel.NAME, pat.getName());
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
	
	public static List<ElementModel> getMsgSortCriterionListAsModelList(List<MsgSortCriterion> list){
		List<ElementModel> listModel = new Vector<ElementModel>();
        for(MsgSortCriterion elem:list){
        	String tmp = MsgSortCriterionUtil.getMsgSortCriterionAsString(elem);
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, tmp);
			map.put(GridElementLabel.NAME, tmp);
        	listModel.add(new ElementModel(map));
        }
        return listModel; 
	}
	
	public static List<ElementModel> getMsgListAsModelList(List<ActionType> list){
		List<ElementModel> listModel = new Vector<ElementModel>();
        for(ActionType elem:list){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, elem.getServiceID().getTypeID());
			map.put(GridElementLabel.NAME, elem.getName());
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
	
	public static List<ElementModel> getFeedbackCollectionAsModelList(Collection<ActionType> feedbackList){
		List<ElementModel> listModel = new ArrayList<ElementModel>();
        for(ActionType elem:feedbackList){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.ID, elem.getServiceID().getTypeID());
			map.put(GridElementLabel.NAME, elem.getName());
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
	
	public static List<ElementModel> getMapAsModelList(Map<String, String> map){
		List<ElementModel> listModel = new Vector<ElementModel>();
        for(String key:map.keySet()){
        	Map<String, String> tmp = new HashMap<String, String>();
        	tmp.put(GridElementLabel.ID, key);
        	tmp.put(GridElementLabel.NAME, map.get(key));
        	listModel.add(new ElementModel(tmp));
        }
        return listModel;
	}
	public static List<ElementModel> getOperatorMapAsModelList(Map<String, String> map){
		List<ElementModel> listModel = new Vector<ElementModel>();
        for(String key:map.keySet()){
        	Map<String, String> tmp = new HashMap<String, String>();
        	tmp.put(GridElementLabel.ID, key);
        	tmp.put(GridElementLabel.NAME, map.get(key));
        	tmp.put(GridElementLabel.VAL, ComparisonUtil.fromOperId2Str(key));
        	listModel.add(new ElementModel(tmp));
        }
        return listModel;
	}
	
	public static List<ElementModel> getStrAsModel(List<String> agList){
        List<ElementModel> listModel = new ArrayList<ElementModel>();
        for(String ag:agList){
        	Map<String, String> map = new HashMap<String, String>();
			map.put(GridElementLabel.NAME, ag);
        	listModel.add(new ElementModel(map));
        }
        return listModel;
	}
//	public static List<ElementModel> getMapAsModel(Map<String,String> map){
//        List<ElementModel> listModel = new ArrayList<ElementModel>();
//        Set<String> agIdSet = map.keySet();
//        for(String agId:agIdSet){
//        	Map<String, String> tmp = new HashMap<String, String>();
//			tmp.put(GridElementLabel.ID, new String(agId));
//			tmp.put(GridElementLabel.NAME, map.get(agId));
//        	listModel.add(new ElementModel(tmp));
//        }
//        return listModel;
//	}
	
	
}
