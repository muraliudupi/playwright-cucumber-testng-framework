package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileDrawingPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileDrawingSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileDrawingPage mobileDrawingPage;

    public MobileDrawingSteps(MobileProductPage mobileProductPage, MobileDrawingPage mobileDrawingPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileDrawingPage = mobileDrawingPage;
    }

    @When("the user opens Drawing from the menu")
    public void the_user_opens_drawing_from_the_menu() {
        mobileProductPage.openDrawing();
    }

    @Then("the Drawing screen should be displayed")
    public void the_drawing_screen_should_be_displayed() {
        Assert.assertTrue(mobileDrawingPage.isDrawingScreenDisplayed(),
                "Drawing Failure: screen was not displayed after selecting the menu item.");
    }

    @And("the user draws a stroke on the signature pad")
    public void the_user_draws_a_stroke_on_the_signature_pad() {
        mobileDrawingPage.drawStroke();
    }

    @And("the user clears the signature")
    public void the_user_clears_the_signature() {
        mobileDrawingPage.clearSignature();
    }

    @And("the user saves the signature")
    public void the_user_saves_the_signature() {
        mobileDrawingPage.saveSignature();
    }
}