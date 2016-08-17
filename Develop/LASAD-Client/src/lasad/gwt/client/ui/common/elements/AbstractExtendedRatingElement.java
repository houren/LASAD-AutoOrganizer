package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractExtendedRatingElement extends AbstractExtendedElement{

	private int localScore = 0;
	private int min_width = 10;
	private final String label;
	private final int MAX_SCORE;
	private final int MIN_SCORE;

	private EventListener myEventListener = null;

	private Element elementContent = null;
	Element tbody = DOM.createTBody();
	Element row = DOM.createTR();
	Element labelTD = DOM.createTD();
	Element minusTD = DOM.createTD();
	Element scoreTD = DOM.createTD();
	Element plusTD = DOM.createTD();
	Element spaceTD = DOM.createTD();
		Element text = DOM.createDiv();
		Element minus = DOM.createDiv();
		Element scoreE = DOM.createDiv();
		Element plus = DOM.createDiv();
		Element space = DOM.createDiv();

	public AbstractExtendedRatingElement(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
		this.elementVars.add(ParameterTypes.Score);
		MIN_SCORE = Integer.parseInt(config.getElementOption(ParameterTypes.MinScore));
		MAX_SCORE = Integer.parseInt(config.getElementOption(ParameterTypes.MaxScore));
		if (config.getElementOption(ParameterTypes.Label) != null) {
			label = config.getElementOption(ParameterTypes.Label) + ": ";
		}
		else {
			label = "Score: ";
		}
		
		if(config.getElementOption(ParameterTypes.Score) != null) {
			this.setVarValue(ParameterTypes.Score, config.getElementOption(ParameterTypes.Score), LASAD_Client.getInstance().getUsername());
		}
	}
	
	public AbstractExtendedRatingElement(ExtendedElementContainerInterface container, ElementInfo config, boolean readOnly) {
		this(container, config);
		this.readOnly = readOnly;
	}



	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}
		// Build Content Div
		elementContent = DOM.createTable();


		//SCORE
		text.setInnerHTML(label);

		//SCORE
		scoreE.setInnerHTML(Integer.toString(localScore));

		//PLUS
		plus.setClassName("myButton_plus");
		DOM.setStyleAttribute(plus, "width", min_width+"px");
		DOM.appendChild(elementContent, plus);

		//MINUS
		minus.setClassName("myButton_minus");
		DOM.setStyleAttribute(minus, "width", min_width+"px");
		DOM.appendChild(elementContent, minus);

		DOM.appendChild(elementContent, tbody);
		DOM.appendChild(tbody, row);
		DOM.appendChild(row, labelTD);
		DOM.appendChild(labelTD, text);
		DOM.appendChild(row, scoreTD);
		DOM.appendChild(scoreTD, scoreE);
		DOM.appendChild(row, minusTD);
		DOM.appendChild(minusTD, minus);
		DOM.appendChild(row, plusTD);
		DOM.appendChild(plusTD, plus);
		DOM.appendChild(row, spaceTD);
		DOM.appendChild(spaceTD, space);


		// Set elements' styles				

		//Buttons
		plus.setClassName("myButton_plus");
		minus.setClassName("myButton_minus");

		//Score
		DOM.setStyleAttribute(scoreE, "fontSize", 10+"px");
		DOM.setStyleAttribute(scoreE, "color", "black");
		DOM.setStyleAttribute(scoreE, "width", 6+min_width+"px");
		DOM.setStyleAttribute(scoreE, "align", "left");
		DOM.setStyleAttribute(scoreTD, "width", 6+min_width+"px");
		DOM.setStyleAttribute(scoreTD, "align", "left");
		DOM.setStyleAttribute(scoreTD, "paddingRight", 4+"px");

		//Buttons
		DOM.setStyleAttribute(plus, "width", min_width+"px");
		DOM.setStyleAttribute(plusTD, "width", min_width+"px");
		DOM.setStyleAttribute(plus, "align", "left");
		DOM.setStyleAttribute(plusTD, "align", "left");
		DOM.setStyleAttribute(minus, "width", min_width+"px");
		DOM.setStyleAttribute(minus, "align", "left");
		DOM.setStyleAttribute(minusTD, "align", "left");

		//Label
		DOM.setStyleAttribute(text, "fontSize", 10+"px");
		DOM.setStyleAttribute(text, "color", "black");
		DOM.setStyleAttribute(text, "paddingLeft", 2+"px");
		DOM.setStyleAttribute(text, "paddingRight", 2+"px");

		//Space-hack TO-DO: Find a better way to have everything aligned left....
		DOM.setStyleAttribute(space, "width", 100+"%");
		DOM.setStyleAttribute(spaceTD, "width", 100+"%");

		DOM.setStyleAttribute(elementContent, "borderCollapse", "collapse");

		setElementSize(new Size(getActualViewModeWidth(),
				getActualViewModeHeight()));

		if(isModificationAllowed() && !readOnly) {
			this.createListener();
		}
	}

	public void createListener() {
		this.myEventListener = new EventListener() {
			public void onBrowserEvent(Event be) {
				if(be.getTypeInt()==Events.OnClick.getEventCode()){
					if(AbstractExtendedRatingElement.this.isActiveViewMode()){
						if(((Element) be.getEventTarget().cast()).getClassName().equals("myButton_plus")){
							if (localScore < MAX_SCORE){
								AbstractExtendedRatingElement.this.localScore = AbstractExtendedRatingElement.this.localScore + 1;
								AbstractExtendedRatingElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedRatingElement.this);
								AbstractExtendedRatingElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedRatingElement.this);
							} else {
								LASADInfo.display("Error", "Maximum reached!");
							}
						}
						
						else if (((Element) be.getEventTarget().cast()).getClassName().equals("myButton_minus")){
							if (localScore > MIN_SCORE) {
								AbstractExtendedRatingElement.this.localScore = AbstractExtendedRatingElement.this.localScore - 1;
								AbstractExtendedRatingElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedRatingElement.this);
								AbstractExtendedRatingElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedRatingElement.this);
							}
							else {
								LASADInfo.display("Error", "Minimum reached!");
							}
						}
					}
				} 
				be.stopPropagation();
			}
		};

		DOM.sinkEvents(minus, Events.OnClick.getEventCode());
		DOM.setEventListener(minus, myEventListener);
		DOM.sinkEvents(plus, Events.OnClick.getEventCode());
		DOM.setEventListener(plus, myEventListener);
	}

	protected String getVarValue(ParameterTypes name) {
		if(name == ParameterTypes.Score){
			return Integer.toString(this.localScore);
		}
		return null;
	}



	protected void setVarValue(ParameterTypes name, String value, String username) {
		if(name == ParameterTypes.Score){
			this.checkForHighlight(username);
			
			if (scoreE != null){
				if (localScore <= MAX_SCORE && localScore >= MIN_SCORE){
					this.localScore = Integer.parseInt(value);
					this.scoreE.setInnerHTML(value);
				}
			}
		}
	}

	protected void setElementSize(Size size) { }

	protected void switchToEditMode(Element contentFrame) {

	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();

		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}

	}

	@Override
	protected void setElementHighlight(boolean highlight) {}

	protected void onEstablishModelConnection() { }

	@Override
	protected void onRemoveModelConnection() { }
}
