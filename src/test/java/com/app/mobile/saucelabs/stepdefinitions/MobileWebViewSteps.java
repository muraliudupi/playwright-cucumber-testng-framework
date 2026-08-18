package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileWebViewPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileWebViewSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileWebViewPage mobileWebViewPage;

    public MobileWebViewSteps(MobileProductPage mobileProductPage, MobileWebViewPage mobileWebViewPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileWebViewPage = mobileWebViewPage;
    }

    @When("the user opens WebView from the menu")
    public void the_user_opens_webview_from_the_menu() {
        mobileProductPage.openWebView();
    }

    @Then("the WebView screen should be displayed")
    public void the_webview_screen_should_be_displayed() {
        Assert.assertTrue(mobileWebViewPage.isWebViewScreenDisplayed(),
                "WebView Failure: screen was not displayed after selecting the menu item.");
    }

    @And("the user navigates to the configured test site in the WebView")
    public void the_user_navigates_to_the_configured_test_site_in_the_webview() {
        String url = ConfigReader.get("mobile.webview.test.url", "https://www.saucelabs.com");
        mobileWebViewPage.enterUrl(url);
        mobileWebViewPage.tapGoToSite();
    }

    @Then("the page load should complete")
    public void the_page_load_should_complete() {
        int timeoutSeconds = ConfigReader.getInt("mobile.webview.load.timeout.sec", 20);
        Assert.assertTrue(mobileWebViewPage.waitForPageLoadToComplete(timeoutSeconds),
                "WebView Failure: page load did not complete within " + timeoutSeconds + "s.");
    }

    @And("the loaded page should match the configured test site")
    public void the_loaded_page_should_match_the_configured_test_site() {
        String url = ConfigReader.get("mobile.webview.test.url", "https://www.saucelabs.com");
        int timeoutSeconds = ConfigReader.getInt("mobile.webview.load.timeout.sec", 20);
        Assert.assertTrue(mobileWebViewPage.verifyLoadedUrlMatches(url, timeoutSeconds),
                "WebView Failure: loaded page did not match expected URL '" + url + "'.");
    }
}