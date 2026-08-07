package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.*;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.models.CheckoutDetails;
import com.framework.models.MobileCartData;
import com.framework.models.MobileCheckoutData;
import com.framework.models.MobileCheckoutGuestData;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MobileCartCheckoutSteps extends BaseSteps {

    private static final String CTX_PRODUCT_LABEL = "CART_PRODUCT_LABEL";

    private final MobileProductPage mobileProductPage;
    private final MobileCartPage mobileCartPage;
    private final MobileLoginPage mobileLoginPage;
    private final MobileShippingPage mobileShippingPage;
    private final MobilePaymentPage mobilePaymentPage;
    private final MobileReviewPage mobileReviewPage;
    private final ScenarioContext context;

    public MobileCartCheckoutSteps(MobileProductPage mobileProductPage,
                                   MobileCartPage mobileCartPage,
                                   MobileShippingPage mobileShippingPage,
                                   MobilePaymentPage mobilePaymentPage,
                                   MobileReviewPage mobileReviewPage,
                                   MobileLoginPage mobileLoginPage,
                                   ScenarioContext context) {
        this.mobileProductPage = mobileProductPage;
        this.mobileCartPage = mobileCartPage;
        this.mobileShippingPage = mobileShippingPage;
        this.mobilePaymentPage = mobilePaymentPage;
        this.mobileReviewPage = mobileReviewPage;
        this.mobileLoginPage = mobileLoginPage;
        this.context = context;
    }

    @When("the user adds a product to the cart using data key {string} sheet {string}")
    public void the_user_adds_a_product_to_the_cart(String testCaseId, String sheetName) {
        MobileCartData mobileCartData = getExcelModelByKey(testCaseId, sheetName, MobileCartData::fromMap);

        String productLabel = mobileCartData.item().productLabel();
        int quantity = mobileCartData.item().quantity();

        context.setContext(CTX_PRODUCT_LABEL, productLabel);
        addProductToCart(productLabel, quantity);
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
        mobileCartPage.removeProduct(productLabel);
    }

    @Then("{string} should no longer be visible in the cart")
    public void product_should_no_longer_be_in_cart(String productLabel) {
        Assert.assertFalse(mobileCartPage.isProductInCart(productLabel),
                "Cart Verification Failure: product was still present after removal.");
    }

    @When("the user removes the added product from the cart")
    public void the_user_removes_the_added_product_from_the_cart() {
        mobileCartPage.removeProduct(context.getStringContext(CTX_PRODUCT_LABEL));
    }

    @Then("the removed product should no longer be visible in the cart")
    public void the_removed_product_should_no_longer_be_visible_in_the_cart() {
        String productLabel = context.getStringContext(CTX_PRODUCT_LABEL);
        Assert.assertFalse(mobileCartPage.isProductInCart(productLabel),
                "Cart Verification Failure: product was still present after removal.");
    }

    @When("the user proceeds to checkout and completes the order using data key {string} sheet {string}")
    public void the_user_completes_checkout(String testCaseId, String sheetName) {
        MobileCheckoutData mobileCheckoutData = getExcelModelByKey(testCaseId, sheetName, MobileCheckoutData::fromMap);
        completeCheckoutFlow(mobileCheckoutData);
    }

    @When("the user proceeds to checkout with different billing address and completes the order using data key {string} sheet {string}")
    public void the_user_completes_checkout_with_different_billing_addr(String testCaseId, String sheetName) {
        MobileCheckoutData mobileCheckoutData = getExcelModelByKey(testCaseId, sheetName, MobileCheckoutData::fromMap);
        completeCheckoutWithDifferentBillingAddress(mobileCheckoutData);
    }

    @When("the user proceeds to checkout as a guest and completes the order using data key {string} sheet {string}")
    public void the_user_completes_guest_checkout(String testCaseId, String sheetName) {
        MobileCheckoutGuestData mobileCheckoutGuestData = getExcelModelByKey(testCaseId, sheetName, MobileCheckoutGuestData::fromMap);
        mobileProductPage.openCart().tapCheckout();
        boolean loginPromptShown = mobileLoginPage.isLoginOptionDisplayed();
        Assert.assertTrue(loginPromptShown,
                "Guest Checkout Failure: expected login screen to appear for an unauthenticated checkout attempt.");

        mobileLoginPage.login(
                mobileCheckoutGuestData.details().username(),
                mobileCheckoutGuestData.details().password()
        );
        enterShippingAndPaymentDetails(mobileCheckoutGuestData).proceedToReview();
        verifyShippingAndPaymentOnReview(mobileCheckoutGuestData);

        mobileReviewPage.placeOrder();
    }

    @Then("the order confirmation should be displayed")
    public void the_order_confirmation_should_be_displayed() {
        Assert.assertTrue(mobileReviewPage.isOrderConfirmationDisplayed(),
                "Order Confirmation Failure: 'Thank You' confirmation screen was not displayed.");
    }

    @When("the user adds {string} in color {string} to the cart with quantity {int}")
    public void the_user_adds_product_with_color_to_cart(String productLabel, String colorName, int quantity) {
        context.setContext(CTX_PRODUCT_LABEL, productLabel);
        mobileProductPage
                .selectProduct(productLabel)
                .selectColor(colorName)
                .setQuantity(quantity)
                .addToCart();
        mobileProductPage.openCart();
    }

    @Then("a color indicator should be displayed for the product in the cart")
    public void a_color_indicator_should_be_displayed() {
        String productLabel = context.getStringContext(CTX_PRODUCT_LABEL);
        Assert.assertTrue(mobileCartPage.isColorIndicatorDisplayedForProduct(productLabel),
                "Color Selection Failure: no color indicator rendered for '" + productLabel + "' in the cart.");
    }

    @When("the user reaches mobile login screen")
    public void the_user_reaches_mobile_login_screen() {
        mobileCartPage.tapCheckout();
        Assert.assertTrue(mobileLoginPage.isLoginOptionDisplayed(),
                "Expected login screen to appear for unauthenticated checkout.");
    }

    @And("the user clicks on Continue Shopping button and Catalog page should be displayed")
    public void the_user_clicks_continue_shopping_Catalog_page_displayed() {
        mobileReviewPage.continueShopping();
        Assert.assertTrue(mobileProductPage.verifyDashboard(),
                "Cross-Platform Validation Failure: Product Catalog header dashboard missing.");
    }

    private void completeCheckoutFlow(MobileCheckoutData mobileCheckoutData) {
        MobilePaymentPage payment = enterTillPaymentDetails(mobileCheckoutData);

        payment.proceedToReview();
        verifyShippingAndPaymentOnReview(mobileCheckoutData);

        mobileReviewPage.placeOrder();
    }

    private void completeCheckoutWithDifferentBillingAddress(MobileCheckoutData mobileCheckoutData) {
        MobilePaymentPage payment = enterTillPaymentDetails(mobileCheckoutData);

        payment.uncheckBillingSameAsShipping()
                .enterBillingAddressDetails(
                        mobileCheckoutData.billFullName(),
                        mobileCheckoutData.billingAddress()
                )
                .proceedToReview();

        verifyShippingAndPaymentOnReview(mobileCheckoutData);
        Assert.assertTrue(mobileReviewPage.billingDetailsMatch(mobileCheckoutData.billFullName(), mobileCheckoutData.billingAddress()),
                "Review Order Failure: billing details did not match what was entered.");

        mobileReviewPage.placeOrder();
    }

    private MobilePaymentPage enterTillPaymentDetails(MobileCheckoutData mobileCheckoutData) {
        String productLabel = mobileCheckoutData.item().productLabel();
        int quantity = mobileCheckoutData.item().quantity();

        context.setContext(CTX_PRODUCT_LABEL, productLabel);
        addProductToCart(productLabel, quantity);
        mobileProductPage.openCart().tapCheckout();

        return enterShippingAndPaymentDetails(mobileCheckoutData);
    }

    private MobilePaymentPage enterShippingAndPaymentDetails(CheckoutDetails checkoutDetails) {
        context.setContext(ContextKeys.CHECKOUT_DETAILS, checkoutDetails);

        mobileShippingPage
                .fillShippingDetails(checkoutDetails.fullName(), checkoutDetails.shippingAddress())
                .proceedToPayment();

        return mobilePaymentPage.enterPaymentDetails(checkoutDetails.paymentDetails());
    }

    private void addProductToCart(String productLabel, int quantity) {
        mobileProductPage
                .selectProduct(productLabel)
                .setQuantity(quantity)
                .addToCart();
    }

    private void verifyShippingAndPaymentOnReview(CheckoutDetails checkoutDetails) {
        Assert.assertTrue(mobileReviewPage.shippingDetailsMatch(checkoutDetails.fullName(), checkoutDetails.shippingAddress()),
                "Review Order Failure: shipping details did not match what was entered.");
        Assert.assertTrue(mobileReviewPage.paymentDetailsMatch(checkoutDetails.paymentDetails()),
                "Review Order Failure: payment details did not match what was entered.");
    }
}
