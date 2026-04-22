package com.demo.tests;

import com.demo.pages.CartPage;
import com.demo.pages.CheckoutPage;
import com.demo.pages.DashboardPage;
import com.demo.pages.LoginPage;
import com.framework.base.BaseDriver;
import com.framework.config.ConfigReader;
import com.framework.listeners.ReportListener;
import com.framework.utils.DataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class E2ETests extends BaseDriver {

    private static final String VALID_USER = ConfigReader.get("valid.username");
    private static final String VALID_PASS = ConfigReader.get("valid.password");

    @Test
    public void completeCheckoutWithTwoProducts() {

        ReportListener.info("STEP 1: Login");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginAs(VALID_USER, VALID_PASS);

        DashboardPage dashboardPage = new DashboardPage(getDriver());
        Assert.assertTrue(dashboardPage.isDisplayed(), "Dashboard page should be visible after login");

        ReportListener.info("STEP 2: Add two products to cart");
        List<Double> addedPrices = dashboardPage.addProductsToCart(2);
        //calculation total sum of the Product Items
        double expectedSubtotal = addedPrices.stream().mapToDouble(Double::doubleValue).sum();
        ReportListener.info("Expected subtotal: $" + String.format("%.2f", expectedSubtotal));

        Assert.assertEquals(dashboardPage.getCartCount(), 2,
                "Cart badge should show 2 items");

        ReportListener.info("STEP 3: Verify cart");
        CartPage cartPage = dashboardPage.goToCart();

        Assert.assertEquals(cartPage.getItemCount(), 2,
                "Cart should contain 2 line items");

        double cartSubtotal = cartPage.getCartSubtotal();
        Assert.assertEquals(cartSubtotal, expectedSubtotal,
                "Cart subtotal should match the sum of added product prices");

        ReportListener.info("STEP 4: Enter checkout details");
        String firstName = DataGenerator.firstName();
        String lastName = DataGenerator.lastName();
        String zip = DataGenerator.zipCode();

        CheckoutPage checkoutPage = cartPage.clickCheckout();
        checkoutPage.enterDetails(firstName, lastName, zip)
                .clickContinue();

        ReportListener.info("STEP 5: Verify order overview subtotal");
        double overviewSubtotal = checkoutPage.getSubtotal();
        Assert.assertEquals(overviewSubtotal, expectedSubtotal,
                "Order overview subtotal should match the sum of product prices");

        ReportListener.info("STEP 6: Complete order");
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderSuccessful(),
                "Order confirmation header should be displayed");
        Assert.assertEquals(checkoutPage.getSuccessHeader(), "Thank you for your order!",
                "Success header text mismatch");

        ReportListener.info("Order placed successfully for "
                + firstName + " " + lastName);
    }
}