package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public abstract class AbstractExtendedSpotsElement extends AbstractExtendedElement {
    
    private ElementInfo config = null;
    
    public AbstractExtendedSpotsElement(
	    ExtendedElementContainerInterface container, ElementInfo config, int valueSpot1, int valueSpot2) {
	super(container, config);
	this.config = config;
	/**
	 * SPOT1: left spot
	 * TEXT1: left text
	 * SPOT2: right spot
	 * TEXT1: right text
	 */
	this.elementVars.add(ParameterTypes.Spot1);
	this.elementVars.add(ParameterTypes.Text1);
	this.elementVars.add(ParameterTypes.Spot2);
	this.elementVars.add(ParameterTypes.Text2);

	this.setVarValue(ParameterTypes.Spot1, Integer.toString(valueSpot1),"");
	this.setVarValue(ParameterTypes.Spot2, Integer.toString(valueSpot2),"");
    }

    private Element elementContent = null;
    private Element tbody = DOM.createTBody();
    	private Element row = DOM.createTR();
    	private Element labelLeftTD = DOM.createTD();
    	private Element spotLeftTD = DOM.createTD();
    	private Element labelRightTD = DOM.createTD();
    	private Element spotRightTD = DOM.createTD();
    		private Element textLeft = DOM.createDiv();
    		private Element spotLeft = DOM.createDiv();
    		private Element textRight = DOM.createDiv();
    		private Element spotRight = DOM.createDiv();

    protected void buildElement() {
	if (elementContent != null) {
	    // Already built
	    return;
	}

	// INIT Spots Info

	// Build Content Div
	elementContent = DOM.createTable();
	elementContent.setClassName("extendedSpotsElement-text");
	textLeft.setClassName("extendedSpotsElement-text");
	textRight.setClassName("extendedSpotsElement-text");
	
	textLeft.setInnerHTML(config.getElementOption(ParameterTypes.Text1));
	textRight.setInnerHTML(config.getElementOption(ParameterTypes.Text2));
	
	DOM.appendChild(labelLeftTD, textLeft);
	DOM.appendChild(labelRightTD, textRight);
	DOM.appendChild(spotLeftTD, spotLeft);
	DOM.appendChild(spotRightTD, spotRight);
	DOM.appendChild(row, labelLeftTD);
	DOM.appendChild(row, spotLeftTD);
	DOM.appendChild(row, labelRightTD);
	DOM.appendChild(row, spotRightTD);
	DOM.appendChild(tbody, row);
	DOM.appendChild(elementContent, tbody);

	setElementSize(new Size(getActualViewModeWidth(),
		getActualViewModeHeight()));

    }

    @Override
    protected String getVarValue(ParameterTypes vName) {
    	return null;
    }

    @Override
    protected void onEstablishModelConnection() { }

    @Override
    protected void onRemoveModelConnection() { }

    @Override
    protected void setElementHighlight(boolean highlight) { }

    @Override
    protected void setElementSize(Size size) { }

    @Override
    protected void setVarValue(ParameterTypes vName, String value, String username) {
    this.checkForHighlight(username);
    	
	if (vName == ParameterTypes.Spot1) {
	    switch (Integer.parseInt(value)) {
	    case -1:
		spotLeft.setClassName("mySpot_red");
		break;
	    case 0:
		spotLeft.setClassName("mySpot_yellow");
		break;
	    case 1:
		spotLeft.setClassName("mySpot_green");
		break;
	    default:
		break;
	    }
	}
	
	else if (vName == ParameterTypes.Spot2) {
	    switch (Integer.parseInt(value)) {
	    case -1:
		spotRight.setClassName("mySpot_red");
		break;
	    case 0:
		spotRight.setClassName("mySpot_yellow");
		break;
	    case 1:
		spotRight.setClassName("mySpot_green");
		break;
	    default:
		break;
	    } 
	}
	
	else if (vName == ParameterTypes.Text1) {
	    textLeft.setInnerHTML(value);
	}
	
	else if (vName == ParameterTypes.Text2) {
	    textRight.setInnerHTML(value);
	}
    }

    @Override
    protected void switchToEditMode(Element contentFrame) { }

    @Override
    protected void switchToViewMode(Element contentFrame) {
	buildElement();

	if (!contentFrame.hasChildNodes()) {
	    DOM.appendChild(contentFrame, elementContent);
	}

    }

}
