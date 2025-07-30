package com.playwright.base; // Changed package to com.playwright.base

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.playwright.base.ElementHandler; // Import the new ElementHandler
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.qameta.allure.Step;

/**
 * ProductsPage class represents the SauceDemo products page (inventory page).
 * It contains locators for elements on the products page and methods to interact with them.
 * Interactions now use the ElementHandler for reusability, logging, and error handling.
 */
public class ProductsPage extends BaseTest  {

    private final Page page;
    private final ElementHandler elementHandler; // Declare ElementHandler instance
    private static final Logger log = LoggerFactory.getLogger(ProductsPage.class);

    // Locators for elements on the Products Page
    private final Locator productsPageTitle;
    private final Locator addToCartButton; // General locator for add to cart buttons
    private final Locator shoppingCartLink;

    /**
     * Constructor for ProductsPage.
     * Initializes the Page object, ElementHandler, and defines locators for page elements.
     * @param page The Playwright Page object associated with the current browser tab.
     */
    public ProductsPage(Page page) {
        this.page = page;
        this.elementHandler = new ElementHandler(page); // Initialize ElementHandler
        this.productsPageTitle = page.locator(".title");
        this.addToCartButton = page.locator("[id^='add-to-cart']"); // Locates all 'add to cart' buttons
        this.shoppingCartLink = page.locator(".shopping_cart_link");
        log.info("ProductsPage object initialized with ElementHandler.");
    }

    /**
     * Checks if the Products page title is displayed using ElementHandler, indicating successful navigation.
     * @return true if the products page title is visible, false otherwise.
     */
    @Step("Verify Products page title is displayed")
    public boolean isProductsPageDisplayed() {
        return elementHandler.isVisible(productsPageTitle, "Products Page Title");
    }

    /**
     * Gets the text of the Products page title.
     * @return The text of the products page title.
     */
    @Step("Get Products page title text")
    public String getProductsPageTitle() {
        // Direct text content retrieval is fine here as it's not an interaction
        String title = productsPageTitle.textContent();
        log.info("Products page title: " + title);
        takeScreenshotAndAttach("On Products Page - Successful Login"); // Attach screenshot on products page
        return title;
    }

    /**
     * Adds a specific item to the cart by its name using ElementHandler.
     * This assumes the item name is visible and clickable near an 'Add to cart' button.
     * For SauceDemo, each 'Add to cart' button has an ID like 'add-to-cart-sauce-labs-backpack'.
     *
     * @param itemName The visible name of the item to add to cart (e.g., "Sauce Labs Backpack").
     */
    @Step("Add item '{itemName}' to cart")
    public void addItemToCart(String itemName) {
        // Construct the specific locator for the "Add to cart" button for the given item.
        String buttonId = "add-to-cart-" + itemName.toLowerCase().replace(" ", "-");
        Locator itemAddToCartButton = page.locator("#" + buttonId);

        elementHandler.clickElement(itemAddToCartButton, "Add to Cart Button for " + itemName);
    }

    /**
     * Clicks on the shopping cart icon using ElementHandler.
     */
    @Step("Click shopping cart link")
    public void clickShoppingCart() {
        elementHandler.clickElement(shoppingCartLink, "Shopping Cart Link");
    }

    /**
     * Gets the number of items currently in the shopping cart as indicated by the badge.
     * @return The number of items in the cart, or 0 if no badge is present.
     */
    @Step("Get shopping cart item count")
    public int getShoppingCartItemCount() {
        Locator cartBadge = page.locator(".shopping_cart_badge");
        if (elementHandler.isVisible(cartBadge, "Shopping Cart Badge")) {
            int count = Integer.parseInt(cartBadge.textContent());
            log.info("Shopping cart has " + count + " items.");
            return count;
        }
        log.info("Shopping cart is empty (no badge found).");
        return 0;
    }
}
