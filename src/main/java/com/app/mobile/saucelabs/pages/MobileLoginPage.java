package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class MobileLoginPage extends MobileBasePage {

    @AndroidFindBy(accessibility = "open menu")
    @iOSXCUITFindBy(accessibility = "tab bar option menu")
    private WebElement openMenuButton;

    @AndroidFindBy(accessibility = "menu item log in")
    @iOSXCUITFindBy(accessibility = "menu item log in")
    private WebElement menuLoginOption;

    @AndroidFindBy(accessibility = "menu item log out")
    @iOSXCUITFindBy(accessibility = "menu item log out")
    private WebElement menuLogoutOption;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")
    @iOSXCUITFindBy(accessibility = "Username Input Field")
    private WebElement usernameField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordET")
    @iOSXCUITFindBy(accessibility = "Password Input Field")
    private WebElement passwordField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    @iOSXCUITFindBy(accessibility = "Login Button Element")
    private WebElement loginButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    @iOSXCUITFindBy(accessibility = "Product Catalog Header Text")
    private WebElement productCatalogHeader;

    public MobileLoginPage() {
        super();
    }

    public MobileLoginPage open() {
        ensureElementsInitialized();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(openMenuButton)).click();
        wait(Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(menuLoginOption)).click();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(usernameField));
        return this;
    }

    public MobileLoginPage login(String username, String password) {
        ensureElementsInitialized();

        usernameField.clear();
        usernameField.sendKeys(username);

        passwordField.clear();
        passwordField.sendKeys(password);

        loginButton.click();
        return this;
    }

    public boolean verifyDashboard() {
        ensureElementsInitialized();

        boolean isDisplayed;
        try {
            WebElement header = wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(productCatalogHeader));
            isDisplayed = header.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        }

        return isDisplayed;
    }

    public boolean isLogoutOptionDisplayed() {
        ensureElementsInitialized();

        wait(Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(openMenuButton)).click();

        boolean isDisplayed;
        try {
            WebElement logoutItem = wait(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(menuLogoutOption));
            isDisplayed = logoutItem.isDisplayed();
        } catch (Exception e) {
            isDisplayed = false;
        } finally {
            wait(Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(openMenuButton)).click();
        }

        return isDisplayed;
    }

}