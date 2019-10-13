package app;

import java.util.*;
import obj.*;
import util.*;

/*
 * Main Application Class
 */
public class Simulation {
	private static Vector<Firm> firms; 
	private static int[] firmTypeCounts; // simple array; types start at 0 so array index = type
	private static int firmCounter = 0;

	public static void main(String[] args) {
		// load experimental setup
		FileIO.loadParameters(args[0]);
		Globals.printParameters();
		
		// setup ecosystem
		firmTypeCounts = new int[Globals.getNumFirmTypes()];
		firms = new Vector<Firm>();
		for (int i = 0; i < Globals.getNumFirmTypes(); i++) {
			for (int j = 0; j < Globals.getNumFirmsOfType(i); j++) {
				// add firms of type i, socialDistance, numTraitsToChange as defined in config file 
				// firms.add(new Firm(firmCounter++, i, Globals.getSocialDistanceForType(i), Globals.getNumTraitsToChangeForType(i)));	
				firms.add(new Firm(firmCounter++, i, Globals.getReferenceScopeForType(i), Globals.getComparisonTargetForType(i)));
				firmTypeCounts[i]++;
			}
		}
		
		reportDetails(0);
		reportAggregate(0);
		// for (Firm f : firms) {
		// 	System.out.println(f.toString());
		// }
		// System.out.println("\n\n");
		
		/**
		 *  RUN ITERATIONS
		 */
		for (int t = 1; t < Globals.getIterations() + 1; t++) {
			System.out.println(t);
			
			StatCalc.reset();

			//// DRAW PERFORMANCE FOR THIS PERIOD
			for (Firm f : firms) {
				f.drawPerformance();
				StatCalc.enter(f.getPerformance());
			}

			//// GIVEN PERFORMANCE, DETERMINE WHETHER FIRM SHOULD CHANGE STRATEGY
			for (Firm f : firms) {
				f.makeDecision(firms);  // make decision only sets the plan -- i.e., stay or draw new strategy.  
			}
			
			//debugAndQuit();

			//// REMOVE PERFORMING FIRMS 
			// the order of events in Greve (2002) is not very clear.  Do we do strategy change before or after replacement?
			// if before, then performance has to be drawn for changed orgs again, 
			// if after, then poor performing orgs get less chance to change.  
			// FOR NOW, we implment AFTER REPLACEMENT

			double minSurvivalPerf = StatCalc.getMean() - (Globals.getReplacementCutoff() * StatCalc.getStandardDeviation());
			
			Iterator<Firm> i = firms.iterator();
			while (i.hasNext()) {
				Firm f = i.next(); // must be called before you can call i.remove()
				if (f.getPerformance() < minSurvivalPerf) {
					firmTypeCounts[f.getType()]--; // since array index = firm type id
					i.remove();
				}
			}

			//// REMAINING FIRMS CHANGE STRATEGY GIVEN SPLINE CHANGE FUNCTION
			for (Firm f : firms) {
				f.implementDecision();
			}

			//// REPLACE REMOVED FIRMS 
			if (Globals.getReplacement().equals("number")) {
				replaceByNumber();
			} else if (Globals.getReplacement().equals("strategy")) {
				replaceByStrategy();
			}
			//Collections.sort(firms);

			//// END OF PERIOD REPORTING
			reportDetails(t);
			reportAggregate(t);

		}


	}

	private static void replaceByNumber() {
		// number of (remaining) firms by type is stored in firmTypeCounts[] 
		int numRemainingFirms = 0; 
		double[] proportions = new double[firmTypeCounts.length];
		for (int i = 0; i < firmTypeCounts.length; i++) {
			numRemainingFirms += firmTypeCounts[i];
		}
		for (int i = 0; i < firmTypeCounts.length; i++) {
			proportions[i] = (double)firmTypeCounts[i] / (double)numRemainingFirms;
		}
		for (int i = 0; i < Globals.getNumTotalFirms() - numRemainingFirms; i++) {
			int typeToAdd = getType(Globals.rand.nextDouble(), proportions);
			//Firm(int aType, int id, double aSocialDistance, int aNumTraitsToChange)
			firms.add(new Firm(firmCounter++, typeToAdd, Globals.getReferenceScopeForType(typeToAdd), Globals.getComparisonTargetForType(typeToAdd)));
			firmTypeCounts[typeToAdd]++;
		}
	}

	private static void replaceByStrategy() {
		int numRemainingFirms = 0; 
		double[] proportions = new double[firmTypeCounts.length];
		for (int i = 0; i < firmTypeCounts.length; i++) {
			numRemainingFirms += firmTypeCounts[i];
		}

		double sumStrategy[] = new double[firmTypeCounts.length];
		for (Firm f : firms) {
			sumStrategy[f.getType()] += f.getStrategy();
		}
		// reset negative sums to zero
		double totalStrategy = 0.0d;
		for (int i = 0; i < sumStrategy.length; i++) {
			if (sumStrategy[i] < 0) {
				sumStrategy[i] = 0;
			} else {
				totalStrategy += sumStrategy[i];
			}
		}
		for (int i = 0; i < firmTypeCounts.length; i++) {
			proportions[i] = sumStrategy[i] / totalStrategy;
		}
		for (int i = 0; i < Globals.getNumTotalFirms() - numRemainingFirms; i++) {
			int typeToAdd = getType(Globals.rand.nextDouble(), proportions);
			//Firm(int aType, int id, double aSocialDistance, int aNumTraitsToChange)
			firms.add(new Firm(firmCounter++, typeToAdd, Globals.getReferenceScopeForType(typeToAdd), Globals.getComparisonTargetForType(typeToAdd)));
			firmTypeCounts[typeToAdd]++;
		}
	}

	private static int getType(double prob, double[] proportions) {
		int retInt = -1;
		double[] cumProportions = new double[proportions.length];
		// create cumulative proportions
		for (int i = 0; i < proportions.length; i++) {
			if (i == 0) {
				cumProportions[i] = proportions[i];
			} else {
				cumProportions[i] = cumProportions[i-1] + proportions[i];
			}
		}

		for (int i = 0; i < proportions.length; i++) {
			if (prob <= cumProportions[i]) {
				retInt = i; 
				break;
			} 
		}
		return retInt;
	}

	private static void reportAggregate(int tick) {
		// what do we need to report for analysis?  details or summary?
		// fig 1: tick x avgStrategy across types 
		double[] strategy = new double[Globals.getNumFirmTypes()];
		double[] performance = new double[Globals.getNumFirmTypes()];
		int[] numFirmsByType = new int[Globals.getNumFirmTypes()];

		for (Firm f : firms) {
			strategy[f.getType()] += f.getStrategy();
			performance[f.getType()] += f.getPerformance();
			numFirmsByType[f.getType()] += 1;
		}

		for (int i = 0; i < Globals.getNumFirmTypes(); i++) {
			if (numFirmsByType[i] == 0) {
				Globals.summary.println(tick + "\t" + i + "\t" + numFirmsByType[i] + "\t\t");
			} else {
				Globals.summary.println(tick + "\t" + i + "\t" + numFirmsByType[i] + "\t" + (strategy[i] / numFirmsByType[i]) + "\t" + (performance[i] / numFirmsByType[i]));
			}
		}

	}

	private static void reportDetails(int tick) {
		for (Firm f : firms) {
			Globals.details.println(tick + "\t" + f.toString());
		}
	}

	private static void debugAndQuit() {
		// print firms and quit
		for (Firm f : firms) {
			System.out.println(f.toString());
		}
		System.exit(0);
	}
}
