package it.unipd.dei.ims.falcon2;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;

/**
 * Unit test for simple AudioRepr.
 */
public class AudioReprTest 
extends TestCase {
	public AudioReprTest( String testName )	{
		super( testName );
	}

	public static Test suite() {
		return new TestSuite( AudioReprTest.class );
	}

	public void testChromaToInteger()	{
		float[] c = {0.62289f,0.7082f,1f,0.66311f,0.47023f,0.58287f,0.37143f,0.65953f,0.67465f,0.50376f,0.5022f,0.76787f};
		assertTrue(AudioRepr.chromaToInteger(c, 3) == 2 + 11*12 + 1*12*12);
	}

	public void testSringToFloatArray() {
		String s = "0.62289,0.7082,1,0.66311,0.47023,0.58287,0.37143,0.65953,0.67465,0.50376,0.5022,0.76787";
		float[] f1 = {0.62289f,0.7082f,1f,0.66311f,0.47023f,0.58287f,0.37143f,0.65953f,0.67465f,0.50376f,0.5022f,0.76787f};
		float[] f2 = AudioRepr.stringToFloatArray(s);
		float err = 0;
		for(int i = 0; i < f1.length; i++) 
			err += (f2[i]-f1[i])*(f2[i]-f1[i]);
		assertTrue(err < 0.01 && f1.length == f2.length);
	}

	public void testStreamToQuantizedChromas() {
		File testFile = new File("/Users/nicolamontecchio/mir/data/rti_minichroma/18775.mp3.chroma");
		try {
			AudioRepr.streamToQuantizedChromas(new FileInputStream(testFile), 3);
			assertTrue(true);
		} catch(Exception e) {
			assertTrue(false); 
		}
	}
}
