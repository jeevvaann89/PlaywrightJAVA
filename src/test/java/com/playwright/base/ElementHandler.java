package com.playwright.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert; // Import TestNG Assert
import io.qameta.allure.Step;

import java.time.Duration;

/**
 * ElementHandler provides reusable functions for common Playwright element interactions.
 * It encapsulates actions like clicking, typing, selecting dropdowns, waiting for elements,
 * and checking visibility, with integrated logging, exception handling, and now, assertions.
 */
public class ElementHandler {

    private final Page page;
    private static final Logger log = LoggerFactory.getLogger(ElementHandler.class);

    /**
     * Constructor for ElementHandler.
     * @param page The Playwright Page object to perform actions on.
     */
    public ElementHandler(Page page) {
        this.page = page;
        log.info("ElementHandler initialized for page: " + page.url());
    }

    /**
     * Clicks on a specified Playwright Locator.
     * Logs the action and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator to click.
     * @param elementName A descriptive name for the element, used in logs and reports.
     */
    @Step("Clicking element: {elementName}")
    public void clickElement(Locator locator, String elementName) {
        try {
            log.info("Attempting to click element: '{}'", elementName);
            locator.click();
            log.info("Successfully clicked element: '{}'", elementName);
        } catch (PlaywrightException e) {
            log.error("Failed to click element '{}'. Error: {}", elementName, e.getMessage());
            throw new RuntimeException("Failed to click element '" + elementName + "'", e);
        }
    }

    /**
     * Enters text into a specified Playwright Locator (input field).
     * Logs the action and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator (input field) to enter text into.
     * @param text The text string to enter.
     * @param elementName A descriptive name for the element, used in logs and reports.
     */
    @Step("Entering text '{text}' into element: {elementName}")
    public void enterText(Locator locator, String text, String elementName) {
        try {
            log.info("Attempting to enter text '{}' into element: '{}'", text, elementName);
            locator.fill(text);
            log.info("Successfully entered text into element: '{}'", elementName);
        } catch (PlaywrightException e) {
            log.error("Failed to enter text into element '{}'. Error: {}", elementName, e.getMessage());
            throw new RuntimeException("Failed to enter text into element '" + elementName + "'", e);
        }
    }

    /**
     * Selects an option from a dropdown (select element) by its value.
     * Logs the action and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator for the select dropdown element.
     * @param value The 'value' attribute of the option to select.
     * @param elementName A descriptive name for the element, used in logs and reports.
     */
    @Step("Selecting dropdown option '{value}' for element: {elementName}")
    public void selectDropdownByValue(Locator locator, String value, String elementName) {
        try {
            log.info("Attempting to select dropdown option '{}' by value for element: '{}'", value, elementName);
            locator.selectOption(value);
            log.info("Successfully selected dropdown option '{}' for element: '{}'", value, elementName);
        } catch (PlaywrightException e) {
            log.error("Failed to select dropdown option '{}' for element '{}'. Error: {}", value, elementName, e.getMessage());
            throw new RuntimeException("Failed to select dropdown option '" + value + "' for element '" + elementName + "'", e);
        }
    }

    /**
     * Waits for a specified Playwright Locator to be visible and enabled.
     * Logs the waiting process and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator to wait for.
     * @param timeoutSeconds The maximum time in seconds to wait for the element.
     * @param elementName A descriptive name for the element, used in logs and reports.
     */
    @Step("Waiting for element: {elementName} to be visible (timeout: {timeoutSeconds}s)")
    public void waitForLocator(Locator locator, int timeoutSeconds, String elementName) {
        try {
            log.info("Waiting for element '{}' to be visible/enabled for {} seconds...", elementName, timeoutSeconds);
            locator.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(Duration.ofSeconds(timeoutSeconds).toMillis()));
            log.info("Element '{}' is visible/enabled.", elementName);
        } catch (PlaywrightException e) {
            log.error("Element '{}' was not visible/enabled within {} seconds. Error: {}", elementName, timeoutSeconds, e.getMessage());
            throw new RuntimeException("Element '" + elementName + "' was not visible/enabled within " + timeoutSeconds + " seconds", e);
        }
    }

    /**
     * Checks if a specified Playwright Locator is currently visible on the page.
     * Logs the check and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator to check visibility for.
     * @param elementName A descriptive name for the element, used in logs and reports.
     * @return true if the element is visible, false otherwise.
     */
    @Step("Checking visibility of element: {elementName}")
    public boolean isVisible(Locator locator, String elementName) {
        try {
            boolean visible = locator.isVisible();
            log.info("Element '{}' visibility status: {}", elementName, visible);
            return visible;
        } catch (PlaywrightException e) {
            log.error("Failed to check visibility of element '{}'. Error: {}", elementName, e.getMessage());
            return false; // Return false if an exception occurs during visibility check
        }
    }

    /**
     * Checks if a specified Playwright Locator is currently enabled on the page.
     * Logs the check and handles Playwright-specific exceptions.
     * @param locator The Playwright Locator to check enabled status for.
     * @param elementName A descriptive name for the element, used in logs and reports.
     * @return true if the element is enabled, false otherwise.
     */
    @Step("Checking enabled status of element: {elementName}")
    public boolean isEnabled(Locator locator, String elementName) {
        try {
            boolean enabled = locator.isEnabled();
            log.info("Element '{}' enabled status: {}", elementName, enabled);
            return enabled;
        } catch (PlaywrightException e) {
            log.error("Failed to check enabled status of element '{}'. Error: {}", elementName, e.getMessage());
            return false;
        }
    }

    /**
     * Asserts that two strings are equal.
     * This method wraps TestNG's Assert.assertEquals for consistent logging.
     * @param actual The actual string value.
     * @param expected The expected string value.
     * @param message The message to display if the assertion fails.
     */
    @Step("Asserting that '{actual}' equals '{expected}'")
    public void assertEquals(String actual, String expected, String message) {
        log.info("Asserting equality: Actual='{}', Expected='{}'", actual, expected);
        Assert.assertEquals(actual, expected, message);
        log.info("Assertion successful: Actual and Expected are equal.");
    }

    /**
     * Asserts that a condition is true.
     * This method wraps TestNG's Assert.assertTrue for consistent logging.
     * @param condition The condition to check.
     * @param message The message to display if the assertion fails.
     */
    @Step("Asserting that condition is true: {message}")
    public void assertTrue(boolean condition, String message) {
        log.info("Asserting condition is true. Condition: {}. Message: {}", condition, message);
        Assert.assertTrue(condition, message);
        log.info("Assertion successful: Condition is true.");
    }
}
