package lasad.shared.dfki.meta.agents.provision.priority;

import java.util.List;
import java.util.Vector;

public class MsgSortCriterionUtil {
	
	public static final String MSG_PRIORITY = "MsgPriority";
	public static final String MORE_RECENT_INSTANCES = "MoreRecentInstances";
	public static final String OLDER_INSTANCES = "OlderInstances";
	public static final String INSTANCES_NOT_YET_POINTED_TO = "InstancesNotYetPointedTo";
	public static final String INSTANCES_USER_CONTRIBUTED_TO = "InstancesUserContributedTo";
	public static final String INSTANCES_USER_OWNS = "InstancesUserOwns";
	public static final String INSTANCES_VISIBLE_ON_USER_SCREEN = "InstancesVisibleOnUserScreen";
	public static final String MSG_GROUP_PRIORITY = "MsgGroupPriority";
	
	public static List<MsgSortCriterion> getAllMsgSortCriterionAvailable(){
		List<MsgSortCriterion> msgList = new Vector<MsgSortCriterion>();
		msgList.add(new MsgSortCriterion_MsgPriority());
		msgList.add(new MsgSortCriterion_MoreRecentInstances());
//		msgList.add(new MsgSortCriterion_OlderInstances());
//		msgList.add(new MsgSortCriterion_InstancesNotYetPointedTo());
//		msgList.add(new MsgSortCriterion_InstancesUserContributedTo());
//		msgList.add(new MsgSortCriterion_InstancesUserOwns());
//		msgList.add(new MsgSortCriterion_InstancesVisibleOnUserScreen());
//		msgList.add(new MsgSortCriterion_MsgGroupPriority());
		
		return msgList;
	}
	
	public static String getMsgSortCriterionAsString(MsgSortCriterion msgSortCriterion){
		String retVal = null;
		if(msgSortCriterion instanceof MsgSortCriterion_MsgPriority){
			retVal = MSG_PRIORITY;
		} else if(msgSortCriterion instanceof MsgSortCriterion_MoreRecentInstances){
			retVal = MORE_RECENT_INSTANCES;
		} else if(msgSortCriterion instanceof MsgSortCriterion_OlderInstances){
			retVal = OLDER_INSTANCES;
		} else if(msgSortCriterion instanceof MsgSortCriterion_InstancesNotYetPointedTo){
			retVal = INSTANCES_NOT_YET_POINTED_TO;
		} else if(msgSortCriterion instanceof MsgSortCriterion_InstancesUserContributedTo){
			retVal = INSTANCES_USER_CONTRIBUTED_TO;
		} else if(msgSortCriterion instanceof MsgSortCriterion_InstancesUserOwns){
			retVal = INSTANCES_USER_OWNS;
		} else if(msgSortCriterion instanceof MsgSortCriterion_InstancesVisibleOnUserScreen){
			retVal = INSTANCES_VISIBLE_ON_USER_SCREEN;
		} else if(msgSortCriterion instanceof MsgSortCriterion_MsgGroupPriority){
			retVal = MSG_GROUP_PRIORITY;
		}
		return retVal;
	}
	
	public static MsgSortCriterion getMsgSortCriterionAsObj(String msgSortCriterion){
		MsgSortCriterion retVal = null;
		if(msgSortCriterion.equals(MSG_PRIORITY)){
			retVal = new MsgSortCriterion_MsgPriority();
		} else if(msgSortCriterion.equals(MORE_RECENT_INSTANCES)){
			retVal = new MsgSortCriterion_MoreRecentInstances();
		} else if(msgSortCriterion.equals(OLDER_INSTANCES)){
			retVal = new MsgSortCriterion_OlderInstances();
		} else if(msgSortCriterion.equals(INSTANCES_NOT_YET_POINTED_TO)){
			retVal = new MsgSortCriterion_InstancesNotYetPointedTo();
		} else if(msgSortCriterion.equals(INSTANCES_USER_CONTRIBUTED_TO)){
			retVal = new MsgSortCriterion_InstancesUserContributedTo();
		} else if(msgSortCriterion.equals(INSTANCES_USER_OWNS)){
			retVal = new MsgSortCriterion_InstancesUserOwns();
		} else if(msgSortCriterion.equals(INSTANCES_VISIBLE_ON_USER_SCREEN)){
			retVal = new MsgSortCriterion_InstancesVisibleOnUserScreen();
		} else if(msgSortCriterion.equals(MSG_GROUP_PRIORITY)){
			retVal = new MsgSortCriterion_MsgGroupPriority();
		}
		return retVal;
	}

}
