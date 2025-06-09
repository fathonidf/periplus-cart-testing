package com.periplus.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

public class ProductDetailPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = Logger.getLogger(ProductDetailPage.class.getName());

    // Element Locators
    private final By productLinkInGrid = By.xpath(".//a[contains(@href,'/p/')]");
    private final By addToCartButton = By.xpath("//button[contains(text(),'Add to Cart') or contains(text(),'Add To Cart') or contains(@class,'btn-add-to-cart')]");
    private final By quantityInput = By.xpath("//input[@name='quantity' or @id='input-quantity' or @class='input-number']");
    private final By productPrice = By.xpath("//div[@class='quickview-price']");

    public ProductDetailPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void setQuantity(int quantity) {
        WebElement quantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityInput));
        quantityElement.clear();
        quantityElement.sendKeys(String.valueOf(quantity));
        logger.info("Set product quantity to: " + quantity);
    }

    public void clickAddToCartButton() {
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartBtn);
        logger.info("Add to Cart button clicked.");
        // Anda bisa menambahkan wait untuk konfirmasi penambahan ke keranjang, misal pop-up notifikasi
        wait.withTimeout(Duration.ofSeconds(5)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading-overlay")));
    }

    public String getProductPrice() {
        WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(productPrice));
        logger.info("Getting product price from cart.");
        return priceElement.getText().substring(3).replace(",", "");
    }

    public void clickFirstProductAndAddToCart() {
        List<WebElement> productLinks = driver.findElements(By.xpath("//div[@class='row row-category-grid']//a[contains(@href,'/p/')]"));
        if (productLinks.isEmpty()) {
            throw new IllegalStateException("No products found in the grid to add to cart.");
        }
        WebElement firstProductLink = productLinks.get(0);
        String productHref = firstProductLink.getAttribute("href");
        logger.info("Product found: " + productHref + ". Clicking on it.");

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProductLink);
        logger.info("Navigated to product detail page.");

        clickAddToCartButton();
    }
}