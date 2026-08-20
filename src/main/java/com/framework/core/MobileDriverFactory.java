package com.framework.core;

import com.framework.utils.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.MutableCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public final class MobileDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MobileDriverFactory.class);

    private static final ThreadLocal<AppiumDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static final Map<Long, AppiumDriver> DRIVER_REGISTRY = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> ACQUIRED_LOCAL_DEVICE = new ThreadLocal<>();
    private static final BlockingQueue<String> LOCAL_DEVICE_POOL = new LinkedBlockingQueue<>();
    private static volatile boolean devicePoolInitialized = false;

    private MobileDriverFactory() {
    }

    private static synchronized void ensureDevicePoolInitialized() {
        if (devicePoolInitialized) {
            return;
        }
        String poolConfig = ConfigReader.get("mobile.local.device.pool");
        if (poolConfig == null || poolConfig.isBlank()) {
            poolConfig = ConfigReader.get("mobile.local.device.name"); // single-device fallback
        }
        if (poolConfig == null || poolConfig.isBlank()) {
            throw new IllegalStateException(
                    "Local mobile execution requires 'mobile.local.device.pool' or 'mobile.local.device.name' in config.properties.");
        }
        for (String device : poolConfig.split(",")) {
            LOCAL_DEVICE_POOL.add(device.trim());
        }
        devicePoolInitialized = true;
        LOG.info("Local device pool initialized with {} device(s): {}", LOCAL_DEVICE_POOL.size(), LOCAL_DEVICE_POOL);
    }

    public static void initializeDriver(String platform) {
        long threadId = Thread.currentThread().threadId();

        if (DRIVER_THREAD_LOCAL.get() == null) {
            try {
                // Determine execution mode: local vs cloud (BrowserStack)
                String executionMode = ConfigReader.get("mobile.execution.mode");
                AppiumDriver driver;

                if ("local".equalsIgnoreCase(executionMode)) {
                    ensureDevicePoolInitialized();
                    String deviceName;
                    try {
                        deviceName = LOCAL_DEVICE_POOL.take(); // blocks until a device is free — never collides
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while waiting for an available local device.", e);
                    }
                    ACQUIRED_LOCAL_DEVICE.set(deviceName);
                    LOG.info("[Thread-{}] Initializing local Android emulator instance: {}", threadId, deviceName);

                    UiAutomator2Options options = new UiAutomator2Options()
                            .setPlatformName("Android")
                            .setDeviceName(deviceName)
                            .setUdid(deviceName)
                            .setAutomationName("UiAutomator2")
                            .setApp(System.getProperty("user.dir") + "/" + ConfigReader.get("local.app.path"))

                            .setAppWaitActivity(ConfigReader.get("mobile.local.app.wait.activity"))
                            .setNewCommandTimeout(Duration.ofSeconds(
                                    ConfigReader.getInt("mobile.local.new.command.timeout.sec", 60)))

                            .amend("appium:ensureWebviewsHavePages", true)
                            .amend("appium:chromedriverAutodownload", true);

                    URL localUrl = URI.create("http://127.0.0.1:4723/").toURL();
                    driver = new AndroidDriver(localUrl, options);
                } else {
                    // CLOUD EXECUTION: BrowserStack Cloud
                    LOG.info("[Thread-{}] Spawning remote Appium session on BrowserStack for platform: {}", threadId, platform);

                    MutableCapabilities caps = MobileCapsManager.getBrowserStackCaps(platform);
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
                DRIVER_REGISTRY.put(threadId, driver);

            } catch (Exception e) {
                LOG.error("Critical Mobile Infrastructure Initialization Crash on Thread-{}", threadId, e);
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
        long threadId = Thread.currentThread().threadId();
        AppiumDriver driver = DRIVER_THREAD_LOCAL.get();
        try {
            if (driver != null) {
                driver.quit();
                LOG.info("[Thread-{}] Recycled mobile remote Appium driver session cleanly.", threadId);
            }
        } finally {
            DRIVER_THREAD_LOCAL.remove();
            DRIVER_REGISTRY.remove(threadId);
            String device = ACQUIRED_LOCAL_DEVICE.get();
            if (device != null) {
                LOCAL_DEVICE_POOL.add(device);
                ACQUIRED_LOCAL_DEVICE.remove();
            }
        }
    }

    public static void quitAllDrivers() {
        LOG.info("Global mobile driver thread registry verification hook executing...");
        DRIVER_REGISTRY.forEach((threadId, driver) -> {
            try {
                if (driver != null) {
                    driver.quit();
                    LOG.info("Suite Cleanup: Recycled dangling Appium session on Thread-{}", threadId);
                }
            } catch (Exception e) {
                LOG.error("Suite Teardown Alert: Anomaly discarding mobile session on Thread-{}", threadId, e);
            }
        });
        DRIVER_REGISTRY.clear();
        DRIVER_THREAD_LOCAL.remove();
    }
}