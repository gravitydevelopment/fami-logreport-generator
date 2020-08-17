package fami.app.loggerreport.func;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.Line;

/**
 * io.java - io implementations
 *
 * @author   Fami Romli, https://github.com/gravitydevelopment/fami-cicd
 * @version  1.0
 * @since    13 August 2020
 */


public class io {
	
	private static File filePath;
	private static ArrayList<String> logFileList=new ArrayList<String>();
	private String keyword;
	
	protected io (String myKeyword , String logFilePath) { 
		filePath = new File(logFilePath); 
		keyword = myKeyword;  // to be implemented for future for sorting info type logs
	}
	
	protected boolean checkFolder() {
		
		Boolean status = false;
		
		File[] fileList = filePath.listFiles();
		
		 for (File file : fileList) {
			 if (file.isFile()) {
				 String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
				 if(filename[1].equalsIgnoreCase("log")) {
					 logFileList.add(filename[0] + "." + filename[1]);
					 System.out.println("INFO:: Scanning :" + filename[0] + "." + filename[1] + "\n");
					 status = true;
				 }
			 }
		 }
		 
		 if (status == false) {
			 System.out.println("INFO:: No log files existed in " + "\"" + filePath.getAbsolutePath() + "\"");
		 }
		
		return status;
	}
	
	
	protected void constructProcessLog() {
		
		try {
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			File logReportFile = new File(filePath.getAbsolutePath() + "/log_report_" + timestamp.getTime() +".txt");
			File logReportFileTemp = new File(filePath.getAbsolutePath() + "/log_report.temp");
			FileOutputStream outputLogReport;
			FileOutputStream outputTempLogReport;
			
			ArrayList<String> sessionIdLists = new ArrayList<String>();
			List<Integer> lineNumberToCopyFromTemp = new ArrayList<Integer>();
			
			logReportFile.createNewFile();
			
			String error = "";
			
			for (String fileName : logFileList) {
				
				outputTempLogReport = new FileOutputStream(logReportFileTemp); 
				List<String> allLines = Files.readAllLines(Paths.get(filePath.getAbsolutePath() + "/" + fileName));
				
				//Construct the session Id List
				constructSessionList (allLines, sessionIdLists, "ERROR:"); 
				
				//Construct temp log report file
				for (String sessionId : sessionIdLists)
					writeToTempLogFiles(outputTempLogReport, allLines, sessionId); 
				
				List<String> allLinesInTempFile = Files.readAllLines(Paths.get(filePath.getAbsolutePath() + "/log_report.temp"));
				
				// Construct the log detail and append the log report file
				outputLogReport = new FileOutputStream(logReportFile,true); 
				
				error = error.concat("\n-----"+ fileName + "----- \n\n");
				
				int tempCount = 0;
				
				//Get Line Pointer to copy
				for (String sessionId : sessionIdLists) {
					lineNumberToCopyFromTemp = getLineNumberToCopy(outputTempLogReport, allLinesInTempFile, sessionId);
					error = constructTempLogData(lineNumberToCopyFromTemp, error, fileName, allLinesInTempFile, sessionId);
					tempCount = tempCount + 1;
				}
				
				error = error.concat("\n--------------------------------");
				
				outputLogReport.write(error.getBytes());
				outputLogReport.close();
				outputTempLogReport.close();
				
				error = "";
				lineNumberToCopyFromTemp.clear();
				sessionIdLists.clear();
			}
			
			System.out.println("INFO:: Total scanned log files :" + logFileList.size() + "\n");
			System.out.println("INFO:: Log report file generated at: " + '"' + logReportFile.getAbsolutePath() +"\"\n");

		} catch (IOException e) { e.printStackTrace(); }	
	}
	

