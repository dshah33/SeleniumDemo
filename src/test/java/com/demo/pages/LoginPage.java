package com.demo.pages;

import com.framework.config.ConfigReader;
import com.framework.listeners.ReportListener;
import com.framework.utils.CommonMethods;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "user-name")
    private WebElement txtUsername;

    @FindBy(id = "password")
    private WebElement txtPassword;

    @FindBy(id = "login-button")
    private WebElement btnLogin;

    @FindBy(css = "[data-test='error']")
    private WebElement lblError;

    @FindBy(className = "title")
    private WebElement lblDashboard;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Integer.parseInt(ConfigReader.get("explicit.wait"))));
    }

    public LoginPage enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(txtUsername)).clear();
        txtUsername.sendKeys(username);
        ReportListener.info("Entered username: " + username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        txtPassword.clear();
        txtPassword.sendKeys(password);
        ReportListener.info("Entered password");
        return this;
    }

    public void clickLogin() {
       btnLogin.click();
        ReportListener.info("Clicked Login button");
    }

    public void loginAs(String username, String password) {
        ReportListener.info("Login with user: " + username);
        enterUsername(username)
                .enterPassword(password)
                .clickLogin();
        CommonMethods.pause(3);
        
        CommonMethods.isAlertPresent(driver);
    }
    


    public boolean isLoginPageDisplayed() {
        return wait.until(ExpectedConditions
                .visibilityOf(btnLogin)).isDisplayed();
    }

    public boolean isDashboardVisible() {
        boolean isVisible = wait.until(ExpectedConditions
                .visibilityOf(lblDashboard)).isDisplayed();
        ReportListener.info("Dashboard page displayed: " + isVisible);
        return isVisible;
    }

    public String getErrorMessage() {
        WebElement error = wait.until(
                ExpectedConditions.visibilityOf(lblError));
        String text = error.getText();
        ReportListener.info("Error message: " + text);
        return text;
    }
}