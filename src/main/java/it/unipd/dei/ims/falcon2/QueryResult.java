package it.unipd.dei.ims.falcon2;

public class QueryResult implements Comparable<QueryResult> {
	private int recordingID;
	private float score;
	public QueryResult(int id, float s) {
		recordingID = id;
		score = s;
	}
	public int compareTo(QueryResult b) {
		QueryResult a = this;
		if(a.score > b.score)
			return -1;
		if(b.score > a.score)
			return 1;
		return 0;
	}
	public String toString() {
		return String.format("%10d : %10.8f", recordingID, score);
	}
}
