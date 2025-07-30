package com.playwright.testcases;

import com.playwright.base.BaseTest;
import com.playwright.base.LoginPage;
import com.playwright.base.ProductsPage;
import com.playwright.base.ElementHandler;
import com.playwright.dataprovider.SwagLabsDataProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SwagLabsE2ETest class contains end-to-end test cases for the SauceDemo application.
 * It extends BaseTest to inherit Playwright setup/teardown and Allure integration.
 * This class uses Page Objects (LoginPage, ProductsPage) that internally
 * leverage the ElementHandler for interactions and assertions.
 */
@Feature("End-to-End User Journey")
@Story("Login and Product Purchase Flow")
public class SwagLabsE2ETest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(SwagLabsE2ETest.class);
    private ElementHandler elementHandler;
    private LoginPage loginPage;
    private ProductsPage productsPage;


    @BeforeMethod(alwaysRun = true)
    public void setupTestObjects() {
        log.info("Initializing Page Objects and ElementHandler for current E2E test method.");
        elementHandler = new ElementHandler(getPage());
        loginPage = new LoginPage(getPage());
        productsPage = new ProductsPage(getPage());
    }

    /**
     * End-to-End Test Case: Verify successful login and adding a product to the cart.
     * This test simulates a complete user journey from login to adding an item to the cart.
     */
    @Test(description = "User Journey: Verify successful login and adding a product to cart E2E", groups = {"sanity"},
            dataProvider = "product-to-add", dataProviderClass = SwagLabsDataProvider.class) // Added dataProvider
    @Description("This end-to-end test logs in a standard user, verifies navigation to the products page, " +
            "adds a specified product to the cart, and verifies the shopping cart badge count.")
    public void testLoginAndAddProductE2E(String productName) { // Added productName parameter
        log.info("Starting E2E test: testLoginAndAddProductE2E for product: {}", productName);

        // Retrieve credentials from config.properties
        String username = config.getProperty("standard.username");
        String password = config.getProperty("standard.password");

        // Step 1: Perform Login
        log.info("Performing login with user: {}.", username);
        loginPage.login(username, password); // Use credentials from config.properties

        // Step 2: Verify successful login (landing on Products Page)
        elementHandler.assertTrue(productsPage.isProductsPageDisplayed(), "Login Failed: Products page not displayed after login!");
        elementHandler.assertEquals(productsPage.getProductsPageTitle(), "Products", "Login Failed: Products page title mismatch!");
        log.info("Successfully logged in and verified products page.");

        // Step 3: Add a product to the cart
        productsPage.addItemToCart(productName); // Use productName from data provider
        log.info("Added '{}' to cart.", productName);

        // Step 4: Verify the shopping cart count
        int expectedCartCount = 1;
        int actualCartCount = productsPage.getShoppingCartItemCount();
        elementHandler.assertEquals(String.valueOf(actualCartCount), String.valueOf(expectedCartCount),
                "Shopping cart count mismatch after adding one product.");
        log.info("Verified shopping cart count: {}. E2E Test completed successfully.", actualCartCount);
    }



}

