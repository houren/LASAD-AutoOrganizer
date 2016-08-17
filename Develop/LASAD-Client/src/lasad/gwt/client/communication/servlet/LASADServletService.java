package lasad.gwt.client.communication.servlet;


import lasad.shared.communication.objects.ActionPackage;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;




/**
 * This interface defines all methods that can be used on the server side from
 * the client
 * 
 * @author Frank Loll
 */
@RemoteServiceRelativePath("LASADAction")
public interface LASADServletService extends RemoteService {
	void doActionOnServer(ActionPackage p);
}
