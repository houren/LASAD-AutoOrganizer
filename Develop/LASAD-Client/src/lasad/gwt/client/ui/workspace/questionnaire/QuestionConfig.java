package lasad.gwt.client.ui.workspace.questionnaire;

public class QuestionConfig {

	private String id;
	private String qid;
	private String type = "text";
	private int minScore;
	private int maxScore;
	private String minScoreLabel;
	private String maxScoreLabel;

	private String question;

	public QuestionConfig(String id, String qid, String question) {
		this.id = id;
		this.qid = qid;
		this.question = question;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMinScore() {
		return minScore;
	}

	public void setMinScore(int minScore) {
		this.minScore = minScore;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public String getMinScoreLabel() {
		return minScoreLabel;
	}

	public void setMinScoreLabel(String minScoreLabel) {
		this.minScoreLabel = minScoreLabel;
	}

	public String getMaxScoreLabel() {
		return maxScoreLabel;
	}

	public void setMaxScoreLabel(String maxScoreLabel) {
		this.maxScoreLabel = maxScoreLabel;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

}
