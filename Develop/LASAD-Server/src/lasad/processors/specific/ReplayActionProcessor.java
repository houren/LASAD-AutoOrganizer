package lasad.processors.specific;

import java.util.Vector;

import lasad.controller.ManagementController;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.processors.ActionProcessor;

public class ReplayActionProcessor extends AbstractActionObserver implements
		ActionObserver {

	public ReplayActionProcessor() {
		super();
	}
	
	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null&&a.getCategory().equals(Categories.Replay)){
			int mapID = ActionProcessor.getMapIDFromAction(a);

			Vector<Integer> allMapRevisionIDs = Revision.getIDsOfRevisionsConnectedToMap(mapID);

			// If only 1 revision --> Empty map, Ignore.
			if (allMapRevisionIDs.size() == 1) {
				ActionPackage ap = ActionPackageFactory.errorReplay(mapID, Map.getMapName(mapID), -3);
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
				return true;
			}

			// Remove first revision from list (it is not required, it says only
			// that the map has been created)
			allMapRevisionIDs.removeElementAt(0);

			// Estimate replay time in seconds
			long realStartSec = (Revision.getTime(allMapRevisionIDs.firstElement()) / 1000) - 1;
			long totalSec = Revision.getTime(allMapRevisionIDs.lastElement()) - Revision.getTime(allMapRevisionIDs.firstElement());
			totalSec = (totalSec / 1000) + 1;

			// Create response
			ActionPackage ap = ActionPackageFactory.startInitReplay(mapID);

			ap = Map.getCompleteElementInformationForReplay(ap, mapID, true, realStartSec);

			// Create final action to initialize the replay-client
			ap = ActionPackageFactory.endInitReplay(ap, mapID, totalSec, myServer.currentState);
			// Add Map-ID to ActionPackage
			ap.addParameter(ParameterTypes.MapId, a.getParameterValue(ParameterTypes.MapId));
//			ap.addParameter("MAP-ID", a.getParameterValue("MAP-ID"));
			// Add ActionPackage to UsersActionQueue
			Logger.doCFLogging(ap);
			ManagementController.addToUsersActionQueue(ap, u.getSessionID());
			
			returnValue = true;

		}
		
		return returnValue;
	}
	
	

}
