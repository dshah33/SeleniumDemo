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
import java.util.List;

public class CartPage {

	private final WebDriver driver;
	private final WebDriverWait wait;
	  
	  @FindBy(className = "cart_item")
	  private List<WebElement> lblCartItems;
	  
	  @FindBy(className = "inventory_item_name") 
	  private List<WebElement> 	  lblItemName;
	  
	  @FindBy(className = "inventory_item_price") 
	  private List<WebElement> 	  lblItemPrice;
	  
	  @FindBy(css = "[data-test='checkout']")
	  private WebElement btnCheckout;
	 

	public CartPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(ConfigReader.get("explicit.wait"))));
	}

	public boolean isDisplayed() {
		return driver.getCurrentUrl().contains("cart");
	}

	public int getItemCount() {
		wait.until(ExpectedConditions.visibilityOfAllElements(lblCartItems));
		return lblCartItems.size();
	}

	public double getCartSubtotal() {
		List<WebElement> items = lblCartItems;
		double total = 0;
		for (int i=0;i<items.size();i++) {
			String name = lblItemName.get(i).getText();
			String priceText = lblItemPrice.get(i).getText();
			total += parsePrice(priceText);
			ReportListener.info("Cart item: " + name + " - " + priceText);
		}
		ReportListener.info("Cart subtotal (calculated): $" + String.format("%.2f", total));
		return total;
	}

	public CheckoutPage clickCheckout() {
		wait.until(ExpectedConditions.elementToBeClickable(btnCheckout)).click();
		ReportListener.info("Clicked Checkout");
		return new CheckoutPage(driver);
	}

	private static double parsePrice(String priceText) {
		return Double.parseDouble(priceText.replace("$", "").trim());
	}
}