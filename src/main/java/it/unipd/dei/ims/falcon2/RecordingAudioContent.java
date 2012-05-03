package it.unipd.dei.ims.falcon2;
import java.util.LinkedList;
import java.util.TreeMap;
import java.io.OutputStream;

/** Represents a song audio content, as a linked-list of chroma segments. */
public class RecordingAudioContent {

	private class Segment {
		int[] chromaValues;
		int[] chromaCounts; 
		int length; // note: this is NOT chromaValues.length, it is sum(chromaCounts)

		/** construct a segment starting from a (partial) integer array */
		public Segment(int[] chromas, int from, int to) {
			TreeMap<Integer,Integer> m = new TreeMap<Integer,Integer>();
			for(int i = from; i < to; i++) {
				int c = chromas[i];
				if(!m.containsKey(c))
					m.put(c,0);
				int v = m.get(c);
				m.put(c,v+1);
			}
			length = to-from;
			chromaValues = new int[m.size()];
			chromaCounts = new int[m.size()];
			int i = 0;
			while(!m.isEmpty()) {
				int k = m.firstKey();
				int v = m.remove(k);
				chromaValues[i] = k;
				chromaCounts[i] = v;
				i++;
			}
		}

		/** Construct a segment starting from its string representation. No format checks are performed. */
		public Segment(String s) {
			String[] ss = s.trim().split(" ");
			length = 0;
			chromaValues = new int[ss.length-2];
			chromaCounts = new int[ss.length-2];
			for(int i = 1; i < ss.length-1; i++) {
				String[] kv = ss[i].trim().split(":");
				chromaValues[i-1] = Integer.parseInt(kv[0]);
				chromaCounts[i-1] = Integer.parseInt(kv[1]);
				length += chromaCounts[i-1];
			}
		}

		/** Local similarity, between two segments */
		public float similarityTo(Segment b) {
			Segment a = this;
			int i = 0;   // on segment a
			int j = 0;   // on segment b
			float aL = (float) a.length;
			float bL = (float) b.length;
			float similarity = 0;
			while(i < a.chromaValues.length && j < b.chromaValues.length) {
				if(a.chromaValues[i] < b.chromaValues[j])
					i++;
				else if(a.chromaValues[i] > b.chromaValues[j])
					j++;
				else 
					similarity += Math.min(a.chromaCounts[i++]/aL, b.chromaCounts[j++]/bL);
			}
			return similarity;
		}

		/** representation: "SEGMENT k1:v1 k2:v2 --- kn:vn END" */
		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append("SEGMENT ");
			for(int i = 0; i < chromaValues.length; i++) 
				s.append(String.format("%d:%d ", chromaValues[i], chromaCounts[i]));
			s.append("END");
			return s.toString();
		}

	}

	private LinkedList<Segment> segments = null;

	/** Construct a representation of audio content as a sequence of overlapping segments */
	public RecordingAudioContent(int[] chromas, int segmentLength, int segmentHopsize) {
		segments = new LinkedList<Segment>();
		int from = 0;
		while(from < chromas.length) {
			int to = from + segmentLength;
			if(to > chromas.length)
				to = chromas.length;
			segments.addLast(new Segment(chromas, from, to));
			from += segmentHopsize;
		}
	}

	/** Load the representation of an audio recording from a string. Perform no format checks. */
	public RecordingAudioContent(String s) {
		segments = new LinkedList<Segment>();
		int from = 0;
		while(s.indexOf("SEGMENT", from) >= 0) {
			from = s.indexOf("SEGMENT", from);
			int to = s.indexOf("END", from) + 3;
			//System.out.println(s.substring(from,to));

			segments.addLast(new Segment(s.substring(from, to)));
			from = to;
		}
	}

	/** compute (asymmetrical) similarity between recordings */
	public float similarityTo(RecordingAudioContent b) {
		RecordingAudioContent a = this;
		float similarity = 1;
		for(Segment as : a.segments) {
			float s = 0;
			for(Segment bs : b.segments)
				s = Math.max(s, as.similarityTo(bs));
			similarity *= s;
		}
		similarity = (float) Math.pow(similarity, 1./a.segments.size());
		return similarity;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("RECORDINGAUDIOCONTENT ");
		for(Segment seg : segments)
			s.append(seg.toString() + " ");
		s.append("END");
		return s.toString();
	}

}
