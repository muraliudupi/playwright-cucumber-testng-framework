package com.app.mobile.saucelabs.pages;

import com.framework.models.Address;
import com.framework.models.PaymentDetails;
import com.framework.utils.MobileScrollUtils;
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

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameET")  private WebElement txtBillingFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/address1ET")  private WebElement txtBillingAddress1;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityET")      private WebElement txtBillingCity;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stateET")     private WebElement txtBillingState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/zipET")       private WebElement txtBillingZip;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryET")   private WebElement txtBillingCountry;

    private final MobileReviewPage mobileReviewPage;

    public MobilePaymentPage(MobileReviewPage mobileReviewPage) {
        this.mobileReviewPage = mobileReviewPage;
    }

    public MobilePaymentPage enterPaymentDetails(PaymentDetails paymentDetails) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtCardHolderName)).sendKeys(paymentDetails.fullName());
        txtCardNumber.sendKeys(paymentDetails.cardNumber());
        txtExpirationDate.sendKeys(paymentDetails.expirationDate());
        txtSecurityCode.sendKeys(paymentDetails.securityCode());
        return this;
    }

    public MobilePaymentPage enterBillingAddressDetails(String billFullName, Address billingAddress) {
        ensureElementsInitialized();
        for (int i = 0; i < 3 && !isElementVisible(txtBillingCountry); i++) {
            MobileScrollUtils.scrollDown(driver());
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

    public MobileReviewPage proceedToReview() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return mobileReviewPage;
    }

    public void submitPaymentEmpty() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
    }
}