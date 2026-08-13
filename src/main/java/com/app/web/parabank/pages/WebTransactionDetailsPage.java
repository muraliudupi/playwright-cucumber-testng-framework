package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

public class WebTransactionDetailsPage extends WebBasePage {

    private Locator detailsHeading() { return page().locator("h1.title:has-text('Transaction Details')"); }
    private Locator detailsTable()   { return page().locator("table"); }

    public boolean isTransactionDetailsDisplayed() {
        try {
            detailsHeading().waitFor(new Locator.WaitForOptions()
                    .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTransactionIdFromUrl() {
        String url = page().url();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[?&]id=(\\d+)").matcher(url);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not extract a transaction ID from the Transaction Details URL: " + url);
        }
        return matcher.group(1);
    }

    public String getTransactionDetailsText() {
        return detailsTable().innerText();
    }
}