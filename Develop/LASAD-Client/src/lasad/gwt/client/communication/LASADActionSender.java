package lasad.gwt.client.communication;

import java.util.Date;
import java.util.Set;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.eventservice.events.ActionPackageEvent;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.communication.servlet.LASADServletService;
import lasad.gwt.client.communication.servlet.LASADServletServiceAsync;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.ui.LASADStatusBar;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.loaddialogues.ReloadingMapsDialogue;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.novanic.eventservice.client.ClientHandler;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListenerAdapter;


/** Helper class to send ActionPackage to the server **/
public class LASADActionSender {
	
	private static final int RETRY_LIMIT = 10;

	int tries = 0;
	int counter = 0;
	
	private final RemoteEventService myGWTEventService;
	private final LASADServletServiceAsync myServlet = GWT.create(LASADServletService.class);
	private final LASADActionReceiver receiver = LASADActionReceiver.getInstance();
	private String clientID = null; //ClientHandler

	private static LASADActionSender myInstance = null;

	private RemoteEventListener myListener = null;

	/**
	 * Singleton pattern implementation
	 * 
	 * @return
	 */
	public static LASADActionSender getInstance() {
		if (myInstance == null) {
			myInstance = new LASADActionSender();
		}
		return myInstance;
	}
	
	private void getClientHandler(){
		Logger.log("###getClientHandler getting Connection ID", Logger.DEBUG);
		RemoteEventServiceFactory gwtEventServiceFactory = RemoteEventServiceFactory.getInstance();
		gwtEventServiceFactory.requestClientHandler(new AsyncCallback<ClientHandler>() {
			/** If the servlet call fails, log it. */
			public void onFailure(Throwable caught) {
				Logger.log("[lasad.gwt.client.communication.objects.LASADActionSender][sendActionSet] Error: RPC failure", Logger.DEBUG);
				Logger.log("[Error details] " + caught.toString(), Logger.DEBUG);
			}

			/**
			 * If the servlet call is successful 
			 * do further processing
			 * 
			 * @param result
			 */
			public void onSuccess(ClientHandler result) {
				Logger.log("###getClientHandler Connection ID" + result.getConnectionId(), Logger.DEBUG);
				clientID = result.getConnectionId();
			}
		});
	}

	/**
	 * Private constructor to support Singleton
	 */
	private LASADActionSender() {
		getClientHandler();

		RemoteEventServiceFactory gwtEventServiceFactory = RemoteEventServiceFactory.getInstance();
		myGWTEventService = gwtEventServiceFactory.getRemoteEventService();

		myListener = new RemoteEventListener() {

			// Event from Server
			@Override
			public void apply(Event anEvent) {
				if (anEvent instanceof ActionPackageEvent) {
					receiver.doActionPackage(((ActionPackageEvent) anEvent).getActionPackage());
				} else {
					Logger.log("Unknown event type received from GWTEventService!", Logger.DEBUG_ERRORS);
				}
			}
		};
	}

	/**
	 * Sends a set of actions to the server
	 * 
	 * @param actionSet
	 */
	public void sendActionPackage(final ActionPackage actionSet) {

		if(connectEventService())
		{
			LASADStatusBar.getInstance().setConnectionBusy(true);

			actionSet.addParameter(ParameterTypes.SessionId, clientID); //"CLIENT-ID"
			
			//Add extra parameters from by url parameters
			LASAD_Client.getInstance().urlParameterConfig.addParamsToActionPackage(actionSet);
			
			// Send actions to server via GWT RPC and Servlet
			myServlet.doActionOnServer(actionSet, new AsyncCallback<Void>() {

				/** If the servlet call fails, log it. */
				public void onFailure(Throwable caught) {
					Logger.log("[lasad.gwt.client.communication.objects.LASADActionSender][sendActionSet] Error: RPC failure", Logger.DEBUG_ERRORS);
					Logger.log("[Error details] " + caught.toString(), Logger.DEBUG_ERRORS);
				}

				/**
				 * If the servlet call is successful forward the action that
				 * will be echoed by the server to the LASADActionReceiver which
				 * will do the further processing
				 * 
				 * @param result
				 */
				public void onSuccess(Void result) {
					LASADStatusBar.getInstance().setConnectionBusy(false);
				}
			});
		}
		else {
			LASADInfo.display("Error", "Connection cannot be established.");
		}		
	}

