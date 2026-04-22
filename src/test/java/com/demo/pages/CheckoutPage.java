package com.demo.pages;

import com.framework.config.ConfigReader;
import com.framework.listeners.ReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "[data-test='firstName']")
    private WebElement txtFirstName;

    @FindBy(css = "[data-test='lastName']")
    private WebElement txtLastName;

    @FindBy(css = "[data-test='postalCode']")
    private WebElement txtZipCode;

    @FindBy(css = "[data-test='continue']")
    private WebElement btnContinue;

    @FindBy(className = "summary_subtotal_label")
    private WebElement lblSubtotal;

    @FindBy(css = "[data-test='finish']")
    private WebElement btnFinish;

    @FindBy(className = "complete-header")
    private WebElement lblSuccessHeader;

    @FindBy(className = "complete-text")
    private WebElement lblSuccessText;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Integer.parseInt(ConfigReader.get("explicit.wait"))));
    }

    public CheckoutPage enterDetails(String firstName, String lastName, String zip) {
        wait.until(ExpectedConditions.visibilityOf(txtFirstName))
                .sendKeys(firstName);
       txtLastName.sendKeys(lastName);
        txtZipCode.sendKeys(zip);

        ReportListener.info("Checkout details - "
                + "First: " + firstName + ", "
                + "Last: " + lastName + ", "
                + "Zip: " + zip);
        return this;
    }

    public CheckoutPage clickContinue() {
        btnContinue.click();
        ReportListener.info("Clicked Continue to order overview");
        return this;
    }

    public double getSubtotal() {
        String text = wait.until(ExpectedConditions
                .visibilityOf(lblSubtotal)).getText();
        double value = Double.parseDouble(text.replaceAll("[^0-9.]", ""));
        ReportListener.info("Order overview subtotal: $" + String.format("%.2f", value));
        return value;
    }

    public void clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(btnFinish)).click();
        ReportListener.info("Clicked Finish - order submitted");
    }

    public boolean isOrderSuccessful() {
        return wait.until(ExpectedConditions
                .visibilityOf(lblSuccessHeader)).isDisplayed();
    }

    public String getSuccessHeader() {
        String text = lblSuccessHeader.getText();
        ReportListener.info("Success header: " + text);
        return text;
    }

    public String getSuccessText() {
        return lblSuccessText.getText();
    }
}