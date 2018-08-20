package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class FileIO {
	public static void loadParameters(String configFile) {
		/*
		 *	double uncertainty = 0.5d;
		 *	int numTraits = 5;
		 *	int numTraitValues = 10;
		 *	int numFirms = 10;
		*/
		if (!configFile.equals("")) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(configFile));
				// simulation parameters
				if (p.getProperty("numFirms") != null) { Globals.setNumFirms(Integer.parseInt(p.getProperty("numFirms"))); }
				if (p.getProperty("iterations") != null) { Globals.setIterations(Integer.parseInt(p.getProperty("iterations"))); }
				// if (p.getProperty("adaptation") != null) { Globals.setAdaptation(p.getProperty("adaptation")); }
				if (p.getProperty("uncertainty") != null) { Globals.setInnovation(Double.parseDouble(p.getProperty("uncertainty"))); }
				if (p.getProperty("numTraits") != null) { Globals.setResourcesIncrement(Integer.parseInt(p.getProperty("numTraits"))); }
				if (p.getProperty("numTraitValues") != null) { Globals.setSearchScope(Integer.parseInt(p.getProperty("numTraitValues"))); }
				if (p.getProperty("outfile") != null) { Globals.setOutfile(p.getProperty("outfile")); }
					else { Globals.setOutfile(""); }
					
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			} // END try..catch
		}  // end if confFile
	}
	
	public static void printParameters() {
		Set<Entry<Object, Object>> entries = props.entrySet();
    	for (Entry<Object, Object> entry : entries) {
      		System.out.println(entry.getKey() + " : " + entry.getValue());
    	}

		System.out.println("N: " + Globals.getN());
		System.out.println("initResources: " + Globals.getInitResources());
		System.out.println("numFirms: " + Globals.getNumFirms());
		System.out.println("iterations: " + Globals.getIterations());
		System.out.println("innovation: " + Globals.getInnovation());
		System.out.println("resourcesIncrement: " + Globals.getResourcesIncrement());
		System.out.println("searchScope: " + Globals.getSearchScope());
		// System.out.println("adaptation: " + Globals.getAdaptation());
		System.out.println("resourceDecision: " + Globals.getResourceDecision());
		System.out.println("resourceThreshold: " + Globals.getResourceThreshold());
		System.out.println("outfile: " + Globals.getOutfilename());
	}

	public static void main(String[] args) {
		// for test only
		loadParameters(args[0]);
		printParameters();
	}

}