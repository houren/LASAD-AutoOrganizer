package lasad.gwt.client.ui.workspace.minimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.events.LASADEventListenerInterface;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.ScrollListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;


/**
 * Provides MiniMap with anchor
 * 
 * @author David Drexler
 * 
 * @version 1.0
 */
public class MiniMapPanel extends ContentPanel implements LASADEventListenerInterface {
	//LASAD Client constants for all texts
	private final lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	private CssColor boxBorderColor;
	private CssColor windowMarkingColor;
	private CssColor anchorMarkingColor;
	private CssColor canvasBorderColor;
	
	private Size defaultBoxSize;
	
	private int scrollBugCorrection;
	
	private int buttonLayoutSpace;
	private int panelLayoutSpace;
	private int canvasBorder;
	
	private Size miniMapSize;	
	
	private Canvas myCanvas;
	private Context2d myContext2d;
	
	private AbstractGraphMap myMap;
	
	private HashMap<Integer, RelationOrig> relationOrigRoot;
	private HashMap<Integer, BoxOrig> boxOrigRoot;
	
	private HashMap<String, HashMap<Integer, Line>> relationColorMiniRoots;
	private HashMap<String, HashMap<Integer, Rectangle>> boxColorMiniRoots;
	
	private Point anchorScrollPos;
	private boolean firstAnchor;	
	
	private Size mapSize;
	private Size mapInner;
	
	private float divisorW;
	private float divisorH;

	private Rectangle anchorMarking;		
	private Rectangle currentMarking;
	
	private HashMap<String, Color> relColor;
	private HashMap<String, Color> boxColor;

	
	/**
	 * Initialize new panel of MiniMap.
	 */	
	public MiniMapPanel(AbstractGraphMap map, ArgumentMapSpace mspace) {
				
//Possible changeable settings Edit-BEGIN
//Please change only if you know the impact.
		
		//Set all colors to be used
		this.boxBorderColor = CssColor.make(91, 109, 129);
		this.windowMarkingColor = CssColor.make(255, 0, 0);
		this.anchorMarkingColor = CssColor.make(108, 135, 160);
		this.canvasBorderColor = CssColor.make(61, 76, 95);
		
		//Set default size of box if no size is given
		this.defaultBoxSize = new Size(150,84);
		
		//Set scroll bug correction value
		//Bug: Maximum value of scroll position is always +17 (compared to real dimension of map)
		this.scrollBugCorrection = 17;
		
		//Set layout space for buttons
		this.buttonLayoutSpace = 63;
		//Set layout space for panel (border etc.)
		this.panelLayoutSpace = 3;
		//Set canvas border
		this.canvasBorder = 1;
		
		//Set default MiniMap size at the beginning
		int miniMapWidth = 300 - panelLayoutSpace - 2 * canvasBorder;
		int miniMapHeight = 300 - panelLayoutSpace - 2 * canvasBorder;
		this.miniMapSize = new Size(miniMapWidth, miniMapHeight);
		
//Please change only if you know the impact.		
//Possible changeable settings Edit-END

		
//Initialize remaining global variables of this panel
		
		//Set heading of this panel
		this.setHeading(myConstants.MiniMapHeading());
		
		//Get and set canvas
		this.myCanvas = Canvas.createIfSupported();
		//No canvas? -> so get out of here
		if (myCanvas == null) {
			this.addText(myConstants.MiniMapCanvasErrorMessage());
			return;
		}
		//Get and set context2d from canvas
		this.myContext2d = myCanvas.getContext2d();
		
		//Set canvas coordinates
		this.myCanvas.setCoordinateSpaceWidth(miniMapSize.width + 2 * canvasBorder);
		this.myCanvas.setCoordinateSpaceHeight(miniMapSize.height + 2 * canvasBorder);
		
		//Set ArgumentMap
		this.myMap = map;
		
		//Set layout of this panel
		this.setLayout(new AbsoluteLayout());
		this.setBodyBorder(false);
		this.setBorders(true);
		//Add canvas to this panel
	    this.add(myCanvas, new AbsoluteData(0,0));
		
		//Initialize Roots (BackBones) for original sizes of relations and boxes
		this.relationOrigRoot = new HashMap<Integer, RelationOrig>();
		this.boxOrigRoot = new HashMap<Integer, BoxOrig>();
		//Initialize Roots (BackBones) for MiniMap sizes of relations and boxes - sorted by color (elementID)
		this.relationColorMiniRoots = new HashMap<String, HashMap<Integer, Line>>();
		this.boxColorMiniRoots = new HashMap<String, HashMap<Integer, Rectangle>>();
		
		//Initialize scroll position of anchor
		this.anchorScrollPos = new Point(0,0);
		//To get later the first scroll position as first anchor
		this.firstAnchor = true;
   
		//Get and set current map size
		this.mapSize = myMap.getMapDimensionSize();
		//Get and set map inner size
	    newMapInnerSize();
	    
		//Calculate and initialize divisors (width and height)
		this.divisorW = (float) mapSize.width / miniMapSize.width;
		this.divisorH = (float) mapSize.height / miniMapSize.height;
	    
		//Calculate and initialize markings (anchor and current area)
        this.anchorMarking = newMarkingSize(new Rectangle(0,0,0,0));
        this.currentMarking = newMarkingSize(new Rectangle(0,0,0,0));
        
        //Get and set defined colors of box and relation elements
    	this.boxColor = new HashMap<String, Color>();
    	this.relColor = new HashMap<String, Color>();
        Map<String, ElementInfo> boxElements = myMap.getMyViewSession().getController().getMapInfo().getElementsByType("box");
        Map<String, ElementInfo> relElements = myMap.getMyViewSession().getController().getMapInfo().getElementsByType("relation");
        //Check on all different box colors
        if(!boxElements.isEmpty() && boxElements != null) {
        	for (String key : boxElements.keySet()) {
        		//Get and save color of box
        		//ElementInfo boxInfo = boxElements.get(key);
        		String stringColor = boxElements.get(key).getUiOption(ParameterTypes.BackgroundColor);
        		// MODIFIED by SN in order to fix mini map (the keys in the hashmap need to be lowercase only!)
        		this.boxColor.put(key.toLowerCase(), getColor(stringColor));        		
        	}
		}
        //Check on all different relation colors
        if(!relElements.isEmpty() && relElements != null) {
        	for (String key : relElements.keySet()) {
        		//Get and save color of line
        		String stringColor = relElements.get(key).getUiOption(ParameterTypes.LineColor);
        		this.relColor.put(key.toLowerCase(), getColor(stringColor));
        	}
		}
        
			
//ADD all buttons...
		
		//Set button for using anchor
	    Button useAnchor = new Button(
	    		myConstants.MiniMapUseAnchorButtonText(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
					//Go to saved scroll position of anchor
					myMap.getLayoutTarget().dom.setScrollLeft(anchorScrollPos.x);
					myMap.getLayoutTarget().dom.setScrollTop(anchorScrollPos.y);
				}
		});
	    useAnchor.setScale(ButtonScale.SMALL);
	    useAnchor.setTitle(myConstants.MiniMapUseAnchorButtonTitle());
	    
	   
		//Set button for overwriting anchor with the current area
	    Button newAnchor = new Button(
	    		myConstants.MiniMapNewAnchorButtonText(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
					firstAnchor = false;
					//Get and set new scroll position of anchor
					newAnchorScrollPos();
					//Get and set new position of anchor marking on MiniMap
			        anchorMarking = newMarkingPos(anchorMarking);
			        //Refresh MiniMap
			        drawCompleteMiniMap();
				}
		});
	    newAnchor.setScale(ButtonScale.SMALL);
	    newAnchor.setTitle(myConstants.MiniMapNewAnchorButtonTitle()); 
	    
	    
	    //Add buttons to this panel
	    this.addButton(useAnchor);
	    this.addButton(newAnchor);	    
	    
	    
