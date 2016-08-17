package lasad.gwt.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import de.novanic.eventservice.service.EventServiceImpl;
import de.novanic.eventservice.service.registry.user.UserActivityScheduler;
import de.novanic.eventservice.service.registry.user.UserManager;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;

@SuppressWarnings("serial")
public class LASADEventServiceImpl extends EventServiceImpl {

	public void init(ServletConfig aConfig) throws ServletException {
		super.init(aConfig);

		UserManager theUserManager = UserManagerFactory.getInstance().getUserManager(1000);
		UserActivityScheduler theUserActivityScheduler = theUserManager.getUserActivityScheduler();
		theUserActivityScheduler.addTimeoutListener(LASADGWTServiceBroker.getInstance());
		theUserManager.activateUserActivityScheduler();
	}

	@Override
	protected void checkPermutationStrongName() throws SecurityException {
	    return;
	}
	
}
