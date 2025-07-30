package com.playwright.dataprovider;


import com.playwright.base.BaseTest;
import org.testng.annotations.DataProvider;



public class SwagLabsDataProvider extends BaseTest {

    @DataProvider(name = "product-to-add", parallel = true)
    public Object[][] productToAdd() {
        return new Object[][]{
                {"Sauce Labs Backpack"}
        };
    }

    @DataProvider(name = "user-credentials")
    public Object[][] getUserCredentials() {

        String environment = config.getProperty("environment","dev"); // Get environment from BaseTest's config

        switch (environment.toLowerCase()) {
            case "dev":
                return new Object[][]{
                        {"dev_user1", "dev_pass1"},
                        {"dev_user2", "dev_pass2"}
                };
            case "qa":
                return new Object[][]{
                        {"qa_user_A", "qa_pass_A"},
                        {"qa_user_B", "qa_pass_B"}
                };
            case "prod":
                return new Object[][]{
                        {"prod_user_X", "prod_pass_X"}
                };
            default:
                throw new RuntimeException("Unsupported environment: " + environment);
        }
    }

}

