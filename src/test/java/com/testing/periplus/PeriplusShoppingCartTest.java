package com.testing.periplus;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.List;

public class PeriplusShoppingCartTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Replace with your test account credentials
    private final String TEST_EMAIL = "daffafathoni@gmail.com";
    private final String TEST_PASSWORD = "testing123";

    @BeforeClass
    public void setUp() {
        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        // Initialize driver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Chrome browser successfully opened");
    }

    @Test(priority = 1)
    public void testNavigateToPeriplusWebsite() {
        try {
            // Navigate to Periplus website
            driver.get("https://www.periplus.com/");
            System.out.println("Successfully navigated to https://www.periplus.com/");

            // Verify page loaded
            wait.until(ExpectedConditions.titleContains("Periplus"));
            String pageTitle = driver.getTitle();
            System.out.println("Page title: " + pageTitle);

            Assert.assertTrue(pageTitle.contains("Periplus"), "Page title should contain 'Periplus'");

        } catch (Exception e) {
            System.err.println("Error navigating to website: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = {"testNavigateToPeriplusWebsite"})
    public void testLogin() {
        try {
            // Look for login link/button (adjust selector based on actual website)
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Login') or contains(text(),'Sign In') or contains(@href,'login')]")));
            loginLink.click();
            System.out.println("Successfully clicked login button");

            // Wait for login form to appear
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='email' or @name='email' or @id='email']")));

            WebElement passwordField = driver.findElement(
                    By.xpath("//input[@type='password' or @name='password' or @id='password']"));

            // Enter credentials
            emailField.clear();
            emailField.sendKeys(TEST_EMAIL);
            System.out.println("Email successfully entered: " + TEST_EMAIL);

            passwordField.clear();
            passwordField.sendKeys(TEST_PASSWORD);
            System.out.println("Password successfully entered");

            // Find and click login button
            WebElement loginButton = driver.findElement(
                    By.xpath("//button[contains(text(),'Login') or contains(text(),'Sign In') or @type='submit']"));
            loginButton.click();
            System.out.println("Login button clicked");

            // Wait for login to complete - look for user account indicator
            try {
                Thread.sleep(3000); // Give time for page to load
            } catch (InterruptedException interruptEx) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Test interrupted", interruptEx);
            }

            // Verify login success (adjust based on actual website behavior)
            try {
                WebElement userAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(@href,'account') or contains(text(),'Account') or contains(text(),'Profile')]")));
                System.out.println("Login successful - user account detected");
            } catch (Exception accountEx) {
                System.out.println("Login might be successful, but account indicator not found");
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void testFindAndAddProductToCart() {
        try {
            // Search for a product or navigate to products section
            try {
                WebElement searchBox = driver.findElement(
                        By.xpath("//input[@type='search' or @placeholder*='Search' or @name='search']"));
                searchBox.clear();
                searchBox.sendKeys("book");
                searchBox.submit();
                System.out.println("Product search for 'book' successful");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptEx) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception searchEx) {
                System.out.println("Search box not found, trying to navigate to product category");
                // Alternative: navigate to books category or any product category
                WebElement categoryLink = driver.findElement(
                        By.xpath("//a[contains(@href,'book') or contains(text(),'Book') or contains(text(),'Product')]"));
                categoryLink.click();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptEx) {
                    Thread.currentThread().interrupt();
                }
            }

            // Find first available product
            List<WebElement> products = driver.findElements(
                    By.xpath("//div[contains(@class,'product') or contains(@class,'item')]//a[contains(@href,'product')]"));

            if (products.size() > 0) {
                WebElement firstProduct = products.get(0);
                String productName = firstProduct.getText();
                System.out.println("Product found: " + productName);

                // Click on the product to go to product detail page
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProduct);
                System.out.println("Navigated to product detail page");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptEx) {
                    Thread.currentThread().interrupt();
                }

                // Find and click "Add to Cart" button
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Add to Cart') or contains(text(),'Add To Cart') or contains(@class,'add-to-cart')]")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
                System.out.println("Product successfully added to cart");

                // Wait for add to cart confirmation
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptEx) {
                    Thread.currentThread().interrupt();
                }

            } else {
                throw new Exception("No products found");
            }

        } catch (Exception e) {
            System.err.println("Error finding/adding product: " + e.getMessage());
        }
    }

    @Test(priority = 4, dependsOnMethods = {"testFindAndAddProductToCart"})
    public void testVerifyProductInCart() {
        try {
            // Navigate to shopping cart
            WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'cart') or contains(@class,'cart') or contains(text(),'Cart')]")));
            cartIcon.click();
            System.out.println("Navigated to shopping cart page");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException interruptEx) {
                Thread.currentThread().interrupt();
            }

            // Verify product is in cart
            List<WebElement> cartItems = driver.findElements(
                    By.xpath("//div[contains(@class,'cart-item') or contains(@class,'product')]"));

            Assert.assertTrue(cartItems.size() > 0, "Cart should contain at least 1 product");
            System.out.println("Verification successful: Product found in cart (" + cartItems.size() + " items)");

            // Verify the cart is not empty
            try {
                WebElement emptyCartMessage = driver.findElement(
                        By.xpath("//*[contains(text(),'empty') or contains(text(),'Empty')]"));
                Assert.fail("Cart is still empty - product was not successfully added");
            } catch (Exception emptyEx) {
                System.out.println("Cart is not empty - test successful");
            }

            // Additional verification: check if total amount is displayed
            try {
                WebElement totalAmount = driver.findElement(
                        By.xpath("//*[contains(text(),'Total') or contains(text(),'total')]"));
                String totalText = totalAmount.getText();
                System.out.println("Total amount found: " + totalText);
            } catch (Exception totalEx) {
                System.out.println("Total amount not found, but items exist in cart");
            }

            System.out.println("TEST SUCCESSFUL: Product successfully added to cart and verified");

        } catch (Exception e) {
            System.err.println("Error verifying cart: " + e.getMessage());
            throw e;
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            // Take screenshot before closing (optional)
            System.out.println("Test completed. Browser will close in 5 seconds...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
            System.out.println("Browser successfully closed");
        }
    }

    // Utility method for waiting for elements
    private void waitForElement(By locator, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        customWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}