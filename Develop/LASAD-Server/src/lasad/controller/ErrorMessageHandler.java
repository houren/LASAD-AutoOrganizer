package lasad.controller;

import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.shared.communication.objects.ActionPackage;

public class ErrorMessageHandler {
	// create a error message to send to user
	public static void createErrorMessageForUser(String errorMessage, User u) {
		ActionPackage ap1 = ActionPackageFactory.error(errorMessage);
		Logger.doCFLogging(ap1);
		ManagementController.addToUsersActionQueue(ap1, u.getSessionID());

		// Send end action to close any loading dialogues
		ActionPackage ap2 = ActionPackageFactory.loadFromFileFinished("LASAD");
		Logger.doCFLogging(ap2);
		ManagementController.addToUsersActionQueue(ap2, u.getSessionID());
	}

}
