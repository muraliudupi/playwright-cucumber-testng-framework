package com.app.mobile.saucelabs.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MobileFingerprintPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/alertTitle")
    private WebElement lblAlertTitle;

    @AndroidFindBy(id = "android:id/message")
    private WebElement lblAlertMessage;

    @AndroidFindBy(id = "android:id/button1")
    private WebElement btnAlertOk;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricTV")
    private WebElement lblFingerprintTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricInfoTV")
    private WebElement lblFingerprintInfo;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricDemoInfoTV")
    private WebElement lblFingerprintDemoInfo;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricSw")
    private WebElement swBiometricToggle;

    public boolean isBiometricAlertDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getBiometricAlertMessage() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).getText();
    }

    public void dismissBiometricAlert() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnAlertOk)).click();
    }

    public boolean isFingerprintScreenDisplayed() {
        ensureElementsInitialized();
        if(isBiometricAlertDisplayed()){
            dismissBiometricAlert();
        }
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintInfo)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintDemoInfo)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBiometricToggleDisabled() {
        ensureElementsInitialized();
        return !wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isEnabled();
    }

    public boolean isBiometricToggleEnabled() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isEnabled();
    }

    public void tapBiometricToggle() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(swBiometricToggle)).click();
        // The tap triggers the OS BiometricPrompt dialog, which takes a moment to render —
        // capture what's actually on screen here for diagnosis if authentication still fails.
        sleepBriefly();
        captureAuthDiagnostics("after-toggle-tap");
    }

    public void simulateFingerprintTouch() {
        ((AndroidDriver) driver()).fingerPrint(1);
        // Let the prompt process the simulated touch and any resulting UI transition settle
        // before the caller checks the toggle state.
        sleepBriefly();
        captureAuthDiagnostics("after-touch");
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Best-effort diagnostics for the authentication flow, parallel to
    // MobileFingerprintEnroller's own capture — same reasoning: this interacts with an OS-level
    // biometric prompt, not app UI we fully control, so a failure here should leave real
    // evidence of the actual screen state rather than just an assertion message.
    private void captureAuthDiagnostics(String stage) {
        try {
            Path dir = Paths.get("build", "reports", "fingerprint-auth-debug");
            Files.createDirectories(dir);

            String pageSource = driver().getPageSource();
            Files.writeString(dir.resolve(stage + "-pagesource.xml"), pageSource == null ? "" : pageSource);

            byte[] screenshot = driver().getScreenshotAs(OutputType.BYTES);
            Files.write(dir.resolve(stage + "-screenshot.png"), screenshot);
        } catch (Exception ignored) {
            // Diagnostics are best-effort only — never let capture failure affect the test.
        }
    }

    public boolean isBiometricToggleOn() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isSelected();
    }
}