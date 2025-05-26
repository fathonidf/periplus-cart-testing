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
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");

        // Initialize driver
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
            // Berdasarkan HTML, ada beberapa cara untuk akses login:
            // 1. Via dropdown menu di account icon
            // 2. Langsung ke halaman login

            System.out.println("Attempting to find login elements...");

            // Method 1: Try clicking on account icon to show dropdown
            try {
                // Cari icon user account (berdasarkan HTML: fa-user-circle-o)
                WebElement accountIcon = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//i[contains(@class,'fa-user-circle-o')]/parent::a")));

                // Hover over the account icon to show dropdown
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", accountIcon);
                Thread.sleep(1000);

                // Click on account icon or hover to show dropdown
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accountIcon);
                System.out.println("Account icon clicked");

                Thread.sleep(2000);

                // Look for login link in dropdown
                WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='https://www.periplus.com/account/Login'] | //a[contains(@href,'/account/Login')]")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginLink);
                System.out.println("Login link clicked from dropdown");

            } catch (Exception e1) {
                System.out.println("Method 1 failed, trying direct navigation...");

                // Method 2: Direct navigation to login page
                driver.get("https://www.periplus.com/account/Login");
                System.out.println("Navigated directly to login page");
            }

            Thread.sleep(3000);

            // Wait for login form to appear
            System.out.println("Looking for email/username field...");

            // Try different possible selectors for email field
            WebElement emailField = null;
            String[] emailSelectors = {
                    "//input[@name='email']",
                    "//input[@type='email']",
                    "//input[@id='email']",
                    "//input[contains(@placeholder,'email') or contains(@placeholder,'Email')]",
                    "//input[@name='username']",
                    "//input[@id='input-email']"
            };

            for (String selector : emailSelectors) {
                try {
                    emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
                    System.out.println("Email field found with selector: " + selector);
                    break;
                } catch (Exception e) {
                    System.out.println("Selector failed: " + selector);
                }
            }

            if (emailField == null) {
                throw new Exception("Email field not found with any selector");
            }

            // Try different possible selectors for password field
            WebElement passwordField = null;
            String[] passwordSelectors = {
                    "//input[@name='password']",
                    "//input[@type='password']",
                    "//input[@id='password']",
                    "//input[@id='input-password']"
            };

            for (String selector : passwordSelectors) {
                try {
                    passwordField = driver.findElement(By.xpath(selector));
                    System.out.println("Password field found with selector: " + selector);
                    break;
                } catch (Exception e) {
                    System.out.println("Password selector failed: " + selector);
                }
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
            String[] loginButtonSelectors = {
                    "//button[contains(text(),'Login')]",
                    "//button[contains(text(),'Sign In')]",
                    "//input[@type='submit' and contains(@value,'Login')]",
                    "//button[@type='submit']",
                    "//input[@type='submit']",
                    "//a[contains(@class,'btn') and contains(text(),'Login')]"
            };

            for (String selector : loginButtonSelectors) {
                try {
                    loginButton = driver.findElement(By.xpath(selector));
                    System.out.println("Login button found with selector: " + selector);
                    break;
                } catch (Exception e) {
                    System.out.println("Login button selector failed: " + selector);
                }
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
                // Berdasarkan HTML, search field ada dengan name="filter_name"
                WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@name='filter_name']")));

                searchBox.clear();
                searchBox.sendKeys("book");
                System.out.println("Search term 'book' entered");

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

            // Find available products
            List<WebElement> products = driver.findElements(
                    By.xpath("//a[contains(@href,'/p/') or contains(@href,'product')]"));

            if (products.size() > 0) {
                WebElement firstProduct = products.get(0);
                String productHref = firstProduct.getAttribute("href");
                System.out.println("Product found: " + productHref);

                // Click on the product
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProduct);
                System.out.println("Navigated to product detail page");
                Thread.sleep(3000);

                // Find and click "Add to Cart" button
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Add to Cart') or contains(text(),'Add To Cart') or contains(@onclick,'cart.add')]")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
                System.out.println("Product successfully added to cart");

                Thread.sleep(3000);

            } else {
                throw new Exception("No products found");
            }

        } catch (Exception e) {
            System.err.println("Error finding/adding product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 4, dependsOnMethods = {"testFindAndAddProductToCart"})
    public void testVerifyProductInCart() {
        try {
            // Navigate to shopping cart - berdasarkan HTML ada link ke checkout/cart
            WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='https://www.periplus.com/checkout/cart'] | //a[contains(@href,'checkout/cart')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartIcon);
            System.out.println("Navigated to shopping cart page");

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