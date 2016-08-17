package lasad.processors.specific;

import lasad.controller.ManagementController;
import lasad.controller.MapController;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * This processor processes all file actions incoming and being distributed by ActionProcessor
 * @author MB
 */
public class FileActionProcessor extends AbstractActionObserver implements
		ActionObserver {

 	
	public FileActionProcessor() {
		super();
	}

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (u != null&&a.getCategory().equals(Categories.File)){
			if (a.getCmd().equals(Commands.ImportFinished)) {
				ActionPackage ap = ActionPackageFactory.loadFromFileFinished(a.getParameterValue(ParameterTypes.ImportType));
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());
			}
			else {
				boolean userJoin = Boolean.parseBoolean(a.getParameterValue(ParameterTypes.UserJoin));
				MapController.loadMapFromFile(a, u, userJoin);
//				processLoadMapFromFile(a, u);
			}
		returnValue = true;
		}
		return returnValue;
	}

}
