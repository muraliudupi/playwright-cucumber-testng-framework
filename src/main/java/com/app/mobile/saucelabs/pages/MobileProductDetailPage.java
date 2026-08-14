package com.app.mobile.saucelabs.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileProductDetailPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/plusIV")
    private WebElement btnQuantityPlus;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/minusIV")
    private WebElement btnQuantityMinus;

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
        while (currentQuantity > targetQuantity) {
            wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnQuantityMinus)).click();
            currentQuantity--;
        }
        return this;
    }

    public void addToCart() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnAddToCart)).click();
    }

    public MobileProductDetailPage selectColor(String colorName) {
        ensureElementsInitialized();
        By colorSwatchLocator = AppiumBy.accessibilityId(colorName + " color");
        WebElement colorSwatch = wait(longWait()).until(ExpectedConditions.presenceOfElementLocated(colorSwatchLocator));
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(colorSwatch)).click();
        return this;
    }

    public void backToCatalog() {
        navigateBack();
    }
}