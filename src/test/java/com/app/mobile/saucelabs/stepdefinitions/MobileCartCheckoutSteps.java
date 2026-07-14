package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.*;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.Map;

public class MobileCartCheckoutSteps extends BaseSteps {

    private static final String CTX_PRODUCT_LABEL = "CART_PRODUCT_LABEL";

    private final MobileProductPage mobileProductPage;
    private final MobileProductDetailPage mobileProductDetailPage;
    private final MobileCartPage mobileCartPage;
    private final MobileCheckoutPage mobileCheckoutPage;
    private final MobileLoginPage mobileLoginPage;
    private final ScenarioContext context;

    public MobileCartCheckoutSteps(MobileProductPage mobileProductPage,
                                   MobileProductDetailPage mobileProductDetailPage,
                                   MobileCartPage mobileCartPage,
                                   MobileCheckoutPage mobileCheckoutPage,
                                   MobileLoginPage mobileLoginPage,
                                   ScenarioContext context) {
        this.mobileProductPage = mobileProductPage;
        this.mobileProductDetailPage = mobileProductDetailPage;
        this.mobileCartPage = mobileCartPage;
        this.mobileCheckoutPage = mobileCheckoutPage;
        this.mobileLoginPage = mobileLoginPage;
        this.context = context;
    }

    @When("the user adds a product to the cart using data key {string} sheet {string}")
    public void the_user_adds_a_product_to_the_cart(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        String productLabel = rowData.get("ProductLabel");
        int quantity = Integer.parseInt(rowData.get("Quantity"));

        context.setContext(CTX_PRODUCT_LABEL, productLabel);

        mobileProductPage
                .selectProduct(productLabel)
                .setQuantity(quantity)
                .addToCart();

        mobileProductPage.openCart();
    }

    @Then("the product should be visible in the cart")
    public void the_product_should_be_visible_in_the_cart() {
        String productLabel = context.getStringContext(CTX_PRODUCT_LABEL);
        Assert.assertTrue(mobileCartPage.isProductInCart(productLabel),
                "Cart Verification Failure: added product was not found in the cart list.");
    }

    @Given("the user has a product already added to the cart using data key {string} sheet {string}")
    public void the_user_has_a_product_already_in_the_cart(String testCaseId, String sheetName) {
        the_user_adds_a_product_to_the_cart(testCaseId, sheetName);
    }

    @When("the user removes {string} from the cart")
    public void the_user_removes_product_from_the_cart(String productLabel) {
        mobileProductPage.openCart().removeProduct(productLabel);
    }

    @Then("{string} should no longer be visible in the cart")
    public void product_should_no_longer_be_in_cart(String productLabel) {
        Assert.assertFalse(mobileCartPage.isProductInCart(productLabel),
                "Cart Verification Failure: product was still present after removal.");
    }

    @When("the user proceeds to checkout and completes the order using data key {string} sheet {string}")
    public void the_user_completes_checkout(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        completeCheckoutFlow(rowData);
    }

    @When("the user proceeds to checkout as a guest and completes the order using data key {string} sheet {string}")
    public void the_user_completes_guest_checkout(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        mobileProductPage.openCart().tapCheckout();

        boolean loginPromptShown = mobileLoginPage.isLoginOptionDisplayed();
        Assert.assertTrue(loginPromptShown,
                "Guest Checkout Failure: expected login screen to appear for an unauthenticated checkout attempt.");

        mobileLoginPage.login(rowData.get("Username"), rowData.get("Password"));
        completeCheckoutDetailsOnly(rowData);
    }

    @Then("the order confirmation should be displayed")
    public void the_order_confirmation_should_be_displayed() {
        Assert.assertTrue(mobileCheckoutPage.isOrderConfirmationDisplayed(),
                "Order Confirmation Failure: 'Thank You' confirmation screen was not displayed.");
    }

    private void completeCheckoutFlow(Map<String, String> rowData) {
        String productLabel = rowData.get("ProductLabel");
        context.setContext(CTX_PRODUCT_LABEL, productLabel);

        mobileProductPage
                .selectProduct(productLabel)
                .setQuantity(Integer.parseInt(rowData.get("Quantity")))
                .addToCart();

        mobileProductPage.openCart().tapCheckout();
        completeCheckoutDetailsOnly(rowData);
    }

    private void completeCheckoutDetailsOnly(Map<String, String> rowData) {
        mobileCheckoutPage
                .enterShippingDetails(rowData.get("FullName"), rowData.get("Address1"), rowData.get("City"),
                        rowData.get("State"), rowData.get("Zip"), rowData.get("Country"))
                .enterPaymentDetails(rowData.get("FullName"), rowData.get("CardNumber"), rowData.get("ExpirationDate"), rowData.get("SecurityCode"))
                .placeOrder();
    }
}