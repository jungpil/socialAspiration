package util; 

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Globals {
	/* 
	 * General utils
	 */
	public static PrintWriter details;
	public static PrintWriter summary;
	public static long runID = System.currentTimeMillis(); 
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID); 
	private static String detailsFilename;
	private static String summaryFilename;

	/* 
	 * Simulation parameters
	 */
	private static int iterations = 500;
	// private static String experiment = "between"; // within or between; actuall we don't need between as we can set the cutoff to be very large 
	private static String replacement = "number"; // number or strategy
	private static int numFirmTypes = 1; // >1 if only experiment = "between"
	private static double replacementCutoff = 100; // 1.28;  // parameter c in Greve (2002) -- replace orgs with perf < meanPerf - c*stdPerf
												   // setting it to a really high number guarantees that there will not be any replacement

	/*
	 * Model Parameters and Default values 
	 */

	private static int numTraits = 10; // N
	private static int numTraitValues = 2; // K 
	private static int[] numFirms = new int[] {200, 200, 200, 200, 200};
	private static int numTotalFirms;
	private static double[] socialDistance = new double[] {0.5d, 0.6d, 0.7d, 0.8d, 0.9d};
	private static int[] traitsToChange = new int[] {1, 1, 1, 1, 1};
	private static double uncertainty = 0.5d;

	/*
	 * GETters and SETters
	 */

	public static int getNumFirmsOfType(int type) {
		return numFirms[type];
	}

	public static double getSocialDistanceForType(int idx) {
		return socialDistance[idx];
	}

	public static int getNumTraitsToChangeForType(int idx) {
		return traitsToChange[idx];
	}

	// public static String getExperiment() {
	// 	return experiment;
	// }
	
	// public static void setExperiment(String s) {
	// 	if (s.equals("within")) {
	// 		experiment = s;
	// 		setReplacementCutoff(100d);
	// 	} else if (s.equals("between")) {
	// 		experiment = s;
	// 	} else {
	//     	System.err.println("INVALID CONFIGURATION ERROR: experiment (" + s + ") must be either \"within\" or \"between\"");
	//     	System.exit(0);
	// 	}
	// }
	
	public static String getReplacement() {
		return replacement;
	}
	
	public static void setReplacement(String s) {
		if (s.equals("number") || s.equals("strategy")) {
			replacement = s;
		} else {
	    	System.err.println("INVALID CONFIGURATION ERROR: replacement (" + s + ") must be either \"number\" or \"strategy\"");
	    	System.exit(0);
		}
	}
	
	public static int getNumFirmTypes() {
		return numFirmTypes;
	}

	public static void setNumFirmTypes(String s) {
		try {
			numFirmTypes = Integer.parseInt(s);			
			if (numFirmTypes <= 0) {
		    	System.err.println("INVALID PARAMETER ERROR: numFirmTypes (" + numFirmTypes + ") must be at least 1!");
		    	System.exit(0);
			} else {
				numFirms = new int[numFirmTypes];
				socialDistance = new double[numFirmTypes];
				traitsToChange = new int[numFirmTypes];
			}
		} catch (NumberFormatException e) {
	    	System.err.println("INVALID PARAMETER ERROR: numFirmTypes (" + s + ") must be an integer (>0)!");
	    	System.exit(0);
		}
	}

	public static int getNumTotalFirms() {
		return numTotalFirms;
	}

	public static int getNumFirms(int idx) {
		return numFirms[idx];
	}

	public static void setNumFirms(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		if (numFirmTypes == st.countTokens()) {
			try {
				int type = 0;
				while (st.hasMoreElements()) {
					int num = Integer.parseInt(st.nextToken());
					if (num <= 0) {
				    	System.err.println("INVALID PARAMETER ERROR: numFirms (" + num + ") must be positive!");
				    	System.exit(0);
					} else {
						numFirms[type] = num;
						numTotalFirms += num;
						type++;
					}
				}
			} catch (NumberFormatException e) {
		    	System.err.println("INVALID PARAMETER ERROR: numFirms (" + s + ") must be an integer (>0)!");
		    	System.exit(0);
			}
		} else {
	    	System.err.println("INVALID PARAMETER ERROR: number of numFirms (" + st.countTokens() + ") must match numFirmTypes (" + numFirmTypes + ")!");
	    	System.exit(0);
		}
	}

	public static double getUncertainty() {
		return uncertainty;
	}

	public static void setUncertainty(String s) {
		try {
			uncertainty = Double.parseDouble(s);
			if (uncertainty < 0 || uncertainty > 1) {
				System.err.println("INVALID PARAMETER ERROR: uncertainty (" + s + ") must be between 0 and 1!");
		    	System.exit(0);
			}
		} catch (NumberFormatException e) {
			System.err.println("INVALID PARAMETER ERROR: uncertainty (" + s + ") must be a double (probability) between 0 and 1!");
	    	System.exit(0);
		}
		
	}

	public static double getReplacementCutoff() {
		return replacementCutoff;
	}

	public static void setReplacementCutoff(String s) {
		try {
			replacementCutoff = Double.parseDouble(s);
			if (replacementCutoff < 0) {
				System.err.println("INVALID PARAMETER ERROR: replacementCutoff (" + s + ") must be a positive double!");
		    	System.exit(0);
			}
		} catch (NumberFormatException e) {
			System.err.println("INVALID PARAMETER ERROR: replacementCutoff (" + s + ") must be a positive double!");
	    	System.exit(0);
		}
		
	}

	private static void setReplacementCutoff(double d) {
		replacementCutoff = d;
	}

	public static double getSocialDistance(int idx) {
		return socialDistance[idx];
	}

	public static void setSocialDistance(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		if (numFirmTypes == st.countTokens()) {
			try {
				int type = 0;
				while (st.hasMoreElements()) {
					double dist = Double.parseDouble(st.nextToken());
					if (dist < 0 || dist > 1) {
				    	System.err.println("INVALID PARAMETER ERROR: socialDistance (" + dist + ") must be between 0 and 1!");
				    	System.exit(0);
					} else {
						socialDistance[type] = dist;
						type++;
					}
				}
			} catch (NumberFormatException e) {
		    	System.err.println("INVALID PARAMETER ERROR: socialDistance (" + s + ") must be an double between 0 and 1!");
		    	System.exit(0);
			}
		} else {
	    	System.err.println("INVALID PARAMETER ERROR: number of socialDistance (" + st.countTokens() + ") must match numFirmTypes (" + numFirmTypes + ")!");
	    	System.exit(0);
		}
	}

	public static int getNumTraits() {
		return numTraits;
	}

	public static void setNumTraits(String s) {
		try {
			numTraits = Integer.parseInt(s);
			if (numTraits <= 0) {
		    	System.err.println("INVALID PARAMETER ERROR: numTraits (" + numTraits + ") must be positive!");
		    	System.exit(0);
			}
		} catch (NumberFormatException e) {
	    	System.err.println("INVALID PARAMETER ERROR: numTraits (" + s + ") must be an integer (>0)!");
	    	System.exit(0);
		}
	}

	public static int getNumTraitValues() {
		return numTraitValues;
	}

	public static void setNumTraitValues(String s) {
		try {
			numTraitValues = Integer.parseInt(s);
			if (numTraitValues < 2) {
		    	System.err.println("INVALID PARAMETER ERROR: numTraitValues (" + numTraitValues + ") must be positive and at least 2!");
		    	System.exit(0);
			}
		} catch (NumberFormatException e) {
	    	System.err.println("INVALID PARAMETER ERROR: numTraitValues (" + s + ") must be an integer (>0)!");
	    	System.exit(0);
		}
	}

	public static int getTraitsToChangeAt(int idx) {
		return traitsToChange[idx];
	}

	public static void setTraitsToChange(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		if (numFirmTypes == st.countTokens()) {
			try {
				int type = 0;
				while (st.hasMoreElements()) {
					int toChange = Integer.parseInt(st.nextToken());
					if (toChange <= 0 || toChange >= numTraits) {
				    	System.err.println("INVALID PARAMETER ERROR: traitsToChange (" + toChange + ") must be positive and less than " + numTraits + "!");
				    	System.exit(0);
					} else {
						traitsToChange[type] = toChange;
						type++;
					}
				}
			} catch (NumberFormatException e) {
		    	System.err.println("INVALID PARAMETER ERROR: traitsToChange (" + s + ") must be an integer (>0)!");
		    	System.exit(0);
			}
		} else {
	    	System.err.println("INVALID PARAMETER ERROR: number of traitsToChange (" + st.countTokens() + ") must match numFirmTypes (" + numFirmTypes + ")!");
	    	System.exit(0);
		}

	}

	public static int getIterations() {
		return iterations;
	}

	public static void setIterations(String s) {
		try {
			iterations = Integer.parseInt(s);
			if (iterations <= 0) {
		    	System.err.println("INVALID PARAMETER ERROR: iterations (" + iterations + ") must be positive!");
		    	System.exit(0);
			}
		} catch (NumberFormatException e) {
	    	System.err.println("INVALID PARAMETER ERROR: iterations (" + s + ") must be an integer (>0)!");
	    	System.exit(0);
		}
	}

	public static void setOutfile(String file) {
		try {
			if (file.equals("")) {
				// System.out.println("setting STDOUT");
				details = new PrintWriter(System.out, true);
				summary = new PrintWriter(new FileOutputStream("/dev/null"), true);
			} else {
				detailsFilename = "out/" + file + ".txt";
				summaryFilename = "out/" + file + "-summary.txt";
				details = new PrintWriter(new FileOutputStream(detailsFilename, true), true);
				summary = new PrintWriter(new FileOutputStream(summaryFilename, true), true);
			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}

	}

	public static void printParameters() {
		System.out.println("outfilename:\t" + detailsFilename);
		System.out.println("iterations:\t" + iterations);
		System.out.println("uncertainty:\t" + uncertainty);
		System.out.println("replacement:\t" + replacement);
		System.out.println("replacementCutoff:\t" + replacementCutoff);
		System.out.println("numFirmTypes:\t" + numFirmTypes);
		System.out.println("numTraits:\t" + numTraits);
		System.out.println("numTraitValues:\t" + numTraitValues);
		System.out.println("numTotalFirms:\t" + numTotalFirms);
		System.out.print("numFirms:\t");
			for (int i = 0; i < numFirms.length - 1; i++) { System.out.print(numFirms[i] + ","); }
			System.out.println(numFirms[numFirms.length - 1]);
		System.out.print("socialDistance:\t");
			for (int i = 0; i < socialDistance.length - 1; i++) { System.out.print(socialDistance[i] + ","); }
			System.out.println(socialDistance[socialDistance.length - 1]);
		System.out.print("traitsToChange:\t");
			for (int i = 0; i < traitsToChange.length - 1; i++) { System.out.print(traitsToChange[i] + ","); }
			System.out.println(traitsToChange[traitsToChange.length - 1]);
	}

}