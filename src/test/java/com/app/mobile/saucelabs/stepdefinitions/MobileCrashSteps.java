package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileCrashPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileCrashSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileCrashPage mobileCrashPage;

    public MobileCrashSteps(MobileProductPage mobileProductPage, MobileCrashPage mobileCrashPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileCrashPage = mobileCrashPage;
    }

    @When("the user opens Crash app from the menu")
    public void the_user_opens_crash_app_from_the_menu() {
        mobileProductPage.openCrashApp();
    }

    @Then("the Crashes screen should display both crash trigger buttons")
    public void the_crashes_screen_should_display_both_crash_trigger_buttons() {
        Assert.assertTrue(mobileCrashPage.isCrashScreenDisplayed(),
                "Crash App Failure: 'Crashes' title and/or one of the crash trigger buttons were not displayed.");
    }
}