package com.periplus.pages;

import org.openqa.selenium.*;
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
    private final By productNameInCart = By.xpath(".//p[contains(@class,'product-name')]");
    private final By productQuantityInCart = By.xpath(".//input[contains(@class,'input-number') and @type='text']");
    private final By cartTotalElement = By.xpath("//span[@id='sub_total']");
    private final By checkoutButton = By.xpath("//div[@class='button5']//a[contains(@onclick,'beginCheckout()')]");
    private final By removeProductButton = By.xpath("//a[contains(@class,'btn btn-cart-remove')]");
    private final By removeSelectedProductButton = By.xpath(".//a[contains(@class,'btn btn-cart-remove')]");
    private final By productPriceInCart = By.xpath(".//div[contains(@class,'col-lg-10') and contains(@class,'col-9')]//div[@class='row' and contains(.,'Rp ')]");
    private final By emptyCartMessage = By.xpath("//div[@class='content' and text()='Your shopping cart is empty']");

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

    public void setQuantity(String productName, int quantity) {
        try {
                List<WebElement> cartItems = driver.findElements(cartItemContainers);

                for (int i = 0; i < cartItems.size(); i++) {
                    WebElement item = driver.findElements(cartItemContainers).get(i);
                    WebElement nameElement = item.findElement(productNameInCart);
                    String actualProductName = nameElement.getText().trim();

                    if (actualProductName.contains(productName)) {
                        logger.info("Product '" + productName + "' found. Setting quantity to: " + quantity);

                        WebElement quantityElement = item.findElement(productQuantityInCart);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", quantityElement, String.valueOf(quantity));
                        quantityElement.sendKeys(Keys.ENTER);

                        logger.info("Quantity set to: " + quantity);
                        return;
                    }
                }
                throw new RuntimeException("Product '" + productName + "' not found in cart.");

        } catch (Exception e) {
            logger.severe("Failed to set quantity: " + e.getMessage());
            throw new RuntimeException("Failed to set product quantity.", e);
        }
    }

    public String getPrice(String productName) {
        try {
            List<WebElement> cartItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemContainers));
            Assert.assertFalse(cartItems.isEmpty(), "Cart should contain at least 1 product.");

            boolean productFound = false;

            for (int i = 0; i < cartItems.size(); i++) {
                WebElement item = cartItems.get(i);
                try {
                    WebElement nameElement = item.findElement(productNameInCart);
                    String actualProductName = nameElement.getText().trim();
                    logger.info("Checking cart item " + (i+1) + " product name: " + actualProductName);

                    if (actualProductName.contains(productName)) {

                        // Verify product price
                        WebElement priceElement = item.findElement(productPriceInCart);
                        String actualPriceText = priceElement.getText().trim();
                        logger.info("Actual price text found: '" + actualPriceText + "' for product '" + actualProductName + "'");

                        return actualPriceText.split(" or ")[0].trim().substring(3).replace(",", "");
                    }
                } catch (Exception e) {
                    logger.warning("Error processing cart item " + (i+1) + ": " + e.getMessage());
                }
            }

            if (!productFound) {
                throw new RuntimeException("Product '" + productName + "' not found in cart to get price.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to get product price.", e);
        }
        return "0";
    }

    public void removeProduct(String productName) {
        try {
            List<WebElement> cartItems = driver.findElements(cartItemContainers);

            for (int i = 0; i < cartItems.size(); i++) {
                WebElement item = driver.findElements(cartItemContainers).get(i);
                WebElement nameElement = item.findElement(productNameInCart);
                String actualProductName = nameElement.getText().trim();

                if (actualProductName.contains(productName)) {
                    logger.info("Product '" + productName + "' found. Removing " + productName);

                    WebElement removeButton = item.findElement(removeSelectedProductButton);

                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", removeButton);

                    wait.until(ExpectedConditions.elementToBeClickable(removeButton));

                    Thread.sleep(500);

                    try {
                        removeButton.click();
                        logger.info("Clicked remove button for an item using normal click.");
                    } catch (ElementClickInterceptedException e) {
                        logger.warning("Normal click intercepted, trying JavaScript click.");

                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", removeButton);
                        logger.info("Clicked remove button for an item using JavaScript click.");
                    }

                    wait.until(ExpectedConditions.stalenessOf(removeButton));
                    logger.info("Item removed successfully.");
                    return;
                }
            }
            throw new RuntimeException("Product '" + productName + "' not found in cart.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Thread interrupted while removing product: " + e.getMessage());
            throw new RuntimeException("Failed to remove product due to interruption.", e);
        } catch (Exception e) {
            logger.severe("Failed to remove product: " + e.getMessage());
            throw new RuntimeException("Failed to remove product.", e);
        }
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
    public void verifyCartIsEmpty() {
        logger.info("Verifying if the shopping cart is empty...");
        try {
            WebElement emptyMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(emptyCartMessage));
            String actualMessage = emptyMessageElement.getText().trim();
            String expectedMessage = "Your shopping cart is empty";

            Assert.assertEquals(actualMessage, expectedMessage,
                    "The empty cart message is not as expected.");
            logger.info("Successfully verified: '" + actualMessage + "' is displayed, confirming cart is empty.");

        } catch (TimeoutException e) {
            logger.severe("Timeout: The 'Your shopping cart is empty' message was not found on the page within the specified wait time.");
            Assert.fail("Timeout: The 'Your shopping cart is empty' message was not found on the page.");
        } catch (NoSuchElementException e) {
            logger.severe("No such element: The 'Your shopping cart is empty' message element was not found.");
            Assert.fail("No such element: The 'Your shopping cart is empty' message element was not found.");
        } catch (Exception e) {
            logger.severe("An unexpected error occurred during cart empty verification: " + e.getMessage());
            Assert.fail("An unexpected error occurred during cart empty verification: " + e.getMessage());
        }
    }

    public void proceedToCheckout() {
        try {
            logger.info("Attempting to proceed to checkout...");

            WebElement checkoutElement = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", checkoutElement);

            Thread.sleep(500);

            try {
                checkoutElement.click();
                logger.info("Checkout button clicked successfully using normal click.");
            } catch (ElementClickInterceptedException e) {

                logger.warning("Normal click intercepted, trying JavaScript click for checkout.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkoutElement);
                logger.info("Checkout button clicked successfully using JavaScript click.");
            }

            wait.until(ExpectedConditions.urlContains("checkout"));

            logger.info("Successfully proceeded to checkout page.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Thread interrupted during checkout: " + e.getMessage());
            throw new RuntimeException("Failed to proceed to checkout due to interruption.", e);
        } catch (TimeoutException e) {
            logger.severe("Timeout: Checkout button was not found or checkout page did not load within the specified wait time.");
            throw new RuntimeException("Timeout: Failed to proceed to checkout.", e);
        } catch (Exception e) {
            logger.severe("Failed to proceed to checkout: " + e.getMessage());
            throw new RuntimeException("Failed to proceed to checkout.", e);
        }
    }
}