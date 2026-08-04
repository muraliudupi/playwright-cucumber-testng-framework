package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileSortDialogPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameAscCL")
    private WebElement nameAscendingOption;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameDesCL")
    private WebElement nameDescendingOption;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/priceAscCL")
    private WebElement priceAscendingOption;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/priceDesCL")
    private WebElement priceDescendingOption;

    public void selectSortOption(String sortOption) {
        ensureElementsInitialized();
        WebElement target = switch (sortOption) {
            case "Name - Ascending" -> nameAscendingOption;
            case "Name - Descending" -> nameDescendingOption;
            case "Price - Ascending" -> priceAscendingOption;
            case "Price - Descending" -> priceDescendingOption;
            default -> throw new IllegalArgumentException("Unknown sort option: " + sortOption);
        };
        wait(longWait()).until(ExpectedConditions.elementToBeClickable(target)).click();
    }
}