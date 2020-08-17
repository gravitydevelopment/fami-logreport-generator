package fami.app.loggerreport.func;

/**
 * logger.java - logger check implementation
 *
 * @author   Fami Romli, https://github.com/gravitydevelopment/fami-cicd
 * @version  1.0
 * @since    13 August 2020
 */

public class logger {
	
	public static void constructProccessLog(String myKeyword , String myLogFilePath) {
		
		io myio = new io(myKeyword, myLogFilePath);
	
		if (myio.checkFolder() == true) myio.constructProcessLog();
		
	}
}
