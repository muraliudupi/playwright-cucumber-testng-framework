package mobile.framework.pages;

import mobile.framework.core.MobileDriverFactory;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class MobileTransferPage {

    public MobileTransferPage() {
        // Enforces implicit contextual locator evaluations across OS targets
        PageFactory.initElements(new AppiumFieldDecorator(MobileDriverFactory.getDriver(),
                Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(id = "com.parabank.app:id/amount_input")
    @iOSXCUITFindBy(accessibility = "AmountInputField")
    private WebElement amountField;

    @AndroidFindBy(accessibility = "Transfer Options Button")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='TransferOptions']")
    private WebElement transferButton;

    public MobileTransferPage navigateToTransferFunds() {
        // Need to be Implemented
        return this;
    }

    public void executeMobileTransfer(String amount, String fromAccount, String toAccount) {
        amountField.sendKeys(amount);
        // Need to be Implemented
        transferButton.click();
    }

    public void verifyTransferLayoutVisible() {
        // Need to be Implemented
    }

    public String getActualResultMessage() {
        String message = null;
        // Need to be Implemented
        return message;
    }
}