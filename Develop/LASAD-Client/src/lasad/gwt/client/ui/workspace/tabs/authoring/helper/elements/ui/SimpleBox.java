package lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui;

import java.util.Vector;

import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.helper.connector.UIObjectConnector;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainer;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.ExtendedElementContainerMasterInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.helper.TextSelection;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;



/**
 * Visual representation of each box in the graph-layout
 */
public class SimpleBox extends BoxComponent implements ExtendedElementContainerInterface, ExtendedElementContainerMasterInterface {
	
	private ExtendedElementContainer extendedElementContainer = null; // The container that holds all elements of the box, e.g. text fields, url, etc.	

	private ContentPanel myMap; // Reference to the corresponding map
	
	private String title; // Title of the box, which appears in the header
	private String color; // Color code of the header
	private String border = "standard"; // The outter border of the box
	
	// Constants to calculate elements size correctly
	private final int CONNECTOR_WIDTH = 18, CONNECTOR_HEIGHT = 9;
	private final int BORDER_WIDTH = 5, BORDER_HEIGHT = 5;
	private final int HEADER_HEIGHT = 13;
	
	private Connector cn = null;
	
	public Connector getConnector() {
	    return cn;
	}
	
	// Size of the box
	private int width = 0, height = 0;
	
	// This is a DIV element which is essential for correct positioning in Firefox
	private Element positioningElement;
	
	// DOM elements for 9x9 layout, please don't change the indentation
	private Element outerGrid;
	
	private Element outerGridTBody;
	
	private Element outerTopRow;
		private Element outerTopRowLeftCell;
		
			private Element resizeCornerNE;
				private Element outerTopRowMiddleCell;
					private Element northArea;
				private SimpleBoxConnectorElement northConnector;
					private Element outerTopRowRightCell;
						private Element resizeCornerNW;
				private Element outerMiddleRow;
					private Element outerMiddleRowLeftCell;
							
			private Element westArea;
				private SimpleBoxConnectorElement westConnector;
					private Element outerMiddleRowMiddleCell;
						private Element innerGrid;
				private Element innerGridTBody;
					private Element innerTopRow;
						private Element innerTopRowLeftCell;
						
							private Element borderULC;
								private Element innerTopRowMiddleCell;
									private Element borderUMC;
								private Element innerTopRowRightCell;
									private Element borderURC;
								private Element innerMiddleRow;
									private Element innerMiddleRowLeftCell;
							
							private Element borderMLC;
								private Element innerMiddleRowMiddleCell;
									private Element boxContentDiv; 
								private Element innerMiddleRowRightCell;
									private Element borderMRC;
								private Element innerBottomRow;
									private Element innerBottomRowLeftCell;

							private Element borderBLC;
								private Element innerBottomRowMiddleCell;
									private Element borderBMC;
								private Element innerBottomRowRightCell;
									private Element borderBRC;
								private Element outerMiddleRowRightCell;
									public Element eastArea;
						
				private SimpleBoxConnectorElement eastConnector;
					private Element outerBottomRow;
						private Element outerBottomRowLeftCell;
			private Element resizeCornerSW;
				private Element outerBottomRowMiddleCell;
					private Element southArea;
				private SimpleBoxConnectorElement southConnector;
					private Element outerBottomRowRightCell;
						private Element resizeCornerSE;
				private Element rootElement; // Main element to make the box visible
					private SimpleBoxHeaderElement boxHeading; // Header of the box. Present in all
															// boxes. Needed for the
															// DragListener

	/**
	 * @param map
	 */
	public SimpleBox(ContentPanel map, String title, String border, int width, int height, String color, int rootID) {
		this.myMap = map;
		this.setSize(width, height);
		this.initBoxElements();
		

		this.setBorder(border);
		this.title = title;
		this.color = color;
		
		disableTextSelection();
		
		cn = UIObjectConnector.wrap(this);
		
		boxHeading = new SimpleBoxHeaderElement(this.title);
		boxHeading.render(boxContentDiv, 0);
		ComponentHelper.doAttach(boxHeading);
		
		boxHeading.setRootElementID(rootID+"");
	}
	
