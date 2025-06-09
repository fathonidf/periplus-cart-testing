package com.testing.periplus;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
    @BeforeClass
    public void setUp() {
        super.setUp();
        homePage = new HomePage(driver, wait);
        loginPage = new LoginPage(driver, wait);
        productDetailPage = new ProductDetailPage(driver, wait);
        shoppingCartPage = new ShoppingCartPage(driver, wait);
    }

    @AfterMethod
    public void removeProductFromCartEachTestCase() {
        shoppingCartPage.removeAllProductFromCart();
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
            productDetailPage.clickFirstProduct();
            productDetailPage.clickAddToCartButton();
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
            logAndFail("Error during TC CART 001 test.", e);
        }
    }

    @Test
    public void TC_CART_002_addMultipleQuantitiesOfSameProduct() {
        logger.info(" Starting TC Cart 002: Add Multiple Quantities of Same Product Test...");
        String productTitle = "Atomic Habits";
        int quantity = 3;

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
            productDetailPage.clickFirstProduct();

            // Set quantity to 3 and add to cart
            productDetailPage.setQuantity(quantity); // Set quantity to 3
            productDetailPage.clickAddToCartButton();
            int productPrice = Integer.parseInt(productDetailPage.getProductPrice());
            logger.info(Integer.toString(quantity) + " product titled " + productTitle + " with each price " + Integer.toString(productPrice) + " successfully added to cart.");

            // Verify added product title, price, and quantity is in cart
            shoppingCartPage.navigateToShoppingCart();
            shoppingCartPage.verifyProductInCart(productTitle, productPrice, quantity);
            logger.info("Product title successfully added to cart and verified.");

            // Verify total price
            shoppingCartPage.verifyTotalPriceInCart(productPrice * quantity); // multipled with the quantity
            logger.info("Total price in cart calculated successfully.");

            logger.info("TC CART 002 Add Multiple Quantities of Same Product Completed Successfully!");

        } catch (Exception e) {
            logAndFail("Error during TC CART 002 test.", e);
        }
    }

    @Test
    public void TC_CART_003_addMultipleDifferentProducts() {
        logger.info(" Starting TC Cart 003: Add Multiple Different Products Test...");
        String productTitle1 = "Sunrise on the Reaping";
        String productTitle2 = "Atomic Habits";

        try  {
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

            // Find product 1 & add to cart
            homePage.searchForProduct(productTitle1);
            productDetailPage.clickFirstProduct();
            productDetailPage.clickAddToCartButton();
            int productPrice1 = Integer.parseInt(productDetailPage.getProductPrice());
            logger.info("Product titled " + productTitle1 + " with price " + Integer.toString(productPrice1) + " successfully added to cart.");

            homePage.navigateToHomePage();

            // Find product 2 & add to cart
            homePage.searchForProduct(productTitle2);
            productDetailPage.clickFirstProduct();
            productDetailPage.clickAddToCartButton();
            int productPrice2 = Integer.parseInt(productDetailPage.getProductPrice());
            logger.info("Product titled " + productTitle2 + " with price " + Integer.toString(productPrice2) + " successfully added to cart.");

            // Verify added product title, price, and quantity is in cart
            shoppingCartPage.navigateToShoppingCart();
            shoppingCartPage.verifyProductInCart(productTitle1, productPrice1, 1);
            shoppingCartPage.verifyProductInCart(productTitle2, productPrice2, 1);
            logger.info("Products title successfully added to cart and verified.");

            // Verify total price
            shoppingCartPage.verifyTotalPriceInCart(productPrice1 + productPrice2); // multipled with the quantity
            logger.info("Total price in cart calculated successfully.");
        } catch (Exception e) {
            logAndFail("Error during TC CART 003 test.", e);
        }

    }
}