package com.playwright.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * BaseTest class provides the foundational setup and teardown for Playwright tests.
 * It initializes Playwright, manages browser contexts and pages, and includes
 * hooks for logging and Allure report integration (e.g., attaching screenshots on failure).
 * This version also supports multi-environment and multi-browser configurations
 * loaded from a 'config.properties' file, and is designed to be thread-safe
 * for parallel test execution using ThreadLocal.
 */
public class BaseTest {

    // Playwright objects - now using ThreadLocal for thread safety
    private static ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
    private static ThreadLocal<Page> tlPage = new ThreadLocal<>();
    private static ThreadLocal<String> tlBrowserName = new ThreadLocal<>(); // Thread-local for browser name

    // Logger for logging test execution information
    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    // Properties object to load configuration from config.properties
    // This can remain a single instance as it's read-only after setup.
    protected static Properties config = new Properties(); // Make static to load once for all threads
    private static final String DEFAULT_BROWSER = "chromium";

    private static final boolean DEFAULT_HEADLESS = true;

    private static final String DEFAULT_ENVIRONMENT = "dev";

    /**
     * Getter for the thread-local Page object.
     * Test classes will use this to get the current thread's page instance.
     * @return The Playwright Page instance for the current thread.
     */
    public Page getPage() {
        return tlPage.get();
    }

