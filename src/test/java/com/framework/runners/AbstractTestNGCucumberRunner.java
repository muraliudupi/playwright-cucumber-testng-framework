package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;

public abstract class AbstractTestNGCucumberRunner extends AbstractTestNGCucumberTests {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestNGCucumberRunner.class);

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        LOG.info("Suite complete — tearing down system infrastructure.");
        com.framework.core.DriverFactory.quitAllDrivers();
        mobile.framework.core.MobileDriverFactory.quitAllDrivers();
        com.framework.utils.DatabaseUtil.closePool();
    }
}