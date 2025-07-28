package demo.playwright.page;

import com.microsoft.playwright.Locator;
import demo.playwright.base.Generic;

public class CheckoutPage {

    Generic generic = new Generic("");

    private String checkoutInfo_txt = "//span[@class='title']"; //xpath for getting "Checkout: Your Information"  text
    private String firstName = "first-name"; // id for first name field
    private String lastName = "last-name"; // id for last name field
    private String postalCode = "postal-code"; // id for postal code field
    private String continue_btn = "continue"; // id continue button
    private String allItemSubTotal_txt = ".summary_subtotal_label"; //css selector for getting item total without tax
    private String totalTax_txt = ".summary_tax_label"; //css selector for getting tax
    private String totalPrice_txt = ".summary_info_label.summary_total_label"; //css selector for getting the final total price including tax
    private String finist_btn = "finish"; //id for finish  button

    public Locator loc_checkoutPageTitle(){
        return generic.getByLocator(checkoutInfo_txt);
    }

    public Locator loc_getFirstName(){
        return generic.getById(firstName);
    }

    public  Locator loc_getLastName(){
        return  generic.getById(lastName);
    }

    public Locator loc_getPostalAdd(){
        return generic.getById(postalCode);
    }

    public Locator loc_getContinueButton(){
        return generic.getById(continue_btn);
    }

    public Locator loc_subTotalValue(){
        return generic.getByLocator(allItemSubTotal_txt);
    }

    public Locator loc_taxValue(){
        return generic.getByLocator(totalTax_txt);
    }

    public Locator loc_totalValue(){
        return generic.getByLocator(totalPrice_txt);
    }

    public Locator loc_finishButton(){
        return generic.getById(finist_btn);
    }

    /**
     * This method return the title text of checkout page
     * @return String
     */
    public String getCheckoutPageTitle(){
        return generic.getInnerText(loc_checkoutPageTitle());
    }

    /**
     * This method fills the checkout information.
     * @param firstName
     * @param lastName
     * @param postalCode
     */
    public void fillCheckoutInfo(String firstName,String lastName,String postalCode){
        generic.fill(loc_getFirstName(),firstName);
        generic.fill(loc_getLastName(),lastName);
        generic.fill(loc_getPostalAdd(),postalCode);
    }

    /**
     * This method clicks on continue button
     */
    public void clickOnContinue(){
        generic.click(loc_getContinueButton());
    }

    /**
     * This method return price for given value
     * @param priceFor: tax or total or subTotal
     * @return String return price without $ symbol
     */
    public  String getPrice(String priceFor){
        String price = "";
        switch (priceFor){
            case "total":
                price = generic.getInnerText(loc_totalValue()).split(":")[1].trim().substring(1);
                break;
            case "tax":
                price = generic.getInnerText(loc_taxValue()).split(":")[1].trim().substring(1);
                break;
            case "subTotal":
                price = generic.getInnerText(loc_subTotalValue()).split(":")[1].trim().substring(1);
                break;
        }
        return price;
    }

    /**
     * This method click on finish button
     */
    public void clickOnFinish(){
        generic.click(loc_finishButton());
    }
}