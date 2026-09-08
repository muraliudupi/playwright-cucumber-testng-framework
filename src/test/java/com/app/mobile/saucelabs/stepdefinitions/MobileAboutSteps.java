package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileAboutPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileAboutSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileAboutPage mobileAboutPage;

    public MobileAboutSteps(MobileProductPage mobileProductPage, MobileAboutPage mobileAboutPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileAboutPage = mobileAboutPage;
    }

    @When("the user opens the About screen from the menu")
    public void the_user_opens_the_about_screen_from_the_menu() {
        mobileProductPage.openAbout();
    }

    @Then("the About screen should display the app title, version, team, and website information")
    public void the_about_screen_should_display_the_app_title_version_team_and_website_information() {
        Assert.assertTrue(mobileAboutPage.isAboutScreenDisplayed(),
                "About Screen Failure: one or more expected elements (title, icon, version, team, website) were not displayed.");
    }
}