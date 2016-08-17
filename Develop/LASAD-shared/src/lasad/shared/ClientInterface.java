package lasad.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import lasad.shared.communication.objects.ActionPackage;



/**
 * Each RMI client should provide this RMI interface
 * 
 * @author Frank Loll
 *
 */
public interface ClientInterface extends Remote {
	
	public void doActionPackagesOnClient(Vector<ActionPackage> vp) throws RemoteException;
}
