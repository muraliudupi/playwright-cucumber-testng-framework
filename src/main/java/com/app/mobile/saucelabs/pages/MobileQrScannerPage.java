package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class MobileQrScannerPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/qrCodeTV")
    private WebElement lblQrCodeTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/previewView")
    private WebElement cameraPreviewView;

    private static final By ALLOW_WHILE_USING_APP =
            By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");

    public void grantCameraPermissionIfPrompted() {
        try {
            wait(longWait())
                    .until(ExpectedConditions.elementToBeClickable(ALLOW_WHILE_USING_APP))
                    .click();
            LOG.info("Dialog appeared — permission granted.");
        } catch (Exception e) {
            LOG.info("Dialog didn't appear — permission already granted from a prior run.");
        }
    }

    public boolean isScannerScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblQrCodeTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(cameraPreviewView)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean scannerNavigatedAwayAfterScan(int timeoutSeconds) {
        ensureElementsInitialized();
        try {
            wait(Duration.ofSeconds(timeoutSeconds)).until(ExpectedConditions.invisibilityOf(lblQrCodeTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}