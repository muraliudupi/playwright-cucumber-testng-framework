package com.app.mobile.saucelabs.pages;

import com.framework.utils.ConfigReader;
import com.framework.utils.MobileScrollUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MobileProductPage extends MobileBasePage {

    private final MobileLoginPage mobileLoginPage;
    private final MobileProductDetailPage mobileProductDetailPage;
    private final MobileCartPage mobileCartPage;
    private final MobileQrScannerPage mobileQrScannerPage;
    private final MobileGeoLocationPage mobileGeoLocationPage;
    private final MobileDrawingPage mobileDrawingPage;

    public MobileProductPage(MobileLoginPage mobileLoginPage, MobileProductDetailPage mobileProductDetailPage,
                             MobileCartPage mobileCartPage, MobileQrScannerPage mobileQrScannerPage,
                             MobileGeoLocationPage mobileGeoLocationPage, MobileDrawingPage mobileDrawingPage) {
        super();
        this.mobileLoginPage = mobileLoginPage;
        this.mobileProductDetailPage = mobileProductDetailPage;
        this.mobileCartPage = mobileCartPage;
        this.mobileQrScannerPage = mobileQrScannerPage;
        this.mobileGeoLocationPage = mobileGeoLocationPage;
        this.mobileDrawingPage = mobileDrawingPage;
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

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"QR Code Scanner\")")
    private WebElement btnMenuQrScanner;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Geo Location\")")
    private WebElement btnMenuGeoLocation;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Drawing\")")
    private WebElement btnMenuDrawing;

    @AndroidFindBy(id = "android:id/button1")
    //@iOSXCUITFindBy(accessibility = "Logout")
    private WebElement btnPopupLogout;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/sortIV")
    private WebElement btnSort;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/productTV")
    //@iOSXCUITFindBy(accessibility = "title")
    private WebElement lblTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartRL")
    private WebElement btnCartIcon;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartTV")
    private WebElement lblCartBadge;


    public MobileLoginPage openLoginScreen() {
        navigateToLoginScreen();
        return mobileLoginPage;
    }

    public MobileQrScannerPage openQrScanner() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuQrScanner)).click();
        mobileQrScannerPage.grantCameraPermissionIfPrompted();
        return mobileQrScannerPage;
    }

    public MobileGeoLocationPage openGeoLocation() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuGeoLocation)).click();
        return mobileGeoLocationPage;
    }

    public MobileDrawingPage openDrawing() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnMenu)).click();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnMenuDrawing)).click();
        mobileDrawingPage.grantMediaPermissionIfPrompted();
        return mobileDrawingPage;
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

        By productImageByLabel = By.xpath(String.format(
                "//android.widget.TextView[@text='%s']/preceding-sibling::android.widget.ImageView[@resource-id='com.saucelabs.mydemoapp.android:id/productIV']",
                productLabel));

        int maxScrollAttempts = ConfigReader.getInt("mobile.product.select.max.scroll.attempts", 5);
        int attempts = 0;
        while (driver().findElements(productImageByLabel).isEmpty() && attempts < maxScrollAttempts) {
            MobileScrollUtils.scrollDown(driver());
            attempts++;
        }

        if (driver().findElements(productImageByLabel).isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Product Selection Failure: '%s' was not found in the catalog after %d scroll attempt(s).",
                    productLabel, maxScrollAttempts));
        }

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
        return driver().findElements(By.id("com.saucelabs.mydemoapp.android:id/titleTV"))
                .stream().map(WebElement::getText).collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<Double> getVisibleProductPrices() {
        ensureElementsInitialized();
        return driver().findElements(By.id("com.saucelabs.mydemoapp.android:id/priceTV"))
                .stream()
                .map(e -> Double.parseDouble(e.getText().replaceAll("[^0-9.]", "")))
                .collect(java.util.stream.Collectors.toList());
    }

    public record ProductSummary(String title, double price) {
    }

    public List<ProductSummary> collectAllProducts(int maxScrolls) {
        ensureElementsInitialized();
        LinkedHashMap<String, Double> seen = new LinkedHashMap<>();
        captureVisibleProductsInto(seen);
        for (int i = 0; i < maxScrolls; i++) {
            MobileScrollUtils.scrollDown(driver());
            captureVisibleProductsInto(seen);
        }
        return seen.entrySet().stream()
                .map(e -> new ProductSummary(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private void captureVisibleProductsInto(LinkedHashMap<String, Double> seen) {
        List<String> titles = getVisibleProductTitles();
        List<Double> prices = getVisibleProductPrices();
        for (int i = 0; i < titles.size() && i < prices.size(); i++) {
            seen.putIfAbsent(titles.get(i), prices.get(i));
        }
    }

    public boolean cartBadgeMatches(int expectedCount) {
        ensureElementsInitialized();
        try {
            String actual = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblCartBadge)).getText();
            return String.valueOf(expectedCount).equals(actual.trim());
        } catch (Exception e) {
            return false;
        }
    }
}