package it.unipd.dei.ims.falcon2;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;


class AudioRepr {
	
	private static int argmax(float[] x) {
		int m = 0;
		for(int i = 1; i < x.length; i++)
			if(x[i] > x[m])
				m = i;
		return m;
	}

	public static int chromaToInteger(float[] chroma, int quantizationLevels) {
		float[] cc = new float[chroma.length];
		for(int i = 0; i < cc.length; i++)
			cc[i] = chroma[i];
		int q = 0;
		for(int i = 0; i < quantizationLevels; i++) {
			int am = argmax(cc);
			cc[am] = 0;
			for(int j = 0; j < i; j++)
				am *= 12;
			q += am;
		}		
		return q;
	}

	public static float[] stringToFloatArray(String s) {
		String[] ss = s.trim().split(",");
		float[] ff = new float[ss.length];
		for(int i = 0; i < ff.length; i++)
			ff[i] = Float.parseFloat(ss[i]);
		return ff;
	}

	public static int[] streamToQuantizedChromas(InputStream in, int quantizationLevels) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		LinkedList<Integer> ll = new LinkedList<Integer>();
		while(true) {
			String line = reader.readLine();
			if(line == null)
				break;
			ll.addLast(chromaToInteger(stringToFloatArray(line),quantizationLevels));
		}
		int[] qc = new int[ll.size()];
		for(int i = 0; i < qc.length; i++) 
			qc[i] = ll.removeFirst();
		return qc;
	}

}
