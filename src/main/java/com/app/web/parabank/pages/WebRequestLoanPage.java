package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WebRequestLoanPage extends WebBasePage {

    private Locator requestLoanLink() { return page().locator("a:has-text('Request Loan')"); }
    private Locator loanAmount()      { return page().locator("#amount"); }
    private Locator downPayment()     { return page().locator("#downPayment"); }
    private Locator fromAccountDropdown() { return page().locator("#fromAccountId"); }
    private Locator applyButton()     { return page().locator("input[value='Apply Now']"); }
    private Locator loanStatusHeading() { return page().locator("#requestLoanResult h1.title"); }
    private Locator newAccountId()      { return page().locator("#newAccountId"); }

    public WebRequestLoanPage navigateToRequestLoan() {
        requestLoanLink().click();
        loanAmount().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public WebRequestLoanPage requestLoan(String amount, String downPaymentValue, String fromAccount) {
        loanAmount().fill(amount);
        downPayment().fill(downPaymentValue);

        boolean requestedAccountFound = true;
        try {
            Locator optionTarget = fromAccountDropdown().locator(String.format("option[value='%s']", fromAccount));
            optionTarget.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(ConfigReader.getInt("web.dropdown.wait.timeout.ms", 3000)));
            fromAccountDropdown().selectOption(fromAccount);
        } catch (Exception e) {
            requestedAccountFound = false;
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        if (!requestedAccountFound) {
            LOG.warn("Requested FromAccount '{}' was not available in the dropdown; framework substituted account '{}' instead.",
                    fromAccount, fromAccountDropdown().inputValue());
        }

        applyButton().click();
        loanStatusHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
        return this;
    }

    public boolean isLoanApproved() {
        return loanStatusHeading().innerText().contains("Approved");
    }

    public String getNewLoanAccountId() {
        return newAccountId().isVisible() ? newAccountId().innerText().trim() : null;
    }
}