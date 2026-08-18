package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class MobileDrawingPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/drawingTV")
    private WebElement lblTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/signature_pad")
    private WebElement signaturePad;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/clearBtn")
    private WebElement btnClear;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/saveBtn")
    private WebElement btnSave;

    private static final By ALLOW =
            By.id("com.android.permissioncontroller:id/permission_allow_button");

    public void grantMediaPermissionIfPrompted() {
        try {
            wait(longWait())
                    .until(ExpectedConditions.elementToBeClickable(ALLOW))
                    .click();
            LOG.info("Dialog appeared — permission granted.");
        } catch (Exception e) {
            LOG.info("Dialog didn't appear — permission already granted from a prior run.");
        }
    }

    public boolean isDrawingScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void drawStroke() {
        ensureElementsInitialized();
        Rectangle padBounds = wait(longWait()).until(ExpectedConditions.visibilityOf(signaturePad)).getRect();

        int startX = padBounds.getX() + padBounds.getWidth() / 4;
        int endX = padBounds.getX() + (padBounds.getWidth() * 3) / 4;
        int y = padBounds.getY() + padBounds.getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence stroke = new Sequence(finger, 0);
        stroke.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        stroke.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        stroke.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), endX, y));
        stroke.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver().perform(List.of(stroke));
    }

    public void clearSignature() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnClear)).click();
    }

    public void saveSignature() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnSave)).click();
    }
}