package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;

public class WebBillPayPage extends WebBasePage {

    private Locator billPayLink() {
        return page().locator("a:has-text('Bill Pay')");
    }

    private Locator payeeName() {
        return page().locator("input[name='payee.name']");
    }

    private Locator payeeAddress() {
        return page().locator("input[name='payee.address.street']");
    }

    private Locator payeeCity() {
        return page().locator("input[name='payee.address.city']");
    }

    private Locator payeeState() {
        return page().locator("input[name='payee.address.state']");
    }

    private Locator payeeZip() {
        return page().locator("input[name='payee.address.zipCode']");
    }

    private Locator payeePhone() {
        return page().locator("input[name='payee.phoneNumber']");
    }

    private Locator payeeAccount() {
        return page().locator("input[name='payee.accountNumber']");
    }

    private Locator verifyAccount() {
        return page().locator("input[name='verifyAccount']");
    }

    private Locator amount() {
        return page().locator("input[name='amount']");
    }

    private Locator fromAccountDropdown() {
        return page().locator("select[name='fromAccountId']");
    }

    private Locator sendPaymentButton() {
        return page().locator("input[value='Send Payment']");
    }

    private Locator confirmationHeading() {
        return page().locator("#billpayResult h1.title:has-text('Bill Payment Complete')");
    }

    public WebBillPayPage navigateToBillPay() {
        billPayLink().click();
        page().waitForLoadState(LoadState.NETWORKIDLE);
        payeeName().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public String payBill(String payeeNameValue, String address, String city, String state,
                          String zip, String phone, String accountNumber, String amountValue, String fromAccount) {
        payeeName().fill(payeeNameValue);
        payeeAddress().fill(address);
        payeeCity().fill(city);
        payeeState().fill(state);
        payeeZip().fill(zip);
        payeePhone().fill(phone);
        payeeAccount().fill(accountNumber);
        verifyAccount().fill(accountNumber);
        amount().fill(amountValue);

        String actualFromAccount = selectAccountWithFallback(fromAccountDropdown(), fromAccount, 0);

        sendPaymentButton().click();
        return actualFromAccount;
    }

    public void verifyPaymentConfirmed() {
        confirmationHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
    }

    public void submitBillPayWithoutValues() {
        sendPaymentButton().click();
    }

    public void submitBillPayWithInvalidValues() {
        payeeAccount().fill("@#$%^&");
        verifyAccount().fill("@#$%^&");
        amount().fill("@#$%^&");
        sendPaymentButton().click();
    }
}