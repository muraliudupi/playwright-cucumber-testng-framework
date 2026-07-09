package com.framework.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features/mobile",
        glue = {"com.app.mobile.saucelabs.stepdefinitions", "com.framework.hooks.mobile"},
        tags = "not @web and not @wip",
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
