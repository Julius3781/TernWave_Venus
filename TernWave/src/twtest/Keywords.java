package twtest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.Select;


public class Keywords extends DriverScript {

	
    public static String EnterText(){
    	String data= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	
      AppLogs.debug("Executing EnterText");
      try{
    	  driver.findElement(By.xpath(OR.getProperty(object))).clear();
    	  driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(data); 
      } catch(Throwable t) {
    	  AppLogs.debug("Error occurred while entering text: " +object +t.getMessage());   
    	  return "Fail : " + t.getMessage();
      }
        return "Pass";
    }
    
    //Gary
    public static String EnterKeyText(){
    	String data= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	
    	AppLogs.debug("Executing EnterText");
    	
    	try{
    		
    	  driver.findElement(By.xpath(OR.getProperty(object))).clear();

    	  //Not the best way to do this but we'll go with this for now; Possibly use Reflection instead
    	  //Users should add cases to the switch statement as necessary
    	  switch (data)
  		  {
  			case "ARROW_DOWN":
  				driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(Keys.ARROW_DOWN);
  				break;
  			
  		  }
 
    	} catch(Throwable t) {
    	  AppLogs.debug("Error occurred while entering text: " +object +t.getMessage());   
    	  return "Fail : " + t.getMessage();
    	}
        return "Pass";
    }
    

    public static String ClickButton(){
        AppLogs.debug("Executing ClickButton");
        try{ 
        	driver.findElement(By.xpath(OR.getProperty(object))).click();
        } catch(Throwable t){
        	AppLogs.debug("Error occurred when clicking on button: "+object + t.getMessage());
        	return "Fail: "+ t.getMessage();
        }
        return "Pass";
        //driver.findElement(By.linkText(wsgPath));
    }


    public static String Navigate(){
  		AppLogs.debug("Executing Navigate");
  	  		if(twd == null){
  	  			if(CONFIG.getProperty("browser").equals("Firefox")){
  	  				twd = new FirefoxDriver();
  	  				driver = new EventFiringWebDriver(twd);
  	  				AppLogs.debug("Browser started");
  	  				driver.manage().window().maximize();
  	  				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  	  			}
  	  		}
  	  		driver.navigate().to(CONFIG.getProperty(object));
  	  		return "Pass";	
  	}
 
    public static String GetUrl(){
    	AppLogs.debug(" Executing GetUrl");
    	if(twd==null){
    		if (CONFIG.getProperty("browser").equals("Firefox")){
    			twd= new FirefoxDriver();
    			driver=new EventFiringWebDriver(twd);
    			driver.manage().window().maximize();
    			driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
    		}
    	}
        driver.get(CONFIG.getProperty("testSiteName"));
    	return "Pass";
    }

    public static String CheckBox(){
        AppLogs.debug("Executing CheckBox");
        
        try{
        if(driver.findElement(By.xpath(OR.getProperty(object))).getAttribute("checked").equalsIgnoreCase("Checked")){
            // Don't do anything
        }else {
                driver.findElement(By.xpath(OR.getProperty(object))).click();
        }
        }catch(Throwable t){
        	AppLogs.debug("Error occurred while checking the checkbox: "+object +t.getMessage());
			return "Fail : " + t.getMessage();
        }
        
        return "Pass";
    }

    public static String VerifyText(){
    	
        AppLogs.debug("Executing verifyText");
        
        //Removed by Gary
        ////String expected=APPTEXT.getProperty(object);//Should get expected from Data sheet, not properties file
        //Added by Gary
        String expected= testData.getCellData(currentTest,dataColumnName,repeatTest);
        
        String actual=driver.findElement(By.xpath(OR.getProperty(object))).getText();
     
        AppLogs.debug(expected);
        AppLogs.debug(actual);
        
        try{
        	Assert.assertEquals(expected.trim(), actual.trim());
        }catch(Throwable t){
        	AppLogs.debug("Error in text: " + object);
        	AppLogs.debug("Actual: " + actual);
        	AppLogs.debug("Expected: " + expected);
        	return "Fail: " + t.getMessage(); 
        }
        return "Pass"; 
    }
    
    //Gary
    public static String VerifyTextOnSubPage() {
    	//This is method is not ideal from a generic perspective but
    	//it's as close as we could get given the limitations of the SS
    	//Ideally, we would use VerifyText() only, but make it more expansive
    	//to handle situations such as these. And that would require additional
    	//information from the SS; i.e. Additional columns.
    	
        AppLogs.debug("Executing verifyTextOnSubPage");
        
        String expected = testData.getCellData(currentTest,dataColumnName,repeatTest);
        String actual = "";
        
        //Gets the page. Remove this line
        driver.findElement(By.xpath(OR.getProperty(object))); //The object is:  //*[@id='accordion']
		
		for(WebElement we: driver.findElement(By.xpath(OR.getProperty(object))).findElements(By.tagName("span"))) {
			System.out.println(we.getText());
			
			if(we.getText().equals(expected)) {
				actual = expected;
			}
		}
     
        AppLogs.debug(expected);
        AppLogs.debug(actual);

        try{
        	Assert.assertEquals(expected.trim(), actual.trim());
        }catch(Throwable t){
        	AppLogs.debug("Error in text: " + object);
        	AppLogs.debug("Actual: " + actual);
        	AppLogs.debug("Expected: " + expected);
        	return "Fail: " + t.getMessage(); 
        }
        return "Pass"; 
    }
    
