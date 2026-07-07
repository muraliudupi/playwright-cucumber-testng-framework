package com.framework.hooks;

import com.framework.core.DriverFactory;
import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class Hooks {

    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);
    private static final Path TRACE_DIR = Paths.get("build", "reports", "traces");

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        LOG.info("=== Starting scenario: '{}' [Thread-{}] ===", scenario.getName(), Thread.currentThread().threadId());
        DriverFactory.createNewPageForScenario(scenario.getName());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        try {
            boolean screenshotOnPass = Boolean.parseBoolean(
                    ConfigReader.get("screenshot.on.pass") != null
                            ? ConfigReader.get("screenshot.on.pass")
                            : "true");

            if (scenario.isFailed() || screenshotOnPass) {
                try {
                    attachScreenshot(scenario);
                } catch (Exception e) {
                    LOG.error("Failed to capture status screenshot for report context", e);
                }
            }

            try {
                if (scenario.isFailed()) {
                    attachTrace(scenario);
                } else {
                    com.microsoft.playwright.Page page = DriverFactory.getPage();
                    if (page != null && DriverFactory.getContext() != null) {
                        DriverFactory.getContext().tracing().stop(new com.microsoft.playwright.Tracing.StopOptions().setPath(null));
                    }
                }
            } catch (Exception e) {
                LOG.error("Failed to complete teardown tracing cleanup lifecycle", e);
            }

        } finally {
            DriverFactory.closeContextAndPage();
        }
    }

    private void attachScreenshot(Scenario scenario) {
        Page page = DriverFactory.getPage();
        if (page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            String attachmentName = scenario.isFailed() ? "Failure-State-Snapshot" : "Success-State-Snapshot";
            scenario.attach(screenshot, "image/png", attachmentName);
        }
    }

    private void attachTrace(Scenario scenario) {
        if (DriverFactory.getContext() != null) {
            String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
            Path tracePath = TRACE_DIR.resolve(safeName + "-" + Instant.now().toEpochMilli() + ".zip");

            DriverFactory.getContext().tracing().stop(new com.microsoft.playwright.Tracing.StopOptions().setPath(tracePath));
            LOG.info("Trace saved and bounded to execution report at: {}", tracePath.toAbsolutePath());
        }
    }
}
