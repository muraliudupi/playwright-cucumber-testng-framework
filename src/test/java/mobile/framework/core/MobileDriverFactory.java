package mobile.framework.core;

import com.framework.utils.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MobileDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MobileDriverFactory.class);
    private static final ThreadLocal<AppiumDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private static final Map<Long, AppiumDriver> DRIVER_REGISTRY = new ConcurrentHashMap<>();

    private MobileDriverFactory() {}

    public static void initializeDriver(String platform) {
        long tid = Thread.currentThread().threadId();

        if (DRIVER_THREAD_LOCAL.get() == null) {
            try {
                // Determine execution mode: local vs cloud (BrowserStack)
                String executionMode = ConfigReader.get("mobile.execution.mode");
                AppiumDriver driver;

                if ("local".equalsIgnoreCase(executionMode)) {
                    LOG.info("[Thread-{}] Initializing local Android emulator instance.", tid);

                    io.appium.java_client.android.options.UiAutomator2Options options = new io.appium.java_client.android.options.UiAutomator2Options()
                            .setPlatformName("Android")
                            .setDeviceName("emulator-5554") // Default name for the first booted AVD
                            .setAutomationName("UiAutomator2")
                            .setApp(System.getProperty("user.dir") + "/" + ConfigReader.get("local.app.path"))

                            .setAppWaitActivity("*") // Bypasses the strict check for SplashActivity
                            .setAppWaitDuration(java.time.Duration.ofSeconds(50)) // Allows up to 50s for sluggish local emulators to render the app
                            .amend("appium:ensureWebviewsHavePages", true); // Recommended helper for hybrid components if any

                    java.net.URL localUrl = java.net.URI.create("http://127.0.0.1:4723/").toURL();
                    driver = new AndroidDriver(localUrl, options);
                } else {
                    // CLOUD EXECUTION: BrowserStack Cloud
                    LOG.info("[Thread-{}] Spawning remote Appium session on BrowserStack for platform: {}", tid, platform);

                    DesiredCapabilities caps = MobileCapsManager.getBrowserStackCaps(platform);
                    String hubUrlStr = ConfigReader.get("bs.hub.url");

                    if (hubUrlStr == null || hubUrlStr.isBlank()) {
                        throw new IllegalStateException("Mobile Infrastructure Error: 'bs.hub.url' property is undefined.");
                    }
                    URL remoteUrl = new URL(hubUrlStr);
                    driver = "android".equalsIgnoreCase(platform.trim())
                            ? new AndroidDriver(remoteUrl, caps)
                            : new IOSDriver(remoteUrl, caps);
                }

                DRIVER_THREAD_LOCAL.set(driver);
                DRIVER_REGISTRY.put(tid, driver);

            } catch (Exception e) {
                LOG.error("Critical Mobile Infrastructure Initialization Crash on Thread-{}", tid, e);
                throw new RuntimeException("Mobile driver initialization failed.", e);
            }
        }
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException("Automation Thread Failure: Mobile driver instance was not initialized.");
        }
        return driver;
    }

    public static void quitDriver() {
        long tid = Thread.currentThread().threadId();
        AppiumDriver driver = DRIVER_THREAD_LOCAL.get();
        try {
            if (driver != null) {
                driver.quit();
                LOG.info("[Thread-{}] Recycled mobile remote Appium driver session cleanly.", tid);
            }
        } finally {
            DRIVER_THREAD_LOCAL.remove();
            DRIVER_REGISTRY.remove(tid);
        }
    }

    public static void quitAllDrivers() {
        LOG.info("Global mobile driver thread registry verification hook executing...");
        DRIVER_REGISTRY.forEach((tid, driver) -> {
            try {
                if (driver != null) {
                    driver.quit();
                    LOG.info("Suite Cleanup: Recycled dangling Appium session on Thread-{}", tid);
                }
            } catch (Exception e) {
                LOG.error("Suite Teardown Alert: Anomaly discarding mobile session on Thread-{}", tid, e);
            }
        });
        DRIVER_REGISTRY.clear();
        DRIVER_THREAD_LOCAL.remove();
    }
}