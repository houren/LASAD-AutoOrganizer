package lasad.gwt.client.ui.common.helper;

import com.google.gwt.user.client.Element;

public class TextSelection {
	public static native void disableTextSelection(Element element)/*-{
		element.onselectstart = function() {
			return false;
		};
		element.unselectable = "on";
		element.style.MozUserSelect = "none";
		element.style.cursor = "default";
	}-*/;
}
