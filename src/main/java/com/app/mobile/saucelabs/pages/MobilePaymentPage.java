package com.app.mobile.saucelabs.pages;

import com.framework.models.Address;
import com.framework.models.PaymentDetails;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobilePaymentPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")            private WebElement txtCardHolderName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cardNumberET")      private WebElement txtCardNumber;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/expirationDateET")  private WebElement txtExpirationDate;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/securityCodeET")    private WebElement txtSecurityCode;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billingAddressCB")  private WebElement chkBillingSameAsShipping;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/paymentBtn")        private WebElement btnProceed;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/thankYouTV")        private WebElement lblThankYou;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameET")  private WebElement txtBillingFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/address1ET")  private WebElement txtBillingAddress1;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityET")      private WebElement txtBillingCity;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stateET")     private WebElement txtBillingState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/zipET")       private WebElement txtBillingZip;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryET")   private WebElement txtBillingCountry;

    public MobilePaymentPage enterPaymentDetails(String billFullName, PaymentDetails paymentDetails) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtCardHolderName)).sendKeys(billFullName);
        txtCardNumber.sendKeys(paymentDetails.cardNumber());
        txtExpirationDate.sendKeys(paymentDetails.expirationDate());
        txtSecurityCode.sendKeys(paymentDetails.securityCode());
        return this;
    }

    public MobilePaymentPage enterBillingAddressDetails(String billFullName, Address billingAddress) {
        ensureElementsInitialized();
        String scrollToBillingSection =
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/countryET\"))";
        try {
            driver().findElement(io.appium.java_client.AppiumBy.androidUIAutomator(scrollToBillingSection));
        } catch (Exception ignored) {
            // Field may already be on-screen
        }

        wait(longWait()).until(ExpectedConditions.visibilityOf(txtBillingFullName)).sendKeys(billFullName);
        txtBillingAddress1.sendKeys(billingAddress.address());
        txtBillingCity.sendKeys(billingAddress.city());
        txtBillingState.sendKeys(billingAddress.state());
        txtBillingZip.sendKeys(billingAddress.zip());
        txtBillingCountry.sendKeys(billingAddress.country());
        return this;
    }

    public MobilePaymentPage uncheckBillingSameAsShipping() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(chkBillingSameAsShipping)).click();
        return this;
    }

    public MobilePaymentPage proceedToReview() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
    }

    public MobilePaymentPage placeOrder() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
    }

    public void submitPaymentEmpty() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
    }

    public boolean isOrderConfirmationDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(longWait()).until(ExpectedConditions.visibilityOf(lblThankYou)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}