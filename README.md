

# Periplus Shopping Cart Test Automation

This project contains automated tests for the shopping cart functionality of the Periplus e-commerce website, developed using Selenium WebDriver, TestNG, and the Page Object Model (POM) design pattern.

## Author

* **Name:** Daffa Mohamad Fathoni
* **University:** Computer Science - University of Indonesia
* **Task:** Technical Task - Testing Shopping Cart Functionality on Periplus

## Project Structure

The project follows a standard Maven structure, leveraging the Page Object Model for maintainability and scalability.

```
periplus-automation/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── periplus/
│   │               └── pages/           # Page Objects representing UI pages/components
│   │                   ├── HomePage.java
│   │                   ├── LoginPage.java
│   │                   ├── ProductDetailPage.java
│   │                   └── ShoppingCartPage.java
│   │                   └── ... (Other relevant Page Objects)
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── testing/
│                   └── periplus/        # Test classes
│                       ├── BaseTest.java        # Base class for common setup/teardown
│                       ├── CartCheckoutTest.java
│                       ├── CartFromEmptyTest.java
│                       └── CartWithOneProductTest.java
│
├── .env                             # Environment variables (e.g., TEST_EMAIL, TEST_PASSWORD)
├── pom.xml                          # Maven Project Object Model (dependencies)
├── README.md                        # This file
└── testng.xml                       # TestNG test suite configuration
```

## Technologies Used

* **Selenium WebDriver:** For browser automation.
* **TestNG:** As the testing framework.
* **WebDriverManager:** For automatic WebDriver setup.
* **Java-dotenv:** For managing environment variables (e.g., login credentials).
* **Apache Commons IO:** For utility operations like taking screenshots.
* **Java (JDK 11+)**

## Setup and Running Tests

1.  **Clone the Repository:**
    ```bash
    git clone <this-repo-url>
    cd periplus-automation
    ```

2.  **Configure Environment Variables:**
    * Create a file named `.env` in the root directory of the project.
    * Add Periplus account credentials:
        ```
        TEST_EMAIL=your_email@example.com
        TEST_PASSWORD=your_password
        ```
        (Replace with valid credentials for testing purposes.)

3.  **Install Dependencies:**
    * Ensure you have Maven (or Gradle) installed.
    * Navigate to the project root directory and run:
        ```bash
        mvn clean install # For Maven
        # Or: gradle build # For Gradle
        ```
        This will download all necessary dependencies specified in `pom.xml`.

4.  **Run the Tests:**
    * You can run the tests using the `testng.xml` suite file:
        ```bash
        mvn test -DsuiteXmlFile=testng.xml # For Maven
        # Or run directly from your IDE (IntelliJ IDEA, Eclipse) by right-clicking testng.xml and selecting "Run 'testng.xml'"
        ```

## Test Case Specifications

This project implements automated tests for the following shopping cart functionalities:

* **TC_CART_001: Add Single Product to Cart**
    * Verify user can add one product, and cart displays correct name, price, and quantity (1).
* **TC_CART_002: Add Multiple Quantities of Same Product**
    * Verify user can add multiple units of the same product, and total price reflects quantity.
* **TC_CART_003: Add Multiple Different Products**
    * Verify user can add distinct products, and all appear with correct details and sum up to the total.
* **TC_CART_004: Update Product Quantity in Cart**
    * Verify user can change product quantity directly in the cart, and total price recalculates.
* **TC_CART_005: Remove Product from Cart**
    * Verify user can remove a product, cart content updates, and total price recalculates.
* **TC_CART_006: Empty Cart Scenario**
    * Verify cart displays "Your shopping cart is empty" message when no items are present.
* **TC_CART_007: Cart Persistence After Re-login**
    * Verify products added to cart remain after logging out and then logging back in.
* **TC_CART_008: Proceed to Checkout**
    * Verify user can successfully navigate from the shopping cart page to the checkout process.

## Design Patterns and Best Practices

* **Page Object Model (POM):**
    * Locators and interactions with UI elements are encapsulated within dedicated "Page" classes (`HomePage`, `LoginPage`, `ShoppingCartPage`, etc.). This approach significantly enhances test **maintainability** by centralizing UI element definitions. If a UI element changes, only its definition in the respective Page Object needs updating, not every test case using it.
    * **Reference:** [Selenium Documentation - Page Object Models](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)

* **Test Isolation (`@AfterMethod` Cleanup):**
    * An `@AfterMethod` hook (`removeAllProductFromCartEachTestCase()`) is implemented in `CartTest` to ensure that the shopping cart is cleared after each individual test method execution. This guarantees that each test starts with a clean state, preventing inter-test dependencies and making tests more reliable.

* **Logging and Error Handling:**
    * Standard Java `Logger` is used for informative logging, providing clear execution steps and debugging messages.
    * Robust `try-catch` blocks are implemented with screenshot capture (`takeScreenshot()`) on failures, aiding in quick diagnosis of test issues.

* **Dynamic Locators and Utility Methods:**
    * Strategic use of XPath locators (`contains(@class,'...')`, `text()='...'`) to target elements reliably.
    * Utility methods (e.g., `formatPriceToRupiah()`) are provided in `BaseTest` or `Page` classes for common operations, promoting code reusability and clarity.

## References

* **Page Object Models:**
    * [Selenium Documentation: Page Object Models](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
    * [Martin Fowler: PageObject](https://martinfowler.com/bliki/PageObject.html)
* **Selenium Locators:**
    * [Selenium Documentation: Locating Elements](https://www.selenium.dev/documentation/webdriver/elements/locators/)
    * [Selenium Documentation: Finding Elements](https://www.selenium.dev/documentation/webdriver/elements/finders/)
* **Selenium Interactions:**
    * [Selenium Documentation: User Interactions](https://www.selenium.dev/documentation/webdriver/elements/interactions/)

