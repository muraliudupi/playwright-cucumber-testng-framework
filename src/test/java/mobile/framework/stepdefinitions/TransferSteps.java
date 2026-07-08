package mobile.framework.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.framework.stepdefinitions.BaseSteps;
import mobile.framework.pages.MobileTransferPage;
import io.cucumber.java.en.*;
import java.util.Map;

public class TransferSteps extends BaseSteps {

    private final MobileTransferPage transferPage;
    private final ScenarioContext context;

    public TransferSteps(MobileTransferPage transferPage, ScenarioContext context) {
        this.transferPage = transferPage;
        this.context = context;
    }

    @And("the user navigates into the mobile app Transfer Funds interface")
    public void the_user_navigates_into_mobile_app_transfer_funds_interface() {
        transferPage.navigateToTransferFunds();
    }

    @And("the user performs a mobile transfer using data from data key {string} sheet {string}")
    public void executes_a_mobile_transfer_using_data_from_sheet(String testCaseId, String sheetName){
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        String amount = rowData.get("Amount");
        String fromAccount = rowData.get("FromAccount");
        String toAccount = rowData.get("ToAccount");

        context.setContext("TX_AMOUNT", amount);
        context.setContext("TX_FROM", fromAccount);
        context.setContext("TX_TO", toAccount);

        transferPage.executeMobileTransfer(amount, fromAccount, toAccount);
    }

    @Then("the mobile app should display a successful transfer message")
    public void the_mobile_transfer_completes_successfully_with_a_confirmation_message() {
        transferPage.verifyTransferLayoutVisible();
        String expectedAmount = context.getStringContext("TX_AMOUNT");
        String actualMessage = transferPage.getActualResultMessage();

        if (!expectedAmount.startsWith("$")) {
            expectedAmount = "$" + String.format("%.2f", Double.parseDouble(expectedAmount));
        }

        String regexAmount = expectedAmount.replace("$", "\\$");
        String validationRegex = "^" + regexAmount + " has been transferred from account #(\\d+) to account #(\\d+)\\.$";

        org.testng.Assert.assertTrue(
                actualMessage.matches(validationRegex),
                String.format("Format Mismatch!\nActual: '%s'\nPattern: '%s'", actualMessage, validationRegex)
        );
    }
}