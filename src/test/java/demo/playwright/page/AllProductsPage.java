package demo.playwright.page;

import com.microsoft.playwright.Locator;
import demo.playwright.base.Generic;

public class AllProductsPage {
    private Generic generic = new Generic("");

    //creating generic xpath for add to cart button, use string format to replace %s with product name
    private String addToCart_btn = "//div[text()='%s']//ancestor::div[@class='inventory_item_description']//button";

    // creating generic xpath to get the price label for the product name, use string format to replace %s with product name
    private String price_lbl = "//div[text()='%s']//ancestor::div[@class='inventory_item_description']//div[@class='inventory_item_price']";

    // using id for shopping cart button present on all products page
    private String shoppingCart_btn = "shopping_cart_container";

    // using css selector for count displayed on cart button when any product is added in cart
    private String shoppingCartCount_txt = "#shopping_cart_container>a>span";

    public Locator loc_AddToCart(String productName){
        return generic.getByLocator(String.format(addToCart_btn,productName));
    }

    public Locator loc_ProductPrice(String productName){
        return generic.getByLocator(String.format(price_lbl,productName));
    }

    public Locator loc_ShoppingCart(){
        return generic.getById(shoppingCart_btn);
    }

    public Locator loc_ShoppingCartItemCount(){
        return generic.getByLocator(shoppingCartCount_txt);
    }

    /**
     * This method is used to add given product in cart
     * @param productName
     */
    public void addProduct(String productName){
        generic.click(loc_AddToCart(productName));
    }

    /**
     * This method clicks on cart button
     */
    public void clickOnCartButton(){
        generic.click(loc_ShoppingCart());
    }

    /**
     * This methods returns the product quantity displayed over cart button
     * @return String
     */
    public String getCartProductQuantity(){
        return generic.getInnerText(loc_ShoppingCartItemCount());
    }

    /**
     * This method returns the price of given product
     * @param productName
     * @return String
     */
    public String getProductPrice(String productName){
        return generic.getInnerText(loc_ProductPrice(productName));
    }

}