	public SimpleBox(ContentPanel map, ElementInfo info) {
		this(map, info.getElementOption(ParameterTypes.Heading), info.getUiOption(ParameterTypes.Border), Integer.parseInt(info.getUiOption(ParameterTypes.Width)== null? info.getUiOption(ParameterTypes.Width): info.getUiOption(ParameterTypes.Width)), Integer.parseInt(info.getUiOption(ParameterTypes.Height)==null? info.getUiOption(ParameterTypes.Height):info.getUiOption(ParameterTypes.Height)), info.getUiOption(ParameterTypes.BackgroundColor), 1);
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element) {
	    extendedElementContainer.addExtendedElement(element);
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
	    extendedElementContainer.addExtendedElement(element, pos);
	}

	protected void afterRender() {
		super.afterRender();

		this.setColor(this.getColor());

		SimpleBox.this.el().updateZIndex(1);

		calculateElementsSize();
	}

	/**
	 * This method calculates the elements' sizes. It should be called each time
	 * the box size changes. Thus it is called in the setSize() method.
	 * 
	 * Don't change anything unless you know what you are doing! Otherwise, increase counter: TOTAL_HOURS_WASTED = 0;
	 * 
	 * @author Frank Loll
	 */
	public void calculateElementsSize() {

		// Outer 3x3 table
		DOM.setStyleAttribute(outerGrid, "height", this.height + "px");
		DOM.setStyleAttribute(outerGrid, "width", this.width + "px");

		DOM.setStyleAttribute(outerGridTBody, "height", this.height + "px");
		DOM.setStyleAttribute(outerGridTBody, "width", this.width + "px");

		DOM.setStyleAttribute(outerTopRow, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerTopRowLeftCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerTopRowMiddleCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerTopRowRightCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerMiddleRow, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerMiddleRowLeftCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerMiddleRowMiddleCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerMiddleRowRightCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerBottomRow, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerBottomRowLeftCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerBottomRowMiddleCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerBottomRowRightCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		// Inner 3x3 table
		DOM.setStyleAttribute(innerGrid, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(innerGrid, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerGridTBody, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(innerGridTBody, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRow, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRowLeftCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerTopRowMiddleCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRowRightCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowRightCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerMiddleRow, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerMiddleRowLeftCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerMiddleRowMiddleCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerMiddleRowRightCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowRightCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerBottomRow, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerBottomRowLeftCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerBottomRowMiddleCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerBottomRowRightCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowRightCell, "width", BORDER_HEIGHT + "px");

		// Borders
		// Top row
		DOM.setStyleAttribute(borderULC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderULC, "width", BORDER_WIDTH + "px");

		DOM.setStyleAttribute(borderUMC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderUMC, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(borderURC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderURC, "width", BORDER_WIDTH + "px");

		// Middle row
		DOM.setStyleAttribute(borderMLC, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(borderMLC, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(borderMRC, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(borderMRC, "width", BORDER_WIDTH + "px");

		// Bottom row
		DOM.setStyleAttribute(borderBLC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBLC, "width", BORDER_WIDTH + "px");

		DOM.setStyleAttribute(borderBMC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBMC, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(borderBRC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBRC, "width", BORDER_WIDTH + "px");

		// Resize corners
		DOM.setStyleAttribute(resizeCornerSW, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerSW, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerNE, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerNE, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerNW, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerNW, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerSE, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerSE, "width", CONNECTOR_HEIGHT + "px");

		// Box content Div: Contains all elements of the concrete box.
		DOM.setStyleAttribute(boxContentDiv, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(boxContentDiv, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		// Update Extended Element

		if (extendedElementContainer != null) {
			extendedElementContainer.setWidth(this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT);
			extendedElementContainer.setHeight(this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT - HEADER_HEIGHT);
		}

		// Connectors, i.e. the knobs used to create relations

		// North
		DOM.setStyleAttribute(northConnector.getElement(), "left", ((this.width - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// South
		DOM.setStyleAttribute(southConnector.getElement(), "left", ((this.width - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// East
		DOM.setStyleAttribute(eastConnector.getElement(), "top", ((this.height - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// West
		DOM.setStyleAttribute(westConnector.getElement(), "top", ((this.height - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		this.setVisible(true);
	}

	/** Avoids unwanted selection of GUI elements on drag & drop **/
	private void disableTextSelection() {
		TextSelection.disableTextSelection(borderULC);
		TextSelection.disableTextSelection(borderUMC);
		TextSelection.disableTextSelection(borderURC);
		TextSelection.disableTextSelection(borderMLC);
		TextSelection.disableTextSelection(borderMRC);
		TextSelection.disableTextSelection(borderBLC);
		TextSelection.disableTextSelection(borderBMC);
		TextSelection.disableTextSelection(borderBRC);
		
		TextSelection.disableTextSelection(resizeCornerNE);
		TextSelection.disableTextSelection(resizeCornerNW);
		TextSelection.disableTextSelection(resizeCornerSE);
		TextSelection.disableTextSelection(resizeCornerSW);

		TextSelection.disableTextSelection(southArea);
		TextSelection.disableTextSelection(northArea);
		TextSelection.disableTextSelection(eastArea);
		TextSelection.disableTextSelection(westArea);		
	}

	public void extendedElementContainerCallNewHeight(int height) {
	    setSize(width, height + CONNECTOR_HEIGHT + CONNECTOR_HEIGHT + BORDER_HEIGHT + BORDER_HEIGHT + HEADER_HEIGHT);
	}

	public void extendedElementContainerPublishedNewMaxHeight(int height) { }

	public void extendedElementContainerPublishedNewMinHeight(int height) { }

	public String getBorder() {
	    return this.border;
	}

	public String getColor() {
	    return color;
	}

	public Direction getDirection() {
	    return null;
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
	    return extendedElementContainer.getExtendedElements();
	}

	public int getHeight() {
	    return height;
	}

	public ContentPanel getMap() {
	    return myMap;
	}

	public Element getRootElement() {
		return rootElement;
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
	    return width;
	}

	private void initBoxElements() {

		positioningElement = DOM.createDiv();
		DOM.setStyleAttribute(positioningElement, "position", "absolute");
		
		// Create outer 3x3 Grid
		outerGrid = DOM.createTable();
		outerGrid.setClassName("box-outerGrid-table");
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", XDOM.getTopZIndex());
		DOM.setIntStyleAttribute(outerGrid, "zIndex", XDOM.getTopZIndex());

		this.outerGridTBody = DOM.createTBody();
		outerGridTBody.setClassName("box-outerGridTBody");
		DOM.appendChild(outerGrid, outerGridTBody);

		this.outerTopRow = DOM.createTR();
		outerTopRow.setClassName("tr-connector");
		this.outerTopRowLeftCell = DOM.createTD();
		outerTopRowLeftCell.setClassName("box-outerGrid-tlc");
		this.resizeCornerNW = DOM.createDiv();
		resizeCornerNW.setClassName("resizeCornerNW");
		DOM.appendChild(outerTopRowLeftCell, resizeCornerNW);
		DOM.appendChild(outerTopRow, outerTopRowLeftCell);

		this.outerTopRowMiddleCell = DOM.createTD();
		outerTopRowMiddleCell.setClassName("box-outerGrid-tmc");
		DOM.appendChild(outerTopRow, outerTopRowMiddleCell);

		this.northArea = DOM.createDiv();
		this.northArea.setClassName("box-areas");
		DOM.appendChild(outerTopRowMiddleCell, this.northArea);

		this.northConnector = new SimpleBoxConnectorElement("north-connector");
		this.northConnector.render(northArea);

		this.outerTopRowRightCell = DOM.createTD();
		outerTopRowRightCell.setClassName("box-outerGrid-trc");
		this.resizeCornerNE = DOM.createDiv();
		resizeCornerNE.setClassName("resizeCornerNE");
		DOM.appendChild(outerTopRowRightCell, resizeCornerNE);
		DOM.appendChild(outerTopRow, outerTopRowRightCell);

		DOM.appendChild(outerGridTBody, outerTopRow);

		this.outerMiddleRow = DOM.createTR();
		outerMiddleRow.setClassName("tr-fullHeight");
		this.outerMiddleRowLeftCell = DOM.createTD();
		outerMiddleRowLeftCell.setClassName("box-outerGrid-mlc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowLeftCell);

		this.westArea = DOM.createDiv();
		this.westArea.setClassName("box-areas");
		DOM.appendChild(outerMiddleRowLeftCell, this.westArea);

		this.westConnector = new SimpleBoxConnectorElement("west-connector");
		this.westConnector.render(westArea);

		this.outerMiddleRowMiddleCell = DOM.createTD();
		outerMiddleRowMiddleCell.setClassName("box-outerGrid-mmc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowMiddleCell);

		this.outerMiddleRowRightCell = DOM.createTD();
		outerMiddleRowRightCell.setClassName("box-outerGrid-mrc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowRightCell);

		this.eastArea = DOM.createDiv();
		this.eastArea.setClassName("box-areas");
		DOM.appendChild(outerMiddleRowRightCell, this.eastArea);

		this.eastConnector = new SimpleBoxConnectorElement("east-connector");
		this.eastConnector.render(eastArea);

		DOM.appendChild(outerGridTBody, outerMiddleRow);

		this.outerBottomRow = DOM.createTR();
		outerBottomRow.setClassName("tr-connector");
		this.outerBottomRowLeftCell = DOM.createTD();
		outerBottomRowLeftCell.setClassName("box-outerGrid-blc");
		this.resizeCornerSW = DOM.createDiv();
		resizeCornerSW.setClassName("resizeCornerSW");
		DOM.appendChild(outerBottomRowLeftCell, resizeCornerSW);
		DOM.appendChild(outerBottomRow, outerBottomRowLeftCell);

		this.outerBottomRowMiddleCell = DOM.createTD();
		outerBottomRowMiddleCell.setClassName("box-outerGrid-bmc");
		DOM.appendChild(outerBottomRow, outerBottomRowMiddleCell);

		this.southArea = DOM.createDiv();
		this.southArea.setClassName("box-areas");
		DOM.appendChild(outerBottomRowMiddleCell, this.southArea);

		this.southConnector = new SimpleBoxConnectorElement("south-connector");
		this.southConnector.render(southArea);

		this.outerBottomRowRightCell = DOM.createTD();
		outerBottomRowRightCell.setClassName("box-outerGrid-brc");
		this.resizeCornerSE = DOM.createDiv();
		resizeCornerSE.setClassName("resizeCornerSE");
		DOM.appendChild(outerBottomRowRightCell, resizeCornerSE);
		DOM.appendChild(outerBottomRow, outerBottomRowRightCell);

		DOM.appendChild(outerGridTBody, outerBottomRow);

		// Create inner 3x3 Grid
		this.innerGrid = DOM.createTable();
		innerGrid.setClassName("box-innerGrid-table");
		DOM.appendChild(outerMiddleRowMiddleCell, innerGrid);

		innerGridTBody = DOM.createTBody();
		innerGridTBody.setClassName("box-innerGridTBody");
		DOM.appendChild(innerGrid, innerGridTBody);

		this.innerTopRow = DOM.createTR();
		innerTopRow.setClassName("upperRow");
		DOM.appendChild(innerGridTBody, innerTopRow);

		this.innerTopRowLeftCell = DOM.createTD();
		innerTopRowLeftCell.setClassName("box-innerGrid-ulc");
		this.borderULC = DOM.createDiv();

		DOM.appendChild(innerTopRowLeftCell, borderULC);

		this.innerTopRowMiddleCell = DOM.createTD();
		innerTopRowMiddleCell.setClassName("box-innerGrid-umc");
		this.borderUMC = DOM.createDiv();

		DOM.appendChild(innerTopRowMiddleCell, borderUMC);

		this.innerTopRowRightCell = DOM.createTD();
		innerTopRowRightCell.setClassName("box-innerGrid-urc");
		this.borderURC = DOM.createDiv();

		DOM.appendChild(innerTopRowRightCell, borderURC);

		DOM.appendChild(innerTopRow, innerTopRowLeftCell);
		DOM.appendChild(innerTopRow, innerTopRowMiddleCell);
		DOM.appendChild(innerTopRow, innerTopRowRightCell);

		innerMiddleRow = DOM.createTR();
		innerMiddleRow.setClassName("middleRow");
		DOM.appendChild(innerGridTBody, innerMiddleRow);

		this.innerMiddleRowLeftCell = DOM.createTD();
		innerMiddleRowLeftCell.setClassName("box-innerGrid-mlc");
		this.borderMLC = DOM.createDiv();
		DOM.appendChild(innerMiddleRowLeftCell, borderMLC);

		this.innerMiddleRowMiddleCell = DOM.createTD();
		innerMiddleRowMiddleCell.setClassName("box-innerGrid-mmc");
		this.boxContentDiv = DOM.createDiv();
		boxContentDiv.setClassName("box-content-div");
		DOM.appendChild(innerMiddleRowMiddleCell, boxContentDiv);

		this.innerMiddleRowRightCell = DOM.createTD();
		innerMiddleRowRightCell.setClassName("box-innerGrid-mrc");
		this.borderMRC = DOM.createDiv();

		DOM.appendChild(innerMiddleRowRightCell, borderMRC);

		DOM.appendChild(innerMiddleRow, innerMiddleRowLeftCell);
		DOM.appendChild(innerMiddleRow, innerMiddleRowMiddleCell);
		DOM.appendChild(innerMiddleRow, innerMiddleRowRightCell);

		this.innerBottomRow = DOM.createTR();
		innerBottomRow.setClassName("bottomRow");
		DOM.appendChild(innerGridTBody, innerBottomRow);

		this.innerBottomRowLeftCell = DOM.createTD();
		innerBottomRowLeftCell.setClassName("box-innerGrid-blc");
		this.borderBLC = DOM.createDiv();

		DOM.appendChild(innerBottomRowLeftCell, borderBLC);

		this.innerBottomRowMiddleCell = DOM.createTD();
		innerBottomRowMiddleCell.setClassName("box-innerGrid-bmc");
		this.borderBMC = DOM.createDiv();

		DOM.appendChild(innerBottomRowMiddleCell, borderBMC);

		this.innerBottomRowRightCell = DOM.createTD();
		innerBottomRowRightCell.setClassName("box-innerGrid-brc");
		this.borderBRC = DOM.createDiv();

		DOM.appendChild(innerBottomRowRightCell, borderBRC);

		DOM.appendChild(innerBottomRow, innerBottomRowLeftCell);
		DOM.appendChild(innerBottomRow, innerBottomRowMiddleCell);
		DOM.appendChild(innerBottomRow, innerBottomRowRightCell);

		DOM.appendChild(positioningElement, outerGrid);
		this.rootElement = positioningElement;

		// Init ExtendedElementContainer
		initExtendedElementContainer();
	}

	private void initExtendedElementContainer() {
		this.extendedElementContainer = new ExtendedElementContainer(this);
		this.extendedElementContainer.setElementSpacing(1);
		this.extendedElementContainer.setPaddingTop(1);
		this.extendedElementContainer.setPaddingBottom(0);
		this.extendedElementContainer.setPaddingLeft(0);
		this.extendedElementContainer.setPaddingRight(0);
		// add Container Element to DIV
		DOM.appendChild(boxContentDiv, extendedElementContainer.getContainerElement());
	}


	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	public void removeExtendedElement(AbstractExtendedElement element) {
	    extendedElementContainer.removeExtendedElement(element);
	}

	/**
	 * Changes the box border to a predefined class or a passed #color
	 * 
	 * Classes are predefined in boxHighlight.css and box.css
	 * 
	 * @param String border, predefined class or hex color code
	 */
	public void setBorder(String border) {

	    if (border.equals("highlighted")) {

			borderULC.setClassName("border-highlighted");
			borderUMC.setClassName("border-highlighted");
			borderURC.setClassName("border-highlighted");
			borderMLC.setClassName("border-highlighted");
			borderMRC.setClassName("border-highlighted");
			borderBLC.setClassName("border-highlighted");
			borderBMC.setClassName("border-highlighted");
			borderBRC.setClassName("border-highlighted");
	    }

	    else if (border.equals("standard")) {

		borderULC.setClassName("border-ulc-standard");
		borderUMC.setClassName("border-umc-standard");
		borderURC.setClassName("border-urc-standard");
		borderMLC.setClassName("border-mlc-standard");
		borderMRC.setClassName("border-mrc-standard");
		borderBLC.setClassName("border-blc-standard");
		borderBMC.setClassName("border-bmc-standard");
		borderBRC.setClassName("border-brc-standard");


	    }

	    else if (border.equals("double")) {
		borderULC.setClassName("border-ulc-double");
		borderUMC.setClassName("border-umc-double");
		borderURC.setClassName("border-urc-double");
		borderMLC.setClassName("border-mlc-double");
		borderMRC.setClassName("border-mrc-double");
		borderBLC.setClassName("border-blc-double");
		borderBMC.setClassName("border-bmc-double");
		borderBRC.setClassName("border-brc-double");
	    }

	    else if (border.equals("round")) {
		borderULC.setClassName("border-ulc-round");
		borderUMC.setClassName("border-umc-round");
		borderURC.setClassName("border-urc-round");
		borderMLC.setClassName("border-mlc-round");
		borderMRC.setClassName("border-mrc-round");
		borderBLC.setClassName("border-blc-round");
		borderBMC.setClassName("border-bmc-round");
		borderBRC.setClassName("border-brc-round");
	    }

	    else if (border.equals("dashed")) {
		borderULC.setClassName("border-ulc-dashed");
		borderUMC.setClassName("border-umc-dashed");
		borderURC.setClassName("border-urc-dashed");
		borderMLC.setClassName("border-mlc-dashed");
		borderMRC.setClassName("border-mrc-dashed");
		borderBLC.setClassName("border-blc-dashed");
		borderBMC.setClassName("border-bmc-dashed");
		borderBRC.setClassName("border-brc-dashed");
	    }

	    else if (border.equals("zigzag")) {
		borderULC.setClassName("border-ulc-zigzag");
		borderUMC.setClassName("border-umc-zigzag");
		borderURC.setClassName("border-urc-zigzag");
		borderMLC.setClassName("border-mlc-zigzag");
		borderMRC.setClassName("border-mrc-zigzag");
		borderBLC.setClassName("border-blc-zigzag");
		borderBMC.setClassName("border-bmc-zigzag");
		borderBRC.setClassName("border-brc-zigzag");
	    }
	    else {
		// Change Class to border-highlighted-color defined in boxHighlight.css
		// It has no style attributes but is necessary for a "clean" change
		borderULC.setClassName("border-highlighted-color");
		borderUMC.setClassName("border-highlighted-color");
		borderURC.setClassName("border-highlighted-color");
		borderMLC.setClassName("border-highlighted-color");
		borderMRC.setClassName("border-highlighted-color");
		borderBLC.setClassName("border-highlighted-color");
		borderBMC.setClassName("border-highlighted-color");
		borderBRC.setClassName("border-highlighted-color");

		// Look for hex color code and change background via DOM to given color
		DOM.setStyleAttribute(borderULC, "background", border);
		DOM.setStyleAttribute(borderUMC, "background", border);
		DOM.setStyleAttribute(borderURC, "background", border);
		DOM.setStyleAttribute(borderMLC, "background", border);
		DOM.setStyleAttribute(borderMRC, "background", border);
		DOM.setStyleAttribute(borderBLC, "background", border);
		DOM.setStyleAttribute(borderBMC, "background", border);
		DOM.setStyleAttribute(borderBRC, "background", border);
	    }
	}

	public void setColor(String color) {
	    this.color = color;
	    DOM.setStyleAttribute(boxHeading.getElement(), "backgroundColor", color);
	}

	public void setHeight(int height) {
	    this.height = height;
	}

	@Override
	public void setSize(int width, int height) {
		if (this.width != width || this.height != height) {

			this.width = width;
			this.height = height;
			super.setSize(width, height);

			if (this.isRendered()) {
				calculateElementsSize();
			}
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWidth(int width) {
	    this.width = width;
	}

	// Not needed!
	
	@Override
	public ElementInfo getElementInfo() {
		return null;
	}

	@Override
	public GenericFocusHandler getFocusHandler() {
		return null;
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return null;
	}

	@Override
	public MVCViewSession getMVCViewSession() {
		return null;
	}

	@Override
	public FocusableInterface getFocusParent() {
		return null;
	}

	@Override
	public void setElementFocus(boolean focus) { }

	@Override
	public HighlightableElementInterface getHighlightParent() {
		return null;
	}

	@Override
	public void setHighlight(boolean highlight) { }

	@Override
	public void textAreaCallNewHeightgrow(int height) {
		// TODO Auto-generated method stub
		
	}
}