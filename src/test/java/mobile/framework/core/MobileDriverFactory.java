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
                DesiredCapabilities caps = MobileCapsManager.getBrowserStackCaps(platform);

                String hubUrlStr = ConfigReader.get("bs.hub.url");
                if (hubUrlStr == null || hubUrlStr.isBlank()) {
                    throw new IllegalStateException("Mobile Infrastructure Error: 'bs.hub.url' property is undefined.");
                }
                URL remoteUrl = new URL(hubUrlStr);

                LOG.info("[Thread-{}] Spawning remote Appium session on BrowserStack for platform: {}", tid, platform);
                AppiumDriver driver = "android".equalsIgnoreCase(platform.trim())
                        ? new AndroidDriver(remoteUrl, caps)
                        : new IOSDriver(remoteUrl, caps);

                DRIVER_THREAD_LOCAL.set(driver);
                DRIVER_REGISTRY.put(tid, driver); // Register session immediately
            } catch (Exception e) {
                throw new RuntimeException("Critical Mobile Infrastructure Initialization Crash on Thread-" + tid, e);
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