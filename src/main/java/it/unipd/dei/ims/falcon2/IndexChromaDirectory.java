package it.unipd.dei.ims.falcon2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.cli.*;

public class IndexChromaDirectory {

	public static void main(String[] args) throws IOException, ParseException {

		Options options = new Options();
		options.addOption("help", false, "print this help");
		options.addOption("q", true, "quantization levels");
		options.addOption("l", true, "segment length");
		options.addOption("h", true, "segment hopsize");
		options.addOption("e", true, "chroma file extension");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		int quantizationLevels = Integer.parseInt(cmd.getOptionValue("q", "3"));
		int segmentLength = Integer.parseInt(cmd.getOptionValue("l", "500"));
		int segmentHopSize = Integer.parseInt(cmd.getOptionValue("h", "500"));
		String extension = cmd.getOptionValue("e", null);
		args = cmd.getArgs();

		if (args.length != 1) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("IndexChromaDirectory [options] directory", options);
			return;
		}

		Index index = new Index();
		for (File f : new File(args[0]).listFiles()) {
			if (extension == null || f.getName().substring(f.getName().lastIndexOf(".")).equals(extension)) {
				int rId = index.addRecordingFromChromaStream(new FileInputStream(f), quantizationLevels, segmentLength, segmentHopSize);
				System.out.println(String.format("#%10d: %s", rId, f.getName()));
			}
		}
		index.dumpToPlainText(System.out);
	}
}
