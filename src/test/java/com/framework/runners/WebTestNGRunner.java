package com.framework.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features/web",
        glue = {"com.app.web.parabank.stepdefinitions", "com.framework.hooks.web"},
        tags = "not @mobile and not @wip",
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:build/reports/cucumber/web-cucumber-report.html",
                "json:build/reports/cucumber/web-cucumber-report.json"
        },
        monochrome = true
)
public class WebTestNGRunner extends AbstractTestNGCucumberRunner {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
