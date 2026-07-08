package mobile.framework.stepdefinitions;

import com.framework.stepdefinitions.BaseSteps;
import mobile.framework.pages.MobileLoginPage;
import io.cucumber.java.en.*;
import java.util.Map;

public class LoginSteps extends BaseSteps {

    private final MobileLoginPage loginPage;

    public LoginSteps(MobileLoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @Given("the user is on the mobile login screen")
    public void the_user_is_on_the_mobile_login_screen() {
        loginPage.open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        loginPage.login(username, password);
    }

    @When("the user logs into the mobile app using credentials from data key {string} sheet {string}")
    public void the_user_logs_into_mobile_app_using_credentials_from_data_key(String testCaseId, String sheetName){
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        String username = rowData.get("Username");
        String password = rowData.get("Password");

        loginPage.login(username, password);
    }

    @Then("the mobile dashboard should be displayed")
    public void the_mobile_dashboard_should_be_displayed() {
        loginPage.verifyDashboard();
    }
}