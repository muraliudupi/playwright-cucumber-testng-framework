package com.framework.runners;

import com.framework.core.DriverFactory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;

public abstract class AbstractTestNGCucumberRunner extends AbstractTestNGCucumberTests {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestNGCucumberRunner.class);

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        LOG.info("Suite complete — tearing down all Playwright/Browser instances.");
        DriverFactory.quitAllDrivers();
    }
}
