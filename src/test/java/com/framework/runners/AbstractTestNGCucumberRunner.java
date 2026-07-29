package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;

/**
 * Common Cucumber/TestNG runner base. Suite lifecycle ownership is centralized
 * in FrameworkSuiteListener to prevent duplicate teardown across concrete runners.
 */
public abstract class AbstractTestNGCucumberRunner extends AbstractTestNGCucumberTests {
}
