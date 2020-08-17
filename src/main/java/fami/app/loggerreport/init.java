package fami.app.loggerreport;

import fami.app.loggerreport.func.logger;

/**
 * init.java - Initialize application
 *
 * @author   Fami Romli, https://github.com/gravitydevelopment/fami-cicd
 * @version  1.0
 * @since    13 August 2020
 */

public class init {
	
	public static void main(String[] args) {
		
		if (args.length == 1) {
			
			for (String val:args) {
				// input - help				
				if (val.equals("help")) {
					System.out.println("Available input: \nerror");
				}
				
				// input - error				
				else if (val.equals("error")) {
					System.out.println("INFO :: Scanning logs for error in " + "\"" + System.getProperty("user.dir")+ "\"\n");

					logger.constructProccessLog("error", System.getProperty("user.dir")); // do the error logic
				}
				
				else {
					System.out.println("Error::Please insert a correct input. Insert \"help\" to view all available input.");
				}
			}

		}
		
		else if (args.length > 1) {
			System.out.println("Fail::Too many input");
		}
		
		else if (args.length < 1 ) {
			System.out.println("Fail::Please enter your input arguments");
		}
		
	}

}