    /**
     * This hook runs once before all tests in the suite.
     * It initializes the configuration properties.
     */
    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        log.info("--- DEBUG: Entering BaseTest @BeforeSuite: setupSuite() ---");
        // Load configuration properties only once for the entire suite
        if (config.isEmpty()) { // Check if already loaded by another thread/previous run
            try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
                config.load(fis);
                log.info("Configuration properties loaded from config.properties.");
            } catch (IOException e) {
                log.error("Failed to load config.properties. Using default configurations. Error: " + e.getMessage());
                // config is already initialized as new Properties()
            }
        }
        log.info("--- DEBUG: Exiting BaseTest @BeforeSuite: setupSuite() ---");
    }

    /**
     * This hook runs once after all tests in the suite.
     * It closes the main Playwright instance if it was created.
     */
    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        log.info("--- DEBUG: Entering BaseTest @AfterSuite: teardownSuite() ---");
        // No need to close tlPlaywright here, as each thread manages its own Playwright instance.
        // The Playwright.create() is done per thread in BeforeMethod now.
        log.info("--- DEBUG: Exiting BaseTest @AfterSuite: teardownSuite() ---");
    }

    /**
     * This hook runs before each test method.
     * It launches a new browser, creates a new browser context, and a new page
     * for the current thread. It then navigates to the base URL for the configured environment.
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "environment"})
    public void setupMethod(@Optional(DEFAULT_BROWSER) String browserTypeParam,
                            @Optional(DEFAULT_ENVIRONMENT) String environmentParam) {
        log.info("--- Setting up test method for thread: {} ---", Thread.currentThread().getId());

        // Initialize Playwright instance for the current thread
        Playwright playwrightInstance = Playwright.create();
        tlPlaywright.set(playwrightInstance);
        log.info("Playwright instance created for thread: {}", Thread.currentThread().getId());

        // Prioritize TestNG parameters, then fall back to config.properties, then to hardcoded defaults
        String browserType = browserTypeParam != null ? browserTypeParam : config.getProperty("browser", DEFAULT_BROWSER);
        boolean headless = Boolean.parseBoolean(config.getProperty("headless", String.valueOf(DEFAULT_HEADLESS)));
        String environment = environmentParam != null ? environmentParam : config.getProperty("environment", DEFAULT_ENVIRONMENT);
        String baseUrl = config.getProperty("base.url." + environment, "https://www.saucedemo.com/");

        tlBrowserName.set(browserType); // Store the browser name in ThreadLocal

        log.info("Launching browser: {} (Headless: {}) for thread: {}", browserType, headless, Thread.currentThread().getId());
        log.info("Navigating to base URL for environment '{}': {} for thread: {}", environment, baseUrl, Thread.currentThread().getId());

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
        Browser browserInstance;

        switch (browserType.toLowerCase()) {
            case "chromium":
                browserInstance = playwrightInstance.chromium().launch(launchOptions);
                break;
            case "firefox":
                browserInstance = playwrightInstance.firefox().launch(launchOptions);
                break;
            case "webkit":
                browserInstance = playwrightInstance.webkit().launch(launchOptions);
                break;
            default:
                log.warn("Unsupported browser type: {}. Launching Chromium by default for thread: {}", browserType, Thread.currentThread().getId());
                browserInstance = playwrightInstance.chromium().launch(launchOptions);
                break;
        }
        tlBrowser.set(browserInstance);
        log.info("{} browser launched for thread: {}", browserType, Thread.currentThread().getId());

        BrowserContext contextInstance = browserInstance.newContext();
        tlContext.set(contextInstance);
        log.info("New browser context created for thread: {}", Thread.currentThread().getId());

        Page pageInstance = contextInstance.newPage();
        tlPage.set(pageInstance);
        log.info("New page created for thread: {}", Thread.currentThread().getId());

        pageInstance.navigate(baseUrl);
        log.info("Navigated to URL: {} for thread: {}", baseUrl, Thread.currentThread().getId());
    }

    /**
     * This hook runs after each test method.
     * It checks the test result and captures a screenshot if the test failed.
     * Finally, it closes the browser context and browser for the current thread.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        log.info("--- Tearing down test method for thread: {} ---", Thread.currentThread().getId());

        Page currentPage = tlPage.get();
        if (result.getStatus() == ITestResult.FAILURE && currentPage != null) {
            log.error("Test failed: {}. Capturing screenshot for thread: {}", result.getName(), Thread.currentThread().getId());
            // Use the public method to capture and attach screenshot on failure
            takeScreenshotAndAttach(result.getName() + "_FAILURE");
        } else if (currentPage == null) {
            log.warn("Page object was null during tearDown for thread: {}. Cannot capture screenshot.", Thread.currentThread().getId());
        } else {
            log.info("Test passed: {} for thread: {}", result.getName(), Thread.currentThread().getId());
        }

        Browser browserInstance = tlBrowser.get();
        if (browserInstance != null) {
            browserInstance.close();
            log.info("Browser closed for thread: {}", Thread.currentThread().getId());
        }

        Playwright playwrightInstance = tlPlaywright.get();
        if (playwrightInstance != null) {
            playwrightInstance.close();
            log.info("Playwright instance closed for thread: {}", Thread.currentThread().getId());
        }

        // Remove the ThreadLocal variables to prevent memory leaks
        tlPage.remove();
        tlContext.remove();
        tlBrowser.remove();
        tlPlaywright.remove();
        tlBrowserName.remove();
        log.info("ThreadLocal objects removed for thread: {}", Thread.currentThread().getId());
    }

    /**
     * Captures a screenshot of the current page and attaches it to the Allure report.
     * This method can be called from test methods to attach screenshots at specific points.
     *
     * @param attachmentName The name to be given to the screenshot attachment in Allure.
     * @return A byte array representing the captured screenshot.
     */
    @Attachment(type = "image/png")
    public byte[] takeScreenshotAndAttach(String attachmentName) {
        Page currentPage = tlPage.get();
        String currentBrowserName = tlBrowserName.get();

        if (currentPage != null) {
            try {
                // Wait for network to be idle to ensure page is fully loaded
                currentPage.waitForLoadState(LoadState.NETWORKIDLE);
                // Add a small timeout to allow for any final rendering or animations
                currentPage.waitForTimeout(1000); // Wait for 1000 milliseconds (1 second)

                // Generate unique file name for the screenshot
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                String formattedDateTime = formatter.format(currentDateTime);

                String fileName = attachmentName + "_" + (currentBrowserName != null ? currentBrowserName : "unknown") + "_" + formattedDateTime + ".png";
                Path screenshotPath = Paths.get("target/screenshots/" + fileName);

                // Ensure the directory exists
                Files.createDirectories(screenshotPath.getParent());

                // Take screenshot and save to file
                byte[] screenshotBytes = currentPage.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));

                log.info("Screenshot '{}' captured and attached to Allure with name: {} for thread: {}", fileName, attachmentName, Thread.currentThread().getId());
                return screenshotBytes;
            } catch (IOException | PlaywrightException e) {
                log.error("Failed to capture and attach screenshot '{}'. Error: {} for thread: {}", attachmentName, e.getMessage(), Thread.currentThread().getId());
                return new byte[0];
            }
        } else {
            log.warn("Page object is null, cannot capture screenshot for attachment: {} for thread: {}", attachmentName, Thread.currentThread().getId());
            return new byte[0];
        }
    }
}
