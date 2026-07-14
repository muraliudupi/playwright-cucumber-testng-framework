package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileCartPage extends MobileBasePage {

    private final MobileLoginPage mobileLoginPage;
    private final MobileCheckoutPage mobileCheckoutPage;

    public MobileCartPage(MobileLoginPage mobileLoginPage, MobileCheckoutPage mobileCheckoutPage) {
        super();
        this.mobileLoginPage = mobileLoginPage;
        this.mobileCheckoutPage = mobileCheckoutPage;
    }

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartBt")
    private WebElement btnCheckout;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/itemsTV")
    private WebElement lblItemCount;

    public boolean isProductInCart(String productLabel) {
        ensureElementsInitialized();
        try {
            String scrollToProductInCart = String.format(
                    "new UiScrollable(new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productRV\"))"
                            + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                    productLabel);
            driver().findElement(AppiumBy.androidUIAutomator(scrollToProductInCart));

            return wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(cartItemTitleLocator(productLabel)))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getItemCountLabel() {
        ensureElementsInitialized();
        return wait(longWait()).until(ExpectedConditions.visibilityOf(lblItemCount)).getText();
    }

    public MobileCartPage removeProduct(String productLabel) {
        ensureElementsInitialized();
        String scrollToProductInCart = String.format(
                "new UiScrollable(new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productRV\"))"
                        + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                productLabel);
        wait(longWait()).until(d -> {
            d.findElement(AppiumBy.androidUIAutomator(scrollToProductInCart));
            return true;
        });

        By removeButtonForProduct = By.xpath(String.format(
                "//android.widget.TextView[@text='%s']/ancestor::android.view.ViewGroup[1]//android.widget.Button[@resource-id='com.saucelabs.mydemoapp.android:id/removeBt']",
                productLabel));
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(driver().findElement(removeButtonForProduct))).click();
        return this;
    }

    private By cartItemTitleLocator(String productLabel) {
        return By.xpath(String.format("//android.widget.TextView[@resource-id='com.saucelabs.mydemoapp.android:id/titleTV' and @text='%s']", productLabel));
    }

    public void tapCheckout() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnCheckout)).click();
    }

    public MobileLoginPage getMobileLoginPage() {
        return mobileLoginPage;
    }

    public MobileCheckoutPage getMobileCheckoutPage() {
        return mobileCheckoutPage;
    }
}