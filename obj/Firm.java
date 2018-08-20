package obj;

import util.*;
import java.util.*;

public class Firm implements Comparable<Firm> {
	private int firmID;
	private int[] traits; 
	private double performance; 
	private double strategy; 
	private double socialDistance; // tolerance for similarity (differences) to be considered in the reference group
	private boolean changeStrategy = false;

	/* 
	 * CONSTRUCTOR
	 */
	public Firm(int id) {
		firmID = id;
		// set traits and trait values
		traits = new int[Globals.getNumTraits()];
		for (int i = 0; i < traits.length; i++) {
			traits[i] = Globals.rand.nextInt(Globals.getNumTraitValues());
		}
		// set strategy
		newStrategy();
	}

	/*
	 * methods
	 */

	private void newStrategy() {
		strategy = Globals.rand.nextGaussian();	
		changeStrategy = false;
	}

	private void drawPerformance() {
		double eps = Globals.rand.nextGaussian();
		// System.out.println("eps: " + eps);

		performance = strategy + (Globals.getUncertainty() * eps);
	}

	private void makeDecision(Vector<Firm> market) {
		// look at all firms in the market and find reference group 
		// get average performance from reference group and set aspiration level
		// use  Log(P(change)/P(no change)) = -2.0 - (Yt-Lt)_Iyt>Lt - 0.25*(Yt- L)_It<Lt
		double socialAsp = 0.0d;
		int count = 0;
		for (Firm f : market) {
    		if (isNeighbor(f)) {
    			count++;
    			socialAsp += f.getPerformance();
    		}
		}
		// ***** what if count == 0 --> there are no neighbors?
		socialAsp = socialAsp / count;
		double slope = -1.0d;
		if (performance - socialAsp <= 0) {
			slope = -0.25d;
		}
		// double prob = Math.exp(-2.0d + slope*(performance - socialAsp)) / (1 + Math.exp(-2.0d + slope*(performance - socialAsp)));

		if (Globals.rand.nextDouble() < (Math.exp(-2.0d + slope*(performance - socialAsp)) / (1 + Math.exp(-2.0d + slope*(performance - socialAsp))))) {
			changeStrategy = true;	// so that we just set the firms to plan next move but don't actually change so that the actions are coordinated
		}

	}

	private void newStrategy() {
		strategy = Globals.rand.nextGaussian();
	}

	private boolean isNeighbor(Firm otherFirm) {
		int countSame = 0; 
		for (int i = 0; i < traits.length; i++) {
			if (traits[i] == otherFirm.getTraitValueAt(idx)) {
				countSame++;
			}
		}
		if (((double)countSame / (double)traits.length) > socialDistance) {
			System.out.println("Yes, Firm " + toString() + " is a neighbor of Firm " + otherFirm.toString());
			return true;
		} else {
			System.out.println("No, Firm " + toString() + " is NOT a neighbor of Firm " + otherFirm.toString());
			return false;
		}
	}

	/*
	 * GET/SETters
	 */
	public double getPerformance() {
		return performance; 
	}

	protected int getTraitValueAt(int idx) {
		try {
			int retInt = traits[idx];
		} catch (ArrayIndexOutOfBoundsException e) {
	    	System.err.println("ERROR: firm does not have trait " + idx);
	    	System.exit(0);			
		}
		return traits[idx];
	}
	/* 
	 * interface method
	 */
	public int compareTo(Firm compareFirm) {
		double comparePerformance = ((Firm)compareFirm).getPerformance(); 
		double thisPerformance = this.getPerformance();
		if(thisPerformance < comparePerformance) {
			return 1;
		} else if(comparePerformance < thisPerformance) {
			return -1;
		} else {
			return 0;
		}
	}	

	public String toString() {
		String retString = firmID + " - ["; 
		for (int i = 0; i < traits.length - 1; i++) {
			retString += traits[i] + ",";
		}
		retString += traits[traits.length - 1] + "] - " + strategy + "\t" + performance;
		return retString;
	}

	public static void main(String[] args) {
		Firm f = new Firm(10);
		f.drawPerformance();
		System.out.println(f.toString());

	}
}