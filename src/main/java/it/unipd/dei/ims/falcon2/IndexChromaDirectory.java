package it.unipd.dei.ims.falcon2;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class IndexChromaDirectory {
	public static void main( String[] args ) throws IOException {
		Index index = new Index();
		for(File f : new File(args[0]).listFiles()) {
			int rId = index.addRecordingFromChromaStream(new FileInputStream(f), 3, 500, 250);
			System.out.println(String.format("#%10d: %s", rId, f.getName()));
		}
		index.dumpToPlainText(System.out);
	}
}
