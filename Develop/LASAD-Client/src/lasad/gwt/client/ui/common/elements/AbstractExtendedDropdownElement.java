package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * @author Benjamin Neu
 * @author anahuacv - refactoring
 * 
 *         This ExtendElement stands for an HTML <select>
 * 
 *         Ontology Example: <element elementid="dropdown"
 *         elementtype="dropdown" quantity="1" minquantity="1" maxquantity="1">
 *         <elementoptions label="Labeltext" options="A,B for Ben,C"
 *         selectedoption="B for Ben" /> <uisettings minheight="40" />
 *         </element>
 */

public abstract class AbstractExtendedDropdownElement extends AbstractExtendedElement {

	private Element elementContent, labelDiv, selectDiv, select = null;

	// private SelectElement select = null;

	public AbstractExtendedDropdownElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Add variable to keep track of selection status
		// it needs to be add to elementVars in order to check it for change on
		// focus change
		this.elementVars.add(ParameterTypes.Selection);
	}

	public AbstractExtendedDropdownElement(ExtendedElementContainerInterface container, ElementInfo config, boolean readOnly) {
		this(container, config);
		this.readOnly = readOnly;
	}

	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

		// ## Build Content Div for subElement ##
		elementContent = DOM.createDiv();
		elementContent.setClassName("box-ee-dropdown-div");

		// ## Sub Element Label ##

		String labelText = getConfig().getElementOption(ParameterTypes.Label);

		// Switch for div width
		boolean label = false;

		if (!labelText.isEmpty()) {
			labelDiv = DOM.createDiv();
			labelDiv.setClassName("box-ee-dropdown-label");

			labelDiv.setInnerHTML(labelText);

			DOM.appendChild(elementContent, labelDiv);

			label = true;
		}

		// ## Sub Element SelectElement ##
		selectDiv = DOM.createDiv();

		selectDiv.setClassName("box-ee-dropdown-selectdiv");

		DOM.appendChild(elementContent, selectDiv);

		// select = Document.get().createSelectElement();
		select = DOM.createSelect();

		if (label == true) {
			select.setClassName("box-ee-dropdown-select");
		} else {
			select.setClassName("box-ee-dropdown-select-only");
		}

		if (isModificationAllowed() && !readOnly) {

			// Eventlistener which handles the onChange Event of the Dropdown
			EventListener listener = new EventListener() {
				public void onBrowserEvent(Event be) {
					int code = be.getTypeInt();
					if (code == Events.OnChange.getEventCode()) {
						be.stopPropagation();
						// In order to send ActionMessage, setFocus and
						// ReleaseFocus
						// which will lead to a ActionMessage, when a Change was
						// made
						// see ExtendElement.setElementFocus and then
						// ExtendElement.updateModel
						AbstractExtendedDropdownElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedDropdownElement.this);
						AbstractExtendedDropdownElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedDropdownElement.this);
					} else if (code == Event.ONMOUSEDOWN) {
						be.stopPropagation();
					}

				}
			};

			DOM.sinkEvents(select, Event.ONCHANGE);
			DOM.setEventListener(select, listener);
		} else {
			((SelectElement)select.cast()).setDisabled(true);
		}

		// Add Options from Element Options
		String options = getConfig().getElementOption(ParameterTypes.Options);
		String[] split = options.split(",");

		// Add Empty Option as first Item
		DOM.insertListItem(select, "", "", 0);

		// Configuration which item has to be selected!
		String selectedOption = getConfig().getElementOption(ParameterTypes.SelectedOption);

		for (int i = 0; i < split.length; i++) {
			DOM.insertListItem(select, split[i], split[i], i);
			// Check if item has to be selected
			if (split[i].equalsIgnoreCase(selectedOption)) {
				// select.setSelectedIndex(i+1);
				DOM.setElementPropertyInt(select, "selectedIndex", i);
			}
		}

		selectDiv.appendChild(select);
	}

	@Override
	protected String getVarValue(ParameterTypes vName) {

		if (vName == ParameterTypes.Selection) {
			return String.valueOf(this.select.getPropertyInt("selectedIndex"));
		}

		return null;
	}

	@Override
	protected void onEstablishModelConnection() {
	}

	@Override
	protected void onRemoveModelConnection() {
	}

	@Override
	protected void setElementHighlight(boolean highlight) {
	}

	@Override
	protected void setElementSize(Size size) {
	}

	@Override
	protected void setVarValue(ParameterTypes vName, String value, String username) {
		this.checkForHighlight(username);

		if (vName == ParameterTypes.Selection) {
			this.select.setPropertyInt("selectedIndex", Integer.valueOf(value));
		}
	}

	@Override
	protected void switchToEditMode(Element contentFrame) {
	}

	@Override
	protected void switchToViewMode(Element contentFrame) {
		buildElement();

		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}

	}

}
