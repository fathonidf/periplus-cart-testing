package com.periplus.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.util.logging.Logger;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = Logger.getLogger(LoginPage.class.getName());

    // Element Locators
    private final By emailField = By.xpath("//input[@name='email']");
    private final By passwordField = By.xpath("//input[@name='password']");
    private final By loginButton = By.xpath("//input[@type='submit' and contains(@value,'Login')]");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void navigateToLoginPage() {
        driver.get("https://www.periplus.com/account/Login");
        logger.info("Navigated to Login Page.");
    }

    public void performLogin(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(email);
        logger.info("Email entered.");

        driver.findElement(passwordField).sendKeys(password);
        logger.info("Password entered.");

        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
        logger.info("Login button clicked.");
    }
}