package lasad.gwt.client.ui.workspace.tabs;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingLoginDialogue;
import lasad.gwt.client.ui.workspace.tabs.map.MapLoginTab;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;




/**
 * Implementation of the login screen. After login, the screen will change to
 * the map overview
 * 
 */
public class LoginTab extends LayoutContainer {

	private final LASADActionSender communicator = LASADActionSender
			.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	private final lasad_clientConstants myConstants = GWT
			.create(lasad_clientConstants.class);

	private TextField<String> username = null, password = null;

	private ContentPanel userLoginPanel;
	private MapLoginTab mapLoginPanel;


	public MapLoginTab getMapLoginPanel() {
		return mapLoginPanel;
	}

	private static LoginTab instance = null;

	public static LoginTab getInstance() {
		if (instance == null) {
			instance = new LoginTab();
		}
		return instance;
	}

	private LoginTab() {
		this.setLayout(new FitLayout());

		userLoginPanel = initUserLoginPanel();
		mapLoginPanel = MapLoginTab.getInstance();

		showUserLoginPanel();
	}

	private void showUserLoginPanel() {
		this.removeAll();
		this.add(userLoginPanel);
		this.layout();
	}

	private void showMapLoginPanel() {
		this.removeAll();
		mapLoginPanel.clearRightHandSide();
		this.add(mapLoginPanel);
		this.layout();
	}

	private ContentPanel initUserLoginPanel() {

		ContentPanel root = new ContentPanel();
		root.setHeaderVisible(false);
		root.setLayout(new CenterLayout());

		FormPanel loginForm = new FormPanel();
		loginForm.setHeading(myConstants.Login());
		loginForm.setFrame(true);
		loginForm.setWidth(350);

		username = new TextField<String>();
		username.setFieldLabel("<font-color=\"#000000;\">"
				+ myConstants.Username() + "</font>");
		username.setAllowBlank(false);
		loginForm.add(username);

		password = new TextField<String>();
		password.setFieldLabel("<font-color=\"#000000;\">"
				+ myConstants.Password() + "</font>");
		password.setAllowBlank(false);
		password.setPassword(true);
		password.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent event) {
				super.componentKeyPress(event);
				if (event.getKeyCode() == 13) {

					LoadingLoginDialogue.getInstance().showLoadingScreen();
					// Get templates

					ActionPackage myLoginPackage = actionBuilder.createLogin(
							username.getValue(), password.getValue());
					communicator.sendActionPackage(myLoginPackage);

					LASAD_Client.getInstance().activateLoginTabSelectionListener();
					
					event.preventDefault();
				}
				event.cancelBubble();
			}
		});

		loginForm.add(password);

		Button loginButton = new Button(myConstants.LoginButton()) {
			@Override
			protected void onClick(ComponentEvent ce) {
				
				if(username.getValue() == null || password.getValue() == null) {
					return;
				}

				LoadingLoginDialogue.getInstance().showLoadingScreen();
				// Get templates

				ActionPackage myLoginPackage = actionBuilder.createLogin(
						username.getValue(), password.getValue());
				communicator.sendActionPackage(myLoginPackage);

				LASAD_Client.getInstance().activateLoginTabSelectionListener();
				
				ce.cancelBubble();
			}
		};

		Anchor helpLink = new Anchor("Help");
		helpLink.addClickHandler(new ClickHandler()
		{
            @Override
            public void onClick(ClickEvent ce)
            {
               LASAD_Client.getInstance().createTutorialVideosTab();
            }
        });


        helpLink.setHTML("<br><style>a:link {color:blue; background-color:transparent; text-decoration:underline} a:visited {color:blue; background-color:transparent; text-decoration:underline} a:hover   {color:purple; background-color:transparent; text-decoration:underline} a:active  {color:blue; background-color:transparent; text-decoration:underline}</style>" + helpLink.getHTML());

		loginForm.add(loginButton);
		loginForm.add(helpLink);

		root.add(loginForm);
		return root;
	}


	public void refreshMapList() {
		Logger.log("[lasad.gwt.client.communication.LoginTab][refreshMapList] Request to get active maps sent.", Logger.DEBUG);

		ActionPackage refreshMapList = actionBuilder.getMaps();
		communicator.sendActionPackage(refreshMapList);
	}


	public void updateAuthStatus(boolean status) {
		if (status) {
			showMapLoginPanel();

		} else {
			showUserLoginPanel();
		}
	}
}