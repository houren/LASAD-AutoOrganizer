package lasad.gwt.server;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;

import lasad.gwt.client.communication.servlet.LASADServletService;
import lasad.shared.communication.objects.ActionPackage;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;




public class LASADServletServiceImpl extends RemoteServiceServlet implements LASADServletService {
	private static final long serialVersionUID = 5585783378845578472L;

	
	private LASADGWTServiceBroker myBroker = LASADGWTServiceBroker.getInstance();

	@Override
	public void doActionOnServer(ActionPackage p) { 
		//HttpSession session = this.getThreadLocalRequest().getSession(true);
		HttpServletRequest request =  this.getThreadLocalRequest();
		// Forward the client action to the server
		//myBroker.workOnClientActionContainer(session, p);
		myBroker.workOnClientActionContainer(request, p);
	}
	
	@Override
	protected void checkPermutationStrongName() throws SecurityException {
	    return;
	}
}