//ADD all listeners...
	  
	    //Add scroll listener to map
	    myMap.addScrollListener(new ScrollListener() {
	    	@Override
			public void widgetScrolled(ComponentEvent ce) {
	    		//Set the very first scroll position as first anchor
	    		if(firstAnchor) {  			
	    			//Get and set new scroll position of anchor
					newAnchorScrollPos();
					//Get and set new position of anchor marking on MiniMap
			        anchorMarking = newMarkingPos(anchorMarking);
			        //First anchor is now set, so false
					firstAnchor = false;
	    		}
	    		
	    		//Check if map size has changed
	    		Size checkSize = myMap.getMapDimensionSize();
	    		if (mapSize.width != checkSize.width || mapSize.height != checkSize.height) {
	    			//Reconstruct MiniMap
	    			reconstructCompleteMiniMap();
	    		}
	    		else { 
	    			//Get current area and set current marking on MiniMap
	    			currentMarking = newMarkingPos(currentMarking);
	    		}
    			//Refresh MiniMap
    			drawCompleteMiniMap();
	    	}
		});
	
	    
	    //Add resize listener to map
	    myMap.addListener(Events.Resize, new Listener<BoxComponentEvent>() {
			@Override
			public void handleEvent(BoxComponentEvent be) {	
				//Set new inner size of map
		        newMapInnerSize();
		        
				//Check if map size has changed
				Size checkSize = myMap.getMapDimensionSize();
				if (mapSize.width != checkSize.width || mapSize.height != checkSize.height) {
					//Reconstruct MiniMap
					reconstructCompleteMiniMap();
				}
				else {
					//Get new marking sizes on MiniMap
					anchorMarking = newMarkingSize(anchorMarking);
					currentMarking = newMarkingSize(currentMarking);
				}
				//Refresh MiniMap
				drawCompleteMiniMap();
			}
	    });    
	    
	    
	    //Add resize listener to MiniMap
	    this.addListener(Events.Resize, new Listener<BoxComponentEvent>() {
			@Override
			public void handleEvent(BoxComponentEvent be) {
				//Reconstruct MiniMap
				reconstructCompleteMiniMap();
				//Refresh MiniMap
				drawCompleteMiniMap();
			}
	    });    
	    
	    
	    //Add mouse handler to canvas
	    myCanvas.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent mde) {
				//Get mouse coordinates and handle as imaginary center of new view area
				int scrollWidth = Math.round((float) (mde.getX() * divisorW) - (mapInner.width / 2));
				int scrollHeight = Math.round((float) (mde.getY() * divisorH) - (mapInner.height / 2));
				//Get possible maximum scroll positions
				int maxWidth = mapSize.width - mapInner.width + scrollBugCorrection;
				int maxHeight = mapSize.height - mapInner.height + scrollBugCorrection;
				//Check if minimum or maximum scroll position is exceeded
				if (scrollWidth < 0)
					scrollWidth = 0;
				if (scrollHeight < 0)
					scrollHeight = 0;
				if (scrollWidth > maxWidth)
					scrollWidth = maxWidth;
				if (scrollHeight > maxHeight) {
					scrollHeight = maxHeight;
				}
				//Set new scroll position	
				myMap.getLayoutTarget().dom.setScrollLeft(scrollWidth);
				myMap.getLayoutTarget().dom.setScrollTop(scrollHeight);
			}
	    });

	    
		//Add LasadEvent listeners
		myMap.getMyViewSession().addLasadEventListener("MAP_" + Commands.CreateElement, this);
		myMap.getMyViewSession().addLasadEventListener("MAP_" + Commands.UpdateElement, this);
		myMap.getMyViewSession().addLasadEventListener("MAP_" + Commands.DeleteElement, this);
	}

	
	/**
	 * Reacts to fired LasadEvents and handles them for MiniMap.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fireLasadEvent(LasadEvent event) {
		
		//Check if map size has changed
		Size checkSize = myMap.getMapDimensionSize();
		if (mapSize.width != checkSize.width || mapSize.height != checkSize.height) {
			//Reconstruct MiniMap
			reconstructCompleteMiniMap();
			//Refresh MiniMap
			drawCompleteMiniMap();
		}
		
		//Initialize possible parameter values with invalid values
		String type = "", elementID = "";
		//Set "invalid" number to catch missing values later
		int invalid = -100000;
		//Initialize with invalid number
		int posX = invalid, posY = invalid, width = invalid, height = invalid;
		int ID = invalid, parent1 = invalid, parent2 = invalid;
		float percent = (float) invalid;
		
		//Get all important parameters
		for (Parameter param : (Vector<Parameter>) event.getData()) {
			//Get parameter type
			ParameterTypes paramType = param.getType();
			//Get parameter value
			String paramValue = param.getValue();
			
			//Get type
			if (paramType.equals(ParameterTypes.Type)) {
				type = paramValue.toLowerCase();
				//Only box and relation types are needed -> so get out of here
				if(!type.equals("box") && !type.equals("relation"))
					return;
			}
			//Get parents
			else if (paramType.equals(ParameterTypes.Parent)) {
				if (parent1 == invalid)
					parent1 = Integer.parseInt(paramValue);
				else
					parent2 = Integer.parseInt(paramValue);
			}			
			//Get position
			else if (paramType.equals(ParameterTypes.PosX))
				posX = Integer.parseInt(paramValue);
			else if (paramType.equals(ParameterTypes.PosY))
				posY = Integer.parseInt(paramValue);
			//Get size
			else if (paramType.equals(ParameterTypes.Width))
				width = Integer.parseInt(paramValue);
			else if (paramType.equals(ParameterTypes.Height))
				height = Integer.parseInt(paramValue);			
			//Get ID
			else if (paramType.equals(ParameterTypes.Id))
				ID = Integer.parseInt(paramValue);
			//Get element ID
			else if (paramType.equals(ParameterTypes.ElementId))
				elementID = paramValue.toLowerCase();
			//Get percent of relation
			else if (paramType.equals(ParameterTypes.Percent))
				percent = Float.parseFloat(paramValue);			
		}
		
		//It makes no sense without ID -> so get out of here
		if (ID == invalid) {
			return;
		}
		
		//Negative-Position-Bug cleanup
		//Bug: Boxes at the left or upper border can get -1 as position.
		//(By moving extremely close to the border.)
		if (posX == -1) {
			posX = 0;
		}
		if (posY == -1) {
			posY = 0;
		}
		
		//CREATE ELEMENT
		if (event.getType().equalsIgnoreCase(("MAP_" + Commands.CreateElement))) {
			
			//It makes no sense without elementID -> so get out of here
			if (elementID.isEmpty()) {
				return;
			}
			//Check on type - new relation makes no sense without parent1 and parent2
			else if (type.equals("relation") && parent1 != parent2 && parent2 != invalid) {
				
				//Get possible parents
				BoxOrig box1 = boxOrigRoot.get(parent1);
				RelationOrig rel1 = relationOrigRoot.get(parent1);
				BoxOrig box2 = boxOrigRoot.get(parent2);
				RelationOrig rel2 = relationOrigRoot.get(parent2);
				int x1, y1, x2, y2;
				boolean box1type, box2type;				
			
				//Check on type of parent 1
				if(box1 != null) {
					//Calculate new starting values of relation
					x1 = Math.round((float) box1.x + box1.width / 2);
					y1 = Math.round((float) box1.y + box1.height / 2);
					//Add this relation to list of parent 1 (box)
					box1.relationIDs.add(ID);
					boxOrigRoot.put(parent1, box1);
					//Set type of parent 1 as a box
					box1type = true;
				}
				else if (rel1 != null) {
					//Calculate new starting values of relation
					x1 = Math.round((float) rel1.x1 + (rel1.x2 - rel1.x1) * rel1.percent);
					y1 = Math.round((float) rel1.y1 + (rel1.y2 - rel1.y1) * rel1.percent);
					//Add this relation to list of parent 1 (relation)
					rel1.relationIDs.add(ID);
					relationOrigRoot.put(parent1, rel1);
					//Set type of parent 1 as a relation
					box1type = false;
				}
				else {
					//Should never happen -> so get out of here
					return;
				}
				
				//Check on type of parent 2
				if(box2 != null) {					
					//Calculate new ending values of relation
					x2 = Math.round((float) box2.x + box2.width / 2);
					y2 = Math.round((float) box2.y + box2.height / 2);
					//Add this relation to list of parent 2 (box)
					box2.relationIDs.add(ID);
					boxOrigRoot.put(parent2, box2);
					//Set type of parent 2 as a box
					box2type = true;
				}
				else if (rel2 != null) {										
					//Calculate new ending values of relation
					x2 = Math.round((float) rel2.x1 + (rel2.x2 - rel2.x1) * rel2.percent);
					y2 = Math.round((float) rel2.y1 + (rel2.y2 - rel2.y1) * rel2.percent);
					//Add this relation to list of parent 2 (relation)
					rel2.relationIDs.add(ID);
					relationOrigRoot.put(parent2, rel2);
					//Set type of parent 2 as a relation
					box2type = false;
				}
				else {
					//Should never happen -> so get out of here
					return;
				} 
				//Set default percent if invalid
				if(percent == (float) invalid) {
					percent = 0.5F;
				}
				
				//Calculate values for MiniMap
				int x1Mini = Math.round((float) x1 / divisorW);
				int y1Mini = Math.round((float) y1 / divisorH);
				int x2Mini = Math.round((float) x2 / divisorW);
				int y2Mini = Math.round((float) y2 / divisorH);
				
				//Get minimal root by color of relation
				HashMap<Integer,Line> relationMiniRoot = relationColorMiniRoots.get(elementID);
				//Create new instance if none exists
				if (relationMiniRoot == null) {
					relationMiniRoot = new HashMap<Integer,Line>();
				}
				//Save new minimal relation
				relationMiniRoot.put(ID, new Line(x1Mini, y1Mini, x2Mini, y2Mini));
				relationColorMiniRoots.put(elementID, relationMiniRoot);
				//Save new original relation
				relationOrigRoot.put(ID, new RelationOrig(x1, y1, x2, y2, elementID, parent1, parent2, box1type, box2type, percent));	
			}
			
			//Check on type - new box makes no sense with a parent element
			else if (type.equals("box") && parent1 == invalid) {	
				//New element makes no sense without box positions -> so get out of here
				if (posX == invalid || posY == invalid)
					return;
				//Set default size if size is missing
				else if (width == invalid || height == invalid) {
					width = defaultBoxSize.width;
					height = defaultBoxSize.height;
				}
						
				//Calculate values for MiniMap
				int posXMini = Math.round((float) posX / divisorW);
				int posYMini = Math.round((float) posY / divisorH);
				int widthMini = Math.round((float) width / divisorW);
				int heightMini = Math.round((float) height / divisorH);
				
				//Get minimal root by color of box
				HashMap<Integer,Rectangle> boxMiniRoot = boxColorMiniRoots.get(elementID);
				//Create new instance if none exists
				if (boxMiniRoot == null) {
					boxMiniRoot = new HashMap<Integer,Rectangle>();
				}
				//Save new minimal box
				boxMiniRoot.put(ID, new Rectangle(posXMini, posYMini, widthMini, heightMini));
				boxColorMiniRoots.put(elementID, boxMiniRoot);
				//Save new original box
				boxOrigRoot.put(ID, new BoxOrig(posX, posY, width, height, elementID));	
			}
			else {
				//Another created element - not interested -> so get out of here
				return;
			}
		}	
		//UPDATE ELEMENT
		else if (event.getType().equalsIgnoreCase(("MAP_" + Commands.UpdateElement))) {
			
			//Check on type - updated relation makes no sense without new percent value
			if (relationOrigRoot.containsKey(ID) && percent != (float) invalid) {
				//Get original relation
				RelationOrig relOrig = relationOrigRoot.get(ID);
				//Set new percent value
				relOrig.percent = percent;
				//Save changed relation
				relationOrigRoot.put(ID, relOrig);
				//Check on related relations
				if (!relOrig.relationIDs.isEmpty()) {
					recalculateRelations(ID, relOrig.relationIDs);
				}
			}
			//Check on type
			else if (boxOrigRoot.containsKey(ID)) {
				
				//Get old original values
				BoxOrig boxOrig = boxOrigRoot.get(ID);
				//Get minimal root by color of box
				HashMap<Integer,Rectangle> boxMiniRoot = boxColorMiniRoots.get(boxOrig.elementID);
				//Get old minimal values
				Rectangle boxMini = boxMiniRoot.get(ID);
												
				//Check new position values
				if (posX != invalid && posY != invalid) {
					boxMini.x = Math.round((float) posX / divisorW);
					boxMini.y = Math.round((float) posY / divisorH);
					boxOrig.x = posX;
					boxOrig.y = posY;					
				}
				//Check new size values
				if (width != invalid && height != invalid) {
					boxMini.width = Math.round((float) width / divisorW);
					boxMini.height = Math.round((float) height / divisorH);
					boxOrig.width = width;
					boxOrig.height = height;
				}
				
				//Save changed minimal box
				boxMiniRoot.put(ID, boxMini);
				boxColorMiniRoots.put(boxOrig.elementID, boxMiniRoot);
				//Save changed original box
				boxOrigRoot.put(ID, boxOrig);
				//Check on related relations
				if (!boxOrig.relationIDs.isEmpty()) {
					recalculateRelations(ID, boxOrig.relationIDs);
				}
			}
			else {
				//Another updated element - not interested -> so get out of here
				return;
			}				
		}
		//DELETE ELEMENT			
		else if (event.getType().equalsIgnoreCase(("MAP_" + Commands.DeleteElement))) {
				
			//Check on type
			if (relationOrigRoot.containsKey(ID)) {
				//Get original relation
				RelationOrig relOrig = relationOrigRoot.get(ID);
				//Get minimal root by color of relation
				HashMap<Integer,Line> relationMiniRoot = relationColorMiniRoots.get(relOrig.elementID);
				//Remove minimal relation
				relationMiniRoot.remove(ID);
				relationColorMiniRoots.put(relOrig.elementID, relationMiniRoot);
				//Remove original relation
				relationOrigRoot.remove(ID);
				//Check on related boxes
				if(relOrig.box1) {
					//Delete this from relation list at box 1
					BoxOrig box1 = boxOrigRoot.get(relOrig.element1ID);
					box1.relationIDs.remove(box1.relationIDs.indexOf(ID));
					boxOrigRoot.put(relOrig.element1ID, box1);
				}
				if(relOrig.box2) {
					//Delete this from relation list at box 2
					BoxOrig box2 = boxOrigRoot.get(relOrig.element2ID);
					box2.relationIDs.remove(box2.relationIDs.indexOf(ID));
					boxOrigRoot.put(relOrig.element2ID, box2);							
				} else {
					RelationOrig relTarget = relationOrigRoot.get(relOrig.element2ID);
					relTarget.relationIDs.remove(relTarget.relationIDs.indexOf(ID));
					relationOrigRoot.put(relOrig.element2ID, relTarget);
				}
			}
			//Check on type
			else if (boxOrigRoot.containsKey(ID)) {		
				//Get original box
				BoxOrig boxOrig = boxOrigRoot.get(ID);
				//Get minimal root by color of box
				HashMap<Integer,Rectangle> boxMiniRoot = boxColorMiniRoots.get(boxOrig.elementID);
				//Remove minimal box
				boxMiniRoot.remove(ID);
				boxColorMiniRoots.put(boxOrig.elementID, boxMiniRoot);
				//Remove original box
				boxOrigRoot.remove(ID);
			}
			else {
				//Element to delete not registered -> so get out of here
				return;
			}	
		}
		else {
			//Another map event - not interested -> so get out of here
			return;
		}
		
		//Refresh MiniMap
		drawCompleteMiniMap();
	}

	
	/**
	 * Draws and refreshes MiniMap completely new.
	 */
	private void drawCompleteMiniMap() {
		//Clear whole canvas
		myContext2d.clearRect(0, 0, miniMapSize.width + 2*canvasBorder, miniMapSize.height + 2*canvasBorder);
		
		//Draw each relation
		if(!relationColorMiniRoots.isEmpty()) {
			//Set line width of relation
			myContext2d.setLineWidth(1);
			//Draw each path of one color
			for (String elementID : relationColorMiniRoots.keySet()) {
				//Get minimal root by color of relation
				HashMap<Integer,Line> relationMiniRoot = relationColorMiniRoots.get(elementID);
				//Begin path on canvas
				myContext2d.beginPath();
				//Set each relation on path
				if(!relationMiniRoot.isEmpty()) {
					for (int key : relationMiniRoot.keySet()) {
						//Get line of relation
						Line line = relationMiniRoot.get(key);
						//Set starting point of line on path
						myContext2d.moveTo(line.x1 + canvasBorder, line.y1 + canvasBorder);
						//Set ending point of line on path
					    myContext2d.lineTo(line.x2 + canvasBorder, line.y2 + canvasBorder);
					}
				}
				//Close path on canvas
				myContext2d.closePath();
				//Set drawing color
				Color strokeColor = relColor.get(elementID);
				CssColor strokeCssColor = CssColor.make(strokeColor.red, strokeColor.green, strokeColor.blue);
				myContext2d.setStrokeStyle(strokeCssColor);
				//Draw path
				myContext2d.stroke();
			}
		}		
		
		//Draw each box
		if(!boxColorMiniRoots.isEmpty()) {
			for (String elementID : boxColorMiniRoots.keySet()) {
				//Get minimal root by color of box
				HashMap<Integer,Rectangle> boxMiniRoot = boxColorMiniRoots.get(elementID);
				//Draw each box of one color
				if(!boxMiniRoot.isEmpty()) {
					for (int key : boxMiniRoot.keySet()) {
						//Get rectangle of box
						Rectangle rect = boxMiniRoot.get(key);
						//Set drawing color for border of box
						myContext2d.setFillStyle(boxBorderColor);
						//Draw border of box
						myContext2d.fillRect(rect.x + canvasBorder, rect.y + canvasBorder, rect.width, rect.height);
						//Set drawing color for filling of box
						Color fillColor = boxColor.get(elementID);
						CssColor fillCssColor = CssColor.make(fillColor.red, fillColor.green, fillColor.blue);
						myContext2d.setFillStyle(fillCssColor);
						//Draw filling of box
						myContext2d.fillRect(rect.x + canvasBorder + 1, rect.y + canvasBorder + 1, rect.width - 2, rect.height - 2);
					}
				}
			}
		}
		
		//Set and draw canvas border
		int borderPos = -canvasBorder;
		int borderWidth = miniMapSize.width + 2*canvasBorder;
		int borderHeight = miniMapSize.height + 2*canvasBorder;
		drawMarking(new Rectangle(borderPos, borderPos, borderWidth, borderHeight), canvasBorderColor);
		
		//Draw anchor marking
		drawMarking(anchorMarking, anchorMarkingColor);
		//Draw current area marking
		drawMarking(currentMarking, windowMarkingColor);
	}
	
	
	/**
	 * Draws marking of rectangle on MiniMap.
	 * 
	 *  @param rect		the rectangle to draw
	 *  @param color	drawing color
	 */
	private void drawMarking(Rectangle rect, CssColor color) {
		myContext2d.setLineWidth(1);
		myContext2d.setStrokeStyle(color);
		myContext2d.strokeRect(rect.x + canvasBorder, rect.y + canvasBorder, rect.width, rect.height);
	}	
	

	/**
	 * Calculates new position of marking using current scroll positions.
	 * 
	 * @param	marking		marking to modify
	 * @return				changed marking with new position
	 */
	private Rectangle newMarkingPos(Rectangle marking) {
		//Get values of current scroll position
		int scrollX = myMap.getHScrollPosition();
		int scrollY = myMap.getVScrollPosition();
		//Method call with current scroll positions - after that return of changed marking 
		return newMarkingPos(marking, new Point(scrollX, scrollY));
	}	
	
	
	/**
	 * Calculates new position of marking using given scroll positions.
	 * 
	 * @param	marking  		marking to modify
	 * @param	scrollPosition	given scroll position
	 * @return					changed marking with new position
	 */
	private Rectangle newMarkingPos(Rectangle marking, Point scrollPosition) {
		//Scroll-Bug cleanup
		//Bug: Maximum value of scroll position is always +17 compared to real dimension of map
		float fixX = (float) scrollPosition.x;
		float fixY = (float) scrollPosition.y;
		int maxX = mapSize.width - mapInner.width;
		int maxY = mapSize.height - mapInner.height;
		fixX = (float) maxX * fixX / (maxX + scrollBugCorrection);
		fixY = (float) maxY * fixY / (maxY + scrollBugCorrection);
	
		//Calculate and set new positions of marking
		marking.x = Math.round((float) fixX / divisorW);
		marking.y = Math.round((float) fixY / divisorH);
		
		return marking;
	}		 	

	
	/**
	 * Calculates new size of marking using map inner size and divisors.
	 * 
	 * @param	marking		marking to modify
	 * @return				changed marking with new size	
	 */
	private Rectangle newMarkingSize(Rectangle marking) {
		marking.width = Math.round((float) mapInner.width / divisorW);
		marking.height = Math.round((float) mapInner.height / divisorH);
		
		return marking;
	}
	
	
	/**
	 * Sets new anchor using current scroll positions.
	 */
	private void newAnchorScrollPos() {
		anchorScrollPos.x = myMap.getHScrollPosition();
	    anchorScrollPos.y = myMap.getVScrollPosition();
	}

	
	/**
	 * Sets current size of inner map.
	 */
	private void newMapInnerSize() {
		mapInner = new Size(myMap.getInnerWidth(), myMap.getInnerHeight());	
	}	
	
	
	/**
	 * Reconstructs complete MiniMap.
	 */
	private void reconstructCompleteMiniMap() {
		//Get and set current map size
		mapSize = myMap.getMapDimensionSize();
		//Calculate and set current MiniMap size
		miniMapSize.width =  getSize().width - panelLayoutSpace - 2 * canvasBorder;
		miniMapSize.height = getSize().height - buttonLayoutSpace - panelLayoutSpace - 2 * canvasBorder;
		
		//Calculate ratios of the sizes
		float ratioWidth = (float) mapSize.width / miniMapSize.width;
		float ratioHeight = (float) mapSize.height / miniMapSize.height;
				
		//Check on smaller ratio
		if (ratioWidth < ratioHeight) {
			//Smaller ratio is width - so it has to be recalculated
			miniMapSize.width = Math.round((float) mapSize.width * miniMapSize.height / mapSize.height);
		}
		else if (ratioWidth > ratioHeight) {
			//Smaller ratio is height - so it has to be recalculated
			miniMapSize.height = Math.round((float) mapSize.height * miniMapSize.width / mapSize.width);
		}
		
		//Set new canvas coordinates
		myCanvas.setCoordinateSpaceWidth(miniMapSize.width + 2 * canvasBorder);
		myCanvas.setCoordinateSpaceHeight(miniMapSize.height + 2 * canvasBorder);
		
		//Calculate and set new divisors
		divisorW = (float) mapSize.width / miniMapSize.width;
		divisorH = (float) mapSize.height / miniMapSize.height;
		
		//Calculate each relation new using original map values
		if(!relationOrigRoot.isEmpty()) {
			for (int key : relationOrigRoot.keySet()) {
				//Get original relation
				RelationOrig relOrig = relationOrigRoot.get(key);
				//Calculate new minimal values
				int x1 = Math.round((float) relOrig.x1 / divisorW);
				int y1 = Math.round((float) relOrig.y1 / divisorH);
				int x2 = Math.round((float) relOrig.x2 / divisorW);
				int y2 = Math.round((float) relOrig.y2 / divisorH);
				//Get minimal root by color of relation
				HashMap<Integer,Line> relMiniRoot = relationColorMiniRoots.get(relOrig.elementID);
				//Save changed minimal relation
				relMiniRoot.put(key, new Line(x1, y1, x2, y2));
				relationColorMiniRoots.put(relOrig.elementID, relMiniRoot);
			}
		}
		
		//Calculate each box new using original map values
		if(!boxOrigRoot.isEmpty()) {
			for (int key : boxOrigRoot.keySet()) {
				//Get original box
				BoxOrig boxOrig = boxOrigRoot.get(key);
				//Calculate new minimal values
				int x = Math.round((float) boxOrig.x / divisorW);
				int y = Math.round((float) boxOrig.y / divisorH);
				int width = Math.round((float) boxOrig.width / divisorW);
				int height = Math.round((float) boxOrig.height / divisorH);
				//Get minimal root by color of box
				HashMap<Integer,Rectangle> boxMiniRoot = boxColorMiniRoots.get(boxOrig.elementID);
				//Save changed minimal box
				boxMiniRoot.put(key, new Rectangle(x, y, width, height));
				boxColorMiniRoots.put(boxOrig.elementID, boxMiniRoot);
			}
		}
		
		//Calculate and set new marking sizes and positions
	    anchorMarking = newMarkingSize(anchorMarking);
	    currentMarking = newMarkingSize(currentMarking);
	    anchorMarking = newMarkingPos(anchorMarking, anchorScrollPos);
	    currentMarking = newMarkingPos(currentMarking);
	}
	
	
	/**
	 * Recalculates all relations of given list.
	 * 
	 * @param ID			ID of the element from where the list is
	 * @param relationIDs	list of related relations
	 */	
	private void recalculateRelations(int ID, ArrayList<Integer> relationIDs) {
		//Get iterator
		Iterator<Integer> it = relationIDs.iterator();
		//Check on each relation
		while (it.hasNext()) {
			//Get ID of relation
			int relID = it.next();
			//Get original relation
			RelationOrig rel = relationOrigRoot.get(relID);
			//Get minimal root by color of relation
			HashMap<Integer,Line> relationMiniRoot = relationColorMiniRoots.get(rel.elementID);
			//Get minimal relation
			Line relMini = relationMiniRoot.get(relID);			
			
			//Check whether the given element is starting or ending point of relation
			if (ID == rel.element1ID) {
				//Check on type of given element 1
				if(rel.box1) {
					//Get original box 1
					BoxOrig box1 = boxOrigRoot.get(rel.element1ID);
					//Recalculate original starting values of relation
					rel.x1 = Math.round((float) box1.x + box1.width / 2);
					rel.y1 = Math.round((float) box1.y + box1.height / 2);
				}
				else {
					//Get original relation 1
					RelationOrig rel1 = relationOrigRoot.get(rel.element1ID);
					//Recalculate original starting values of relation
					rel.x1 = Math.round((float) rel1.x1 + (rel1.x2 - rel1.x1) * rel1.percent);
					rel.y1 = Math.round((float) rel1.y1 + (rel1.y2 - rel1.y1) * rel1.percent);
				}
				//Recalculate minimal starting values
				relMini.x1 = Math.round((float) rel.x1 / divisorW);
				relMini.y1 = Math.round((float) rel.y1 / divisorH);
			}
			else if (ID == rel.element2ID) {
				//Check on type of given element 2
				if(rel.box2) {
					//Get original box 2
					BoxOrig box2 = boxOrigRoot.get(rel.element2ID);
					//Recalculate original ending values of relation
					rel.x2 = Math.round((float) box2.x + box2.width / 2);
					rel.y2 = Math.round((float) box2.y + box2.height / 2);
				}
				else {
					//Get original relation 2
					RelationOrig rel2 = relationOrigRoot.get(rel.element2ID);
					//Recalculate original ending values of relation
					rel.x2 = Math.round((float) rel2.x1 + (rel2.x2 - rel2.x1) * rel2.percent);
					rel.y2 = Math.round((float) rel2.y1 + (rel2.y2 - rel2.y1) * rel2.percent);
				}
				//Recalculate minimal ending values
				relMini.x2 = Math.round((float) rel.x2 / divisorW);
				relMini.y2 = Math.round((float) rel.y2 / divisorH);
			}
			else {
				//Should never happen -> so get out of here
				return;
			}
			
			//Save recalculated minimal relation
			relationMiniRoot.put(relID, relMini);
			relationColorMiniRoots.put(rel.elementID, relationMiniRoot);
			//Save recalculated original relation
			relationOrigRoot.put(relID, rel);
			
			//Check on related relations
			if (!rel.relationIDs.isEmpty()) {
				recalculateRelations(relID, rel.relationIDs);
			}			
		}
	}
	
	
	/**
	 * Converts a string to a color.
	 * 
	 * @param	stringColor		string of color
	 * @return					converted color	
	 */
	private Color getColor(String stringColor) {
		//Check on possible VGA color names
		if(stringColor.equalsIgnoreCase("black"))
			stringColor = "#000000";
		else if(stringColor.equalsIgnoreCase("maroon"))
			stringColor = "#800000";
		else if(stringColor.equalsIgnoreCase("green"))
			stringColor = "#008000";
		else if(stringColor.equalsIgnoreCase("olive"))
			stringColor = "#808000";
		else if(stringColor.equalsIgnoreCase("navy"))
			stringColor = "#000080";
		else if(stringColor.equalsIgnoreCase("purple"))
			stringColor = "#800080";
		else if(stringColor.equalsIgnoreCase("teal"))
			stringColor = "#008080";
		else if(stringColor.equalsIgnoreCase("silver"))
			stringColor = "#C0C0C0";
		else if(stringColor.equalsIgnoreCase("gray"))
			stringColor = "#808080";
		else if(stringColor.equalsIgnoreCase("red"))
			stringColor = "#FF0000";
		else if(stringColor.equalsIgnoreCase("lime"))
			stringColor = "#00FF00";
		else if(stringColor.equalsIgnoreCase("yellow"))
			stringColor = "#FFFF00";
		else if(stringColor.equalsIgnoreCase("blue"))
			stringColor = "#0000FF";
		else if(stringColor.equalsIgnoreCase("fuchsia"))
			stringColor = "#FF00FF";
		else if(stringColor.equalsIgnoreCase("aqua"))
			stringColor = "#00FFFF";
		else if(stringColor.equalsIgnoreCase("white"))
			stringColor = "#FFFFFF";        		
		
		//Set white as default color
		Color newColor = new Color(255,255,255);
		
		//Calculate decimal color values of hex color
		if(stringColor.charAt(0) == '#') {
    		newColor.red = Integer.valueOf(stringColor.substring( 1, 3), 16);
    		newColor.green = Integer.valueOf(stringColor.substring( 3, 5), 16);
    		newColor.blue = Integer.valueOf(stringColor.substring( 5, 7), 16);
    	}

		return newColor;
	}
}
