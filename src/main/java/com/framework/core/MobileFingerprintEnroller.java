package com.framework.core;

import com.framework.utils.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class MobileFingerprintEnroller {

    private static final Logger LOG = LoggerFactory.getLogger(MobileFingerprintEnroller.class);

    private static final String DEVICE_PIN = "1234";
    private static final int MAX_ENROLL_TOUCHES = 10;
    private static final int SIMULATED_ENROLLED_FINGER_ID = 1;

    private MobileFingerprintEnroller() {
    }

    public static void enrollAllPooledDevices() {
        if (!"local".equalsIgnoreCase(ConfigReader.get("mobile.execution.mode"))) {
            LOG.info("Skipping fingerprint enrollment provisioning: mobile.execution.mode is not 'local'.");
            return;
        }

        String poolConfig = ConfigReader.get("mobile.local.device.pool");
        if (poolConfig == null || poolConfig.isBlank()) {
            poolConfig = ConfigReader.get("mobile.local.device.name"); // single-device fallback
        }
        if (poolConfig == null || poolConfig.isBlank()) {
            LOG.info("Skipping fingerprint enrollment provisioning: no local device pool configured.");
            return;
        }

        for (String device : poolConfig.split(",")) {
            String deviceName = device.trim();
            try {
                enrollOnDevice(deviceName);
            } catch (Exception e) {
                LOG.warn("Fingerprint enrollment provisioning failed on {} — the fingerprint scenario may fail "
                        + "later as a result, but this will not fail the suite here.", deviceName, e);
            }
        }
    }

    private static void enrollOnDevice(String deviceName) throws InterruptedException {
        LOG.info("Provisioning fingerprint enrollment on {}...", deviceName);

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(deviceName)
                .setUdid(deviceName)
                .setAutomationName("UiAutomator2")
                .setNewCommandTimeout(Duration.ofSeconds(60));

        AndroidDriver driver;
        try {
            URL localUrl = URI.create("http://127.0.0.1:4723/").toURL();
            driver = new AndroidDriver(localUrl, options);
        } catch (Exception e) {
            LOG.warn("Could not open a provisioning session on {} — device may not be booted yet, or the "
                    + "Appium server isn't reachable. Skipping enrollment on this device.", deviceName, e);
            return;
        }

        try {
            // 1. Fingerprint enrollment requires a backup credential — set a device PIN.
            driver.executeScript("mobile: shell", Map.of(
                    "command", "locksettings",
                    "args", List.of("set-pin", DEVICE_PIN)
            ));

            // 2. Jump straight to the enrollment wizard via intent, rather than tapping
            //    through Settings > Security > Fingerprint by hand.
            driver.executeScript("mobile: startActivity", Map.of(
                    "intent", "android.settings.FINGERPRINT_ENROLL"
            ));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // 3. The wizard opens on a "confirm your PIN" screen since a credential now
            //    exists. Enter it via the standard AOSP lock-screen numeric keypad.
            for (char digit : DEVICE_PIN.toCharArray()) {
                WebElement key = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.id("com.android.settings:id/key" + digit)));
                key.click();
            }

            // Some flows show an explicit confirmation button after PIN entry.
            clickIfPresent(driver, AppiumBy.androidUIAutomator(
                    "new UiSelector().textMatches(\"(?i)next|done|enter\")"));

            // 4. Touch the sensor repeatedly until the wizard reports enrollment complete.
            //    fingerPrint(id) is the SDK equivalent of `adb emu finger touch <id>`.
            boolean enrollmentConfirmed = false;
            for (int i = 0; i < MAX_ENROLL_TOUCHES; i++) {
                driver.fingerPrint(SIMULATED_ENROLLED_FINGER_ID);
                Thread.sleep(1000);

                if (!driver.findElements(AppiumBy.androidUIAutomator(
                        "new UiSelector().textMatches(\"(?i)fingerprint added|all set|done\")")).isEmpty()) {
                    enrollmentConfirmed = true;
                    break;
                }
            }

            // 5. Dismiss the "Fingerprint added" / "All set" confirmation screen(s).
            clickIfPresent(driver, AppiumBy.androidUIAutomator(
                    "new UiSelector().textMatches(\"(?i)done|ok|got it\")"));

            if (enrollmentConfirmed) {
                LOG.info("Fingerprint enrollment provisioning complete on {}.", deviceName);
            } else {
                LOG.warn("Fingerprint enrollment on {} did not report a completion screen within {} touches — "
                        + "it may still have succeeded (screen text can vary), but this is unconfirmed.",
                        deviceName, MAX_ENROLL_TOUCHES);
            }
        } finally {
            driver.quit();
        }
    }

    private static void clickIfPresent(AndroidDriver driver, By locator) {
        List<WebElement> matches = driver.findElements(locator);
        if (!matches.isEmpty()) {
            matches.get(0).click();
        }
    }
}
