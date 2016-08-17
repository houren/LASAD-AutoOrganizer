package lasad.gwt.client.ui.link.helper;

import java.util.ArrayList;

import lasad.gwt.client.helper.connection.data.BezierConnectionData;
import lasad.gwt.client.ui.link.AbstractLinkPanel;

import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * Extended Version of the original EXT Draggable Class
 * Add support for AfterDrag Position Update without setting directly the Link position.
 *  
 * LinkDraggable -> Event: DragEnd -> Create Action -> Send to Server -> Server send to Client -> Model -> Position Update 
 * 
 * @author Zhenyu Geng
 *
 */
public class LinkDraggable extends Draggable {

	private Point p1,p2,p3,p4;
	private boolean isDragging = false;


	public LinkDraggable(Component dragComponent) {
		super(dragComponent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void startDrag(Event event) {
		BezierConnectionData bdata = ((AbstractLinkPanel)getDragWidget()).getMyLink().getBezierConnectionData();
		if (bdata != null) {
			isDragging = true;
		
			// make the shadow invisible
			super.createProxy().setVisible(false);
		
			// Call the real starDrag End Method to prevent the originally behavior
			super.startDrag(event);
		}
	}
	
	@Override
	protected void onMouseMove(Event event) {
		if(isDragging)
		{
			int x = DOM.eventGetClientX(event);
		    int y = DOM.eventGetClientY(event);

			Point cord = ((AbstractLinkPanel)getDragWidget()).el().translatePoints(new Point(x,y));
	        cord = calculateBezier(calculatePointInLine(cord));
	        int posX = (cord.x - 2) - (((AbstractLinkPanel)getDragWidget()).getWidth() / 2);
			int posY = (cord.y- 2) - (((AbstractLinkPanel)getDragWidget()).getHeight() / 2);
			// Call the LinkPanel method to create an Server Action
			((AbstractLinkPanel)getDragWidget()).setPosition(posX, posY);
		}
		super.onMouseMove(event);
	}

	@Override
	protected void stopDrag(Event event) {
		if(isDragging)
		{
			int x = DOM.eventGetClientX(event);
		    int y = DOM.eventGetClientY(event);
			
		    Point cord = ((AbstractLinkPanel)getDragWidget()).el().translatePoints(new Point(x,y));
		    
	        // Call the LinkPanel method to create an Server Action
			((AbstractLinkPanel)getDragWidget()).setPercent(calculatePointInLine(cord));
		}
		isDragging = false;
		// Call the real stopDrag End Method to prevent the originally behavior
		super.stopDrag(event);
	}
	private void initPoint()
	{
		BezierConnectionData b= ((AbstractLinkPanel)getDragWidget()).getMyLink().getBezierConnectionData();
		p1 = new Point(b.getPoints().get(0).getLeft(),b.getPoints().get(0).getTop());
		p4 = new Point(b.getPoints().get(1).getLeft(),b.getPoints().get(1).getTop());
		p2 = new Point(b.getControlPoints().get(0).getLeft(),b.getControlPoints().get(0).getTop());
		p3 = new Point(b.getControlPoints().get(1).getLeft(),b.getControlPoints().get(1).getTop());
	}
	private float calculatePointInLine(Point point)
	{
		initPoint();
		ArrayList<Point> v = getCurvePoint();
		int x = point.x+((AbstractLinkPanel)getDragWidget()).getWidth() / 2;
		int y = point.y + ((AbstractLinkPanel)getDragWidget()).getHeight() / 2;
		int min = Integer.MAX_VALUE;
		int num = 0;
		int temp = 0;
		for(int i = 0; i < 99; i++)
		{
			temp = (v.get(i).x - x)*(v.get(i).x - x) + (v.get(i).y - y)*(v.get(i).y - y);
			if(min>temp)
			{
				min = temp;
				num = i;
			}
			
		}
		
		return(float)num/100f;
	}
	
	private Point calculateBezier(float v)
	{
		float a = 1 - v;
		float b = a * a * a;
		float c = v * v * v;
		float resultX = b * p1.x + 3 * v * a * a * p2.x + 3 * v
				* v * a * p3.x + c * p4.x;
		int X = Math.round(resultX);
		float resultY = b * p1.y + 3 * v * a * a * p2.y + 3 * v
				* v * a * p3.y + c * p4.y;
		int Y = Math.round(resultY);
		Point result = new Point(X, Y);
		return result;
	}
	
	private ArrayList<Point> getCurvePoint()
	{
		ArrayList<Point> p = new ArrayList<Point>();
		for(int i = 1; i < 100 ; i ++)
		{
			p.add(calculateBezier((float)i /100f));
		}
		
		return p;
	}
}
