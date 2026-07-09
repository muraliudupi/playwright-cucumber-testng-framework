package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class MobileLoginPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/menuIV")
    //@iOSXCUITFindBy(accessibility = "View menu")
    private WebElement btnMenu;

    @AndroidFindBy(accessibility = "Login Menu Item")
    //@iOSXCUITFindBy(accessibility = "Login Menu Item")
    private WebElement btnMenuLogin;

    @AndroidFindBy(accessibility = "Logout Menu Item")
    //@iOSXCUITFindBy(accessibility = "Logout Menu Item")
    private WebElement btnMenuLogout;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")
    //@iOSXCUITFindBy(accessibility = "Username Input Field")
    private WebElement txtUsername;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordET")
    //@iOSXCUITFindBy(accessibility = "Password Input Field")
    private WebElement txtPassword;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    //@iOSXCUITFindBy(accessibility = "Login Button Element")
    private WebElement btnLogin;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    //@iOSXCUITFindBy(accessibility = "title")
    private WebElement lblTitle;

    public MobileLoginPage() {
        super();
    }

    public MobileLoginPage open() {
        ensureElementsInitialized();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(txtUsername));
        return this;
    }

    public MobileLoginPage login(String username, String password) {
        ensureElementsInitialized();

        txtUsername.clear();
        txtUsername.sendKeys(username);

        txtPassword.clear();
        txtPassword.sendKeys(password);

        btnLogin.click();
        return this;
    }

    public boolean verifyDashboard() {
        ensureElementsInitialized();

        boolean isDisplayed;
        try {
            WebElement header = wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(lblTitle));
            isDisplayed = header.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        }

        return isDisplayed;
    }

    public boolean isLogoutOptionDisplayed() {
        ensureElementsInitialized();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();

        boolean isDisplayed;
        try {
            WebElement logoutItem = wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(btnMenuLogout));
            isDisplayed = logoutItem.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        } finally {
            wait(Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        }

        return isDisplayed;
    }

    public MobileLoginPage loginWithValidCredentials(String username, String password) {
        ensureElementsInitialized();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(txtUsername));

        txtUsername.clear();
        txtUsername.sendKeys(username);

        txtPassword.clear();
        txtPassword.sendKeys(password);

        btnLogin.click();
        return this;
    }

}