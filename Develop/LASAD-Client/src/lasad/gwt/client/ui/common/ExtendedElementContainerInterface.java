package lasad.gwt.client.ui.common;

import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;

public interface ExtendedElementContainerInterface extends FocusableInterface, HighlightableElementInterface {
	
	public void addExtendedElement(AbstractExtendedElement element);

	public void addExtendedElement(AbstractExtendedElement element, int pos);

	public void removeExtendedElement(AbstractExtendedElement element);

	public Vector<AbstractExtendedElement> getExtendedElements();
	
	public GenericFocusHandler getFocusHandler();

	public GenericHighlightHandler getHighlightHandler();

	public ElementInfo getElementInfo();
	
	public AbstractMVCViewSession getMVCViewSession();
	
	//Methods my BM to size the box out of the TextArea 
	public void textAreaCallNewHeightgrow(int height);
}
