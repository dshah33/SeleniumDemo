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
import java.util.ArrayList;
import java.util.List;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement lblPageTitle;

    @FindBy(className = "inventory_item")
    private List<WebElement> lblProductItems;

    @FindBy(className = "inventory_item_name")
    private List<WebElement> lblProductName;

    @FindBy(className = "inventory_item_price")
    private List<WebElement> lblProductPrice;

    @FindBy(className = "shopping_cart_link")
    private WebElement lnkCart;

    @FindBy(className = "shopping_cart_badge")
    private List<WebElement> lblCartBadge;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Integer.parseInt(ConfigReader.get("explicit.wait"))));
    }

    public boolean isDisplayed() {
        return wait.until(ExpectedConditions
                .visibilityOf(lblPageTitle)).isDisplayed();
    }

    public List<Double> addProductsToCart(int count) {
        wait.until(ExpectedConditions.visibilityOfAllElements(lblProductItems));
        List<WebElement> items = lblProductItems;
        System.out.println("----items are-----"+items);

        List<Double> prices = new ArrayList<>();
        int added = 0;

        for (int i=0; i< items.size(); i++) {
            if (added >= count) break;

            String name = lblProductName.get(i).getText();
            String priceText = lblProductPrice.get(i).getText();
            double priceValue = parsePrice(priceText);

            //add to cart
            items.get(i).findElement(By.tagName("button")).click();

            ReportListener.info("Added to cart: " + name + " @ " + priceText);
            prices.add(priceValue);
            added++;
        }

        ReportListener.info("Cart badge count: " + getCartCount());
        return prices;
    }

    public int getCartCount() {
    	CommonMethods.pause(3);
        List<WebElement> badges =lblCartBadge;
        if (badges.isEmpty()) return 0;
        return Integer.parseInt(badges.get(0).getText());
    }

    public CartPage goToCart() {
        lnkCart.click();
        ReportListener.info("Navigated to Cart");
        return new CartPage(driver);
    }

    private static double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }
}