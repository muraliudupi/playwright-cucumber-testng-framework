package com.framework.hooks;

import com.framework.core.DriverFactory;
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
            // UNCONDITIONAL SNAPSHOT CAPTURE: Executes for BOTH Pass and Fail states
            try {
                attachScreenshot(scenario);
            } catch (Exception e) {
                LOG.error("Failed to capture status screenshot for report context", e);
            }

            // Trace generation remains selectively pinned to failures to optimize system memory bounds
            if (scenario.isFailed()) {
                try { attachTrace(scenario); } catch (Exception e) { LOG.error("Failed trace capture", e); }
            }
        } finally {
            // Absolute guard line protecting parallel execution pipelines
            DriverFactory.closeContextAndPage();
        }
    }

    /**
     * Captures a full-page PNG binary array and attaches it safely into the Cucumber test engine context.
     * The Extent Adapter intercepts this native .attach() call and formats it perfectly into the visual HTML layer.
     */
    private void attachScreenshot(Scenario scenario) {
        Page page = DriverFactory.getPage();
        if (page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            String attachmentName = scenario.isFailed() ? "Failure-State-Snapshot" : "Success-State-Snapshot";
            scenario.attach(screenshot, "image/png", attachmentName);
        }
    }

    /**
     * Saves a Playwright trace .zip (viewable via `playwright show-trace`) for any failed scenario. Tracing was started per-context in
     * DriverFactory.createNewPageForScenario(); here we simply stop and persist it to disk since it's only needed on failure.
     */
    private void attachTrace(Scenario scenario) {
        String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
        Path tracePath = TRACE_DIR.resolve(safeName + "-" + Instant.now().toEpochMilli() + ".zip");
        DriverFactory.getContext().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        LOG.info("Trace saved to {}", tracePath.toAbsolutePath());
    }
}
