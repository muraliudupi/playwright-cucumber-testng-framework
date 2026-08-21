package com.app.mobile.saucelabs.pages;

import com.framework.models.Address;
import com.framework.models.OrderItem;
import com.framework.models.PaymentDetails;
import com.framework.utils.MobileOrderRowUtils;
import com.framework.utils.MobileScrollUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileReviewPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/itemNumberTV")  private WebElement lblItemCount;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/totalAmountTV") private WebElement lblTotalAmount;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameTV")   private WebElement lblShippingFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/addressTV")    private WebElement lblShippingAddress;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityTV")       private WebElement lblShippingCityState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryTV")    private WebElement lblShippingCountryZip;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cardHolderTV")     private WebElement lblCardHolderName ;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cardNumberTV")     private WebElement lblCardNumber;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/expirationDateTV") private WebElement lblExpirationDate;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billFullnameTV")         private WebElement lblBillingFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billaddressTV")          private WebElement lblBillingAddress;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billingCityAndStateTV")  private WebElement lblBillingCityState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billingZipAndCountryTV") private WebElement lblBillingZipCountry;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/paymentBtn")   private WebElement btnPlaceOrder;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/thankYouTV")   private WebElement lblThankYou;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/shoopingBt")
    private WebElement btnContinueShopping;

    public boolean orderItemMatches(OrderItem item) {
        ensureElementsInitialized();
        for (int i = 0; i < 5 && !(isProductInOrderSummary(item.productLabel())
                && isColorIndicatorDisplayedForProduct(item.productLabel())); i++) {
            MobileScrollUtils.scrollDown(driver());
        }
        return isProductInOrderSummary(item.productLabel())
                && isColorIndicatorDisplayedForProduct(item.productLabel());
    }

    private boolean isProductInOrderSummary(String productLabel) {
        try {
            return wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(MobileOrderRowUtils.productTitleLocator(productLabel)))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isColorIndicatorDisplayedForProduct(String productLabel) {
        try {
            return wait(existenceCheckTimeout())
                    .until(ExpectedConditions.visibilityOfElementLocated(MobileOrderRowUtils.productColorIconLocator(productLabel)))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean itemCountMatches(int expectedQuantity) {
        ensureElementsInitialized();
        String actual = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblItemCount)).getText();
        return actual != null && actual.trim().startsWith(String.valueOf(expectedQuantity));
    }

    public boolean isTotalAmountDisplayed() {
        ensureElementsInitialized();
        try {
            String actual = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblTotalAmount)).getText();
            return actual != null && actual.matches(".*\\d+\\.\\d{2}.*");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean shippingDetailsMatch(String fullName, Address shippingAddress) {
        ensureElementsInitialized();

        return textContains(lblShippingFullName, fullName)
                && textContains(lblShippingAddress, shippingAddress.address())
                && textContains(lblShippingCityState, shippingAddress.city())
                && textContains(lblShippingCityState, shippingAddress.state())
                && textContains(lblShippingCountryZip, shippingAddress.country())
                && textContains(lblShippingCountryZip, shippingAddress.zip());
    }

    public boolean paymentDetailsMatch(PaymentDetails paymentDetails) {
        ensureElementsInitialized();
        for (int i = 0; i < 3 && !isElementVisible(lblExpirationDate); i++) {
            MobileScrollUtils.scrollDown(driver());
        }

        return textContains(lblCardHolderName, paymentDetails.fullName())
                && textContains(lblCardNumber, paymentDetails.cardNumber())
                && textContains(lblExpirationDate, paymentDetails.expirationDate());
    }

    public boolean billingDetailsMatch(String billFullName, Address billingAddress) {
        ensureElementsInitialized();
        for (int i = 0; i < 3 && !isElementVisible(lblBillingZipCountry); i++) {
            MobileScrollUtils.scrollDown(driver());
        }

        return textContains(lblBillingFullName, billFullName)
                && textContains(lblBillingAddress, billingAddress.address())
                && textContains(lblBillingCityState, billingAddress.city())
                && textContains(lblBillingCityState, billingAddress.state())
                && textContains(lblBillingZipCountry, billingAddress.zip())
                && textContains(lblBillingZipCountry, billingAddress.country());
    }

    public void placeOrder() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnPlaceOrder)).click();
    }

    public void continueShopping() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnContinueShopping)).click();
    }

    public boolean isOrderConfirmationDisplayed() {
        ensureElementsInitialized();

        try {
            return wait(longWait()).until(ExpectedConditions.visibilityOf(lblThankYou)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean textContains(WebElement element, String expectedSubstring) {
        try {
            String actual = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(element)).getText();
            return actual != null && actual.contains(expectedSubstring);
        } catch (Exception e) {
            return false;
        }
    }
}