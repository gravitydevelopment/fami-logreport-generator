# fami-logreport-generator

## Project objective
This project is to developed a jar application to compute the log files and produce a summary report of the lookup logs.

## Features
1. Ability to scan for a multiple log files
2. For now support for a "error" type of log information
3. Compute the "error" type of log into log report summary
4. The application will included maximum 3 most recent "info" log information before the error occured
5. The error log information will be marked with "-----"
6. Captured log information will be organized accordingly based on session id
7. Each set of captured log from a multiple log file will be differentiate by : -----< log file name > ----- 

## How to start
1. Clone the master branch to your local 
```
git clone https://github.com/gravitydevelopment/fami-logreport-generator.git
```
2. Using eclipse IDE, import the project.
3. Set the run configuration to include custom input arguments "error"
4. Run the init classes to execute the application
5. Application will read the sample log file provide within this package, "20190501133360_log.log" and "20190401133345_log.log", and produce log report "log_report_<timestamp>.txt"
4. If you would like to build a jar, build the jar by using eclipse import "Export" features. Make sure to select the mode export as "executable jar".
5. Currently we are working to prepare maven build to compile and generate the executable jar file.
  
## Compiling and using the application
1. Included in this package is a "release-0.1.0.zip" package inside "released" folder.
2. Unziped the "release-0.1.0.zip" package.
3. Inside the package theres's an executable binary and two (2) sample log files included ("20190501133360_log.log" and "20190401133345_log.log")
3. from command line interface, access the unzipped folder and execute :
```
java -jar logger-report.jar error
```
4. The application will start scanning the log file and printout:
```
INFO :: Scanning logs for error in "C:\release-0.1"

INFO:: Scanning :20190401133345_log.log

INFO:: Scanning :20190501133360_log.log

INFO:: Total scanned log files :2
```
5. The application will produce a log summary report with a format "log_report_ < timestamp > .txt"
6. After the two(2) logs file were scanned, the content of the "log_report_ < timestamp > .txt" should looks like:
```
-----20190401133345_log.log----- 

2019-4-1 13:33:45 [123] User1 goes to search page 
2019-4-1 13:33:46 [123] User1 types in search text 
2019-4-1 13:33:50 [123] User1 clicks search button 
2019-4-1 13:33:54 [123] ERROR: Some exception occured -----
2019-4-1 13:32:40 [190] User3 logs in 
2019-4-1 13:33:49 [190] User3 runs some job 
2019-4-1 13:33:57 [190] ERROR: Invalid input -----

--------------------------------
-----20190501133360_log.log----- 

2019-4-1 13:33:45 [124] User1 logs in 
2019-4-1 13:33:54 [124] ERROR: file exceptions -----
2019-4-1 13:33:45 [124] User1 goes to search page 
2019-4-1 13:33:46 [124] User1 types in search text 
2019-4-1 13:33:50 [124] User1 clicks search button 
2019-4-1 13:33:54 [124] ERROR: Some exception occured -----
2019-4-1 13:32:40 [191] User1 logs in 
2019-4-1 13:33:49 [191] User3 runs some job 
2019-4-1 13:33:57 [191] ERROR: Invalid input -----

--------------------------------
```
