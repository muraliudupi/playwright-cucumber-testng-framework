package mobile.framework.runners;

import com.framework.runners.AbstractTestNGCucumberRunner;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features/mobile",
        glue = {"mobile.framework.stepdefinitions", "mobile.framework.hooks"},
        tags = "@mobile", //"not @wip",
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:build/reports/cucumber/mobile-cucumber-report.html",
                "json:build/reports/cucumber/mobile-cucumber-report.json"
        },
        monochrome = true
)
public class MobileTestNGRunner extends AbstractTestNGCucumberRunner {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
