package com.testing.periplus;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import com.periplus.pages.HomePage;
import com.periplus.pages.LoginPage;
import com.periplus.pages.ProductDetailPage;
import com.periplus.pages.ShoppingCartPage;

public class CartTest extends BaseTest {

    private HomePage homePage;
    private LoginPage loginPage;
    private ProductDetailPage productDetailPage;
    private ShoppingCartPage shoppingCartPage;

    @Override
    @BeforeClass // Or @BeforeMethod?
    public void setUp() {
        super.setUp();
        homePage = new HomePage(driver, wait);
        loginPage = new LoginPage(driver, wait);
        productDetailPage = new ProductDetailPage(driver, wait);
        shoppingCartPage = new ShoppingCartPage(driver, wait);
    }


    @Test
    public void TC_CART_001_addSingleProductToCart() {
        logger.info("Starting TC Cart 001: Add Single Product to Cart Test...");
        String productTitle = "Sunrise on the Reaping";

        try {
            // Navigate to landing page
            homePage.navigateToHomePage();
            Assert.assertTrue(driver.getTitle().contains("Periplus"), "Page title should contain 'Periplus'");
            logger.info("Page title: " + driver.getTitle() + " - Verification successful.");

            // Login
            loginPage.navigateToLoginPage();
            loginPage.performLogin(TEST_EMAIL, TEST_PASSWORD);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("account"),
                    ExpectedConditions.urlContains("index")
            ));
            logger.info("Login process completed. Current URL: " + driver.getCurrentUrl());

            // Find Selected product
            homePage.searchForProduct(productTitle);
            productDetailPage.clickFirstProductAndAddToCart();
            int productPrice = Integer.parseInt(productDetailPage.getProductPrice());
            logger.info("Product titled " + productTitle + " with price " + Integer.toString(productPrice) + " successfully added to cart.");

            // Verify added product title, price, and quantity is in cart
            shoppingCartPage.navigateToShoppingCart();
            shoppingCartPage.verifyProductInCart(productTitle, productPrice, 1);
            logger.info("Product title successfully added to cart and verified.");

            // Verify total price
            shoppingCartPage.verifyTotalPriceInCart(productPrice);
            logger.info("Total price in cart calculated successfully.");

            logger.info("TC CART 001 Add Single Product To Cart Completed Successfully!");
        } catch (Exception e) {
            logAndFail("Error during full shopping cart flow test.", e);
        }
    }

}