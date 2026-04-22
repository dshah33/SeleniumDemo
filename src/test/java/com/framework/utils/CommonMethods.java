package com.framework.utils;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

public class CommonMethods {

	  public static void pause(int seconds) {
	    	try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	  }
	  
	    public static boolean isAlertPresent(WebDriver driver) {
	        try {
	            driver.switchTo().alert();
	            return true;
	        } catch (NoAlertPresentException e) {
	            return false;
	        }
	    }
}
