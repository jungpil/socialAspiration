package obj;

import util.*;
import java.util.*;

public class Firm implements Comparable<Firm> {
	private int id;
	private int type; // type index
	private int typeString; // string representing the type of firm (in terms of socialDistance and traitsToChange)
	// private int firmID;
	private double socialDistance; // tolerance for similarity (differences) to be considered in the reference group
	private int traitsToChange;
	private int[] traits; 
	private double performance; 
	private double strategy; 

	// control property for syncing strategy change at industry level 
	private boolean changeStrategy = false;	

	/* 
	 * CONSTRUCTOR
	 */

	public Firm(int idx, int aType, double aSocialDistance, int aNumTraitsToChange) {
		id = idx;
		type = aType;
		// firmID = id;
		socialDistance = aSocialDistance;
		traitsToChange = aNumTraitsToChange;

		// set initial traits and trait values
		traits = new int[Globals.getNumTraits()];
		for (int i = 0; i < traits.length; i++) {
			traits[i] = Globals.rand.nextInt(Globals.getNumTraitValues());
		}
		strategy = Globals.rand.nextGaussian();	
		performance = strategy + (Globals.getUncertainty() * Globals.rand.nextGaussian());
	}

	/*
	 * methods
	 */

	private void newStrategy() {
		// change traits 
		// determine traits to change 
		boolean boolTraitsToChange[] = new boolean[traits.length];
		int count = 0; 
		while (count < traitsToChange) {
			int changeTrait = Globals.rand.nextInt(traits.length);
			if (!boolTraitsToChange[changeTrait]) {
				boolTraitsToChange[changeTrait] = true;
				count++;
			}
		}

		for (int i = 0; i < traits.length; i++) {
			if (boolTraitsToChange[i]) {
				traits[i] = (traits[i] + Globals.rand.nextInt(Globals.getNumTraitValues() - 1) + 1) % Globals.getNumTraitValues();
			}
		}

		// draw new strategy value
		strategy = Globals.rand.nextGaussian();	
	}

	public void drawPerformance() { // at each time t, each firm draws a performance score based on its strategy (+ uncertainty)
		changeStrategy = false; // setting to false here as this is the first thing that happens in each time tick
		double eps = Globals.rand.nextGaussian();
		// System.out.println("eps: " + eps);

		performance = strategy + (Globals.getUncertainty() * eps);
	}

	public void makeDecision(Vector<Firm> market) {
		// look at all firms in the market and find reference group 
		// get average performance from reference group and set aspiration level
		// use  Log(P(change)/P(no change)) = -2.0 - (Yt-Lt)_Iyt>Lt - 0.25*(Yt- L)_It<Lt
		double socialAsp = 0.0d;
		int count = 0;
		// System.out.println("SELF:\t" + toString());
		for (Firm f : market) {
			if (id != f.getIdx()) { // exclude self
	    		if (isNeighbor(f)) {
	    			// System.out.println("NEIGHBOR:\t" + f.toString());
	    			count++;
	    			double perf = f.getPerformance(); 
	    			socialAsp += perf;
	    			// System.out.println("adding:\t" + perf);
	    		}
			}
		}
		// ***** what if count == 0 --> there are no neighbors?
		socialAsp = socialAsp / count;
		double slope = -1.0d;
		if (performance - socialAsp <= 0) {
			slope = -0.25d;
		}
		// System.out.println("SocialAsp:\t" + socialAsp);
		// System.out.println("numNeightbors:\t" + count);
		// double prob = Math.exp(-2.0d + slope*(performance - socialAsp)) / (1 + Math.exp(-2.0d + slope*(performance - socialAsp)));

		if (Globals.rand.nextDouble() < (Math.exp(-2.0d + slope*(performance - socialAsp)) / (1 + Math.exp(-2.0d + slope*(performance - socialAsp))))) {
			changeStrategy = true;	// so that we just set the firms to plan next move but don't actually change so that the actions are coordinated
		}

	}

	public void implementDecision() {
		// this is called by all firms (from Simulation) during iteration, so need to check if firm should change strategy
		if (changeStrategy) {
			newStrategy();
		}
	}

	private boolean isNeighbor(Firm otherFirm) {
		int countSame = 0; 
		for (int i = 0; i < traits.length; i++) {
			if (traits[i] == otherFirm.getTraitValueAt(i)) {
				countSame++;
			}
		}
		if (((double)countSame / (double)traits.length) >= socialDistance) {
			// System.out.println("Yes, Firm " + toString() + " is a neighbor of Firm " + otherFirm.toString());
			return true;
		} else {
			// System.out.println("No, Firm " + toString() + " is NOT a neighbor of Firm " + otherFirm.toString());
			return false;
		}
	}

	/*
	 * GET/SETters
	 */
	public int getIdx() {
		return id;
	}

	public int getType() {
		return type;
	}

	// public int getID() {
	// 	return firmID;
	// }

	public double getSocialDistance() {
		return socialDistance;
	}

	public int getNumTraitsToChange() {
		return traitsToChange;
	}
	public double getPerformance() {
		return performance; 
	}

	public double getStrategy() {
		return strategy;
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
	 * interface method; implements Comparable
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

	/*
	 * Printer
	 */
	public String toString() {
		String retString = id + "\t" + type + "\t["; 
		for (int i = 0; i < traits.length - 1; i++) {
			retString += traits[i] + ",";
		}
		retString += traits[traits.length - 1] + "]\t" + strategy + "\t" + performance + "\t";
		if (changeStrategy) { retString += "1"; } else { retString += "0"; }
		return retString;
	}

	/*
	 * Main for testing purposes only 
	 */
	public static void main(String[] args) {
		// Firm(int aType, int id, double aSocialDistance, int aNumTraitsToChange)	
		Firm f1 = new Firm(0, 0, 0.2, 2);
		Firm f2 = new Firm(1, 0, 0.2, 2);
		System.out.println(f1.toString());
		System.out.println(f2.toString());
		if(f1.isNeighbor(f2)) {
			System.out.println("TRUE");
		}
	}
}