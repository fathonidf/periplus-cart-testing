package com.periplus.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.util.logging.Logger;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = Logger.getLogger(HomePage.class.getName());

    // Element Locators
    private final By searchBox = By.xpath("//input[@name='filter_name']");
    private final By searchButton = By.xpath("//button[@type='submit' and contains(@class,'btnn')]");
    private final By productGridDiv = By.xpath("//div[@class='row row-category-grid']");


    public HomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void navigateToHomePage() {
        driver.get("https://www.periplus.com/");
        wait.until(ExpectedConditions.titleContains("Periplus"));
        logger.info("Navigated to Homepage.");
    }

    public void searchForProduct(String productName) {
        WebElement searchBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        searchBoxElement.clear();
        searchBoxElement.sendKeys(productName);
        logger.info("Search term '" + productName + "' entered.");

        WebElement searchBtnElement = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBtnElement);
        logger.info("Search button clicked.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(productGridDiv));
        logger.info("Product grid container found after search.");
    }
}