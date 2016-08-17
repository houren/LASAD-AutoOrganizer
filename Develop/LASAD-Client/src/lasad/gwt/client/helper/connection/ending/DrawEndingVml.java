package lasad.gwt.client.helper.connection.ending;

import lasad.gwt.client.helper.connection.data.Point;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 *  Where is the documentation?  What is a VML?
 */

public class DrawEndingVml extends DrawEnding {

	private Element div = DOM.createDiv();

	// private Element vmlGroup;

	private Element vmlPath;
	private Element vmlShape;

	private Element styledDiv;

	private boolean appendedToParent = false; // hack :(

	{
		// vmlGroup = DOM.createElement("g_vml_:group");
		vmlShape = DOM.createElement("g_vml_:shape");
		vmlPath = DOM.createElement("g_vml_:path");
		// DOM.appendChild(vmlShape, vmlPath); // hack :(

		// DOM.appendChild(vmlGroup, vmlPath);

		// DOM.setElementAttribute(vmlGroup, "class", "gwt-diagrams-vml-group");
		DOM.setElementAttribute(vmlShape, "class", "gwt-diagrams-vml-shape");
		// DOM.appendChild(RootPanel.get().getElement(), vmlGroup); // hack :(

		// DOM.appendChild(RootPanel.get().getElement(), styledDiv); // hack :(

		// #13 Bezier/straight connections doesn't work on IE6.0
		// DOM.setStyleAttribute(styledDiv, "display", "none");

		// DOM.setElementProperty(styledDiv, "className",
		// "gwt-diagrams-vml-shape");

		// DOM.setElementAttribute(vmlPath, "strokecolor",
		// getComputedStyle(styledDiv, "color"));
		DOM.setElementAttribute(vmlShape, "strokecolor", "black");
		// DOM.setElementAttribute(vmlPath, "fillcolor",
		// getComputedStyle(styledDiv, "color"));
		DOM.setElementAttribute(vmlShape, "fillcolor", "black");
		// DOM.setElementAttribute(vmlPath, "strokeweight",
		// getComputedStyle(styledDiv, "width"));
		DOM.setElementAttribute(vmlShape, "strokeweight", "1");
		DOM.setElementAttribute(vmlShape, "filled", "true");

		DOM.setStyleAttribute(vmlShape, "width", "100px");
		DOM.setStyleAttribute(vmlShape, "height", "100px");
		DOM.setStyleAttribute(vmlShape, "position", "absolute");
		DOM.setElementAttribute(vmlShape, "coordsize", "100,100");

	}

	/**
	 * Private constructor. Do not instantiate directly @see BezierCurve
	 */
	private DrawEndingVml() {
	// Do not instantiate directly
	}

	/**
	 * @see lasad.gwt.client.helper.common.bezier.BezierCurve#getElement()
	 */
	public Element getElement() {
		if (appendedToParent) {
			return vmlShape;
		} else {
			return div;
		}
	}

	/**
	 * Removes styledDiv from its parent.
	 * 
	 * @see lasad.gwt.client.helper.common.bezier.BezierCurve#remove()
	 */
	public void remove() {
		// DOM.removeChild(RootPanel.get().getElement(), styledDiv);
		DOM.removeChild((Element) this.getElement().getParentElement(), this.getElement());

	}

	private native boolean initDocument()/*-{
		if (!$doc.namespaces["g_vml_"]) {
		$doc.namespaces.add("g_vml_", "urn:schemas-microsoft-com:vml");
		}

		var ss = $doc.createStyleSheet();
		ss.cssText = "g_vml_\\:*{behavior:url(#default#VML)}";

		return true;
	}-*/;

	// private native static String getComputedStyle(Element element, String
	// cssRule)/*-{
	// if ( element.currentStyle ) {
	// document.title=element.currentStyle[ cssRule ];
	// return element.currentStyle[ cssRule ];
	// } else {
	// return null;
	// }
	// }-*/;

	public Element getCurveStyleElement() {
		return styledDiv;
	}

	public void setCurveStyleElement(Element style) {
		if (styledDiv == null) {
			styledDiv = style;
		}
	}

