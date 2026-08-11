package com.app.mobile.saucelabs.pages;

import com.framework.models.Address;
import com.framework.models.OrderItem;
import com.framework.models.PaymentDetails;
import com.framework.utils.MobileScrollUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileReviewPage extends MobileBasePage {

    private static final String PRODUCT_TITLE_ID = "com.saucelabs.mydemoapp.android:id/titleTV";
    private static final String PRODUCT_QTY_ID   = "com.saucelabs.mydemoapp.android:id/noTV";  //In Cart Review Page
    private static final String COLOR_ICON_DESC  = "Displays color of selected product";

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/itemsTV")  private WebElement lblItemCount; //In Cart Review Page
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/itemNumberTV")  private WebElement lblReviewItemCount; // In Checkout Review Page

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/totalPriceTV") private WebElement lblTotalAmount; //In Cart Review Page
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/totalAmountTV") private WebElement lblReviewTotalAmount; // In Checkout Review Page

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
            By productTitle = By.xpath(String.format(
                    "//android.widget.TextView[@resource-id='%s' and @text='%s']",
                    PRODUCT_TITLE_ID, productLabel));
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOfElementLocated(productTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean quantityMatches(String productLabel, int expectedQuantity) {
        try {
            By quantityForProduct = By.xpath(String.format(
                    "//android.widget.TextView[@text='%s']"
                            + "/ancestor::android.view.ViewGroup[.//android.widget.TextView[@resource-id='%s']][1]"
                            + "//android.widget.TextView[@resource-id='%s']",
                    productLabel, PRODUCT_QTY_ID, PRODUCT_QTY_ID));
            String actual = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOfElementLocated(quantityForProduct)).getText();
            return String.valueOf(expectedQuantity).equals(actual.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isColorIndicatorDisplayedForProduct(String productLabel) {
        try {
            By colorIcon = By.xpath(String.format(
                    "//android.widget.TextView[@text='%s']"
                            + "/ancestor::android.view.ViewGroup[.//android.widget.ImageView[@content-desc='%s']][1]"
                            + "//android.widget.ImageView[@content-desc='%s']",
                    productLabel, COLOR_ICON_DESC, COLOR_ICON_DESC));
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOfElementLocated(colorIcon)).isDisplayed();
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