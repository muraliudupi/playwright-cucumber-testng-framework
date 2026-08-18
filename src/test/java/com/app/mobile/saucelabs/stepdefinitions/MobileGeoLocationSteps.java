package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.When;

public class MobileGeoLocationSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;

    public MobileGeoLocationSteps(MobileProductPage mobileProductPage) {
        this.mobileProductPage = mobileProductPage;
    }

    @When("the user opens Geo Location from the menu")
    public void the_user_opens_geo_location_from_the_menu() {
        mobileProductPage.openGeoLocation();
    }
}