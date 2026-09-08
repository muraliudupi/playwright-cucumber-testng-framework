package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileResetAppPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileResetAppSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileResetAppPage mobileResetAppPage;

    public MobileResetAppSteps(MobileProductPage mobileProductPage, MobileResetAppPage mobileResetAppPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileResetAppPage = mobileResetAppPage;
    }

    @When("the user opens Reset App State from the menu")
    public void the_user_opens_reset_app_state_from_the_menu() {
        mobileProductPage.openResetAppState();
    }

    @Then("the reset app confirmation alert should be displayed")
    public void the_reset_app_confirmation_alert_should_be_displayed() {
        Assert.assertTrue(mobileResetAppPage.isConfirmationAlertDisplayed(),
                "Reset App Failure: confirmation alert (title/message) was not displayed after selecting the menu item.");
    }

    @When("the user confirms the app reset")
    public void the_user_confirms_the_app_reset() {
        mobileResetAppPage.confirmReset();
    }

    @When("the user cancels the app reset")
    public void the_user_cancels_the_app_reset() {
        mobileResetAppPage.cancelReset();
    }

    @Then("the reset app success alert should be displayed")
    public void the_reset_app_success_alert_should_be_displayed() {
        Assert.assertTrue(mobileResetAppPage.isSuccessAlertDisplayed(),
                "Reset App Failure: success alert was not displayed after confirming the reset.");
    }

    @When("the user dismisses the reset success alert")
    public void the_user_dismisses_the_reset_success_alert() {
        mobileResetAppPage.dismissSuccessAlert();
    }

    @Then("the mobile product catalog should still be displayed")
    public void the_mobile_product_catalog_should_still_be_displayed() {
        Assert.assertTrue(mobileProductPage.verifyDashboard(),
                "Reset App Failure: Product Catalog header was not displayed after cancelling the reset.");
    }
}