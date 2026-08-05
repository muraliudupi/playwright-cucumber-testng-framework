package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileCheckoutPage;
import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.steps.BaseSteps;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class MobileCheckoutSteps extends BaseSteps {

    private final MobileCheckoutPage mobileCheckoutPage;
    private final MobileProductPage mobileProductPage;

    public MobileCheckoutSteps(MobileProductPage mobileProductPage, MobileCheckoutPage mobileCheckoutPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileCheckoutPage = mobileCheckoutPage;
    }

    @When("the user submits the shipping form without entering any values")
    public void the_user_submits_shipping_empty() {
        mobileCheckoutPage.submitShippingEmpty();
    }

    @When("the user unchecks billing same as shipping")
    public void the_user_unchecks_billing_same_as_shipping() {
        mobileCheckoutPage.uncheckBillingSameAsShipping();
    }

    @When("the user submits the payment form without entering any values")
    public void the_user_submits_payment_empty() {
        mobileCheckoutPage.submitPaymentEmpty();
    }

    @Then("the following checkout validation messages are displayed:")
    public void the_following_checkout_validation_messages_are_displayed(DataTable dataTable) {
        List<String> expected = dataTable.asList();
        for (String message : expected) {
            assertTrue(mobileCheckoutPage.hasValidationText(message),
                    "Checkout Validation Failure: expected message '" + message + "' was not displayed.");
        }
    }

    @And("the user proceeds to checkout using the cart's current contents")
    public void the_user_proceeds_checkout_using_cart_current_contents() {
        mobileProductPage.openCart().tapCheckout();
        Assert.assertTrue(mobileCheckoutPage.isDisplayed(),
                "Expected Mobile Checkout screen.");
    }

    @And("the user enters shipping details using data key {string} sheet {string}")
    public void the_user_enters_shipping_details(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        completeShipmentDetailsOnly(rowData);
    }

    private void completeShipmentDetailsOnly(Map<String, String> rowData) {
        mobileCheckoutPage
                .enterShippingDetails(rowData.get("FullName"), rowData.get("Address1"), rowData.get("City"),
                        rowData.get("State"), rowData.get("Zip"), rowData.get("Country"))
                .toPayment();
    }
}