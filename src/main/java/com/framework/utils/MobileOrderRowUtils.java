package com.framework.utils;

import org.openqa.selenium.By;

public final class MobileOrderRowUtils {

    private MobileOrderRowUtils() {}

    private static final String PRODUCT_TITLE_ID = "com.saucelabs.mydemoapp.android:id/titleTV";
    private static final String PRODUCT_QTY_ID   = "com.saucelabs.mydemoapp.android:id/noTV";
    private static final String COLOR_ICON_DESC  = "Displays color of selected product";

    public static By productTitleLocator(String productLabel) {
        return By.xpath(String.format(
                "//android.widget.TextView[@resource-id='%s' and @text='%s']",
                PRODUCT_TITLE_ID, productLabel));
    }

    public static By productQuantityLocator(String productLabel) {
        return By.xpath(String.format(
                "//android.widget.TextView[@text='%s']"
                        + "/ancestor::android.view.ViewGroup[.//android.widget.TextView[@resource-id='%s']][1]"
                        + "//android.widget.TextView[@resource-id='%s']",
                productLabel, PRODUCT_QTY_ID, PRODUCT_QTY_ID));
    }

    public static By productColorIconLocator(String productLabel) {
        return By.xpath(String.format(
                "//android.widget.TextView[@text='%s']"
                        + "/ancestor::android.view.ViewGroup[.//android.widget.ImageView[@content-desc='%s']][1]"
                        + "//android.widget.ImageView[@content-desc='%s']",
                productLabel, COLOR_ICON_DESC, COLOR_ICON_DESC));
    }
}