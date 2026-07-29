package com.app.web.parabank.pages;

import com.microsoft.playwright.Locator;
import java.math.BigDecimal;

public class WebAdministrationPage extends WebBasePage {

    private Locator adminPageLink() {
        return page().locator("a:has-text('Admin Page')");
    }

    private Locator loanProcessor() { return page().locator("#loanProcessor"); }

    private Locator threshold() { return page().locator("#loanProcessorThreshold"); }

    public record LoanProcess(String loanProcessor, BigDecimal threshold) {
    }

    public WebAdministrationPage navigateToAdministration() {
        adminPageLink().click();
        waitUntilReady(threshold());
        return this;
    }

    public LoanProcess findloanProcessorAndthreshold() {
        String loanProcessor = loanProcessor().inputValue();
        BigDecimal threshold = new BigDecimal(threshold().inputValue().trim());

        LOG.info("Application Settings: Loan Processor - '{}' with Threshold - {}%.", loanProcessor, threshold);
        return new LoanProcess(loanProcessor, threshold);
    }

}
