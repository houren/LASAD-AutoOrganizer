package lasad.gwt.client.ui.workspace.chat;

public class ChatMessage {

	private String timestamp;
	private String author;
	private String sentenceOpener;
	private String sentenceOpenerTextColor;
	private String message;
	
	public ChatMessage(String timestamp, String author, String message) {
		this.timestamp = timestamp;
		this.author = author;
		this.sentenceOpener = null;
		this.sentenceOpenerTextColor = null;
		this.message = message;
	}
	
	public ChatMessage(String timestamp, String author, String sentenceOpener, String sentenceOpenerTextColor, String message) {
		this.timestamp = timestamp;
		this.author = author;
		this.sentenceOpener = sentenceOpener;
		this.sentenceOpenerTextColor = sentenceOpenerTextColor;
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSentenceOpener() {
		return sentenceOpener;
	}

	public void setSentenceOpener(String sentenceOpener) {
		this.sentenceOpener = sentenceOpener;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSentenceOpenerTextColor() {
		return sentenceOpenerTextColor;
	}

	public void setSentenceOpenerTextColor(String sentenceOpenerTextColor) {
		this.sentenceOpenerTextColor = sentenceOpenerTextColor;
	}
	
	
}
