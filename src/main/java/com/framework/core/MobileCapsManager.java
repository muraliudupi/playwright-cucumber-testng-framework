package com.framework.core;

import com.framework.utils.ConfigReader;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;
import java.util.HashMap;

public class MobileCapsManager {

    public static MutableCapabilities getBrowserStackCaps(String platform) {

        HashMap<String, Object> browserstackOptions = new HashMap<>();

        browserstackOptions.put("userName", ConfigReader.get("bs.username"));
        browserstackOptions.put("accessKey", ConfigReader.get("bs.access.key"));
        browserstackOptions.put("projectName", ConfigReader.get("bs.project.name"));
        browserstackOptions.put("buildName", ConfigReader.get("bs.build.name"));
        browserstackOptions.put("local", Boolean.parseBoolean(ConfigReader.get("bs.local")));

        String cleanPlatform = platform.trim().toLowerCase();

        MutableCapabilities caps;

        if ("android".equals(cleanPlatform)) {
            caps = new UiAutomator2Options()
                    .setPlatformVersion(ConfigReader.get("android.platform.version"))
                    .setDeviceName(ConfigReader.get("android.device.name"))
                    .setApp(ConfigReader.get("android.app.id"));
        } else if ("ios".equals(cleanPlatform)) {
            caps = new XCUITestOptions()
                    .setPlatformVersion(ConfigReader.get("ios.platform.version"))
                    .setDeviceName(ConfigReader.get("ios.device.name"))
                    .setApp(ConfigReader.get("ios.app.id"));
            } else {
            throw new IllegalArgumentException("Unsupported mobile automation target runtime platform: " + platform);
        }

        caps.setCapability("bstack:options", browserstackOptions);
        return caps;
    }
}