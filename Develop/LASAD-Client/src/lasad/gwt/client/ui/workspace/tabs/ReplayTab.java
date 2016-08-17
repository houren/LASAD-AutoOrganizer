package lasad.gwt.client.ui.workspace.tabs;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.model.argument.MapInfo;
import lasad.gwt.client.ui.replay.ReplayControl;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.google.gwt.core.client.GWT;

public class ReplayTab extends MapTab {
	
	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	public ReplayTab(final MapInfo mapInfo, final ReplayControl control) {
		super(mapInfo);
		this.setText(myConstants.ReplayTabTitle() + " "+String.valueOf(mapInfo.getMapID())+": "+ mapInfo.getTitle());
		
		//Shows/Hides the control window on switching tabs
		Listener<BaseEvent> listener = new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				if (be.getType().equals(Events.BeforeHide)) {
					control.hide();
				}
				else {
					control.show();
				}
			}
		};
		
		this.addListener(Events.BeforeHide, listener);
		this.addListener(Events.BeforeShow, listener);
		this.addListener(Events.Close, new Listener<TabPanelEvent>() {
			public void handleEvent(TabPanelEvent be) {
				control.hide();
				
				MVController controller = LASAD_Client.getMVCController(mapInfo.getMapID());
				LASAD_Client.unregisterMVCController(controller);
			}
			
		});
	}
}
