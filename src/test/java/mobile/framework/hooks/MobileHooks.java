package mobile.framework.hooks;

import com.framework.utils.ConfigReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import mobile.framework.core.MobileDriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileHooks {

    private static final Logger LOG = LoggerFactory.getLogger(MobileHooks.class);

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        LOG.info("=== Starting scenario: '{}' [Thread-{}] ===", scenario.getName(), Thread.currentThread().threadId());

        // Determine platform based on Scenario Tag names context
        String platform = "android";
        if (scenario.getSourceTagNames().stream().anyMatch(tag -> tag.equalsIgnoreCase("@ios"))) {
            platform = "ios";
        }

        // Initialize target mobile runtime cloud node context
        MobileDriverFactory.initializeDriver(platform);
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
            long currentTid = Thread.currentThread().threadId();
            try {
                // Future extension placeholder for mobile app performance tracing / session logs if required
                LOG.info("[Thread-{}] Completing lifecycle hook isolation tear down sequence.", currentTid);
            } catch (Exception e) {
                LOG.error("Failed cleanly executing session teardown loop analytics logs", e);
            } finally {
                MobileDriverFactory.quitDriver();
            }
        }
    }

    private void attachScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = ((TakesScreenshot) MobileDriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            String label = scenario.isFailed() ? "Failure-State-Snapshot" : "Success-State-Snapshot";
            scenario.attach(screenshot,  "image/png", label);
        } catch (Exception e) {
            LOG.error("Failed to parse device frame buffer into scenario attachment report interface", e);
        }
    }
}