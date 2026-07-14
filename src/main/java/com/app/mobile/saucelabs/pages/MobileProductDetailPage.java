package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileProductDetailPage extends MobileBasePage {

    public MobileProductDetailPage(MobileCartPage mobileCartPage) {
        super();
    }

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/plusIV")
    private WebElement btnQuantityPlus;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/noTV")
    private WebElement lblQuantity;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartBt")
    private WebElement btnAddToCart;

    public MobileProductDetailPage setQuantity(int targetQuantity) {
        ensureElementsInitialized();
        int currentQuantity = Integer.parseInt(
                wait(longWait()).until(ExpectedConditions.visibilityOf(lblQuantity)).getText().trim());

        while (currentQuantity < targetQuantity) {
            wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnQuantityPlus)).click();
            currentQuantity++;
        }
        return this;
    }

    public void addToCart() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnAddToCart)).click();
    }
}