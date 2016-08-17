package lasad.shared.dfki.meta.util;

import java.util.Vector;

public class ConstantsFE {
	public static final String FEEDBACK_ENGINE_USER = "DFKI";
	public static final String DEVELOPER_ROLE = "Developer";
	public static final String TEACHER_ROLE = "Teacher";
	public static final String STANDARD_ROLE = "Standard";
	public static final String GUEST_ROLE = "Guest";
	private static final Vector<String> feedbackAuthoringValidRoles = new Vector<String>(); //valid roles for Feedback Authoring Tool
	
	static{
		feedbackAuthoringValidRoles.add(DEVELOPER_ROLE);
		//feedbackAuthoringValidRoles.add(TEACHER_ROLE);
	}
	
	public static Vector<String> getFeedbackAuthoringValidRoles(){
		return feedbackAuthoringValidRoles;
	}
	
}