    //Gary
    public static String VerifyNoTextOnSubPage() {
    	//This is method is not ideal from a generic perspective but
    	//it's as close as we could get given the limitations of the SS
    	//Ideally, we would use VerifyText() only, but make it more expansive
    	//to handle situations such as these. And that would require additional
    	//information from the SS; i.e. Additional columns.
    	
        AppLogs.debug("Executing verifyTextOnSubPage");
        
        String expected = testData.getCellData(currentTest,dataColumnName,repeatTest);
        String actual = "";
		
		for(WebElement we: driver.findElement(By.xpath(OR.getProperty(object))).findElements(By.tagName("span"))) {			
			if(we.getText().equals(expected)) {
				actual = expected;
			}
		}
     
        AppLogs.debug(expected);
        AppLogs.debug(actual);

        try{
        	Assert.assertNotEquals(expected.trim(), actual.trim());
        }catch(Throwable t){
        	AppLogs.debug("Error in text: " + object);
        	AppLogs.debug("Actual: " + actual);
        	AppLogs.debug("Expected: " + expected);
        	return "Fail: " + t.getMessage(); 
        }
        return "Pass"; 
    }
    

    public static String DropDownMenu() {
    	String data= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	    	
    	AppLogs.debug("Executing Drop Down Menu");
    	
    	try{	
            WebElement dropdown= driver.findElement(By.xpath(OR.getProperty(object)));
            //Move mouse pointer on the Drop down menu
            Actions actions= new Actions(driver);
            actions.moveToElement(dropdown).perform();
            driver.findElement(By.linkText(data)).click();
            return "Pass";
		} catch (Throwable t){
			AppLogs.debug("Error occurred with the drop down menu: "+object +t.getMessage());
				return "Fail: " + t.getMessage(); 
		}
               
     }
    
 
    public static String waitTime() throws InterruptedException{
    	String data= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	
    	AppLogs.debug("Executing WaitTime");
               
    	Thread.sleep(Long.parseLong(data));
                return "Pass";
    }
    
    
    public static String ClickLink(){
    	AppLogs.debug("Executing ClickLink");
        try{
    	driver.findElement(By.xpath(OR.getProperty(object))).click();
    	
        } catch(Throwable t){
        	AppLogs.debug("Error occurred when clicking on link :" + object);
        	return "Fail:Link not found - " + t.getMessage(); 
        	
        }
        return "Pass";
    }
    
    //Gary
    public static String DropDown() {
    	String data= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	    	
    	AppLogs.debug("Executing Drop Down");
    	
    	try{
    		Select dropdown = new Select(driver.findElement(By.xpath(OR.getProperty(object))));
			dropdown.selectByVisibleText(data);
            return "Pass";
		} catch (Throwable t){
			AppLogs.debug("Error occurred with the drop down: "+object +t.getMessage());
			return "Fail: " + t.getMessage(); 
		}        
    }
    
    //Gary
    public static String FileUploadAutoIT() throws InterruptedException {
    	//This method is for handling a file upload using AutoIT
    	//This method receives an AutoIT file that is executed,
    	//resulting in a file being uploaded to the AUT; e.g. The Browse
    	//button of the AUT is clicked resulting in the presentation of the File Upload dialog
    	//box from Windows. The AutoIT file used by this method will be executed, resulting
    	//in a specified file being uploaded. It is up to you to create the AutoIT file.
    	//Get the AutoIT file from the Test Data sheet
    	String filename= testData.getCellData(currentTest,dataColumnName,repeatTest);
    	
    	try {
    		Thread.sleep(5000);
    		Runtime.getRuntime().exec(filename);
    		Thread.sleep(5000);
    		return "Pass";
    	} catch (Throwable t) {
    		AppLogs.debug("Error occurred with executing the AutoIT file: "+object +t.getMessage());
			return "Fail: " + t.getMessage(); 
    	}	
    }
    
    //Deepa
    public static boolean ElementNotPresent(){
    	AppLogs.debug("Executing Element not present");
    
    	Boolean status = false;
    	try{
    		if(driver.findElement(By.xpath(OR.getProperty(object))).findElements(By.tagName("div")).size() == 0) {
    			status = true;
    		}
    		return status;
    	}
    	catch(Exception e){
    		AppLogs.debug("element is displayed");
    		return false;
    	}
    }
    
    //Deepa
    public static boolean ImageisPresent(){
        AppLogs.debug("Executing VerifyImage");
        try{
        	WebElement ImageFile = driver.findElement(By.xpath(OR.getProperty(object)));
    		ImageFile.isDisplayed();
    		return true;
    	}
    	catch(Exception e){
    		AppLogs.debug("element not displayed");
    		return false;
    	}
    }
    
    //Padmaja
    public static String CheckForAttribute(){
    	AppLogs.debug("Executing CheckForAttribute");
    	//Read attribute which needs to be checked from TestData.xls
    	String attribute = testData.getCellData(currentTest,dataColumnName,repeatTest);
    	        
    	try{       
    	   //Check if the attribute is present in the WebElement. If found, it returns string true.
    	   String attributeActual = driver.findElement(By.xpath(OR.getProperty(object))).getAttribute(attribute);
    	        
    	   String attributeExpected = "true";
    	        
    	   //Check if found and actual values match
    	   Assert.assertEquals(attributeActual, attributeExpected);
    	   AppLogs.debug("Found attribute : " + attribute); 
    	    
    	} catch(Throwable t){
    	   AppLogs.debug("Error occurred when checking for attribute : " + attribute);
    	   return "Fail:Attribute not found - " + t.getMessage();    
    	}
    	return "Pass";
    }

    


}
