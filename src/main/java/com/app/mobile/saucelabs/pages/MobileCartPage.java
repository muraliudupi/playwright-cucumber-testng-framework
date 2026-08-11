package com.app.mobile.saucelabs.pages;

import com.framework.utils.MobileOrderRowUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileCartPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cartBt")
    private WebElement btnCheckout;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/itemsTV")
    private WebElement lblItemCount;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/totalPriceTV")
    private WebElement lblTotalPrice;

    public boolean isProductInCart(String productLabel) {
        ensureElementsInitialized();
        try {
            driver().findElement(AppiumBy.androidUIAutomator(productScrollCommand(productLabel)));

            return wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(MobileOrderRowUtils.productTitleLocator(productLabel)))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getItemCountLabel() {
        ensureElementsInitialized();
        return wait(longWait()).until(ExpectedConditions.visibilityOf(lblItemCount)).getText();
    }

    public String getTotalPriceLabel() {
        ensureElementsInitialized();
        return wait(longWait()).until(ExpectedConditions.visibilityOf(lblTotalPrice)).getText();
    }

    public MobileCartPage removeProduct(String productLabel) {
        ensureElementsInitialized();
        wait(longWait()).until(d -> {
            d.findElement(AppiumBy.androidUIAutomator(productScrollCommand(productLabel)));
            return true;
        });

        By removeButtonForProduct = By.xpath(String.format(
                "//android.widget.TextView[@text='%s']"
                        + "/ancestor::android.view.ViewGroup[.//android.widget.TextView[@resource-id='com.saucelabs.mydemoapp.android:id/removeBt']][1]"
                        + "//android.widget.TextView[@resource-id='com.saucelabs.mydemoapp.android:id/removeBt']",
                productLabel));
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(driver().findElement(removeButtonForProduct))).click();
        return this;
    }

    private String productScrollCommand(String productLabel) {
        return String.format(
                "new UiScrollable(new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productRV\"))"
                        + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                productLabel);
    }

    public void tapCheckout() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnCheckout)).click();
    }

    public boolean isColorIndicatorDisplayedForProduct(String productLabel) {
        ensureElementsInitialized();
        try {
            driver().findElement(AppiumBy.androidUIAutomator(productScrollCommand(productLabel)));
            return wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(MobileOrderRowUtils.productColorIconLocator(productLabel)))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean quantityMatches(String productLabel, int expectedQuantity) {
        ensureElementsInitialized();
        try {
            driver().findElement(AppiumBy.androidUIAutomator(productScrollCommand(productLabel)));
            String actual = wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(MobileOrderRowUtils.productQuantityLocator(productLabel)))
                    .getText();
            return String.valueOf(expectedQuantity).equals(actual.trim());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cartContentsMatch(String productLabel, int expectedQuantity) {
        return isProductInCart(productLabel)
                && quantityMatches(productLabel, expectedQuantity)
                && isColorIndicatorDisplayedForProduct(productLabel);
    }

}
