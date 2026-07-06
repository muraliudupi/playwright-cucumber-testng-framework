package com.framework.core;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ViewportSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE_THREAD_LOCAL = new ThreadLocal<>();

    private static final Map<Long, Playwright> PLAYWRIGHT_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Long, Browser> BROWSER_REGISTRY = new ConcurrentHashMap<>();

    private DriverFactory() {
        // static utility — never instantiated
    }

    private static Playwright getPlaywright() {
        if (PLAYWRIGHT_THREAD_LOCAL.get() == null) {
            LOG.info("[Thread-{}] Creating new Playwright instance", threadId());
            Playwright playwright = Playwright.create();
            PLAYWRIGHT_THREAD_LOCAL.set(playwright);
            PLAYWRIGHT_REGISTRY.put(threadId(), playwright);
        }
        return PLAYWRIGHT_THREAD_LOCAL.get();
    }

    private static Browser getBrowser() {
        if (BROWSER_THREAD_LOCAL.get() == null) {
            String browserName = resolveSetting("browser", "chromium").toLowerCase();
            boolean headless = Boolean.parseBoolean(resolveSetting("headless", "true"));

            LOG.info("[Thread-{}] Launching {} (headless={})", threadId(), browserName, headless);

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(0);

            BrowserType browserType = switch (browserName) {
                case "firefox" -> getPlaywright().firefox();
                case "webkit"  -> getPlaywright().webkit();
                default        -> getPlaywright().chromium();
            };

            Browser browser = browserType.launch(launchOptions);
            BROWSER_THREAD_LOCAL.set(browser);
            BROWSER_REGISTRY.put(threadId(), browser);
        }
        return BROWSER_THREAD_LOCAL.get();
    }

    public static void createNewPageForScenario(String scenarioName) {
        if (CONTEXT_THREAD_LOCAL.get() != null) {
            LOG.warn("[Thread-{}] Stale BrowserContext detected before '{}' — closing it first",
                    threadId(), scenarioName);
            closeContextAndPage();
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(new ViewportSize(1920, 1080))
                .setIgnoreHTTPSErrors(true);

        BrowserContext context = getBrowser().newContext(contextOptions);
        context.setDefaultTimeout(30_000);
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        Page page = context.newPage();

        CONTEXT_THREAD_LOCAL.set(context);
        PAGE_THREAD_LOCAL.set(page);

        LOG.info("[Thread-{}] New BrowserContext + Page ready for scenario '{}'", threadId(), scenarioName);
    }

    public static Page getPage() {
        Page page = PAGE_THREAD_LOCAL.get();
        if (page == null) {
            throw new IllegalStateException(
                    "No Page bound to thread " + threadId() +
                    ". Was createNewPageForScenario() called in a @Before hook?");
        }
        return page;
    }

    public static BrowserContext getContext() {
        BrowserContext context = CONTEXT_THREAD_LOCAL.get();
        if (context == null) {
            throw new IllegalStateException("No BrowserContext bound to thread " + threadId());
        }
        return context;
    }

    public static void closeContextAndPage() {
        try {
            BrowserContext context = CONTEXT_THREAD_LOCAL.get();
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            LOG.error("[Thread-{}] Error closing BrowserContext", threadId(), e);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
            PAGE_THREAD_LOCAL.remove();
        }
    }

    public static void quitAllDrivers() {
        BROWSER_REGISTRY.forEach((tid, browser) -> {
            try {
                browser.close();
                LOG.info("[Thread-{}] Browser closed during suite teardown", tid);
            } catch (Exception e) {
                LOG.error("[Thread-{}] Error closing Browser during suite teardown", tid, e);
            }
        });
        BROWSER_REGISTRY.clear();

        PLAYWRIGHT_REGISTRY.forEach((tid, playwright) -> {
            try {
                playwright.close();
                LOG.info("[Thread-{}] Playwright closed during suite teardown", tid);
            } catch (Exception e) {
                LOG.error("[Thread-{}] Error closing Playwright during suite teardown", tid, e);
            }
        });
        PLAYWRIGHT_REGISTRY.clear();

        LOG.info("Suite-wide Playwright/Browser teardown complete.");
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }

    private static String resolveSetting(String key, String hardcodedDefault) {
        String value = ConfigReader.get(key);
        return value != null && !value.isBlank() ? value : hardcodedDefault;
    }
}