	private List<Integer> getLineNumberToCopy (FileOutputStream tempLogFile, List<String> logFiles, String SessionId) {
		
		Comparator<Integer> comparator = Collections.reverseOrder();
		
		int y = 0;
		List<Integer> lineContainError = new ArrayList<Integer>();
		List<Integer> lineContainToCopy = new ArrayList<Integer>();
		
		for (String line : logFiles) {
			if (line.contains("[" + SessionId + "]")) { 
				if (line.contains("ERROR:")) lineContainError.add(y + 1);
				y ++;
				}
			}
		
	    Collections.sort(lineContainError,comparator);
	    
	    int pointer_a = 0;
	    for (int i = 0 ; i < lineContainError.size() ; i ++) {
	    	
	    	lineContainToCopy.add(lineContainError.get(i));
	    	
	    	if ( i + 2 <= Integer.valueOf(lineContainError.size())) {
	    		pointer_a = lineContainError.get(i) - lineContainError.get(i + 1);
	
	    		if (pointer_a > 3) { for (int j = 1 ; j < 4 ; j++) 
	    			lineContainToCopy.add(lineContainError.get(i) - j); }
	    		
	    		else { for (int j = 1 ; j < pointer_a ; j++) 
	    			lineContainToCopy.add(lineContainError.get(i) - j); }
	    		}
	    	
	    	else {

	    		if (lineContainError.get(i) > 3) for (int j = 1 ; j < 4 ; j++) 
	    			lineContainToCopy.add(lineContainError.get(i) - j); 
	    		
	    		else {
	    			for (int j = 1 ; j < lineContainError.get(i) ; j++) 
	    				if (Integer.valueOf(lineContainError.get(i) - j) > 0 ) 
	    					lineContainToCopy.add(lineContainError.get(i) - j); 
	    		}
	    	}

	    }
	    
//		System.out.println("size for session " + "[" + SessionId + "] : " + y);
//		System.out.println("Line contains error: " + lineContainError);
//		System.out.println("Line for copy: " + lineContainToCopy);
		
		return lineContainToCopy;
	}
	
	
	private String constructTempLogData (List<Integer> linePointer, 
			String logDataStringVariable, String fileName, List<String> allLinesInTempFilea, String SessionId) {
		
		int x = 1 ;
		
		for (String line : allLinesInTempFilea) 
			if (line.contains("[" + SessionId + "]")) {
				
				for (int lineCheck : linePointer)
					if (x == lineCheck) {
						if (line.contains("ERROR:")) logDataStringVariable = logDataStringVariable.concat(line + "-----\n");
						else logDataStringVariable = logDataStringVariable.concat(line + "\n"); 
					}	
				
				x++;
			}
				
		return logDataStringVariable;
		
	}
	
	
	private void writeToTempLogFiles (FileOutputStream tempLogFile, List<String> logFiles, String SessionId) {
		
		byte[] strToBytesTempLogReport; String logLine = "";
		strToBytesTempLogReport = logLine.getBytes();
		
		try {
			for (String line : logFiles) {
				if (line.contains("[" + SessionId + "]")) { logLine = logLine.concat(line + "\n"); }}
			
			strToBytesTempLogReport = logLine.getBytes();
			tempLogFile.write(strToBytesTempLogReport);
			
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	
	private void constructSessionList (List<String> logFiles, ArrayList<String> sessionIdList, String logType) {	
		for (String line : logFiles) {
			if (line.contains("ERROR:")) setSessionList(sessionIdList, line);
		}	
	}
	
	
	private void setSessionList(ArrayList<String> sessionIdList, String input) {
		
		Boolean idCheck = false;
		
		Pattern pattern = Pattern.compile("(.*)\\[(.*?)\\](.*)");
		Matcher matcher = pattern.matcher(input);
		
		while (matcher.find()) {
			
			// Initial value add to the arraylist
			if (!matcher.group(2).equals("")
					&& !matcher.group(2).equals(null)
					&& sessionIdList.size() == 0) sessionIdList.add(matcher.group(2));
			
			// Check the validity of session id existence in the session id list
			else if (!matcher.group(2).equals("") && !matcher.group(2).equals(null)) {
				for(String sessionId : sessionIdList) {
					if (!matcher.group(2).toString().equals(sessionId)) idCheck = true;
					else {idCheck = false; }
				}
				// If the id is not exist, stored it in array list
				if (idCheck.equals(true)) sessionIdList.add(matcher.group(2));	
			}	
		}		
	}
	
	
}
