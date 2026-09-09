package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileVirtualUsbPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileVirtualUsbSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileVirtualUsbPage mobileVirtualUsbPage;

    public MobileVirtualUsbSteps(MobileProductPage mobileProductPage, MobileVirtualUsbPage mobileVirtualUsbPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileVirtualUsbPage = mobileVirtualUsbPage;
    }

    @When("the user opens Virtual USB from the menu")
    public void the_user_opens_virtual_usb_from_the_menu() {
        mobileProductPage.openVirtualUsb();
    }

    @Then("the Virtual USB screen should display the setup instructions")
    public void the_virtual_usb_screen_should_display_the_setup_instructions() {
        Assert.assertTrue(mobileVirtualUsbPage.isVirtualUsbScreenDisplayed(),
                "Virtual USB Failure: one or more expected instructional elements were not displayed.");
    }
}