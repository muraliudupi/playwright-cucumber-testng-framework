package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileSortDialogPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.testng.Assert.assertEquals;

public class MobileSortSteps extends BaseSteps {

    private final MobileProductPage mobileProductPage;
    private final MobileSortDialogPage mobileSortDialogPage;

    public MobileSortSteps(MobileProductPage mobileProductPage, MobileSortDialogPage mobileSortDialogPage) {
        this.mobileProductPage = mobileProductPage;
        this.mobileSortDialogPage = mobileSortDialogPage;
    }

    @And("the user sorts products by {string}")
    public void the_user_sorts_products_by(String sortOption) {
        mobileProductPage.openSortDialog();
        mobileSortDialogPage.selectSortOption(sortOption);
    }

    @Then("the visible products should be sorted by {string}")
    public void the_visible_products_should_be_sorted_by(String sortOption) {
        int scrollCount = ConfigReader.getInt("mobile.sort.verification.scroll.count", 3);
        List<MobileProductPage.ProductSummary> products = mobileProductPage.collectAllProducts(scrollCount);

        if (sortOption.contains("Name")) {
            List<String> titles = products.stream().map(MobileProductPage.ProductSummary::title).toList();
            List<String> expected = new ArrayList<>(titles);
            expected.sort(sortOption.contains("Ascending")
                    ? String.CASE_INSENSITIVE_ORDER
                    : String.CASE_INSENSITIVE_ORDER.reversed());
            assertEquals(titles, expected, "Sort Failure: product titles across the scrolled catalog were not in " + sortOption + " order.");
        } else {
            List<Double> prices = products.stream().map(MobileProductPage.ProductSummary::price).toList();
            List<Double> expected = new ArrayList<>(prices);
            expected.sort(sortOption.contains("Ascending") ? Comparator.naturalOrder() : Comparator.reverseOrder());
            assertEquals(prices, expected, "Sort Failure: product prices across the scrolled catalog were not in " + sortOption + " order.");
        }
    }
}