package lasad.gwt.client.ui.common;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;

public interface ExtendedElementContainerMasterInterface {

	
	public GenericFocusHandler getFocusHandler();

	public GenericHighlightHandler getHighlightHandler();

	public AbstractMVCViewSession getMVCViewSession();
	
	public void extendedElementContainerCallNewHeight(int height);

	public void extendedElementContainerPublishedNewMaxHeight(int height);
	public void extendedElementContainerPublishedNewMinHeight(int height);

}
