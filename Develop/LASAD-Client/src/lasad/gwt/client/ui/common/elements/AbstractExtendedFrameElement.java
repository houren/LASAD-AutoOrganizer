package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public abstract class AbstractExtendedFrameElement extends AbstractExtendedElement {

	public AbstractExtendedFrameElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
		this.elementVars.add(ParameterTypes.Link);
	}

	Element elementContent = null;

	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

//		EventListener listener = new EventListener() {
//			public void onBrowserEvent(Event be) {
////				int code = be.getTypeInt();
////				if (code == Events.OnClick.getEventCode() && be.getCurrentEventTarget().cast() != urlField) {
////					if (ExtendedFrameElement.this.isActiveViewMode()) {
////						// Ask for Focus
////						ExtendedFrameElement.this.getContainer().getFocusHandler().setFocus(ExtendedFrameElement.this);
////					}
////				} else if (code == Events.OnFocus.getEventCode()) {
////					if (ExtendedFrameElement.this.isActiveViewMode()) {
////						// Ask for Focus by TABULATOR
////						ExtendedFrameElement.this.getContainer().getFocusHandler().setFocus(ExtendedFrameElement.this);
////					}
////				} else if (code == Events.OnBlur.getEventCode()) {
////					if (ExtendedFrameElement.this.isActiveViewMode()) {
////						// Focus was lost by TABULATOR
////						ExtendedFrameElement.this.getContainer().getFocusHandler().releaseFocus(ExtendedFrameElement.this);
////					}
////				}
//				be.stopPropagation();
//			}
//		};

		elementContent = DOM.createIFrame();
		elementContent.setClassName("extendedFrameElement");
		
//		elementContent.setPropertyString("src", "http://web-expresser.appspot.com/?userKey=x5Spdu9XABVAVTSKxzQl67&thumbnail=64");
		elementContent.setPropertyString("src", "http://google.com");

		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	protected void setElementSize(Size size) {
		int balanceWidth = 4, balanceHeight = 4; // 1px distance to the frameborder, 1 px padding in each direction
		//System.out.println("[setElementSize] call");
		if (elementContent!= null){
			DOM.setStyleAttribute(elementContent, "width", Math.max(0, size.width - balanceWidth) + "px");
			DOM.setStyleAttribute(elementContent, "height", Math.max(0, size.height - balanceHeight) + "px");
		}
	}

	protected void switchToEditMode(Element contentFrame) {
		buildElement();
		if (contentFrame != null){
			if (!contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);
			}
		}
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();
		if (contentFrame != null){
			if (!contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);
			}
		}
	}
	
	protected String getVarValue(ParameterTypes name) {
//		if (name == ParameterTypes.Link) {
//			return link;
//		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		this.checkForHighlight(username);

		if (name == ParameterTypes.Link) {
			if (value.startsWith("http://") || value.startsWith("HTTP://")) {
				if (elementContent != null) {
					elementContent.setPropertyString("src", value);
				}
			} 
		}
	}

	@Override
	protected void setElementHighlight(boolean highlight) {
	}

	@Override
	protected void onEstablishModelConnection() {
	}

	@Override
	protected void onRemoveModelConnection() {
	}
}