package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileQrScannerPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import com.framework.utils.VirtualScenePosterUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileQrScannerSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileQrScannerPage mobileQrScannerPage;

    public MobileQrScannerSteps(MobileProductPage mobileProductPage, MobileQrScannerPage mobileQrScannerPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileQrScannerPage = mobileQrScannerPage;
    }

    @Given("the emulator virtual scene camera shows a QR code image")
    public void the_emulator_virtual_scene_camera_shows_a_qr_code_image() throws Exception {
        String posterImagePath = ConfigReader.get("mobile.qr.scanner.poster.image.path",
                "src/test/resources/qrcodes/My-Git.png");
        VirtualScenePosterUtils.setVirtualScenePoster(posterImagePath);
    }

    @When("the user opens the QR code scanner from the menu")
    public void the_user_opens_the_qr_code_scanner_from_the_menu() {
        mobileProductPage.openQrScanner();
    }

    @Then("the QR scanner screen should be displayed")
    public void the_qr_scanner_screen_should_be_displayed() {
        Assert.assertTrue(mobileQrScannerPage.isScannerScreenDisplayed(),
                "QR Scanner Failure: scanner screen was not displayed after selecting the menu item.");
    }

    @Then("a scanned QR code should navigate away from the scanner")
    public void a_scanned_qr_code_should_navigate_away_from_the_scanner() {
        int timeoutSeconds = ConfigReader.getInt("mobile.qr.scan.timeout.sec", 15);
        Assert.assertTrue(mobileQrScannerPage.scannerNavigatedAwayAfterScan(timeoutSeconds),
                "QR Scanner Failure: scanner did not navigate away within " + timeoutSeconds
                        + "s — requires the test environment's camera to be fed a QR code (not yet configured).");
    }
}