package com.app.mobile.saucelabs.pages;

import com.framework.utils.ConfigReader;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class MobileProductPage extends MobileBasePage {

    private Duration longWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.wait.timeout.sec", 15));
    }

    private Duration shortWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.short.wait.timeout.sec", 10));
    }

/*
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

    public MobileProductPage() {
        super();
    }

    public MobileProductPage open() {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();

        wait(longWait()).until(ExpectedConditions.visibilityOf(txtUsername));
        return this;
    }

    public MobileProductPage login(String username, String password) {
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
            WebElement header = wait(longWait()).until(ExpectedConditions.visibilityOf(lblTitle));
            isDisplayed = header.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        }

        return isDisplayed;
    }

    public boolean isLogoutOptionDisplayed() {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();

        boolean isDisplayed;
        try {
            WebElement logoutItem = wait(longWait()).until(ExpectedConditions.visibilityOf(btnMenuLogout));
            isDisplayed = logoutItem.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        } finally {
            wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        }

        return isDisplayed;
    }

    public MobileProductPage loginWithValidCredentials(String username, String password) {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();

        wait(longWait()).until(ExpectedConditions.visibilityOf(txtUsername));

        txtUsername.clear();
        txtUsername.sendKeys(username);

        txtPassword.clear();
        txtPassword.sendKeys(password);

        btnLogin.click();
        return this;
    }
*/
}