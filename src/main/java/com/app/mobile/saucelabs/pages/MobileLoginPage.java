package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileLoginPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")
    //@iOSXCUITFindBy(accessibility = "Username Input Field")
    private WebElement txtUsername;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordET")
    //@iOSXCUITFindBy(accessibility = "Password Input Field")
    private WebElement txtPassword;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    //@iOSXCUITFindBy(accessibility = "Login Button Element")
    private WebElement btnLogin;

    public MobileLoginPage() {
        super();
    }

    public MobileLoginPage login(String username, String password) {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.visibilityOf(txtUsername));

        txtUsername.clear();
        txtUsername.sendKeys(username);

        txtPassword.clear();
        txtPassword.sendKeys(password);

        btnLogin.click();

        return this;
    }

    public boolean isLoginOptionDisplayed() {
        ensureElementsInitialized();

        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(txtUsername)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

}