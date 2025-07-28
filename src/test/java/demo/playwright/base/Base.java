package demo.playwright.base;

import com.microsoft.playwright.*;

public final class Base {

    private static String configFile = "./config.properties";
    private static  ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static  ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();
    private static  ThreadLocal<Page> page = new ThreadLocal<>();
    private static ThreadLocal<Base> base = new ThreadLocal<>();

    /**
     * Setting the reference of Playwright
     */
    private static void setPlaywright(){
        playwright.set(Playwright.create());
    }

    /**
     * Setting the browser reference
     */
    private static void setBrowser(){
        String binary = ReadPropertyFile.getProperty("binary",configFile);
        String channel = ReadPropertyFile.getProperty("channel",configFile);
        String headless = ReadPropertyFile.getProperty("headless",configFile);
        switch (binary){
            case "chromium":
                browser.set( getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setChannel(channel).setHeadless(Boolean.valueOf(headless))));
                break;
            case "webkit":
                browser.set( getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setChannel(channel).setHeadless(Boolean.valueOf(headless))));
                break;
            case "firefox":
                browser.set( getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setChannel(channel).setHeadless(Boolean.valueOf(headless))));
                break;
            default:
                browser.set( getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setChannel(channel).setHeadless(Boolean.valueOf(headless))));
                break;
        }
    }

    /**
     * Setting the browser context
     */
    private static void setBrowserContext(){
        browserContext.set(getBrowser().newContext());
    }

    /**
     * Setting the Page
     */
    protected static void setPage(){
        page.set(getBrowserContext().newPage());
    }

    /**
     * Getting the Playwright reference
     */
    public static Playwright getPlaywright() {
        return playwright.get();
    }

    /**
     * Getting the Browser object
     */
    public static Browser getBrowser() {
        return browser.get();
    }

    /**
     * Getting the Browser Context object
     */
    public static BrowserContext getBrowserContext() {
        return browserContext.get();
    }

    /**
     * Getting the Page object
     */
    public static Page getPage() {
        return page.get();
    }

    /**
     * Creating constructor of class and creating objects
     */
    private Base(){
        setPlaywright();
        setBrowser();
        setBrowserContext();
        setPage();
    }

    /**
     * This method returns the current thread page object
     */
    public static Page getBasePage(){
        if(base.get()==null)
            base.set(new Base());
        return base.get().getPage();
    }

}