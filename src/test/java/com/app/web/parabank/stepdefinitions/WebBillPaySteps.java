package com.app.web.parabank.stepdefinitions;

import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.app.web.parabank.pages.WebBillPayPage;
import com.framework.models.BillPayData;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class WebBillPaySteps extends BaseSteps {

    private final WebBillPayPage webBillPayPage;
    private final ScenarioContext context;

    public WebBillPaySteps(WebBillPayPage webBillPayPage, ScenarioContext context) {
        this.webBillPayPage = webBillPayPage;
        this.context = context;
    }

    @And("the user navigates to Bill Pay and submits a payment using data key {string} sheet {string}")
    public void the_user_pays_a_bill_using_data_key(String testCaseId, String sheetName) {
        BillPayData billPayData = getExcelModelByKey(testCaseId, sheetName, BillPayData::fromMap);

        webBillPayPage.navigateToBillPay();
        String actualFromAccount = webBillPayPage.payBill(
                billPayData.payeeName(),
                billPayData.address().address(),
                billPayData.address().city(),
                billPayData.address().state(),
                billPayData.address().zip(),
                billPayData.phone(),
                billPayData.accountNumber(),
                String.valueOf(billPayData.amount()),
                billPayData.fromAccount()
        );

        context.setContext(ContextKeys.BILLPAY_PAYEE_NAME, billPayData.payeeName());
        context.setContext(ContextKeys.BILLPAY_AMOUNT, String.valueOf(billPayData.amount()));
        context.setContext(ContextKeys.BILLPAY_FROM_ACCOUNT, actualFromAccount);
    }

    @Then("the bill payment is confirmed")
    public void the_bill_payment_is_confirmed() {
        webBillPayPage.verifyPaymentConfirmed();
    }

    @And("the user navigates to Bill Pay and submits without entering any values")
    public void the_user_submits_bill_pay_without_values() {
        webBillPayPage.navigateToBillPay().submitBillPayWithoutValues();
    }

    @And("the user navigates to Bill Pay and submits with invalid entering in Account & Amount fields")
    public void the_user_submits_bill_pay_with_invalid_values() {
        webBillPayPage.navigateToBillPay().submitBillPayWithInvalidValues();
    }
}