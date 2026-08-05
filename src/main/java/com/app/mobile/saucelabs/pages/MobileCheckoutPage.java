package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileCheckoutPage extends MobileBasePage {

    // --- Checkout Info (shipping / billing - address) ---
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameET")  private WebElement txtFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/address1ET")  private WebElement txtAddress1;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityET")      private WebElement txtCity;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stateET")     private WebElement txtState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/zipET")       private WebElement txtZip;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryET")   private WebElement txtCountry;

    // --- Checkout Info (Payment details) ---
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")  private WebElement txtFName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cardNumberET")     private WebElement txtCardNumber;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/expirationDateET") private WebElement txtExpirationDate;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/securityCodeET")   private WebElement txtSecurityCode;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/billingAddressCB")
    private WebElement chkBillingSameAsShipping;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/paymentBtn") private WebElement btnProceed;

    // --- Order Confirmation ---
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/thankYouTV") private WebElement lblThankYou;

    public MobileCheckoutPage enterShippingDetails(String fullName, String address1, String city,
                                                   String state, String zip, String country) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtFullName)).sendKeys(fullName);
        txtAddress1.sendKeys(address1);
        txtCity.sendKeys(city);
        txtState.sendKeys(state);
        txtZip.sendKeys(zip);
        txtCountry.sendKeys(country);
        return this;
    }

    public MobileCheckoutPage enterPaymentDetails(String fullName, String cardNumber, String expirationDate, String securityCode) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtFName)).sendKeys(fullName);
        txtCardNumber.sendKeys(cardNumber);
        txtExpirationDate.sendKeys(expirationDate);
        txtSecurityCode.sendKeys(securityCode);
        return this;
    }

    public MobileCheckoutPage enterBillingAddressDetails(String fullName, String address1, String city,
                                                         String state, String zip, String country) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtFullName)).sendKeys(fullName);
        txtAddress1.sendKeys(address1);
        txtCity.sendKeys(city);
        txtState.sendKeys(state);
        txtZip.sendKeys(zip);
        txtCountry.sendKeys(country);
        return this;
    }

    public MobileCheckoutPage toPayment() {
        clickProceedButton();
        return this;
    }

    public MobileCheckoutPage reviewOrder() {
        clickProceedButton();
        return this;
    }

    public MobileCheckoutPage placeOrder() {
        clickProceedButton();
        return this;
    }

    public void clickProceedButton() {
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

    public MobileCheckoutPage submitShippingEmpty() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
    }

    public MobileCheckoutPage uncheckBillingSameAsShipping() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(chkBillingSameAsShipping)).click();
        return this;
    }

    public MobileCheckoutPage submitPaymentEmpty() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
    }

    public boolean hasValidationText(String expectedText) {
        try {
            wait(shortWait()).until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(String.format("//android.widget.TextView[@text='%s']", expectedText))));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(txtFullName)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
