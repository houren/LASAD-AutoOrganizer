package lasad.gwt.client.ui.workspace.tabs;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.argument.MapInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class MapTab extends TabItem {

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	private ArgumentMapSpace myMapSpace;

	public MapTab(MapInfo mapInfo) {
		this.setText(myConstants.MapTabText() + " " + String.valueOf(mapInfo.getMapID()) + ": " + mapInfo.getTitle());
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setLayout(new FitLayout());
		this.setBorders(false);
		/* Was trying to add ability for links to appear on screen
		this.addListener(Events.Select, new Listener<ComponentEvent>()
		{
			public void handleEvent(ComponentEvent be)
			{
				if (myMapSpace != null)
				{
					myMapSpace.getMyMap().getFocusHandler().releaseAllFocus();
					LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().leaveMap(myMapSpace.getMyMap().getID()));
					LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().joinMap(myMapSpace.getMyMap().getID()));
					//MapTab.this.close();
					//LASAD_Client.reopen(MapTab.this());
				}				
			}
		});
		*/
	}

	public ArgumentMapSpace getMyMapSpace() {
		return myMapSpace;
	}

	public void setMyMapSpace(ArgumentMapSpace myMapSpace) {
		this.myMapSpace = myMapSpace;
	}
	
}