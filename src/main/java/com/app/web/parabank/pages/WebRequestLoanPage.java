package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

public class WebRequestLoanPage extends WebBasePage {

    private Locator requestLoanLink() { return page().locator("a:has-text('Request Loan')"); }
    private Locator loanAmount()      { return page().locator("#amount"); }
    private Locator downPayment()     { return page().locator("#downPayment"); }
    private Locator fromAccountDropdown() { return page().locator("#fromAccountId"); }
    private Locator applyButton()     { return page().locator("input[value='Apply Now']"); }
    private Locator resultContainer() { return page().locator("#requestLoanResult"); }
    private Locator loanStatusHeading() { return resultContainer().locator("h1.title"); }
    private Locator newAccountId()      { return page().locator("#newAccountId"); }

    public WebRequestLoanPage navigateToRequestLoan() {
        requestLoanLink().click();
        waitUntilReady(applyButton());
        return this;
    }

    public WebRequestLoanPage requestLoan(String amount, String downPaymentValue, String fromAccount) {
        loanAmount().fill(amount);
        downPayment().fill(downPaymentValue);

        selectAccountWithFallback(fromAccountDropdown(), fromAccount, 0);

        applyButton().click();
        loanStatusHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
        return this;
    }

    public WebRequestLoanPage requestLoan(String amount, String downPaymentValue) {
        loanAmount().fill(amount);
        downPayment().fill(downPaymentValue);

        applyButton().click();
        loanStatusHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
        return this;
    }

    public boolean isLoanApproved() {
        return resultContainer().getByText("Approved", new Locator.GetByTextOptions().setExact(true)).count() > 0;
    }

    public boolean isLoanDenied() {
        return resultContainer().getByText("Denied", new Locator.GetByTextOptions().setExact(true)).count() > 0;
    }

    public String getNewLoanAccountId() {
        return newAccountId().isVisible() ? newAccountId().innerText().trim() : null;
    }

    public void applyForLoanWithoutValues() {
        applyButton().click();
    }

    public void applyForLoanWithInvalidValues() {
        loanAmount().fill("ASDFG");
        downPayment().fill("QWERT");
        applyButton().click();
    }
}
