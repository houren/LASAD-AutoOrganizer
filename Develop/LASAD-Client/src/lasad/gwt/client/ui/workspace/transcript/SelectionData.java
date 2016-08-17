package lasad.gwt.client.ui.workspace.transcript;

public class SelectionData {

	private int StartRow, EndRow, StartPoint, EndPoint;

	public SelectionData(int sR, int sP, int eR, int eP) {
		// Switch Start/End Values, if bottom to top selection
		if (sR > eR) {
			StartRow = eR;
			EndRow = sR;
			StartPoint = eP;
			EndPoint = sP;
		} else {
			StartRow = sR;
			EndRow = eR;
			StartPoint = sP;
			EndPoint = eP;
		}
	}

	public int getStartRow() {
		return StartRow;
	}

	public int getEndRow() {
		return EndRow;
	}

	public int getStartPoint() {
		return StartPoint;
	}

	public int getEndPoint() {
		return EndPoint;
	}

	public void setStartRow(int startRow) {
		StartRow = startRow;
	}

	public void setEndRow(int endRow) {
		EndRow = endRow;
	}

	public void setStartPoint(int startPoint) {
		StartPoint = startPoint;
	}

	public void setEndPoint(int endPoint) {
		EndPoint = endPoint;
	}
}