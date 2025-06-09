
package com.testing.periplus;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Dotenv dotenv = Dotenv.load();
    protected final String TEST_EMAIL = dotenv.get("TEST_EMAIL");
    protected final String TEST_PASSWORD = dotenv.get("TEST_PASSWORD");
    protected static final Logger logger = Logger.getLogger(BaseTest.class.getName());

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Chrome browser successfully opened");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            logger.info("Test completed. Browser will close in 3 seconds...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Thread interrupted during teardown: " + e.getMessage(), e);
            }
            driver.quit();
            logger.info("Browser successfully closed");
        }
    }

    /**
     * Logs a severe message and fails the TestNG test.
     * @param message The error message.
     * @param e The exception that occurred.
     */
    protected void logAndFail(String message, Exception e) {
        logger.log(Level.SEVERE, message + " Error: " + e.getMessage(), e);
        Assert.fail(message + " Error: " + e.getMessage());
    }
}