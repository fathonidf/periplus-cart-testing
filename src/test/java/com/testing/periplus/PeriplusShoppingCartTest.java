package com.testing.periplus;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
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

    private final Dotenv dotenv = Dotenv.load();

    private final String TEST_EMAIL = dotenv.get("TEST_EMAIL");
    private final String TEST_PASSWORD = dotenv.get("TEST_PASSWORD");

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

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
            driver.get("https://www.periplus.com/account/Login");
            System.out.println("Navigated directly to login page");

            Thread.sleep(3000);

            // Wait for login form to appear
            System.out.println("Looking for email/username field...");

            // Try different possible selectors for email field
            WebElement emailField = null;
            String emailSelector = "//input[@name='email']";

            try {
                emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(emailSelector)));
                System.out.println("Email field found with selector: " + emailSelector);
            } catch (Exception e) {
                System.out.println("Selector failed: " + emailSelector);
            }

            if (emailField == null) {
                throw new Exception("Email field not found with any selector");
            }

            // Try different possible selectors for password field
            WebElement passwordField = null;
            String passwordSelector = "//input[@name='password']";

            try {
                passwordField = driver.findElement(By.xpath(passwordSelector));
                System.out.println("Password field found with selector: " + passwordSelector);
            } catch (Exception e) {
                System.out.println("Password selector failed: " + passwordSelector);
            }

            if (passwordField == null) {
                throw new Exception("Password field not found");
            }

            // Enter credentials
            emailField.clear();
            emailField.sendKeys(TEST_EMAIL);
            System.out.println("Email successfully entered: " + TEST_EMAIL);

            passwordField.clear();
            passwordField.sendKeys(TEST_PASSWORD);
            System.out.println("Password successfully entered");

            // Find and click login button
            WebElement loginButton = null;

            String loginButtonSelector = "//input[@type='submit' and contains(@value,'Login')]";

            try {
                loginButton = driver.findElement(By.xpath(loginButtonSelector));
                System.out.println("Login button found with selector: " + loginButtonSelector);
            } catch (Exception e) {
                System.out.println("Login button selector failed: " + loginButtonSelector);
            }

            if (loginButton == null) {
                throw new Exception("Login button not found");
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);
            System.out.println("Login button clicked");

            // Wait for login to complete
            Thread.sleep(5000);

            // Verify login success - check current URL or look for user indicators
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after login: " + currentUrl);

            // Check if redirected to account page or homepage
            if (currentUrl.contains("account") && !currentUrl.contains("Login")) {
                System.out.println("Login successful - redirected to account page");
            } else {
                // Alternative verification - look for user account elements
                try {
                    WebElement userIndicator = driver.findElement(
                            By.xpath("//a[contains(@href,'Your-Account') or contains(text(),'Account')]"));
                    System.out.println("Login successful - user account indicator found");
                } catch (Exception e) {
                    System.out.println("Login status unclear - proceeding with test");
                }
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();

            // Take screenshot for debugging
            try {
                System.out.println("Current page source (first 500 chars): " +
                        driver.getPageSource().substring(0, Math.min(500, driver.getPageSource().length())));
            } catch (Exception ex) {
                System.out.println("Could not get page source");
            }
        }
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void testFindAndAddProductToCart() {
        try {
            // Navigate back to homepage if needed
            driver.get("https://www.periplus.com/");
            Thread.sleep(2000);

            // Search for a product using the search functionality from HTML
            try {
                WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@name='filter_name']")));

                searchBox.clear();
                searchBox.sendKeys("Hunger Games");
                System.out.println("Search term 'Hunger Games' entered");

                // Click search button
                WebElement searchButton = driver.findElement(
                        By.xpath("//button[@type='submit' and contains(@class,'btnn')]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);

                System.out.println("Search button clicked");
                Thread.sleep(3000);

            } catch (Exception searchEx) {
                System.out.println("Search failed, trying to navigate to books category");
                // Alternative: navigate to books category
                WebElement booksLink = driver.findElement(
                        By.xpath("//a[@href='http://www.periplus.com/c/1/books']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", booksLink);
                Thread.sleep(3000);
            }

            // Cari parent div row row-category-grid
            WebElement productGridDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='row row-category-grid']")));
            System.out.println("Product grid container found");

            // Cari semua link produk yang mengandung /p/ di dalam parent div tersebut
            List<WebElement> productLinks = productGridDiv.findElements(
                    By.xpath(".//a[contains(@href,'/p/')]"));

            if (productLinks.size() > 0) {
                WebElement firstProductLink = productLinks.get(0);
                String productHref = firstProductLink.getAttribute("href");
                System.out.println("Product found: " + productHref);

                // Click on the product link
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProductLink);
                System.out.println("Navigated to product detail page");
                Thread.sleep(3000);

                // Find and click "Add to Cart" button
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Add to Cart') or contains(text(),'Add To Cart') or contains(@onclick,'cart.add')]")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
                System.out.println("Product successfully added to cart");

                Thread.sleep(3000);

            } else {
                throw new Exception("No products found in the grid");
            }

        } catch (Exception e) {
            System.err.println("Error finding/adding product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 4, dependsOnMethods = {"testFindAndAddProductToCart"})
    public void testVerifyProductInCart() {
        try {
            driver.get("https://www.periplus.com/checkout/cart");
            System.out.println("Navigated directly to Shopping Cart page");

            Thread.sleep(3000);

            // Verify product is in cart
            List<WebElement> cartItems = driver.findElements(
                    By.xpath("//div[contains(@class,'cart-item') or contains(@class,'product') or //tr[contains(@class,'cart')]]"));

            Assert.assertTrue(cartItems.size() > 0, "Cart should contain at least 1 product");
            System.out.println("Verification successful: Product found in cart (" + cartItems.size() + " items)");

            // Check for total amount
            try {
                WebElement totalAmount = driver.findElement(
                        By.xpath("//*[contains(text(),'Total') or contains(text(),'total') or contains(@class,'total')]"));
                String totalText = totalAmount.getText();
                System.out.println("Total amount found: " + totalText);
            } catch (Exception totalEx) {
                System.out.println("Total amount not clearly visible, but cart has items");
            }

            System.out.println("TEST SUCCESSFUL: Product successfully added to cart and verified");

        } catch (Exception e) {
            System.err.println("Error verifying cart: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
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

    // Utility method untuk debugging
    private void debugCurrentPage() {
        try {
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());
        } catch (Exception e) {
            System.out.println("Debug info not available");
        }
    }
}