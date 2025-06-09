package com.periplus.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class ShoppingCartPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = Logger.getLogger(ShoppingCartPage.class.getName());

    private final By cartItemContainers = By.xpath("//div[@class='row row-cart-product']");
    private final By productNameInCart = By.xpath(".//p[contains(@class,'product-name')]"); // FIXED: Added dot
    private final By productQuantityInCart = By.xpath(".//input[contains(@class,'input-number') and @type='text']");
    private final By cartTotalElement = By.xpath("//span[@id='sub_total']");
    private final By proceedToCheckoutButton = By.xpath("//a[contains(@href,'checkout/checkout') or text()='Proceed to Checkout']");
    private final By removeProductButton = By.xpath("//a[contains(@class,'btn btn-cart-remove')]");
    private final By productPriceInCart = By.xpath(".//div[contains(@class,'col-lg-10') and contains(@class,'col-9')]//div[@class='row' and contains(.,'Rp ')]");
    private final By emptyCartMessage = By.xpath("//p[contains(text(),'Your shopping cart is empty') or contains(text(),'Your cart is empty') or contains(text(),'Keranjang Anda kosong')]");

    public ShoppingCartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    protected String formatPriceToRupiah(long price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        String formattedPrice = formatter.format(price);
        return "Rp " + formattedPrice;
    }

    public void navigateToShoppingCart() {
        driver.get("https://www.periplus.com/checkout/cart");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemContainers));
        logger.info("Navigated to Shopping Cart page.");
    }

    public void verifyProductInCart(String expectedProductName, int expectedProductPrice, int expectedQuantity) {
        List<WebElement> cartItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemContainers));
        Assert.assertFalse(cartItems.isEmpty(), "Cart should contain at least 1 product.");
        logger.info("Product(s) found in cart (" + cartItems.size() + " items).");

        boolean productFound = false;
        String formattedExpectedPrice = formatPriceToRupiah(expectedProductPrice);

        for (int i = 0; i < cartItems.size(); i++) {
            WebElement item = cartItems.get(i);
            try {
                // FIXED: Now using relative xpath from item context
                WebElement nameElement = item.findElement(productNameInCart);
                String actualProductName = nameElement.getText().trim();
                logger.info("Cart item " + (i+1) + " product name: " + actualProductName);

                // Verify product name
                if (actualProductName.contains(expectedProductName)) {
                    logger.info("Product '" + expectedProductName + "' found in cart. Proceeding with price and quantity verification.");
                    Assert.assertTrue(actualProductName.contains(expectedProductName),
                            "Actual product name '" + actualProductName + "' does not contain expected '" + expectedProductName + "'");

                    // Verify product price
                    WebElement priceElement = item.findElement(productPriceInCart);
                    String actualPriceText = priceElement.getText().trim();
                    logger.info("Actual price text found: '" + actualPriceText + "' for product '" + actualProductName + "'");

                    String cleanActualPrice = actualPriceText.split(" or ")[0].trim();
                    Assert.assertEquals(cleanActualPrice, formattedExpectedPrice,
                            "Price for product '" + expectedProductName + "' is incorrect. Expected: " + formattedExpectedPrice + ", Actual: " + cleanActualPrice);
                    logger.info("Price for '" + expectedProductName + "' verified: " + cleanActualPrice);

                    // Verify product quantity
                    WebElement quantityInputElement = item.findElement(productQuantityInCart);
                    String actualQuantityValue = quantityInputElement.getAttribute("value");
                    int actualQuantity = Integer.parseInt(actualQuantityValue);

                    Assert.assertEquals(actualQuantity, expectedQuantity,
                            "Quantity for product '" + expectedProductName + "' is incorrect. Expected: " + expectedQuantity + ", Actual: " + actualQuantity);
                    logger.info("Quantity for '" + expectedProductName + "' verified: " + actualQuantity);

                    productFound = true;
                    break;
                }
            } catch (Exception e) {
                logger.warning("Error processing cart item " + (i+1) + ": " + e.getMessage());
            }
        }
        Assert.assertTrue(productFound, "Product '" + expectedProductName + "' with price " + formattedExpectedPrice + " and quantity " + expectedQuantity + " not found in cart or verification failed.");
    }

    public void verifyTotalPriceInCart(int expectedTotalPrice) {
        WebElement totalPriceElement = driver.findElement(cartTotalElement);
        String actualPrice = totalPriceElement.getText();
        String formattedExpectedTotalPrice = formatPriceToRupiah(expectedTotalPrice);
        logger.info("Found total price in cart is " + actualPrice);

        Assert.assertTrue(actualPrice.contains(formattedExpectedTotalPrice));
    }

    public void removeAllProductFromCart() {
        navigateToShoppingCart();

        while (true) {
            List<WebElement> currentRemoveButtons = driver.findElements(removeProductButton);
            if (currentRemoveButtons.isEmpty()) {
                logger.info("No more remove buttons found. Cart is empty or all items removed.");
                break;
            }

            try {
                WebElement removeButton = currentRemoveButtons.get(0);
                removeButton.click();
                logger.info("Clicked remove button for an item.");

                wait.until(ExpectedConditions.stalenessOf(removeButton));
                logger.info("Item removed. Waiting for cart to update...");
                logger.info("Cart updated after removal.");

            } catch (Exception e) {
                logger.warning("Error during product removal or waiting for cart update: " + e.getMessage() + ". Attempting next item if any.");
            }
        }
        logger.info("All products successfully removed from cart.");
    }
}