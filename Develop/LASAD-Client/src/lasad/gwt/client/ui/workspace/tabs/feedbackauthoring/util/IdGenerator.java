package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import java.util.Random;

/**
 * Generates unique Ids
 * @author Anahuac
 *
 */
public class IdGenerator {
	final static Random generator = new Random(System.currentTimeMillis());
	
	public static final String AGENT_PREFIX="Agent";
	public static final String PATTERN_PREFIX="Pattern_";
	public static final String PHASE_PREFIX="phase_";
	public static final String FEEDBACK_PREFIX="feedback_";
	public static final String PHASE_PRIO_PREFIX="phaseprio_";
	public static final String PROVISION_PREFIX="provision_";
	public static final String CONSTRAINT_PREFIX="constraint_";
	
	private static int rootElementIdCounter = 0; //for Types "relation" || "emptyrelation" || "box" || "emptybox" 
	private static int elementIdCounter = 0; //for all elements
	private static int paternIdCounter = 0;
	//private static int constraintIdCounter = 0;
	
	private static final int LIMIT = 9999999;
	
	public static int getNewRequestId(){
		return Math.abs(generator.nextInt());
	}
	
	public static int getNewRootElementId(){
		return ++rootElementIdCounter;
	}
	public static int getNewElementId(){
		return ++elementIdCounter;
	}
	
	public static int getNewId(){
		return generator.nextInt(LIMIT);
	}
	public static String getNewPhaseId(){
		return PHASE_PREFIX + getNewId();
	}
	public static String getNewFeedbackId(){
		return FEEDBACK_PREFIX + getNewId();
	}
	public static String getNewPatternId(){
		//return PATTERN_PREFIX + generator.nextInt();
		return String.valueOf(++paternIdCounter);
	}
	public static String getNewAgentId(){
		return AGENT_PREFIX + Math.abs(generator.nextInt());
	}
	public static String getNewPhasePriorityId(){
		return PHASE_PRIO_PREFIX + getNewId();
	}
	public static String getNewProvisionId(){
		return PROVISION_PREFIX + getNewId();
	}
	public static String getNewConstraintId(){
		return CONSTRAINT_PREFIX + getNewId();
	}

}
