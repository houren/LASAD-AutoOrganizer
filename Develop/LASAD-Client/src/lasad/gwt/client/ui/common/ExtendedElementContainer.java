package lasad.gwt.client.ui.common;

import java.util.LinkedHashMap;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class ExtendedElementContainer implements ExtendedElementContainerInterface {

	private ExtendedElementContainerMasterInterface master;

	private Element rootDiv = DOM.createDiv();

	private Vector<AbstractExtendedElement> extendedElementsOrdered = new Vector<AbstractExtendedElement>();
	private LinkedHashMap<AbstractExtendedElement, Element> extendedElements = new LinkedHashMap<AbstractExtendedElement, Element>();

	public ExtendedElementContainer(ExtendedElementContainerMasterInterface master) {
		this.master = master;

		initRoot();
	}

	private void initRoot() {
		// Initiate the Root Element

		rootDiv.setClassName("extendedElementContainer-root");
	}

	private int height;

	public void setHeight(int height) {
		if (this.height != height) {
			// Height has changed
			this.height = height;
			updateContainerHeight();
		}
		// Else nothing to do
	}

	private int width;

	public void setWidth(int width) {
		if (this.width != width) {
			// Width has changed
			this.width = width;
			updateContainerWidth();
		}
		// Else nothing to do
	}

	private int minHeight;
	private int maxHeight;

	private void setMinHeight(int height) {
		if (this.minHeight != height) {
			// Update
			this.minHeight = height;
			master.extendedElementContainerPublishedNewMinHeight(height);
		}
	}

	public int getMinHeight() {
		return minHeight;
	}

	private void setMaxHeight(int height) {
		if (this.maxHeight != height) {
			// Update
			this.maxHeight = height;
			master.extendedElementContainerPublishedNewMaxHeight(height);
		}
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	private void updateContainerHeight() {
		int generalHeightOffset = getPaddingTop() + // TOP PADDING
				getPaddingBottom() + // BOOTOM PADDING
				(getElementSpacing() * (extendedElements.size() - 1)); // SPACING
																		// BETWEEN
																		// ELEMENTS

		int usedHeightByFixedElement = 0;
		int minVariableElementSize = 0;
		int maxVariableElementSize = 0;
		int dynamicElementCount = 0;
		int fixedElementCount = 0;

		for (AbstractExtendedElement element : extendedElements.keySet()) {
			if (element.isViewModeFixedSize()) {
				// Element has a fixed size
				// Set Element Container Height
				DOM.setStyleAttribute(extendedElements.get(element), "height", element.getViewModeMinHeight() + "px");
				usedHeightByFixedElement += element.getViewModeMinHeight();
				fixedElementCount++;
			}
			else {
				// Element has an dynamic height
				minVariableElementSize += element.getViewModeMinHeight();
				maxVariableElementSize += element.getViewModeMaxHeight();
				dynamicElementCount++;
			}

			// Set Spacing to mostly each element Container, without the last
			// one
			if (fixedElementCount + dynamicElementCount != extendedElements.size()) {
				// Add ElementSpacing
				DOM.setStyleAttribute(extendedElements.get(element), "marginBottom", getElementSpacing() + "px");
			}
		}

		// Preset Actions:
		// Elements with fixed heights are already done
		// Spacing between Containers are already set

		// Height which will be used for the dynamic Elements
		int leftHeight;
		int update = 0;
		if (this.height - (generalHeightOffset + usedHeightByFixedElement + minVariableElementSize) < 0) {
			// The RootContainer is to small, will run in overflow
			// Master needs to be extended
			update = +1;
			// Set all dyn. Element to its minHeights
			leftHeight = minVariableElementSize;
		}
		else if (this.height - (generalHeightOffset + usedHeightByFixedElement + maxVariableElementSize) > 0) {
			// The RootContainer is to big, space will be left
			// Master needs to be updated
			update = -1;
			leftHeight = maxVariableElementSize;
		}
		else {
			// Size is ok
			leftHeight = this.height - (generalHeightOffset + usedHeightByFixedElement);
		}

		// Now set Height to dynamic Elements

		// Convert LeftHeight to Height, which will be used to expand the dyn.
		// Elements
		int overallSize = leftHeight;
		int overall = (int) overallSize;

		// Old Method befor BM-Method!
//		for (ExtendedElement element : extendedElements.keySet()) {
//			if (!element.isViewModeFixedSize()) {
//				dynamicElementCount--;
//				if (dynamicElementCount != 0) {
//					height = (int) (element.getViewModeMinHeight() + ((double) (overallSize - minVariableElementSize)
//							/ (maxVariableElementSize - minVariableElementSize) * (element.getViewModeMaxHeight() - element
//							.getViewModeMinHeight())));
//					overall -= height;
//				}
//				else {
//					height = overall;
//				}
//				// Set Height to Element
//				element.setActualViewModeHeight(height);
//			}
//		}
		
		
		//--------------------modified by BM------------------------------------------
		//Written 19.09.2012 by BM
		int oldSize = 0;
		for (AbstractExtendedElement element : extendedElements.keySet()) {
			if (!element.isViewModeFixedSize()) {
					oldSize += element.getActualViewModeHeight(); //adding the old hights
			}
		}
		
		for (AbstractExtendedElement element : extendedElements.keySet()) {
			if (!element.isViewModeFixedSize()) {
				dynamicElementCount--;
				if (dynamicElementCount != 0) {
					if (oldSize == 0) { //catch divide by 0 before it happens ^^!
						oldSize = 1;
					} 
					
					/*
					 * Sett the height to the minimum height + the value it gets from the 
					 * set height(overall) - minimum VariableElementSize. So every Element gets minimum the minimumSize. 
					 * If there is more space than the minimum Height it gets the % of its old size in relation to the 
					 * overall old size.		
					 */
					height = (int) (element.getViewModeMinHeight() + ((double)element.getActualViewModeHeight() / (double)oldSize)* (overall - minVariableElementSize ));
					
					//catch if element get to big by my %
					// Does not work?!
					if (height > element.getViewModeMaxHeight()) { 
						height = element.getViewModeMaxHeight();
					}else if(height <= element.getViewModeMinHeight()){
						height = element.getViewModeMinHeight();						
					}
					overall -= height;
				}else { 
					height = overall;
				}
				// Set Height to Element
				element.setActualViewModeHeight(height);
			}
		}
		//Modified by BM----- END -------------------------------------------------------------------	
		

		// Present Actions: All ElementHeight were updated

		// Now update the min & Max size
		if (extendedElements.size() > 0) {
			this.setMaxHeight(maxVariableElementSize + generalHeightOffset + usedHeightByFixedElement);
			this.setMinHeight(minVariableElementSize + generalHeightOffset + usedHeightByFixedElement);

			// Now check Root Container Size, perhaps it needs to be updated
			if (update < 0) {
				// The Size of the root Container needs to be shrieked
				master.extendedElementContainerCallNewHeight(generalHeightOffset + usedHeightByFixedElement
						+ maxVariableElementSize);
			}
			else if (update > 0) {
				// The Size of the root Container needs to be extended
				master.extendedElementContainerCallNewHeight(generalHeightOffset + usedHeightByFixedElement
						+ minVariableElementSize);
			}

		}
		else {
			this.setMaxHeight(0);
			this.setMinHeight(0);
			master.extendedElementContainerCallNewHeight(0);
		}
	}

	private void updateContainerWidth() {
		int generalWidthOffset = getPaddingLeft() + // LEFT PADDING
				getPaddingRight(); // RIGHT PADDING

		for (AbstractExtendedElement element : extendedElements.keySet()) {
			// Set the Width to the ExtendedElementContainer
			DOM.setStyleAttribute(extendedElements.get(element), "width", (this.width - generalWidthOffset) + "px");

			// Set Width to each ExtendedElement
			element.setActualViewModeWidth(Math.max(0, (this.width - generalWidthOffset)));
		}
	}

	public void addExtendedElement(AbstractExtendedElement element) {
		addExtendedElement(element, DOM.getChildCount(rootDiv)+1);
	}

	
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
		if (extendedElements.containsKey(element)) {
			// Already added, nothing to do
			return;
		}
		// Else: Add Element

		// Creates the DIV which hosts an Extended Elements
		Element extendedElementDiv = DOM.createDiv();
	
		extendedElementsOrdered.add(pos, element);
		extendedElements.put(element, extendedElementDiv);
	
		extendedElementDiv.setClassName("extendedElementContainer");

		// Add Element to Container, Add Container to Root
		DOM.appendChild(extendedElementDiv, element.getContentFrame());
		DOM.insertChild(rootDiv, extendedElementDiv, pos);
		// Calculate New Element Heights
		updateContainerHeight();
		updateContainerWidth();
	}
	
	public void addExtendedElement(AbstractExtendedElement element, boolean top) {
		if(top) {
			if (extendedElements.containsKey(element)) {
				return;
			}
			// Else: Add Element

			// Creates the DIV which hosts an Extended Elements
			Element extendedElementDiv = DOM.createDiv();
		
			extendedElementsOrdered.add(0, element);
			extendedElements.put(element, extendedElementDiv);
		
			extendedElementDiv.setClassName("extendedElementContainer");

			// Add Element to Container, Add Container to Root
			DOM.appendChild(extendedElementDiv, element.getContentFrame());
			
			DOM.insertChild(rootDiv, extendedElementDiv, 0);
			// Calculate New Element Heights
			updateContainerHeight();
			updateContainerWidth();
		}
	}
	
	public void removeExtendedElement(AbstractExtendedElement element) {
		if (!extendedElements.containsKey(element)) {
			// Element is unknown
			return;
		}

		// Remove the ElementContainer from DOM
		DOM.removeChild(rootDiv, extendedElements.get(element));

		extendedElementsOrdered.remove(element);
		extendedElements.remove(element);

		// Only effects the Height
		updateContainerHeight();
	}

	public Vector<AbstractExtendedElement> getExtendedElements() {
		return extendedElementsOrdered;

	}

	private int paddingLeft = 0;
	private int paddingRight = 0;
	private int paddingTop = 0;
	private int paddingBottom = 0;
	private int elementSpacing = 0;

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		if (this.paddingLeft != paddingLeft) {
			this.paddingLeft = paddingLeft;
			DOM.setStyleAttribute(rootDiv, "paddingLeft", paddingLeft + "px");
			// Left padding only affects the Width
			updateContainerWidth();
		}
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		if (this.paddingRight != paddingRight) {
			this.paddingRight = paddingRight;
			DOM.setStyleAttribute(rootDiv, "paddingRight", paddingRight + "px");
			// Right padding only affects the Width
			updateContainerWidth();
		}
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		if (this.paddingTop != paddingTop) {
			this.paddingTop = paddingTop;
			DOM.setStyleAttribute(rootDiv, "paddingTop", paddingTop + "px");
			// Top padding only affects the Height
			updateContainerHeight();
		}
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		if (this.paddingBottom != paddingBottom) {
			this.paddingBottom = paddingBottom;
			DOM.setStyleAttribute(rootDiv, "paddingBottom", paddingBottom + "px");
			// bottom padding only affects the Height
			updateContainerHeight();
		}
	}

	/**
	 * Set the Spacing in Pixel between the ExtendedElements
	 * 
	 * @param space
	 */
	public void setElementSpacing(int elementSpacing) {
		if (this.elementSpacing != elementSpacing) {
			// Has Changed
			this.elementSpacing = elementSpacing;
			// Spacing only affects the Height
			updateContainerHeight();
		}
	}

	public int getElementSpacing() {
		return elementSpacing;
	}

	public Element getContainerElement() {
		return this.rootDiv;
	}

	@Override
	public ElementInfo getElementInfo() {
		Logger.log("[ExtendedElementContainer][getElementInfo]: this call should never happened.", Logger.DEBUG_ERRORS);
		return null;
	}

	public GenericFocusHandler getFocusHandler() {
		return master.getFocusHandler();
	}

	public AbstractMVCViewSession getMVCViewSession() {
		return master.getMVCViewSession();
	}

	public FocusableInterface getFocusParent() {
		Logger.log("[ExtendedElementContainer][getFocusParent]: this call should never happened.", Logger.DEBUG_ERRORS);
		return null;
	}

	public void setElementFocus(boolean focus) {
		// Will never happen
		Logger.log("[ExtendedElementContainer][setElementFocous]: this call should never happened.", Logger.DEBUG_ERRORS);
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return master.getHighlightHandler();
	}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		Logger.log("[ExtendedElementContainer][getHighlightParent]: this call should never happened.", Logger.DEBUG_ERRORS);
		return null;
	}

	@Override
	public void setHighlight(boolean highlight) {
		// Will never happen
		Logger.log("[ExtendedElementContainer][setHighlight]: this call should never happened.", Logger.DEBUG_ERRORS);
	}

	@Override
	public void textAreaCallNewHeightgrow(int height) {
		// TODO Auto-generated method stub
		
	}
}
