/*
 * Copyright 2007 Michał Baliński
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package lasad.gwt.client.helper.common.bezier;

import lasad.gwt.client.helper.connection.data.Point;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Canvas based implementation of BezierCurve.
 * Do not instantiate directly @see BezierCurve
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 * 
 * Modified by LASAD team
 */
public class BezierCurveCanvas extends BezierCurve {

	private Element styledDiv = DOM.createDiv();
	
	/**
	 * Private constructor.
	 * Do not instantiate directly @see BezierCurve
	 */
	protected BezierCurveCanvas() {	}
	
	private Element canvas;
	
	{
		initCanvas();
	}

	protected void initCanvas(){
		this.canvas = DOM.createElement("canvas");
		String prev = DOM.getElementAttribute(canvas, "class");
		DOM.setElementAttribute(canvas, "class", prev + " gwt-diagrams-canvas");
		
		DOM.appendChild(RootPanel.get().getElement(), styledDiv); // hack :(
		DOM.setElementAttribute(styledDiv, "class","gwt-diagrams-vml-curve");
		
	}
	
	public void draw(Point p1, Point p2, Point c1, Point c2) {
		
//		Point start = new Point( Math.min(p1.left, p2.left), Math.min(p1.top, p2.top) );
		
		int lineWidth = Integer.parseInt(DOM.getStyleAttribute(styledDiv, "width").replace("px", ""));
		
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		if(p1.left < minX) minX = p1.left;
		if(p1.left > maxX) maxX = p1.left;
		
		if(p2.left < minX) minX = p2.left;
		if(p2.left > maxX) maxX = p2.left;
		
		if(c1.left < minX) minX = c1.left;
		if(c1.left > maxX) maxX = c1.left;
		
		if(c2.left < minX) minX = c2.left;
		if(c2.left > maxX) maxX = c2.left;
		
		
		if(p1.top < minY) minY = p1.top;
		if(p1.top > maxY) maxY = p1.top;
		
		if(p2.top < minY) minY = p2.top;
		if(p2.top > maxY) maxY = p2.top;
		
		if(c1.top < minY) minY = c1.top;
		if(c1.top > maxY) maxY = c1.top;
		
		if(c2.top < minY) minY = c2.top;
		if(c2.top > maxY) maxY = c2.top;
		
		int width = Math.abs(minX - maxX)+lineWidth;
		int height = Math.abs(minY - maxY)+lineWidth;
//		int width = Math.abs(p1.left - p2.left);
//		int height = Math.abs(p1.top - p2.top);

//		int size = Math.max(width, height);
//		size = Math.max(size,Math.abs(p1.left - c1.left));
//		size = Math.max(size,Math.abs(p1.top - c1.top));
//		size = Math.max(size,Math.abs(p2.left - c2.left));
//		size = Math.max(size,Math.abs(p2.top - c2.top));

//		Point realStart = new Point(start.left - size, start.top - size);
		
		Point realStart = new Point(minX, minY);
		
		DOM.setElementAttribute(canvas, "left", Integer.toString(realStart.left)+"px");
		DOM.setElementAttribute(canvas, "top", Integer.toString(realStart.top)+"px");
		DOM.setStyleAttribute(canvas, "left", Integer.toString(realStart.left)+"px");
		DOM.setStyleAttribute(canvas, "top", Integer.toString(realStart.top)+"px");
		DOM.setElementAttribute(canvas, "width", Integer.toString(width)+"px");
		DOM.setElementAttribute(canvas, "height", Integer.toString(height)+"px");
		DOM.setStyleAttribute(canvas, "width", width+"px");
		DOM.setStyleAttribute(canvas, "height", height+"px");
//		DOM.setElementAttribute(canvas, "width", Integer.toString(width+size*2)+"px");
//		DOM.setElementAttribute(canvas, "height", Integer.toString(height+size*2)+"px");
//		DOM.setStyleAttribute(canvas, "width", Integer.toString(width+size*2)+"px");
//		DOM.setStyleAttribute(canvas, "height", Integer.toString(height+size*2)+"px");
		
		realStart = realStart.move(new Point(lineWidth/2,lineWidth/2).negative());
//		Logger.log("Width: "+DOM.getStyleAttribute(styledDiv, "width"));
		
		drawImpl(
				p1.move(realStart.negative()),
				p2.move(realStart.negative()),
				c1.move(realStart.negative()),
				c2.move(realStart.negative()),
				DOM.getStyleAttribute(styledDiv, "color"),
				DOM.getStyleAttribute(styledDiv, "width").replace("px", ""));
		
		// TODO lineWidth, maybe shadows etc.
	}

	private native void drawImpl(Point p1, Point p2, Point c1, Point c2, String color, String linewidth)/*-{
	  
	  	var canvas = this.@lasad.gwt.client.helper.common.bezier.BezierCurveCanvas::canvas;
		var ctx = canvas.getContext('2d');

	    ctx.beginPath();
	   	ctx.moveTo(p1.@lasad.gwt.client.helper.connection.data.Point::left,
	   			   p1.@lasad.gwt.client.helper.connection.data.Point::top);
	    ctx.strokeStyle = color;
	    ctx.lineWidth = linewidth;
	    ctx.bezierCurveTo(
	    	c1.@lasad.gwt.client.helper.connection.data.Point::left,
	    	c1.@lasad.gwt.client.helper.connection.data.Point::top,
	    	c2.@lasad.gwt.client.helper.connection.data.Point::left,
	    	c2.@lasad.gwt.client.helper.connection.data.Point::top,
	    	p2.@lasad.gwt.client.helper.connection.data.Point::left,
	   		p2.@lasad.gwt.client.helper.connection.data.Point::top);

	    ctx.stroke();

	}-*/;
	
	/**
	 * @see lasad.gwt.client.helper.common.bezier.BezierCurve#getElement()
	 */
	public Element getElement() {
		return canvas;
	}
	
	public Element getCurveStyleElement() {
		return styledDiv;
	}

//	private native static String getComputedStyle(Element element, String cssRule)/*-{
//		if( $doc.defaultView && $doc.defaultView.getComputedStyle ){
//			return $doc.defaultView.getComputedStyle( element, '' ).getPropertyValue(cssRule);
//		} else {
//		 	return null;
//		}
//	}-*/;
	
	/**
	 * Removes styledDiv from its parent.
	 * 
	 * @see lasad.gwt.client.helper.common.bezier.BezierCurve#remove()
	 */
	public void remove() {
		if(DOM.isOrHasChild(RootPanel.get().getElement(), styledDiv)){
			DOM.removeChild(RootPanel.get().getElement(), styledDiv);
		}	
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}