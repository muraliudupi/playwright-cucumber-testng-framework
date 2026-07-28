package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebUpdateContactPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.Map;

public class WebUpdateContactSteps extends BaseSteps {

    private final WebUpdateContactPage webUpdateContactPage;

    public WebUpdateContactSteps(WebUpdateContactPage webUpdateContactPage) {
        this.webUpdateContactPage = webUpdateContactPage;
    }

    @And("the user updates contact info using data key {string} sheet {string}")
    public void the_user_updates_contact_info_using_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        webUpdateContactPage.navigateToUpdateContact().updateContactInfo(
                rowData.get("FirstName"), rowData.get("LastName"), rowData.get("Address"),
                rowData.get("City"), rowData.get("State"), rowData.get("ZipCode"), rowData.get("Phone"));
    }

    @Then("the contact info update is confirmed")
    public void the_contact_info_update_is_confirmed() {
        webUpdateContactPage.verifyContactUpdated();
    }

    @And("the user clears all contact fields and submits")
    public void the_user_clears_all_contact_fields_and_submits() {
        webUpdateContactPage.navigateToUpdateContact().clearAllFieldsAndSubmit();
    }
}