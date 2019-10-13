package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class FileIO {
	private static Properties p = new Properties();
	public static void loadParameters(String configFile) {
		/*
		 *	double uncertainty = 0.5d;
		 *	int numTraits = 5;
		 *	int numTraitValues = 10;
		 *	int numFirms = 10;
		*/
		if (!configFile.equals("")) {
			// Properties p = new Properties();
			try {
				p.load(new FileInputStream(configFile));
				// simulation parameters
				// if (p.getProperty("experiment") != null) { Globals.setExperiment(p.getProperty("experiment")); }
				// if (p.getProperty("numTraits") != null) { Globals.setNumTraits(p.getProperty("numTraits")); } 
				// if (p.getProperty("numTraitValues") != null) { Globals.setNumTraitValues(p.getProperty("numTraitValues")); }
				if (p.getProperty("replacement") != null) { Globals.setReplacement(p.getProperty("replacement")); }
				if (p.getProperty("numFirmTypes") != null) { Globals.setNumFirmTypes(p.getProperty("numFirmTypes")); }
				if (p.getProperty("iterations") != null) { Globals.setIterations(p.getProperty("iterations")); }
				if (p.getProperty("numFirms") != null) { Globals.setNumFirms(p.getProperty("numFirms")); }
				if (p.getProperty("referenceScope") != null) { Globals.setReferenceScope(p.getProperty("referenceScope")); }
				if (p.getProperty("comparisonTarget") != null) { Globals.setComparisonTarget(p.getProperty("comparisonTarget")); }
				if (p.getProperty("tiering") != null) { Globals.setTiering(p.getProperty("tiering")); }
				// if (p.getProperty("socialDistance") != null) { Globals.setSocialDistance(p.getProperty("socialDistance")); }
				if (p.getProperty("uncertainty") != null) { Globals.setUncertainty(p.getProperty("uncertainty")); }
				// if (p.getProperty("traitsToChange") != null) { Globals.setTraitsToChange(p.getProperty("traitsToChange")); }
				if (p.getProperty("replacementCutoff") != null) { Globals.setReplacementCutoff(p.getProperty("replacementCutoff")); }
				if (p.getProperty("outfile") != null) { Globals.setOutfile(p.getProperty("outfile")); }
					else { Globals.setOutfile(""); }
					
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			} // END try..catch
		}  // end if confFile
	}
	
	public static void printParameters() {
		Set<Entry<Object, Object>> entries = p.entrySet();
    	for (Entry<Object, Object> entry : entries) {
      		System.out.println(entry.getKey() + " : " + entry.getValue());
    	}
	}

	public static void main(String[] args) {
		// for test only
		loadParameters(args[0]);
		printParameters();
	}

}