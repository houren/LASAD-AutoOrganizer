package lasad.processors.specific;

import lasad.controller.ManagementController;
import lasad.entity.ActionParameter;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.processors.ActionProcessor;

/** 
 * this class records all communications between users
 * 
 * @author ?
 */
public class CommunicationActionProcessor extends AbstractActionObserver
		implements ActionObserver {
	
	public CommunicationActionProcessor(){
		super();
	}

	/** 
	 * After a communication happening each time, the revision is updated to record this communication
	 * 
	 * @param a      a specific LASAD action  
	 * @param u      the User,who owns this map
	 * @param sessionID  the Session ID 
	 * @author ZGE
	 */
	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null&&a.getCategory().equals(Categories.Communication)){
			// Currently, there are only chat messages in this category, thus, a
			// further differentiation is not needed
			int mapID = ActionProcessor.getMapIDFromAction(a);

			Revision r = new Revision(mapID, "Chat message", u.getUserID());
			r.saveToDatabase();

			ActionParameter.saveParametersForRevision(r.getId(), a);
			a.addParameter(ParameterTypes.UserName, u.getNickname());
			a.addParameter(ParameterTypes.Time, System.currentTimeMillis() + "");

			ActionPackage ap = ActionPackage.wrapAction(a);
			Logger.doCFLogging(ap);
			ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);
			
			returnValue = true;
		}
		return returnValue;
	}

}
