package com.framework.base;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverDesign;
import com.framework.driver.DriverInit;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseDriver {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver driver = DriverInit.createDriver();
        DriverDesign.setDriver(driver);
        driver.get(ConfigReader.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverDesign.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverDesign.getDriver();
    }
}