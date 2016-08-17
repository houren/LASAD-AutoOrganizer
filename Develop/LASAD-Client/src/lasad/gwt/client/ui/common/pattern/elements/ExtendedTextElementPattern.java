package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;
import lasad.gwt.client.ui.workspace.LASADInfo;

public class ExtendedTextElementPattern extends AbstractExtendedTextElement {

	public ExtendedTextElementPattern(boolean readOnly,
			ExtendedElementContainerInterface container, ElementInfo config,
			boolean labelOnTop) {
		super(readOnly, container, config, labelOnTop);
		this.setBlocked(true);
	}

	public ExtendedTextElementPattern(boolean readOnly,
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(readOnly, container, config);
		this.setBlocked(true);
	}

	public ExtendedTextElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
		this.setBlocked(true);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do
	}

	@Override
	protected void sendRequestLockElement(String mapID, int elementID) {
		// not interested in this event
	}

	@Override
	protected void sendRequestUnlockElement(String mapID, int elementID) {
		// not interested in this event
	}
	
	/*
	 * used to block text input in text field
	 * and keep normal CSS style
	 */
	@Override
	public void setBlocked(boolean blocked) {
		if (DEBUG)
			LASADInfo.display("setBlocked", blocked + "");
		super.setBlocked(blocked);

		if (isBlocked()) {
			if (!ExtendedTextElementPattern.this.isActiveViewMode()) {
				this.setViewMode(true);
			}

			if (textArea != null) {
				if (labelOnTop) {
					textArea.setClassName("extendedTextElement-TextArea-ViewModeTop");
				} else {
					textArea.setClassName("extendedTextElement-TextArea-ViewMode");
				}
				textArea.setAttribute("readonly", "readonly");
			}
			if (textField != null) {
				textField.setClassName("extendedTextElement-TextField-ViewMode");
				textField.setAttribute("readonly", "readonly");
			}
			this.setElementFocus(false);
		}

		else {
			if (ExtendedTextElementPattern.this.isActiveViewMode()) {
				if (textArea != null) {
					if (labelOnTop) {
						textArea.setClassName("extendedTextElement-TextArea-ViewModeTop");
					} else {
						textArea.setClassName("extendedTextElement-TextArea-ViewMode");
					}
					textArea.removeAttribute("readonly");
				}
				if (textField != null) {
					textField.setClassName("extendedTextElement-TextField-ViewMode");
					textField.removeAttribute("readonly");
				}
			}

//			if (wantFocus) {
//				if (AbstractExtendedTextElement.this.isActiveViewMode()) {
//					// Ask for Focus
//					AbstractExtendedTextElement.this.getContainer().getFocusHandler()
//							.setFocus(AbstractExtendedTextElement.this);
//					wantFocus = false;
//				}
//			}
		}
	}

}