	/**
	 * Establishing connection to GWTEventService
	 * 
	 * @return
	 */
	public boolean connectEventService() {
		
		if(myGWTEventService.isActive() && myGWTEventService.getActiveDomains().size() == 2) {
			Logger.log("connectEventService.isActiveT", Logger.DEBUG);
			LASADStatusBar.getInstance().setConnectionStatus("online");
			LASADStatusBar.getInstance().setConnectionBusy(false);
			
			return true;
		}
		
		else {
			Logger.log("Problems with EventService connection", Logger.DEBUG);
			Logger.log("connectEventService.isActive ==" + myGWTEventService.isActive(), Logger.DEBUG);
			Logger.log("connectEventService.getActiveDomains().size() ==" + myGWTEventService.getActiveDomains().size(), Logger.DEBUG);
			try {
				
			// Remove all existing listeners to avoid any conflicts
			myGWTEventService.removeListeners();
			
			// Add the default listener for server events. This could take some time, thus, we will have to wait for it...
			myGWTEventService.addListener(null, myListener);
			
			Logger.log("calling getClientHandler() again", Logger.DEBUG);
			//getClientHandler();
			
			myGWTEventService.addUnlistenListener(Scope.LOCAL, new UnlistenEventListenerAdapter() {
				public void onUnlisten(UnlistenEvent anUnlistenEvent) {
					
					Logger.log("[lasad.gwt.client.communication.objects.LASADActionSender][onUnlisten] Unlisten Event detected (" + new Date(System.currentTimeMillis()) + ")", Logger.DEBUG);
					Logger.log("GWT Event Service: Unlisten Event detected: " + new Date(System.currentTimeMillis()), Logger.DEBUG);

					//myGWTEventService.removeListeners();
					// Increase connection attempt counter
					tries++;

					if (tries <= RETRY_LIMIT) { 
												
						Logger.log("Reconnect - Try "+tries+"", Logger.DEBUG);						
						
						Timer timerT = new Timer() {

							@Override
							public void run() {
								Logger.log("timerT in try=" + tries, Logger.DEBUG);
								if(myGWTEventService.isActive() && myGWTEventService.getActiveDomains().size() == 2) {
									tries = 0;
									Logger.log("Active counter="+counter+"", Logger.DEBUG);
									counter = 0;
									// If there are active maps, leave and rejoin them to make sure the state is consistent
									
									if(LASAD_Client.getMapTabs().keySet().size() > 0) {
										
										final Set<String> mapIDs = LASAD_Client.getMapTabs().keySet();
										final int size = mapIDs.size();
										
										ReloadingMapsDialogue.getInstance().setMapCount(size);
										ReloadingMapsDialogue.getInstance().showLoadingScreen();
										
										Timer rejoinAllMaps = new Timer() {

											@Override
											public void run() {
				
												for(final String i : mapIDs) {
													Logger.log("connectEventService.isActiveT sending leave", Logger.DEBUG);
													sendActionPackage(ActionFactory.getInstance().leaveMap(i));
													
													Timer joinMapAgain = new Timer() {
														@Override
														public void run() {
															Logger.log("connectEventService.isActiveT sending join", Logger.DEBUG);
															sendActionPackage(ActionFactory.getInstance().joinMap(i+""));
														}
													};
													
													joinMapAgain.schedule(500);
												}
											}
											
										};
										
										rejoinAllMaps.schedule(2500);
									}
									this.cancel();
									
								}
								else{
									Logger.log("Not active yet counter="+counter+"", Logger.DEBUG);
								}
								counter++;
							}
							
						};
						timerT.scheduleRepeating(250);

						
					} else {
						Logger.log("Reconnection was not possible", Logger.DEBUG);
						LASAD_Client.getInstance().disable(true);
						if(LASAD_Client.getMapTabs().keySet().size() > 0) {
							
							final Set<String> mapIDs = LASAD_Client.getMapTabs().keySet();
							final int size = mapIDs.size();
							
							ReloadingMapsDialogue.getInstance().setMapCount(size);
							ReloadingMapsDialogue.getInstance().showLoadingScreen();
							
							Timer leaveAllMaps = new Timer() {

								@Override
								public void run() {
	
									for(final String i : mapIDs) {
										Logger.log("Sending leave msgs, reconnection was not possible", Logger.DEBUG);
										sendActionPackage(ActionFactory.getInstance().leaveMap(i));
									}
								}
								
							};
							
							leaveAllMaps.schedule(500);
						}
					}

					
					
				}
			}, new DefaultUnlistenEvent(), new VoidAsyncCallback<Void>());
			
			Timer waitUntilListenerIsAdded = new Timer() {
				@Override
				public void run() {
					if (myGWTEventService.isActive() && myGWTEventService.getActiveDomains().size() == 1) {

						LASADStatusBar.getInstance().setConnectionStatus("online");
						LASADStatusBar.getInstance().setConnectionBusy(false);
						
						this.cancel();
					}
					else {
						// Do nothing, just wait for the next try
					}
				}
				
			};
			
			waitUntilListenerIsAdded.scheduleRepeating(250);
			
			return true;
			
			} catch (Exception e) {
				Logger.log("Connecting to Servlet failed.", Logger.DEBUG_ERRORS);
				return false;
			}
		}
	}

	private abstract class DefaultAsyncCallback<T> implements AsyncCallback<T> {
		public void onFailure(Throwable aThrowable) {
			GWT.log("Error in unlisten!", aThrowable);
		}
	}
	
	private class VoidAsyncCallback<T> extends DefaultAsyncCallback<T> {
		public void onSuccess(T aResult) {
		}
	}
}