package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileFingerprintPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileFingerprintSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileFingerprintPage mobileFingerprintPage;

    public MobileFingerprintSteps(MobileProductPage mobileProductPage, MobileFingerprintPage mobileFingerprintPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileFingerprintPage = mobileFingerprintPage;
    }

    @When("the user opens FingerPrint from the menu")
    public void the_user_opens_fingerprint_from_the_menu() {
        mobileProductPage.openFingerprint();
    }

    @Then("the biometrics alert should be displayed")
    public void the_biometrics_alert_should_be_displayed() {
        Assert.assertTrue(mobileFingerprintPage.isBiometricAlertDisplayed(),
                "FingerPrint Failure: 'Biometrics' alert (title/message) was not displayed after opening the screen.");
    }

    @When("the user dismisses the biometric alert")
    public void the_user_dismisses_the_biometric_alert() {
        mobileFingerprintPage.dismissBiometricAlert();
    }

    @Then("the fingerprint screen should be displayed")
    public void the_fingerprint_screen_should_be_displayed() {
        Assert.assertTrue(mobileFingerprintPage.isFingerprintScreenDisplayed(),
                "FingerPrint Failure: one or more expected elements (title, info, demo info, toggle) were not displayed.");
    }

    @Then("the biometric toggle should be disabled")
    public void the_biometric_toggle_should_be_disabled() {
        Assert.assertTrue(mobileFingerprintPage.isBiometricToggleDisabled(),
                "FingerPrint Failure: biometric toggle was expected to be disabled on this device but was enabled.");
    }
}