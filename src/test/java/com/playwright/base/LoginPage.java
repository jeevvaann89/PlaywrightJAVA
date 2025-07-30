package com.playwright.base;


import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.playwright.base.ElementHandler; // New import for ElementHandler
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;

/**
 * LoginPage class represents the SauceDemo login page.
 * It contains locators for elements on the login page and methods to interact with them.
 * Interactions now use the ElementHandler for reusability, logging, and error handling.
 */
public class LoginPage extends BaseTest{

    private final Page page;
    private final ElementHandler elementHandler; // Declare ElementHandler instance
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    // Locators for elements on the Login Page
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    /**
     * Constructor for LoginPage.
     * Initializes the Page object, ElementHandler, and defines locators for page elements.
     * @param page The Playwright Page object associated with the current browser tab.
     */
    public LoginPage(Page page) {
        this.page = page;
        this.elementHandler = new ElementHandler(page); // Initialize ElementHandler
        this.usernameInput = page.locator("#user-name");
        this.passwordInput = page.locator("#password");
        this.loginButton = page.locator("#login-button");
        this.errorMessage = page.locator("[data-test='error']");
        log.info("LoginPage object initialized with ElementHandler.");
    }

    /**
     * Enters the username into the username input field using ElementHandler.
     * @param username The username to enter.
     */
    @Step("Enter username: {username}")
    public void enterUsername(String username) {
        elementHandler.enterText(usernameInput, username, "Username Input Field");
    }

    /**
     * Enters the password into the password input field using ElementHandler.
     * @param password The password to enter.
     */
    @Step("Enter password: {password}")
    public void enterPassword(String password) {
        elementHandler.enterText(passwordInput, password, "Password Input Field");
    }

    /**
     * Clicks the login button using ElementHandler.
     */
    @Step("Click Login button")
    public void clickLoginButton() {
        elementHandler.clickElement(loginButton, "Login Button");
    }

    /**
     * Performs the login action by entering username, password, and clicking login button.
     * @param username The username for login.
     * @param password The password for login.
     */
    @Step("Perform login with username: {username} and password: {password}")
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        log.info("Login action performed.");
        takeScreenshotAndAttach("After Login - Successful");
    }

    /**
     * Checks if the login error message is displayed using ElementHandler.
     * @return true if the error message is visible, false otherwise.
     */
    @Step("Verify error message is displayed")
    public boolean isErrorMessageDisplayed() {
        return elementHandler.isVisible(errorMessage, "Login Error Message");
    }

    /**
     * Gets the text of the login error message.
     * @return The text of the error message.
     */
    @Step("Get error message text")
    public String getErrorMessageText() {
        // Direct text content retrieval is fine here as it's not an interaction
        String text = errorMessage.textContent();
        log.info("Error message text: " + text);
        return text;
    }
}
