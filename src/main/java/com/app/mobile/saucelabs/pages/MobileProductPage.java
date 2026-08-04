package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileProductPage extends MobileBasePage {

    private final MobileLoginPage mobileLoginPage;
    private final MobileProductDetailPage mobileProductDetailPage;
    private final MobileCartPage mobileCartPage;

    public MobileProductPage(MobileLoginPage mobileLoginPage, MobileProductDetailPage mobileProductDetailPage, MobileCartPage mobileCartPage) {
        super();
        this.mobileLoginPage = mobileLoginPage;
        this.mobileProductDetailPage = mobileProductDetailPage;
        this.mobileCartPage = mobileCartPage;
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

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/sortIV")
    private WebElement btnSort;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    //@iOSXCUITFindBy(accessibility = "title")
    private WebElement lblTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartIV")
    private WebElement btnCartIcon;


    public MobileLoginPage openLoginScreen() {
        navigateToLoginScreen();
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
        navigateToLoginScreen();
        mobileLoginPage.login(username, password);
    }

    private void navigateToLoginScreen() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogin)).click();
    }

    public MobileLoginPage logOut() {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuLogout)).click();

        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnPopupLogout)).click();

        return mobileLoginPage;
    }

    public MobileProductDetailPage selectProduct(String productLabel) {
        ensureElementsInitialized();

        String scrollToProductInCatalog = String.format(
                "new UiScrollable(new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productRV\"))"
                        + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                productLabel);
        wait(longWait()).until(d -> {
            d.findElement(AppiumBy.androidUIAutomator(scrollToProductInCatalog));
            return true;
        });

        By productImageByLabel = By.xpath(String.format(
                "//android.widget.TextView[@text='%s']/preceding-sibling::android.widget.ImageView[@resource-id='com.saucelabs.mydemoapp.android:id/productIV']",
                productLabel));
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(driver().findElement(productImageByLabel))).click();

        return mobileProductDetailPage;
    }

    public MobileCartPage openCart() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnCartIcon)).click();
        return mobileCartPage;
    }

    public void openSortDialog() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnSort)).click();
    }

    public java.util.List<String> getVisibleProductTitles() {
        ensureElementsInitialized();
        return driver().findElements(org.openqa.selenium.By.id("com.saucelabs.mydemoapp.android:id/titleTV"))
                .stream().map(org.openqa.selenium.WebElement::getText).collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<Double> getVisibleProductPrices() {
        ensureElementsInitialized();
        return driver().findElements(org.openqa.selenium.By.id("com.saucelabs.mydemoapp.android:id/priceTV"))
                .stream()
                .map(e -> Double.parseDouble(e.getText().replaceAll("[^0-9.]", "")))
                .collect(java.util.stream.Collectors.toList());
    }
}
