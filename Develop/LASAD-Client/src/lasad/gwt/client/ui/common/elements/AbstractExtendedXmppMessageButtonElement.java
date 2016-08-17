package lasad.gwt.client.ui.common.elements;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;

public abstract class AbstractExtendedXmppMessageButtonElement extends AbstractExtendedElement {
	
	protected final LASADActionSender communicator = LASADActionSender.getInstance();
	protected final ActionFactory actionBuilder = ActionFactory.getInstance();

	String link = "http://google.com";
	String buttonLabel="";
	public AbstractExtendedXmppMessageButtonElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
		
		
		
		if (config.getElementOption(ParameterTypes.Label) != null || config.getElementOption(ParameterTypes.Label) != "") {
			buttonLabel = config.getElementOption(ParameterTypes.Label);

		} else {
			buttonLabel = "Go To Tool";
		}
		
		this.elementVars.add(ParameterTypes.Link);
		this.setVarValue(ParameterTypes.Link, link, LASAD_Client.getInstance().getUsername());
	}

	Element elementContent = null;

	protected void buildElement() {
		if (elementContent != null) {
			// Already builded
			return;
		}

		EventListener listener = new EventListener() {
			public void onBrowserEvent(Event be) {
				int code = be.getTypeInt();
				if (code == Events.OnClick.getEventCode()) {
					if (AbstractExtendedXmppMessageButtonElement.this.isActiveViewMode()) {
						// Ask for Focus and release it to trigger update
						Vector<Object[]> values = new Vector<Object[]>();
						Object[] tmp = {ParameterTypes.Link, getVarValue(ParameterTypes.Link)};
						values.add(tmp);
						if (LASAD_Client.getInstance().urlParameterConfig.isStandAlone()){
							Window.open(getVarValue(ParameterTypes.Link), "_blank", "");
						}
						MVCViewSession viewSession = (MVCViewSession) AbstractExtendedXmppMessageButtonElement.this.getContainer().getMVCViewSession();
						communicator.sendActionPackage(actionBuilder.updateElementWithMultipleValues(
								viewSession.getController().getMapID(), 
								getConnectedModel().getId(), values));
//						communicator.sendActionPackage(actionBuilder.updateElementWithMultipleValues(
//								AbstractExtendedXmppMessageButtonElement.this.getContainer().getMVCViewSession().getController().getMapID(), 
//								getConnectedModel().getId(), values));
					}
				}
				be.stopPropagation();
			}
		};

		elementContent = DOM.createButton();
		elementContent.setClassName("extendedXmppMessageElement");
		elementContent.setPropertyString("name", buttonLabel);
		elementContent.setPropertyString("value", buttonLabel);
		elementContent.setInnerText(buttonLabel);

		DOM.sinkEvents(this.elementContent, Events.OnClick.getEventCode());
		DOM.setEventListener(this.elementContent, listener);
		//setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
		setElementSize(new Size(100, 50));
	}
	
	protected void switchToEditMode(Element contentFrame) {
		buildElement();
		if (contentFrame != null){
			if (elementContent!= null && !contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);
			}
		}
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();
		if (contentFrame != null){
			if (elementContent!= null && !contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);
			}
		}
	}

	protected String getVarValue(ParameterTypes name) {
		if (name == ParameterTypes.Link) {
			return link;
		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		if (name == ParameterTypes.Link) {
			link = value;
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

	protected void setElementSize(Size size) {
		int balanceWidth = 4, balanceHeight = 4; // 1px distance to the frameborder, 1 px padding in each direction
		if (elementContent!= null ){
			DOM.setStyleAttribute(elementContent, "width", Math.max(0, size.width - balanceWidth) + "px");
			DOM.setStyleAttribute(elementContent, "height", Math.max(0, size.height - balanceHeight) + "px");
		}
	}
}