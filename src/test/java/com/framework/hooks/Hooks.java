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

import java.io.IOException;
import java.nio.file.Files;
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
            String isScreenshot = ConfigReader.get("screenshot.on.pass");
            boolean screenshotOnPass = Boolean.parseBoolean(isScreenshot != null ? isScreenshot : "true");

            if (scenario.isFailed() || screenshotOnPass) {
                try {
                    attachScreenshot(scenario);
                } catch (Exception e) {
                    LOG.error("Failed to append snapshot attachment", e);
                }
            }
        } finally {
            try {
                if (scenario.isFailed()) {
                    attachTrace(scenario);
                } else {
                    DriverFactory.getContext().tracing().stop();
                }
            } catch (Exception e) {
                LOG.error("Failed cleanly executing trace shutdown sequence", e);
            } finally {
                DriverFactory.closeContextAndPage();
            }
        }
    }

    private void attachScreenshot(Scenario scenario) {
        Page page = DriverFactory.getPage();
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        String label = scenario.isFailed() ? "Failure-State-Snapshot" : "Success-State-Snapshot";
        scenario.attach(screenshot, "image/png", label);
    }

    private void attachTrace(Scenario scenario) {
        try {
            Files.createDirectories(TRACE_DIR);
        } catch (IOException e) {
            LOG.error("Could not create trace output directory: {}", TRACE_DIR.toAbsolutePath(), e);
        }

        String safeName    = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
        String threadId    = String.valueOf(Thread.currentThread().threadId());
        String uniqueMarker = java.util.UUID.randomUUID().toString().substring(0, 8);

        Path tracePath = TRACE_DIR.resolve(String.format("%s-thread%s-%s-%s.zip",
                safeName, threadId, Instant.now().toEpochMilli(), uniqueMarker));

        DriverFactory.getContext().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        LOG.info("[Thread-{}] Trace written successfully to isolated path: {}", threadId, tracePath.toAbsolutePath());
    }
}