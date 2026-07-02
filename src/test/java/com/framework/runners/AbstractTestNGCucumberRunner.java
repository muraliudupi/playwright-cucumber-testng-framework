package com.framework.runners;

import com.framework.core.DriverFactory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

/**
 * Base runner every concrete feature-set runner extends.

 * Concrete subclasses only need an @CucumberOptions annotation — all parallelism plumbing and suite-wide teardown live here, once.

 * Parallelism model: {@link #scenarios()} overrides the data provider with {@code parallel = true}. TestNG then farms each returned scenario out to
 * its thread pool (sized by testng.xml's thread-count). Combined with DriverFactory's ThreadLocal isolation, this is what gives us safe
 * concurrent execution without cross-scenario browser-session bleed.
 */
public abstract class AbstractTestNGCucumberRunner extends AbstractTestNGCucumberTests {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestNGCucumberRunner.class);

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    /**
     * Runs exactly once, on a single thread, after every worker thread has finished executing every scenario in the suite. This is the ONE safe
     * place to close out browsers/Playwright instances registered by every worker thread — see DriverFactory.quitAllDrivers() for why this can't
     * be done per-thread via a normal @After hook.
     */
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        LOG.info("Suite complete — tearing down all Playwright/Browser instances.");
        DriverFactory.quitAllDrivers();
    }
}
