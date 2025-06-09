package com.testing.periplus;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.periplus.pages.HomePage;
import com.periplus.pages.LoginPage;
import com.periplus.pages.ProductDetailPage;
import com.periplus.pages.ShoppingCartPage;


public class CartWithOneProductTest extends BaseTest {
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

        addedOneProductPrecondition(productName);
    }

    public void addedOneProductPrecondition(String productTitle) {
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
    public void TC_CART_004_updateProductQuantityInCart() {
        logger.info(" Starting TC Cart 004: Update Product Quantity In Cart Test...");
        try {
            int setQuantityChange = 2;
            shoppingCartPage.navigateToShoppingCart();
            int productPrice = Integer.parseInt(shoppingCartPage.getPrice(productName));
            shoppingCartPage.setQuantity(productName, setQuantityChange);

            shoppingCartPage.verifyProductInCart(productName, productPrice, setQuantityChange);
            shoppingCartPage.verifyTotalPriceInCart(productPrice * setQuantityChange);
        } catch (Exception e) {
            logAndFail("Error during TC CART 004 test.", e);
        }
    }

    @Test
    public void TC_CART_005_removeProductFromCart() {
        logger.info(" Starting TC Cart 005: Remove Product From Cart Test...");
        try {
            String productTitle = "Sunrise on the Reaping";
            // Navigate to landing page
            homePage.navigateToHomePage();

            // Find Selected other product & add to cart
            homePage.searchForProduct(productTitle);
            productDetailPage.clickFirstProduct();
            productDetailPage.clickAddToCartButton();
            int productPrice = Integer.parseInt(productDetailPage.getProductPrice());
            logger.info("Product titled " + productTitle + " with price " + Integer.toString(productPrice) + " successfully added to cart.");

            // remove the previous product
            shoppingCartPage.navigateToShoppingCart();
            shoppingCartPage.removeProduct(productName);

            shoppingCartPage.verifyProductInCart(productTitle, productPrice, 1);
            shoppingCartPage.verifyTotalPriceInCart(productPrice);
        } catch (Exception e) {
            logAndFail("Error during TC CART 005 test.", e);
        }
    }

    @Test
    public void TC_CART_006_emptyCartScenario() {
        logger.info(" Starting TC Cart 005: Remove Product From Cart Test...");
        try {
            shoppingCartPage.navigateToShoppingCart();
            shoppingCartPage.removeAllProductFromCart();

            shoppingCartPage.verifyCartIsEmpty();
        } catch (Exception e) {
            logAndFail("Error during TC CART 006 test.", e);
        }
    }
}