	public void update(int left, int top, float angle) {
		
		if (angle < 0.0f || angle >= 360.0f) {
			throw new IllegalArgumentException("Angle must from [0.0f, 360.0f)");
		}
		if (!appendedToParent) {
			// DOM.appendChild(DOM.getParent(div), vmlGroup);
			DOM.appendChild(DOM.getParent(div), vmlShape);
			DOM.appendChild(vmlShape, vmlPath);
			DOM.removeChild(DOM.getParent(div), div);
			div = null;
			appendedToParent = true;
		}

		Element parent = DOM.getParent(getElement());
		int startParentLeft = Integer.parseInt(DOM.getStyleAttribute(parent, "left").replace("px", ""));
		int startParentTop = Integer.parseInt(DOM.getStyleAttribute(parent, "top").replace("px", ""));

		int arrowheadWidth = 6;
		int arrowheadHeight = 8;

		int startLeft = left - startParentLeft;
		int startTop = top - startParentTop;

		if (angle > 315.0f || angle < 45.0f) {
			// UP
			startLeft = startLeft - arrowheadWidth / 2;

		} else if (angle > 45.0f && angle < 135.0f) {
			// RIGHT
			startLeft = startLeft - arrowheadHeight;
			startTop = startTop - arrowheadWidth / 2;
		} else if (angle > 135.0f && angle < 225.0f) {
			// DOWN
			startLeft = startLeft - arrowheadWidth / 2;
			startTop = startTop - arrowheadHeight;
		} else {
			// LEFT
			startTop = startTop - arrowheadWidth / 2;
		}

		if (!styledDiv.equals(null)) {
			DOM.setElementAttribute(vmlShape, "strokecolor", DOM.getStyleAttribute(styledDiv, "color"));
			DOM.setElementAttribute(vmlShape, "fillcolor", DOM.getStyleAttribute(styledDiv, "color"));
			DOM.setElementAttribute(vmlShape, "strokeweight", DOM.getStyleAttribute(styledDiv, "width"));
		} else {
			DOM.setElementAttribute(vmlShape, "strokecolor", "black");
			DOM.setElementAttribute(vmlShape, "fillcolor", "black");
			DOM.setElementAttribute(vmlShape, "strokeweight", "1");

		}

		// DOM.setElementAttribute(vmlPath, "strokecolor",
		// getComputedStyle(styledDiv, "color"));
		// DOM.setElementAttribute(vmlShape, "strokecolor",
		// DOM.getStyleAttribute(styledDiv, "color"));
		// DOM.setElementAttribute(vmlShape, "fillcolor",
		// DOM.getStyleAttribute(styledDiv, "color"));
		// DOM.setElementAttribute(vmlShape, "strokecolor", "black");
		// DOM.setElementAttribute(vmlShape, "fillcolor", "black");

		// DOM.setElementAttribute(vmlPath, "strokeweight",
		// DOM.getStyleAttribute(styledDiv, "width"));
		// DOM.setElementAttribute(vmlShape, "strokeweight", "1");
		// DOM.setElementAttribute(vmlPath, "strokecolor",
		// getComputedStyle(vmlPath, "color"));
		// DOM.setElementAttribute(vmlPath, "strokeweight",
		// getComputedStyle(vmlPath, "width"));

		// DOM.setStyleAttribute(vmlGroup, "left", Integer.toString(startLeft));
		// DOM.setStyleAttribute(vmlGroup, "top", Integer.toString(startTop));
		// DOM.setStyleAttribute(vmlPath, "left", Integer.toString(startLeft));
		// DOM.setStyleAttribute(vmlPath, "top", Integer.toString(startTop));
		// DOM.setStyleAttribute(vmlShape, "left",
		// Integer.toString(-arrowheadWidth));
		// DOM.setStyleAttribute(vmlShape, "top",
		// Integer.toString(-arrowheadHeight));

		Point p1, p2, p3;

		if (angle > 315.0f || angle < 45.0f) {
			// UP
			p1 = new Point(startLeft + arrowheadWidth / 2, startTop);
			p2 = new Point(startLeft, startTop + arrowheadHeight);
			p3 = new Point(startLeft + arrowheadWidth, startTop + arrowheadHeight);

			String path = "m " + String.valueOf(p1.getLeft()) + "," + String.valueOf(p1.getTop()) + " l " + String.valueOf(p2.getLeft()) + "," + String.valueOf(p2.getTop()) + " l " + String.valueOf(p3.getLeft()) + "," + String.valueOf(p3.getTop()) + " x e";
			DOM.setElementAttribute(vmlPath, "v", path);
		} else if (angle > 45.0f && angle < 135.0f) {
			// RIGHT
			p1 = new Point(startLeft + arrowheadHeight, startTop + arrowheadWidth / 2);
			p2 = new Point(startLeft, startTop);
			p3 = new Point(startLeft, startTop + arrowheadWidth);

			String path = "m " + String.valueOf(p1.getLeft()) + "," + String.valueOf(p1.getTop()) + " l " + String.valueOf(p2.getLeft()) + "," + String.valueOf(p2.getTop()) + " l " + String.valueOf(p3.getLeft()) + "," + String.valueOf(p3.getTop()) + " x e";
			DOM.setElementAttribute(vmlPath, "v", path);
		} else if (angle > 135.0f && angle < 225.0f) {
			// DOWN
			p1 = new Point(startLeft + arrowheadWidth / 2, startTop + arrowheadHeight);
			p2 = new Point(startLeft + arrowheadWidth, startTop);
			p3 = new Point(startLeft, startTop);

			String path = "m " + String.valueOf(p1.getLeft()) + "," + String.valueOf(p1.getTop()) + " l " + String.valueOf(p2.getLeft()) + "," + String.valueOf(p2.getTop()) + " l " + String.valueOf(p3.getLeft()) + "," + String.valueOf(p3.getTop()) + " x e";
			DOM.setElementAttribute(vmlPath, "v", path);
		} else {
			// LEFT
			p1 = new Point(startLeft, startTop + arrowheadWidth / 2);
			p2 = new Point(startLeft + arrowheadHeight, startTop + arrowheadWidth);
			p3 = new Point(startLeft + arrowheadHeight, startTop);

			String path = "m " + String.valueOf(p1.getLeft()) + "," + String.valueOf(p1.getTop()) + " l " + String.valueOf(p2.getLeft()) + "," + String.valueOf(p2.getTop()) + " l " + String.valueOf(p3.getLeft()) + "," + String.valueOf(p3.getTop()) + " x e";
			DOM.setElementAttribute(vmlPath, "v", path);
		}

	}

}
