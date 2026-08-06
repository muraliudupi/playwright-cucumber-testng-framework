package com.app.mobile.saucelabs.pages;

import com.framework.models.Address;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileShippingPage extends MobileBasePage {

    private final MobilePaymentPage mobilePaymentPage;

    public MobileShippingPage(MobilePaymentPage mobilePaymentPage) {
        this.mobilePaymentPage = mobilePaymentPage;
    }

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/fullNameET")  private WebElement txtFullName;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/address1ET")  private WebElement txtAddress1;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cityET")      private WebElement txtCity;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stateET")     private WebElement txtState;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/zipET")       private WebElement txtZip;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/countryET")   private WebElement txtCountry;
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/paymentBtn")  private WebElement btnProceed;

    // Refactored Method: Accepts fullName and Address domain model
    public MobileShippingPage fillShippingDetails(String fullName, Address address) {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.visibilityOf(txtFullName)).sendKeys(fullName);
        txtAddress1.sendKeys(address.address());
        txtCity.sendKeys(address.city());
        txtState.sendKeys(address.state());
        txtZip.sendKeys(address.zip());
        txtCountry.sendKeys(address.country());
        return this;
    }

    public MobilePaymentPage proceedToPayment() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
        return mobilePaymentPage;
    }

    public void submitShippingEmpty() {
        ensureElementsInitialized();
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(btnProceed)).click();
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