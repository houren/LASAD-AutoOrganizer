package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractExtendedTextElement extends AbstractExtendedElement{

	protected final boolean DEBUG = false;

	// private final LASADActionSender communicator = LASADActionSender.getInstance();
	// private final ActionFactory actionBuilder = ActionFactory.getInstance();

	protected boolean labelOnTop = false; // Used for text fields and areas in the
											// details panel. These will be shown
											// with the label on top instead of the
											// left-hand side.

	private Element elementContent = null;
	private Element labelDiv = null;
	protected Element textArea = null;
	protected Element textField = null;
	private Element textFrameDiv = null;

	EventListener listener = null;

	private boolean wantFocus = false;
	private boolean readOnly = false;
	private boolean autoResize = false;

	public AbstractExtendedTextElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
		this.elementVars.add(ParameterTypes.Text);
		this.elementVars.add(ParameterTypes.Status);
		// if (config.getUiOption("resizable") != null || config.getUiOption("resizable")!="") {
		// resizable =Boolean.parseBoolean(config.getUiOption("resizable"));
		// }
		// else {
		// resizable=true;
		// }
		if (super.isBlocked()) {

			setBlocked(true);
		}

		// MODIFIED BY BM
		try {

			this.autoResize = this.getContainer().getMVCViewSession().getController().getMapInfo().isAutoGrowTextArea();
		} catch (NullPointerException e) {

			// MODIFIED BY SN in order to fix Preview of Nodes when creating them in a ontology. (added try/catch block)
			this.autoResize = false;
		} catch (Exception e) {
			/*for (StackTraceElement stackElement : e.getStackTrace()) {
			}*/

			// MODIFIED BY SN in order to fix Preview of Nodes when creating them in a ontology. (added try/catch block)
			this.autoResize = false;
		}
		// MODIFIED END
	}

	public AbstractExtendedTextElement(boolean readOnly, ExtendedElementContainerInterface container, ElementInfo config) {
		// super(container, config);
		this(container, config);
		this.readOnly = readOnly;
	}

	// public AbstractExtendedTextElement(ExtendedElementContainerInterface container,
	// ElementInfo config, boolean labelOnTop) {
	// this(container, config);
	// if ("textfield".equalsIgnoreCase(config.getElementOption(ParameterTypes.TextType))) {
	// this.labelOnTop = false;
	// } else {
	// this.labelOnTop = labelOnTop;
	// }
	// }

	public AbstractExtendedTextElement(boolean readOnly, ExtendedElementContainerInterface container, ElementInfo config, boolean labelOnTop) {
		this(container, config);
		this.readOnly = readOnly;
		if ("textfield".equalsIgnoreCase(config.getElementOption(ParameterTypes.TextType))) {
			this.labelOnTop = false;
		} else {
			this.labelOnTop = labelOnTop;
		}
	}

	public void setFontSize(int fontSize){
		if(textArea != null){
			textArea.getStyle().setFontSize(fontSize,com.google.gwt.dom.client.Style.Unit.PX);
		}else if(textField != null){
			textField.getStyle().setFontSize(fontSize,com.google.gwt.dom.client.Style.Unit.PX);
		}
	}
	
	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

		String tmpValue;

		elementContent = DOM.createDiv();
		elementContent.setClassName("extendedTextElement");

		if (!readOnly) {

			listener = new EventListener() {
				public void onBrowserEvent(final Event be) {
					int code = be.getTypeInt();
					if (code == Events.OnMouseDown.getEventCode()) {
						be.stopPropagation();
						if (isBlocked() == false) {
							if (AbstractExtendedTextElement.this.isActiveViewMode()) {
								// Ask for Focus
								AbstractExtendedTextElement.this.getContainer().getFocusHandler()
										.setFocus(AbstractExtendedTextElement.this);
							}
						} else {
							wantFocus = true;
						}
					} else if (code == Events.OnClick.getEventCode()) {
						be.stopPropagation();
					} else if (code == Events.OnFocus.getEventCode()) {
						be.stopPropagation();
						if (AbstractExtendedTextElement.this.isActiveViewMode()) {
							// Ask for Focus by TABULATOR
							AbstractExtendedTextElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedTextElement.this);
						}
					} else if (code == Events.OnBlur.getEventCode()) {
						Logger.log("OnBlur!", Logger.DEBUG);
						be.stopPropagation();
						if (!AbstractExtendedTextElement.this.isActiveViewMode()) {
							// Focus was lost by TABULATOR
							AbstractExtendedTextElement.this.getContainer().getFocusHandler()
									.releaseFocus(AbstractExtendedTextElement.this);
						}
						// BM Modification
					} else if (code == Events.OnKeyDown.getEventCode()) {
						Logger.log("Key down event: "+be, Logger.DEBUG);
						if (autoResize) {
							autoSizeTextArea();
						}
					}
					// BM modified END---------------------------------

					// when the user presses enter, commits the text and loses focus
					if (isCommitByEnter()) {
						Event.addNativePreviewHandler(new NativePreviewHandler() {
							public void onPreviewNativeEvent(NativePreviewEvent event) {
								if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && !event.getNativeEvent().getShiftKey()) {
									be.stopPropagation();
									AbstractExtendedTextElement.this.getContainer().getFocusHandler()
											.releaseFocus(AbstractExtendedTextElement.this);
								}
							}
						});
					}

				}
			};
			DOM.sinkEvents(this.elementContent, Events.OnMouseDown.getEventCode());
			DOM.setEventListener(this.elementContent, listener);

		}

		tmpValue = getConfig().getElementOption(ParameterTypes.Label);
		if (tmpValue != null) {
			labelDiv = DOM.createDiv();

			if (labelOnTop) {
				labelDiv.setClassName("extendedTextElement-TextLabel-Top");
			} else {
				labelDiv.setClassName("extendedTextElement-TextLabel-Left");
			}

			labelDiv.setInnerHTML(tmpValue);
			DOM.appendChild(elementContent, labelDiv);
		}

		// TEXT
		textFrameDiv = DOM.createDiv();
		DOM.appendChild(elementContent, textFrameDiv);

		tmpValue = getConfig().getElementOption(ParameterTypes.TextType);

		if (tmpValue != null && tmpValue.equals("textfield")) {
			// OneRowTextfield
			textField = DOM.createInputText();

			// if(labelOnTop) {
			// textField.setClassName("extendedTextElement-TextField-ViewModeTop");
			// }
			// else {
			textField.setClassName("extendedTextElement-TextField-ViewMode");
			// }

			DOM.appendChild(textFrameDiv, textField);

			if (!readOnly) {
				DOM.sinkEvents(textField, Events.Focus.getEventCode());
				DOM.sinkEvents(textField, Events.Blur.getEventCode());
				DOM.sinkEvents(textField, Events.OnMouseOver.getEventCode());
				DOM.setEventListener(textField, listener);
			}
		} else {
			// TextArea
			textArea = DOM.createTextArea();

			if (labelOnTop) {
				textArea.setClassName("extendedTextElement-TextArea-ViewModeTop");
			} else {
				textArea.setClassName("extendedTextElement-TextArea-ViewMode");
			}

			DOM.appendChild(textFrameDiv, textArea);

			if (!readOnly) {
				DOM.sinkEvents(textArea, Events.Focus.getEventCode());
				DOM.sinkEvents(textArea, Events.Blur.getEventCode());
				DOM.sinkEvents(textArea, Events.OnMouseOver.getEventCode());
				DOM.sinkEvents(textArea, Events.OnClick.getEventCode());
				DOM.sinkEvents(textArea, Events.OnKeyDown.getEventCode());
				DOM.setEventListener(textArea, listener);
			}
		}
		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	protected void setElementSize(Size size) {
		int balanceWidth = 4, balanceHeight = 4; // 1px distance to the
													// frameborder, 1 px padding
													// in each direction
		
		int fontSize = 10;
		String mapID = null;
		
		if(this.getContainer().getMVCViewSession() != null){
			mapID = this.getContainer().getMVCViewSession().getController().getMapID();
			fontSize = LASAD_Client.getMapTab(mapID).getMyMapSpace().getMyMap().getFontSize();
		}
		
		if (textFrameDiv != null) {

			DOM.setStyleAttribute(textFrameDiv, "width", Math.max(0, size.width - balanceWidth) + "px");

			DOM.setStyleAttribute(textFrameDiv, "height", Math.max(0, size.height - balanceHeight) + "px");
		}
		
		if (textArea != null) {
			if (labelOnTop && labelDiv != null) {

				DOM.setStyleAttribute(textArea, "height", Math.max(0, size.height - balanceHeight - labelDiv.getOffsetHeight()) + "px");
			} else {

				DOM.setStyleAttribute(textArea, "height", Math.max(0, size.height - balanceHeight) + "px");
			}

			if (labelDiv != null) {
				DOM.setStyleAttribute(textArea, "width", Math.max(0, size.width - balanceWidth - labelDiv.getOffsetWidth()) + "px");
			} else {
				DOM.setStyleAttribute(textArea, "width", Math.max(0, size.width - balanceWidth) + "px");
			}
			
			DOM.setStyleAttribute(textArea, "font-size", fontSize+"px");
		}
		if (textField != null) {
			// if(labelOnTop && labelDiv != null) {
			// DOM.setStyleAttribute(textField,"height",Math.max(0,size.height-balanceHeight-labelDiv.getOffsetHeight())+"px");
			// }
			// else {

			DOM.setStyleAttribute(textField, "height", Math.max(0, size.height - balanceHeight) + "px");
			// }
			if (labelDiv != null) {
				DOM.setStyleAttribute(textField, "width", Math.max(0, size.width - balanceWidth - labelDiv.getOffsetWidth()) + "px");
			} else {
				DOM.setStyleAttribute(textField, "width", Math.max(0, size.width - balanceWidth) + "px");
			}
			
			DOM.setStyleAttribute(textField, "font-size", fontSize+"px");
		}
	}

	protected void switchToEditMode(Element contentFrame) {
		if (!readOnly) {
			if (!isBlocked()) {
				buildElement();
				if (!contentFrame.hasChildNodes()) {
					DOM.appendChild(contentFrame, elementContent);
				}

				if (textArea != null) {
					if (labelOnTop) {
						textArea.setClassName("extendedTextElement-TextArea-EditModeTop");
					} else {
						textArea.setClassName("extendedTextElement-TextArea-EditMode");
					}

					textArea.removeAttribute("readonly");
				}
				if (textField != null) {
					// if(labelOnTop) {
					// textField.setClassName("extendedTextElement-TextField-EditModeTop");
					// }
					// else {
					textField.setClassName("extendedTextElement-TextField-EditMode");
					// }
					textField.removeAttribute("readonly");
				}
			} else {
				if (DEBUG)
					LASADInfo.display("Error", "This element is locked because another user is working on it currently...");
			}
		}
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();
		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
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
			// if(labelOnTop) {
			// textField.setClassName("extendedTextElement-TextField-ViewModeTop");
			// }
			// else {
			textField.setClassName("extendedTextElement-TextField-ViewMode");
			// }
			textField.setAttribute("readonly", "readonly");
		}
	}

	protected String getVarValue(ParameterTypes name) {
		if (name == ParameterTypes.Text) {
			if (textArea != null) {
				return DOM.getElementProperty(this.textArea, "value");
			}
			if (textField != null) {
				return DOM.getElementProperty(this.textField, "value");
			}
		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		if (name == ParameterTypes.Text) {
			this.checkForHighlight(username);

			if (textArea != null) {
				DOM.setElementProperty(this.textArea, "value", value);
			}
			if (textField != null) {
				DOM.setElementProperty(this.textField, "value", value);
			}
		} else if (name == ParameterTypes.Status) {
			if (value.equalsIgnoreCase("LOCK")) {
				this.setBlocked(true);
			} else if (value.equalsIgnoreCase("UNLOCK")) {
				this.setBlocked(false);
			}
		}
	}

	@Override
	public void setBlocked(boolean blocked) {
		if (DEBUG)
			LASADInfo.display("setBlocked", blocked + "");
		super.setBlocked(blocked);

		if (isBlocked()) {
			if (!AbstractExtendedTextElement.this.isActiveViewMode()) {
				this.setViewMode(true);
			}

			if (textArea != null) {
				if (labelOnTop) {
					textArea.setClassName("extendedTextElement-TextArea-LockedModeTop");
				} else {
					textArea.setClassName("extendedTextElement-TextArea-LockedMode");
				}
				textArea.setAttribute("readonly", "readonly");
			}
			if (textField != null) {
				// if(labelOnTop) {
				// textField.setClassName("extendedTextElement-TextField-LockedModeTop");
				// }
				// else {
				textField.setClassName("extendedTextElement-TextField-LockedMode");
				// }
				textField.setAttribute("readonly", "readonly");
			}
			this.setElementFocus(false);
		}

		else {
			if (AbstractExtendedTextElement.this.isActiveViewMode()) {
				if (textArea != null) {
					if (labelOnTop) {
						textArea.setClassName("extendedTextElement-TextArea-ViewModeTop");
					} else {
						textArea.setClassName("extendedTextElement-TextArea-ViewMode");
					}
					textArea.removeAttribute("readonly");
				}
				if (textField != null) {
					// if(labelOnTop) {
					// textField.setClassName("extendedTextElement-TextField-ViewModeTop");
					// }
					// else {
					textField.setClassName("extendedTextElement-TextField-ViewMode");
					// }
					textField.removeAttribute("readonly");
				}
			}

			if (wantFocus) {
				if (AbstractExtendedTextElement.this.isActiveViewMode()) {
					// Ask for Focus
					AbstractExtendedTextElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedTextElement.this);
					wantFocus = false;
				}
			}
		}
	}

	@Override
	public void setElementFocus(boolean focus) {
		super.setElementFocus(focus);
		if (!isBlocked()) {
			if (focus) {
				if (DEBUG)
					LASADInfo.display("setElementFocus", "LOCK");
				// communicator.sendActionPackage(actionBuilder.lockElement(this
				// .getConnectedModel().getValue(ParameterTypes.MapId), this
				// .getConnectedModel().getId()));
				sendRequestLockElement(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId());
			} else {
				//Modified by Darlan Santana Farias
					//resize the box when losing focus
					//added this line so the box get resized after pasting content, intead of only when typing
				if (autoResize) {
					autoSizeTextArea();
				}
				
				if (DEBUG)
					LASADInfo.display("setElementFocus", "UNLOCK");
				// communicator.sendActionPackage(actionBuilder.unlockElement(this
				// .getConnectedModel().getValue(ParameterTypes.MapId), this
				// .getConnectedModel().getId()));
				sendRequestUnlockElement(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId());
			}
		}
	}

	/**
	 * function to grow the textarea. Sets the height to the cur Height + Scroll
	 * height
	 * 
	 * @author BM
	 * @date 19.06.2012
	 */
	public void autoSizeTextArea() {
		// save the old height to get the difference
		int oldClientHeight = textArea.getClientHeight();

		if (textArea.getClientHeight() != textArea.getScrollHeight() && textArea.getClientHeight() < textArea.getScrollHeight()) {
			// size the textArea to the right height
			this.setActualViewModeHeight(textArea.getScrollHeight() + 10);
		}

		ExtendedElementContainerInterface temp = getContainer();
		// call method at BOX! and size it by the difference
		temp.textAreaCallNewHeightgrow(textArea.getClientHeight() - oldClientHeight);
	}

	public int determineBoxHeightChange()
	{
		final int OLD_BOX_HEIGHT = textArea.getClientHeight();
		if (textArea.getClientHeight() != textArea.getScrollHeight() && textArea.getClientHeight() < textArea.getScrollHeight()) {
			// size the textArea to the right height
			this.setActualViewModeHeight(textArea.getScrollHeight() + 10);
		}

		return textArea.getClientHeight() - OLD_BOX_HEIGHT;
	}

	protected abstract void sendRequestLockElement(String mapID, int elementID);

	protected abstract void sendRequestUnlockElement(String mapID, int elementID);

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