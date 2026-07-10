package com.app.mobile.saucelabs.pages;

import com.framework.utils.ConfigReader;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class MobileProductPage extends MobileBasePage {

    private final MobileLoginPage mobileLoginPage;

    public MobileProductPage(MobileLoginPage mobileLoginPage) {
        super();
        this.mobileLoginPage = mobileLoginPage;
    }

    private Duration longWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.wait.timeout.sec", 15));
    }

    private Duration shortWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.short.wait.timeout.sec", 10));
    }

    private Duration existenceCheckTimeout() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.existence.check.timeout.sec", 3));
    }

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/menuIV")
    //@iOSXCUITFindBy(accessibility = "View menu")
    private WebElement btnMenu;

    @AndroidFindBy(accessibility = "Login Menu Item")
    //@iOSXCUITFindBy(accessibility = "Login Menu Item")
    private WebElement btnMenuLogin;

    @AndroidFindBy(accessibility = "Logout Menu Item")
    //@iOSXCUITFindBy(accessibility = "Logout Menu Item")
    private WebElement btnMenuLogout;

    @AndroidFindBy(id = "android:id/button1")
    //@iOSXCUITFindBy(accessibility = "Logout")
    private WebElement btnPopupLogout;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    //@iOSXCUITFindBy(accessibility = "title")
    private WebElement lblTitle;

    public MobileLoginPage openLoginScreen() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();
        return mobileLoginPage;
    }

    public boolean verifyDashboard() {
        ensureElementsInitialized();
        try {
            return wait(longWait()).until(ExpectedConditions.visibilityOf(lblTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLogoutOptionDisplayed() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(btnMenuLogout)).isDisplayed();
        } catch (Exception e) {
            return false;
        } finally {
            wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        }
    }

    public void loginWithValidCredentials(String username, String password) {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();

        mobileLoginPage.login(username, password);
    }

    public MobileLoginPage logOut() {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogout)).click();

        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnPopupLogout)).click();

        return mobileLoginPage;
    }
}