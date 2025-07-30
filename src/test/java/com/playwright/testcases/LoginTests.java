package com.playwright.testcases;


import com.playwright.base.BaseTest;
import com.playwright.base.LoginPage;
import com.playwright.base.ProductsPage;
import com.playwright.base.ElementHandler; // Import ElementHandler
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod; // Import BeforeMethod
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoginTests class contains test cases for the login functionality of SauceDemo.
 * It extends BaseTest to inherit Playwright setup/teardown and Allure integration.
 * This class now uses Page Objects (LoginPage, ProductsPage) that internally
 * leverage the ElementHandler for interactions, and also uses ElementHandler
 * for assertions.
 */
@Feature("Authentication")
@Story("User Login")
public class LoginTests extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(LoginTests.class);
    private ElementHandler elementHandler; // Declare ElementHandler instance
    private LoginPage loginPage; // Declare LoginPage instance
    private ProductsPage productsPage; // Declare ProductsPage instance

    /**
     * This method runs before each test method in this class.
     * It initializes the ElementHandler, LoginPage, and ProductsPage objects
     * to ensure they are fresh for each test, using the 'page' object
     * provided by the BaseTest's @BeforeMethod.
     */

    @BeforeMethod(alwaysRun = true)
    public void setupTestObjects() {

        log.info("Initializing Page Objects and ElementHandler for current test method.");
        elementHandler = new ElementHandler(getPage());
        loginPage = new LoginPage(getPage());
        productsPage = new ProductsPage(getPage());
    }

    /**
     * Test case for successful login with valid credentials.
     * Verifies that the user is redirected to the products page after successful login.
     */
    @Test(description = "Verify successful login with valid credentials",groups = {"smoke"})
    @Description("This test attempts to log in with a standard user and verifies navigation to the products page.")
    public void testSuccessfulLogin() {
        // Retrieve credentials from config.properties
        String username = config.getProperty("standard.username");
        String password = config.getProperty("standard.password");

        loginPage.login(username, password);
        elementHandler.assertTrue(productsPage.isProductsPageDisplayed(), "Products page should be displayed after successful login.");
        elementHandler.assertEquals(productsPage.getProductsPageTitle(), "Products", "Products page title should be 'Products'.");
    }

    /**
     * Test case for failed login with invalid credentials.
     * Verifies that an error message is displayed.
     */
    @Test(description = "Verify failed login with invalid credentials")
    @Description("This test attempts to log in with invalid credentials and verifies that an error message appears.")
    public void testInvalidLogin() {
        log.info("Starting test: testInvalidLogin");

        // Perform login with invalid credentials
        loginPage.login("invalid_user", "wrong_password");
        log.info("Attempted login with invalid_user.");
//        takeScreenshotAndAttach("After Invalid Login Attempt"); // Attach screenshot after invalid login

        // Assert that an error message is displayed using ElementHandler's assertTrue
        elementHandler.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login.");
        // Assert that the error message text is correct using ElementHandler's assertEquals
        elementHandler.assertEquals(loginPage.getErrorMessageText(), "Epic sadface: Username and password do not match any user in this service",
                "Error message text is incorrect for invalid login.");
        log.info("Verified error message for invalid login.");
//        takeScreenshotAndAttach("Invalid Login Error Message Verified"); // Attach screenshot after verification
    }

    /**
     * Test case for failed login with locked out user.
     * Verifies that a specific error message for locked out user is displayed.
     */
    @Test(description = "Verify failed login with locked out user")
    @Description("This test attempts to log in with a locked out user and verifies the specific error message.")
    public void testLockedOutUserLogin() {
        log.info("Starting test: testLockedOutUserLogin");

        // Perform login with locked out user credentials
        loginPage.login("locked_out_user", "secret_sauce");
        log.info("Attempted login with locked_out_user.");
//        takeScreenshotAndAttach("After Locked Out User Login Attempt"); // Attach screenshot after locked out login

        // Assert that the specific error message for locked out user is displayed using ElementHandler's assertTrue
        elementHandler.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for locked out user.");
        // Assert that the error message text is correct using ElementHandler's assertEquals
        elementHandler.assertEquals(loginPage.getErrorMessageText(), "Epic sadface: Sorry, this user has been locked out.",
                "Incorrect error message for locked out user.");
        log.info("Verified error message for locked out user.");
//        takeScreenshotAndAttach("Locked Out User Error Message Verified"); // Attach screenshot after verification
    }

    /**
     * Test case to demonstrate screenshot on failure.
     * This test is intentionally designed to fail.
     */
    @Test(description = "Demonstrate screenshot on failure (intentional failure)")
    @Description("This test is designed to intentionally fail to demonstrate the automatic screenshot capture on failure.")
    public void testScreenshotOnFailure() {
        log.info("Starting test: testScreenshotOnFailure (designed to fail)");

        loginPage.login("standard_user", "secret_sauce");
        log.info("Logged in as standard_user.");
//        takeScreenshotAndAttach("Before Intentional Failure"); // Attach screenshot before failure

        // Intentionally make an assertion that will fail using ElementHandler's assertEquals
        elementHandler.assertEquals(productsPage.getProductsPageTitle(), "NonExistentTitle",
                "This assertion is designed to fail to trigger a screenshot.");
        log.info("Assertion completed (this line should not be reached if assertion fails).");
        // Screenshot on failure will be handled by BaseTest's @AfterMethod
    }

    /**
     * Test case to add a single product to the cart and verify the cart count.
     */
    @Test(description = "To verify adding a single product to the cart and checking the count.")
    @Description("This test logs in a standard user, adds 'Sauce Labs Backpack' to the cart, and verifies the cart badge count.")
    public void testAddProductToCart() {
        log.info("Starting test: testAddProductToCart");

        // 1. Log in as a standard user
        loginPage.login("standard_user", "secret_sauce");
        log.info("Logged in as standard_user for product add test.");
//        takeScreenshotAndAttach("After Login to Add Product"); // Screenshot after login

        // Assert that we are on the products page
        elementHandler.assertTrue(productsPage.isProductsPageDisplayed(), "Should be on products page after login.");
//        takeScreenshotAndAttach("On Products Page Before Add"); // Screenshot on products page

        // 2. Add a product to the cart
        String productName = "Sauce Labs Backpack";
        productsPage.addItemToCart(productName);
        log.info("Added '{}' to cart.", productName);
//        takeScreenshotAndAttach("After Adding " + productName); // Screenshot after adding product

        // 3. Verify the shopping cart count
        int expectedCartCount = 1;
        int actualCartCount = productsPage.getShoppingCartItemCount();
        elementHandler.assertEquals(String.valueOf(actualCartCount), String.valueOf(expectedCartCount),
                "Shopping cart count mismatch after adding one product.");
        log.info("Verified shopping cart count: {}. Test completed.", actualCartCount);
//        takeScreenshotAndAttach("After Cart Count Verification"); // Screenshot after verification
    }
}

