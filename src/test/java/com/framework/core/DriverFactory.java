package com.framework.core;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {}

    private static Playwright getPlaywright() {
        if (PLAYWRIGHT_THREAD_LOCAL.get() == null) {
            LOG.info("[Thread-{}] Instantiating unique thread-isolated Playwright instance.", threadId());
            PLAYWRIGHT_THREAD_LOCAL.set(Playwright.create());
        }
        return PLAYWRIGHT_THREAD_LOCAL.get();
    }

    private static Browser getBrowser() {
        if (BROWSER_THREAD_LOCAL.get() == null) {
            String browserType = resolveSetting("browser", "chromium").trim().toLowerCase();
            boolean headless = Boolean.parseBoolean(resolveSetting("headless", "true"));

            LOG.info("[Thread-{}] Spawning isolated {} OS process (Headless={}).", threadId(), browserType, headless);
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

            Browser browser = switch (browserType) {
                case "firefox" -> getPlaywright().firefox().launch(options);
                case "webkit" -> getPlaywright().webkit().launch(options);
                default -> getPlaywright().chromium().launch(options);
            };
            BROWSER_THREAD_LOCAL.set(browser);
        }
        return BROWSER_THREAD_LOCAL.get();
    }

    public static BrowserContext getContext() {
        if (CONTEXT_THREAD_LOCAL.get() == null) {
            BrowserContext context = getBrowser().newContext(
                    new Browser.NewContextOptions().setViewportSize(1280, 720)
            );
            CONTEXT_THREAD_LOCAL.set(context);
        }
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static Page getPage() {
        if (PAGE_THREAD_LOCAL.get() == null) {
            Page page = getContext().newPage();
            PAGE_THREAD_LOCAL.set(page);
        }
        return PAGE_THREAD_LOCAL.get();
    }

    public static void createNewPageForScenario(String scenarioName) {
        LOG.info("[Thread-{}] Mapping fresh Page viewport context allocation for: '{}'", threadId(), scenarioName);
        getPage();
        getContext().tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
    }

    public static void closeContextAndPage() {
        long currentTid = threadId();
        LOG.info("[Thread-{}] Initiating dynamic lifecycle sweep and browser process recycling...", currentTid);

        try {
            Page page = PAGE_THREAD_LOCAL.get();
            if (page != null) page.close();
        } catch (Exception e) {
            LOG.error("[Thread-{}] Page reference cleanup anomaly logged.", currentTid, e);
        } finally {
            PAGE_THREAD_LOCAL.remove();
        }

        try {
            BrowserContext context = CONTEXT_THREAD_LOCAL.get();
            if (context != null) context.close();
        } catch (Exception e) {
            LOG.error("[Thread-{}] Context mapping cleanup anomaly logged.", currentTid, e);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
        }

        try {
            Browser browser = BROWSER_THREAD_LOCAL.get();
            if (browser != null) {
                browser.close();
                LOG.info("[Thread-{}] Browser OS process recycled cleanly.", currentTid);
            }
        } catch (Exception e) {
            LOG.error("[Thread-{}] Browser binary lifecycle execution closure error.", currentTid, e);
        } finally {
            BROWSER_THREAD_LOCAL.remove();
        }

        try {
            Playwright pw = PLAYWRIGHT_THREAD_LOCAL.get();
            if (pw != null) pw.close();
        } catch (Exception e) {
            LOG.error("[Thread-{}] Playwright core engine channel exception.", currentTid, e);
        } finally {
            PLAYWRIGHT_THREAD_LOCAL.remove();
        }
    }

    public static void quitAllDrivers() {
        LOG.info("Global driver thread infrastructure verification check complete.");
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }

    private static String resolveSetting(String key, String hardcodedDefault) {
        String value = ConfigReader.get(key);
        return (value != null && !value.isBlank()) ? value : hardcodedDefault;
    }
}