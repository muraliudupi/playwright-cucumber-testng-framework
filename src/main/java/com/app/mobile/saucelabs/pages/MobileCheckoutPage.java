package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileCheckoutPage extends MobileBasePage {

    // --- Checkout Info (shipping address) ---
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameET")  private WebElement txtFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/address1ET")  private WebElement txtAddress1;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityET")      private WebElement txtCity;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stateET")     private WebElement txtState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/zipET")       private WebElement txtZip;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryET")   private WebElement txtCountry;

    // --- Payment ---
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")  private WebElement txtFulName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cardNumberET")     private WebElement txtCardNumber;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/expirationDateET") private WebElement txtExpirationDate;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/securityCodeET")   private WebElement txtSecurityCode;

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
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
    }

    public MobileCheckoutPage enterPaymentDetails(String fullName, String cardNumber, String expirationDate, String securityCode) {
        ensureElementsInitialized();

        wait(longWait()).until(ExpectedConditions.visibilityOf(txtFulName)).sendKeys(fullName);
        txtCardNumber.sendKeys(cardNumber);
        txtExpirationDate.sendKeys(expirationDate);
        txtSecurityCode.sendKeys(securityCode);
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();

        return this;
    }

    public MobileCheckoutPage placeOrder() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return this;
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