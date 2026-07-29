package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
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
        page().waitForLoadState(LoadState.NETWORKIDLE);
        threshold().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public LoanProcess findloanProcessorAndthreshold() {

        String loanProcessor = ""; //assign value in loanProcessor dropdown.
        BigDecimal threshold = null; //assign value in threshold textbox.

        LOG.info("Application Settings: Loan Processor - '{}' with Threshold - {}%.", loanProcessor, threshold);
        return new LoanProcess(loanProcessor, threshold);
    }

}
