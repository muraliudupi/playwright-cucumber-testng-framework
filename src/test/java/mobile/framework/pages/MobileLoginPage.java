package mobile.framework.pages;

import mobile.framework.core.MobileDriverFactory;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class MobileLoginPage {

    public MobileLoginPage() {
        // Enforces implicit contextual locator evaluations across OS targets
        PageFactory.initElements(new AppiumFieldDecorator(MobileDriverFactory.getDriver(),
                Duration.ofSeconds(10)), this);
    }

    public MobileLoginPage open() {
        //Need to be Implemented
        return this;
    }

    public MobileLoginPage login(String username, String password) {
        //Need to be Implemented
        return this;
    }

    public void verifyDashboard() {
        //Need to be Implemented
    }

}