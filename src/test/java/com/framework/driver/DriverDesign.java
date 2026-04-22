package com.framework.driver;

import org.openqa.selenium.WebDriver;

public class DriverDesign {

    private static final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    private DriverDesign() {
    }

    public static WebDriver getDriver() {
        return webDriver.get();
    }

    public static void setDriver(WebDriver driver) {
        webDriver.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = webDriver.get();
        if (driver != null) {
            driver.quit();
            webDriver.remove();
        }
    }
}
