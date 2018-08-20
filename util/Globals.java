package util; 

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class Globals {
	/* 
	 * Simulation utils
	 */
	public static PrintWriter out;
	public static long runID = System.currentTimeMillis(); 
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID); 
	private static int iterations = 3;
	private static String outfilename = "testing.txt";

	/*
	 * Model Parameters and Default values 
	 */

	private static double uncertainty = 0.5d;
	private static int numTraits = 5;
	private static int numTraitValues = 10;
	private static int numFirms = 10;

	/*
	 * GETters and SETters
	 */
	public static int getNumFirms() {
		return numFirms;
	}

	public void setNumFirms(int n) {
		numFirms = n;
	}
	public static double getUncertainty() {
		return uncertainty;
	}

	public static void setUncertainty(double d) {
		uncertainty = d;
	}

	public static int getNumTraits() {
		return numTraits;
	}

	public static void setNumTraits(int n) {
		numTraits = n;
	}

	public static int getNumTraitValues() {
		return numTraitValues;
	}

	public static void setNumTraitValues(int n) {
		numTraitValues = n;
	}

	public static void setIterations(int n) {
		iteration = n;
	}

	public static void setOutfile(String file) {
		outfilename = "out/" + file;
		try {
			if (outfilename.equals("")) {
				// System.out.println("setting STDOUT");
				out = new PrintWriter(System.out, true);
			} else {
				out = new PrintWriter(new FileOutputStream(outfilename, true), true);
			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}

	}

				if (p.getProperty("numTraits") != null) { Globals.setResourcesIncrement(Integer.parseInt(p.getProperty("numTraits"))); }
				if (p.getProperty("numTraitValues") != null) { Globals.setSearchScope(Integer.parseInt(p.getProperty("numTraitValues"))); }
				if (p.getProperty("outfile") != null) { Globals.setOutfile(p.getProperty("outfile")); }

}