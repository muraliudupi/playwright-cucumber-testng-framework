package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileVirtualUsbPage extends MobileBasePage {

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Virtual USB is a tool developed by Sauce Labs, to allow you 'adb' access to the device. With it, you can use your development suite with a remote device, as if it were connected physically to your computer.\")")
    private WebElement lblIntro;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"To use this part of the demo, make sure you started this session using virtual-usb-client.jar\")")
    private WebElement lblClientJarInstruction;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Next, run these commands from your terminal:\")")
    private WebElement lblCommandsHeading;

    @AndroidFindBy(uiAutomator = "new UiSelector().textContains(\"adb forward tcp:40000 tcp:50000\")")
    private WebElement lblCommandSnippet;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/virtual_usb_message")
    private WebElement lblVirtualUsbMessage;

    public boolean isVirtualUsbScreenDisplayed() {
        ensureElementsInitialized();
        boolean introDisplayed = isElementDisplayed(lblIntro, "Intro paragraph");
        boolean clientJarDisplayed = isElementDisplayed(lblClientJarInstruction, "Client jar instruction");
        boolean commandsHeadingDisplayed = isElementDisplayed(lblCommandsHeading, "Commands heading");
        boolean commandSnippetDisplayed = isElementDisplayed(lblCommandSnippet, "Command snippet");
        boolean statusMessageDisplayed = isElementDisplayed(lblVirtualUsbMessage, "Virtual USB status message");
        return introDisplayed && clientJarDisplayed && commandsHeadingDisplayed
                && commandSnippetDisplayed && statusMessageDisplayed;
    }

    private boolean isElementDisplayed(WebElement element, String label) {
        try {
            boolean displayed = wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(element)).isDisplayed();
            if (!displayed) {
                LOG.warn("Virtual USB Screen: '{}' element located but not displayed.", label);
            }
            return displayed;
        } catch (Exception e) {
            LOG.warn("Virtual USB Screen: '{}' element not found within timeout — {}", label, e.getClass().getSimpleName());
            return false;
        }
    }

    public String getVirtualUsbMessageText() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblVirtualUsbMessage)).getText();
    }
}