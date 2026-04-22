package com.demo.tests;

import com.demo.pages.LoginPage;
import com.framework.base.BaseDriver;
import com.framework.config.ConfigReader;
import com.framework.listeners.ReportListener;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseDriver {

    private static final String VALID_USER = ConfigReader.get("valid.username");
    private static final String VALID_PASS = ConfigReader.get("valid.password");
    private static final String LOCKED_USER = ConfigReader.get("locked.username");

    @Test(description = "Valid credentials should navigate to inventory page")
    public void loginWithValidCredentials() {
        ReportListener.info("Navigated to: " + ConfigReader.get("base.url"));

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginAs(VALID_USER, VALID_PASS);

        ReportListener.info("Verifying Dashboard page is displayed");
        Assert.assertTrue(loginPage.isDashboardVisible(),
                "Dashboard page should be visible after login");
    }

    @Test(description = "Locked-out user should see an error message")
    public void loginWithLockedOutUser() {
        ReportListener.info("Navigated to: " + ConfigReader.get("base.url"));

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginAs(LOCKED_USER, VALID_PASS);

        ReportListener.info("Verifying locked-out error message is shown");
        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("locked out"),
                "Expected locked-out error, got: " + error);
    }
}