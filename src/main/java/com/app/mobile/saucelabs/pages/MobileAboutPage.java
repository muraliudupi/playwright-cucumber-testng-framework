package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileAboutPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/aboutTV")
    private WebElement lblAboutTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/titleIV")
    private WebElement imgAppIcon;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/versionTV")
    private WebElement lblVersion;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/teamIV")
    private WebElement imgTeam;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/webTV")
    private WebElement lblWebsite;

    public boolean isAboutScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAboutTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(imgAppIcon)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblVersion)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(imgTeam)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblWebsite)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getVersionText() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblVersion)).getText();
    }
}