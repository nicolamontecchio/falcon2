package it.unipd.dei.ims.falcon2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Arrays;

public class Index {

	private class Recording {
		private int id;
		private RecordingAudioContent content;
		public Recording(int id, RecordingAudioContent content) {
			this.id = id;
			this.content = content;
		}
		public String toString() {
			return "" + id + " " + content.toString();
		}
		public Recording(String s) {
			s = s.trim();
			id = Integer.parseInt(s.substring(0, s.indexOf(" ")));
			content = new RecordingAudioContent(s.substring(s.indexOf("RECORDINGAUDIOCONTENT")));
		}
	}

	private LinkedList<Recording> recordings = null;

	/** Create an empty index */
	public Index() {
		recordings = new LinkedList();
	}
	
	/** Construct an index reading from an input stream. Each line is in the form "recordingId contentdump" */
	public Index(InputStream is) throws IOException {
		recordings = new LinkedList();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while(true) {
			String line = reader.readLine();
			if(line == null)
				break;
			if(!line.substring(0,1).equals("#") && line.length() > 0) {
				recordings.addLast(new Recording(line));
			}
		}
	}

	public void dumpToPlainText(OutputStream os) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
		for(Recording r : recordings) {
			writer.write(r.toString());
			writer.newLine();
		}
		writer.close();
	}

	/** Return id of the newly added recording. */
	public int addRecordingFromChromaStream(InputStream is, int quantizationLevels, 
			int segmentLength, int segmentHopsize) throws IOException {
		int newId = 0;
		if(!recordings.isEmpty())
			newId = recordings.peekLast().id + 1;
		RecordingAudioContent content = new RecordingAudioContent(
				AudioRepr.streamToQuantizedChromas(is,quantizationLevels), segmentLength, segmentHopsize);
		recordings.addLast(new Recording(newId, content));
		return newId;
	}

	/** Return id of most recently added document */
	public int getMostRecentId() {
		return recordings.peekLast().id;
	}

	public QueryResult[] query(RecordingAudioContent query) {
		QueryResult[] results = new QueryResult[recordings.size()];
		int i = 0;
		for(Recording r : recordings) 
			results[i++] = new QueryResult(r.id, query.similarityTo(r.content));
		Arrays.sort(results);
		return results;
	}

	public RecordingAudioContent getAudioContent(int recordingId) {
		// while it could be better, efficiency here is hardly a problem
		for(Recording r : recordings)
			if(r.id == recordingId)
				return r.content;
		return null;
	}

}
