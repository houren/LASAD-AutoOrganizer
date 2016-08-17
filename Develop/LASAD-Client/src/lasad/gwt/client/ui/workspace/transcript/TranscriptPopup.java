//package lasad.gwt.client.ui.workspace.transcript;
//
//import com.extjs.gxt.ui.client.widget.BoxComponent;
//import com.extjs.gxt.ui.client.widget.ContentPanel;
//import com.extjs.gxt.ui.client.widget.Html;
//import com.google.gwt.user.client.DOM;
//import com.google.gwt.user.client.Element;
//
//public class TranscriptPopup extends ContentPanel {
//
//	private TextPassage tPassage = new TextPassage();
//	private TranscriptLinkData tData = null;
//
//	public TranscriptPopup() {
//
//		this.setWidth("150px");
//		this.setStyleName("transcript-popup");
//		this.setBodyBorder(false);
//		this.setHeaderVisible(false);
//		this.setBorders(false);
//		this.setVisible(false);
//
//		Html title = new Html();
//		title.setHtml("Transcriptlink:");
//		title.setStyleName("transcript-popup-title");
//		this.add(title);
//		this.add(tPassage);
//	}
//
//	public void setTData(TranscriptLinkData tData) {
//		this.tData = tData;
//	}
//
//	public void show() {
//		if (tData != null) {
//			// Update TextPassage
//			this.tPassage.updateData(tData);
//
//			super.show();
//
//		}
//	}
//
//	class TextPassage extends BoxComponent {
//
//		private TranscriptLinkData tData = null;
//
//		private Element domSpan;
//
//		protected void onRender(Element target, int index) {
//
//			super.onRender(target, index);
//			Element outerDiv = DOM.createDiv();
//			// Create the needed DOM Element for the Text
//			Element element = DOM.createDiv();
//			DOM.appendChild(outerDiv, element);
//			element.setInnerText("");
//			domSpan = element;
//
//			setElement(outerDiv, target, index);
//		}
//
//		protected void afterRender() {
//			super.afterRender();
//
//			domSpan.setClassName("transcript-popup-span-small");
//			this.getElement().setClassName("transcript-popup-span-small");
//		}
//
//		public void updateData(TranscriptLinkData tData) {
//			// DOM.setStyleAttribute(domSpan, "backgroundColor",
//			// tData.getBgColor());
//			// DOM.setStyleAttribute(domSpan,"borderColor", tData.getBgColor());
//			domSpan.setInnerText(tData.getText());
//
//		}
//
//	}
//
//}
