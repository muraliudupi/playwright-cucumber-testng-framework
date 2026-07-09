package com.framework.core;

import com.framework.utils.ConfigReader;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.HashMap;

public class MobileCapsManager {

    public static DesiredCapabilities getBrowserStackCaps(String platform) {
        DesiredCapabilities caps = new DesiredCapabilities();
        HashMap<String, Object> browserstackOptions = new HashMap<>();

        browserstackOptions.put("userName", ConfigReader.get("bs.username"));
        browserstackOptions.put("accessKey", ConfigReader.get("bs.access.key"));
        browserstackOptions.put("projectName", ConfigReader.get("bs.project.name"));
        browserstackOptions.put("buildName", ConfigReader.get("bs.build.name"));
        browserstackOptions.put("local", Boolean.parseBoolean(ConfigReader.get("bs.local")));

        String cleanPlatform = platform.trim().toLowerCase();

        if ("android".equals(cleanPlatform)) {
            caps.setCapability("platformName", "android");
            caps.setCapability("appium:platformVersion", ConfigReader.get("android.platform.version"));
            caps.setCapability("appium:deviceName", ConfigReader.get("android.device.name"));
            caps.setCapability("appium:automationName", ConfigReader.get("android.automation.name"));
            caps.setCapability("appium:app", ConfigReader.get("android.app.id"));
        } else if ("ios".equals(cleanPlatform)) {
            caps.setCapability("platformName", "ios");
            caps.setCapability("appium:platformVersion", ConfigReader.get("ios.platform.version"));
            caps.setCapability("appium:deviceName", ConfigReader.get("ios.device.name"));
            caps.setCapability("appium:automationName", ConfigReader.get("ios.automation.name"));
            caps.setCapability("appium:app", ConfigReader.get("ios.app.id"));
        } else {
            throw new IllegalArgumentException("Unsupported mobile automation target runtime platform: " + platform);
        }

        caps.setCapability("bstack:options", browserstackOptions);
        return caps;
    }
}
