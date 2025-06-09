package com.testing.periplus;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.periplus.pages.HomePage;
import com.periplus.pages.LoginPage;
import com.periplus.pages.ProductDetailPage;
import com.periplus.pages.ShoppingCartPage;

public class CartCheckoutTest extends BaseTest {
    private HomePage homePage;
    private LoginPage loginPage;
    private ProductDetailPage productDetailPage;
    private ShoppingCartPage shoppingCartPage;

    private static final String productName = "Atomic Habits";

    @Override
    @BeforeClass
    public void setUp() {
        super.setUp();
        homePage = new HomePage(driver, wait);
        loginPage = new LoginPage(driver, wait);
        productDetailPage = new ProductDetailPage(driver, wait);
        shoppingCartPage = new ShoppingCartPage(driver, wait);
    }

    @AfterClass
    public void clearCart() {
        shoppingCartPage.navigateToShoppingCart();
        shoppingCartPage.removeAllProductFromCart();
    }

    public void addedOneProduct(String productTitle) {
        homePage.navigateToHomePage();
        loginPage.navigateToLoginPage();
        loginPage.performLogin(TEST_EMAIL, TEST_PASSWORD);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("account"),
                ExpectedConditions.urlContains("index")
        ));

        homePage.searchForProduct(productTitle);
        productDetailPage.clickFirstProduct();
        productDetailPage.clickAddToCartButton();
    }

    @Test
    public void TC_CART_007_cartPersistenceAfterRelogin() {
        logger.info(" Starting TC Cart 007: Cart Persistence After Re-login Test...");
        try {
            addedOneProduct(productName);

            // logout
            homePage.navigateToHomePage();
            homePage.logout();

            // login lagi
            loginPage.navigateToLoginPage();
            loginPage.performLogin(TEST_EMAIL, TEST_PASSWORD);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("account"),
                    ExpectedConditions.urlContains("index")
            ));

            shoppingCartPage.navigateToShoppingCart();
            int productPrice = Integer.parseInt(shoppingCartPage.getPrice(productName));
            shoppingCartPage.verifyProductInCart(productName, productPrice, 1);
            shoppingCartPage.verifyTotalPriceInCart(productPrice);
        } catch (RuntimeException e) {
            logAndFail("Error during TC CART 007 test.", e);
        }
    }

    @Test
    public void TC_CART_008_proceedToCheckout() {
        logger.info("Starting TC Cart 008: Proceeding to Checkout Test...");

        shoppingCartPage.navigateToShoppingCart();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("checkout/cart"),
                "Expected to be on cart page, but current URL is: " + currentUrl);
        logger.info("Successfully navigated to cart page: " + currentUrl);

        // Proceed to checkout
        shoppingCartPage.proceedToCheckout();

        String checkoutUrl = driver.getCurrentUrl();
        Assert.assertTrue(checkoutUrl.contains("checkout/shipping_address"),
                "Expected to be redirected to shipping address page, but current URL is: " + checkoutUrl);

        logger.info("Successfully proceeded to checkout shipping address page: " + checkoutUrl);
    }
}
