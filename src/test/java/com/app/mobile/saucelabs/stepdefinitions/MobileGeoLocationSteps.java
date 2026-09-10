package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileGeoLocationPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileGeoLocationSteps extends BaseSteps {

    // Tolerance for comparing injected vs. displayed coordinates — accounts for the app's display
    // rounding, not environment variance, so it's a fixed constant rather than a config property.
    private static final double COORDINATE_TOLERANCE_DEGREES = 0.0001;

    private final MobileProductPage mobileProductPage;
    private final MobileGeoLocationPage mobileGeoLocationPage;

    public MobileGeoLocationSteps(MobileProductPage mobileProductPage, MobileGeoLocationPage mobileGeoLocationPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileGeoLocationPage = mobileGeoLocationPage;
    }

    @When("the user opens Geo Location from the menu")
    public void the_user_opens_geo_location_from_the_menu() {
        mobileProductPage.openGeoLocation();
    }

    @Then("the Geo Location screen should be displayed")
    public void the_geo_location_screen_should_be_displayed() {
        Assert.assertTrue(mobileGeoLocationPage.isGeoLocationScreenDisplayed(),
                "Geo Location Failure: screen was not displayed after selecting the menu item.");
    }

    @And("the latitude and longitude should be populated")
    public void the_latitude_and_longitude_should_be_populated() {
        int timeoutSeconds = ConfigReader.getInt("mobile.geolocation.coordinates.timeout.sec", 15);
        Assert.assertTrue(mobileGeoLocationPage.coordinatesArePopulated(timeoutSeconds),
                "Geo Location Failure: latitude/longitude did not populate within " + timeoutSeconds + "s.");
    }

    @And("the user stops observing the location")
    public void the_user_stops_observing_the_location() {
        mobileGeoLocationPage.stopObserving();
    }

    @And("the user starts observing the location")
    public void the_user_starts_observing_the_location() {
        mobileGeoLocationPage.startObserving();
    }

    @When("the device location is set to latitude {double} and longitude {double}")
    public void the_device_location_is_set_to(double latitude, double longitude) {
        mobileGeoLocationPage.setDeviceLocation(latitude, longitude);
    }

    @Then("the displayed coordinates should match latitude {double} and longitude {double}")
    public void the_displayed_coordinates_should_match(double latitude, double longitude) {
        int timeoutSeconds = ConfigReader.getInt("mobile.geolocation.coordinates.timeout.sec", 15);

        Assert.assertTrue(
                mobileGeoLocationPage.displayedCoordinatesMatch(latitude, longitude, COORDINATE_TOLERANCE_DEGREES, timeoutSeconds),
                String.format("Geo Location Failure: displayed coordinates did not settle to latitude=%s, longitude=%s within %ds.",
                        latitude, longitude, timeoutSeconds));
    }
}