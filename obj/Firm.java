package obj;

import util.*;
import java.util.*;

public class Firm implements Comparable<Firm> {
	private int id;
	private int type; // type index
	private int age; // how many periods a firm is alive

	// with referenceScope and comparisonTarget, firms determine how they set their aspiration levels -- e.g., global-top, global-avg, tier-top, tier-avg
	private String referenceScope; // either "global" or "tier"
	private String comparisonTarget; // either "top" or "avg"
	
	// private int typeString; // string representing the type of firm (in terms of socialDistance and traitsToChange)
	// private int firmID;
	// private double socialDistance; // tolerance for similarity (differences) to be considered in the reference group
	// private int traitsToChange;
	// private int[] traits; 
	private double performance; 
	private double strategy; 
	private int rankingTier; // ranking tier will simply be an integer from 0, 1, ... that represents different performance ranking tiers
	private int rank;
	private boolean newFirm; // need this so that new replacement firms can be assigned a rank and ranking tier

	// control property for syncing strategy change at industry level 
	private boolean changeStrategy = false;	

	/* 
	 * CONSTRUCTOR
	 */
	// public Firm(int idx, int aType, double aSocialDistance, int aNumTraitsToChange) {
	public Firm(int idx, int aType, String refScope, String compTarget) {

		id = idx;
		type = aType;
		referenceScope = refScope;
		comparisonTarget = compTarget;
		// firmID = id;
		// socialDistance = aSocialDistance;
		// traitsToChange = aNumTraitsToChange;

		// set initial traits and trait values
		// traits = new int[Globals.getNumTraits()];
		// for (int i = 0; i < traits.length; i++) {
		// 	traits[i] = Globals.rand.nextInt(Globals.getNumTraitValues());
		// }

		// draw initial strategy and performance 
		strategy = Globals.rand.nextGaussian();	
		performance = strategy + (Globals.getUncertainty() * Globals.rand.nextGaussian());
		newFirm = true;
	}

	/*
	 * methods
	 */

	private void newStrategy() {
		//// change traits 
		// determine traits to change 
		// boolean boolTraitsToChange[] = new boolean[traits.length];
		// int count = 0; 
		// while (count < traitsToChange) {
		// 	int changeTrait = Globals.rand.nextInt(traits.length);
		// 	if (!boolTraitsToChange[changeTrait]) {
		// 		boolTraitsToChange[changeTrait] = true;
		// 		count++;
		// 	}
		// }

		// for (int i = 0; i < traits.length; i++) {
		// 	if (boolTraitsToChange[i]) {
		// 		traits[i] = (traits[i] + Globals.rand.nextInt(Globals.getNumTraitValues() - 1) + 1) % Globals.getNumTraitValues();
		// 	}
		// }

		// draw new strategy value
		strategy = Globals.rand.nextGaussian();	
	}

	public void drawPerformance() { // at each time t, each firm draws a performance score based on its strategy (+ uncertainty)
		changeStrategy = false; // setting to false here as this is the first thing that happens in each time tick
		newFirm = false; // setting to false here 
		age++;
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
		double topPerformance = -100.0d;

		// The following calculates social aspiration as average of group 
		// depending on type (global vs. tier; top vs. average) we need to change this.
		for (Firm f : market) {
			if (id != f.getIdx()) { // exclude self
	    		if (isNeighbor(f)) {
	    			// System.out.println("NEIGHBOR:\t" + f.toString());
	    			count++;
	    			double perf = f.getPerformance(); 
	    			socialAsp += perf;
	    			if (perf > topPerformance) {
	    				topPerformance = perf;
	    			}
	    			// System.out.println("adding:\t" + perf);
	    		}
			}
		}
		
		if (comparisonTarget.equals("top")) {
			socialAsp = topPerformance;
		} else {  // comparisonTarget = "avg"
			// ***** what if count == 0 --> there are no neighbors?
			socialAsp = socialAsp / count;
		}
		
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
		if (referenceScope.equals("global")) {
			return true;
		} else { // referenceScope = "tier" 
			if (rankingTier == otherFirm.getRankingTier()) {
				return true;
			} else {
				return false;
			}
		}
		// int countSame = 0; 
		// for (int i = 0; i < traits.length; i++) {
		// 	if (traits[i] == otherFirm.getTraitValueAt(i)) {
		// 		countSame++;
		// 	}
		// }
		// if (((double)countSame / (double)traits.length) >= socialDistance) {
		// 	// System.out.println("Yes, Firm " + toString() + " is a neighbor of Firm " + otherFirm.toString());
		// 	return true;
		// } else {
		// 	// System.out.println("No, Firm " + toString() + " is NOT a neighbor of Firm " + otherFirm.toString());
		// 	return false;
		// }
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

	public int getRank() {
		return rank;
	}

	public int getRankingTier() {
		return rankingTier;
	}

	public void setRankAndTier(int aRank) {
		rank = aRank;
		rankingTier = Globals.getTierFromRank(aRank);
		System.out.println("setting rank for firm (" + id + ") with rank: " + aRank + ", ranking tier: " + rankingTier);
	}

	public double getPerformance() {
		return performance; 
	}

	public double getStrategy() {
		return strategy;
	}

	// protected int getTraitValueAt(int idx) {
	// 	try {
	// 		int retInt = traits[idx];
	// 	} catch (ArrayIndexOutOfBoundsException e) {
	//     	System.err.println("ERROR: firm does not have trait " + idx);
	//     	System.exit(0);			
	// 	}
	// 	return traits[idx];
	// }

	public boolean isNew() {
		return newFirm;
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
		// String retString = id + "\t" + type + "\t["; 
		// for (int i = 0; i < traits.length - 1; i++) {
		// 	retString += traits[i] + ",";
		// }
		// retString += traits[traits.length - 1] + "]\t" + strategy + "\t" + performance + "\t";
		String retString = id + "\t" + type + "\t" + referenceScope + "\t" + comparisonTarget + "\t" + strategy + "\t" + performance + "\t" + "\t" + age + "\t" + rankingTier + "\t" + rank + "\t";
		if (changeStrategy) { retString += "1"; } else { retString += "0"; }
		return retString;
	}

	/*
	 * Main for testing purposes only 
	 */
	public static void main(String[] args) {
		// Firm(int aType, int id, double aSocialDistance, int aNumTraitsToChange)	
		Firm f1 = new Firm(0, 0, "global", "top");
		Firm f2 = new Firm(1, 0, "tier", "avg");
		System.out.println(f1.toString());
		System.out.println(f2.toString());
		// if(f1.isNeighbor(f2)) {
		// 	System.out.println("TRUE");
		// }
	}
}