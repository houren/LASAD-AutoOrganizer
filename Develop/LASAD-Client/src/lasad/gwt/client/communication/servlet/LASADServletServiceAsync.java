package lasad.gwt.client.communication.servlet;


import lasad.shared.communication.objects.ActionPackage;

import com.google.gwt.user.client.rpc.AsyncCallback;




/** The Async part of the interface **/
public interface LASADServletServiceAsync {
	void doActionOnServer(ActionPackage p, AsyncCallback<Void> asyncCallback);
}