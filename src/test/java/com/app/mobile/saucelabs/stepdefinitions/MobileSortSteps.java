package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.app.mobile.saucelabs.pages.MobileSortDialogPage;
import com.framework.steps.BaseSteps;
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
        if (sortOption.contains("Name")) {
            List<String> titles = mobileProductPage.getVisibleProductTitles();
            List<String> expected = new ArrayList<>(titles);
            expected.sort(sortOption.contains("Ascending")
                    ? String.CASE_INSENSITIVE_ORDER
                    : String.CASE_INSENSITIVE_ORDER.reversed());
            assertEquals(titles, expected, "Sort Failure: visible product titles were not in " + sortOption + " order.");
        } else {
            List<Double> prices = mobileProductPage.getVisibleProductPrices();
            List<Double> expected = new ArrayList<>(prices);
            expected.sort(sortOption.contains("Ascending") ? Comparator.naturalOrder() : Comparator.reverseOrder());
            assertEquals(prices, expected, "Sort Failure: visible product prices were not in " + sortOption + " order.");
        }
    }
}