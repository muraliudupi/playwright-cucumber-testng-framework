package com.framework.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features/web",
        glue = {"com.framework.stepdefinitions", "com.framework.hooks"},
        tags = "not @wip",
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:build/reports/cucumber/web-cucumber-report.html",
                "json:build/reports/cucumber/web-cucumber-report.json"
        },
        monochrome = true
)
public class TestNGRunner extends AbstractTestNGCucumberRunner {

        @Override
        @DataProvider(parallel = true)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
