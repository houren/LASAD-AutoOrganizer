package lasad.gwt.client.ui;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * This class represents the status bar at the bottom of the screen. Uses
 * Singleton pattern
 */
public class LASADStatusBar extends ToolBar {

	private final lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	private Status serverVersion = new Status();
	private Status connectionStatus = new Status();
	private Status loginStatus = new Status();
	private Button logout = new Button();

	private static LASADStatusBar myInstance = null;

	public static LASADStatusBar getInstance() {
		if (myInstance == null) {
			myInstance = new LASADStatusBar();
		}
		return myInstance;
	}

	private LASADStatusBar() {
		serverVersion.setAutoWidth(true);
		this.add(serverVersion);
		this.add(new FillToolItem());
		
//		this.setAlignment(HorizontalAlignment.RIGHT);
//		this.setSpacing(2);

		connectionStatus.setAutoWidth(true);
		connectionStatus.setWidth(200);
		this.add(connectionStatus);

		loginStatus.setAutoWidth(true);
		loginStatus.setWidth(200);
		this.add(loginStatus);

		logout.setAutoWidth(true);
		logout.setText("Logout");
		logout.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (be.getType().getEventCode() == Events.OnClick.getEventCode()) {
					LASAD_Client.getInstance().logOut();
				}
			}
		});
		this.add(logout);
		
		// Set Default Values
		setConnectionStatus("offline");
		setLoginStatus(myConstants.NotLoggedIn());
	}

	public void setServerVersion(String text) {
		this.serverVersion.setText(text);
	}
	
	public void setConnectionStatus(String text) {
		this.connectionStatus.setText("<b>" + myConstants.Connection() + ": </b>" + text);
	}

	public void setConnectionBusy(boolean visible) {
		if (visible) {
			this.connectionStatus.setBusy(this.connectionStatus.getText());
		} else {
			this.connectionStatus.setText(this.connectionStatus.getText());
			this.connectionStatus.clearStatus(this.connectionStatus.getText());
		}
	}

	public void setLoginStatus(String text) {
		this.loginStatus.setText("<b>Login: </b>" + text);
		if (text.equals("not logged in")) {
			logout.hide();
		} else {
			logout.show();
		}
	}
}