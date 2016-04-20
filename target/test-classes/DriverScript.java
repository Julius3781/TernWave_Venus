package test.resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.log4j.Logger;

//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import reports.ReportUtil;
import tw.xls.read.Xls_Reader;
//import util.SendMail;
import util.TestUtil;


public class DriverScript {
	
	public static Xls_Reader controller ; //points to suite.xlsx
	public static Xls_Reader testData; // Data for test
	public static String currentTest; //points to current test
	public static String TestCaseName; // point to test case name
	public static String Keyword; //point to keywords
	
	public static Properties CONFIG;
	public static Properties OR;
	
	public static EventFiringWebDriver driver=null;
	public static WebDriver twd=null;
	
	public static String object;
	//public static String currentTestStepID;
	public static String proceedOnFail;
	public static String testStatus;
	public static String dataColumnName;
	public static int repeatTest;
	public static Logger AppLogs= Logger.getLogger("devpinoyLogger");
	
	@BeforeClass //i.e. Once before all methods of this class
	public static void startTesting() throws IOException {
		ReportUtil.startTesting("C://Users//gary//Documents//ITELearn//SeleniumLiveProject//Selenium_Reports//index.html",TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),"Emp-Suite80",null);
		
		CONFIG = new Properties();
		//user.dir is the project folder name
		FileInputStream fs= new FileInputStream(System.getProperty("user.dir")+"\\src\\tw\\config\\config.properties");
		CONFIG.load(fs);
	 
		OR = new Properties(); //Object Repository properties file
		fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\tw\\config\\OR.properties");
		OR.load(fs);
		
		//Essentially, controller references Controller.xlsx and testData references TestData.xlsx 	
		controller = new Xls_Reader(System.getProperty("user.dir")+"\\src\\tw\\config\\Controller.xlsx");
		testData = new Xls_Reader(System.getProperty("user.dir")+"\\src\\tw\\config\\TestData.xlsx");
	}
	
	
	@Test
	public void TestApp() {
		
		String startTime = null;
		ReportUtil.startSuite("Suite1");		
				
		for(int tcid=2; tcid <= controller.getRowCount("Suite1"); tcid++){ // points to Suite1 Sheet in Controller.xlsx
			currentTest=controller.getCellData("Suite1", "TCID", tcid); //Used by Report. Provides current Test Case ID; e.g. TC001		
			if(controller.getCellData("Suite1", "Execute", tcid).equals("Y")){  //is Execute for keyword Y/N
				
				//currentTest is obtained from Suite1 sheet, TCID column; e.g. currentTest
				//will be TC001, TC002 etc..
				//currenttest is also the name for the sheet with the steps for that test case; e.g.
				//TC001 of Suite1 sheet has a corresponding sheet in TestData.xlsx called TC001.
				//And this is where the test data for that test case resides. This for loop controls
				//access of the test data for the given test case
				for(repeatTest = 2; repeatTest <= testData.getRowCount(currentTest); repeatTest ++) {
					startTime=TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa");
					AppLogs.debug("Executing the test: " + currentTest); //log4j
				
					//This for loop controls access of the test steps for a given test case
					//We implement Keywords using Reflection API
					for(int tsid=2; tsid <= controller.getRowCount(currentTest);tsid++){
						Keyword=controller.getCellData(currentTest, "KeyWords", tsid); //get keyword from KeyWords column. keyword is the method in the Keywords class
						object=controller.getCellData(currentTest, "Object", tsid);//get object from Object column. object is the key in the OR.properties file
						TestCaseName=controller.getCellData(currentTest, "Test Case Name", tsid);//TestCaseName is used in our report
						//currentTestStepID=controller.getCellData(currentTest, "TSID", tsid);
						proceedOnFail=controller.getCellData(currentTest,"ProceedOnFail",tsid);
						dataColumnName = controller.getCellData(currentTest,"Data",tsid);
										
						try{ //Calls the Keyword functions
							Method method = Keywords.class.getMethod(Keyword);
							//Returns Object type so cast to String
							String result= (String)method.invoke(method);
							AppLogs.debug("Result of execution is: " + result);
				
							//Screenshots at each Keyword
							String fileName="Suite1_TC"+(tcid-1)+"_TS"+tsid+"_"+Keyword+repeatTest+".jpg";
							TestUtil.takeScreenShot(CONFIG.getProperty("screenshotPath")+fileName);
							ReportUtil.addKeyword(TestCaseName,Keyword, result,fileName);
				
							if(result.startsWith("Fail")) {
								testStatus=result; //failing result may also include Exception string
								// screen shot only on Fail  if using this and for every keyword "fileName" area is null for ReportUtil
								//String fileName="Suite1_TC"+tcid+"_TS"+tsid+"_"+Keyword+repeatTest+".jpg";
								//TestUtil.takeScreenShot(CONFIG.getProperty("Screenshots")+fileName);
								//ReportUtil.addKeyword(TestCaseName,Keyword, result,fileName);
					
								//column is currently blank so we proceed on fail
								if(proceedOnFail.equalsIgnoreCase("N")) {
									break; // exit keywords for current if proceed is N; i.e. bra
								}	
							}
							
							//Update Result column of Suite1 tab with "Pass" or "Fail"
							if (result.startsWith("Fail")){
								controller.setCellData("Suite1","Result", tcid, "Fail");
							} else {
								controller.setCellData("Suite1","Result", tcid, result);
							}
								
						} catch(Throwable t){
							AppLogs.debug("Error came");
						}
								
					} //test steps loop
				
					//Report Pass or Fail
					//Check Test Status				
					if(testStatus==null){
						testStatus="Pass";
					}		
					AppLogs.debug(" Current Test Case: "+currentTest+" Status: "+ testStatus );
					ReportUtil.addTestCase(currentTest, startTime, TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),testStatus);
				} //test data loop
			} 	
			else { 
				AppLogs.debug("skipping the test: " + currentTest); 
				testStatus="Skipped";
				AppLogs.debug(" Current Test Case: "+currentTest+" Status: "+ testStatus );
				
				ReportUtil.addTestCase(currentTest, startTime, TestUtil.now("dd.MMMMM.yyyyhhmm.ss aaa"),testStatus);
			}
			testStatus=null;
		}//test case loop		
		ReportUtil.endSuite();
	}
	@AfterClass
	public static void endScript(){
		ReportUtil.updateEndTime(TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"));
		
			driver.close();
		
	}
	
}
