package demo.playwright.page;

import com.microsoft.playwright.Locator;
import demo.playwright.base.Generic;

public class LoginPage {
    private Generic generic = new Generic("");
    private String login_btn = "login-button"; //using id locator for login button
    private String username = "user-name"; //using id locator for username
    private String password = "password"; //using id locator for password

    public Locator loc_LoginBtn(){
        return generic.getById(login_btn);
    }

    public Locator loc_Username(){
        return generic.getById(username);
    }

    public Locator loc_Password(){
        return generic.getById(password);
    }

    /**
     * This method is used for login with given credentials
     * @param username
     * @param password
     */
    public void login(String username, String password){
        //using generic method to fill usename in username fields
        generic.fill(loc_Username(),username);
        //using generic method to fill password in password fields
        generic.fill(loc_Password(),password);
        //using generic method to click on password button
        generic.click(loc_LoginBtn());
    }
}