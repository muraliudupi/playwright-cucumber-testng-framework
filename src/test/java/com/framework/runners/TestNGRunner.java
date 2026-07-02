package com.framework.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.framework.hooks", "com.framework.stepdefinitions"},
        tags = "not @ignore",
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:build/reports/cucumber/cucumber-report.html",
                "json:build/reports/cucumber/cucumber-report.json"
        },
        monochrome = true,
        publish = false
)
public class TestNGRunner extends AbstractTestNGCucumberRunner {
}