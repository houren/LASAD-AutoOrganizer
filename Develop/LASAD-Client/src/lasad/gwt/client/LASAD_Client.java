package lasad.gwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.logger.ConsoleLogger;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.LASADStatusBar;
import lasad.gwt.client.ui.replay.ReplayControl;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingLoginDialogue;
import lasad.gwt.client.ui.workspace.tableview.ArgumentEditionStyleEnum;
import lasad.gwt.client.ui.workspace.tabs.AboutTab;
import lasad.gwt.client.ui.workspace.tabs.DebugTab;
import lasad.gwt.client.ui.workspace.tabs.LoginInfoTab;
import lasad.gwt.client.ui.workspace.tabs.LoginTab;
import lasad.gwt.client.ui.workspace.tabs.MapTab;
import lasad.gwt.client.ui.workspace.tabs.ReplayTab;
import lasad.gwt.client.ui.workspace.tabs.TutorialVideosTab;
import lasad.gwt.client.ui.workspace.tabs.authoring.AuthoringTab;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTab;
import lasad.gwt.client.ui.workspace.tabs.map.MapLoginTab;
import lasad.gwt.client.urlparameter.UrlParameterConfig;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.dfki.meta.util.ConstantsFE;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LASAD_Client implements EntryPoint {

	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	public boolean authed = false;

	private String role = "Standard";
	private String username = "Unknown";

	public static LASADStatusBar statusBar = LASADStatusBar.getInstance();

	private Viewport viewport;

	private static TabPanel advanced;

	// These two ones are needed globally to remove the listener before showing the
	// LoginTab. The problem here is the race condition between indirectly selecting the
	// tab holding LoginTab/MapLoginTab, which are no actual tabs (see class def),
	// and the switch between LoginTab and MapLoginTab. Therefore the listener
	// that refreshes the MapLoginTab must be removed before refreshing the whole workspace
	// which occurs on logout and removes all open map tabs and/or debugging tabs
	private static TabItem item2;
	private static Listener<BaseEvent> selectionListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			if (loginTabInstance.getItem(0) instanceof MapLoginTab) {
				MapLoginTab.getInstance().updateOverviews();
			}
		}
	};

	private ContentPanel appPanel;
	private boolean isConfirmedTabClose = false;
	private static LoginTab loginTabInstance = LoginTab.getInstance();

	private static LASAD_Client instance = null;
	private static Map<String, MapTab> mapTabs = new HashMap<String, MapTab>();

	public static Map<String, MapTab> getMapTabs() {
		return mapTabs;
	}

	private static Map<String, MVController> mvcControllers = new HashMap<String, MVController>();

	public UrlParameterConfig urlParameterConfig;
	private boolean showFeedbackAuthoringToolTab = false;

	private LASAD_Client() {
		// Initializing console logger
		Logger.addLogListener(ConsoleLogger.getInstance());

		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent e) {
				if (e.getTypeInt() == Event.getTypeInt(KeyDownEvent.getType().getName())) {
					NativeEvent nativeEvent = e.getNativeEvent();
					if (nativeEvent.getKeyCode() == KeyCodes.KEY_ESCAPE) {
						nativeEvent.preventDefault();
					}
				}
			}
		});
		urlParameterConfig = new UrlParameterConfig();

	}

	public static LASAD_Client getInstance() {
		if (LASAD_Client.instance == null) {
			instance = new LASAD_Client();
		}
		return instance;
	}

	public static MapTab getMapTab(String mapID) {
		return mapTabs.get(mapID);
	}

	public static MVController getMVCController(String mapID) {
		return mvcControllers.get(mapID);
	}

	public static MVController registerMVCController(MVController controller) {
		if (!mvcControllers.containsKey(controller.getMapID())) {
			// Add the Controller
			mvcControllers.put(controller.getMapID(), controller);
			return controller;
		}
		return null;
	}

	public static void removeMapTab(String mapID) {
		MapTab tab = mapTabs.get(mapID);
		if (tab != null) {
			mapTabs.remove(mapID);
			tab.close();
			tab = null;
		}
	}

	public static void unregisterMVCController(MVController controller) {
		mvcControllers.remove(controller.getMapID());
	}

	public void addDeveloperTab() {
		TabItem item6 = new DebugTab();
		advanced.add(item6);
	}

	public void addAuthoringTab() {
		TabItem authoring = new AuthoringTab();
		advanced.add(authoring);
	}

	public void addLoginInfoTab() {
		TabItem loginInfo = new LoginInfoTab();

		HTML introduction = new HTML();
		introduction
				.setHTML("<div align=\"left\" style=\"padding-left:25px\"><font color=#000000><br><br><h2>Thank you for your interest in LASAD.</h2><br><p>To login to the system, the following user names can be used:<br><br></p><ul style=\"list-style-type:circle; list-style-position:inside\"><li>User name: t1   Password: t2</li><li>User name: t3   Password: t3 </li><li>User name: t3   Password: t3 </li></ul></br></br><p>We would appreciate your feedback concerning LASAD.</br></br>If you are interested in the authoring tool of LASAD, there is <a href=\"manual.pdf\" target=\"_blank\">manual</a> which may be helpful..</br></br>Please note, that the system is only for testing purposes and not intended to be used regularly on this website.</br></br>If you have any questions concerning the system's use, please do not hesitate to <a href=\"http://cses.informatik.hu-berlin.de/research/details/lasad/\" target=\"_blank\">contact us</a>.</p></font></div>");
		loginInfo.add(introduction);
		advanced.add(loginInfo);
	}

	public void addFeedbackAuthoringToolTab() {
		TabItem fatTab = new FeedbackAuthoringTab();

		// fatTab.addListener(Events.BeforeShow, new Listener<TabPanelEvent>() {
		// public void handleEvent(final TabPanelEvent be) {
		// FeedbackAuthoringTabContent.getInstance().refreshPanels();
		// }
		// });

		advanced.add(fatTab);
		// advanced.setSelection(fatTab); //TODO added to focus on Feedback Authoring :)
	}

	// public void addMapOverviewTab() {
	// TabItem mapLogin = new MapLoginTab();
	// advanced.add(mapLogin);
	// }

	public MapTab createMapTab(final MVController controller) {
		// Create MapTab
		final MapTab item = new MapTab(controller.getMapInfo());

		// Add MapTab to List of MapTabs
		mapTabs.put(controller.getMapID(), (MapTab) item);

		// Added Exit Dialog to the Tab
		item.addListener(Events.BeforeClose, new Listener<TabPanelEvent>() {

			public void handleEvent(final TabPanelEvent be) {
				if (isConfirmedTabClose()) {
					setConfirmedTabClose(false);
				} else {
					be.setCancelled(true);

					MessageBox box = new MessageBox();
					box.setButtons(MessageBox.YESNO);
					box.setIcon(MessageBox.QUESTION);
					box.setTitle(myConstants.CloseMapHeader());
					box.setMessage(myConstants.CloseMapText());
					box.addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
								setConfirmedTabClose(true);
								item.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
								LASADActionSender.getInstance().sendActionPackage(actionBuilder.leaveMap(controller.getMapID()));
							}
						}

					});
					box.show();
				}
			}
		});

		advanced.add(item);
		advanced.setSelection(item); // TODO removed to focus on Feedback Authoring Tool tab:)

		if (controller.getMapInfo().isTrackCursor() && !role.equalsIgnoreCase("Observer")) {
			LASADActionSender.getInstance().sendActionPackage(
					actionBuilder.createAwarenessCursor(controller.getMapID(), LASAD_Client.getInstance().getUsername()));
		}

		return (MapTab) item;
	}

	public void createAboutTab()
	{
		AboutTab about = AboutTab.getInstance();
		advanced.add(about);
		advanced.setSelection(about);
	}

	public void createTutorialVideosTab()
	{
		TutorialVideosTab vidTab = TutorialVideosTab.getInstance();
		advanced.add(vidTab);
		advanced.setSelection(vidTab);
	}

	/** Creating tabs **/
	private TabPanel createTabs() {

		advanced = new TabPanel();
		advanced.setSize("100%", "100%");
		advanced.setMinTabWidth(115);
		advanced.setAnimScroll(true);
		advanced.setTabScroll(true);

		/*
		TabItem item = new TabItem();
		item.setText(myConstants.Introduction());
		item.setClosable(true);
		item.addStyleName("pad-text");
		item.setStyleAttribute("backgroundColor", "#FBFBFB");
		// item.setEnabled(false);

		// TODO This text should be loaded from the server.
		HTML introduction = new HTML();
		introduction
				.setHTML("<br><div align=\"center\"><img src=\"resources/images/lasad.png\" border=\"0\"><font color=#000000><br><br><h2>Welcome!</h2><br><p>This is version "
						+ DebugSettings.version
						+ " of the LASAD system.<br></p><p>You have two options: (1) Try out the pre-configured LASAD system or (2) configure the system to your needs by means of the authoring tool.<br><br></p><p>To just test the system, please click on \"Login & Map Overview\" at the top of the screen. <br>After login, you can join an existing argumentation map or create a new one.<br><br></p><p>If you want to configure the system to your needs, please click first on \"Login and Map Overview\" and enter your username and password.<br> After that, there will be a new tab at the top of the screen called \"LASAD Authoring Tool\".<br> Here you can configure the system. <br>Once your configuration is complete, you can switch to the \"Login & Map Overview\" tab to actually use it.</p></div></font>");
		item.add(introduction);
		advanced.add(item);
		*/

		item2 = new TabItem();
		item2.setLayout(new FitLayout());
		item2.setText(myConstants.LoginTabText());
		item2.setClosable(false);
		item2.addStyleName("pad-text");
		item2.add(loginTabInstance);
		item2.setEnabled(true);

		item2.addListener(Events.Show, selectionListener);

		advanced.add(item2);

		// if the user is not automatically logging in, set to login tab
		if (!(urlParameterConfig.isAutoLogin() && urlParameterConfig.getUsername() != null && urlParameterConfig.getPassword() != null && urlParameterConfig
				.getMapId() != null)) {
			advanced.setSelection(item2);
		}

		// Commented out by Kevin Loughlin - login info tab is unncessary and unwanted for our purposes right now
		// this.addLoginInfoTab();

		return advanced;
	}

	public void activateLoginTabSelectionListener() {
		item2.addListener(Events.Show, selectionListener);
	}

	public void deactivateLoginTabSelectionListener() {
		item2.removeListener(Events.Show, selectionListener);
	}

	public String getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAuthed() {
		return authed;
	}

	private boolean isConfirmedTabClose() {
		return isConfirmedTabClose;
	}

	public void logOut() {
		this.deactivateLoginTabSelectionListener();

		ActionPackage actionSet = new ActionPackage();
		ActionPackage logout = actionBuilder.logout(getUsername());
		actionSet.getActions().addAll(logout.getActions());
		// Send Action --> Server
		LASADActionSender.getInstance().sendActionPackage(actionSet);
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		urlParameterConfig = new UrlParameterConfig();
		GXT.setDefaultTheme(new Theme("slate", GXT.MESSAGES.themeSelector_grayTheme(), "resources/gxt/css/xtheme-slate.css"), true);

		// Viewport provides window-resizing
		viewport = new Viewport();
		viewport.setBorders(false);
		viewport.setLayout(new FitLayout());

		RootPanel.get().setSize("100%", "100%");
		RootPanel.get().add(viewport);

		appPanel = new ContentPanel();
		appPanel.setLayout(new BorderLayout());
		appPanel.setHeaderVisible(false);
		appPanel.setBorders(false);

		/* Setting up tabs */
		TabPanel myTabs = createTabs();
		myTabs.setSize("100%", "100%");
		appPanel.add(myTabs, new BorderLayoutData(LayoutRegion.CENTER));

		// Set StatusBar
		appPanel.setBottomComponent(statusBar);

		viewport.add(appPanel);
		viewport.layout();

		LASADActionSender.getInstance().connectEventService();

		if (urlParameterConfig.isAutoLogin() && urlParameterConfig.getUsername() != null && urlParameterConfig.getPassword() != null) {
			Timer delayCall = new Timer() {
				@Override
				public void run() {
					if (urlParameterConfig.isPasswordEncrypted()) {
						LASADActionSender.getInstance().sendActionPackage(
								ActionFactory.getInstance().createLogin(urlParameterConfig.getUsername(), urlParameterConfig.getPassword(),
										true));
					} else {
						LASADActionSender.getInstance()
								.sendActionPackage(
										ActionFactory.getInstance().createLogin(urlParameterConfig.getUsername(),
												urlParameterConfig.getPassword()));
					}
				}
			};

			LoadingLoginDialogue.getInstance().showLoadingScreen();
			delayCall.schedule(3000);
		}

	}

	public void refreshWorkspace() {

		LoginTab myLoginTab = LoginTab.getInstance();
		myLoginTab.updateAuthStatus(false);

		try {
			Iterator<TabItem> iterator = advanced.getItems().iterator();
			while (iterator.hasNext()) {
				TabItem tab = iterator.next();
				if (tab instanceof DebugTab || tab instanceof AuthoringTab || tab instanceof ReplayTab
						|| tab instanceof FeedbackAuthoringTab || tab instanceof AboutTab || tab instanceof TutorialVideosTab) {
					iterator.remove();
					tab.close();
				}

			}
			// If we want to stay with this auto-refresh we must check if this is called via log-out. If that's the case, the auto
			// refresh must not occur to avoid errors
			// myLoginTab.refreshMapList();

		} catch (Exception e) {
			Logger.log(e.toString(), Logger.DEBUG_ERRORS);
		}

	}

	public void setAuthed(boolean authed) {
		this.authed = authed;
	}

	public void setConfirmedTabClose(boolean isConfirmedTabClose) {
		this.isConfirmedTabClose = isConfirmedTabClose;
	}

	public void setRole(String role) {
		this.role = role;

		if ("Developer".equalsIgnoreCase(role)) {
			this.addDeveloperTab();
			if (showFeedbackAuthoringToolTab) {
				this.addFeedbackAuthoringToolTab();
			}
		}

		for (String validRole : ConstantsFE.getFeedbackAuthoringValidRoles()) {
			if (validRole.equalsIgnoreCase(role)) {
				this.addAuthoringTab();
			}
		}

		if ("Guest".equalsIgnoreCase(role)) {
			MapLoginTab.getInstance().templateOverview.setEnabled(false);
		} else {
			MapLoginTab.getInstance().templateOverview.setEnabled(true);
		}
	}

	public void setUsername(String username) {
		this.username = username;
		LASAD_Client.statusBar.setLoginStatus("logged in as " + username);
	}

	private static Map<String, ArgumentEditionStyleEnum> mapSelectedEditionStyle = new HashMap<String, ArgumentEditionStyleEnum>();

	public static ArgumentEditionStyleEnum getMapEditionStyle(String mapId) {
		ArgumentEditionStyleEnum style = mapSelectedEditionStyle.get(mapId);

		if (style == null) {
			style = ArgumentEditionStyleEnum.GRAPH;
		}
		return style;
	}

	public ReplayTab createReplayTab(final MVController controller, final int totalSec, final List<Integer> indexSecondsReplay,
			final HashMap<Integer, ActionPackage> indexForwReplay, final HashMap<Integer, List<Action>> indexElementReplay,
			final HashMap<Integer, List<Integer>> lastActionReplay, final HashMap<Integer, List<Action>> indexChatReplay,
			TreeMap<String, List<Integer>> treeUserReplay) {

		final ReplayControl control = new ReplayControl(controller, totalSec, indexSecondsReplay, indexForwReplay, indexElementReplay,
				lastActionReplay, indexChatReplay, treeUserReplay);

		// Create ReplayTab
		final ReplayTab item = new ReplayTab(controller.getMapInfo(), control);

		// EXIT DIALOG
		// ///////////////////////////////
		// Add listener with exit dialog to the replay tab
		item.addListener(Events.BeforeClose, new Listener<TabPanelEvent>() {

			public void handleEvent(final TabPanelEvent be) {
				if (!isConfirmedTabClose()) {
					setConfirmedTabClose(false);
				} else {
					be.setCancelled(true);
					// Initialize message box
					MessageBox box = new MessageBox();
					box.setButtons(MessageBox.YESNO);
					box.setIcon(MessageBox.QUESTION);
					box.setTitle(myConstants.ReplayTabExitTitle());
					box.setMessage(myConstants.ReplayTabExitMessage());
					box.addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
								// Cancel timer in each case
								control.getTimer().cancel();
								setConfirmedTabClose(true);

								LASAD_Client.unregisterMVCController(controller);
								control.hide();
								item.close();
							}
						}
					});
					box.show();
				}
			}
		});

		// Add replay tab
		advanced.add(item);
		advanced.setSelection(item);
		// Return replay tab

		// Add MapTab to List of MapTabs
		mapTabs.put(controller.getMapID(), (MapTab) item);

		return item;

	}

	public void enable() {
		advanced.setEnabled(true);
	}

	public void disable(boolean withMessage) {
		advanced.setEnabled(false);

		if (withMessage) {
			MessageBox.alert("Connection problem",
					"Your connection to the server crashed multiple times. Please check your connection, reload the page and login again.",
					null);
		}
	}
}