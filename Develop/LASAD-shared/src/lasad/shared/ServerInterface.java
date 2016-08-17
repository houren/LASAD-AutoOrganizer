package lasad.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import lasad.shared.communication.objects.ActionPackage;



public interface ServerInterface extends Remote {
	
	public void doActionOnServer(ActionPackage p) throws RemoteException;
}
