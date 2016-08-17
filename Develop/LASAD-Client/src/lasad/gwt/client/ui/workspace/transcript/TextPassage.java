package lasad.gwt.client.ui.workspace.transcript;

import java.util.Vector;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class TextPassage extends BoxComponent {

	// Some TranscriptLink related Stuff
	private Vector<TranscriptLinkData> tDataMap = new Vector<TranscriptLinkData>();
	private TranscriptLinkData activeTData = null;

	private String text;

	private TranscriptRow transcriptRow;

	private int start = 0;
	private int end = 0;

	private boolean emptyState = true;

	// MouseEventListener
	Listener<DomEvent> listener;

	// Drag & Drop
	private DragSource dragSource;
	private boolean lastMouseDown = false;

	public TextPassage(TranscriptRow transcriptRow, String text, int start) {
		this.transcriptRow = transcriptRow;

		this.setText(text);
		this.setStart(start);
		this.setEnd(start + text.length());

		init();
	}

	/**
	 * Some initiation stuff
	 */
	private void init() {
		// Create Listener for MouseEvents
		this.sinkEvents(Events.OnMouseDown.getEventCode());
		this.sinkEvents(Events.OnMouseUp.getEventCode());
		this.sinkEvents(Events.OnMouseOver.getEventCode());
		this.sinkEvents(Events.OnContextMenu.getEventCode());
	}

	/**
	 * Add a TranscriptLink to this TextPassage
	 * 
	 * @param tData
	 *            the TranscriptLink
	 */
	public void addTranscriptLink(TranscriptLinkData tData) {
		this.getTDataMap().add(tData);
		tData.getTextPassages().add(this);
		this.emptyState = false;
	}

	/**
	 * Remove a TranscriptLink from the TextPassage
	 * 
	 * @param tData
	 *            the TranscriptLink to remove
	 */
	public void removeTranscriptLink(TranscriptLinkData tData) {
		this.tDataMap.remove(tData);
		tData.getTextPassages().remove(this);
		if (this.tDataMap.isEmpty()) {
			this.emptyState = true;
		}
		// Update the active TranscriptLink of the TextPassage
		switchToNextTLD();
		updateActiveTranscriptStyle();
	}

	/**
	 * Activate a TranscriptLink
	 * 
	 * @param tData
	 *            the TranscriptLink to activate
	 */
	public void activateTranscriptLink(TranscriptLinkData tData) {

		if (!tDataMap.contains(tData) && tData != null) {
			addTranscriptLink(tData);
		}
		this.setActiveTData(tData);

		updateActiveTranscriptStyle();
	}

	/**
	 * Switch and Returns the active TranscriptLink of the TextArea
	 * 
	 * @return the new active TranscriptLink
	 */
	public TranscriptLinkData switchToNextTLD() {
		if (!tDataMap.isEmpty()) {
			if (tDataMap.lastElement().equals(activeTData)) {
				activeTData = tDataMap.firstElement();
			} else {
				activeTData = tDataMap.get(tDataMap.indexOf(activeTData) + 1);
			}
		} else {
			activeTData = null;
		}
		return activeTData;
	}

	/**
	 * Updates the TextPassage Style
	 */
	protected void updateActiveTranscriptStyle() {
		if (this.isRendered()) {

			this.el().setStyleName("trans-span"); // BaseStyle
			this.setStyleAttribute("backgroundColor", "");
			this.setStyleAttribute("borderColor", "");

			if (activeTData == null || !activeTData.isSelected()) {
				dragSource.getDraggable().setEnabled(false);
				// Selection.enableTextSelection(this.getElement());
				enableTextSelection2(this.getElement());
			} else {
				dragSource.getDraggable().setEnabled(true);
				// Selection.disableTextSelection(this.getElement());
				// Selection.clearAnySelectedText();
				disableTextSelection2(this.getElement());

			}

			if (activeTData != null) {
				// Set general style infos
				this.el().addStyleName("trans-background");

				// Set active general style
				this.el().addStyleName(activeTData.getStyle());

				this.setStyleAttribute("backgroundColor", activeTData.getBgColor());
				this.setStyleAttribute("borderColor", activeTData.getBgColor());

				this.el().addStyleName("trans-highlight-middle");
				// trans-highlight-active-start-end
				// trans-highlight-inner-active-end
				// trans-highlight-active-start
				// trans-highlight-inner-active-start-end

				// if (this.getStart() == activeTData.getStartPoint() &&
				// transcriptRow.getLineNumber() == activeTData.getStartRow() &&
				// this.getEnd() == activeTData.getEndPoint() &&
				// activeTData.getStartRow() == activeTData.getEndRow()) {
				// this.el().addStyleName(activeTData.getStyle() +
				// "-start-end");
				//
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.el().addStyleName("trans-highlight-active-start-end");
				// }
				// } else if (this.getStart() == activeTData.getStartPoint() &&
				// transcriptRow.getLineNumber() == activeTData.getStartRow()) {
				//
				// // Check if another TranscriptLink Starts or End
				// // there
				// for (TranscriptLinkData tData : this.tDataMap) {
				// if (this.getEnd() == tData.getEndPoint() &&
				// transcriptRow.getLineNumber() == tData.getEndRow()) {
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.addStyleName("trans-highlight-inner-active-end");
				// } else {
				// this.addStyleName(activeTData.getStyle() + "-start-end");
				// }
				// wasSet = true;
				// break;
				// }
				// }
				//
				// if (!wasSet) {
				// this.el().addStyleName(activeTData.getStyle() + "-start");
				//
				// }
				//
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.el().addStyleName("trans-highlight-active-start");
				// }
				// } else if (this.getEnd() == activeTData.getEndPoint() &&
				// transcriptRow.getLineNumber() == activeTData.getEndRow()) {
				// for (TranscriptLinkData tData : this.tDataMap) {
				// if (this.getStart() == tData.getStartPoint() &&
				// transcriptRow.getLineNumber() == tData.getStartRow()) {
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.addStyleName("trans-highlight-inner-active-start");
				// } else {
				// this.el().addStyleName(activeTData.getStyle() +
				// "-start-end");
				// }
				// wasSet = true;
				// break;
				// }
				// }
				// if (!wasSet) {
				// this.el().addStyleName(activeTData.getStyle() + "-end");
				//
				// }
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.el().addStyleName("trans-highlight-active-end");
				// }
				// } else {
				// // Check if another TranscriptLink Starts or End
				// // there
				// for (TranscriptLinkData tData : this.tDataMap) {
				// if (this.getStart() == tData.getStartPoint() &&
				// transcriptRow.getLineNumber() == tData.getStartRow() &&
				// this.getEnd() == tData.getEndPoint() && tData.getStartRow()
				// == tData.getEndRow()) {
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.addStyleName("trans-highlight-inner-active-start-end");
				// } else {
				// this.el().addStyleName(activeTData.getStyle() +
				// "-start-end");
				// }
				// wasSet = true;
				// break;
				// }
				// }
				//
				// if (!wasSet) {
				// for (TranscriptLinkData tData : this.tDataMap) {
				// if (this.getStart() == tData.getStartPoint() &&
				// transcriptRow.getLineNumber() == tData.getStartRow()) {
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.addStyleName("trans-highlight-inner-active-start");
				// } else {
				// this.el().addStyleName(activeTData.getStyle() + "-start");
				// }
				// wasSet = true;
				// break;
				// }
				// }
				// }
				// if (!wasSet) {
				// for (TranscriptLinkData tData : this.tDataMap) {
				// if (this.getEnd() == tData.getEndPoint() &&
				// transcriptRow.getLineNumber() == tData.getEndRow()) {
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.addStyleName("trans-highlight-inner-active-end");
				// } else {
				// this.el().addStyleName(activeTData.getStyle() + "-end");
				// }
				// wasSet = true;
				// break;
				// }
				// }
				// }
				// if (!wasSet) {
				// this.el().addStyleName(activeTData.getStyle() + "-middle");
				//
				// }
				// if (activeTData.isActive() && !activeTData.isSelection()) {
				// this.el().addStyleName("trans-highlight-active");
				// }
				// }
			}
		}
	}

	protected void onRender(Element target, int index) {

		// Create the needed DOM Element for the Text
		Element element = DOM.createSpan();
		element.setInnerText(this.text);

		super.onRender(target, index);

		setElement(element, target, index);
	}

	protected void afterRender() {

		this.el().setStyleName("trans-span"); // Set BaseStyle

		this.transcriptRow.getTranscript().registerEventElement(this.getElement(), this);

		makeDraggable();

		super.afterRender();

	}

	private void makeDraggable() {
		dragSource = new DragSource(this) {

			// dragSource.setData(null);
			// dragSource.getDraggable().setStartDragDistance(0);

			// DNDListener dndListener = new DNDListener() {
			@Override
			public void onDragStart(DNDEvent e) {
				e.setData(new TranscriptLinkData(activeTData));
				e.getStatus().update("");
				e.getStatus().el().updateZIndex(XDOM.getTopZIndex());

				e.cancelBubble();
			}
		};

		// this.dragSource.addDNDListener(dndListener);
		// dragSource.getDraggable().setEnabled(false);

	}

	/*
	 * GETTER & SETTER
	 */

	/**
	 * Return the TextPassage Text
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	public TranscriptRow getTranscriptRow() {
		return transcriptRow;
	}

	public void setTranscriptRow(TranscriptRow transcriptRow) {
		this.transcriptRow = transcriptRow;
	}

	public Vector<TranscriptLinkData> getTDataMap() {
		return tDataMap;
	}

	public void settDataMap(Vector<TranscriptLinkData> tDataMap) {
		this.tDataMap = tDataMap;
	}

	public TranscriptLinkData getActiveTData() {
		return activeTData;
	}

	public void setActiveTData(TranscriptLinkData activeTData) {
		this.activeTData = activeTData;
	}

	/**
	 * Set the TextPassage Text
	 * 
	 * @param text
	 *            the text
	 */
	public void setText(String text) {
		this.text = text;
		if (this.isRendered()) {
			this.getElement().setInnerText(text);
		}
	}

	/**
	 * Return the StartPoint of the Text
	 * 
	 * @return the StartPoint
	 */
	public int getStart() {
		return this.start;
	}

	/**
	 * Set the StartPoint of the Text
	 * 
	 * @param start
	 *            the StartPoint
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Return the EndPoint of the Text
	 * 
	 * @return the EndPoint
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Set the EndPoint of the Text
	 * 
	 * @param end
	 *            the EndPoint
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	public boolean isLastMouseDown() {
		return lastMouseDown;
	}

	public void setLastMouseDown(boolean lastMouseDown) {
		this.lastMouseDown = lastMouseDown;
	}

	/*
	 * EVENTS
	 */

	public void handleEvent(DomEvent be) {
		if (activeTData != null && activeTData.isSelected()) {
			if (be.getType() == Events.OnMouseDown) {
				this.fireEvent(be.getType(), be); // Fire Events for Drag&Drop
				if (!dragSource.getDraggable().isDragging()) {
					this.fireEvent(be.getType(), be); // Fire Events for
														// Drag&Drop
				}
			}
			be.cancelBubble();
		} else {
			if (be.getType() == Events.OnMouseDown) {
				handleMouseDown(be);
			} else if (be.getType() == Events.OnMouseUp) {
				handleMouseUp(be);
			} else if (be.getType() == Events.OnMouseOver) {
				handleMouseOver(be);
			} else if (be.getType() == Events.OnMouseMove) {
				handleMouseMove(be);
			}
			be.cancelBubble();

		}
	}

	private void handleMouseDown(DomEvent be) {
		if (getTranscriptRow().getTranscript().getSelectionStartRow() == null) {
			this.getTranscriptRow().getTranscript().setSelectionMode(true);
			this.getTranscriptRow().getTranscript().setSelectionStartRow(this.getTranscriptRow());
			this.getTranscriptRow().setSelectionStartTextPassage(this);
		}

		this.setLastMouseDown(true);
		this.sinkEvents(Events.OnMouseMove.getEventCode());
	}

	private void handleMouseUp(DomEvent be) {
		if (this.isLastMouseDown()) {
			// Emulates an onClick Event
			this.setLastMouseDown(false);
			this.unsinkEvents(Events.OnMouseMove.getEventCode());

			this.getTranscriptRow().getTranscript().setSelectionMode(false);

			if (!this.isEmptyState()) {
				this.getTranscriptRow().getTranscript().onTextPassageClick(this, be);
			} else {
				this.getTranscriptRow().getTranscript().resetSelection();
				this.getTranscriptRow().getTranscript().resetNativeSelection();
			}
		} else {
			if (this.getTranscriptRow().getTranscript().isSelectionMode()) {
				this.getTranscriptRow().getTranscript().setSelectionMode(false);
				this.getTranscriptRow().getTranscript().setSelectionEndRow(this.getTranscriptRow());
				this.getTranscriptRow().setSelectionEndTextPassage(this);
				this.getTranscriptRow().getTranscript().onSelectArea();
			}
		}
	}

	public boolean isEmptyState() {
		return emptyState;
	}

	public void setEmptyState(boolean emptyState) {
		this.emptyState = emptyState;
	}

	private void handleMouseOver(DomEvent be) {
		// this.setLastMouseDown(false);
		// if (this.getTranscriptRow().getTranscript().isSelectionMode() &&
		// this.getTranscriptRow().getTranscript().getSelectionStartRow().getSelectionStartTextPassage()
		// == null) {
		// this.getTranscriptRow().getTranscript().getSelectionStartRow().setSelectionStartTextPassage(this);
		// }
	}

	private void handleMouseMove(DomEvent be) {
		this.setLastMouseDown(false);
	}

	private native void disableTextSelection2(Element element)/*-{
		element.onselectstart = function() {
		return false;
		};
		element.unselectable = "on";
		element.style.MozUserSelect = "none";
		element.style.cursor = "default";
	}-*/;

	private native void enableTextSelection2(Element element)/*-{
		element.onselectstart = function() {
		return true;
		};
		element.unselectable = "off";
		element.style.MozUserSelect = "default";
		element.style.cursor = "default";
	}-*/;

}
